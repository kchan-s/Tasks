package app.sato.kchan.tasks.fanction

import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.time.LocalDateTime

/********************
 * 複数のノートにかかわる処理を担当する
 *
 * クラス名 :  LocationManager
 ********************/
class LocationManager public constructor(il : MutableList<String> = mutableListOf(), tl : MutableMap<String,MutableMap<String, String>> = mutableMapOf()) {
    //<プロパティ>
    private var idList : MutableList<String>
    private var point : Int = 0
    private var tempList : MutableMap<String, MutableMap<String, String> >
    private var nextTempId : Int = 0

    //<初期化処理>
    init {
        idList = il
        tempList = tl
    }

    //<メソッド>
    fun search(word : String){
        idList.clear()
        tempList = mutableMapOf()
        val res = DataOperator().selectQuery(
            table = "place",
            column = arrayOf("place_id", "service_id"),
            filter = arrayOf(mutableMapOf(
                "column" to "name",
                "value" to word,
                "compare" to "Equal"
            )),
            sort = arrayOf(mutableMapOf(
                "column" to "priority",
                "type" to "DESC"
            ))
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

    fun getLocationNumber():Int {
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
    fun create():Location{
        var res = DataOperator().selectQuery(
            table = "place",
            column = "place_id",
            pick = mutableMapOf("service_id" to "0"),
            sort = arrayOf(mutableMapOf(
                "column" to "place_id",
                "type" to "DESC"
            ))
        )
        var placeId:Int = 0
        if(res.isResult()){
            for(v in res.getIntArray("place_id")){
                if(placeId < v){
                    placeId = v
                }
            }
        }
        res = DataOperator().selectQuery(
            table = "place",
            column = "priority",
            sort = arrayOf(mutableMapOf(
                "column" to "priority",
                "type" to "DESC"
            ))
        )
        var priority:Int = 0
        if(res.isResult()){
            for(v in res.getIntArray("priority")){
                if(priority < v){
                    priority = v
                }
            }
        }
        val dt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now())
        DataOperator().insertQuery(
            table = "place",
            value = mutableMapOf(
                "place_id" to placeId.toString(),
                "service_id" to "0",
                "create_at" to dt,
                "name" to "",
                "address" to "",
                "priority" to priority.toString(),
            )
        )
        return Location(mutableMapOf("place_id" to placeId.toString(), "service_id" to "0"))
    }
}