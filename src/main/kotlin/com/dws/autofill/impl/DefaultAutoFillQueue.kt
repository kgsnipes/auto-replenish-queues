package com.dws.autofill.impl

import com.dws.autofill.AutoFillQueue
import com.dws.autofill.ObjectFetcher
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Semaphore
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class DefaultAutoFillQueue(private val batchSize:Int,private var fetcher: ObjectFetcher<*>): AutoFillQueue<Any> {

    private val concurrentQueue= ConcurrentLinkedQueue<Any>()
    private val isReplenishing= AtomicBoolean(false)
    private val lock= Semaphore(1)
    private val totalCount= AtomicInteger(0)

    override fun take(): Any? {
        val values=take(1)
        return if(values.isNotEmpty()){
            values.first()
        }
        else
        {
            null
        }
    }

    override fun take(count: Int): List<Any> {
        return _take(count)
    }

    private fun _take(count:Int):List<Any>
    {
        return if(concurrentQueue.isNotEmpty() && count<totalCount.get()) {
            val mutableList= mutableListOf<Any>()
            for(i in 1..count)
            {
                totalCount.decrementAndGet()
                mutableList.add(concurrentQueue.poll())
            }
            mutableList
        }
        else {
            if(count<batchSize)
            {
                replenish()
                totalCount.set(totalCount.get()-batchSize)
            }
            else
            {
                replenish(count)
                totalCount.set(totalCount.get()-count)
            }

            val mutableList= mutableListOf<Any>()
            for(i in 1..count)
            {
                if(concurrentQueue.isNotEmpty()) {
                    totalCount.decrementAndGet()
                    mutableList.add(concurrentQueue.poll())
                }
            }
            mutableList
        }
    }

    override fun replenish() {
        replenish(batchSize)
    }

    override fun replenish(count: Int) {
        try {
            lock.acquire(1)
            isReplenishing.set(true)

            while(totalCount.get()<batchSize)
            {
                val objects=fetcher.fetchObjects(count)
                if(objects.isNotEmpty())
                {
                    objects.forEach {
                        concurrentQueue.offer(it)
                        totalCount.incrementAndGet()
                    }

                }
            }
        }
        finally {
            lock.release(1)
            isReplenishing.set(false)
        }
    }

    override fun isReplenishing(): Boolean {
        return isReplenishing.get()
    }

    override fun size(): Int {
       return totalCount.get()
    }

    override fun getBatchSize():Int {
        return batchSize
    }

    override fun setObjectFetcher(fetcher: ObjectFetcher<Any>) {
        this.fetcher=fetcher
    }
}