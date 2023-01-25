package app.sato.kchan.tasks.fanction

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


/********************
 * JSONを扱う独自クラス
 *
 * クラス名: MyData
 ********************/
class MyData constructor(s:MutableMap<Int,MutableMap<String,Int>> = mutableMapOf(0 to mutableMapOf()), t:MutableMap<Int,String> = mutableMapOf(), a:MutableMap<Int, Array<Int>>, v:MutableMap<Int,String> = mutableMapOf(), r: Int = 0, h:MutableList<Int> = mutableListOf(), c: Int = 0) {
    //<プロパティ>
    private var structure:MutableMap<Int, MutableMap<String, Int>>
    private var type:MutableMap<Int, String>
    private var array:MutableMap<Int, Array<Int>>
    private var value:MutableMap<Int, String>
    private var nextC:Int = 0
    private var root:Int = 0
    private var current:Int = 0
    private var hierarchy: MutableList<Int>

    //<初期化処理>
    init {
        this.structure = s
        this.type = t
        this.array = a
        this.value = v
        this.root = r
        this.hierarchy = h
        this.current = c
    }

    //<メソッド>
    inner class KeyException(message: String) : Exception(message)
    inner class StructureException(message: String) : Exception(message)
    inner class IdentificationException(message: String) : Exception(message)
    private fun valueDecoder(text: List<Char>, index: Int = 0, end: String = ""): Pair<Int, Int> {
        var i:Int = index
        while(text[i] in " \n\t") { i++ }
        when(text[i]) {
            '"' -> return stringDecoder(text, i)
            '[' -> return arrayDecoder(text, i)
            '{' -> return objectDecoder(text, i)
            else -> {
                val id:Int = nextC++
                var buff:String = ""
                while(text[i] !in end && text[i] !in " \n\t") {
                    buff += text[i]
                    i++
                }
                while(text[i] in " \n\t") { i++ }
                if(text[i] !in end) throw StructureException("")
                when(buff) {
                    "null" -> {
                        type[id] = "Null"
                    }
                    else -> {
                        type[id] = "Value"
                        value[id] = buff
                    }
                }
            }
        }
        return Pair(i, 0)
    }
    private fun stringDecoder(text: List<Char>, index: Int): Pair<Int, Int> {
        val id:Int = nextC++
        type[id] = "String"
        var i:Int = index + 1
        var buff:String = ""
        while(text[i] != '"') {
            buff += text[i]
            i++
        }
        value[id] = buff
        return Pair(i, id)
    }
    private fun arrayDecoder(text: List<Char>, index: Int): Pair<Int, Int>{
        val id:Int = nextC++
        type[id] = "Array"
        array[id] = arrayOf()
        var i:Int = index
        while(text[i] != ']'){
            i++
            val (index, valueId) = valueDecoder(text, i, ",]")
            i = index
            array[id]?.plus(valueId)
        }
        return Pair(i, id)
    }
    private fun objectDecoder(text: List<Char>, index: Int): Pair<Int, Int>{
        val id: Int = nextC++
        type[id] = "Object"
        structure[id] = mutableMapOf()
        var i:Int = index + 1
        while(true) {
            while (text[i] in " \n\t") { i++ }
            var key: String = ""
            when (text[i]) {
                '"' -> {
                    i++
                    while (text[i] != '"') {
                        key += text[i]
                        i++
                    }
                    i++
                }
                '}' -> return Pair(i, id)
                else -> throw StructureException("")
            }
            while (text[i] in " \n\t") { i++ }
            if (text[i] != ':') throw KeyException("")
            i++
            while (text[i] in " \n\t") { i++ }
            val (index, valueId) = valueDecoder(text, i, ",}")
            i = index
            structure[id]?.set(key, valueId) ?: throw IdentificationException("")
            i++
        }
    }
    fun inJSON(data:String):Boolean{
        try{
            val text = data.toList()
            val (index, valueId) = valueDecoder(text)
            root = valueId
            current = valueId
        }catch(e:Exception){
            structure = mutableMapOf(0 to mutableMapOf())
            type = mutableMapOf()
            array = mutableMapOf()
            value = mutableMapOf()
            root = 0
            current = 0
            return false
        }
        return true
    }
    private fun valueEncoder(id: Int): String {
        when(type[id]) {
            "Value" -> {
                if(id in value)
                    return value[id]!!
                else
                    throw Exception("Unknown Data")
            }
            "String" -> return stringEncoder(id)
            "Array" -> return arrayEncoder(id)
            "Object" -> return objectEncoder(id)
            "Null" -> return "nul"
            else -> throw Exception("Unknown Type")
        }
    }
    private fun stringEncoder(id: Int): String {
        val my:String = value[id] ?: throw Exception("Unknown Data")
        return "\"" + my.replace("\"", "\\\"") + "\""
    }
    private fun arrayEncoder(id: Int): String {
        val list = array[id] ?: throw Exception("Unknown Data")
        var buff:Array<String> = arrayOf()
        for(item in list){
            buff += valueEncoder(item)
        }
        return "[" + buff.joinToString(",") + "]"
    }
    private fun objectEncoder(id: Int): String {
        var buff:Array<String> = arrayOf()
        val items:MutableMap<String,Int> = structure[id] ?: throw StructureException("")
        for((key, valueId) in items){
            buff += "\"" + key.replace("\"", "\\\"") + "\"" + ":" + valueEncoder(valueId)
        }
        return "{" + buff.joinToString(",") + "}"
    }
    fun outJSON():String?{
        return try{
            valueEncoder(root)
        }catch(e:Exception){
            null
        }
    }
    fun showJSON(): String{
        return outJSON() ?: return "??"
    }
    fun keys(): MutableSet<String> {
        val list:MutableSet<String> = mutableSetOf()
        if("Object" == type[current]){
            for(key in structure[current]?.keys ?: return mutableSetOf()){
                list.add(key)
            }
        }
        return list
    }
//    fun values(): Array<String> {
//        var list:Array<String> = arrayOf()
//        if("Object" == type[current]){
//            for(value in structure[current]?.values ?: return arrayOf()){
//                list += value
//            }
//        }
//        return list
//    }
//    fun items(): MutableMap<String,String> {
//
//    }
    fun type(): String{
        return type[current] ?: throw Exception("Nonexistent current element")
    }
    fun move(key: String): Boolean{
        val now = structure[current] ?: throw Exception("Nonexistent current element")
        current = now[key] ?: return false
        return true
    }
    fun moveRoot(){
        current = root
    }
    fun back(){
        if(hierarchy.size > 0) {
            current = hierarchy.last()
            hierarchy.removeLast()
        }else{
            current = root
        }
    }
    fun moveChain(key: String): MyData{
        val newData = this.copy()
        if(!newData.move(key)) throw Exception("Nonexistent element")
        return newData
    }
    fun backChain(): MyData{
        val newData = this.copy()
        newData.back()
        return newData
    }
    fun moveByPath(vararg paths: String){
        for(path in paths){
            for(key in path.split("/")) {
                when (key) {
                    "" -> this.moveRoot()
                    "." -> {}
                    ".." -> this.back()
                    else -> this.move(key)
                }
            }
        }
    }
    fun getString(): String{
        if(null == type[current] || "Null" == type[current]) throw Exception("Null in non-nullable type")
        return value[current] ?: throw Exception("Null in non-nullable type")
    }
    fun getStringOrNull(): String? {
        return value[current]
    }
    fun setString(v: String?){
        if(v == null)
            type[current] = "Null"
        else{
            type[current] = "String"
            value[current] = v
        }
    }
    fun getInt(): Int {
        if(null == type[current] || "Null" == type[current]) throw Exception("Null in non-nullable type")
        return value[current]?.toInt() ?: throw Exception("Null in non-nullable type")
    }
    fun getIntOrNull(): Int ?{
        return value[current]?.toInt()
    }
    fun setInt(v: Int?) {
        if (v == null)
            type[current] = "Null"
        else {
            type[current] = "Value"
            value[current] = v.toString()
        }
    }
    fun getBoolean(): Boolean {
        if(null == type[current] || "Null" == type[current]) throw Exception("Null in non-nullable type")
        return value[current]?.toBoolean() ?: throw Exception("Null in non-nullable type")
    }
    fun getBooleanOrNull(): Boolean? {
        return value[current]?.toBoolean()
    }
    fun setBoolean(v: Boolean?) {
        if (v == null)
            type[current] = "Null"
        else {
            type[current] = "Value"
            value[current] = v.toString()
        }
    }
    fun getDateTime(): LocalDateTime {
        if(null == type[current] || "Null" == type[current]) throw Exception("Null in non-nullable type")
        return LocalDateTime.parse(value[current], DateTimeFormatter.ofPattern("yyyy-MM-dd, hh:mm:ss")) ?: throw Exception("Null in non-nullable type")
    }
    fun getDateTimeOrNull(): LocalDateTime {
        return LocalDateTime.parse(value[current], DateTimeFormatter.ofPattern("yyyy-MM-dd, hh:mm:ss"))
    }
    fun setDateTime(v: LocalDateTime?) {
        if (v == null)
            type[current] = "Null"
        else {
            type[current] = "Value"
            value[current] = v.toString()
        }
    }
    fun delete(){
        if(type[current].toString() in "String,Value") value.remove(current)
        type.remove(current)
    }
    fun copy(): MyData{
        return MyData(structure,type,array,value,root,hierarchy,current)
    }
}

