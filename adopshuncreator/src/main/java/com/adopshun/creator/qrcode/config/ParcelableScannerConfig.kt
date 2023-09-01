package com.adopshun.creator.qrcode.config

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
internal class ParcelableScannerConfig(
  val formats: IntArray,
  val stringRes: Int,
  val drawableRes: Int?,
  val hapticFeedback: Boolean,
  val showTorchToggle: Boolean,
  val horizontalFrameRatio: Float,
  val useFrontCamera: Boolean,
) : Parcelable