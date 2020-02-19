package studio.honidot.litsap

import android.graphics.Typeface.BOLD
import android.text.Spannable
import android.text.SpannableString
import android.text.format.DateFormat
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import studio.honidot.litsap.LiTsapApplication.Companion.instance
import studio.honidot.litsap.data.Module
import studio.honidot.litsap.data.TaskItem
import studio.honidot.litsap.data.User
import studio.honidot.litsap.data.Workout
import studio.honidot.litsap.task.TaskAdapter
import studio.honidot.litsap.task.create.ModuleCreateAdapter
import studio.honidot.litsap.task.detail.DetailModuleAdapter
import studio.honidot.litsap.task.finish.FootprintAdapter
import studio.honidot.litsap.task.workout.RecordAdapter
import studio.honidot.litsap.util.CurrentFragmentType
import studio.honidot.litsap.util.Logger
import studio.honidot.litsap.util.Util.getColor
import java.util.*

//Task List
@BindingAdapter("taskItems")
fun bindRecyclerViewWithTaskItems(recyclerView: RecyclerView, taskItems: List<TaskItem>?) {
    taskItems?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is TaskAdapter -> submitList(it)
            }
        }
    }
}


//Global
@BindingAdapter("taskCategory")
fun bindTaskCategories(imageView: ImageView, categoryId: Int) {
    val taskCategory= TaskCategory.values()[categoryId]
    imageView.background =
        when (taskCategory) {
            TaskCategory.EXERCISE -> instance.getDrawable(R.drawable.category_exercise)
            TaskCategory.FOOD -> instance.getDrawable(R.drawable.category_food)
            TaskCategory.STUDY -> instance.getDrawable(R.drawable.category_study)
            TaskCategory.WEALTH -> instance.getDrawable(R.drawable.category_wealth)
            TaskCategory.ART -> instance.getDrawable(R.drawable.category_brush)
            TaskCategory.NETWORKING -> instance.getDrawable(R.drawable.category_networking)
            else -> instance.getDrawable(R.drawable.category_other)
        }
}

@BindingAdapter("toolbarVisibility")
fun bindToolbarVisibility(view: View, fragment: CurrentFragmentType) {
   view.visibility =
        when (fragment) {
            CurrentFragmentType.TASK  -> View.VISIBLE
            CurrentFragmentType.POST -> View.VISIBLE
            else -> View.GONE
        }
}

@BindingAdapter("bottomNavVisibility")
fun bindBottomNavVisibility(view: View, fragment: CurrentFragmentType) {
    view.visibility =
        when (fragment) {
            CurrentFragmentType.TASK  -> View.VISIBLE
            CurrentFragmentType.POST -> View.VISIBLE
            CurrentFragmentType.PROFILE -> View.VISIBLE
            else -> View.GONE
        }
}

//@BindingAdapter("boldPartialText", "startIndex", "endIndex","color")
//fun bindTextSpan(textView: TextView, text: String?, start: Int, end: Int, color: String) {
//    text?.let {
//        val spannable = SpannableString(text)
////        ForegroundColorSpan(getColor(R.color.white)),
//        spannable.setSpan(
//            ForegroundColorSpan(Color.parseColor(color)),
//            start,
//            end,
//            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//        )
//        spannable.setSpan(
//            StyleSpan(BOLD),
//            start, end,
//            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//        )
//        textView.text = spannable
//    }
//}

//Task Create
@BindingAdapter("tags")
fun bindRecyclerViewWithTags(recyclerView: RecyclerView, tags: List<String>?) {
    tags?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is ModuleCreateAdapter -> submitList(it)
            }
        }
    }
}

//Task Detail
@BindingAdapter("modules")
fun bindRecyclerViewWithModules(recyclerView: RecyclerView, modules: List<Module>?) {
    modules?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is DetailModuleAdapter -> submitList(it)
            }
        }
    }
}

@BindingAdapter("layoutMarginStart")
fun setLayoutMarginStart(view: View, dimen: Float) {
    val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
    layoutParams.marginStart = dimen.toInt()
    view.layoutParams = layoutParams
}

@BindingAdapter("time")
fun bindTime(textView: TextView, time: Int?) {
    time?.let { textView.text = instance.getString(R.string.task_time, it * 20) }
}

//Task CountDown
@BindingAdapter("countDownTime")
fun bindCountDownTime(textView: TextView, time: Int) {
    val min = time / 60
    val sec = time - min * 60
    textView.text = String.format("%02d:%02d", min, sec)
}


@BindingAdapter("timerStampConverter")
fun bindTimeStamp(textView: TextView, timeStamp: com.google.firebase.Timestamp) {
    textView.text = DateFormat.format("截止時間: yyyy 年 MM 月 dd 日",timeStamp.toDate()).toString()
}

@BindingAdapter("timerLongConverter")
fun bindTimeLong(textView: TextView, timeLong: Long) {
    textView.text = DateFormat.format("截止時間: yyyy 年 MM 月 dd 日",Date(timeLong)).toString()
}

@BindingAdapter("messages")
fun bindRecyclerViewWithMessages(recyclerView: RecyclerView, tags: List<String>?) {
    tags?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is RecordAdapter -> submitList(it)
            }
        }
    }
}

//Task Finish
@BindingAdapter("receiveExperience")
fun bindReceiveExperience(textView: TextView, achieveSection: Int) {
    val xpAcquired = achieveSection * achieveSection
    textView.text = instance.getString(R.string.profile_experience, xpAcquired)
}

@BindingAdapter("footprints")
fun bindRecyclerViewWithFootprints(recyclerView: RecyclerView, workoutResult: Workout?) {
    workoutResult?.let {
        Logger.i("Hello bindRecyclerViewWithFootprints: ${workoutResult.recordInfo}")
        recyclerView.adapter?.apply {
            when (this) {
                is FootprintAdapter -> submitList(it.recordInfo)
            }
        }
    }
}

//Profile
@BindingAdapter("experience")
fun bindExperience(textView: TextView, xp: Long) {
    val next = xp+ 10- (xp%10)
    textView.text = instance.getString(R.string.profile_experience, xp)+ " / $next"
}

@BindingAdapter("level")
fun bindLevel(textView: TextView, level: Long) {
    textView.text = instance.getString(R.string.profile_level, level)
}

@BindingAdapter("levelInfo")
fun bindLevelInfo(textView: TextView, remaining: Int) {
    textView.text = instance.getString(R.string.profile_level_information, remaining)
}

@BindingAdapter("userProfile")
fun bindUserProfile(imageView: ImageView, userProfileId: Int) {
    val userProfile= UserProfile.values()[userProfileId]
    imageView.background =
        when (userProfile) {
            UserProfile.ACTOR -> instance.getDrawable(R.drawable.profile_actor)
            UserProfile.ARTIST -> instance.getDrawable(R.drawable.profile_artist)
            UserProfile.DETECTIVE -> instance.getDrawable(R.drawable.profile_detective)
            UserProfile.GIRL -> instance.getDrawable(R.drawable.profile_girl)
            UserProfile.MAAM -> instance.getDrawable(R.drawable.profile_maam)
            UserProfile.MAN -> instance.getDrawable(R.drawable.profile_man)
            UserProfile.PIRATE -> instance.getDrawable(R.drawable.profile_pirate)
            UserProfile.POLITICIAN -> instance.getDrawable(R.drawable.profile_politician)
            UserProfile.STUDENT -> instance.getDrawable(R.drawable.profile_student)
            UserProfile.USER -> instance.getDrawable(R.drawable.profile_user)
            UserProfile.WOMEN -> instance.getDrawable(R.drawable.profile_woman)
        }
}