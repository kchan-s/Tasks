package app.sato.kchan.tasks.fanction


import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory


interface MyService {
    @POST("backbone")
    fun serverPost(@Body postData: RequestBody): Call<String>
}


class ConnectionWrapper{

    companion object {
        private const val TAG = "MainActivity"
        private const val BASE_URL = "http://kutse09.hopto.org:50080/"

        private var outPut: String = "before"

        val scope = CoroutineScope(Dispatchers.IO)

        private val mediaType : MediaType? = MediaType.parse("application/json; charset=utf-8")
        //private val mediaType: MediaType = "application/json".toMediaType()

        private val service = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(MyService::class.java)
    }



    private fun requestBodyConverter(input : String): RequestBody {
        return RequestBody.create(mediaType,input)
    }

    fun executeServerConnection(input : String){
        outPut = "before"
        Log.d(TAG, outPut)
        val requestBody = requestBodyConverter(input)
        val postAction = service.serverPost(requestBody).clone().execute()
        if(postAction.isSuccessful){
            outPut = postAction.body().toString()
        }else{
        }
    }

    fun postOutput() : String{
        return outPut
    }

}