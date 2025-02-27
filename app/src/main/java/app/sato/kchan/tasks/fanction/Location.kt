package app.sato.kchan.tasks.fanction

import java.time.LocalDateTime
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter

/********************
 * 複数のノートにかかわる処理を担当する
 *
 * クラス名: NoteManager
 ********************/
class Location public constructor(pick:MutableMap<String, String>) {
    //<プロパティ>
    private val pick:MutableMap<String, String>

    //<初期化処理>
    init {
        this.pick = pick
    }

    //<メソッド>
    fun getName():String?{
        return DataOperator().selectQuery(
            table = "place",
            column = "name",
            pick = pick
        ).getStringNulls()
    }
    fun setName(value:String){
        DataOperator().updateQuery(
            table = "place",
            value = mutableListOf(
                "name" to value,
                "name_update_at" to DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now())
            ),
            pick = pick
        )
    }
    fun getAddress():String?{
        return DataOperator().selectQuery(
            table = "place",
            column = "address",
            pick = pick
        ).getStringNulls()
    }
    fun setAddress(value:String){
        DataOperator().updateQuery(
            table = "place",
            value = mutableListOf(
                "address" to value,
                "address_update_at" to DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now())
            ),
            pick = pick
        )
    }
    fun getLongitude():Float?{
        return DataOperator().selectQuery(
            table = "place",
            column = "longitude",
            pick = pick
        ).getFloatNulls()
    }
    fun getLatitude():Float?{
        return DataOperator().selectQuery(
            table = "place",
            column = "latitude",
            pick = pick
        ).getFloatNulls()
    }
    fun setLongitude(value:Float?){
        DataOperator().updateQuery(
            table = "place",
            value = mutableListOf(
                "longitude" to value.toString(),
                "address_update_at" to DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now())
            ),
            pick = pick
        )
    }
    fun setLatitude(value:Float?){
        DataOperator().updateQuery(
            table = "place",
            value = mutableListOf(
                "latitude" to value.toString(),
                "address_update_at" to DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now())
            ),
            pick = pick
        )
    }
    fun move(index:Int){
        DataOperator().updateQuery(
            table = "place",
            value = mutableListOf(
                "priority" to "priority - 1",
                "priority_update_at" to DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now())
            ),
            filter = arrayOf(
                mutableMapOf(
                    "column" to "priority",
                    "value" to index.toString(),
                    "compare" to "Small"
                )
            )
        )
        DataOperator().updateQuery(
            table = "place",
            value = mutableListOf(
                "priority" to index.toString(),
                "priority_update_at" to DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now())
            ),
            pick = pick
        )
    }
    fun isPermanent():Boolean{
        return  DataOperator().selectQuery(
            table = "place",
            column = "status_flag",
            pick = pick
        ).getInt() and 1.shl(0) != 0
    }
    fun setPermanent(){
        DataOperator().updateQuery(
            table = "place",
            value = mutableListOf(
                "status_flag" to "1",
                "priority_update_at" to DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now())
            ),
            pick = pick
        )
    }
    fun setTemporary(){
        DataOperator().updateQuery(
            table = "place",
            value = mutableListOf(
                "status_flag" to "status_flag & ~ (1 << 0)",
                "priority_update_at" to DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now())
            ),
            pick = pick
        )
    }
    fun isCollision():Boolean{
        val status = DataOperator().selectQuery(
            table = "place",
            column = "status_flag",
            pick = pick
        ).getInt()
        return status and 1.shl(30) > 0
    }
    fun setCollisionReset(){
        DataOperator().updateQuery(
            table = "place",
            value = mutableListOf(
                "status_flag" to "status_flag & ~ (1 << 30)",
                "status_update_at" to DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now())
            ),
            pick = pick
        )
    }
    fun delete(){
        DataOperator().updateQuery(
            table = "place",
            value = mutableListOf(
                "status_flag" to "status_flag | (1 << 31)",
                "status_update_at" to DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now())
            ),
            pick = pick
        )
    }
    fun getPick():MutableMap<String, String>{
        return this.pick
    }
}
