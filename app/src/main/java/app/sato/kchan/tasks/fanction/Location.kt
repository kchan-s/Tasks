package app.sato.kchan.tasks.fanction

/********************
 * 複数のノートにかかわる処理を担当する
 *
 * クラス名: NoteManager
 ********************/
class Location public constructor(pick:MutableMap<String, String>) {
    //<プロパティ>
    var pick:MutableMap<String, String>

    //<初期化処理>
    init {
        this.pick = pick
    }

    //<メソッド>
    fun getName():String{
        return do.selectQuery(table = "place", column = "name", pick = pick)
    }
    fun setName(value:String):Unit{
        do.updateQuery(table = "place", column = "name", value = value, pick = pick)
    }
    fun getAddress():String{
        return do.selectQuery(table = "place", column = "address", pick = pick)
    }
    fun setAddress(value:String):Unit{
        do.updateQuery(table = "place", column = "address", value = value, pick = pick)
    }
    fun move(index:Int):Unit{
        do.updateQuery(table = "place", column = "priority", value = "priority - 1", filter = arrayOf(mutableMapOf("priority" to "column", index to "value", "Small" to "compare")))
        do.updateQuery(table = "place", column = "priority", value = index, pick = pick)
    }
    fun isCollision():Boolean{
        return do.selectQuery(table = "place", column = "status_flag", pick = pick) and 0x40000000
    }
    fun setCollisionReset():Unit{
        do.updateQuery(table = "place", column = "status_flag", value = "status_flag & ~ 0x40000000", pick = pick)
    }
    fun delete():Unit{
        do.updateQuery(table = "place", column = "status_flag", value = "status_flag | 0x80000000", pick = pick)
    }
}