package by.lebedev.exchanger.ui.utils

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavOptions

sealed class BaseNavEvent
object NavigateUp : BaseNavEvent()
object StartOver : BaseNavEvent()

data class NavCommand(
    val action: Int,
    val args: Bundle? = null,
    val navOptions: NavOptions? = null
) : BaseNavEvent()

data class GlobalNavCommand(
    val action: Int,
    val args: Bundle? = null,
    val navOptions: NavOptions? = null,
    val globalHost: Int? = null
) : BaseNavEvent()

data class PopBackStack(
    @IdRes val destinationId: Int, val inclusive: Boolean
) : BaseNavEvent()
