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
        return DataOperator.selectQuery(table = "place", column = "name", pick = pick).getString()
    }
    fun setName(value:String):Unit{
        DataOperator.updateQuery(table = "place", value = mutableListOf("name" to value), pick = pick)
    }
    fun getAddress():String{
        return DataOperator.selectQuery(table = "place", column = "address", pick = pick)
    }
    fun setAddress(value:String):Unit{
        DataOperator.updateQuery(table = "place", value = mutableListOf("address" to value), pick = pick)
    }
    fun move(index:Int):Unit{
        DataOperator.updateQuery(table = "place", value = mutableListOf("priority" to "priority - 1"), filter = arrayOf(mutableMapOf("priority" to "column", index to "value", "Small" to "compare")))
        DataOperator.updateQuery(table = "place", value = mutableListOf("priority" to index), pick = pick)
    }
    fun isCollision():Boolean{
        return DataOperator.selectQuery(table = "place", column = "status_flag", pick = pick) and 0x40000000
    }
    fun setCollisionReset():Unit{
        DataOperator.updateQuery(table = "place", value = mutableListOf("status_flag" to "status_flag & ~ 0x40000000"), pick = pick)
    }
    fun delete():Unit{
        DataOperator.updateQuery(table = "place", value = mutableListOf("status_flag" to "status_flag | 0x80000000"), pick = pick)
    }
}

