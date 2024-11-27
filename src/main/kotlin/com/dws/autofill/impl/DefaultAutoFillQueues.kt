package com.dws.autofill.impl

import com.dws.autofill.AutoFillQueue
import com.dws.autofill.AutoFillQueues
import org.slf4j.LoggerFactory
import java.lang.Thread.sleep
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.thread
import kotlin.random.Random

class DefaultAutoFillQueues: AutoFillQueues<AutoFillQueue<*>> {
    companion object
    {
        private val queueMap= ConcurrentHashMap<String, AutoFillQueue<*>>()
        private val replenishmentThresholdMap= ConcurrentHashMap<String,Int>()
        private val queueToCheckForReplenishment=HashSet<String>()
        private var replenishThread:Thread?=null
        private val log=LoggerFactory.getLogger(DefaultAutoFillQueues::class.java)
    }

    init {

        initBackgroundThread()

    }

    private fun isThreadDead(replenishThread: Thread?): Boolean {
        return replenishThread ==null || !replenishThread.isAlive || replenishThread.isInterrupted
    }

    fun initBackgroundThread()
    {
        if(isThreadDead(replenishThread))
        {
            log.info("Starting the background thread")
            replenishThread =getBackgroundReplenishmentThread()
            replenishThread?.start()
        }
    }

    fun getBackgroundReplenishmentThread():Thread
    {
        return thread(start=false,isDaemon = true,contextClassLoader = null, name = "backgroundReplenishmentThread", priority = 1, block = {

            while (true)
            {
                try {
                    if(queueToCheckForReplenishment.isNotEmpty())
                    {
                        queueToCheckForReplenishment.toList().forEach { que->
                            if(queueMap.containsKey(que))
                            {
                                val queue= queueMap[que]
                                if(queue!=null)
                                {
                                    val threshold= replenishmentThresholdMap[que]?:queue.getBatchSize()
                                    if(queue.size()<threshold)
                                    {
                                        log.info("The threshold went down for $que , trying to replenish..")
                                        queue.replenish()
                                    }
                                }
                                queueToCheckForReplenishment.remove(que)
                            }

                        }
                    }
                    log.info("Background thread taking a nap!!")
                    sleep(Random(1000).nextLong(5000))
                }catch (e:Exception)
                {
                    log.error(e.message,e)
                }
            }

        })
    }

    override fun addQueue(key: String, queue: AutoFillQueue<*>, replenishThreshold:Int) {
        queueMap[key]=queue
        replenishmentThresholdMap[key]=replenishThreshold
    }

    override fun take(key: String, count: Int): List<*> {
        initBackgroundThread()
        queueToCheckForReplenishment.add(key)
        return getQueue(key)!!.take(count)
    }

    override fun take(key: String): Any? {
        initBackgroundThread()
        queueToCheckForReplenishment.add(key)
        return getQueue(key)!!.take()
    }

    override fun isQueueAvailable(key: String): Boolean {
       return queueMap.containsKey(key)
    }

    override fun getQueue(key: String): AutoFillQueue<*>? {
        return queueMap[key]
    }
}