package com.dws.autofill

interface ObjectFetcher<T> {

    fun fetchObjects(count:Int):List<T>
}