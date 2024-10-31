package com.dsw.autofill

import com.dsw.autofill.impl.DefaultAutoFillQueue
import com.google.gson.Gson
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class AutoFillQueueTest {

    @Test
    fun `test01`()
    {
        val autoFillQueue=DefaultAutoFillQueue(1000,createIdentifierObjectFetcher("test-id-bucket"))
        Assertions.assertEquals(1000,autoFillQueue.take(1000).size)
        Assertions.assertEquals(1000,autoFillQueue.take(1000).size)
        Assertions.assertEquals(100,autoFillQueue.take(100).size)
        val counterValue=(autoFillQueue.take() as String).toLong()
        val counterValuePlusOne=(autoFillQueue.take() as String).toLong()
        Assertions.assertEquals(counterValue+1,counterValuePlusOne)
    }

    private fun createIdentifierObjectFetcher(bucket:String): ObjectFetcher<String> {
        return object: ObjectFetcher<String> {
            val okHttpClient=OkHttpClient()
            val gson=Gson()
            override fun fetchObjects(count:Int):List<String>{
                val list= mutableListOf<String>()
                val url="http://localhost:9000/id-service/id/$bucket"
                val httpBuilder = url.toHttpUrl().newBuilder().addQueryParameter("count","$count").addQueryParameter("format","none")
                val request = Request.Builder().get()
                    .url(httpBuilder.build())
                    .build()
                okHttpClient.newCall(request).execute().use { response ->
                    if (response.isSuccessful)
                    {
                        val serviceResponse=gson.fromJson(response.body!!.string(), IDServiceResponse::class.java)
                        if(serviceResponse!=null && serviceResponse.id.isNotEmpty())
                        {
                            list.addAll(serviceResponse.id)
                        }
                    }
                }
                return list
            }
        }
    }

    data class IDServiceResponse(var id:List<String>)
}