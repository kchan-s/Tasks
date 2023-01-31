package app.sato.kchan.tasks.fanction

import kotlinx.coroutines.launch

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
            info["status"] = "setting"
            val my = this
            ConnectionWrapper.scope.launch {
                val request = info["request"] ?: ""
                ConnectionWrapper().executeServerConnection(request)
                if (0 < id) {
                    val info = situation[id] ?: throw Exception("✖✖✖✖✖")
                    info["response"] = ConnectionWrapper().postOutput()
                    info["status"] = "end"
                    info["result"] = "success"
                    if (callback.containsKey(id))
                        callback[id]!!(my)
                }
            }
        }
    }
    fun callback(func: (connect:Connect) -> Unit){
        if(0 < id) {
            callback[id] = func
        }
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
    fun isSuccess():Boolean {
        val info = situation[id] ?: throw Exception("✖✖✖✖✖")
        return info["result"] == "success"
    }
    fun getResponse(): String{
        val info = situation[id] ?: throw Exception("✖✖✖✖✖")
        return info["response"] ?: throw Exception("✖✖✖✖✖")
    }
    fun getResponseMyData():MyData {
        val info = situation[id] ?: throw Exception("✖✖✖✖✖")
        val response = info["response"] ?: throw Exception("✖✖✖✖✖")
        val myData = MyData()
        myData.inJSON(response)
        return myData
    }
}