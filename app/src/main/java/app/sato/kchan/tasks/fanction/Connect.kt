package app.sato.kchan.tasks.fanction

import kotlinx.coroutines.launch
import java.time.LocalDateTime

class Connect {
    private companion object {
        var situation: MutableMap<Int, MutableMap<String, String>> = mutableMapOf()
        var callback: MutableMap<Int,(connect:Connect) -> Unit> = mutableMapOf()
    }
    private var id: Int = 0
    init {
        do{
            id = (15..30).random()
        }while(id <= 0 || situation.containsKey(id))
        situation[id] = mutableMapOf("status" to "setting")
    }
    fun setRequest(request: String){
        val info = situation[id] ?: throw Exception("✖✖✖✖✖")
        info["request"] = request
    }
    fun send(){
        if(0 < id) {
            val info = situation[id] ?: throw Exception("✖✖✖✖✖")
            info["status"] = "connecting"
            val my = this
            ConnectionWrapper.scope.launch {
                val request = info["request"] ?: ""
                ConnectionWrapper().executeServerConnection(request)
                if (0 < id) {
                    val info = situation[id] ?: throw Exception("✖✖✖✖✖")
                    info["response"] = ConnectionWrapper().postOutput()
                    info["result"] = "success"
                    info["status"] = "end"
                    if (callback.containsKey(id))
                        callback[id]!!(my)
                }
            }
        }
    }

    fun callback(func: (connect:Connect) -> Unit){
        callback[id] = func
    }
    fun isEnd(wait:Int = 100):Boolean {
        val info = situation[id] ?: throw Exception("✖✖✖✖✖")
        if(info["status"] == "end"){
            return true
        }else{
            Thread.sleep(wait.toLong())
            return false
        }
    }
    fun waitEnd(timeOut:Int = 20):Boolean{
        val endTime = LocalDateTime.now().plusSeconds(timeOut.toLong())
        do{
            if(LocalDateTime.now() < endTime) return false
        } while (isEnd())
        return true
    }
    fun isSuccess():Boolean {
        val info = situation[id] ?: throw Exception("✖✖✖✖✖")
        return info["result"] == "success"
    }
    fun getResponse(): String{
        val info = situation[id] ?: throw Exception("✖✖✖✖✖")
        return info["response"] ?: throw Exception("✖✖✖✖✖" + info)
    }
    fun getResponseMyData():MyData {
        val info = situation[id] ?: throw Exception("✖✖✖✖✖")
        val response = info["response"] ?: throw Exception("✖✖✖✖✖")
        val myData = MyData()
        myData.inJSON(response)
        return myData
    }
    fun close() {
        situation.remove(id)
        callback.remove(id)
    }
}