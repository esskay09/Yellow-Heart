package com.terranullius.yellowheart.utils

sealed class PaymentCallback{

    object success: PaymentCallback()
    class failure(message: String) : PaymentCallback()
    object NONE: PaymentCallback()
}
