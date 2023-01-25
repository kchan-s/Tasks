package app.sato.kchan.tasks.fanction

/********************
 * 複数のノートにかかわる処理を担当する
 *
 * クラス名: Account
 ********************/
class Account public constructor() {
    //<プロパティ>

    //<メソッド>
    fun getId():String{
        val res = DataOperator().selectQuery(
            table = "account",
            column = arrayOf("account_id")
        )
        return if(res.isResult() and !res.isNull())
            res.getString()
        else
            ""
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
        return true
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
}