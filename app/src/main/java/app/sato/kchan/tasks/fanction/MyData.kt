package app.sato.kchan.tasks.fanction

import android.util.Log
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
//    private var type:MutableMap<Int, String>
    private companion object {
        var structure:MutableMap<Int, MutableMap<String, Int>> = mutableMapOf()
        var type:MutableMap<Int, String> = mutableMapOf()
        var array:MutableMap<Int, MutableList<Int>> = mutableMapOf()
        var value:MutableMap<Int, String> = mutableMapOf()
        var use:Int = 0
        var nextC:Int = 0
    }

    //<初期化処理>
    init {
        if(r == null) {
            val id = newId()
            this.root = id
            this.current = id
            type[id] = "Object"
            structure[id] = mutableMapOf()
        }else{
            this.root = r
            this.current = c ?: r
        }
        this.hierarchy = h
    }

    //<メソッド>
    private fun newId():Int{
        var id:Int = 0
        for(i in 1..Int.MAX_VALUE) {
            if(nextC >= Int.MAX_VALUE) nextC = 0
            id = nextC++
            if(use and (1 shl id) == 0) {
                use = ( use and (1 shl id) )
                return id
            }
        }
        throw Exception("最大要素数を超えました。")
    }
    private fun delId(id:Int){
        use = ( use and (1 shl id).inv() )
    }
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
                while (i < text.size && text[i] in " \n\t") i++
                if(i == text.size || text[i] in end)
                    return return Pair(i, valueId)
                else
                    throw StructureException("")
            }
            '[' -> {
                val (index, valueId) = arrayDecoder(text, i)
                i = index + 1
                while (i < text.size && text[i] in " \n\t") i++
                if(i == text.size || text[i] in end)
                    return return Pair(i, valueId)
                else
                    throw StructureException("")
            }
            '{' -> {
                val (index, valueId) = objectDecoder(text, i)
                i = index + 1
                while (i < text.size && text[i] in " \n\t") i++
                if(i == text.size || text[i] in end)
                    return return Pair(i, valueId)
                else {
                    throw StructureException("Hit: " + text[i])
                }
            }
            else -> {
                val id:Int = newId()
                var buff:String = ""
                while(i < text.size && text[i] !in end && text[i] !in " \n\t") {
                    buff += text[i]
                    i++
                }
                while(i < text.size && text[i] in " \n\t") i++
                if(i >= text.size) i--
                if(text[i] !in end) throw StructureException(i.toString() + "/" + text.size.toString() + "  " + text[i] + "   " + text)
                when(buff) {
                    "null" -> {
                        type[id] = "Null"
                    }
                    "false" -> {
                        type[id] = "Value"
                        value[id] = false.toString()
                    }
                    "true" -> {
                        type[id] = "Value"
                        value[id] = true.toString()
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
        val id:Int = newId()
        type[id] = "String"
        var i:Int = index + 1
        var buff:String = ""
        while(text[i] != '"') {
            buff += text[i]
            i++
        }
        value[id] = buff.replace("\"", "\\\"")
        return Pair(i, id)
    }
    private fun arrayDecoder(text: List<Char>, index: Int): Pair<Int, Int>{
        val id:Int = newId()
        type[id] = "Array"
        array[id] = mutableListOf()
        var i:Int = index + 1
        while(text[i] != ']'){
            val (index, valueId) = valueDecoder(text, i, ",]")
            i = index
            array[id]?.add(valueId)
            if(text[i] == ']') break
            i++
        }
        return Pair(i, id)
    }
    private fun objectDecoder(text: List<Char>, index: Int): Pair<Int, Int>{
        val id: Int = newId()
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
            return true
        }catch(e:Exception){
            Log.w("MyData/inJSON", "解析に失敗しました")
            Log.d("MyData/inJSON", data.replace("\n"," "))
            return false
        }
    }
    private fun valueEncoder(id: Int): String {
        return when(type[id]) {
            "Value" -> {
                when(value[id]) {
                    null ->  throw Exception("data does not exist")
                    "true" -> "true"
                    "false" -> "false"
                    else -> value[id] ?: throw Exception("data does not exist")
                }
            }
            "String" -> stringEncoder(id)
            "Array" -> arrayEncoder(id)
            "Object" -> objectEncoder(id)
            "Null" -> "null"
            else -> {
                Log.d("MyData/outJSON", "Type: $type")
                Log.d("MyData/outJSON", "ID:   $id")
                throw Exception("Unknown Type")
            }
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
//        try{
            return valueEncoder(root)
//        }catch(e:Exception){
//            Log.w("MyData/outJSON", "解析に失敗しました")
//            return null
//        }
    }
    fun showJSON(): String{
        return outJSON() ?: return "??"
    }
    fun keys(): MutableSet<String> {
        if("Object" == type[current]){
            return structure[current]?.keys ?: mutableSetOf()
        }
        return mutableSetOf()
    }
//  　fun values(): Array<String> {
//        var list:Array<MyData> = arrayOf()
//        if("Object" == type[current]){
//            for(value in structure[current]?.values ?: return arrayOf()){
//                list += value
//            }
//        }
//        return list
//    }
//    fun items(): MutableMap<String,String> {
//        val list:MutableSet<String> = mutableSetOf()
//        if("Object" == type[current]){
//            for(key in structure[current]?.keys ?: return mutableSetOf()){
//                list.add(key)
//            }
//        }
//        return list
//    }
    fun isData(key:String): Boolean{
        return key in (structure[current]?.keys ?: throw Exception("data does not exist"))
    }
    fun isKey(key:String): Boolean{
        return key in (structure[current]?.keys ?: throw Exception("data does not exist"))
    }
    fun isData(index:Int): Boolean{
        return index < (array[current]?.size ?: throw Exception("data does not exist"))
    }
    private fun getId(key:String): Int {
        return if (isData(key)){
            structure[current]!![key] ?: throw Exception("data does not exist")
        }else{
            val id: Int = newId()
            type[id] = "Null"
            structure[current]!![key] = id
            id
        }
    }
    private fun getId(index:Int): Int {
        if (isData(index)){
            return array[current]!![index]
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
        hierarchy = mutableListOf()
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
//        println("~~~~~~~~~~")
//        println("type:      $type")
//        println("structure: $structure")
//        println("value:     $value")
//        println("root:      $root")
//        println("current:   $current")
        val id:Int
        if(key in (structure[current]?.keys ?: throw Exception("current does not exist"))){
            id = structure[current]?.get(key) ?: throw Exception("data does not exist")
            if (type[id] != "Object" && type[id] != "Array") {
                type[id] = "Object"
                structure[id] = mutableMapOf()
                array.remove(id)
                value.remove(id)
            }
        }else {
            id = newId()
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
            structure.remove(current)
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
    val size:Int get() {
        if(type[current] == "Array")
            return array[current]?.size ?: throw Exception("Null in non-nullable type")
        else
            throw Exception("Type is not an array")
    }
    fun delete(key:String){
        if(type[current] == "Object") {
            if (isData(key)) {
                val id = getId(key)
                structure[current]?.remove(key) ?: return
                type.remove(id)
                structure.remove(id)
                array.remove(id)
                value.remove(id)
                delId(id)
            }
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
                return value[id] ?: throw Exception("data does not exist")
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
        if(v == null){
            type[id] = "Null"
            value.remove(id)
        }else{
            type[id] = "String"
            value[id] = v
        }
    }
    fun setString(index:Int, v: String?){
        val id = getId(index)
        if(v == null){
            type[id] = "Null"
            value.remove(id)
        }else{
            type[id] = "String"
            value[id] = v
        }
    }
    fun push(v: String?){
        val id = newId()
        if(v == null){
            type[id] = "Null"
            value.remove(id)
        }else{
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
        if(v == null){
            type[id] = "Null"
            value.remove(id)
        }else{
            type[id] = "Value"
            value[id] = v.toString()
        }
    }
    fun setInt(index:Int, v: Int?){
        val id = getId(index)
        if(v == null){
            type[id] = "Null"
            value.remove(id)
        }else{
            type[id] = "Value"
            value[id] = v.toString()
        }
    }
    fun push(v: Int?){
        val id = newId()
        if(v == null){
            type[id] = "Null"
            value.remove(id)
        }else{
            type[id] = "Value"
            value[id] = v.toString()
        }
        array[current]?.add(id)
    }
    fun getBoolean(key:String): Boolean{
        val id = getId(key)
        when(type[id]){
            "Null" -> {
                throw Exception("Null in non-nullable type")
            }
            "Object" -> {
                throw Exception("is in object form")
            }
            "String" -> {
                return value[id]?.toBoolean() ?: throw Exception("Null in non-nullable type")
            }
            "Value" -> {
                return value[id]?.toBoolean() ?: throw Exception("Null in non-nullable type")
            }
            else -> {
                throw Exception("There is no process corresponding to the type.")
            }
        }
    }
    fun getBoolean(index:Int): Boolean{
        val id = getId(index)
        when(type[id]){
            "Null" -> {
                throw Exception("Null in non-nullable type")
            }
            "Object" -> {
                throw Exception("is in object form")
            }
            "String" -> {
                return value[id]?.toBoolean() ?: throw Exception("Null in non-nullable type")
            }
            "Value" -> {
                return value[id]?.toBoolean() ?: throw Exception("Null in non-nullable type")
            }
            else -> {
                throw Exception("There is no process corresponding to the type.")
            }
        }
    }
    fun getBooleanOrNull(key:String): Boolean? {
        val id = getId(key)
        when(type[id]){
            "Null" -> {
                throw Exception("Null in non-nullable type")
            }
            "Object" -> {
                throw Exception("is in object form")
            }
            "String" -> {
                return value[id]?.toBoolean() ?: throw Exception("Null in non-nullable type")
            }
            "Value" -> {
                return value[id]?.toBoolean() ?: throw Exception("Null in non-nullable type")
            }
            else -> {
                throw Exception("There is no process corresponding to the type.")
            }
        }
    }
    fun getBooleanOrNull(index:Int): Boolean? {
        val id = getId(index)
        when(type[id]){
            "Null" -> {
                throw Exception("Null in non-nullable type")
            }
            "Object" -> {
                throw Exception("is in object form")
            }
            "String" -> {
                return value[id]?.toBoolean() ?: throw Exception("Null in non-nullable type")
            }
            "Value" -> {
                return value[id]?.toBoolean() ?: throw Exception("Null in non-nullable type")
            }
            else -> {
                throw Exception("There is no process corresponding to the type.")
            }
        }
    }
    fun setBoolean(key:String, v: Boolean?){
        val id = getId(key)
        if(v == null){
            type[id] = "Null"
            value.remove(id)
        }else{
            type[id] = "Value"
            value[id] = v.toString()
        }
    }
    fun setBoolean(index:Int, v: Boolean?){
        val id = getId(index)
        if(v == null){
            type[id] = "Null"
            value.remove(id)
        }else{
            type[id] = "Value"
            value[id] = v.toString()
        }
    }
    fun push(v: Boolean?){
        val id = newId()
        if(v == null){
            type[id] = "Null"
            value.remove(id)
        }else{
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
        if(v == null){
            type[id] = "Null"
            value.remove(id)
        }else{
            type[id] = "String"
            value[id] = v.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS"))
        }
    }
    fun setDateTime(index:Int, v: LocalDateTime?){
        val id = getId(index)
        if(v == null) {
            type[id] = "Null"
            value.remove(id)
        }else{
            type[id] = "String"
            value[id] = v.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS"))
        }
    }
    fun push(v: LocalDateTime?){
        if(type[current] == "Array"){
            val id = newId()
            if(v == null){
                type[id] = "Null"
                value.remove(id)
            }else{
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
    fun getStringMap():MutableMap<String,String>{
        var buff:MutableMap<String,String> = mutableMapOf()
        for(key in keys()) {
            buff[key] = getString(key)
        }
        return buff
    }
    fun setStringMap(map:Map<String,String>){
        for((k,v) in map) {
            setString(k, v)
        }
    }
    fun setMyData(key:String, myData:MyData){
    }
    fun copy(): MyData{
        return MyData(root,current,hierarchy)
    }
    fun close() {
        when (type[current]) {
            "Object" -> {
                for(key in keys()){
                    when (type[current]) {
                        "Object","Array" -> {
                            moveChain(key).close()
                        }
                        else -> {
                            val id = moveChain(key).getId(key)
                            type.remove(id)
                            structure.remove(id)
                            array.remove(id)
                            value.remove(id)
                            delId(id)
                        }
                    }
                }
            }
            "Array" -> {
                for (index in 0..size) {
                    when (type[index]) {
                        "Object", "Array" -> {
                            moveChain(index).close()
                        }
                        else -> {
                            val id = moveChain(index).getId(index)
                            type.remove(id)
                            structure.remove(id)
                            array.remove(id)
                            value.remove(id)
                            delId(id)
                        }
                    }
                }
            }
        }
        type.remove(current)
        structure.remove(current)
        array.remove(current)
        value.remove(current)
        delId(current)
    }
    fun closeRoot() {
        moveRootChain().close()
    }
}

