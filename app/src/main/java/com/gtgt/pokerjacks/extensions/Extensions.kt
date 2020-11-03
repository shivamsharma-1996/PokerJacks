package com.gtgt.pokerjacks.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.hardware.biometrics.BiometricManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.*
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager.widget.ViewPager
import com.androidisland.vita.VitaOwner
import com.androidisland.vita.vita
import com.bumptech.glide.Glide
import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.get
import com.github.salomonbrys.kotson.obj
import com.github.salomonbrys.kotson.string
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.material.tabs.TabLayout
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.gtgt.pokerjacks.BuildConfig
import com.gtgt.pokerjacks.MyApplication
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.utils.OnOneClickListener
import com.gtgt.pokerjacks.utils.ProgressBarHandler
import com.gtgt.pokerjacks.utils.ViewModelWithArgumentsFactory
import com.gtgt.pokerjacks.base.BaseActivity
import com.gtgt.pokerjacks.base.BaseFragment
import com.gtgt.pokerjacks.base.BaseViewModel
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.android.synthetic.main.snack_view.view.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.net.ssl.SSLException
import javax.net.ssl.SSLHandshakeException
import kotlin.concurrent.thread
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

fun Context.showToast(message: String) {
    runOnMain { Toast.makeText(this, message, Toast.LENGTH_SHORT).show() }
}


fun Fragment.showToast(message: String) {
    context?.let {
        runOnMain { Toast.makeText(it, message, Toast.LENGTH_SHORT).show() }
    }
}

fun Activity.showSnack(message: String) {
    runOnMain {
        if (isRunning()) {
            var isClosed = false
            var isGame = false

            val rootView = findViewById<View>(android.R.id.content).rootView as ViewGroup
            val snackView = layoutInflater.inflate(R.layout.snack_view, null)
            rootView.findViewWithTag<View>("snack")?.let {
                it.startAnimation(AnimationUtils.loadAnimation(this, R.anim.top_exit))
                rootView.removeView(it)
            }

            snackView.message_tv.text = message
            rootView.addView(snackView)

            if (javaClass.simpleName.endsWith("GameActivity")) {
                isGame = true
                snackView.mainView.icon.visibility = VISIBLE
                snackView.mainView.closeSnack.visibility = VISIBLE

                snackView.mainView.closeSnack.onOneClick {
                    isClosed = true
                    if (!isClosed && isRunning() && snackView.isAttachedToWindow)
                        snackView.startAnimation(
                            AnimationUtils.loadAnimation(
                                this,
                                R.anim.top_exit
                            )
                        )
                    rootView.removeView(snackView)
                }

                snackView.mainView.setBackgroundColor(Color.parseColor("#000000"))
            } else {
                snackView.mainView.padding(top = getStatusBarHeight())
            }

            snackView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.top_enter))

            Handler().postDelayed({
                if (!isClosed && isRunning() && snackView.isAttachedToWindow)
                    snackView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.top_exit))
                rootView.removeView(snackView)
            }, (if (isGame) 3000L else 5000L))
        }
    }
}

fun Context.getStatusBarHeight(): Int {
    var result = 0
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        result = resources.getDimensionPixelSize(resourceId)
    }
    return result
}

fun Fragment.showSnack(message: String) {
    if (isActive()) {
        activity?.showSnack(message)
    }
}


fun ViewPager.autoScroll(imagesSize: Int, DELAY_MS: Long, PERIOD_MS: Long) {
    val handler = Handler()
    val update = Runnable {
        if (currentItem == imagesSize - 1) {
            this.setCurrentItem(0, false)
        } else {
            this.setCurrentItem(currentItem + 1, true)
        }
    }

    handler.postDelayed(update, 4000)

    addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {

        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {

        }

        override fun onPageSelected(position: Int) {
            handler.removeCallbacks(update)
            handler.postDelayed(update, 3000)
        }
    })

}

inline fun <reified T> mock(): T where T : Any {
    return gson.fromJson("{}")
}


inline fun <reified T> mock(no: Int): List<T> where T : Any {
    return (1..no).map { mock<T>() }
}

/*inline fun <reified T> getModel(key: String): T? where T : Any {
    val sp = MyApplication.sharedPreferences
    return sp.getString(key, null)?.let { gson.fromJson(it) }
}*/

fun <T> diffChecker(itemsSame: (T, T) -> Boolean): DiffUtil.ItemCallback<T> where T : Any {
    return object : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T) = itemsSame(oldItem, newItem)

        override fun areContentsTheSame(oldItem: T, newItem: T) =
            oldItem.toJson() == newItem.toJson()
    }
}


fun <T> diffCheckerForce(itemsSame: (T, T) -> Boolean): DiffUtil.ItemCallback<T> {
    return object : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T) = itemsSame(oldItem, newItem)

        override fun areContentsTheSame(oldItem: T, newItem: T) = false
    }
}

fun Float.convertTorounded(): String {
    val format = DecimalFormat("0.##")

    return format.format(this)
}

fun Double.convertTorounded(): String {
    val format = DecimalFormat("0.##")
    return format.format(this)
}

val gson = Gson()
val gsonPretty = GsonBuilder().setPrettyPrinting().create()

fun Any.toJson() = gson.toJson(this)
fun Any.toPrettyJson() = gsonPretty.toJson(this)

fun Any.toJsonTree() = gson.toJsonTree(this)

fun Bundle.toJson(): JSONObject {
    val json = json()
    val keys = keySet()
    for (key in keys) {
        try {
// json.put(key, bundle.get(key)); see edit below
            json.put(key, get(key))
        } catch (e: JSONException) {
//Handle exception here
        }
    }
    return json
}

fun Double.numberCalculation(): String {
    val suffix = arrayOf(' ', 'k', 'M', 'B', 'T', 'P', 'E')
    val value = floor(log10(this))
    val base = value.toInt() / 3
    if (value >= 3 && base < suffix.size) {
        return DecimalFormat("#0.0").format(this / 10.0.pow(base * 3)) + suffix[base]
    } else {
        return DecimalFormat("#,##0").format(this)
    }
}

fun TabLayout.addSpaceInTabItems(space: Int = 5) {
    for (i in 0 until this.tabCount) {
        val tab = (this.getChildAt(0) as ViewGroup).getChildAt(i)
        val p = tab.layoutParams as ViewGroup.MarginLayoutParams
        p.setMargins(space, 0, space, 0)
        tab.requestLayout()
    }
}

var timeDiffWithServer = 0L
fun calculateDiffrence(serverTime: Long) {
    timeDiffWithServer = System.currentTimeMillis() - serverTime
}

val dayMtsFormat = SimpleDateFormat("dd MMM | hh:mm a", Locale.US)
val timeMtsFormat = SimpleDateFormat("hh:mm a | ", Locale.US)
val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.US)
val dateTimeFormat = SimpleDateFormat("dd-MM-yyyy | hh:mm a", Locale.US)

val timeHMSAFormat = SimpleDateFormat("hh:mm a", Locale.US)

fun Activity.isOnline(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE)
    return if (connectivityManager is ConnectivityManager) {
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        networkInfo?.isConnected ?: false
    } else false
}

fun Activity?.isRunning(): Boolean = if (this == null) false else !(isDestroyed || isFinishing)

fun Any?.isActivityRunning(): Boolean = if (this == null) false else when {
    this is Activity -> isRunning()
    this is Fragment -> if (activity == null) false else activity!!.isRunning()
    else -> false
}

//var jsonParser = JsonParser()
fun String.toJsonTree(): JsonElement {
    return JsonParser.parseString(this)
}

inline fun <reified VM : BaseViewModel, T> T.viewModel(args: JsonElement? = null): Lazy<VM> where T : BaseActivity {
    return lazy {
        ViewModelProvider(this,
            ViewModelWithArgumentsFactory(args)
        ).get(VM::class.java)
            .apply {
                activity = this@viewModel
                context = this@viewModel
            }
    }
}

inline fun <reified VM : BaseViewModel, T> T.sharedViewModel(args: JsonElement? = null): Lazy<VM> where T : BaseFragment {
    return lazy {
        activity?.run {
            ViewModelProvider(this).get(VM::class.java)
                .apply {
                    activity = (this@run as BaseActivity)
                    context = this@sharedViewModel.context
                }

        } ?: throw Exception("Invalid Activity")
    }
}

inline fun <reified VM : BaseViewModel, T> T.viewModel(args: JsonElement? = null): Lazy<VM> where T : BaseFragment {
    return lazy {
        ViewModelProvider(this,
            ViewModelWithArgumentsFactory(args)
        ).get(VM::class.java).apply {
            activity = this@viewModel.activity as BaseActivity
            context = this@viewModel.context
        }
    }
}

inline fun <reified VM : BaseViewModel, T> T.store(): Lazy<VM> where T : BaseActivity {
    return lazy {
        vita.with(VitaOwner.Multiple(this)).getViewModel<VM>().apply { activity = this@store }
    }
}

inline fun <reified VM : BaseViewModel, T> T.store(): Lazy<VM> where T : BaseFragment {
    return lazy {
        vita.with(VitaOwner.Multiple(this.viewLifecycleOwner)).getViewModel<VM>().apply {
            activity = this@store.activity as BaseActivity
        }
    }
}

private val mainHandler = Handler(Looper.getMainLooper())

fun runOnMain(code: () -> Unit) {
    mainHandler.post {
        code()
    }
}

fun Activity.runOnUiThreadIfRunning(code: () -> Unit) {
    if (isRunning()) {
        Handler(Looper.getMainLooper()).post {
            code()
        }
    }
}

inline fun <reified T> List<T>.clone(): List<T> where T : Any {
    return this.map { it }
}

inline fun <reified T> T.deepClone(): T where T : Any {
    return gson.fromJson(toJson())
}

fun Activity.messageAlert(
    title: String,
    message: String,
    buttonText: String = "OK",
    hideClose: Boolean = false,
    negativeText: String = "",
    isDuel: Boolean = false,
    callback: (Boolean, AlertDialog) -> Unit
) {
    try {
        /*runOnMain {
            if (!isRunning())
                return@runOnMain
            val dialogView = LayoutInflater.from(this).inflate(R.layout.message_dialog, null)
            //Now we need an AlertDialog.Builder object
            val builder = AlertDialog.Builder(this)
            //setting the view of the builder to our custom view that we already inflated
            builder.setView(dialogView)

            val alertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            *//*if (isDuel) {
                dialogView.title.setBackgroundResource(R.drawable.confirmation_gradient_background)
            }*//*

            if (!isRunning())
                return@runOnMain
            try {
                alertDialog.show()
            } catch (ex: Exception) {

            }

            dialogView.title.text = title
            dialogView.message.text = message
            dialogView.positive_button.text = buttonText

            if (negativeText.isNotEmpty()) {
                dialogView.nagitive_button.visibility = VISIBLE
                dialogView.nagitive_button.text = negativeText
                dialogView.nagitive_button.onOneClick {
                    callback(false, alertDialog!!)
                    alertDialog.dismiss()
                }

            } else {
                dialogView.nagitive_button.visibility = View.GONE
            }

            if (hideClose) {
                dialogView.close.visibility = View.GONE
            } else {
                dialogView.close.onOneClick {
                    callback(false, alertDialog!!)
                    alertDialog.dismiss()
                }
            }
            dialogView.positive_button.onOneClick {
                callback(true, alertDialog!!)
            }
        }*/
    } catch (ex: java.lang.Exception) {
        ex.printStackTrace()
    }
}

fun ImageView.loadBitmap(bitmap: Bitmap) {
    Glide.with(this)
        .asBitmap()
        .load(bitmap)
        .into(this)
}

fun Context.getVersionName(): String {
    val manager = this.packageManager
    val info = manager.getPackageInfo(this.packageName, PackageManager.GET_ACTIVITIES)
    return info.versionName
}

fun Double.toDecimalFormat(): String {
    val formatter = DecimalFormat("#,###,###.##")
    formatter.isDecimalSeparatorAlwaysShown = false
    return formatter.format(this)
}

fun Int.toDecimalFormat(): String {
    val formatter = DecimalFormat("#,###,###")
    return formatter.format(this)
}

fun String.toDecimalFormat(): String {
    if (trim().isEmpty())
        return ""
    val doubleNumber = toDoubleOrNull()
    return if (doubleNumber != null) {
        val formatter = DecimalFormat("#,###,###.##")
        formatter.isDecimalSeparatorAlwaysShown = false
        formatter.format(doubleNumber)
    } else {
        val formatter = DecimalFormat("#,###,###")
        formatter.format(this.toInt())
    }
}

fun Double.toDecimalFormatNoCama(): String {
    val formatter = DecimalFormat("#######.##")
    formatter.isDecimalSeparatorAlwaysShown = false
    return formatter.format(this)
}

fun Int.toDecimalFormatNoCama(): String {
    val formatter = DecimalFormat("#######")
    return formatter.format(this)
}

fun String.toDecimalFormatNoCama(): String {
    if (trim().isEmpty())
        return ""
    val doubleNumber = toDoubleOrNull()
    return if (doubleNumber != null) {
        val formatter = DecimalFormat("#######.##")
        formatter.format(doubleNumber)
    } else {
        val formatter = DecimalFormat("#######")
        formatter.format(this.toInt())
    }
}

fun View.onOneClick(time: Long = 1000, callback: () -> Unit) {
    //if (background != null)
//    background = getAdaptiveRippleDrawable(background)
    setOnClickListener(object : OnOneClickListener(time) {
        override fun onOneClick(v: View) {
            callback()
        }
    })
}

fun <T : Any> Call<T>.executeNoError(
    activity: BaseActivity? = null,
    showLoading: Boolean = true,
    showError: Boolean = showLoading,
    callback: (T?) -> Unit
) {
    val progressBarHandler: ProgressBarHandler? =
        activity?.let { if (showLoading) ProgressBarHandler(
            activity
        ) else null }
    thread {
        runOnMain {
            progressBarHandler?.show()
        }

        if (activity != null) {
            (activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
                .activeNetworkInfo.let { networkInfo ->
                    if (networkInfo == null || !networkInfo.isConnected) {
                        Thread.sleep(500)
                        progressBarHandler?.hide()

                        if (showError) {
                            activity?.networkErrorDialog {
                                Handler().postDelayed({
                                    execute(activity, showLoading, callback)
                                    progressBarHandler?.hide()
                                }, 200)

                            }
                        } else {
                            callback(null)
                        }
                        return@thread
                    }
                }
        }

        try {
            val response = execute()
//            val code = response.code()
            var result: T?

            runOnMain {
                progressBarHandler?.hide()
            }

            result = response.body()
            runOnMain {
                callback(result)
            }
        } catch (ex: SocketTimeoutException) {

        } catch (ex: Exception) {
        }
    }
}

fun logout(activity: BaseActivity?) {
    runOnMain {

        if (activity != null) {
            activity.messageAlert(
                "Login Expired",
                "Your session has been expired, please login again",
                "OK",
                true
            ) { _, dialog ->

                dialog.dismiss()
                activity.apply {
                    try {
                        putBoolean("IS_USER_LOGIN", false)

                    } catch (ex: java.lang.Exception) {
                        ex.printStackTrace()
                    }

                    /*val intent = Intent(
                        this,
                        RegistrationActivity::class.java
                    )
                    unSubscribeFromTopic(Constants.TopicName.ALL.name)
                    unSubscribeFromTopic(Constants.TopicName.NEWUSER.name)
                    unSubscribeFromTopic(Constants.TopicName.PAIDUSER.name)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    clearUserSavedData()
                    finishAffinity()*/
                }
            }
        }
    }
}


fun <T> Call<T>.execute(
    activity: BaseActivity? = null,
    showLoading: Boolean = true,
    callback: (T) -> Unit
) {
    val progressBarHandler: ProgressBarHandler? =
        activity?.let { if (showLoading) ProgressBarHandler(
            activity
        ) else null }
    thread {
        runOnMain {
            progressBarHandler?.show()
        }
        if (activity != null) {
            (activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
                .activeNetworkInfo.let { networkInfo ->
                    if (networkInfo == null || !networkInfo.isConnected) {
                        Thread.sleep(500)
                        progressBarHandler?.hide()

                        activity.networkErrorDialog {
                            Handler().postDelayed({
                                execute(activity, showLoading, callback)
                                progressBarHandler?.hide()
                            }, 200)

                        }
                        return@thread
                    }
                }
        }

        val requestInfo = request()

        try {
            val response = execute()

            val code = response.code()

            val headers = response.headers()

            val result: T?


            when (code) {
                400 -> {
                    runOnMain {
                        progressBarHandler?.hide()
                        activity?.showServerErrorDialog(
                            requestInfo,
                            "400 error\n ${response.errorBody()?.string() ?: ""}"
                        )
                    }
                    result = null
                }
                500 -> {
                    runOnMain {
                        progressBarHandler?.hide()
                        activity?.showServerErrorDialog(requestInfo, "500 error")
                    }
                    result = null
                }
                in 401..404 -> {

                    when (code) {
                        403, 401 -> {
                            runOnMain {
                                val error =
                                    response.errorBody()?.string()?.toJsonTree()!!["detail"].string
                                progressBarHandler?.hide()
                                activity?.showErrorDialog(
                                    error,
                                    true
                                )
                            }
                        }
                        else -> {
                            response.errorBody()?.string()?.toJsonTree()?.obj?.let {
                                if (it.has("error")) {
                                    it.get("error")?.let {
                                        if (it.string == "insufficient_scope" || it.string == "invalid_token" || it.string.contains(
                                                "unauthorized"
                                            )
                                        ) {
                                            logout(activity)
                                        } else {
                                            runOnMain {
                                                progressBarHandler?.hide()
                                                activity?.showServerErrorDialog(requestInfo, "$it")
                                            }
                                        }
                                    } ?: runOnMain {
                                        progressBarHandler?.hide()
                                        activity?.showServerErrorDialog(requestInfo, "$it")
                                    }

                                } else {
                                    progressBarHandler?.hide()
                                    activity?.showServerErrorDialog(requestInfo, "$it")
                                }
                            } ?: run {
                                progressBarHandler?.hide()
                                activity?.showServerErrorDialog(requestInfo, "Error code: $code")
                            }
                        }
                    }
                    result = null
                }
                in 200..299 -> {
                    val body = response.body()

                    result = if (body == null) {
                        runOnMain {
                            progressBarHandler?.hide()
                            activity?.showServerErrorDialog(requestInfo, "Body is null")
                        }
                        null
                    } else {
                        body
                    }
                }
                else -> {
                    runOnMain {
                        progressBarHandler?.hide()
                        activity?.showServerErrorDialog(requestInfo, "Error code: $code")
                    }
                    result = null
                }
            }

            runOnMain {
                calculateDiffrence(headers.getDate("Date")!!.time)

                progressBarHandler?.hide()
                result?.let { callback(it) }
            }

            /*activity?.runOnUiThreadIfRunning {
                calculateDiffrence(headers.getDate("Date")!!.time)

                progressBarHandler?.hide()
                callback(result)
            }*/
        } catch (e: SocketTimeoutException) {
            activity?.networkErrorDialog {
                Handler().postDelayed({
                    execute(activity, showLoading, callback)
                    progressBarHandler?.hide()
                }, 200)

            }
        } catch (e: SSLHandshakeException) {
            activity?.networkErrorDialog {
                Handler().postDelayed({
                    execute(activity, showLoading, callback)
                    progressBarHandler?.hide()
                }, 200)

            }
        } catch (e: SSLException) {
            activity?.networkErrorDialog {
                Handler().postDelayed({
                    execute(activity, showLoading, callback)
                    progressBarHandler?.hide()
                }, 200)

            }
        } catch (e: UnknownHostException) {
            activity?.networkErrorDialog {
                Handler().postDelayed({
                    execute(activity, showLoading, callback)
                    progressBarHandler?.hide()
                }, 200)

            }
        } catch (e: ConnectException) {
            activity?.networkErrorDialog {
                Handler().postDelayed({
                    execute(activity, showLoading, callback)
                    progressBarHandler?.hide()
                }, 200)

            }
        } catch (e: IllegalStateException) {
            /*activity?.networkErrorDialog {
                Handler().postDelayed({
                    execute(activity, showLoading, callback)
                    progressBarHandler?.hide()
                }, 200)
            }*/
        } catch (e: Exception) {
            e.printStackTrace()
            runOnMain {
                progressBarHandler?.hide()
                activity?.showServerErrorDialog(requestInfo, e.message)
            }

            /*activity?.networkErrorDialog {
                progressBarHandler?.hide()
                execute(activity, showLoading, callback)
            }*/
        }
        runOnMain {
            progressBarHandler?.hide()
        }
    }
}

fun <T> LiveData<T>.notify() {
    if (this is MutableLiveData<T>) this.value = value
}

fun <T> MutableLiveData<T>.notify() {
    postValue(value)
}

fun Bitmap.toBase64(quality: Int = 100): String {
    val baos = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.JPEG, quality, baos)
    val b = baos.toByteArray()
    return Base64.encodeToString(b, Base64.DEFAULT)
}

fun json(vararg entries: Pair<String, Any>): JSONObject {
    val jsonObject = JSONObject()
    entries.forEach {
        jsonObject.put(it.first, it.second)
    }
    return jsonObject
}


fun List<String>.toBulletedList(): CharSequence {
    return "• " + joinToString("\n• ")
}

fun getFormmatedTime(creationDate: String): Long {
    val date: Date = SimpleDateFormat(
        "yyyy-MM-dd'T'HH:mm:ss.SSS",
        Locale.US
    ).parse(creationDate.split("+")[0])

    return date.time
}

fun getFormattedDate(creationDate: String): Date {
    val date: Date = SimpleDateFormat(
        "yyyy-MM-dd'T'HH:mm:ss.SSS",
        Locale.US
    ).parse(creationDate.split("+")[0])

    return date
}

fun getDateFormatForBonus(creationDate: String): Long {
    val date: Date = SimpleDateFormat(
        "yyyy-MM-dd HH:mm:ss",
        Locale.US
    ).parse(creationDate.split("+")[0])

    return date.time
}

@SuppressLint("SimpleDateFormat")
fun String.formatDate(): String {
    val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm")
    return formatter.format(parser.parse(this))
}

@SuppressLint("SimpleDateFormat")
fun String.formatTime(): String {
    val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    val sdf = SimpleDateFormat("hh:mm a")
    return sdf.format(parser.parse(this))
}

/*fun clearUserSavedData() {
    MyApplication.sharedPreferences.edit().clear().apply()
}*/

fun Activity.setStatusBarColor(color: Int) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

        window.statusBarColor = ContextCompat.getColor(this, color)
    } else {

    }
}

fun Activity.setStatusBarColor(color: String) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

        window.statusBarColor = Color.parseColor(color)
    } else {

    }
}

fun RadioGroup.checkedButton() = findViewById<RadioButton>(this.checkedRadioButtonId)

fun Activity.setBackgroundBlur(view: BlurView, root: ViewGroup? = null) {
    val radius = 5f

    val decorView = window.decorView
    //ViewGroup you want to start blur from. Choose root as close to BlurView in hierarchy as possible.
    val rootView = root ?: decorView.findViewById(android.R.id.content) as ViewGroup
    //Set drawable to draw in the beginning of each blurred frame (Optional).
    //Can be used in case your layout has a lot of transparent space and your content
    //gets kinda lost after after blur is applied.
    val windowBackground = decorView.background

    view.setupWith(rootView)
        .setFrameClearDrawable(windowBackground)
        .setBlurAlgorithm(RenderScriptBlur(this))
        .setBlurRadius(radius)
        .setHasFixedTransformationMatrix(true)
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
    })
}

@SuppressLint("HardwareIds")
fun uniqueId(applicationContext: Context, callback: (String) -> Unit) {
    val unique_id = Settings.Secure.getString(
        applicationContext.contentResolver,
        Settings.Secure.ANDROID_ID
    )

    putString("UNIQUE_ID", unique_id)
    callback(unique_id)
}

fun canAuthenticateWithBiometrics(context: Context): Boolean {
    // Check whether the fingerprint can be used for authentication (Android M to P)
    val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
    return if (Build.VERSION.SDK_INT < 29) {
        val fingerprintManagerCompat = FingerprintManagerCompat.from(context)
        fingerprintManagerCompat.hasEnrolledFingerprints() && fingerprintManagerCompat.isHardwareDetected && keyguardManager.isKeyguardSecure
    } else {    // Check biometric manager (from Android Q)
        val biometricManager = context.getSystemService(BiometricManager::class.java)
        if (biometricManager != null) {
            biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS && keyguardManager.isKeyguardSecure
        } else false
    }
}

inline fun <reified T : Any> JsonElement.to() = gson.fromJson<T>(this)

fun EditText.onDone(callback: () -> Unit) {
    // These lines optional if you don't want to set in Xml
    imeOptions = EditorInfo.IME_ACTION_DONE
    maxLines = 1
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            callback.invoke()
            true
        }
        false
    }
}

fun log(tag: String, message: String = "__") {
    if (BuildConfig.DEBUG) {
        Log.i(tag, message)
    }
}

fun log(tag: Any, message: Any?) {
    if (BuildConfig.DEBUG) {
        Log.i(tag.toString(), message?.toJson() ?: "___")
    }
}

inline fun <reified T> getModel(key: String): T? where T : Any {
    val sp = MyApplication.sharedPreferences
    return sp.getString(key, null)?.let { gson.fromJson(it) }
}

fun getFcmToken(callback: (String) -> Unit) {
    FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
        val deviceId = it.token
        callback(deviceId)
        putString("FCM_ID", deviceId)
    }
}

fun getAdId(callback: (String?) -> Unit) {
    AsyncTask.execute {
        try {
            val adInfo = AdvertisingIdClient.getAdvertisingIdInfo(MyApplication.appContext!!)
            runOnMain {
                callback(adInfo?.id!!)
                putString("AAID", adInfo.id!!)
            }
        } catch (exception: IOException) {
            // Error handling if needed
        } catch (exception: GooglePlayServicesRepairableException) {
        } catch (exception: GooglePlayServicesNotAvailableException) {
        }
    }
}

fun Context.getServiceProviderName(): String {
    // Get System TELEPHONY service reference
    var tManager: TelephonyManager = this
        .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

// Get carrier name (Network Operator Name)
    return tManager.networkOperatorName
}

fun Activity.clearUserSavedData() {
    MyApplication.sharedPreferences.edit().clear().commit()
//    DatabaseHelper.clearDB()
}
