package com.adopshun.creator.qrcode.extensions

import com.adopshun.creator.qrcode.config.ParcelableScannerConfig
import com.adopshun.creator.qrcode.config.ScannerConfig

internal fun ScannerConfig.toParcelableConfig() =
  ParcelableScannerConfig(
    formats = formats,
    stringRes = stringRes,
    drawableRes = drawableRes,
    hapticFeedback = hapticFeedback,
    showTorchToggle = showTorchToggle,
    horizontalFrameRatio = horizontalFrameRatio,
    useFrontCamera = useFrontCamera,
  )