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
    fun getName():String{
        return DataOperator().selectQuery(
            table = "place",
            column = "name",
            pick = pick
        ).getString()
    }
    fun setName(value:String){
        DataOperator().updateQuery(
            table = "place",
            value = mutableListOf("name" to value),
            pick = pick
        )
    }
    fun getAddress():String{
        return DataOperator().selectQuery(
            table = "place",
            column = "address",
            pick = pick
        ).getString()
    }
    fun setAddress(value:String){
        DataOperator().updateQuery(
            table = "place",
            value = mutableListOf("address" to value),
            pick = pick
        )
    }
    fun move(index:Int){
        DataOperator().updateQuery(
            table = "place",
            value = mutableListOf("priority" to "priority - 1"),
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
            value = mutableListOf("priority" to index.toString()),
            pick = pick
        )
    }
    fun isPermanent():Boolean{
        return DataOperator().selectQuery(
            table = "place",
            column = "status_flag",
            pick = pick
        ).getInt() and 1.shl(0) > 0
    }
    fun setPermanent(){
        DataOperator().updateQuery(
            table = "place",
            value = mutableListOf("status_flag" to "status_flag | (1 << 0)"),
            pick = pick
        )
    }
    fun setTemporary(){
        DataOperator().updateQuery(
            table = "place",
            value = mutableListOf("status_flag" to "status_flag & ~ (1 << 0)"),
            pick = pick
        )
    }
    fun isCollision():Boolean{
        return DataOperator().selectQuery(
            table = "place",
            column = "status_flag",
            pick = pick
        ).getInt() and 1.shl(30) > 0
    }
    fun setCollisionReset(){
        DataOperator().updateQuery(
            table = "place",
            value = mutableListOf("status_flag" to "status_flag & ~ (1 << 30)"),
            pick = pick
        )
    }
    fun delete(){
        DataOperator().updateQuery(
            table = "place",
            value = mutableListOf("status_flag" to "status_flag | (1 << 31)"),
            pick = pick
        )
    }
    fun getPick():MutableMap<String, String>{
        return this.pick
    }
}
