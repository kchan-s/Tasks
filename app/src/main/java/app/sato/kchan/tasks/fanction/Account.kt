package app.sato.kchan.tasks.fanction

import android.provider.ContactsContract.Data
import android.util.Log
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.random.Random

/********************
 * 複数のノートにかかわる処理を担当する
 *
 * クラス名: Account
 ********************/
class Account public constructor() {

    //<プロパティ>

    //<初期化処理>
    init {
//        println("########################################################################################################################################################################################")
//        println("########################################################################################################################################################################################")

        if(getId() == "") {
            //--- 初期化処理 ここから ------------------------------
            val dt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"))
            if (!DataOperator().selectQuery(
                    table = "account",
                    column = arrayOf("account_id")
                ).setResultTop()) {
                var buff: Array<Char> = arrayOf()
                val chars = "!#\$%&()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~"
                for (i in 0..63)
                    buff += chars[Random.nextInt(chars.length)]
                val token = buff.toString()
                DataOperator().insertQuery(
                    table = "account",
                    value = mutableMapOf(
                        "password_flag" to "false",
                        "connect_token" to token
                    )
                )
            }
            if (!DataOperator().selectQuery(
                    table = "setting",
                    column = arrayOf("status_flag")
                ).setResultTop()) {
                DataOperator().insertQuery(
                    table = "setting",
                    value = mutableMapOf(
                        "color1" to "0",
                        "color2" to "0",
                        "color3" to "0",
                        "status_flag" to "0",
                        "color_update_at" to dt,
                        "auto_delete_update_at" to dt,
                        "init_show_update_at" to dt,
                        "init_hide_update_at" to dt,
                        "status_update_at" to dt
                    )
                )
            }
            if (!DataOperator().selectQuery(
                table = "service",
                column = arrayOf("service_id"),
                pick = mutableMapOf("service_id" to "0")
                ).setResultTop()){
                DataOperator().insertQuery(
                    table = "service",
                    value = mutableMapOf(
                        "service_id" to "0",
                        "create_at" to dt,
                        "service_name" to "Android",
                        "type" to "1",
                        "version" to "1",
                        "status_flag" to "0",
                        "name_update_at" to dt,
                        "others_update_at" to dt,
                        "status_update_at" to dt,
                    )
                )
            }
            //--- 初期化処理 ここまで ------------------------------


            var data = MyData()
            data.setString("type", "New")
            data.move("content")
            if(true) {
                val account = data.moveChain("account")
                account.initArray()
                val res = DataOperator().selectQuery(
                    table = "account",
                    column = arrayOf(
                        "connect_token"
                    )
                )
                if(res.setResultTop()){
                    val record = MyData()
                    record.setString("connect_token", res.getString("connect_token"))
                    account.push(record)
                }
            }
            if(true) {
                val setting = data.moveChain("setting")
                setting.initArray()
                val res = DataOperator().selectQuery(
                    table = "setting",
                    column = arrayOf(
                        "color1",
                        "color2",
                        "color3",
                        "status_flag",
                        "color_update_at",
                        "auto_delete_update_at",
                        "init_show_update_at",
                        "init_hide_update_at",
                        "status_update_at"
                    )
                )
                if(res.setResultTop()) {
                    val record = MyData()
                    record.setInt("color1", res.getInt("color1"))
                    record.setInt("color2", res.getInt("color2"))
                    record.setInt("color3", res.getInt("color3"))
                    record.setInt("status_flag", res.getInt("status_flag"))
                    record.setDateTime("color_update_at", res.getDateTime("color_update_at"))
                    record.setDateTime("auto_delete_update_at", res.getDateTime("auto_delete_update_at"))
                    record.setDateTime("init_show_update_at", res.getDateTime("init_show_update_at"))
                    record.setDateTime("init_hide_update_at", res.getDateTime("init_hide_update_at"))
                    record.setDateTime("status_update_at", res.getDateTime("status_update_at"))
                    setting.push(record)
                }
            }
            if(true) {
                val service = data.moveChain("service")
                service.initArray()
                val res = DataOperator().selectQuery(
                    table = "service",
                    column = arrayOf(
                        "create_at",
                        "service_name",
                        "type",
                        "version",
                        "status_flag",
                        "name_update_at",
                        "others_update_at",
                        "status_update_at",
                    ),
                    pick = mutableMapOf("service_id" to "0")
                )
                if(res.setResultTop()) {
                    val record = MyData()
                    record.setDateTime("create_at", res.getDateTime("create_at"))
                    record.setString("service_name", res.getString("service_name"))
                    record.setInt("type", res.getInt("type"))
                    record.setInt("version", res.getInt("version"))
                    record.setInt("status_flag", res.getInt("status_flag"))
                    record.setDateTime("name_update_at", res.getDateTime("name_update_at"))
                    record.setDateTime("others_update_at", res.getDateTime("others_update_at"))
                    record.setDateTime("status_update_at", res.getDateTime("status_update_at"))
                    service.push(record)
                }
            }
            val request: String = data.outJSON() ?: throw Exception("送信データ出力失敗")
            println(request)
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
                        DataOperator().updateQuery(
                            table = "account",
                            value = mutableListOf(
                                "account_id" to content.moveChain("account").moveChain(0).getString("account_id")
                            )
                        )
//                        println(content.moveChain("account").moveChain(0).getString("account_id"))
                    }else{
                        println("サーバー処理解析失敗!!")
                        println("-> " + data.outJSON())
                    }
                }else{
                    println("JSON解析失敗!!")
                }
            }
        }
    }
    //<メソッド>
    fun getId():String{
        val res = DataOperator().selectQuery(
            table = "account",
            column = arrayOf("account_id")
        )
        if(res.setResultTop())
            if(!res.isNull())
                return res.getString()
        return ""
    }
    fun isPassword():Boolean{
        val res = DataOperator().selectQuery(
            table = "account",
            column = arrayOf("password_flag")
        )
        return if(res.setResultTop())
            res.getBoolean()
        else
            throw Exception("Null Prohibited Value")
    }
    fun setPassword(password:String):Boolean{
        var data = MyData()
        data.setString("type","SetPass")
        data.move("")
        return false
    }
    fun changePassword(oldPassword:String, newPassword:String):Boolean{
        return true
    }
    fun resetPassword():Boolean{
        return true
    }
    fun getQuestion(no:Int):String{
        val res = DataOperator().selectQuery(
            table = "account",
            column = arrayOf("secret_question${no}_item")
        )
        return if(res.setResultTop() and !res.isNull())
            res.getString()
        else
            ""
    }
    fun login(accountId:String, password:String):Boolean{
        return true
    }




    fun hello():String{
        var data = MyData()
        data.setString("type", "Hello")
        data.move("content")
        val res: String = data.outJSON() ?: throw Exception("")
        println(res)
        return res

    }
}