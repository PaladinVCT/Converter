package by.lebedev.exchanger.ui.utils

import android.text.InputFilter
import android.text.Spanned
import java.util.regex.Matcher
import java.util.regex.Pattern


class AmountInputFilter(digitsBeforeZero: Int, digitsAfterZero: Int) :
        InputFilter {
        private var mPattern: Pattern
        override fun filter(
            source: CharSequence,
            start: Int,
            end: Int,
            dest: Spanned,
            dstart: Int,
            dend: Int
        ): CharSequence {
            val matcher: Matcher = mPattern.matcher(dest)
            return if (!matcher.matches()) "" else source
        }

        init {
            Pattern.compile("[0-9]{0," + (digitsBeforeZero-1) + "}+((\\.[0-9]{0," + (digitsAfterZero-1) + "})?)||(\\.)?")
                .also { mPattern = it }
        }
    }