package com.dsw.autofill

interface ObjectFetcher<T> {

    fun fetchObjects(count:Int):List<T>
}