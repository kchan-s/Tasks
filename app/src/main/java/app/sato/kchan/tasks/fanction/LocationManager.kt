package app.sato.kchan.tasks.fanction

import java.time.format.DateTimeFormatter
import java.time.LocalDateTime

/********************
 * 複数のノートにかかわる処理を担当する
 *
 * クラス名 :  LocationManager
 ********************/
class LocationManager public constructor(il : MutableList<String>, tl : MutableMap<String, MutableMap<String, String> >) {
    //<プロパティ>
    private var idList : MutableList<String>
    private var point : Int = 0
    private var tempList : MutableMap<String, MutableMap<String, String> >
    private var nextTempId : Int = 0

    //<初期化処理>
    init {
        idList = il ?: mutableListOf()
        tempList = tl ?: mutableMapOf<String, MutableMap<String, String>>()
    }

    //<メソッド>
    fun search(word : String){
        idList.clear()
        tempList = mutableMapOf()
        val res = DataOperator().selectQuery(table = "place", column = arrayOf("place_id", "sevice_id") , filter = arrayOf(mutableMapOf("name" to "column", word to "value", "Equal" to "compare")))
        if(res.isResult()){
            do{
                val key = "location_" + nextTempId
                tempList[key] = res.getMapString()
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

    fun getLocationNumber(): Int {
        return idList.size
    }

    fun isLocation():Boolean{
        val flag = idList.size > point
        if(!flag) point = 0
        return flag
    }
    fun next():Boolean{
        point++
        return isLocation()
    }
    fun getPick():MutableMap<String, String>?{
        val tempId = idList[point]
        return tempList[tempId]
    }
    fun getLocation():Location{
        val pick = getPick() ?: return Location(mutableMapOf())
        return Location(pick)
    }
    fun getTempId():String{
        return idList[point]
    }

    fun delete(){
        getLocation().delete()
    }
    fun deleteAll(){
        if(isLocation()){
            do{
                getLocation().delete()
            } while(next())
        }
    }
    fun create():MutableMap<String,String>{
        val res = DataOperator().selectQuery(table = "place", column = arrayOf("place_id"), pick = mutableMapOf("123" to "service_id"))
        var max:Int = 0
        if(res.isResult()){
            for(d in res.getArray()){
                if(max < d.toInt()){
                    max = d.toInt()
                }
            }
        }
        val dt = LocalDateTime.now().toString()
        DataOperator().insertQuery(table = "place", value = mutableMapOf(max.toString() to "place_id", "123" to "service_id", dt to "create_at", "Kari" to "title", "" to "content", "0" to "status_flag", dt to "title_update_at", dt to "content_update_at", dt to "completion_update_at", dt to "lock_update_at", dt to "status_update_at"))
        return mutableMapOf(max.toString() to "place_id", "123" to "service_id")
    }
}