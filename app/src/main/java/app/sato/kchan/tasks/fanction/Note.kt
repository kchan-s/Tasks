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
//    fun getTitle(){
//        return do.selectQuery(table = "note", column = "title", pick = pick)
//    }
//    fun setTitle(value:String){
//        do.updateQuery(table = "note", column = "title", value = value, pick = pick)
//    }
//    fun getContent(){
//        return do.selectQuery(table = "note", column = "content", pick = pick)
//    }
//    fun setContent(value:String){
//        do.updateQuery(table = "note", column = "content", value = value, pick = pick)
//    }
//    fun getNoticeShow(){
//        return do.selectQuery(table = "notice", column = "show_at", pick = pick)
//    }
//    fun setNoticeShow(value:String){
//        do.updateQuery(table = "notice", column = "show_at", value = value, pick = pick)
//    }
//    fun getNoticeHide(){
//        return do.selectQuery(table = "notice", column = "hide_at", pick = pick)
//    }
//    fun setNoticeHide(value:String){
//        do.updateQuery(table = "notice", column = "hide_at", value = value, pick = pick)
//    }
//    fun getNoticeLocation(){
//        return do.selectQuery(table = "notice", column = ["place_id","place_service_id"], value = value, pick = pick)
//    }
//    fun setNoticeLocation(value1:String, value2:String){
//        do.updateQuery(table = "note", column = ["place_id","place_service_id"], value = [value1, value2], pick = pick)
//    }
//    fun isLock(){
//        return do.selectQuery(table = "notice", column = "hide_at", pick = pick)
//    }
//    fun setLock(){
//        do.updateQuery(table = "note", column = "title", value = true, pick = pick)
//    }
//    fun setUnlock(){
//        do.updateQuery(table = "note", column = "title", value = false, pick = pick)
//    }
//    fun isComplete(){
//        return do.selectQuery(table = "notice", column = "hide_at", pick = pick)
//    }
//    fun setComplete(){
//        do.updateQuery(table = "note", column = "complete_at", value = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"), pick = pick)
//    }
//    fun setUncomplete(){
//        do.updateQuery(table = "note", column = "complete_at", pick = pick)
//    }
//    fun isCollision(){
//        return do.selectQuery(table = "note", column = "status_flag", pick = pick) and 0x40000000
//    }
//    // fun getCollisionStatus(){
//    // 	return do.selectQuery(table = "note", column = "status_flag", pick = pick)
//    // }
//    fun serCollisionReset(){
//        do.updateQuery(table = "note", column = "status_flag", value = status_flag and 0x40000000.inv(), pick = pick)
//    }
//    fun delete(){
//        do.updateQuery(table = "note", column = "status_flag", value = status_flag or 0x80000000, pick = pick)
//    }
}