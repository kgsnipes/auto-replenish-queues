package com.dsw.autofill

interface AutoFillQueues<AutoFillQueue> {

    fun addQueue(key: String,queue:AutoFillQueue,replenishThreshold:Float)
    fun getObjects(key:String,count:Int):List<*>
    fun isQueueAvailable(key:String):Boolean
    fun getQueue(key:String): AutoFillQueue?

}