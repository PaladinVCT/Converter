package by.lebedev.exchanger.ui.utils


import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.BindingAdapter


@BindingAdapter("visibleGone")
fun showHide(view: View, show: Boolean) {
    view.isVisible = show
}

@BindingAdapter("setAllCaps")
fun TextView.setAllCaps(flag: Boolean?) {
    if (flag == true) text = text.toString().uppercase()
}

@BindingAdapter("setCurrencies")
fun AutoCompleteTextView.setValues(currencies: List<String>?) {
    currencies?.let {
        setAdapter(
            ArrayAdapter(
                this.context,
                android.R.layout.simple_dropdown_item_1line,
                it
            )
        )
    }
}

@BindingAdapter("setInitValue")
fun EditText.setInitValue(initValue: String?) {
    if (initValue==null)  setText("")
}

@BindingAdapter("setTextChangedListener")
fun EditText.setTextChangedListener(callback: (String) -> Unit) {
    doAfterTextChanged {
        callback(it.toString())
    }
}

@BindingAdapter("setAmountMask")
fun EditText.setAmountMask(flag: Boolean) {
    val filter = AmountInputFilter(99, 2)
    filters = arrayOf(filter)
}


