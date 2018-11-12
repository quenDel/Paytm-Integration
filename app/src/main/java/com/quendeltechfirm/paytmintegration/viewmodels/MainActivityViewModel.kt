package com.quendeltechfirm.paytmintegration.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import android.os.Bundle
import com.google.gson.Gson
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPGService
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import com.quendeltechfirm.paytmintegration.MainActivity
import com.quendeltechfirm.paytmintegration.constants.PaytmConstant
import com.quendeltechfirm.paytmintegration.eventFire.MainActivityEvents
import com.quendeltechfirm.paytmintegration.model.PaymentModel
import com.quendeltechfirm.paytmintegration.network.RemoteConnectionService
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap

class MainActivityViewModel(private val app: Application) : AndroidViewModel(app) {

    val disposable = CompositeDisposable()
    val amount = ObservableField<String>()
    val eventNotifier = MutableLiveData<MainActivityEvents>()
    val paymentResponse = MutableLiveData<MainActivityDataEvents>()
    lateinit var order: PaymentModel
    var j = JSONObject()

    fun initializePayment() {
        if (amount.get().isNullOrEmpty()) {
            eventNotifier.value = MainActivityEvents.AMOUNT_FIELD_EMPTY
        } else
            generateCheckSum(createOrder())
    }

    private fun createOrder(): PaymentModel {
        order = PaymentModel()
        order.mid = PaytmConstant.MID
        order.channelid = PaytmConstant.CHANNEL_ID
        order.industrytypeid = PaytmConstant.INDUSTRY_TYPE_ID
        order.website = PaytmConstant.WEBSITE
        order.custid = "CUST${generateUniqueId()}"
        order.txnamount = amount.get().toString()
        order.orderid = "ORDER".plus(generateUniqueId())
        order.callbackurl = PaytmConstant.CALLBACK_URL + "=${order.orderid}"

        return order
    }

    private fun generateCheckSum(order: PaymentModel) {
        eventNotifier.value = MainActivityEvents.SHOW_LOADING

        val orderMap = Gson().toJson(order)

        val request = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
                orderMap.toString())
        disposable.addAll(Single.fromCallable { RemoteConnectionService.getInstance().generateCheckSum(request) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ it ->
                    val json = JSONObject(it.body().string())
                    if (json.getBoolean("status")) {
                        val checksum = json.getString("CHECKSUMHASH")
                        order.checksumhash = checksum
                        j.put("CHECKSUMHASH", checksum)
                        eventNotifier.value = MainActivityEvents.ON_CHECKSUM_GENERATED
                    } else eventNotifier.value = MainActivityEvents.HIDE_LOADING
                }, {
                    eventNotifier.value = MainActivityEvents.HIDE_LOADING
                }))
    }

    fun startPayment(context: MainActivity) {
        val service = PaytmPGService.getStagingService()

        val m = Gson().fromJson<HashMap<String, String>>(Gson().toJson(order), HashMap::class.java)
        val paytmOrder = PaytmOrder(m)

        service.initialize(paytmOrder, null)

        eventNotifier.value = MainActivityEvents.HIDE_LOADING

        service.startPaymentTransaction(context, true, true,
                object : PaytmPaymentTransactionCallback {
                    override fun onTransactionResponse(inResponse: Bundle) {
                        val msg = inResponse.getString("RESPMSG")
                        if (inResponse.getString("STATUS") == "TXN_FAILURE") {
                            postPaymentResponse(false, msg, null)
                        } else {
                            postPaymentResponse(true, msg, inResponse)
                        }
                    }

                    override fun clientAuthenticationFailed(inErrorMessage: String?) {
                        postPaymentResponse(false, inErrorMessage, null)
                    }

                    override fun someUIErrorOccurred(inErrorMessage: String?) {
                        postPaymentResponse(false, inErrorMessage, null)
                    }

                    override fun onTransactionCancel(inErrorMessage: String?, inResponse: Bundle?) {
                        postPaymentResponse(false, inErrorMessage, null)
                    }

                    override fun networkNotAvailable() {
                        postPaymentResponse(false, "NetWork not available", null)
                    }

                    override fun onErrorLoadingWebPage(iniErrorCode: Int, inErrorMessage: String?, inFailingUrl: String?) {
                        postPaymentResponse(false, inErrorMessage, null)
                    }

                    override fun onBackPressedCancelTransaction() {
                        postPaymentResponse(false, "Cancel by back press", null)
                    }
                })
    }

    private fun generateUniqueId(): String {
        val r = Random(System.currentTimeMillis())
        return ((1 + r.nextInt(2)) * 10000
                + r.nextInt(10000)).toString()
    }

    private fun postPaymentResponse(status: Boolean, msg: String?, bundle: Bundle?) {
        paymentResponse.value = MainActivityDataEvents(status, msg, bundle)
    }

    data class MainActivityDataEvents(var status: Boolean, val msg: String?, val bundle: Bundle?)
}
