//package app.sato.kchan.tasks.fanction
//
//
///********************
// * JSONを扱う独自クラス
// *
// * クラス名: MyJSON
// ********************/
//class MyJSON constructor(s:MutableMap<Int,MutableMap<String,Int>> = mutableMapOf(), v:MutableMap<Int,String> = mutableMapOf()) {
//    //<プロパティ>
//    private var structure:MutableMap<Int, MutableMap<String, Int>>
//    private var value:MutableMap<Int, String?>
//    private var current:Int = 0
//    private var nextC:Int = 0
//
//    //<初期化処理>
//    init {
//        this.structure = s
//        this.value = v
//    }
//
//    //<メソッド>
//    fun analysis(data:String):Boolean{
//        try{
//            val text = data.toList()
//            var stack:MutableList<MutableMap<String,String>> = mutableListOf()
//            var v:MutableMap<String,String> = mutableMapOf("type" to "val", "id" to "0")
//            var buff:String = ""
//            for(t in text){
//                when(v["type"]) {
//                    "val" -> {
//                        when(t) {
//                            ' ' -> {}
//                            '{' -> {
//                                stack += v
//                                v = mutableMapOf("type" to "obj")
//                                buff = ""
//                            }
//                            '[' -> {
//                                stack += v
//                                v = mutableMapOf("type" to "arr")
//                                buff = ""
//                            }
//                            '"' -> {
//                                stack += v
//                                v = mutableMapOf("type" to "str")
//                                buff = ""
//                            }
//                            ',' -> {
//                                if(buff != ""){
//                                    val id:String = v["id"] ?: throw Exception("ID Error")
//                                    value[id.toInt()] = if(buff != "null") buff else null
//                                    v = stack.last()
//                                    stack.removeLast()
//                                }
//                                stack += v
//                                v = mutableMapOf("type" to "str")
//                            }
//                            else -> {
//                                if(buff == "null"){
//                                    val id:String = stack.last()["id"] ?: throw Exception("ID Error")
//                                    value[id.toInt()] = null
//                                }else{
//
//                                }
//                            }
//                        }
//                    }
//                    "obj" -> {
//                        when(t) {
//                            ' ' -> {}
//                            '}' -> {
//                                v = stack.last()
//                                stack.removeLast()
//                            }
//                            ',' -> {
//                                stack += v
//                                v = mutableMapOf("type" to "key")
//                            }
//                        }
//                    }
//                    "key" -> {
//                        when(t) {
//                            ' ' -> {}
//                            ':' -> {
//                                val id:String = stack.last()["id"] ?: throw Exception("ID Error")
//                                structure[id.toInt()]?.set(buff, nextC)
//                                stack += v
//                                v = mutableMapOf("type" to "val", "id" to nextC.toString())
//                                nextC++
//                                buff = ""
//                            }
//                            '"' -> {
//                                stack += v
//                                v = mutableMapOf("type" to "str")
//                            }
//                            else -> {
//                                throw Exception("Key Error")
//                                //buff += t
//                            }
//                        }
//                    }
//                    "arr" -> {
//                        when(t) {
//                            ' ' -> {}
//                            ']' -> {
//                                v = stack.last()
//                                stack.removeLast()
//                            }
//                            ',' -> {
//                                stack += v
//                                v = mutableMapOf("type" to "val")
//                            }
//                        }
//                    }
//                    "str" -> {
//                        when(t) {
//                            '"' -> {
//                                v = stack.last()
//                                stack.removeLast()
//                            }
//                            else -> {
//                                buff += t
//                            }
//                        }
//                    }
//                }
//
//            }
//            structure[] = mutableMapOf()
//            child = mutableMapOf()
//        }catch(e:Exception){
//            structure = mutableMapOf()
//            value = mutableMapOf()
//            return false
//        }
//        return true
//    }
//}
//
