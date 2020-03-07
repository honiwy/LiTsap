package studio.honidot.litsap.source.remote

import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Continuation
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import studio.honidot.litsap.LiTsapApplication
import studio.honidot.litsap.LiTsapApplication.Companion.instance
import studio.honidot.litsap.R
import studio.honidot.litsap.data.*
import studio.honidot.litsap.source.LiTsapDataSource
import studio.honidot.litsap.util.Logger
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


object LiTsapRemoteDataSource : LiTsapDataSource {

    private const val ONE_DAY_MILLI_SECOND = 86400 * 1000
    private const val PATH_GROUPS = "groups"
    private const val PATH_MEMBERS = "members"
    private const val PATH_USERS = "users"
    private const val PATH_TASKS = "tasks"
    private const val PATH_SHARES = "shares"
    private const val PATH_MODULES = "modules"
    private const val PATH_HISTORY = "history"
    private const val FIELD_GROUP_FULL = "groupFull"
    private const val FIELD_GROUP_CATEGORY_ID = "groupCategoryId"
    private const val FIELD_ONGOING_TASK = "ongoingTasks"
    private const val FIELD_HISTORY_TASK = "historyTasks"
    private const val FIELD_ICON_ID = "iconId"
    private const val FIELD_TASK_ID = "taskId"
    private const val FIELD_SHARE_ID = "shareId"
    private const val FIELD_TODAY_DONE = "todayDone"
    private const val FIELD_RECORD_DATE = "recordDate"
    private const val FIELD_ACCUMULATE_COUNT = "accumCount"
    private const val FIELD_EXPERIENCE = "experience"
    private const val FIELD_TODAY_DONE_COUNT = "todayDoneCount"
    private const val FIELD_MURMUR = "murmur"
    private const val FIELD_ACHIEVE_SECTION = "achieveSection"
    private const val FOLDER_UPLOAD = "uploads/"
    private const val GROUP_NUMBER_LIMIT = 6

    override suspend fun addMemberToGroup(member: Member): Result<Boolean> =
        suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance().collection(PATH_GROUPS).document(member.groupId)
                .collection(
                    PATH_MEMBERS
                ).document(member.userId).set(member)
                .addOnCompleteListener { addMember ->
                    if (addMember.isSuccessful) {
                        continuation.resume(Result.Success(true))
                    } else {
                        addMember.exception?.let {
                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                        }
                        continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
                    }
                }
        }

    override suspend fun createGroup(group: Group): Result<String> =
        suspendCoroutine { continuation ->
            val newGroupDocument =
                FirebaseFirestore.getInstance().collection(PATH_GROUPS).document()
            group.groupId = newGroupDocument.id
            newGroupDocument.set(group).addOnCompleteListener { addGroup ->
                if (addGroup.isSuccessful) {
                    continuation.resume(Result.Success(newGroupDocument.id))
                } else {
                    addGroup.exception?.let {
                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                    }
                    continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
                }
            }
        }

    override suspend fun checkGroupFull(groupId: String): Result<Boolean> =
        suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance().collection(PATH_GROUPS).document(groupId).collection(
                PATH_MEMBERS
            )
                .get()
                .addOnCompleteListener { findGroupMember ->
                    if (findGroupMember.isSuccessful) {
                        val groupMemberList = mutableListOf<Member>()
                        for (documentG in findGroupMember.result!!) {
                            groupMemberList.add(documentG.toObject(Member::class.java))
                        }
                        if (groupMemberList.size == GROUP_NUMBER_LIMIT) {
                            FirebaseFirestore.getInstance().collection(PATH_GROUPS)
                                .document(groupId)
                                .update(FIELD_GROUP_FULL, true)
                                .addOnCompleteListener { updateId ->
                                    if (updateId.isSuccessful) {
                                        continuation.resume(Result.Success(true))
                                    } else {
                                        updateId.exception?.let {
                                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                                            continuation.resume(Result.Error(it))
                                        }
                                        continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
                                    }
                                }
                        } else {
                            continuation.resume(Result.Success(true))
                        }
                    } else {
                        findGroupMember.exception?.let {
                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
                    }
                }
        }

    override suspend fun findGroup(taskCategoryId: Int): Result<List<String>> =
        suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance().collection(PATH_GROUPS)
                .whereEqualTo(FIELD_GROUP_CATEGORY_ID, taskCategoryId)
                .whereEqualTo(FIELD_GROUP_FULL, false)
                .get()
                .addOnCompleteListener { findGroup ->
                    if (findGroup.isSuccessful) {
                        val groupIdList = mutableListOf<String>()
                        for (documentG in findGroup.result!!) {
                            val groupFound = documentG.toObject(Group::class.java)
                            groupIdList.add(groupFound.groupId)
                        }
                        continuation.resume(Result.Success(groupIdList))
                    } else {
                        findGroup.exception?.let {
                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
                    }
                }
        }

    override suspend fun findUser(firebaseUserId: String): Result<User?> =
        suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance().collection(PATH_USERS).document(firebaseUserId)
                .get()
                .addOnCompleteListener { findUser ->
                    if (findUser.isSuccessful) {
                        findUser.result?.let { documentU ->
                            val user = documentU.toObject(User::class.java)
                            continuation.resume(Result.Success(user))
                        }
                    } else {
                        findUser.exception?.let {
                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
                    }
                }
        }

    override suspend fun createUser(user: User): Result<Boolean> =
        suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance().collection(PATH_USERS).document(user.userId).set(user)
                .addOnCompleteListener { addUser ->
                    if (addUser.isSuccessful) {
                        continuation.resume(Result.Success(true))
                    } else {
                        addUser.exception?.let {

                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                        }
                        continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
                    }
                }
        }

    override fun getUser(userId: String): LiveData<User> {
        val user = MutableLiveData<User>()
        FirebaseFirestore.getInstance().collection(PATH_USERS).document(userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    user.value = snapshot.toObject(User::class.java)!!

                } else {
                    Logger.d("Current data: null")
                }
            }

        return user
    }

    override suspend fun deleteUserOngoingTask(userId: String, taskId: String): Result<Boolean> =
        suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance().collection(PATH_USERS).document(userId)
                .update(FIELD_ONGOING_TASK, FieldValue.arrayRemove(taskId))
                .addOnCompleteListener { addId ->
                    if (addId.isSuccessful) {
                        continuation.resume(Result.Success(true))
                    } else {
                        addId.exception?.let {

                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                        }
                        continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
                    }
                }
        }

    override suspend fun updateUserIcon(userId: String, iconId: Int): Result<Boolean> =
        suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance().collection(PATH_USERS).document(userId)
                .update(FIELD_ICON_ID, iconId)
                .addOnCompleteListener { updateId ->
                    if (updateId.isSuccessful) {
                        continuation.resume(Result.Success(true))
                    } else {
                        updateId.exception?.let {

                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                        }
                        continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
                    }
                }
        }

    override suspend fun deleteTask(taskId: String): Result<Boolean> =
        suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance().collection(PATH_TASKS).document(taskId)
                .delete()
                .addOnCompleteListener { addId ->
                    if (addId.isSuccessful) {
                        continuation.resume(Result.Success(true))
                    } else {
                        addId.exception?.let {

                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                        }
                        continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
                    }
                }
        }

    override suspend fun getTasks(taskIdList: List<String>): Result<List<Task>> =
        suspendCoroutine { continuation ->
            val tasks = mutableListOf<Task>()
            FirebaseFirestore.getInstance().collection(PATH_TASKS)
                .whereIn(FIELD_TASK_ID, taskIdList)
                .orderBy(FIELD_TODAY_DONE)
                .get().addOnCompleteListener { findTask ->
                    if (findTask.isSuccessful) {
                        for (documentT in findTask.result!!) {
                            tasks.add(documentT.toObject(Task::class.java))
                        }
                        continuation.resume(Result.Success(tasks))
                    } else {
                        findTask.exception?.let {
                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
                    }
                }
        }

    override suspend fun getShares(shareIdList: List<String>): Result<List<Share>> =
        suspendCoroutine { continuation ->
            val shares = mutableListOf<Share>()
            FirebaseFirestore.getInstance().collection(PATH_SHARES)
                .whereIn(FIELD_TASK_ID, shareIdList)
                .get().addOnCompleteListener { findShare ->
                    if (findShare.isSuccessful) {
                        for (documentT in findShare.result!!) {
                            shares.add(documentT.toObject(Share::class.java))
                        }
                        continuation.resume(Result.Success(shares))
                    } else {
                        findShare.exception?.let {
                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
                    }
                }
        }

    override suspend fun getModules(taskId: String): Result<List<Module>> =
        suspendCoroutine { continuation ->
            val modules = mutableListOf<Module>()
            FirebaseFirestore.getInstance().collection(PATH_TASKS).document(taskId)
                .collection(PATH_MODULES)
                .get().addOnCompleteListener { findModule ->
                    if (findModule.isSuccessful) {
                        for (documentM in findModule.result!!) {
                            modules.add(documentM.toObject(Module::class.java))
                        }
                        continuation.resume(Result.Success(modules))
                    } else {
                        findModule.exception?.let {
                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
                    }
                }
        }

    override suspend fun getHistory(
        taskIdList: List<String>,
        passNday: Int
    ): Result<List<History>> =
        suspendCoroutine { continuation ->
            val timeMin = (Calendar.getInstance().timeInMillis - ONE_DAY_MILLI_SECOND * passNday)
            val listH = mutableListOf<History>()
            FirebaseFirestore.getInstance().collectionGroup(PATH_HISTORY)
                .whereIn(FIELD_TASK_ID, taskIdList).whereGreaterThan(FIELD_RECORD_DATE, timeMin)
                .get()
                .addOnCompleteListener { findHistory ->
                    if (findHistory.isSuccessful) {
                        for (documentH in findHistory.result!!) {
                            listH.add(documentH.toObject(History::class.java))
                        }
                        continuation.resume(Result.Success(listH))
                    } else {
                        findHistory.exception?.let {
                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
                    }
                }
        }

    override suspend fun getMemberMurmurs(groupId: String): Result<List<Member>> =
        suspendCoroutine { continuation ->
            val members = mutableListOf<Member>()
            FirebaseFirestore.getInstance().collection(PATH_GROUPS).document(groupId)
                .collection(PATH_MEMBERS)
                .get().addOnCompleteListener { findMember ->
                    if (findMember.isSuccessful) {
                        for (documentM in findMember.result!!) {
                            members.add(documentM.toObject(Member::class.java))
                        }
                        continuation.resume(Result.Success(members))
                    } else {
                        findMember.exception?.let {
                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
                    }
                }
        }

    override suspend fun getHistoryOnThatDay(
        taskIdList: List<String>,
        dateString: String
    ): Result<List<History>> =
        suspendCoroutine { continuation ->
            val startDate =
                SimpleDateFormat(instance.getString(R.string.diary_record_date)).parse(dateString)
                    .time
            val listH = mutableListOf<History>()
            FirebaseFirestore.getInstance().collectionGroup(PATH_HISTORY)
                .whereIn(FIELD_TASK_ID, taskIdList)
                .whereGreaterThan(FIELD_RECORD_DATE, startDate)
                .whereLessThan(FIELD_RECORD_DATE, startDate + ONE_DAY_MILLI_SECOND).get()
                .addOnCompleteListener { findHistory ->
                    if (findHistory.isSuccessful) {
                        for (documentH in findHistory.result!!) {
                            listH.add(documentH.toObject(History::class.java))
                        }
                        continuation.resume(Result.Success(listH))
                    } else {
                        findHistory.exception?.let {
                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
                    }
                }
        }

    override suspend fun createTask(task: Task): Result<String> =
        suspendCoroutine { continuation ->
            val newTaskDocument = FirebaseFirestore.getInstance().collection(PATH_TASKS).document()
            task.taskId = newTaskDocument.id
            newTaskDocument.set(task).addOnCompleteListener { addTask ->
                if (addTask.isSuccessful) {
                    Toast.makeText(
                        LiTsapApplication.appContext,
                        instance.getString(R.string.create_task_success),
                        Toast.LENGTH_SHORT
                    ).show()
                    continuation.resume(Result.Success(newTaskDocument.id))
                } else {
                    addTask.exception?.let {
                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                    }
                    continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
                }
            }
        }

    override suspend fun createTaskModules(taskId: String, module: Module): Result<Boolean> =
        suspendCoroutine { continuation ->
            val newModuleDocument =
                FirebaseFirestore.getInstance().collection(PATH_TASKS).document(taskId)
                    .collection(PATH_MODULES).document()
            module.moduleId = newModuleDocument.id
            newModuleDocument.set(module).addOnCompleteListener { addModule ->
                if (addModule.isSuccessful) {
                    continuation.resume(Result.Success(true))
                } else {
                    addModule.exception?.let {
                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                    }
                    continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
                }
            }
        }

    override suspend fun addUserOngoingList(userId: String, taskId: String): Result<Boolean> =
        suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance().collection(PATH_USERS).document(userId)
                .update(FIELD_ONGOING_TASK, FieldValue.arrayUnion(taskId))
                .addOnCompleteListener { addId ->
                    if (addId.isSuccessful) {
                        continuation.resume(Result.Success(true))
                    } else {
                        addId.exception?.let {

                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                        }
                        continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
                    }
                }
        }

    override suspend fun addUserHistoryList(userId: String, taskId: String): Result<Boolean> =
        suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance().collection(PATH_USERS).document(userId)
                .update(FIELD_HISTORY_TASK, FieldValue.arrayUnion(taskId))
                .addOnCompleteListener { addId ->
                    if (addId.isSuccessful) {
                        continuation.resume(Result.Success(true))
                    } else {
                        addId.exception?.let {

                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                        }
                        continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
                    }
                }
        }

    override suspend fun updateTaskStatus(
        taskId: String,
        accumulationPoints: Long
    ): Result<Boolean> =
        suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance().collection(PATH_TASKS).document(taskId)
                .update(
                    mapOf(
                        FIELD_TODAY_DONE to true,
                        FIELD_ACCUMULATE_COUNT to FieldValue.increment(accumulationPoints)
                    )
                )
                .addOnCompleteListener { addId ->
                    if (addId.isSuccessful) {
                        continuation.resume(Result.Success(true))
                    } else {
                        addId.exception?.let {

                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                        }
                        continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
                    }
                }
        }

    //If user did the task that is done today will be something wrong with following code
    override suspend fun updateUserStatus(workout: Workout): Result<Boolean> =
        suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance().collection(PATH_USERS).document(workout.userId)
                .update(
                    mapOf(
                        FIELD_EXPERIENCE to FieldValue.increment(workout.achieveSectionCount * workout.achieveSectionCount.toLong()),
                        FIELD_TODAY_DONE_COUNT to FieldValue.increment(1L)
                    )
                )
                .addOnCompleteListener { addId ->
                    if (addId.isSuccessful) {
                        continuation.resume(Result.Success(true))
                    } else {
                        addId.exception?.let {

                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                        }
                        continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
                    }
                }
        }

    override suspend fun updateMurmur(member: Member): Result<Boolean> =
        suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance().collection(PATH_GROUPS).document(member.groupId)
                .collection(
                    PATH_MEMBERS
                ).document(member.userId)
                .update(FIELD_MURMUR, member.murmur)
                .addOnCompleteListener { addId ->
                    if (addId.isSuccessful) {
                        continuation.resume(Result.Success(true))
                    } else {
                        addId.exception?.let {

                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                        }
                        continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
                    }
                }
        }

    override suspend fun updateTaskModule(workout: Workout): Result<Boolean> =
        suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance().collection(PATH_TASKS).document(workout.taskId)
                .collection(
                    PATH_MODULES
                ).document(workout.moduleId)
                .update(
                    FIELD_ACHIEVE_SECTION,
                    FieldValue.increment(workout.achieveSectionCount.toLong())
                )
                .addOnCompleteListener { addId ->
                    if (addId.isSuccessful) {
                        continuation.resume(Result.Success(true))
                    } else {
                        addId.exception?.let {

                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                        }
                        continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
                    }
                }
        }


    override suspend fun createTaskHistory(history: History): Result<Boolean> =
        suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance().collection(PATH_TASKS).document(history.taskId)
                .collection(
                    PATH_HISTORY
                ).document().set(history)
                .addOnCompleteListener { addId ->
                    if (addId.isSuccessful) {
                        continuation.resume(Result.Success(true))
                    } else {
                        addId.exception?.let {

                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                        }
                        continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
                    }
                }
        }

    override suspend fun uploadImage(imageUri: Uri): Result<Uri> =
        suspendCoroutine { continuation ->
            val ref = FirebaseStorage.getInstance()
                .reference.child(FOLDER_UPLOAD + UUID.randomUUID().toString())
            ref.putFile(imageUri)
                .continueWithTask(Continuation<UploadTask.TaskSnapshot, com.google.android.gms.tasks.Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                        }
                        continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
                    }
                    return@Continuation ref.downloadUrl
                }).addOnCompleteListener { task ->
                    continuation.resume(Result.Success(task.result!!))
                }.addOnFailureListener {
                    Logger.w("Upload fail!")
                }
        }


}