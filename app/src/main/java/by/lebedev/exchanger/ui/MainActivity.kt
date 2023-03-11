package by.lebedev.exchanger.ui


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import by.lebedev.exchanger.R
import by.lebedev.exchanger.databinding.ActivityMainBinding
import com.airbnb.epoxy.EpoxyDataBindingPattern
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
@EpoxyDataBindingPattern(rClass = R::class, layoutPrefix = "view_holder")
class MainActivity : AppCompatActivity() {

    var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}