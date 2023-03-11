package by.lebedev.exchanger.ui.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.text.Spanned
import android.text.method.KeyListener
import android.util.TypedValue
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.*
import androidx.core.content.res.use
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import by.lebedev.exchanger.R
import by.lebedev.exchanger.data.network.utils.ErrorType
import by.lebedev.exchanger.data.network.utils.ThrowableResult
import by.lebedev.exchanger.data.network.utils.Timeouts
import by.lebedev.exchanger.ui.MainActivity
import com.airbnb.mvrx.BuildConfig
import com.airbnb.mvrx.Mavericks
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import okhttp3.OkHttpClient
import retrofit2.HttpException
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.*


fun EditText.setTextAndCursor(charSequence: CharSequence?) {
    if (setTextIfNew(charSequence)) {
        setSelection(length())
    }
}

@Px
fun Context.dpToPx(@Dimension(unit = Dimension.DP) dp: Int): Int {
    return TypedValue
        .applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(),
            resources.displayMetrics
        ).toInt()
}

@Px
fun Context.resToPx(@DimenRes itemSpacingRes: Int): Int {
    return if (itemSpacingRes != 0) resources.getDimensionPixelOffset(itemSpacingRes) else 0
}

@SuppressLint("Recycle")
@ColorInt
fun Context.getThemeColor(@ColorRes colorRes: Int): Int {
    return obtainStyledAttributes(intArrayOf(colorRes)).use {
        it.getColor(0, Color.MAGENTA)
    }
}

fun TextView.setTextIfNew(charSequence: CharSequence?): Boolean =
    if (isTextDifferent(
            charSequence,
            text
        )
    ) {
        text = charSequence
        true
    } else false


fun TextInputLayout.updateError(message: CharSequence?) {
    if (isTextDifferent(error, message)) {
        error = message
    }
}

interface StorableValueEnum<T> {
    val value: T
}

fun TextInputLayout.updateHelperText(text: CharSequence?) {
    if (isTextDifferent(helperText, text)) {
        helperText = text
    }
}

fun TextInputLayout.updateHint(hintMessage: CharSequence?) {
    if (isTextDifferent(hint, hintMessage)) {
        hint = hintMessage
    }
}

fun EditText.updateInputType(type: Int) {
    if (inputType != type) {
        inputType = type
    }
}

fun EditText.updateKeyListener(listener: KeyListener) {
    if (keyListener != listener) {
        keyListener = listener
    }
}

fun isTextDifferent(str1: CharSequence?, str2: CharSequence?): Boolean {
    if (str1 === str2) {
        return false
    }
    if (str1 == null || str2 == null) {
        return true
    }
    val length = str1.length
    if (length != str2.length) {
        return true
    }

    if (str1 is Spanned) {
        return str1 != str2
    }

    for (i in 0 until length) {
        if (str1[i] != str2[i]) {
            return true
        }
    }
    return false
}


fun OkHttpClient.Builder.setupTimeout(timeouts: Timeouts): OkHttpClient.Builder {
    connectTimeout(timeouts.connect, timeouts.timeUnit)
    readTimeout(timeouts.read, timeouts.timeUnit)
    writeTimeout(timeouts.write, timeouts.timeUnit)
    return this
}

val HttpException.errorResponse: String?
    get() = response()?.errorBody()?.string()

val HttpException.errorMessage: String
    get() {
        val msg = response()!!.errorBody()?.string()
        return if (msg.isNullOrEmpty()) {
            message()
        } else {
            msg
        } ?: "unknown error"
    }

inline fun <reified T : Any> Fragment.lazyArg(key: String): Lazy<T> = lazyFast {
    val args = arguments ?: throw IllegalStateException("Missing arguments!")
    @Suppress("UNCHECKED_CAST")
    args.get(key) as T
}

inline fun <reified T> lazyFast(crossinline operation: () -> T): Lazy<T> =
    lazy(LazyThreadSafetyMode.NONE) {
        operation()
    }

fun Fragment.handleDefaultError(throwable: Throwable) {
    if (throwable is ThrowableResult) {

        longToast(throwable.message ?: throwable.type.toLocalizedString(requireContext()))

        if (throwable.type == ErrorType.Unauthorized) {
            requireActivity().restart()
        }
    } else if (BuildConfig.DEBUG) {
        longToast(throwable.message ?: return)
    }
}

fun ErrorType?.toLocalizedString(context: Context): String {
    val res = when (this) {
        ErrorType.IO -> R.string.error_io
        ErrorType.Network -> R.string.error_network
        ErrorType.NetworkConnection -> R.string.error_connection
        ErrorType.JsonParsing -> R.string.error_mapping
        ErrorType.Timeout -> R.string.error_timeout
        ErrorType.Unauthorized -> R.string.error_unauthorized
        ErrorType.Unclassified -> R.string.error_unclassified
        null -> null
    }
    return res?.let { context.getString(it) }.orEmpty()
}


fun Activity.restart() {
    finish()
    startActivity(Intent(this, MainActivity::class.java))
}

fun <T : Parcelable> T.toBundleMvRx(): Bundle = bundleOf(Mavericks.KEY_ARG to this)

fun Double.round(digitsAfterDot: Int): Double {
    val nf = NumberFormat.getInstance(Locale.ENGLISH)
    return nf.parse(String.format("%.${digitsAfterDot}f", this))?.toDouble() ?: 0.0
}

fun Double.roundBigDecimalString(): String {
    val df = DecimalFormat("#.##", DecimalFormatSymbols(Locale.ENGLISH))
    df.roundingMode = RoundingMode.FLOOR
    return df.format(this).toDouble().toBigDecimal()
        .toString()
}

fun Double.roundToTwoDigits(): Double {
    val df = DecimalFormat("#.##", DecimalFormatSymbols(Locale.ENGLISH))
    df.roundingMode = RoundingMode.DOWN
    return df.format(this).toDouble()
}

fun String?.toSafeDouble(): Double {
    return when {
        this == null || this.isEmpty() ||this=="." -> 0.0
        else -> this.toDouble()
    }
}

fun SharedPreferences.getConversionCount(): Int {
    return getInt(by.lebedev.exchanger.BuildConfig.HAS_FEE_KEY, 0)
}

fun SharedPreferences.incrementConversionCount() {
    edit().putInt(
        by.lebedev.exchanger.BuildConfig.HAS_FEE_KEY,
        getInt(by.lebedev.exchanger.BuildConfig.HAS_FEE_KEY, 0).plus(1)
    ).apply()
}

fun Fragment.showDialog(cluster: MessageCluster
) {

    MaterialAlertDialogBuilder(requireContext())
        .setTitle(cluster.titleRes)
        .setMessage(cluster.message)
        .setPositiveButton(cluster.buttonRes) { dialog, _ ->
            dialog.dismiss()
        }.show()
}