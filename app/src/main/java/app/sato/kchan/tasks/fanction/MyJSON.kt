package app.sato.kchan.tasks.fanction


/********************
 * JSONを扱う独自クラス
 *
 * クラス名: MyJSON
 ********************/
class MyJSON constructor(s:MutableMap<Int,MutableMap<String,Int>> = mutableMapOf(), v:MutableMap<Int,String> = mutableMapOf()) {
    //<プロパティ>
    private val structure:MutableMap<Int, MutableMap<String, Int>>
    private val value:MutableMap<Int, String>
    private var current = 0
    private var c = 0

    //<初期化処理>
    init {
        this.structure = s
        this.value = v
    }

    //<メソッド>
    fun analysis(data:String):Boolean{
        fun layer(text:String, i:Int, no:Int){
            var child = c
            for(t in text){
                if(t == '{'){
                    layer(data, i, child)
                }
                i++
            }
            structure[] = mutableMapOf()
            child = mutableMapOf()
        }
        try{

            layer(data.toList(), 0, 0)
        }catch(e:Exception){
            return false
        }
        return true
    }
}