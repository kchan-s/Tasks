package app.sato.kchan.tasks.fanction

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class DataOperator {
    //<プロパティ>
    lateinit var context: Context // エラー出てるんで仮置き
    val dbHelper = DBHelper(context, "DB", null, 1);
    val database = dbHelper.writableDatabase
    //<初期化処理>
    init {
    }
    //<メソッド>
//    fun getData(path:String):String{
//
//    }
//    fun setData(path:String, value:String):Unit{
//
//    }
    fun insertQuery(table:String, value:Map<String,String?>) {
        val values = ContentValues()
        for ((k, v) in value) {
            values.put(k, v)
        }
        database.insert(table, null, values)
    }
    fun selectQuery(table:String, column:String, pick:Map<String,String?> = mutableMapOf(), filter:Array<Map<String,String?>> = arrayOf(), sort:Array<Map<String,String?>> = arrayOf()):Res {
        return selectQuery(table = table, column = arrayOf(column), pick = pick, filter = filter)
    }
    fun selectQuery(table:String, column:Array<String>, pick:Map<String,String?> = mutableMapOf(), filter:Array<Map<String,String?>> = arrayOf(), sort:Array<Map<String,String?>> = arrayOf()):Res {
        var sql = ""
        sql += "SELECT "
        //var sql = "SELECT "
        var c = 0
        for(col in column){
            if(c > 0)
                sql += ", "
            sql += col + ""
            c++
        }
        sql += " FROM  " + table + " "
        var values = arrayOf<String?>()
        c = 0
        for ((k, v) in pick) {
            if(c > 0)
                sql += " AND "
            sql += k + " = ?"
            values += v
            c++
        }
        for (fil in filter) {
            if(c > 0)
                sql += " AND "
            sql += fil["column"]
            if(fil["compare"] == "Big")
                sql += " > "
            if(fil["compare"] == "Small")
                sql += " < "
            if(fil["compare"] == "Equal")
                sql += " = "
            sql += "?"
            values += fil["value"].toString()
            c++
        }
        val cursor = if(c == 0){database.rawQuery(sql, null)}else{database.rawQuery(sql, values)}
        return Res(column, cursor)
    }
    inner class Res(columns:Array<String>, cursor: Cursor) {
        private var cursor:Cursor
        private var columns:Array<String>
        init {
            this.columns = columns
            this.cursor = cursor
            setResultTop()
        }
        private fun getNumber(column:String):Int{
            return columns.indexOf(column)
        }
        fun isResult():Boolean{
            return cursor.count > 0
        }
        fun setResultTop():Boolean{
            cursor.moveToFirst()
            return cursor.count > 0
        }
        fun next():Boolean{
            cursor.moveToNext()
            return !cursor.isAfterLast
        }
        fun isNull(no:Int = 0):Boolean{
            return cursor.getString(no) == null
        }
        fun isNull(column:String):Boolean{
            val no: Int = getNumber(column)
            return isNull(no)
        }
        fun getString(no:Int = 0):String{
            return cursor.getString(no)
        }
        fun getString(column:String):String{
            val no: Int = getNumber(column)
            return getString(no)
        }
        fun getInt(no:Int = 0):Int{
            return cursor.getInt(no)
        }
        fun getInt(column:String):Int{
            val no: Int = getNumber(column)
            return getInt(no)
        }
        fun getBoolean(no:Int = 0):Boolean{
            return cursor.getInt(no) != 0
        }
        fun getBoolean(column:String):Boolean{
            val no: Int = getNumber(column)
            return getBoolean(no)
        }
        fun getBlob(no:Int = 0): ByteArray {
            return cursor.getBlob(no)
        }
        fun getBlob(column:String): ByteArray {
            val no: Int = getNumber(column)
            return getBlob(no)
        }
        fun getDateTime(no:Int = 0): LocalDateTime {
            return LocalDateTime.parse(cursor.getString(no), DateTimeFormatter.ofPattern("yyyy-MM-dd, hh:mm:ss"))
        }
        fun getDateTime(column:String): LocalDateTime {
            val no: Int = getNumber(column)
            return getDateTime(no)
        }
        fun getStringArray(no:Int = 0):Array<String> {
            var array = emptyArray<String>()
            if (setResultTop()) {
                do {
                    array += cursor.getString(no)
                } while (next())
            }
            return array
        }
        fun getStringArray(column:String):Array<String>{
            val no: Int = getNumber(column)
            return getStringArray(no)
        }
        fun getIntArray(no:Int = 0):Array<Int> {
            var array = emptyArray<Int>()
            if (setResultTop()) {
                do {
                    array += cursor.getInt(no)
                } while (next())
            }
            return array
        }
        fun getIntArray(column:String):Array<Int>{
            val no: Int = getNumber(column)
            return getIntArray(no)
        }
        fun getStringMap():Map<String,String>{
            val map = mutableMapOf<String, String>()
            var i = 0
            for(col in columns){
                map[col] = cursor.getString(i)
                i++
            }
            return map.toMap()
        }
        fun getMapStringArray():Array<Map<String,String>>{
            var array: Array<Map<String, String>> = emptyArray<Map<String,String>>()
            if(setResultTop()){
                do {
                    array += getStringMap()
                } while(next())
            }
            return array
        }
    }
    fun updateQuery(table:String, value: MutableList<Pair<String, String?>>, pick:Map<String,String> = mutableMapOf(), filter:Array<Map<String,String?>> = arrayOf()):Int {

        val values = ContentValues()
        for ((k,v) in value){
            values.put(k, v)
        }

        var vl = arrayOf<String?>()
        var sql = ""
        var c = 0

        for (fil in filter) {
            if(c > 0)
                sql += " AND "
            sql += fil["column"]
            if(fil["compare"] == "Big")
                sql += " > "
            if(fil["compare"] == "Small")
                sql += " < "
            if(fil["compare"] == "Equal")
                sql += " = "
            sql += "?"
            vl += fil["value"].toString()
            c++
        }

        for ((k, v) in pick) {
            if(c > 0)
                sql += " AND "
            sql += k + " = ?"
            vl += v
            c++
        }

        return database.update(table, values, sql, vl)
    }
    fun sync() {

    }

    // fun serverConnect(value:): {

    // }

    // fun analyze():Map{

    // }
}