package com.example.newsgb._core.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.newsgb.R
import com.example.newsgb._core.data.AppState
import com.example.newsgb._core.viewmodel.BaseViewModel
import com.example.newsgb.databinding.MainActivityBinding
import com.example.newsgb.utils.network.OnlineLiveData
import com.example.newsgb.utils.ui.AlertDialogFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding
    protected var isNetworkAvailable: Boolean = true

    private val model: BaseViewModel<AppState>
        get() = TODO("заменить BaseViewModel на MainViewModel")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subscribeToNetworkChange(savedInstanceState)
    }

    private fun subscribeToNetworkChange(savedInstanceState: Bundle?) {
        OnlineLiveData(this).observe(this@MainActivity) { isNetworkAvailable ->
            if (isNetworkAvailable) {
                startNewsFragment(savedInstanceState)
            } else {
                Toast.makeText(
                    this,
                    R.string.dialog_message_device_is_offline,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun startNewsFragment(savedInstanceState: Bundle?) {
//        if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.main_container, NewsFragment.newInstance()) //TODO переделать в зависимости от реализации фрагмента
//                .commitNow()
//        }
    }

    override fun onResume() {
        super.onResume()
        binding = MainActivityBinding.inflate(layoutInflater)
        if (!isNetworkAvailable && isDialogNull()) {
            showNoInternetConnectionDialog()
        }
    }


    private fun isDialogNull(): Boolean =
        supportFragmentManager.findFragmentByTag(DIALOG_FRAGMENT_TAG) == null

    protected fun showNoInternetConnectionDialog() {
        showAlertDialog(
            getString(R.string.dialog_title_device_is_offline),
            getString(R.string.dialog_message_device_is_offline)
        )
    }

    private fun showAlertDialog(title: String?, message: String?) =
        AlertDialogFragment.newInstance(title, message)
            .show(supportFragmentManager, DIALOG_FRAGMENT_TAG)


    companion object {
        const val DIALOG_FRAGMENT_TAG = "74a54328-5d62-46bf-ab6b-cbf5d8c79522"
    }
}