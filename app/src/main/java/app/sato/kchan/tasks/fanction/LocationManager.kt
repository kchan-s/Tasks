package app.sato.kchan.tasks.fanction

import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.time.LocalDateTime

/********************
 * 複数のノートにかかわる処理を担当する
 *
 * クラス名 :  LocationManager
 ********************/
class LocationManager public constructor(il : MutableList<String> = mutableListOf(), tl : MutableMap<String,MutableMap<String, String>> = mutableMapOf(), nti:Int = 0) {
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
    fun search(word : String, option:Array<String> = arrayOf()){
        idList.clear()
        tempList = mutableMapOf()
        var filter:Array<Map<String,String?>> = arrayOf()
        for(ope in option){
            when(ope) {
                "PermanentFlagUp" -> {
                    filter += mutableMapOf(
                        "compare" to "equation",
                        "equation" to "status_flag & (1 << 0) = 1"
                    )
                }
            }
        }
        val res = DataOperator().selectQuery(
            table = "place",
            column = arrayOf("place_id", "service_id"),
            filter = filter + arrayOf(
                mutableMapOf(
                    "column" to "name",
                    "value" to "%" + word + "%",
                    "compare" to "Like"
                ),
                mutableMapOf(
                    "compare" to "equation",
                    "equation" to "status_flag & (1 << 31) = 0"
                )
            ),
            sort = arrayOf(mutableMapOf(
                "column" to "priority",
                "type" to "DESC"
            ))
        )
        if(res.isResult()){
            do{
                val key = "location_" + nextTempId.toString()
                val buf = res.getStringMap()
                tempList[key] = buf.toMutableMap()
                idList.add(key)
                nextTempId++
            } while(res.next())
        }
    }
    fun select(index:Int){
        idList = mutableListOf(idList[index])
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
    fun getLocation():Location?{
        val pick = getPick() ?: return null
        return Location(pick)
    }
    fun getTempId():String{
        return idList[point]
    }
    fun delete(){
        getLocation()?.delete()
    }
    fun deleteAll(){
        if(isLocation()){
            do{
                getLocation()?.delete()
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
                if (v == null) throw Exception()
                if(placeId < v){
                    placeId = v
                }
            }
        }
        placeId++
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
                if (v == null) throw Exception()
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
                "latitude" to "0",
                "longitude" to "0",
                "priority" to priority.toString(),
                "status_flag" to "0",
                "name_update_at" to dt,
                "address_update_at" to dt,
                "priority_update_at" to dt,
                "status_update_at" to dt
            )
        )
        return Location(mutableMapOf("place_id" to placeId.toString(), "service_id" to "0"))
    }
    fun copy(): LocationManager{
        return LocationManager(idList, tempList, nextTempId)
    }
}