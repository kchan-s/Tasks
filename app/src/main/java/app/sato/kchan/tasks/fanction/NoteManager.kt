package app.sato.kchan.tasks.fanction

import java.time.format.DateTimeFormatter
import java.time.LocalDateTime


/********************
 * 複数のノートにかかわる処理を担当する
 *
 * クラス名 :  NoteManager
 ********************/
class NoteManager public constructor(il : MutableList<String>, tl : MutableMap<String, MutableMap<String, String> >) {
    //<プロパティ>
    private var idList : MutableList<String>
    private var point : Int = 0
    private var tempList : MutableMap<String, MutableMap<String, String> >
    private var nextTempId : Int = 0

    //<初期化処理>
    init {
        idList = il ?: mutableListOf()
        tempList = tl ?: mutableMapOf()
    }

    //<メソッド>
    fun search(word : String){
        idList.clear()
        tempList = mutableMapOf()
        val res = DataOperator().selectQuery(
            table = "note",
            column = arrayOf("note_id", "service_id"),
            filter = arrayOf(mutableMapOf("column" to "title", "value" to word, "compare" to "Equal"))
        )
        if(res.isResult()){
            do{
                val key = "location_" + nextTempId.toString()
                tempList[key] = res.getStringMap().toMutableMap()
                idList.add(key)
                nextTempId++
            } while(res.next())
        }
    }
    fun selectByTempId(tempId:String){
        idList.add(tempId)
    }
    fun deselection(){
        idList.clear()
    }
    fun getNoteNumber():Int{
        return idList.size
    }
    fun isNote():Boolean{
        val flag = idList.size > point
        if(!flag) point = 0
        return flag
    }
    fun next():Boolean{
        point++
        return isNote()
    }
    fun getPick():MutableMap<String, String>?{
        val tempId = idList[point]
        return tempList[tempId]
    }
    fun getNote(): Note {
        val pick = getPick() ?: return Note(mutableMapOf())
        return Note(pick)
    }
    fun getTempId():String{
        return idList[point]
    }
    fun delete(){
        getNote().delete()
    }
    fun deleteAll(){
        if(isNote()){
            do{
                getNote().delete()
            } while(next())
        }
    }
    fun create():MutableMap<String,String>{
        val res = DataOperator().selectQuery(
            table = "note",
            column = "note_id",
            pick = mutableMapOf("service_id" to "123")
        )
        var max:Int = 0
        if(res.isResult()){
            for(v in res.getIntArray("note_id")){
                if(max < v){
                    max = v
                }
            }
        }
        val dt = LocalDateTime.now().toString()
        DataOperator().insertQuery(
            table = "note",
            value = mutableMapOf(
                "note_id" to max.toString(),
                "service_id" to "123",
                "create_at" to dt,
                "title" to "Kari",
                "content" to "",
                "status_flag" to "0",
                "title_update_at" to dt,
                "content_update_at" to dt,
                "completion_update_at" to dt,
                "lock_update_at" to dt,
                "status_update_at" to dt
            )
        )
        return mutableMapOf(max.toString() to "place_id", "123" to "service_id")
    }
}