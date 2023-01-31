package app.sato.kchan.tasks.fanction

import android.content.ContentValues
import android.database.Cursor
import android.database.Cursor.FIELD_TYPE_NULL
import app.sato.kchan.tasks.HomeActivity.Companion.context
import kotlinx.coroutines.launch
import java.io.File
import java.lang.String.join
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.Exception


class DataOperator(){

    //<プロパティ>
    val dbHelper = DBHelper(context, "DB", null, 1);
    val database = dbHelper.writableDatabase
    val dbInfo:MyData = dbJSON()
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
            values.put(k, v.toString())
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
        fun getDouble(no:Int = 0):Double{
            return cursor.getDouble(no)
        }
        fun getDouble(column:String):Double{
            val no: Int = getNumber(column)
            return getDouble(no)
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
        fun getAllString():String{
            var text = ""
            if(setResultTop()){
                do {
                    text += getStringMap().toString()
                } while(next())
            }
            return text
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
        return database.delete(table, sql, vl)
    }
    fun sync() {
        var data = MyData()
        data.setString("type", "Sync")
        val content = data.moveChain("content")
        var dt: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"))
        val res = selectQuery(
            table = "account",
            column = arrayOf("account_id","connect_token","sync_at")
        )
        if (res.setResultTop()) {
            data.setString("account", res.getString("account"))
            data.setString("token", res.getString("connect_token"))
            data.setDateTime("dt", LocalDateTime.now())
            dt = res.getString("sync_at")
        }
        for(tName in dbInfo.keys()){
            val table = content.moveChain(tName)
            table.initArray()
            var pickColumn:MutableList<String> = mutableListOf()
            var itemColumn:MutableList<String> = mutableListOf()
            for(col in dbInfo.moveChain(tName).keys()) {
                if(dbInfo.moveChain(tName).moveChain(col).isKey("primary") && dbInfo.moveChain(tName).moveChain(col).getString("primary") == "true"){
                    itemColumn += col
                }else{
                    pickColumn += col
                }
            }
            var pickList: MutableMap<String,MutableMap<String,String>> =  mutableMapOf()
            var change: MutableMap<String,MyData> = mutableMapOf()
            for(cName in itemColumn) {
                val update = dbInfo.moveChain(tName).moveChain(cName).getString("primary")
                val res = selectQuery(
                    table = tName,
                    column = arrayOf(cName).plus(pickColumn),
                    filter = arrayOf(
                        mutableMapOf(
                            "column" to update,
                            "value" to dt,
                            "compare" to "Small"
                        )
                    )
                )
                if (res.setResultTop()) {
                    do {
                        var pickStr:String = ""
                        var piccTemp:MutableMap<String,String> =  mutableMapOf()
                        for(p in pickColumn) {
                            piccTemp[p] = res.getString(p)
                            pickStr += p + ":" + res.getString(p) + ","
                        }
                        if(pickStr !in pickList) {
                            val record = MyData()
                            record.moveChain("pick").setStringMap(piccTemp)
                            table.push(record)
                            pickList[pickStr] = piccTemp
                        }
                        change[pickStr]!!.moveChain("item").moveChain(cName).setString("value", res.getString(cName))
                        change[pickStr]!!.moveChain("item").moveChain(cName).setString("update", res.getString(update))
                    } while (res.next())
                }
            }
        }
        val request: String = data.outJSON() ?: throw Exception("送信データ出力失敗")
        println("送信: " + request)
        ConnectionWrapper.scope.launch{
            ConnectionWrapper().executeServerConnection(request)
            var response = ConnectionWrapper().postOutput()
            println("受信: " + response)
            data = MyData()
            if(data.inJSON(response)){
                println("解析: " + data.outJSON())
                val result = data.moveChain("result")
                if(result.getInt("code") == 0){
                    val content = result.moveChain("content")
                    for(tName in content.keys()){
                        val record = content.moveChain(tName)
                        for(cName in record.keys()){
                            val column = record.moveChain("item").moveChain(cName)
                            updateQuery(
                                table = "account",
                                value = mutableListOf(cName to column.getString("value")),
                                pick = record.moveChain("pick").getStringMap(),
                                filter = arrayOf(
                                    mutableMapOf(
                                        "column" to dbInfo.moveChain(tName).moveChain(cName).getString("update"),
                                        "value" to column.getString("update"),
                                        "compare" to "Small"
                                    )
                                )
                            )
                        }
                    }

                }else{
                    println("サーバー処理解析失敗!!")
                    println("-> " + data.outJSON())
                }
            }else{
                println("JSON解析失敗!!")
            }
        }
    }

    fun dbJSON():MyData {
        val dbInfo = MyData()
        dbInfo.inJSON("""
{
    "setting":{
        "color1":{
            "type": "Int",
            "change": true,
            "update": "update_at"
        },
        "color2":{
            "type": "Int",
            "change": true,
            "update": "update_at"
        },
        "color3":{
            "type": "Int",
            "change": true,
            "update": "update_at"
        },
        "auto_delete_period":{
            "type": "Int",
            "change": true,
            "update": "update_at"
        },
        "init_show_at":{
            "type": "DateTime",
            "change": true,
            "update": "update_at"
        },
        "init_hide_at":{
            "type": "DateTime",
            "change": true,
            "update": "update_at"
        },
        "status_flag":{
            "type": "Int",
            "change": true,
            "update": "update_at"
        },
        "color_update_at":{
            "type": "DateTime",
            "change": false
        },
        "auto_delete_update_at":{
            "type": "DateTime",
            "change": false
        },
        "init_show_update_at":{
            "type": "DateTime",
            "change": false
        },
        "init_hide_update_at":{
            "type": "DateTime",
            "change": false
        },
        "status_update_at":{
            "type": "DateTime",
            "change": false
        }
    },
    "service":{
        "service_id":{
            "type": "Int",
            "primary": true,
            "change": false
        },
        "service_name":{
            "type": "String",
            "change": true,
            "update": "update_at"
        },
        "type":{
            "type": "Int",
            "change": false
        },
        "version":{
            "type": "Int",
            "change": true,
            "update": "update_at"
        },
        "create_at":{
            "type": "DateTime",
            "change": false
        },
        "status_flag":{
            "type": "Int",
            "change": true,
            "update": "update_at"
        }
    },
    "note":{
        "note_id":{
            "type": "Int",
            "primary": true,
            "change": false
        },
        "service_id":{
            "type": "Int",
            "primary": true,
            "change": false
        },
        "title":{
            "type": "String",
            "change": true,
            "update": "title_update_at"
        },
        "content":{
            "type": "String",
            "change": true,
            "update": "content_update_at"
        },
        "show_at":{
            "type": "DateTime",
            "change": true,
            "update": "show_update_at"
        },
        "hide_at":{
            "type": "DateTime",
            "change": true,
            "update": "hide_update_at"
        },
        "complete_at":{
            "type": "DateTime",
            "change": true,
            "update": "complete_update_at"
        },
        "create_at":{
            "type": "DateTime",
            "change": false
        },
        "status_flag":{
            "type": "Int",
            "change": true,
            "update": "status_update_at"
        },
        "title_update_at":{
            "type": "DateTime",
            "change": false
        },
        "content_update_at":{
            "type": "DateTime",
            "change": false
        },
        "show_update_at":{
            "type": "DateTime",
            "change": false
        },
        "hide_update_at":{
            "type": "DateTime",
            "change": false
        },
        "complete_update_at":{
            "type": "DateTime",
            "change": false
        },
        "status_update_at":{
            "type": "DateTime",
            "change": false
        }
    },
    "place":{
        "place_id":{
            "type": "Int",
            "primary": true,
            "change": false
        },
        "service_id":{
            "type": "Int",
            "primary": true,
            "change": false
        },
        "name":{
            "type": "String",
            "change": true,
            "update": "name_update_at"
        },
        "address":{
            "type": "String",
            "change": true,
            "update": "address_update_at"
        },
        "latitude": {
            "type": "Int",
            "update": "address_update_at"
        },
        "longitude": {
            "type": "Int",
            "update": "address_update_at"
        },
        "priority":{
            "type": "Int",
            "change": true,
            "update": "priority_update_at"
        },
        "create_at":{
            "type": "DateTime",
            "change": false
        },
        "status_flag":{
            "type": "Int",
            "change": true,
            "update": "status_update_at"
        },
        "name_update_at":{
            "type": "DateTime",
            "change": false
        },
        "address_update_at":{
            "type": "DateTime",
            "change": false
        },
        "priority_update_at":{
            "type": "DateTime",
            "change": false
        },
        "status_update_at":{
            "type": "DateTime",
            "change": false
        }
    },
    "notice":{
        "notice_id":{
            "type": "Int",
            "primary": true,
            "change": false
        },
        "service_id":{
            "type": "Int",
            "primary": true,
            "change": false
        },
        "note_id":{
            "type": "Int",
            "change": false
        },
        "note_service_id":{
            "type": "Int",
            "change": false
        },
        "notice_service_id":{
            "type": "Int",
            "change": false
        },
        "show_at":{
            "type": "DateTime",
            "change": true,
            "update": "show_update_at"
        },
        "hide_at":{
            "type": "DateTime",
            "change": true,
            "update": "hide_update_at"
        },
        "place_id":{
            "type": "Int",
            "change": true,
            "update": "place_update_at"
        },
        "place_service_id":{
            "type": "Int",
            "change": true,
            "update": "place_update_at"
        },
        "status_flag":{
            "type": "Int",
            "change": true,
            "update": "status_update_at"
        },
        "show_update_at":{
            "type": "DateTime",
            "change": false
        },
        "hide_update_at":{
            "type": "DateTime",
            "change": false
        },
        "place_update_at":{
            "type": "DateTime",
            "change": false
        },
        "status_update_at":{
            "type": "DateTime",
            "change": false
        }
    }
}
        """)
        return dbInfo
    }
}
