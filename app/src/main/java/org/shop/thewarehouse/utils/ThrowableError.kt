package org.shop.thewarehouse.utils

class NetworkFailedError(message: String, cause: Throwable?) : Throwable(message, cause)
class NetworkConnectionError(message: String, cause: Throwable?) : Throwable(message, cause)