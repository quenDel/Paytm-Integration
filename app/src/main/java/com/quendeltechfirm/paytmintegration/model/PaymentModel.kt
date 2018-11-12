package com.quendeltechfirm.paytmintegration.model

import java.io.Serializable
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PaymentModel : Serializable {

    @SerializedName("MID")
    @Expose
    var mid: String? = null
    @SerializedName("ORDER_ID")
    @Expose
    var orderid: String? = null
    @SerializedName("CUST_ID")
    @Expose
    var custid: String? = null
    @SerializedName("INDUSTRY_TYPE_ID")
    @Expose
    var industrytypeid: String? = null
    @SerializedName("CHANNEL_ID")
    @Expose
    var channelid: String? = null
    @SerializedName("TXN_AMOUNT")
    @Expose
    var txnamount: String? = null
    @SerializedName("WEBSITE")
    @Expose
    var website: String? = null
    @SerializedName("CALLBACK_URL")
    @Expose
    var callbackurl: String? = null
    @SerializedName("CHECKSUMHASH")
    @Expose
    var checksumhash: String? = null

}