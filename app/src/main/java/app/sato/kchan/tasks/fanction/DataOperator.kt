package app.sato.kchan.tasks.fanction

import android.content.ContentValues
import android.database.Cursor
import android.database.Cursor.FIELD_TYPE_NULL
import androidx.core.database.getIntOrNull
import app.sato.kchan.tasks.HomeActivity.Companion.context
import kotlinx.coroutines.launch
import java.io.File
import java.lang.String.join
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.Exception


class DataOperator() {

    //<プロパティ>
    val dbHelper = DBHelper(context, "DB", null, 1);
    val database = dbHelper.writableDatabase
    val dbInfo: MyData = dbJSON()

    //<初期化処理>
    init {
    }

    //<メソッド>
    fun insertQuery(table: String, value: Map<String, String?>) {
        val values = ContentValues()
        for ((k, v) in value) {
            values.put(k, v.toString())
        }
        database.insert(table, null, values)
    }

    fun selectQuery(
        table: String,
        column: String,
        pick: Map<String, String?> = mutableMapOf(),
        filter: Array<Map<String, String?>> = arrayOf(),
        sort: Array<Map<String, String?>> = arrayOf()
    ): Res {
        return selectQuery(table = table, column = arrayOf(column), pick = pick, filter = filter)
    }

    fun selectQuery(
        table: String,
        column: Array<String>,
        pick: Map<String, String?> = mutableMapOf(),
        filter: Array<Map<String, String?>> = arrayOf(),
        sort: Array<Map<String, String?>> = arrayOf()
    ): Res {
        var sql = ""
        sql += "SELECT "
        var c = 0
        for (col in column) {
            if (c > 0)
                sql += ", "
            sql += col + ""
            c++
        }
        sql += " FROM " + table
        var values = arrayOf<String?>()
        c = 0
        for ((k, v) in pick) {
            sql += if (c > 0)
                " AND "
            else
                " WHERE "
            sql += "$k = ?"
            values += v?.replace("\"", "\\\"") ?: v
            c++
        }
        for (fil in filter) {
            if (c > 0)
                sql += " AND "
            else
                sql += " WHERE "
            when (fil["compare"]) {
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
        val cursor = if (c == 0) {
            database.rawQuery(sql, null)
        } else {
            database.rawQuery(sql, values)
        }
        return Res(column, cursor)
    }

    inner class Res(columns: Array<String>, cursor: Cursor) {
        private var cursor: Cursor
        private var columns: Array<String>

        private val resultFlag: Boolean

        init {
            this.resultFlag = cursor.moveToFirst()
            this.columns = columns
            this.cursor = cursor
            setResultTop()
        }

        private fun getNumber(column: String): Int {
            return columns.indexOf(column)
        }

        fun isResult(): Boolean {
            return this.resultFlag
        }

        fun setResultTop(): Boolean {
            return cursor.moveToFirst()
        }

        fun next(): Boolean {
            cursor.moveToNext()
            return !cursor.isAfterLast
        }

        fun isNull(no: Int = 0): Boolean {
            return cursor.getType(no) == FIELD_TYPE_NULL
        }

        fun isNull(column: String): Boolean {
            val no: Int = getNumber(column)
            return isNull(no)
        }

        fun getString(no: Int = 0): String {
            return cursor.getString(no) ?: throw Exception("nullだよ")
        }

        fun getString(column: String): String {
            val no: Int = getNumber(column)
            return getString(no)
        }

        fun getStringNulls(no: Int = 0): String? {
            if (cursor.getType(no) == FIELD_TYPE_NULL) return null
            return cursor.getString(no)
        }

        fun getStringNulls(column: String): String? {
            val no: Int = getNumber(column)
            return getStringNulls(no)
        }

        fun getInt(no: Int = 0): Int {
            return cursor.getInt(no)
        }

        fun getInt(column: String): Int {
            val no: Int = getNumber(column)
            return getInt(no)
        }
        fun getIntNulls(no: Int = 0): Int? {
            return cursor.getIntOrNull(no)
        }

        fun getIntNulls(column: String): Int? {
            val no: Int = getNumber(column)
            return getIntNulls(no)
        }

        fun getFloat(no: Int = 0): Float {
            return cursor.getFloat(no)
        }

        fun getFloat(column: String): Float {
            val no: Int = getNumber(column)
            return getFloat(no)
        }

        fun getFloatNulls(no: Int = 0): Float? {
            if (cursor.getType(no) == FIELD_TYPE_NULL) return null
            return cursor.getFloat(no)
        }

        fun getFloatNulls(column: String): Float? {
            val no: Int = getNumber(column)
            return getFloat(no)
        }

        fun getDouble(no: Int = 0): Double {
            return cursor.getDouble(no)
        }

        fun getDouble(column: String): Double {
            val no: Int = getNumber(column)
            return getDouble(no)
        }

        fun getBoolean(no: Int = 0): Boolean {
            return cursor.getInt(no) != 0
        }

        fun getBoolean(column: String): Boolean {
            val no: Int = getNumber(column)
            return getBoolean(no)
        }

        fun getByte(no: Int = 0): ByteArray? {
            return cursor.getBlob(no)
        }

        fun getByte(column: String): ByteArray? {
            val no: Int = getNumber(column)
            return getByte(no)
        }

        fun getDateTime(no: Int = 0): LocalDateTime? {
            val dt = cursor.getString(no) ?: return null
            return LocalDateTime.parse(
                cursor.getString(no),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            )
        }

        fun getDateTime(column: String): LocalDateTime? {
            val no: Int = getNumber(column)
            return getDateTime(no)
        }

        fun getStringArray(no: Int = 0): Array<String> {
            var array = emptyArray<String>()
            if (setResultTop()) {
                do {
                    array += cursor.getString(no)
                } while (next())
            }
            return array
        }

        fun getStringArray(column: String): Array<String> {
            val no: Int = getNumber(column)
            return getStringArray(no)
        }

        fun getIntArray(no: Int = 0): Array<Int> {
            var array = emptyArray<Int>()
            if (setResultTop()) {
                do {
                    array += cursor.getInt(no)
                } while (next())
            }
            return array
        }

        fun getIntArray(column: String): Array<Int> {
            val no: Int = getNumber(column)
            return getIntArray(no)
        }

        fun getStringMap(): Map<String, String> {
            val map = mutableMapOf<String, String>()
            var i = 0
            for (col in columns) {
                map[col] = cursor.getString(i)
                i++
            }
            return map.toMap()
        }

        fun getMapStringArray(): Array<Map<String, String>> {
            var array: Array<Map<String, String>> = emptyArray<Map<String, String>>()
            if (setResultTop()) {
                do {
                    array += getStringMap()
                } while (next())
            }
            return array
        }

        fun getAllString(): String {
            var text = ""
            if (setResultTop()) {
                do {
                    text += getStringMap().toString()
                } while (next())
            }
            return text
        }
    }

    fun updateQuery(
        table: String,
        value: MutableList<Pair<String, String?>>,
        pick: Map<String, String> = mutableMapOf(),
        filter: Array<Map<String, String?>> = arrayOf()
    ): Int {
        var vl = arrayOf<String?>()
        var sql = ""
        var c = 0

        val values = ContentValues()
        for ((k, v) in value) {
            values.put(k, v)
        }

        for ((k, v) in pick) {
            if (c > 0)
                sql += " AND "
            sql += k + " = ?"
            vl += v
            c++
        }
        for (fil in filter) {
            if (c > 0)
                sql += " AND "
            when (fil["compare"]) {
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

        return if (c == 0) {
            database.update(table, values, sql, null)
        } else {
            database.update(table, values, sql, vl)
        }
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

    fun deleteQuery(table: String, pick: Map<String, String> = mutableMapOf(), filter: Array<Map<String, String?>> = arrayOf()): Int {
        var vl = arrayOf<String?>()
        var sql = ""
        var c = 0
        for ((k, v) in pick) {
            if (c > 0)
                sql += " AND "
            sql += "$k = ?"
            vl += v
            c++
        }
        for (fil in filter) {
            if (c > 0)
                sql += " AND "
            when (fil["compare"]) {
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

    fun deleteAll() {
        for(tName in dbInfo.keys())
            database.delete(tName, null, null)
    }
    fun sync(stop:Boolean = false):Boolean {
        if(true) {
            val res = selectQuery(
                table = "account",
                column = arrayOf("account_id")
            )
            if (res.setResultTop()) {
                if (res.isNull()) return false
            }else return false
        }
        var data = MyData()
        data.setString("type", "Sync")
        val content = data.moveChain("content")
        var nowDT: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"))
        var syncDT: String = ""
        val res = selectQuery(
            table = "account",
            column = arrayOf("account_id","service_id","connect_token","sync_at")
        )
        if (res.setResultTop()) {
            syncDT = res.getString("sync_at")
            data.setString("account", res.getString("account_id"))
            data.setString("service", res.getString("service_id"))
            data.setString("token", res.getString("connect_token"))
            data.setString("now_at", nowDT)
            data.setString("sync_at", syncDT)

        }
        for(tName in dbInfo.keys()){
            val table = content.moveChain(tName)
            table.initArray()
            var pickColumn:MutableList<String> = mutableListOf()
            var itemColumn:MutableList<String> = mutableListOf()
            for(cName in dbInfo.moveChain(tName).keys()) {
                val conf = dbInfo.moveChain(tName).moveChain(cName)
                if(conf.isKey("sync") && conf.getBoolean("sync")) {
                    if (conf.isKey("primary") && conf.getBoolean("primary"))
                        itemColumn += cName
                    else
                        pickColumn += cName
                }
            }
            var pickList: MutableMap<String,MutableMap<String,String>> =  mutableMapOf()
            var change: MutableMap<String,MyData> = mutableMapOf()
            for(cName in itemColumn) {
                val conf = dbInfo.moveChain(tName).moveChain(cName)
                if(!conf.isKey("update")) content
                println("OK?? ----------  " + conf.outJSON())
                val update = conf.getString("update")
                val res = selectQuery(
                    table = tName,
                    column = arrayOf(cName).plus(pickColumn),
                    filter = arrayOf(
                        mutableMapOf(
                            "column" to update,
                            "value" to syncDT,
                            "compare" to "Big"
                        )
                    )
                )
                if (res.setResultTop()) {
                    do {
                        var pickStr:String = ""
                        var pickTemp:MutableMap<String,String> =  mutableMapOf()
                        for(p in pickColumn) {
                            pickTemp[p] = res.getString(p)
                            pickStr += p + ":" + res.getString(p) + ","
                        }
                        if(pickStr !in pickList) {
                            val record = MyData()
                            change[pickStr] = record
                            table.push(record)
                            record.moveChain("pick").setStringMap(pickTemp)
                            pickList[pickStr] = pickTemp
                        }
                        change[pickStr]!!.moveChain("item").moveChain(cName).setString("value", res.getString(cName))
                        change[pickStr]!!.moveChain("item").moveChain(cName).setString("update", res.getString(update))
                    } while (res.next())
                }
            }
            if(table.size == 0) content.delete(tName)
        }
        val request: String = data.outJSON() ?: throw Exception("送信データ出力失敗")
        println("送信: " + request)
        val con = Connect()
        con.setRequest(request)
//        data.close()
        con.send()
        if(!con.waitEnd()){
            return false
        }
        var response =con.getResponse()
        println("受信: " + response)
        data = MyData()
        if(data.inJSON(response)){
            println("解析: " + data.outJSON())
            val result = data.moveChain("result")
            if(result.getInt("code") == 0){
                val content = result.moveChain("content")
                for(tName in content.keys()) {
                    val record = content.moveChain(tName)
                    for (cName in record.keys()) {
                        val column = record.moveChain("item").moveChain(cName)
                        updateQuery(
                            table = "account",
                            value = mutableListOf(cName to column.getString("value")),
                            pick = record.moveChain("pick").getStringMap(),
                            filter = arrayOf(
                                mutableMapOf(
                                    "column" to dbInfo.moveChain(tName).moveChain(cName)
                                        .getString("update"),
                                    "value" to column.getString("update"),
                                    "compare" to "Small"
                                )
                            )
                        )
                    }
                }
                return true
            }else{
                println("サーバー処理解析失敗!!")
                println("-> " + data.outJSON())
                return false
            }
        }else{
            println("JSON解析失敗!!")
            return true
        }
    }
    fun close() {

    }







    fun dbJSON():MyData {
        val dbInfo = MyData()
        dbInfo.inJSON("""
{
    "account":{
        "account_id":{
            "type": "String",
            "primary": false,
            "sync": false
        },
        "password_flag":{
            "type": "String",
            "primary": false,
            "sync": false
        },
        "secret_question1_item":{
            "type": "String",
            "primary": false,
            "sync": false
        },
        "secret_question2_item":{
            "type": "String",
            "primary": false,
            "sync": false
        },
        "secret_question3_item":{
            "type": "String",
            "primary": false,
            "sync": false
        },
        "connect_token":{
            "type": "String",
            "primary": false,
            "sync": false
        },
        "sync_at":{
            "type": "String",
            "primary": false,
            "sync": false
        }
    },
    "setting":{
        "color1":{
            "type": "Int",
            "primary": false,
            "sync": true,
            "update": "color_update_at"
        },
        "color2":{
            "type": "Int",
            "primary": false,
            "sync": true,
            "update": "color_update_at"
        },
        "color3":{
            "type": "Int",
            "primary": false,
            "sync": true,
            "update": "color_update_at"
        },
        "auto_delete_period":{
            "type": "Int",
            "primary": false,
            "sync": true,
            "update": "auto_delete_update_at"
        },
        "init_show_at":{
            "type": "DateTime",
            "primary": false,
            "sync": true,
            "update": "init_show_update_at"
        },
        "init_hide_at":{
            "type": "DateTime",
            "primary": false,
            "sync": true,
            "update": "init_hide_update_at"
        },
        "status_flag":{
            "type": "Int",
            "primary": false,
            "sync": true,
            "update": "status_update_at"
        },
        "color_update_at":{
            "type": "DateTime",
            "primary": false,
            "sync": false
        },
        "auto_delete_update_at":{
            "type": "DateTime",
            "primary": false,
            "change": false
        },
        "init_show_update_at":{
            "type": "DateTime",
            "primary": false,
            "sync": false
        },
        "init_hide_update_at":{
            "type": "DateTime",
            "primary": false,
            "sync": false
        },
        "status_update_at":{
            "type": "DateTime",
            "primary": false,
            "sync": false
        }
    },
    "service":{
        "service_id":{
            "type": "Int",
            "primary": true
        },
        "create_at":{
            "type": "DateTime",
            "primary": false,
            "sync": false
        },
        "service_name":{
            "type": "String",
            "primary": false,
            "sync": true,
            "update": "name_update_at"
        },
        "type":{
            "type": "Int",
            "primary": false,
            "sync": true,
            "update": "others_update_at"
        },
        "version":{
            "type": "Int",
            "primary": false,
            "sync": true,
            "update": "others_update_at"
        },
        "status_flag":{
            "type": "Int",
            "primary": false,
            "sync": true,
            "update": "status_update_at"
        },
        "name_update_at":{
            "type": "DateTime",
            "primary": false,
            "sync": false
        },
        "others_update_at":{
            "type": "DateTime",
            "primary": false,
            "sync": false
        },
        "status_update_at":{
            "type": "DateTime",
            "primary": false,
            "sync": false
        }
    },
    "note":{
        "note_id":{
            "type": "Int",
            "primary": true
        },
        "service_id":{
            "type": "Int",
            "primary": true
        },
        "create_at":{
            "type": "DateTime",
            "primary": false,
            "sync": false
        },
        "title":{
            "type": "String",
            "primary": false,
            "sync": true,
            "update": "title_update_at"
        },
        "content":{
            "type": "String",
            "primary": false,
            "sync": true,
            "update": "content_update_at"
        },
        "complete_at":{
            "type": "DateTime",
            "primary": false,
            "sync": true,
            "update": "complete_update_at"
        },
        "lock_at":{
            "type": "DateTime",
            "primary": false,
            "sync": true,
            "update": "lock_update_at"
        },
        "notice_bar_id":{
            "type": "Int",
            "primary": false,
            "sync": false
        },
        "status_flag":{
            "type": "Int",
            "primary": false,
            "sync": true,
            "update": "status_update_at"
        },
        "title_update_at":{
            "type": "DateTime",
            "primary": false,
            "sync": false
        },
        "content_update_at":{
            "type": "DateTime",
            "primary": false,
            "sync": false
        },
        "complete_update_at":{
            "type": "DateTime",
            "primary": false,
            "sync": false
        },
        "lock_update_at":{
            "type": "DateTime",
            "primary": false,
            "sync": false
        },
        "status_update_at":{
            "type": "DateTime",
            "primary": false,
            "sync": false
        }
    },
    "place":{
        "place_id":{
            "type": "Int",
            "primary": true
        },
        "service_id":{
            "type": "Int",
            "primary": true
        },
        "create_at":{
            "type": "DateTime",
            "primary": false,
            "sync": false
        },
        "name":{
            "type": "String",
            "primary": false,
            "sync": true,
            "update": "name_update_at"
        },
        "address":{
            "type": "String",
            "primary": false,
            "sync": true,
            "update": "address_update_at"
        },
        "latitude": {
            "type": "Int",
            "primary": false,
            "sync": true,
            "update": "address_update_at"
        },
        "longitude": {
            "type": "Int",
            "primary": false,
            "sync": true,
            "update": "address_update_at"
        },
        "priority":{
            "type": "Int",
            "primary": false,
            "sync": true,
            "update": "priority_update_at"
        },
        "status_flag":{
            "type": "Int",
            "primary": false,
            "sync": true,
            "update": "status_update_at"
        },
        "name_update_at":{
            "type": "DateTime",
            "primary": false,
            "sync": false
        },
        "address_update_at":{
            "type": "DateTime",
            "primary": false,
            "sync": false
        },
        "priority_update_at":{
            "type": "DateTime",
            "primary": false,
            "sync": false
        },
        "status_update_at":{
            "type": "DateTime",
            "primary": false,
            "sync": false
        }
    },
    "notice":{
        "notice_id":{
            "type": "Int",
            "primary": true
        },
        "service_id":{
            "type": "Int",
            "primary": true
        },
        "create_at":{
            "type": "DateTime",
            "primary": false,
            "sync": false
        },
        "target_note_id":{
            "type": "Int",
            "primary": false,
            "sync": false
        },
        "target_note_service_id":{
            "type": "Int",
            "primary": false,
            "sync": false
        },
        "target_service_id":{
            "type": "Int",
            "primary": false,
            "sync": false
        },
        "show_at":{
            "type": "DateTime",
            "primary": false,
            "sync": true,
            "update": "show_update_at"
        },
        "hide_at":{
            "type": "DateTime",
            "primary": false,
            "sync": true,
            "update": "hide_update_at"
        },
        "place_id":{
            "type": "Int",
            "primary": false,
            "sync": true,
            "update": "place_update_at"
        },
        "place_service_id":{
            "type": "Int",
            "primary": false,
            "sync": true,
            "update": "place_update_at"
        },
        "status_flag":{
            "type": "Int",
            "primary": false,
            "sync": true,
            "update": "status_update_at"
        },
        "show_update_at":{
            "type": "DateTime",
            "primary": false,
            "sync": false
        },
        "hide_update_at":{
            "type": "DateTime",
            "primary": false,
            "sync": false
        },
        "place_update_at":{
            "type": "DateTime",
            "primary": false,
            "sync": false
        },
        "status_update_at":{
            "type": "DateTime",
            "primary": false,
            "sync": false
        }
    }
}
        """)
        return dbInfo
    }
}
