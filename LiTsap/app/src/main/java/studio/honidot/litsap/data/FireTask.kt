package studio.honidot.litsap.data

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@Parcelize
@IgnoreExtraProperties
data class FireTask(
    val taskId: String = "",
    val title: String = "",
    val categoryId: Int = -1,
    @Exclude var modules: List<Module> = listOf(),
    val accumulatedCount: Int = -1,
    val totalCount: Int = -1,
    val dueDate: String = "",
    val chatStatus: Boolean = false, //whether somebody is in the chat room
    val taskStatus: Boolean = false //whether finish the task today
) : Parcelable