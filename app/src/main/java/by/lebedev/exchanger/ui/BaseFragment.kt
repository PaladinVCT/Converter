package by.lebedev.exchanger.ui

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import by.lebedev.exchanger.data.network.utils.observeEvent
import by.lebedev.exchanger.ui.utils.*
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelChildren
import javax.inject.Inject


typealias BindingInflater<VB> = (LayoutInflater, ViewGroup?, Boolean) -> VB

abstract class BaseFragment<V : ViewDataBinding> : Fragment(), MavericksView {

    protected var onDismissCallback: (() -> Unit)? = null

    @Inject
    lateinit var processScope: CoroutineScope


    protected var binding: V? = null
    private lateinit var navController: NavController
    abstract val bindingInflater: BindingInflater<V>
    protected abstract val viewModel: BaseViewModel<out MavericksState>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return createBinding(inflater, container, savedInstanceState).also {
            it.lifecycleOwner = viewLifecycleOwner
            binding = it
        }.root
    }

    protected open fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): V = bindingInflater(inflater, container, false)

    abstract fun onViewCreated(binding: V, savedInstanceState: Bundle?)

    protected fun requireBinding(): V = requireNotNull(binding)
    protected fun requireBaseContext(): Context = requireActivity().baseContext
    protected inline fun withBinding(block: V.() -> Unit) = requireBinding().block()

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        onViewCreated(requireBinding(), savedInstanceState)

        viewModel.onNavigateEvent.observeEvent(viewLifecycleOwner) {
            handleNavigation(it)
        }
        viewModel.onErrorEvent.observeEvent(viewLifecycleOwner) {
            handleError(it)
        }
        viewModel.onAlertEvent.observeEvent(viewLifecycleOwner) {
            showAlert(it)
        }
        viewModel.onMessageEvent.observeEvent(viewLifecycleOwner) {
            handleMessage(it)
        }
    }

    private fun showAlert(message: String) {
        AlertDialog.Builder(context)
            .setTitle("Warning")
            .setMessage(message)
            .setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    open fun handleError(throwable: Throwable) {
        handleDefaultError(throwable)
    }

    open fun handleNavigation(navEvent: BaseNavEvent): Unit =
        handleDefaultNavigation(
            navController = navController,
            navEvent = navEvent,
            processScope = processScope
        )

    open fun handleMessage(cluster: MessageCluster) {
        showDialog(cluster)
    }

}

fun Fragment.handleDefaultNavigation(
    navController: NavController,
    navEvent: BaseNavEvent,
    processScope: CoroutineScope? = null
): Unit = when (navEvent) {
    NavigateUp -> {
        navController.navigateUp().let { Unit }
    }
    StartOver -> {
        // Cancel all job for processScope
        processScope?.coroutineContext?.cancelChildren()
        requireActivity().restart()
    }
    is PopBackStack -> {
        navController.popBackStack(
            navEvent.destinationId,
            navEvent.inclusive
        ).let { Unit }
    }
    is NavCommand -> {
        navController.navigate(navEvent.action, navEvent.args, navEvent.navOptions)
    }
    is GlobalNavCommand -> {
        navigateGlobal(navEvent)
    }
}

fun Fragment.navigateGlobal(navCommand: GlobalNavCommand) {
    val navController = if (navCommand.globalHost == null) {
        findNavController()
    } else {
        Navigation.findNavController(requireActivity(), navCommand.globalHost)
    }
    navController.navigate(navCommand.action, navCommand.args, navCommand.navOptions)
}