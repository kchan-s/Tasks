package app.sato.kchan.tasks.fanction

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


/********************
 * JSONを扱う独自クラス
 *
 * クラス名: MyData
 ********************/
class MyData constructor(r: Int? = null, c: Int? = null, h:MutableList<Int> = mutableListOf()) {
    //<プロパティ>
    private var root:Int
    private var current:Int
    private var hierarchy: MutableList<Int>
    private companion object {
        var structure:MutableMap<Int, MutableMap<String, Int>> = mutableMapOf(0 to mutableMapOf())
        var type:MutableMap<Int, String> = mutableMapOf(0 to "Object")
        var array:MutableMap<Int, MutableList<Int>> = mutableMapOf()
        var value:MutableMap<Int, String> = mutableMapOf()
        var nextC:Int = 0
    }

    //<初期化処理>
    init {
        if(r == null) {
            this.root = nextC++
            this.current = this.root
            type[this.current] = "Object"
            structure[this.current] = mutableMapOf()
        }else{
            this.root = r
            if(c == null){
                this.current = root
            }else{
                this.current = c
            }
        }
        this.hierarchy = h
    }

    //<メソッド>
    inner class KeyException(message: String) : Exception(message)
    inner class StructureException(message: String) : Exception(message)
    inner class IdentificationException(message: String) : Exception(message)
    private fun valueDecoder(text: List<Char>, index: Int = 0, end: String = ""): Pair<Int, Int> {
        var i:Int = index
        while(text[i] in " \n\t") { i++ }
        when(text[i]) {
            '"' -> {
                val (index, valueId) = stringDecoder(text, i)
                i = index + 1
                while (text[i] in " \n\t") { i++ }
                if(text[i] in end)
                    return return Pair(i, valueId)
                else
                    throw StructureException("")
            }
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
                return Pair(i, id)
            }
        }
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
        array[id] = mutableListOf()
        var i:Int = index + 1
        while(text[i] != ']'){
            val (index, valueId) = valueDecoder(text, i, ",]")
            i = index
            array[id]?.plus(valueId)
            if(text[i] == ']') break
            i++
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
                '}' -> break
                else -> {
                    throw StructureException("")
                }
            }
            while (text[i] in " \n\t") { i++ }
            if (text[i] != ':') throw KeyException("")
            i++
            while (text[i] in " \n\t") { i++ }
            val (index, valueId) = valueDecoder(text, i, ",}")
            i = index
            structure[id]?.set(key, valueId) ?: throw IdentificationException("")
            if(text[i] == '}') break
            i++
        }
        return Pair(i, id)
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
        for((key, valueId) in structure[id] ?: throw StructureException("")){
            buff += "\"" + key.replace("\"", "\\\"") + "\"" + ":" + valueEncoder(valueId)
        }
        return "{" + buff.joinToString(",") + "}"
    }
    fun outJSON():String?{
        println(structure)
        println(array)
        println(type)
        println(value)
//        try{
            val text = valueEncoder(root)
            println(text)
            return text
//        }catch(e:Exception){
//            return null
//        }
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
    fun isData(index:Int): Boolean{
        println(array[current])
        return index < (array[current]?.size ?: throw Exception("data does not exist"))
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
    private fun getId(index:Int): Int {
        return if (isData(index)){
            array[current]!![index]
        }else{
            throw Exception("Index exceeds array range")
        }
    }
    private fun getType(id: Int): String{
        return type[id] ?: throw Exception("can't get type")
    }
    fun getType(key: String): String{
        val id = getId(key)
        return getType(id)
    }
    fun initArray(){
        array[current] = mutableListOf()
        type[current] = "Array"
        structure.remove(current)
        value.remove(current)
    }
    fun initObject(){
        structure[current] = mutableMapOf()
        type[current] = "Object"
        array.remove(current)
        value.remove(current)
    }
//    fun move(key: String){
//        if(type[current] == "Object"){
//            val id = structure[current]?.get(key) ?: throw Exception("data does not exist")
//            hierarchy += current
//            current = id
//        }
//    }
//    fun move(index: Int){
//        if(type[current] == "Array"){
//            val id = array[current]!![index]
//            hierarchy += current
//            current = id
//        }
//    }
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
    fun moveChain(index: Int): MyData{
        val myData = this.copy()
        myData.move(index)
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
    fun move(key: String){
        val id:Int
        if(key in structure[current]!!){
            id = structure[current]?.get(key) ?: throw Exception("data does not exist")
            if (type[id] != "Object" && type[id] != "Array") {
                type[id] = "Object"
                structure[id] = mutableMapOf()
                array.remove(id)
                value.remove(id)
            }
        }else {
            id = nextC++
            type[id] = "Object"
            structure[id] = mutableMapOf()
        }
        structure[current]?.set(key, id)
        hierarchy += current
        current = id
    }
    fun move(index: Int){
        if(type[current] != "Array") {
            array[current] = mutableListOf()
            type[current] = "Array"
            value.remove(current)
        }
        val id = getId(index)
        if(type[id] != "Object") {
            structure[id] = mutableMapOf()
            type[id] = "Object"
            array.remove(id)
            value.remove(id)
        }
        hierarchy += current
        current = id
    }
    fun back(){
        current = hierarchy.last()
        hierarchy.removeLast()
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
                return value[id] ?: throw throw Exception("data does not exist")
            }
            "Value" -> {
                return value[id].toString()
            }
            else -> {
                throw Exception("There is no process corresponding to the type.")
            }
        }
    }
    fun getString(index:Int): String {
        val id = getId(index)
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
    fun getStringOrNull(index:Int): String? {
        val id = getId(index)
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
    fun setString(index:Int, v: String?){
        val id = getId(index)
        if(v == null)
            type[id] = "Null"
        else{
            type[id] = "String"
            value[id] = v
        }
    }
    fun push(v: String?){
        val id = nextC++
        if(v == null)
            type[id] = "Null"
        else{
            type[id] = "String"
            value[id] = v
        }
        array[current]?.add(id)
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
    fun getInt(index:Int): Int{
        val id = getId(index)
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
    fun getIntOrNull(index:Int): Int? {
        val id = getId(index)
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
    fun setInt(index:Int, v: Int?){
        val id = getId(index)
        if(v == null)
            type[id] = "Null"
        else{
            type[id] = "Value"
            value[id] = v.toString()
        }
    }
    fun push(v: Int?){
        val id = nextC++
        if(v == null)
            type[id] = "Null"
        else{
            type[id] = "Value"
            value[id] = v.toString()
        }
        array[current]?.add(id)
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
                return LocalDateTime.parse(value[id], DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")) ?: throw Exception("Null in non-nullable type")
            }
            "Value" -> {
                return LocalDateTime.parse(value[id], DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")) ?: throw Exception("Null in non-nullable type")
            }
            else -> {
                throw Exception("There is no process corresponding to the type.")
            }
        }
    }
    fun getDateTime(index:Int): LocalDateTime{
        val id = getId(index)
        when(type[id]){
            "Null" -> {
                throw Exception("Null in non-nullable type")
            }
            "Object" -> {
                throw Exception("is in object form")
            }
            "String" -> {
                return LocalDateTime.parse(value[id], DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")) ?: throw Exception("Null in non-nullable type")
            }
            "Value" -> {
                return LocalDateTime.parse(value[id], DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")) ?: throw Exception("Null in non-nullable type")
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
                return LocalDateTime.parse(value[id], DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")) ?: throw Exception("Null in non-nullable type")
            }
            "Value" -> {
                return LocalDateTime.parse(value[id], DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")) ?: throw Exception("Null in non-nullable type")
            }
            else -> {
                throw Exception("There is no process corresponding to the type.")
            }
        }
    }
    fun getDateTimeOrNull(index:Int): LocalDateTime? {
        val id = getId(index)
        when(type[id]){
            "Null" -> {
                return null
            }
            "Object" -> {
                throw Exception("is in object form")
            }
            "String" -> {
                return LocalDateTime.parse(value[id], DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")) ?: throw Exception("Null in non-nullable type")
            }
            "Value" -> {
                return LocalDateTime.parse(value[id], DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")) ?: throw Exception("Null in non-nullable type")
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
            type[id] = "String"
            value[id] = v.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS"))
        }
    }
    fun setDateTime(index:Int, v: LocalDateTime?){
        val id = getId(index)
        if(v == null)
            type[id] = "Null"
        else{
            type[id] = "String"
            value[id] = v.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS"))
        }
    }
    fun push(v: LocalDateTime?){
        if(type[current] == "Array"){
            val id = nextC++
            if(v == null)
                type[id] = "Null"
            else{
                type[id] = "String"
                value[id] = v.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS"))
            }
            array[current]?.add(id)
        }else{
            throw Exception("current is not an array")
        }
    }
    fun push(myData: MyData){
        if(type[current] == "Array"){
            array[current]?.add(myData.root)
        }else{
            throw Exception("current is not an array")
        }
    }
    fun copy(): MyData{
        return MyData(root,current,hierarchy)
    }
}

