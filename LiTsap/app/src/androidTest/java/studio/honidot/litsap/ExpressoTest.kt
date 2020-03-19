package studio.honidot.litsap

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class ExpressoTest {

    @get:Rule
    var activityRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun createTaskFlow() {
        Thread.sleep(3000)
        onView(withId(R.id.button_facebook_login)).perform(click())
        Thread.sleep(3000)
        onView(withId(R.id.fab)).perform(click())
        onView(withId(R.id.edit_title)).perform(replaceText("EspressoTest"))
        Thread.sleep(1500)
        onView(withId(R.id.edit_module)).perform(replaceText("Hello"))
        onView(withId(R.id.button_module_add)).perform(click())
        Thread.sleep(1500)
        onView(withId(R.id.edit_module)).perform(replaceText("Hola"))
        onView(withId(R.id.button_module_add)).perform(click())
        Thread.sleep(1500)
        onView(withId(R.id.edit_task_amount_editor)).perform(replaceText("5"))
        Thread.sleep(1500)
        onView(withId(R.id.button_create)).perform(click())
        Thread.sleep(5000)
    }

//    @Test
//    fun chooseTaskToWorkoutFlow(){
//        Thread.sleep(3000)
//        onView(withId(R.id.button_facebook_login)).perform(click())
//        Thread.sleep(3000)
//        onView(withId(R.id.recycler_task)).perform(RecyclerViewActions.actionOnItemAtPosition<TaskAdapter.AssignmentViewHolder>(2,
//            click()))
//        Thread.sleep(2000)
//        onView(withId(R.id.button_power3)).perform(click())
//        Thread.sleep(1500)
//        onView(withId(R.id.button_create)).perform(click())
//        Thread.sleep(5000)
//
//    }
}