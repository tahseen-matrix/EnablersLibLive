package com.adopshun.creator.utils

import com.adopshun.creator.qrcode.QRScannerActivity


internal object MlKitErrorHandler {

  @Suppress("UNUSED_PARAMETER", "FunctionOnlyReturningConstant")
  fun isResolvableError(activity: QRScannerActivity, exception: Exception) = false // always false when bundled
}