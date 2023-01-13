package app.sato.kchan.tasks.fanction

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.text.SimpleDateFormat

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
        return DataOperator().selectQuery(table = "note", column = "title", pick = pick).getString("title")
    }
    fun setTitle(value:String){
        DataOperator().updateQuery(table = "note", value = mutableListOf("title" to value), pick = pick)
    }
    fun getContent():String{
        return DataOperator().selectQuery(table = "note", column = "content", pick = pick).getString("content")
    }
    fun setContent(value:String){
        DataOperator().UpdateQuery(table = "note", value = mutableListOf("content" to value), pick = pick)
    }
    fun getNoticeShow(): Date {
        return DataOperator().selectQuery(table = "notice", column = "show_at", pick = pick).getString("show_at")
    }
    fun setNoticeShow(value:Date){
        DataOperator().updateQuery(table = "notice", value = mutableListOf("show_at" to value.format(SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))), pick = pick)
    }
    fun getNoticeHide():Date{
        return DataOperator().selectQuery(table = "notice", column = "hide_at", pick = pick)
    }
    fun setNoticeHide(value:Date){
        DataOperator().updateQuery(table = "notice", value = mutableListOf("hide_at" to value), pick = pick)
    }
    fun getNoticeLocation():Location?{
        val res = DataOperator().selectQuery(table = "notice", column = arrayOf("place_id","place_service_id"), pick = pick) ?: return null
        val locaId = res.getString("place_id") ?: return null
        val locaSerId = res.getString("place_service_id") ?: return null
        return Location(mutableMapOf("place_id" to locaId, "service_id" to locaSerId))
    }
    fun setNoticeLocation(location:Location){
        val locaObj = location.getPick() ?: return
        val locaId = locaObj["location_id"] ?: return
        val locaSerId = locaObj["service_id"] ?: return
        DataOperator().updateQuery(table = "notice", value = mutableListOf("place_id" to locaId,"place_service_id" to locaSerId), pick = pick)
    }
    fun isLock():Boolean{
        return DataOperator().selectQuery(table = "notice", column = "hide_at", pick = pick)
    }
    fun setLock(){
        DataOperator().updateQuery(table = "note", value = mutableListOf("title" to true), pick = pick)
    }
    fun setUnlock(){
        DataOperator().updateQuery(table = "note", value = mutableListOf("title" to false), pick = pick)
    }
    fun isComplete():Boolean{
        return DataOperator().selectQuery(table = "notice", column = "hide_at", pick = pick)
    }
    fun setComplete(){
        DataOperator().updateQuery(table = "note", value = mutableListOf("complete_at" to , pick = pick)
    }
    fun setUncomplete(){
        DataOperator().updateQuery(table = "note", column = "complete_at", pick = pick)
    }
    fun isCollision():Boolean{
        return DataOperator().selectQuery(table = "note", column = "status_flag", pick = pick) and 0x40000000
    }
    // fun getCollisionStatus(){
    // 	return do.selectQuery(table = "note", column = "status_flag", pick = pick)
    // }
    fun serCollisionReset(){
        DataOperator().updateQuery(table = "note", value = mutableListOf("status_flag" to status_flag and 0x40000000.inv()), pick = pick)
    }
    fun delete(){
        DataOperator().updateQuery(table = "note", value = mutableListOf("status_flag" to status_flag or 0x80000000), pick = pick)
    }
    fun getPick():MutableMap<String, String>{
        return this.pick
    }
}
