package app.sato.kchan.tasks.fanction

import android.util.Log
import kotlinx.coroutines.launch

/********************
 * 複数のノートにかかわる処理を担当する
 *
 * クラス名: Account
 ********************/
class Account public constructor() {

    //<プロパティ>

    //<初期化処理>
    init {
        if(true || getId() == ""){
            val range = (0..255)
            var token = ""
            for(i in 1..64)
                token += range.random()
            DataOperator().insertQuery(
                table = "account",
                value = mutableMapOf(
                    "password_flag" to "false",
                    "connect_token" to token
                )
            )

            var data = MyData()
            data.setString("type", "New")
            data.move("content")
            val request: String = data.outJSON() ?: throw Exception("")
            println(request)
//            Log.d("Function-Account","request: $request")
            ConnectionWrapper.scope.launch{
                ConnectionWrapper().executeServerConnection(request)
//                Log.d("SettingActivity",ConnectionWrapper().postOutput())
                var response = ConnectionWrapper().postOutput()
                println(response)
                data = MyData()
                data.inJSON(response)
                println(data.keys())
                data.move("result")
                println(data.keys())
                data.move("content")
                println(data.keys())
                println(data.outJSON())
                DataOperator().updateQuery(
                    table = "account",
                    value = mutableListOf(
                        "account_id" to data.getString("account_id")
                    )
                )
            }
        }
    }
    //<メソッド>
    fun getId():String{
        val res = DataOperator().selectQuery(
            table = "account",
            column = arrayOf("account_id")
        )
        if(res.isResult())
            res.setResultTop()
            if(!res.isNull())
                return res.getString()
        return ""
    }
    fun isPassword():Boolean{
        val res = DataOperator().selectQuery(
            table = "account",
            column = arrayOf("password_flag")
        )
        return if(res.isResult())
            res.getBoolean()
        else
            throw Exception("Null Prohibited Value")
    }
    fun setPassword(password:String):Boolean{
        var data = MyData()
        data.setString("type","")
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
        return if(res.isResult() and !res.isNull())
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