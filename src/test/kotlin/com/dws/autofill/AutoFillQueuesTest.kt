package com.dws.autofill

import com.dws.autofill.impl.DefaultAutoFillQueue
import com.dws.autofill.impl.DefaultAutoFillQueues
import com.google.gson.Gson
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class AutoFillQueuesTest {
    @Test
    fun `test01`()
    {
        val queues= DefaultAutoFillQueues()

        queues.addQueue("bucket1", DefaultAutoFillQueue(1000,createIdentifierObjectFetcher("test-id-bucket-1")),500)
        queues.addQueue("bucket2", DefaultAutoFillQueue(1000,createIdentifierObjectFetcher("test-id-bucket-2")),500)

        Assertions.assertEquals(100,queues.take("bucket1",100).size)
        Assertions.assertEquals(100,queues.take("bucket2",100).size)

        val counterValue=(queues.take("bucket1") as String).toLong()
        val counterValuePlusOne=(queues.take("bucket1") as String).toLong()
        Assertions.assertEquals(counterValue+1,counterValuePlusOne)
    }

    @Test
    fun `test02`()
    {
        val queues= DefaultAutoFillQueues()

        queues.addQueue("bucket3", DefaultAutoFillQueue(1000,createIdentifierObjectFetcher("test-id-bucket-3")),500)
        queues.addQueue("bucket4", DefaultAutoFillQueue(1000,createIdentifierObjectFetcher("test-id-bucket-4")),500)

        Assertions.assertEquals(600,queues.take("bucket3",600).size)
        Assertions.assertEquals(600,queues.take("bucket4",600).size)
        Thread.sleep(10000)
        Assertions.assertEquals(600,queues.take("bucket3",600).size)
        Assertions.assertEquals(600,queues.take("bucket4",600).size)
        Thread.sleep(10000)
        Assertions.assertEquals(600,queues.take("bucket3",600).size)
        Assertions.assertEquals(600,queues.take("bucket4",600).size)

        val counterValue=(queues.take("bucket3") as String).toLong()
        val counterValuePlusOne=(queues.take("bucket3") as String).toLong()
        Assertions.assertEquals(counterValue+1,counterValuePlusOne)
    }

    private fun createIdentifierObjectFetcher(bucket:String): ObjectFetcher<String> {
        return object: ObjectFetcher<String> {
            val okHttpClient= OkHttpClient()
            val gson= Gson()
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
