package com.dws.autofill

interface AutoFillQueue<T> {

    fun take():T?
    fun take(count:Int):List<T>
    fun replenish()
    fun replenish(count:Int)
    fun isReplenishing():Boolean
    fun setObjectFetcher(fetcher: ObjectFetcher<T>)
    fun size():Int
    fun getBatchSize():Int
}