package by.lebedev.exchanger.ui.converter


import android.os.Bundle
import by.lebedev.exchanger.databinding.FragmentConverterBinding
import by.lebedev.exchanger.ui.BaseFragment
import by.lebedev.exchanger.ui.BindingInflater
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Singleton


@AndroidEntryPoint
@Singleton
class ConverterFragment : BaseFragment<FragmentConverterBinding>() {

    @Inject
    lateinit var controller: ConverterController

    override val viewModel: ConverterViewModel by fragmentViewModel()

    override val bindingInflater: BindingInflater<FragmentConverterBinding> =
        FragmentConverterBinding::inflate

    override fun onViewCreated(binding: FragmentConverterBinding, savedInstanceState: Bundle?) {
        withBinding {
            withState(viewModel) {
                state = it
                controller.callbacks = controllerCallbacks
                converterRv.setController(controller)
            }
        }

        viewModel.onEach(ConverterViewState::customerBalances) {
            controller.notifyModelChanged(1)
        }
    }

    override fun invalidate() = withState(viewModel) { state ->
        requireBinding().state = state
        controller.setData(state)
    }

    override fun onResume() {
        super.onResume()
        viewModel.startAutoUpdateJob()
    }

    override fun onPause() {
        viewModel.cancelAutoUpdateJob()
        super.onPause()
    }

    private val controllerCallbacks = object : ConverterController.Callbacks {
        override fun onSubmitClick() {
            viewModel.onSubmitClick()
        }

        override fun onCurrencyForSellChanged(currency: String) {
            viewModel.onCurrencyForSellChanged(currency)
        }

        override fun onAmountForSellChanged(amount: String) {
            viewModel.onAmountForSellChanged(amount)
        }

        override fun onCurrencyForReceiveChanged(currency: String) {
            viewModel.onCurrencyForReceiveChanged(currency)
        }

    }
}