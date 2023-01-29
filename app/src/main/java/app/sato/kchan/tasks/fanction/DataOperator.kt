package app.sato.kchan.tasks.fanction

import android.content.ContentValues
import android.database.Cursor
import android.database.Cursor.FIELD_TYPE_NULL
import app.sato.kchan.tasks.HomeActivity.Companion.context
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.Exception


class DataOperator(){

    //<プロパティ>
    val dbHelper = DBHelper(context, "DB", null, 1);
    val database = dbHelper.writableDatabase
    //<初期化処理>
    init {
//        database.delete("account", null, null)
//        database.delete("setting", null, null)
//        database.delete("service", null, null)
    }
    //<メソッド>
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
        var c = 0
        for(col in column){
            if(c > 0)
                sql += ", "
            sql += col + ""
            c++
        }
        sql += " FROM " + table
        var values = arrayOf<String?>()
        c = 0
        for ((k, v) in pick) {
            if(c > 0)
                sql += " AND "
            else
                sql += " WHERE "
            sql += k + " = ?"
            values += v
            c++
        }
        for (fil in filter) {
            if(c > 0)
                sql += " AND "
            else
                sql += " WHERE "
            when(fil["compare"]) {
                "Big" -> {
                    sql += fil["column"] + " > ?"
                    values += fil["value"].toString()
                }
                "Small" -> {
                    sql += fil["column"] + " < ?"
                    values += fil["value"].toString()
                }
                "Equal" -> {
                    sql += fil["column"] + " = ?"
                    values += fil["value"].toString()
                }
                "Like" -> {
                    sql += fil["column"] + " LIKE ?"
                    values += fil["value"].toString()
                }
                "equation" -> {
                    sql += fil["equation"]
                }
                else -> throw Exception("そんな比較演算子使えない!! " + fil["compare"])
            }
            c++
        }
        val cursor = if(c == 0){database.rawQuery(sql, null)}else{database.rawQuery(sql, values)}
        return Res(column, cursor)
    }
    inner class Res(columns:Array<String>, cursor: Cursor) {
        private var cursor:Cursor
        private var columns:Array<String>

        private val resultFlag: Boolean
        init {
            resultFlag = cursor.moveToFirst()
            this.columns = columns
            this.cursor = cursor
            setResultTop()
        }
        private fun getNumber(column:String):Int{
            return columns.indexOf(column)
        }
        fun isResult():Boolean{
            return resultFlag
        }
        fun setResultTop():Boolean{
            return cursor.moveToFirst()
        }
        fun next():Boolean{
            cursor.moveToNext()
            return !cursor.isAfterLast
        }
        fun isNull(no:Int = 0):Boolean{
            return cursor.getType(no) == FIELD_TYPE_NULL
        }
        fun isNull(column:String):Boolean{
            val no: Int = getNumber(column)
            return isNull(no)
        }
        fun getString(no:Int = 0):String{
            return cursor.getString(no) ?: throw Exception("nullだよ")
        }
        fun getString(column:String):String{
            val no: Int = getNumber(column)
            return getString(no)
        }
        fun getStringNulls(no:Int = 0):String?{
            if(cursor.getType(no) == FIELD_TYPE_NULL) return null
            return cursor.getString(no)
        }
        fun getStringNulls(column:String):String?{
            val no: Int = getNumber(column)
            return getStringNulls(no)
        }
        fun getInt(no:Int = 0):Int{
            return cursor.getInt(no)
        }
        fun getInt(column:String):Int{
            val no: Int = getNumber(column)
            return getInt(no)
        }
        fun getFloat(no:Int = 0):Float{
            return cursor.getFloat(no)
        }
        fun getFloat(column:String):Float{
            val no: Int = getNumber(column)
            return getFloat(no)
        }
        fun getFloatNulls(no:Int = 0):Float?{
            if(cursor.getType(no) == FIELD_TYPE_NULL) return null
            return cursor.getFloat(no)
        }
        fun getFloatNulls(column:String):Float?{
            val no: Int = getNumber(column)
            return getFloat(no)
        }
        fun getBoolean(no:Int = 0):Boolean{
            return cursor.getInt(no) != 0
        }
        fun getBoolean(column:String):Boolean{
            val no: Int = getNumber(column)
            return getBoolean(no)
        }
        fun getByte(no:Int = 0): ByteArray? {
            return cursor.getBlob(no)
        }
        fun getByte(column:String): ByteArray? {
            val no: Int = getNumber(column)
            return getByte(no)
        }
        fun getDateTime(no:Int = 0): LocalDateTime? {
            val dt = cursor.getString(no) ?: return null
            return LocalDateTime.parse(cursor.getString(no), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        }
        fun getDateTime(column:String): LocalDateTime? {
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
        var vl = arrayOf<String?>()
        var sql = ""
        var c = 0

        val values = ContentValues()
        for ((k,v) in value){
            values.put(k, v)
        }

        for ((k, v) in pick) {
            if(c > 0)
                sql += " AND "
            sql += k + " = ?"
            vl += v
            c++
        }
        for (fil in filter) {
            if(c > 0)
                sql += " AND "
            when(fil["compare"]) {
                "Big" -> {
                    sql += fil["column"] + " > ?"
                    vl += fil["value"].toString()
                }
                "Small" -> {
                    sql += fil["column"] + " < ?"
                    vl += fil["value"].toString()
                }
                "Equal" -> {
                    sql += fil["column"] + " = ?"
                    vl += fil["value"].toString()
                }
                "Like" -> {
                    sql += fil["column"] + " LIKE ?"
                    vl += fil["value"].toString()
                }
                "Equation" -> {
                    sql += fil["equation"]
                }
                else -> throw Exception("そんな比較演算子使えない!! " + fil["compare"])
            }
            c++
        }

        return if(c == 0){ database.update(table, values, sql, null) }else{ database.update(table, values, sql, vl) }
//        var vl = arrayOf<String?>()
//        var sql = ""
//        var c = 0
//        sql += "UPDATE $table SET "
//        for ((k,v) in value){
//            if(c > 0)
//                sql += ", "
//            sql += "$k = ?"
//            vl += v
//            c++
//        }
//        c = 0
//        for ((k, v) in pick) {
//            sql += if(c > 0)
//                " AND "
//            else
//                " WHERE "
//            sql += "$k = ?"
//            vl += v
//            c++
//        }
//        for (fil in filter) {
//            sql += if(c > 0)
//                " AND "
//            else
//                " WHERE "
//            when(fil["compare"]) {
//                "Big" -> {
//                    sql += fil["column"] + " > ?"
//                    vl += fil["value"].toString()
//                }
//                "Small" -> {
//                    sql += fil["column"] + " < ?"
//                    vl += fil["value"].toString()
//                }
//                "Equal" -> {
//                    sql += fil["column"] + " = ?"
//                    vl += fil["value"].toString()
//                }
//                "Like" -> {
//                    sql += fil["column"] + " LIKE ?"
//                    vl += fil["value"].toString()
//                }
//                "Equation" -> {
//                    sql += fil["equation"]
//                }
//                else -> throw Exception("そんな比較演算子使えない!! " + fil["compare"])
//            }
//            c++
//        }
//        database.execSQL(sql, vl)
//        return 0
    }
    fun deleteQuery(table:String, pick:Map<String,String> = mutableMapOf(), filter:Array<Map<String,String?>> = arrayOf()):Int {
        var vl = arrayOf<String?>()
        var sql = ""
        var c = 0
        for ((k, v) in pick) {
            if(c > 0)
                sql += " AND "
            sql += k + " = ?"
            vl += v
            c++
        }
        for (fil in filter) {
            if(c > 0)
                sql += " AND "
            when(fil["compare"]) {
                "Big" -> {
                    sql += fil["column"] + " > ?"
                    vl += fil["value"].toString()
                }
                "Small" -> {
                    sql += fil["column"] + " < ?"
                    vl += fil["value"].toString()
                }
                "Equal" -> {
                    sql += fil["column"] + " = ?"
                    vl += fil["value"].toString()
                }
                "Like" -> {
                    sql += fil["column"] + " LIKE ?"
                    vl += fil["value"].toString()
                }
                "Equation" -> {
                    sql += fil["equation"]
                }
                else -> throw Exception("そんな比較演算子使えない!! " + fil["compare"])
            }
            c++
        }
        return database.delete("service", sql, vl)
    }
    fun sync() {

    }

    // fun serverConnect(value:): {

    // }

    // fun analyze():Map{

    // }
}
