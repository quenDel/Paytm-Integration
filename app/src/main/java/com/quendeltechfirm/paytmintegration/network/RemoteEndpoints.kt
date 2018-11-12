package com.quendeltechfirm.paytmintegration.network

import com.quendeltechfirm.paytmintegration.constants.PaytmConstant
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface RemoteEndpoints {

    @POST(PaytmConstant.GENERATE_CHECKSUM_END_POINT)
    fun generateChecksum(@Body param: RequestBody?): Call<ResponseBody>

}