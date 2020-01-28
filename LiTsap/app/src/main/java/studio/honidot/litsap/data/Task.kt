package studio.honidot.litsap.data

data class Task(
    val title:String,
    val category:String,
    val interval:Double,
    val accumulatedTime:Double,
    val status: Boolean
)