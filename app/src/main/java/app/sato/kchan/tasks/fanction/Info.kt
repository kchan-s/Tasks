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
        return 0x808080
    }
    fun setColor(no:Int, color:Int):Unit{

    }
    fun isNormalTheme():Boolean{
        return true
    }
    fun isDarkTheme():Boolean{
        return false
    }
    fun getTheme():Int{
        return 0
    }
    fun setNormalTheme():Unit{
        return
    }
    fun setDarkTheme():Unit{
        return
    }
    fun isDefaultNotice():Boolean{
        return false
    }
    fun setDefaultNoticeEnable():Unit{
        return
    }
    fun setDefaultNoticeDisable():Unit{
        return
    }
    fun getDefaultNoticeShow():LocalDateTime{
        return LocalDateTime.now()
    }
    fun setDefaultNoticeShow(date:LocalDateTime):Unit{
        return
    }
    fun getDefaultNoticeHide(): LocalDateTime {
        return LocalDateTime.now()
    }
    fun setDefaultNoticeHide(date:LocalDateTime):Unit{
        return
    }
    fun isAutoDeletion():Boolean{
        return false
    }
    fun setAutoDeletionEnable():Unit{
        return
    }
    fun setAutoDeletionDisable():Unit{
        return
    }
    fun getAutoDeletion(): Int {
        return 720
    }
    fun setAutoDeletion(time:Int):Unit{
        return
    }
}