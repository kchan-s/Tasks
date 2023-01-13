package app.sato.kchan.tasks.fanction

import java.time.LocalDateTime
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter

/********************
 * 複数のノートにかかわる処理を担当する
 *
 * クラス名: NoteManager
 ********************/
class Note public constructor(pick:MutableMap<String, String>) {
    //<プロパティ>
    private var pick:MutableMap<String, String>

    //<初期化処理>
    init {
        this.pick = pick
    }

    //<メソッド>
    fun getTitle():String{
        return DataOperator().selectQuery(
            table = "note",
            column = "title",
            pick = pick
        ).getString()
    }
    fun setTitle(value:String){
        DataOperator().updateQuery(
            table = "note",
            value = mutableListOf("title" to value),
            pick = pick
        )
    }
    fun getContent():String{
        return DataOperator().selectQuery(
            table = "note",
            column = "content",
            pick = pick
        ).getString()
    }
    fun setContent(value:String){
        DataOperator().updateQuery(
            table = "note",
            value = mutableListOf("content" to value),
            pick = pick
        )
    }
    fun getNoticeShow(): LocalDateTime? {
        return DataOperator().selectQuery(
            table = "notice",
            column = "show_at",
            pick = pick
        ).getDateTime()
    }
    fun setNoticeShow(value:LocalDateTime?){
        if(value == null){
            DataOperator().updateQuery(
                table = "notice",
                value = mutableListOf("show_at" to null),
                pick = pick
            )
        }else{
            DataOperator().updateQuery(
                table = "notice",
                value = mutableListOf("show_at" to SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(value)),
                pick = pick
            )
        }

    }
    fun getNoticeHide():LocalDateTime?{
        return DataOperator().selectQuery(
            table = "notice",
            column = "hide_at",
            pick = pick
        ).getDateTime()
    }
    fun setNoticeHide(value:LocalDateTime?){
        if(value == null){
            DataOperator().updateQuery(
                table = "notice",
                value = mutableListOf("hide_at" to null),
                pick = pick
            )
        }else {
            DataOperator().updateQuery(
                table = "notice",
                value = mutableListOf("hide_at" to SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(value)),
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
        val locaId = res.getString("place_id") ?: return null
        val locaSerId = res.getString("place_service_id") ?: return null
        return Location(mutableMapOf("place_id" to locaId, "service_id" to locaSerId))
    }
    fun setNoticeLocation(location:Location){
        val locaObj = location.getPick() ?: return
        val locaId = locaObj["location_id"] ?: return
        val locaSerId = locaObj["service_id"] ?: return
        DataOperator().updateQuery(
            table = "notice",
            value = mutableListOf("place_id" to locaId,"place_service_id" to locaSerId),
            pick = pick
        )
    }
    fun isLock():Boolean{
        return DataOperator().selectQuery(
            table = "notice",
            column = "hide_at",
            pick = pick
        ).getBoolean()
    }
    fun setLock(){
        DataOperator().updateQuery(
            table = "note",
            value = mutableListOf("title" to true.toString()),
            pick = pick
        )
    }
    fun setUnlock(){
        DataOperator().updateQuery(
            table = "note",
            value = mutableListOf("title" to false.toString()),
            pick = pick
        )
    }
    fun isComplete():Boolean{
        return DataOperator().selectQuery(
            table = "notice",
            column = "hide_at",
            pick = pick
        ).isNull()
    }
    fun setComplete(){
        DataOperator().updateQuery(
            table = "note",
            value = mutableListOf("complete_at" to LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm:ss"))),
            pick = pick
        )
    }
    fun setUncomplete(){
        DataOperator().updateQuery(
            table = "note",
            value = mutableListOf("complete_at" to null),
            pick = pick
        )
    }
    fun isCollision():Boolean{
        return DataOperator().selectQuery(
            table = "note",
            column = "status_flag",
            pick = pick
        ).getInt() and 1.shl(30) > 0
    }
    // fun getCollisionStatus(){
    // 	return do.selectQuery(table = "note", column = "status_flag", pick = pick)
    // }
    fun serCollisionReset(){
        DataOperator().updateQuery(
            table = "note",
            value = mutableListOf("status_flag" to "status_flag & ~ (1 << 30)"),
            pick = pick
        )
    }
    fun delete(){
        DataOperator().updateQuery(
            table = "note",
            value = mutableListOf("status_flag" to "status_flag | (1 << 31)"),
            pick = pick
        )
    }
    fun getPick():MutableMap<String, String>{
        return this.pick
    }
}
