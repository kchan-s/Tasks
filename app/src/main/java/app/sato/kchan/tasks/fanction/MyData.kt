package app.sato.kchan.tasks.fanction

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


/********************
 * JSONを扱う独自クラス
 *
 * クラス名: MyData
 ********************/
class MyData constructor(s:MutableMap<Int,MutableMap<String,Int>> = mutableMapOf(0 to mutableMapOf()), t:MutableMap<Int,String> = mutableMapOf(0 to "Object"), a:MutableMap<Int, Array<Int>> = mutableMapOf(), v:MutableMap<Int,String> = mutableMapOf(), r: Int = 0, h:MutableList<Int> = mutableListOf(), c: Int = 0) {
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
        println("pass value")
        return when(type[id]) {
            "Value" -> value[id]!!
            "String" -> stringEncoder(id)
            "Array" -> arrayEncoder(id)
            "Object" -> objectEncoder(id)
            "Null" -> "nul"
            else -> throw Exception("Unknown Type")
        }
    }
    private fun stringEncoder(id: Int): String {
        println("string OK")
        val my:String = value[id] ?: throw Exception("Unknown Data")
        return "\"" + my.replace("\"", "\\\"") + "\""
    }
    private fun arrayEncoder(id: Int): String {
        println("array OK")
        val list = array[id] ?: throw Exception("Unknown Data")
        var buff:Array<String> = arrayOf()
        for(item in list){
            buff += valueEncoder(item)
        }
        return "[" + buff.joinToString(",") + "]"
    }
    private fun objectEncoder(id: Int): String {
        println("object OK")
        var buff:Array<String> = arrayOf()
        for((key, valueId) in structure[id] ?: throw StructureException("")){
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
//  　　  fun values(): Array<String> {
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
    fun isData(key:String): Boolean{
        return key in (structure[current] ?: throw Exception("data does not exist"))
    }
    private fun getId(key:String): Int {
        return if (isData(key)){
            structure[current]!![key] ?: throw Exception("data does not exist")
        }else{
            val id: Int = nextC++
            type[id] = "Null"
            structure[current]!![key] = id
            id
        }
    }
    private fun getType(id: Int): String{
        return type[id] ?: throw Exception("can't get type")
    }
    fun getType(key: String): String{
        val id = getId(key)
        return getType(id)
    }
    fun move(key: String){
        val id = getId(key)
        type[id] = "Object"
        structure[id] = mutableMapOf()
        structure[current]?.set(key, id)
        hierarchy += current
        current = id
    }
    fun moves(vararg keys: String){
        for(key in keys){
            move(key)
        }
    }
    fun moveRoot(){
        current = root
    }
    fun moveChain(key: String): MyData{
        val myData = this.copy()
        myData.move(key)
        return myData
    }
    fun movesChain(vararg keys: String): MyData{
        val myData = this.copy()
        myData.moves(*keys)
        return myData
    }
    fun moveRootChain(): MyData{
        val myData = this.copy()
        myData.moveRoot()
        return myData
    }
    fun back(){
        if(hierarchy.size > 0) {
            current = hierarchy.last()
            hierarchy.removeLast()
        }else{
            current = root
        }
    }
    fun backChain(): MyData{
        val myData = this.copy()
        myData.back()
        return myData
    }
    fun delete(key:String){
        if (isData(key)) {
            val id = getId(key)
            if(type[id].toString() in "String,Value") {
                value.remove(id)
                type[id] = "Null"
            }
            structure.remove(id)
            type.remove(current)
        }
    }
    fun getString(key:String): String{
        val id = getId(key)
        when(type[id]){
            "Null" -> {
                throw Exception("Null in non-nullable type")
            }
            "Object" -> {
                throw Exception("is in object form")
            }
            "String" -> {
                return value[id] ?: throw Exception("Null in non-nullable type")
            }
            "Value" -> {
                return value[id].toString()
            }
            else -> {
                throw Exception("There is no process corresponding to the type.")
            }
        }
    }
    fun getStringOrNull(key:String): String? {
        val id = getId(key)
        when(type[id]){
            "Null" -> {
                return null
            }
            "Object" -> {
                throw Exception("is in object form")
            }
            "String" -> {
                return value[id] ?: throw Exception("Null in non-nullable type")
            }
            "Value" -> {
                return value[id].toString()
            }
            else -> {
                throw Exception("There is no process corresponding to the type.")
            }
        }
    }
    fun setString(key:String, v: String?){
        val id = getId(key)
        if(v == null)
            type[id] = "Null"
        else{
            type[id] = "String"
            value[id] = v
        }
    }
    fun getInt(key:String): Int{
        val id = getId(key)
        when(type[id]){
            "Null" -> {
                throw Exception("Null in non-nullable type")
            }
            "Object" -> {
                throw Exception("is in object form")
            }
            "String" -> {
                return value[id]?.toInt() ?: throw Exception("Null in non-nullable type")
            }
            "Value" -> {
                return value[id]?.toInt() ?: throw Exception("Null in non-nullable type")
            }
            else -> {
                throw Exception("There is no process corresponding to the type.")
            }
        }
    }
    fun getIntOrNull(key:String): Int? {
        val id = getId(key)
        when(type[id]){
            "Null" -> {
                throw Exception("Null in non-nullable type")
            }
            "Object" -> {
                throw Exception("is in object form")
            }
            "String" -> {
                return value[id]?.toInt()
            }
            "Value" -> {
                return value[id]?.toInt()
            }
            else -> {
                throw Exception("There is no process corresponding to the type.")
            }
        }
    }
    fun setInt(key:String, v: Int?){
        val id = getId(key)
        if(v == null)
            type[id] = "Null"
        else{
            type[id] = "Value"
            value[id] = v.toString()
        }
    }
    fun getDateTime(key:String): LocalDateTime{
        val id = getId(key)
        when(type[id]){
            "Null" -> {
                throw Exception("Null in non-nullable type")
            }
            "Object" -> {
                throw Exception("is in object form")
            }
            "String" -> {
                return LocalDateTime.parse(value[id], DateTimeFormatter.ofPattern("yyyy-MM-dd, hh:mm:ss")) ?: throw Exception("Null in non-nullable type")
            }
            "Value" -> {
                return LocalDateTime.parse(value[id], DateTimeFormatter.ofPattern("yyyy-MM-dd, hh:mm:ss")) ?: throw Exception("Null in non-nullable type")
            }
            else -> {
                throw Exception("There is no process corresponding to the type.")
            }
        }
    }
    fun getDateTimeOrNull(key:String): LocalDateTime? {
        val id = getId(key)
        when(type[id]){
            "Null" -> {
                return null
            }
            "Object" -> {
                throw Exception("is in object form")
            }
            "String" -> {
                return LocalDateTime.parse(value[id], DateTimeFormatter.ofPattern("yyyy-MM-dd, hh:mm:ss")) ?: throw Exception("Null in non-nullable type")
            }
            "Value" -> {
                return LocalDateTime.parse(value[id], DateTimeFormatter.ofPattern("yyyy-MM-dd, hh:mm:ss")) ?: throw Exception("Null in non-nullable type")
            }
            else -> {
                throw Exception("There is no process corresponding to the type.")
            }
        }
    }
    fun setDateTime(key:String, v: LocalDateTime?){
        val id = getId(key)
        if(v == null)
            type[id] = "Null"
        else{
            type[id] = "Value"
            value[id] = v.toString()
        }
    }

    fun copy(): MyData{
        return MyData(structure,type,array,value,root,hierarchy,current)
    }
}

