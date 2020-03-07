package studio.honidot.litsap

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.Shape
import android.text.Spannable
import android.text.SpannableString
import android.text.format.DateFormat
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import studio.honidot.litsap.LiTsapApplication.Companion.instance
import studio.honidot.litsap.data.*
import studio.honidot.litsap.network.LoadApiStatus
import studio.honidot.litsap.diary.HistoryAdapter
import studio.honidot.litsap.profile.CompetitionAdapter
import studio.honidot.litsap.profile.MurmurAdapter
import studio.honidot.litsap.profile.face.FaceAdapter
import studio.honidot.litsap.share.post.PostCircleAdapter
import studio.honidot.litsap.share.post.PostGalleryAdapter
import studio.honidot.litsap.task.TaskAdapter
import studio.honidot.litsap.task.create.ModuleCreateAdapter
import studio.honidot.litsap.task.detail.DetailModuleAdapter
import studio.honidot.litsap.task.finish.FootprintAdapter
import studio.honidot.litsap.task.workout.RecordAdapter
import studio.honidot.litsap.util.CurrentFragmentType
import studio.honidot.litsap.util.Util.getColor
import java.util.*

//Task  Fragment
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
/**
 * According to [LoadApiStatus] to decide the visibility of [ProgressBar]
 */
@BindingAdapter("setupApiStatus")
fun bindApiStatus(view: ProgressBar, status: LoadApiStatus?) {
    when (status) {
        LoadApiStatus.LOADING -> view.visibility = View.VISIBLE
        null, LoadApiStatus.DONE, LoadApiStatus.ERROR -> view.visibility = View.GONE
    }
}

/**
 * According to [message] to decide the visibility of [ProgressBar]
 */
@BindingAdapter("setupApiErrorMessage")
fun bindApiErrorMessage(view: TextView, message: String?) {
    when (message) {
        null, "" -> {
            view.visibility = View.GONE
        }
        else -> {
            view.text = message
            view.visibility = View.VISIBLE
        }
    }
}


@BindingAdapter("taskCategory")
fun bindTaskCategories(imageView: ImageView, categoryId: Int) {
    val taskCategory = TaskCategory.values()[categoryId]
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
            CurrentFragmentType.TASK -> View.VISIBLE
            CurrentFragmentType.SHARE -> View.VISIBLE
            CurrentFragmentType.DIARY -> View.VISIBLE
            else -> View.GONE
        }
}

@BindingAdapter("bottomNavVisibility")
fun bindBottomNavVisibility(view: View, fragment: CurrentFragmentType) {
    view.visibility =
        when (fragment) {
            CurrentFragmentType.TASK -> View.VISIBLE
            CurrentFragmentType.SHARE -> View.VISIBLE
            CurrentFragmentType.DIARY -> View.VISIBLE
            CurrentFragmentType.PROFILE -> View.VISIBLE
            else -> View.GONE
        }
}


//Login
@BindingAdapter("bindUserNameFB")
fun bindUserNameFB(textView: TextView, user: User?) {
    val s = instance.getString(R.string.facebook)
    textView.text = if (user != null && user.loginVia == s) {
        instance.getString(R.string.login_with_name, user.userName)
    } else {
        s + instance.getString(R.string.via_login)
    }
}

@BindingAdapter("bindUserNameGoogle")
fun bindUserNameGoogle(textView: TextView, user: User?) {
    val s = instance.getString(R.string.google)
    textView.text = if (user != null && user.loginVia == s) {
        instance.getString(R.string.login_with_name, user.userName)
    } else {
        s + instance.getString(R.string.via_login)
    }
}


//Diary Fragment
@BindingAdapter("records")
fun bindRecyclerViewWithRecords(recyclerView: RecyclerView, records: List<History>?) {
    records?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is HistoryAdapter -> submitList(it)
            }
        }
    }
}

@BindingAdapter("app:customLayoutManager")
fun bindLayoutManagerForRecyclerView(
    recyclerView: RecyclerView,
    layoutManager: RecyclerView.LayoutManager?
) {
    layoutManager?.let {
        recyclerView.layoutManager = it
    }
}

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = it.toUri().buildUpon().build()
        GlideApp.with(imgView.context)
            .load(imgUri)
            .apply(
                RequestOptions().transform(CenterCrop(), RoundedCorners(15))
                    .placeholder(R.drawable.loggo)
                    .error(R.drawable.loggo)
            )
            .into(imgView)
    }
}

@BindingAdapter("minuteHourConverter")
fun bindMinuteHourLong(textView: TextView, timeLong: Long) {
    textView.text =
        DateFormat.format(instance.getString(R.string.diary_record_time), Date(timeLong)).toString()
}


//Post Fragment
@BindingAdapter("addDecoration")
fun bindDecoration(recyclerView: RecyclerView, decoration: RecyclerView.ItemDecoration?) {
    decoration?.let { recyclerView.addItemDecoration(it) }
}

@BindingAdapter("count")
fun bindRecyclerViewByCount(recyclerView: RecyclerView, count: Int?) {
    count?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is PostCircleAdapter -> {
                    submitCount(it)
                }
            }
        }
    }
}

@BindingAdapter("circleStatus")
fun bindDetailCircleStatus(imageView: ImageView, isSelected: Boolean = false) {
    imageView.background = ShapeDrawable(object : Shape() {
        override fun draw(canvas: Canvas, paint: Paint) {

            paint.color = getColor(R.color.white)
            paint.isAntiAlias = true

            when (isSelected) {
                true -> {
                    paint.style = Paint.Style.FILL
                }
                false -> {
                    paint.style = Paint.Style.STROKE
                    paint.strokeWidth = instance.resources
                        .getDimensionPixelSize(R.dimen.edge_post_circle).toFloat()
                }
            }

            canvas.drawCircle(this.width / 2, this.height / 2,
                instance.resources
                    .getDimensionPixelSize(R.dimen.radius_post_circle).toFloat(), paint)
        }
    })
}

@BindingAdapter("images")
fun bindRecyclerViewWithImages(recyclerView: RecyclerView, images: List<String>?) {
    images?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is PostGalleryAdapter -> {
                    submitImages(it)
                }
            }
        }
    }
}


//Create Dialog
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

@BindingAdapter("taskStringLength")
fun bindTaskStringLength(textView: TextView, taskString: String?) {
    taskString?.let { textView.text = instance.getString(R.string.create_task_hint, it.length) }
}

@BindingAdapter("moduleStringLength")
fun bindModuleStringLength(textView: TextView, moduleString: String?) {
    moduleString?.let { textView.text = instance.getString(R.string.create_module_hint, it.length) }
}


//Detail Fragment
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

@BindingAdapter("planAndAchieve")
fun bindPlanAndAchieve(textView: TextView, task: Task?) {
    task?.let {
        var textDisplay = instance.getString(
            R.string.task_plan,
            task.goalCount
        )
        val indexOfGoalCount = textDisplay.indexOf(task.goalCount.toString())
        val countOfFirstHalf = textDisplay.length

        val secondHalfString = instance.getString(R.string.task_achieve, task.accumCount)
        val indexOfAccumCount = secondHalfString.indexOf(task.accumCount.toString())
        textDisplay += secondHalfString

        val spannable = SpannableString(textDisplay)
        spannable.setSpan(
            ForegroundColorSpan(getColor(R.color.dark_red)),
            countOfFirstHalf + indexOfAccumCount,
            countOfFirstHalf + indexOfAccumCount + task.accumCount.toString().length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannable.setSpan(
            ForegroundColorSpan(getColor(R.color.dark_red)),
            indexOfGoalCount,
            indexOfGoalCount + task.goalCount.toString().length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textView.text = spannable
    }
}


//Workout Fragment
@BindingAdapter("countDownTime")
fun bindCountDownTime(textView: TextView, time: Int) {
    val min = time / 60
    val sec = time - min * 60
    textView.text = String.format(instance.getString(R.string.workout_count_down), min, sec)
}

@BindingAdapter("timerLongConverter")
fun bindTimeLong(textView: TextView, timeLong: Long) {
    textView.text =
        DateFormat.format(instance.getString(R.string.detail_end_date), Date(timeLong)).toString()
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


//Finish Fragment
@BindingAdapter("receiveExperience")
fun bindReceiveExperience(textView: TextView, achieveSection: Int) {
    val xpAcquired = achieveSection * achieveSection
    textView.text = instance.getString(R.string.profile_experience, xpAcquired)
}

@BindingAdapter("footprints")
fun bindRecyclerViewWithFootprints(recyclerView: RecyclerView, workoutResult: Workout?) {
    workoutResult?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is FootprintAdapter -> submitList(it.recordInfo)
            }
        }
    }
}


//Profile Fragment
@BindingAdapter("userProfile")
fun bindUserProfile(imageView: ImageView, userProfileId: Int) {
    val userProfile = UserProfile.values()[userProfileId]
    imageView.background =
        when (userProfile) {
            UserProfile.ACTOR -> instance.getDrawable(R.drawable.profile_actor)
            UserProfile.DETECTIVE -> instance.getDrawable(R.drawable.profile_detective)
            UserProfile.GIRL -> instance.getDrawable(R.drawable.profile_girl)
            UserProfile.MAAM -> instance.getDrawable(R.drawable.profile_maam)
            UserProfile.MAN -> instance.getDrawable(R.drawable.profile_man)
            UserProfile.PIRATE -> instance.getDrawable(R.drawable.profile_pirate)
            UserProfile.POLITICIAN -> instance.getDrawable(R.drawable.profile_politician)
            UserProfile.STUDENT -> instance.getDrawable(R.drawable.profile_student)
            UserProfile.USER -> instance.getDrawable(R.drawable.profile_user)
            UserProfile.WOMEN -> instance.getDrawable(R.drawable.profile_woman)
            UserProfile.CHEMIST -> instance.getDrawable(R.drawable.profile_chemist)
            UserProfile.FIGHTER -> instance.getDrawable(R.drawable.profile_fighter)
            UserProfile.SAILOR -> instance.getDrawable(R.drawable.profile_sailor)
            UserProfile.SOLDIER -> instance.getDrawable(R.drawable.profile_soldier)
            UserProfile.BEARDMAN -> instance.getDrawable(R.drawable.profile_beardman)
        }
}

@BindingAdapter("levelInfo")
fun bindLevelInfo(textView: TextView, user: User?) {
    user?.let {
        textView.text = when {
            user.ongoingTasks.isEmpty() -> instance.getString(R.string.profile_level_information_cry)
            else -> {
                val remaining = user.ongoingTasks.size - user.todayDoneCount
                if (remaining == 0) {
                    instance.getString(R.string.profile_level_information_done)
                } else {
                    instance.getString(R.string.profile_level_information, remaining)
                }
            }
        }
    }
}

@BindingAdapter("experience")
fun bindExperience(textView: TextView, xp: Long) {
    val next = xp + User.INTERVAL_CONSTANT - (xp % User.INTERVAL_CONSTANT)
    textView.text = instance.getString(R.string.profile_experience2, xp, next)
}

@BindingAdapter("level")
fun bindLevel(textView: TextView, level: Long) {
    textView.text = instance.getString(R.string.profile_level, level)
}

@BindingAdapter("murmurs")
fun bindRecyclerViewWithMurmurs(recyclerView: RecyclerView, murmurs: List<Member>?) {
    murmurs?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is MurmurAdapter -> submitList(it)
            }
        }
    }
}

@BindingAdapter("onGoingTasks")
fun bindRecyclerViewWithTaskTabs(recyclerView: RecyclerView, onGoingTasks: List<Task>?) {
    onGoingTasks?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is CompetitionAdapter -> submitList(it)
            }
        }
    }
}


//Face Dialog
@BindingAdapter("faces")
fun bindRecyclerViewWithFaces(recyclerView: RecyclerView, faces: List<Int>?) {
    faces?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is FaceAdapter -> submitList(it)
            }
        }
    }
}