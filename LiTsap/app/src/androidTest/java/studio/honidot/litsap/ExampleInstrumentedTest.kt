package studio.honidot.litsap

import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import studio.honidot.litsap.data.History
import studio.honidot.litsap.data.Task
import studio.honidot.litsap.util.Logger
import studio.honidot.litsap.util.Util.countTaskNumberAndAddHeader
import studio.honidot.litsap.util.Util.sortAndCountTaskNumber

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("studio.honidot.litsap", appContext.packageName)
    }

    @Test
    fun checkTaskNum() {
        val tmpList = listOf(
            History(taskName = "Rachel"),
            History(taskName = "George"),
            History(taskName = "Will"),
            History(taskName = "Will"),
            History(taskName = "Jerry"),
            History(taskName = "Rachel")
        )
        val nameList = sortAndCountTaskNumber(tmpList)
        Logger.w("nameList: ${nameList.second}")
        assertEquals(4, nameList.first.size)
    }

    @Test
    fun checkHeader() {
        val tmpList = listOf(
            Task(todayDone = false),
            Task(todayDone = false),
            Task(todayDone = false),
            Task(todayDone = true),
            Task(todayDone = true),
            Task(todayDone = true)
        )
        val taskListWithHeader = countTaskNumberAndAddHeader(tmpList)
        Logger.w("taskListWithHeader: ${taskListWithHeader.second}")
        assertEquals(6, taskListWithHeader.first)
    }
}
