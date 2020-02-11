package studio.honidot.litsap.data

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize



@Parcelize
@IgnoreExtraProperties
data class Task(
    val taskId: String = "",
    val groupId: String = "",
    val userId: String = "",
    val taskName: String = "",
    val goalCount: Int = -1,
    val accumCount: Int = -1,
    val taskCategoryId: Int = -1,
    val dueDate: Long = 0,
    @Exclude var modules: List<Module> = listOf(),
    @Exclude var history: List<History> = listOf(),
    val taskDone: Boolean = false, //whether a history task or not
    val todayDone: Boolean = false //whether finish the task today
) : Parcelable



