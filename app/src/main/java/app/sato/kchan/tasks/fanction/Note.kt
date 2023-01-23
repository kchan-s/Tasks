package app.sato.kchan.tasks.fanction

import java.time.LocalDateTime
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter

/********************
 * 複数のノートにかかわる処理を担当する
 *
 * クラス名: NoteManager
 ********************/
class Note public constructor(pick:MutableMap<String, String>) {
    //<プロパティ>
    private var pick:MutableMap<String, String>

    //<初期化処理>
    init {
        this.pick = pick
    }

    //<メソッド>
    fun getTitle():String{
        return DataOperator().selectQuery(
            table = "note",
            column = "title",
            pick = pick
        ).getString()
    }
    fun setTitle(value:String){
        DataOperator().updateQuery(
            table = "note",
            value = mutableListOf("title" to value),
            pick = pick
        )
    }
    fun getContent():String{
        return DataOperator().selectQuery(
            table = "note",
            column = "content",
            pick = pick
        ).getString()
    }
    fun setContent(value:String){
        DataOperator().updateQuery(
            table = "note",
            value = mutableListOf("content" to value),
            pick = pick
        )
    }
    // fun getNoticeTarget(value:Int?){
    //     var nm = NoticeManager().searchByNote(this)
    //     if(nm.isNotice()){
    //         return nm.getNotice().getNoticeShow()
    //     }else{
    //         return null
    //     }
    // }
    // fun setNoticeTarget(value:Int?){
    // }
    fun getNoticeShow(): LocalDateTime? {
        var nm = NoticeManager()
        nm.searchByNote(this)
        if(nm.isNotice()){
            return nm.getNotice()?.getNoticeShow()
        }else{
            return null
        }
    }
    fun setNoticeShow(value:LocalDateTime?){
        var nm = NoticeManager()
        nm.searchByNote(this)
        val notice = if(nm.isNotice()) nm.getNotice() else nm.create(this, 0)
        notice?.setNoticeShow(value)
    }
    fun getNoticeHide():LocalDateTime?{
        var nm = NoticeManager()
        nm.searchByNote(this)
        if(nm.isNotice()){
            return nm.getNotice()?.getNoticeHide()
        }else{
            return null
        }
    }
    fun setNoticeHide(value:LocalDateTime?){
        var nm = NoticeManager()
        nm.searchByNote(this)
        val notice = if(nm.isNotice()) nm.getNotice() else nm.create(this, 0)
        notice?.setNoticeHide(value)
    }
    fun getNoticeLocation():Location?{
        var nm = NoticeManager()
        nm.searchByNote(this)
        val notice = if(nm.isNotice()) nm.getNotice() else nm.create(this, 0)
        return notice!!.getNoticeLocation()
    }
    fun setNoticeLocation(location:Location?){
        var nm = NoticeManager()
        nm.searchByNote(this)
        val notice = if(nm.isNotice()) nm.getNotice() else nm.create(this, 0)
        notice?.setNoticeLocation(location)
    }
    fun isLock():Boolean{
        return !DataOperator().selectQuery(
            table = "note",
            column = "lock_at",
            pick = pick
        ).isNull()
    }
    fun setLock(){
        DataOperator().updateQuery(
            table = "note",
            value = mutableListOf("title" to LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm:ss"))),
            pick = pick
        )
    }
    fun setUnlock(){
        DataOperator().updateQuery(
            table = "note",
            value = mutableListOf("title" to null),
            pick = pick
        )
    }
    fun isComplete():Boolean{
        return DataOperator().selectQuery(
            table = "note",
            column = "complete_at",
            pick = pick
        ).isNull()
    }
    fun setComplete(){
        DataOperator().updateQuery(
            table = "note",
            value = mutableListOf("complete_at" to LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm:ss"))),
            pick = pick
        )
    }
    fun setUncomplete(){
        DataOperator().updateQuery(
            table = "note",
            value = mutableListOf("complete_at" to null),
            pick = pick
        )
    }
    fun isCollision():Boolean{
        return DataOperator().selectQuery(
            table = "note",
            column = "status_flag",
            pick = pick
        ).getInt() and 1.shl(30) > 0
    }
    // fun getCollisionStatus(){
    // 	return do.selectQuery(table = "note", column = "status_flag", pick = pick)
    // }
    fun serCollisionReset(){
        DataOperator().updateQuery(
            table = "note",
            value = mutableListOf("status_flag" to "status_flag & ~ (1 << 30)"),
            pick = pick
        )
    }
    fun delete(){
        DataOperator().updateQuery(
            table = "note",
            value = mutableListOf("status_flag" to "status_flag | (1 << 31)"),
            pick = pick
        )
    }
    fun getPick():MutableMap<String, String>{
        return this.pick
    }
}
