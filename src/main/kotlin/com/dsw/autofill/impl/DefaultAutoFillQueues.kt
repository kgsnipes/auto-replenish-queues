package com.dsw.autofill.impl

import com.dsw.autofill.AutoFillQueue
import com.dsw.autofill.AutoFillQueues
import com.dsw.autofill.ObjectFetcher
import java.lang.Exception
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.concurrent.thread

class DefaultAutoFillQueues:AutoFillQueues<AutoFillQueue<*>> {
    companion object
    {
        private val queueMap= ConcurrentHashMap<String,AutoFillQueue<*>>()
        private val replenishmentThresholdMap= ConcurrentHashMap<String,Float>()
        private val queueToCheckForReplenishment=ConcurrentLinkedQueue<String>()
        private var replenishThread:Thread?=null
    }

    init {

        initBackgroundThread()

    }

    fun initBackgroundThread()
    {
        if(replenishThread==null || !replenishThread!!.isAlive)
        {
            replenishThread=getBackgroundReplenishmentThread()
            replenishThread?.start()
        }
    }

    fun getBackgroundReplenishmentThread():Thread
    {
        return thread(start=false,isDaemon = true,contextClassLoader = null, name = "backgroundReplenishmentThread", priority = 1, block = {

            while (true)
            {
                try {

                }catch (e:Exception)
                {

                }
            }

        })
    }

    override fun addQueue(key: String, queue:AutoFillQueue<*>,replenishThreshold:Float) {
        queueMap[key]=queue
    }

    override fun getObjects(key: String, count: Int): List<*> {
        initBackgroundThread()
        return getQueue(key)!!.take(count)
    }

    override fun isQueueAvailable(key: String): Boolean {
       return queueMap.containsKey(key)
    }

    override fun getQueue(key: String): AutoFillQueue<*>? {
        return queueMap[key]
    }
}