package app.sato.kchan.tasks.fanction

import java.time.format.DateTimeFormatter
import java.time.LocalDateTime

/********************
 * 複数のノートにかかわる処理を担当する
 *
 * クラス名: Info
 ********************/
class Info public constructor() {
    //<プロパティ>

    //<メソッド>
    fun getColor(no:Int):Int{
        val res = DataOperator().selectQuery(
            table = "setting",
            column = arrayOf("color$no")
        )
        if(res.isResult())
            return res.getInt()
        else
            throw Exception("Null Prohibited Value")
    }
    fun setColor(no:Int, color:Int){
        DataOperator().updateQuery(
            table = "setting",
            value = mutableListOf(Pair("color$no", color.toString()))
        )
    }
    fun isNormalTheme():Boolean{
        val res = DataOperator().selectQuery(
            table = "setting",
            column = arrayOf("status_flag")
        )
        if(res.isResult())
            return res.getInt() and 1.shl(0) == 0
        else
            throw Exception("Null Prohibited Value")
    }
    fun isDarkTheme():Boolean{
        val res = DataOperator().selectQuery(
            table = "setting",
            column = arrayOf("status_flag")
        )
        if(res.isResult())
            return res.getInt() and 1.shl(0) == 1
        else
            throw Exception("Null Prohibited Value")
    }
    fun getTheme():Int{
        val res = DataOperator().selectQuery(
            table = "setting",
            column = arrayOf("status_flag")
        )
        if(res.isResult())
            return if( res.getInt() and 1.shl(0)==0 ) 0 else 1
        else
            throw Exception("Null Prohibited Value")
    }
    fun setNormalTheme(){
        DataOperator().updateQuery(
            table = "setting",
            value = mutableListOf(Pair("status_flag", "status_flag & ~(1 << 0)"))
        )
    }
    fun setDarkTheme(){
        DataOperator().updateQuery(
            table = "setting",
            value = mutableListOf(Pair("status_flag", "status_flag | (1 << 0)"))
        )
    }
    fun isDefaultNoticeShow():Boolean{
        val res = DataOperator().selectQuery(
            table = "setting",
            column = arrayOf("status_flag")
        )
        if(res.isResult())
            return res.getInt() and 1.shl(2) == 1
        else
            throw Exception("Null Prohibited Value")
    }
    fun isDefaultNoticeHide():Boolean{
        val res = DataOperator().selectQuery(
            table = "setting",
            column = arrayOf("status_flag")
        )
        if(res.isResult())
            return res.getInt() and 1.shl(3) == 1
        else
            throw Exception("Null Prohibited Value")
    }
    fun setDefaultNoticeShowEnable(){
        DataOperator().updateQuery(
            table = "setting",
            value = mutableListOf(Pair("status_flag", "status_flag | (1 << 2)"))
        )
    }
    fun setDefaultNoticeShowDisable(){
        DataOperator().updateQuery(
            table = "setting",
            value = mutableListOf(Pair("status_flag", "status_flag & ~(1 << 2)"))
        )
    }
    fun setDefaultNoticeHideEnable(){
        DataOperator().updateQuery(
            table = "setting",
            value = mutableListOf(Pair("status_flag", "status_flag | (1 << 2)"))
        )
    }
    fun setDefaultNoticeHideDisable(){
        DataOperator().updateQuery(
            table = "setting",
            value = mutableListOf(Pair("status_flag", "status_flag & ~(1 << 2)"))
        )
    }
    fun getDefaultNoticeShow():LocalDateTime?{
        val res = DataOperator().selectQuery(
            table = "setting",
            column = arrayOf("init_show_at")
        )
        return if(res.isResult())
            res.getDateTime()
        else
            null
    }
    fun setDefaultNoticeShow(date:LocalDateTime){
        DataOperator().updateQuery(
            table = "setting",
            value = mutableListOf(Pair("status_flag", date.toString()))
        )
    }
    fun getDefaultNoticeHide(): LocalDateTime? {
        val res = DataOperator().selectQuery(
            table = "setting",
            column = arrayOf("init_hide_at")
        )
        return if(res.isResult())
            res.getDateTime()
        else
            null
    }
    fun setDefaultNoticeHide(date:LocalDateTime){
        DataOperator().updateQuery(
            table = "setting",
            value = mutableListOf(Pair("status_flag", date.toString()))
        )
    }
    fun isAutoDeletion():Boolean{
        val res = DataOperator().selectQuery(
            table = "setting",
            column = arrayOf("status_flag")
        )
        if(res.isResult())
            return res.getInt() and 1.shl(1) == 1
        else
            throw Exception("Null Prohibited Value")
    }
    fun setAutoDeletionEnable(){
        DataOperator().updateQuery(
            table = "setting",
            value = mutableListOf(Pair("status_flag", "status_flag | (1 << 2)"))
        )
    }
    fun setAutoDeletionDisable(){
        DataOperator().updateQuery(
            table = "setting",
            value = mutableListOf(Pair("status_flag", "status_flag & ~(1 << 2)"))
        )
    }
    fun getAutoDeletion(): Int {
        val res = DataOperator().selectQuery(
            table = "setting",
            column = arrayOf("auto_delete_period")
        )
        return if(res.isResult())
            res.getInt()
        else
            throw Exception("Null Prohibited Value")
    }
    fun setAutoDeletion(time:Int){
        DataOperator().updateQuery(
            table = "setting",
            value = mutableListOf(Pair("auto_delete_period", time.toString()))
        )
    }
}