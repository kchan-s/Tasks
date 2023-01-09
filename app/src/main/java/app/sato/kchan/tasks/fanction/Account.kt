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
        return "A1B2C3"
    }
    fun isPassword():Boolean{
        return false
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
        return "仮の質問" + no.toString()
    }
    fun isLogin():Boolean{
        return false
    }
    fun login(accountId:String, password:String):Boolean{
        return true
    }
}