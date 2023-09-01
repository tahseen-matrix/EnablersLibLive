package com.adopshun.creator.qrcode

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.adopshun.creator.qrcode.QRResult.QRError
import com.adopshun.creator.qrcode.QRResult.QRMissingPermission
import com.adopshun.creator.qrcode.QRResult.QRSuccess
import com.adopshun.creator.qrcode.QRResult.QRUserCanceled
import com.adopshun.creator.qrcode.QRScannerActivity.Companion.EXTRA_CONFIG
import com.adopshun.creator.qrcode.QRScannerActivity.Companion.RESULT_ERROR
import com.adopshun.creator.qrcode.QRScannerActivity.Companion.RESULT_MISSING_PERMISSION
import com.adopshun.creator.qrcode.config.ScannerConfig
import com.adopshun.creator.qrcode.extensions.getRootException
import com.adopshun.creator.qrcode.extensions.toParcelableConfig
import com.adopshun.creator.qrcode.extensions.toQuickieContentType

public class ScanCustomCode : ActivityResultContract<ScannerConfig, QRResult>() {

  override fun createIntent(context: Context, input: ScannerConfig): Intent {
    return Intent(context, QRScannerActivity::class.java).apply {
      putExtra(EXTRA_CONFIG, input.toParcelableConfig())
    }
  }

  override fun parseResult(resultCode: Int, intent: Intent?): QRResult {
    return when (resultCode) {
      RESULT_OK -> QRSuccess(intent.toQuickieContentType())
      RESULT_CANCELED -> QRUserCanceled
      RESULT_MISSING_PERMISSION -> QRMissingPermission
      RESULT_ERROR -> QRError(intent.getRootException())
      else -> QRError(IllegalStateException("Unknown activity result code $resultCode"))
    }
  }
}