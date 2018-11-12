package com.quendeltechfirm.paytmintegration

import android.app.ProgressDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.quendeltechfirm.paytmintegration.databinding.ActivityMainBinding
import com.quendeltechfirm.paytmintegration.viewmodels.MainActivityViewModel.MainActivityDataEvents
import com.quendeltechfirm.paytmintegration.eventFire.MainActivityEvents
import com.quendeltechfirm.paytmintegration.viewmodels.MainActivityViewModel

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainActivityViewModel
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        viewModel.eventNotifier.observe(this, Observer { handleProgressIndicators(it) })
        viewModel.paymentResponse.observe(this, Observer { handlePaymentStatus(it) })

        binding.presenter = this
        binding.viewModel = viewModel
    }

    fun payNow(view: View) {
        viewModel.initializePayment()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.disposable.clear()
    }

    private fun handleProgressIndicators(event: MainActivityEvents?) {
        when (event) {
            MainActivityEvents.SHOW_LOADING -> showLoading()
            MainActivityEvents.HIDE_LOADING -> hideLoading()
            MainActivityEvents.NETWORK_ERROR -> showErrorMsg("Please check network")
            MainActivityEvents.AMOUNT_FIELD_EMPTY -> showErrorMsg("Please enter Amount")
            MainActivityEvents.ON_CHECKSUM_GENERATED -> viewModel.startPayment(this)
        }
    }

    private fun handlePaymentStatus(dataEvents: MainActivityDataEvents?) {
        if (dataEvents?.status!!) {
            onPaymentSuccess(dataEvents.bundle)
        } else {
            showErrorMsg(dataEvents.msg)
        }
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    private fun onPaymentSuccess(bundle: Bundle?) {
        Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()
    }

    private fun showErrorMsg(msg: String?) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        Log.e("error ", msg)
    }
}

