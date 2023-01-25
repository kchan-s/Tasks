package app.sato.kchan.tasks.fanction

import java.time.format.DateTimeFormatter
import java.time.LocalDateTime

/********************
 * 複数のノートにかかわる処理を担当する
 *
 * クラス名 :  NoticeManager
 ********************/
class NoticeManager public constructor(il : MutableList<String> = mutableListOf(), tl : MutableMap<String, MutableMap<String, String>> = mutableMapOf(), nti:Int = 0) {
    //<プロパティ>
    private var idList : MutableList<String>
    private var point : Int = 0
    private var tempList : MutableMap<String, MutableMap<String, String>>
    private var nextTempId : Int

    //<初期化処理>
    init {
        idList = il
        tempList = tl
        nextTempId = nti
    }

    //<メソッド>
    fun searchByNote(note : Note){
        idList.clear()
        tempList = mutableMapOf()
        val notePick:Map<String,String> = note.getPick()
        val res = DataOperator().selectQuery(
            table = "notice",
            column = arrayOf("notice_id", "service_id"),
            filter = arrayOf(
                mutableMapOf(
                    "column" to "target_note_id",
                    "value" to notePick["note_id"],
                    "compare" to "Equal"
                ),
                mutableMapOf(
                    "column" to "target_note_service_id",
                    "value" to notePick["service_id"],
                    "compare" to "Equal"
                ),
                mutableMapOf(
                    "compare" to "equation",
                    "equation" to "status_flag & ~(1 << 31) = 0"
                )
            ),
            sort = arrayOf(mutableMapOf(
                "column" to "create_at",
                "type" to "ASC"
            ))
        )
        if(res.isResult()){
            do{
                val key = "notice_" + nextTempId.toString()
                tempList[key] = res.getStringMap().toMutableMap()
                idList.add(key)
                nextTempId++
            } while(res.next())
        }
    }
    fun searchByServiceId(serviceId: Int){
        idList.clear()
        tempList = mutableMapOf()
        val res = DataOperator().selectQuery(
            table = "notice",
            column = arrayOf("notice_id", "service_id"),
            filter = arrayOf(
                mutableMapOf(
                    "column" to "target_service_id",
                    "value" to serviceId.toString(),
                    "compare" to "Equal"
                ),
                mutableMapOf(
                    "compare" to "equation",
                    "equation" to "status_flag & ~(1 << 31) = 0"
                )
            ),
            sort = arrayOf(mutableMapOf(
                "column" to "create_at",
                "type" to "ASC"
            ))
        )
        if(res.isResult()){
            do{
                val key = "notice_" + nextTempId.toString()
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
    fun getNoticeNumber():Int{
        return idList.size
    }
    fun isNotice():Boolean{
        val flag = idList.size > point
        if(!flag) point = 0
        return flag
    }
    fun next():Boolean{
        point++
        return isNotice()
    }
    fun getPick():MutableMap<String, String>?{
        val tempId = idList[point]
        return tempList[tempId]
    }
    fun getNotice(): Notice? {
        val pick = getPick() ?: return null
        return Notice(pick)
    }
    fun getTempId():String{
        return idList[point]
    }
    fun delete(){
        getNotice()?.delete()
    }
    fun deleteAll(){
        if(isNotice()){
            do{
                getNotice()?.delete()
            } while(next())
        }
    }
    fun send():String{
        var buff = ""
        var item = arrayOf<String>()
        var c = 0
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
        var idList = arrayOf<String>()
        tempList = mutableMapOf()
        var item = text.split("|")
        for(buff in item){
            var tmp:MutableMap<String, String> = mutableMapOf()
            for(b in buff.split(",")){
                var (column, value) = b.split(":")
                tmp[column] = value
            }
            tempList["note_" + nextTempId.toString()] = tmp
            nextTempId++
        }
    }
    fun create(target_note:Note, target_service_id:Int):Notice{
        val res = DataOperator().selectQuery(
            table = "notice",
            column = "notice_id",
            pick = mutableMapOf("service_id" to "0")
        )
        var max:Int = 0
        if(res.isResult()){
            for(v in res.getIntArray("notice_id")){
                if(max < v){
                    max = v
                }
            }
        }
        max++
        val notePick:Map<String,String> = target_note.getPick()
        val dt = LocalDateTime.now().toString()
        DataOperator().insertQuery(
            table = "notice",
            value = mutableMapOf(
                "notice_id" to max.toString(),
                "service_id" to "0",
                "create_at" to dt,
                "target_note_id" to notePick["note_id"],
                "target_note_service_id" to notePick["service_id"],
                "target_service_id" to target_service_id.toString(),
                "status_flag" to "0",
                "show_update_at" to dt,
                "hide_update_at" to dt,
                "place_update_at" to dt,
                "status_update_at" to dt
            )
        )
        return Notice(mutableMapOf("notice_id" to max.toString(), "service_id" to "0"))
    }
    fun copy(): NoticeManager{
        return NoticeManager(idList, tempList, nextTempId)
    }
}