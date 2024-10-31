# Auto Replenishing Queue
This library is aimed at providing a framework to create and maintain queue of objects which can replenish itself in the background based on a specific threshold that we can mention.

### Example 1 - A single AutoFillQueue
Here we are creating a number generator using the function createSimpleObjectFetcher(). The object that is returned is fed to the Autofill queue and you can then use the take() to fetch values from the queue.

```kotlin

val autoFillQueue=DefaultAutoFillQueue(1000,createSimpleObjectFetcher())
Assertions.assertEquals(1000,autoFillQueue.take(1000).size)
...
...
...

private fun createSimpleObjectFetcher(): ObjectFetcher<String> {
    return object: ObjectFetcher<String> {
        val counter=AtomicLong(0)
        override fun fetchObjects(count:Int):List<String>{
            val values= mutableListOf<String>()
            for(i in 1..count)
            {
                values.add(counter.incrementAndGet().toString())
            }
            return values
        }
    }
}



```

### Example 2 - Now using multiple queues
Here we are creating a number generator using the function createSimpleObjectFetcher(). The object that is returned is fed to the Autofill queue and you can then use the take() to fetch values from the queue.

```kotlin

val queues=DefaultAutoFillQueues()

queues.addQueue("bucket_01",DefaultAutoFillQueue(1000,createSimpleObjectFetcher()),500)
queues.addQueue("bucket_02",DefaultAutoFillQueue(1000,createSimpleObjectFetcher()),500)

Assertions.assertEquals(600,queues.take("bucket_01",600).size)
Assertions.assertEquals(600,queues.take("bucket_02",600).size)


```