package com.dsw.autofill

interface AutoFillQueues<AutoFillQueue> {

    fun addQueue(key: String,queue:AutoFillQueue,replenishThreshold:Int)
    fun take(key:String,count:Int):List<*>
    fun take(key:String):Any?
    fun isQueueAvailable(key:String):Boolean
    fun getQueue(key:String): AutoFillQueue?

}