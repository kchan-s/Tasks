package app.sato.kchan.tasks.fanction

import kotlinx.coroutines.channels.ticker
import java.time.format.DateTimeFormatter
import java.time.LocalDateTime

/********************
 * 複数のノートにかかわる処理を担当する
 *
 * クラス名 :  Notice
 ********************/
class Notice public constructor(pick:MutableMap<String, String>) {
    //<プロパティ>
    private var pick:MutableMap<String, String>

    //<初期化処理>
    init {
        this.pick = pick
    }

    //<メソッド>
    fun getNoticeShow(): LocalDateTime? {
        val res = DataOperator().selectQuery(
            table = "notice",
            column = "show_at",
            pick = pick,
            sort = arrayOf(mutableMapOf(
                "column" to "create_at",
                "type" to "ASC"
            ))
        )
        if(res.isResult())
            return res.getDateTime()
        else
            return null
    }
    fun setNoticeShow(value:LocalDateTime?){
        if(value == null){
            DataOperator().updateQuery(
                table = "notice",
                value = mutableListOf(
                    "show_at" to null,
                    "show_update_at" to DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now())
                ),
                pick = pick
            )
        }else{
            DataOperator().updateQuery(
                table = "notice",
                value = mutableListOf(
                    "show_at" to DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(value),
                    "show_update_at" to DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now())
                ),
                pick = pick
            )
        }

    }
    fun getNoticeHide():LocalDateTime?{
        val res = DataOperator().selectQuery(
            table = "notice",
            column = "hide_at",
            pick = pick,
            sort = arrayOf(mutableMapOf(
                "column" to "create_at",
                "type" to "ASC"
            ))
        )
        if(res.isResult())
            return res.getDateTime()
        else
            return null
    }
    fun setNoticeHide(value:LocalDateTime?){
        if(value == null){
            DataOperator().updateQuery(
                table = "notice",
                value = mutableListOf(
                    "hide_at" to null,
                    "hide_update_at" to DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now())
                ),
                pick = pick
            )
        }else {
            DataOperator().updateQuery(
                table = "notice",
                value = mutableListOf(
                    "hide_at" to DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(value),
                    "hide_update_at" to DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now())
                ),
                pick = pick
            )
        }
    }
    fun getNoticeLocation():Location?{
        val res = DataOperator().selectQuery(
            table = "notice",
            column = arrayOf("place_id","place_service_id"),
            pick = pick
        )
        if (res.isResult()) {
            val locaId = res.getStringNulls("place_id") ?: return null
            val locaSerId = res.getStringNulls("place_service_id") ?: return null
            return Location(mutableMapOf("place_id" to locaId, "service_id" to locaSerId))
        }
        return null
    }
    fun setNoticeLocation(location:Location?){
        val locaId: String?
        val locaSerId: String?
        if(location == null){
            locaId = null
            locaSerId = null
        }else{
            val locaPick = location.getPick()
            locaId = locaPick["place_id"] ?: return
            locaSerId = locaPick["service_id"] ?: return
        }
        DataOperator().updateQuery(
            table = "notice",
            value = mutableListOf(
                "place_id" to locaId,
                "place_service_id" to locaSerId,
                "place_update_at" to DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now())
            ),
            pick = pick
        )
    }
    fun isCollision():Boolean{
        return DataOperator().selectQuery(
            table = "notice",
            column = "status_flag",
            pick = pick
        ).getInt() and 1.shl(30) > 0
    }
    // fun getCollisionStatus(){
    // 	return do.selectQuery(table = "notice", column = "status_flag", pick = pick)
    // }
    fun serCollisionReset(){
        DataOperator().updateQuery(
            table = "notice",
            value = mutableListOf(
                "status_flag" to "status_flag & ~ (1 << 30)",
                "status_update_at" to DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now())
            ),
            pick = pick
        )
    }
    fun delete(){
        DataOperator().updateQuery(
            table = "notice",
            value = mutableListOf(
                "status_flag" to "status_flag | (1 << 31)",
                "status_update_at" to DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now())
            ),
        )
    }
    fun getPick():MutableMap<String, String>{
        return this.pick
    }
}
