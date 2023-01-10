package app.sato.kchan.tasks.fanction

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import app.sato.kchan.tasks.HomeActivity


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
    fun insertQuery(table:String, value:Map<String,String>) {
        val values = ContentValues()
        for ((k, v) in value) {
            values.put(k, v)
        }
        database.insert(table, null, values)
    }
    fun selectQuery(table:String, column:String, pick:Map<String,String>, filter:Array<Map<String,String>>):Res {
        return selectQuery(table = table, column = arrayOf(column), pick = pick, filter = filter)
    }
    fun selectQuery(table:String, column:Array<String>, pick:Map<String,String>, filter:Array<Map<String,String>>):Res {
        var sql = ""
        sql = sql + "SELECT "
        //var sql = "SELECT "
        var c = 0
        for(col in column){
            if(c > 0)
                sql = sql + ", "
            sql = sql + col + ""
            c++
        }
        sql = sql + " FROM  " + table + " "
        var values = arrayOf<String>()
        c = 0
        for ((k, v) in pick) {
            if(c > 0)
                sql = sql + " AND "
            sql = sql + k + " = ?"
            values += v
            c++
        }
        for (fil in filter) {
            if(c > 0)
                sql = sql + " AND "
            sql = sql + fil["column"]
            if(fil["compare"] == "Big")
                sql = sql + " > "
            if(fil["compare"] == "Small")
                sql = sql + " < "
            if(fil["compare"] == "Equal")
                sql = sql + " = "
            sql = sql + "?"
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
            this.cursor = cursor
            this.columns = columns
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
        fun getString(no:Int):String?{
            return cursor.getString(no)
        }
        fun getString(column:String):String?{
            val no:Int = getNumber(column)
            if(no == null) return null
            return getString(no)
        }
        fun getInt(no:Int):Int?{
            return cursor.getInt(no)
        }
        fun getInt(column:String):Int?{
            val no:Int = getNumber(column)
            if(no == null) return null
            return getInt(no)
        }
        //        fun getBlob(no:Int):Int?{
//            return cursor.getBlob(no)
//        }
        fun getBlob(no:Int): ByteArray? {
            return cursor.getBlob(no)
        }
        //        fun getBlob(column:String):Int?{
//            val no:Int = getNumber(column)
//            if(no == null) return null
//            return getBlob(no)
//        }
        fun getBlob(column:String): ByteArray? {
            val no:Int = getNumber(column)
            if(no == null) return null
            return getBlob(no)
        }
        fun getArray(no:Int):Array<String>?{
            var array = emptyArray<String>()
            if(setResultTop()){
                do {
                    array += cursor.getString(no)
                } while(next())
            }
            return array
        }
        fun getArray():Array<String>?{
            return getArray(0)
        }
        fun getArray(column:String):Array<String>?{
            val no:Int = getNumber(column)
            if(no == null) return null
            return getArray(no)
        }
        fun getMap():Map<String,String>{
            var map = mutableMapOf<String, String>()
            var i = 0
            for(col in columns){
                map.put(col,cursor.getString(i))
                i++
            }
            return map.toMap()
        }
        fun getMapArray():Array<Map<String,String>>{
            var array = emptyArray<Map<String,String>>()
            if(setResultTop()){
                do {
                    array += getMap()
                } while(next())
            }
            return array
        }
    }
    fun updateQuery(table:String, value:Map<String,String>, pick:Map<String,String>, filter:Array<Map<String,String>>) {

        val values = ContentValues()
        for ((k,v) in value){
            values.put(k, v)
        }

        var vl = arrayOf<String>()
        var sql = ""
        var c = 0

        for (fil in filter) {
            if(c > 0)
                sql = sql + " AND "
            sql = sql + fil["column"]
            if(fil["compare"] == "Big")
                sql = sql + " > "
            if(fil["compare"] == "Small")
                sql = sql + " < "
            if(fil["compare"] == "Equal")
                sql = sql + " = "
            sql = sql + "?"
            vl += fil["value"].toString()
            c++
        }

        for ((k, v) in pick) {
            if(c > 0)
                sql = sql + " AND "
            sql = sql + k + " = ?"
            vl += v
            c++
        }

        database.update(table, values, sql, vl)
    }
    fun sync() {

    }

    // fun serverConnect(value:): {

    // }

    // fun analyze():Map{

    // }
}