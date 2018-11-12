package com.quendeltechfirm.paytmintegration.constants

class PaytmConstant {

    companion object {
        const val MID = "CrLspl32596480231373"
        const val CHANNEL_ID = "WAP"
        const val WEBSITE = "WEBSTAGING"
        const val INDUSTRY_TYPE_ID = "Retail"
        const val CALLBACK_URL = "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID"


        const val BASE_URL = "http://192.168.43.66/"
        const val GENERATE_CHECKSUM_END_POINT = "Paytm_App_Checksum/generateChecksum.php"
        const val VERIFY_CHECKSUM_END_POINT = ""
    }

}