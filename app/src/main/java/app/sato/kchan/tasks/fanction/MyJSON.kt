package app.sato.kchan.tasks.fanction


/********************
 * JSONを扱う独自クラス
 *
 * クラス名: MyJSON
 ********************/
class MyJSON constructor(s:MutableMap<Int,MutableMap<String,Int>> = mutableMapOf(), v:MutableMap<Int,Array<String>> = mutableMapOf()) {
    //<プロパティ>
    private var structure:MutableMap<Int, MutableMap<String, Int>>
    private var value:MutableMap<Int, Array<String>>
    private var current:Int = 0
    private var nextC:Int = 0

    //<初期化処理>
    init {
        this.structure = s
        this.value = v
    }

    //<メソッド>
    fun analysis(data:String):Boolean{
        val text = data.toList()
        var stack:MutableList<MutableMap<String,String>> = mutableListOf()
        var v:MutableMap<String,String> = mutableMapOf("type" to "obj", "id" to "0")
        var buff:String = ""
        try{
            structure = mutableMapOf()
            for(t in text) {
                when (v["type"]) {
                    "obj" -> {
                        when (t) {
                            ' ' -> {}
                            '\n' -> {}
                            '\t' -> {}
                            ':' -> {
                                if (v.containsKey("id")) throw Exception("Structure Error")
                                v["id"] = nextC.toString()
                                value[nextC] = arrayOf()
                                nextC++
                                buff = ""
                            }
                            '"' -> {
                                stack += v
                                v = mutableMapOf("type" to "str")
                                buff = ""
                            }
                            '[' -> {
                                if (!v.containsKey("id")) throw Exception("Structure Error")
                                val id: String = v["id"] ?: throw Exception("ID Error")
                                value[id.toInt()] = arrayOf()
                                buff = ""
                            }
                            '{' -> {
                                stack += v
                                v = mutableMapOf("type" to "obj")
                                buff = ""
                            }
                            ',' -> {
                                if (!v.containsKey("id")) throw Exception("Structure Error")
                                val id: String = v["id"] ?: throw Exception("ID Error")
                                value[id.toInt()]?.plus(buff)
                                buff = ""
                            }
                            ']' -> {
                                if (!v.containsKey("id")) throw Exception("Structure Error")
                                val id: String = v["id"] ?: throw Exception("ID Error")
                                value[id.toInt()]?.plus(buff)
                                buff = ""
                                v.remove("id")
                            }
                            '}' -> {
                                if (v.containsKey("id")) {
                                    val id: String = v["id"] ?: throw Exception("ID Error")
                                    value[id.toInt()]?.plus(buff)
                                }
                                buff = ""
                                v = stack.last()
                                stack.removeLast()
                            }
                            else -> {
                                buff += t
                            }
                        }
                    }
                    "str" -> {
                        when (t) {
                            '\\' -> {
                                buff += t
                                stack += v
                                v = mutableMapOf("type" to "esc")
                            }
                            '"' -> {
                                v = stack.last()
                                stack.removeLast()
                            }
                            else -> {
                                buff += t
                            }
                        }
                    }
                    "esc" -> {
                        buff += t
                        v = stack.last()
                        stack.removeLast()
                    }
                }
            }
        }catch(e:Exception){
            structure = mutableMapOf()
            value = mutableMapOf()
            return false
        }
        return true
    }
    fun synthesis():String?{
        val text:String = ""
        for(t in text) {

        }
        return text
    }
}

