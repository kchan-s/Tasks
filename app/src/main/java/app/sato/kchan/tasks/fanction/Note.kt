package app.sato.kchan.tasks.fanction

/********************
 * 複数のノートにかかわる処理を担当する
 *
 * クラス名: NoteManager
 ********************/
class Note public constructor(pick:MutableMap<String, String>) {
    //<プロパティ>
    var pick:MutableMap<String, String>

    //<初期化処理>
    init {
        this.pick = pick
    }

    //<メソッド>
    fun getTitle(){
        return DataOperator.selectQuery(table = "note", column = "title", pick = pick)
    }
    fun setTitle(value:String){
        DataOperator.updateQuery(table = "note", value = mutableListOf("title" to value), pick = pick)
    }
    fun getContent(){
        return DataOperator.selectQuery(table = "note", column = "content", pick = pick)
    }
    fun setContent(value:String){
        DataOperator.UpdateQuery(table = "note", value = mutableListOf("content" to value), pick = pick)
    }
    fun getNoticeShow(){
        return DataOperator.selectQuery(table = "notice", column = "show_at", pick = pick)
    }
    fun setNoticeShow(value:String){
        DataOperator.updateQuery(table = "notice", value = mutableListOf("show_at" to value), pick = pick)
    }
    fun getNoticeHide(){
        return DataOperator.selectQuery(table = "notice", column = "hide_at", pick = pick)
    }
    fun setNoticeHide(value:String){
        DataOperator.updateQuery(table = "notice", value = mutableListOf("hide_at" to value), pick = pick)
    }
    fun getNoticeLocation(){
        return DataOperator.selectQuery(table = "notice", column = ["place_id","place_service_id"], value = value, pick = pick)
    }
    fun setNoticeLocation(value1:String, value2:String){
        DataOperator.updateQuery(table = "note", value = mutableListOf("place_id" to value1,"place_service_id" to value2), pick = pick)
    }
    fun isLock(){
        return DataOperator.selectQuery(table = "notice", column = "hide_at", pick = pick)
    }
    fun setLock(){
        DataOperator.updateQuery(table = "note",, value = mutableListOf("title" to true), pick = pick)
    }
    fun setUnlock(){
        DataOperator.updateQuery(table = "note", value = mutableListOf("title" to false), pick = pick)
    }
    fun isComplete(){
        return DataOperator.selectQuery(table = "notice", column = "hide_at", pick = pick)
    }
    fun setComplete(){
        DataOperator.updateQuery(table = "note", value = mutableListOf("complete_at" to DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), pick = pick)
    }
    fun setUncomplete(){
        DataOperator.updateQuery(table = "note", column = "complete_at", pick = pick)
    }
    fun isCollision(){
        return DataOperator.selectQuery(table = "note", column = "status_flag", pick = pick) and 0x40000000
    }
    // fun getCollisionStatus(){
    // 	return do.selectQuery(table = "note", column = "status_flag", pick = pick)
    // }
    fun serCollisionReset(){
        DataOperator.updateQuery(table = "note", value = mutableListOf("status_flag" to status_flag and 0x40000000.inv()), pick = pick)
    }
    fun delete(){
        DataOperator.updateQuery(table = "note", value = mutableListOf("status_flag" to status_flag or 0x80000000), pick = pick)
    }
}