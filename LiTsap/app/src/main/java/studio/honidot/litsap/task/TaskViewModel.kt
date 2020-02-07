package studio.honidot.litsap.task

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import studio.honidot.litsap.LiTsapApplication
import studio.honidot.litsap.LiTsapApplication.Companion.db
import studio.honidot.litsap.TaskCategory
import studio.honidot.litsap.data.*
import studio.honidot.litsap.source.LiTsapRepository
import java.util.ArrayList
import java.util.HashMap

class TaskViewModel(private val repository: LiTsapRepository) : ViewModel() {

    private val _taskItems = MutableLiveData<List<TaskItem>>()

    val taskItems: LiveData<List<TaskItem>>
        get() = _taskItems

    // Handle navigation to detail
    private val _navigateToDetail = MutableLiveData<FireTask>()

    val navigateToDetail: LiveData<FireTask>
        get() = _navigateToDetail

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    init {
        retrieveTasks()
    }

    fun navigateToDetail(task: FireTask) {
        _navigateToDetail.value = task
    }

    fun onDetailNavigated() {
        _navigateToDetail.value = null
    }



    private fun retrieveTasks(){
        coroutineScope.launch {
            val result = repository.getTasks()
            _taskItems.value =  when (result) {
                is Result.Success -> {
                    result.data
                }
                is Result.Fail -> {
                    null
                }
                is Result.Error -> {
                    null
                }
                else -> {
                    null
                }
            }
        }
    }

    private fun getMockTasks() {
        val res = mutableListOf<TaskItem>()
        res.add(TaskItem.Title("待執行"))
//        res.add(TaskItem.Assignment(TaskInfo(
//            Task(0L, "我要成為海賊王", TaskCategory.NETWORKING),
//            listOf(
//                Module("尋找夥伴",3),
//                Module("技能訓練",5),
//                Module("攻佔領地",20),
//                Module("其他項目",11)
//            ),39, 200, "",true,false)))
//        res.add(TaskItem.Assignment(TaskInfo(Task(1L, "韓文小高手", TaskCategory.STUDY),
//            listOf(
//                Module("聽力",1),
//                Module("口說",2),
//                Module("閱讀",0),
//                Module("寫作",0)
//            ),3, 15, "當第一個完成的吧",false,false)))
        res.add(TaskItem.Title("已完成"))
//        res.add(TaskItem.Assignment(TaskInfo(Task(2L, "旅遊基金儲存計畫", TaskCategory.WEALTH), listOf(
//            Module("超商工讀",1),
//            Module("寫文案",1),
//            Module("發傳單",2),
//            Module("即席翻譯",1),
//            Module("其他項目",0)
//        ), 5, 10, "",true,true)))
//        res.add(TaskItem.Assignment(TaskInfo(Task(3L, "舞蹈成發練習", TaskCategory.EXERCISE), listOf(
//            Module("基本動作",1),
//            Module("舞序熟練",3),
//            Module("音樂剪輯",2),
//            Module("情感揣摩",4),
//            Module("進場謝幕",8),
//            Module("其他項目",1)
//        ),19, 30, "",false,true)))
//        res.add(TaskItem.Assignment(TaskInfo(Task(4L, "成為哈佛生", TaskCategory.STUDY), listOf(
//            Module("國文",10),
//            Module("英文",3),
//            Module("數學",2),
//            Module("社會",4),
//            Module("自然",8),
//            Module("軟實力培養",1)
//        ),19, 30, "HAHA",false,true)))
//        res.add(TaskItem.Assignment(Task(5L, "Potluck 料理準備", TaskCategory.FOOD, 4, 30, "",false,true)))

        _taskItems.value = res
    }

}