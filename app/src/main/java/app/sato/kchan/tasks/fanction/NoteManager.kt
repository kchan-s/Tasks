package app.sato.kchan.tasks.fanction

import java.time.format.DateTimeFormatter
import java.time.LocalDateTime

/********************
 * 複数のノートにかかわる処理を担当する
 *
 * クラス名 :  NoteManager
 ********************/
class NoteManager public constructor(il : MutableList<String> = mutableListOf(), tl : MutableMap<String, MutableMap<String, String>> = mutableMapOf()) {
    //<プロパティ>
    private var idList : MutableList<String>
    private var point : Int = 0
    private var tempList : MutableMap<String, MutableMap<String, String>>
    private var nextTempId : Int = 0

    //<初期化処理>
    init {
        idList = il
        tempList = tl
    }

    //<メソッド>
    fun search(word : String){
        idList = mutableListOf()
        tempList = mutableMapOf()
        val res = DataOperator().selectQuery(
            table = "note",
            column = arrayOf("note_id", "service_id"),
            filter = arrayOf(mutableMapOf(
                "column" to "title",
                "value" to "%" + word + "%",
                "compare" to "Like"
            )),
            sort = arrayOf(mutableMapOf(
                "column" to "title",
                "type" to "ASC"
            ))
        )
        if(res.setResultTop()){
            do{
                val key = "note_" + nextTempId.toString()
                tempList[key] = res.getStringMap().toMutableMap()
                idList.add(key)
                nextTempId++
            } while(res.next())
        }
    }
    fun select(index:Int){
        idList = mutableListOf(idList[index])
    }
    fun selectByTempId(tempId:String){
        idList = mutableListOf(tempId)
    }
    fun addSelectByTempId(tempId:String){
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
    fun getNote(): Note? {
        val pick = getPick() ?: return null
        return Note(pick)
    }
    fun getTempId():String{
        return idList[point]
    }
    fun delete(){
        getNote()?.delete()
    }
    fun deleteAll(){
        if(isNote()){
            do{
                getNote()?.delete()
            } while(next())
        }
    }
    fun send():String{
        var buff = ""
        var item = arrayOf<String>()
        var c = 0;
        for(id in idList){
            for((column, value) in tempList[id]!!){
                if(c > 0){
                    buff += ","
                }
                buff += column + ":" + value
                c++
            }
            item += buff
        }
        return item.joinToString("|")
    }
    fun receive(text: String){
        idList = mutableListOf()
        tempList = mutableMapOf()
        var item = text.split("|")
        for(buff in item){
            var tmp:MutableMap<String, String> = mutableMapOf()
            for(b in buff.split(",")){
                var (column, value) = b.split(":")
                tmp[column] = value
            }
            tempList["note_" + nextTempId.toString()] = tmp
            idList += "note_" + nextTempId.toString()
            nextTempId++
        }
    }
    fun create():Note{
        val res = DataOperator().selectQuery(
            table = "note",
            column = "note_id",
            pick = mutableMapOf("service_id" to "0")
        )
        var max:Int = 0
        if(res.isResult()){
            for(v in res.getIntArray("note_id")){
                if(max < v){
                    max = v
                }
            }
        }
        max++
        val dt = LocalDateTime.now().toString()
        DataOperator().insertQuery(
            table = "note",
            value = mutableMapOf(
                "note_id" to max.toString(),
                "service_id" to "0",
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
        return Note(mutableMapOf("note_id" to max.toString(), "service_id" to "0"))
    }
}