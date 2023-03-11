package by.lebedev.exchanger.ui.utils

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment


fun Context.toast(@StringRes message: Int) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun Context.toast(message: CharSequence) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun Context.longToast(@StringRes message: Int) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.longToast(message: CharSequence) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Fragment.toast(@StringRes message: Int) = requireActivity().toast(message)

fun Fragment.toast(message: CharSequence) = requireActivity().toast(message)

fun Fragment.longToast(@StringRes message: Int) = requireActivity().longToast(message)

fun Fragment.longToast(message: CharSequence) = requireActivity().longToast(message)
