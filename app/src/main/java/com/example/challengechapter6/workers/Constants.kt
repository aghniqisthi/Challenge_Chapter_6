package com.example.challengechapter6.workers

// Name of Notification Channel for verbose notifications of background work
@JvmField val VERBOSE_NOTIFICATION_CHANNEL_NAME: CharSequence = "Verbose WorkManager Notifications"
const val VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION = "Shows notifications whenever work starts"
@JvmField val NOTIFICATION_TITLE: CharSequence = "WorkRequest Starting"
const val CHANNEL_ID = "VERBOSE_NOTIFICATION"
const val NOTIFICATION_ID = 1

// The name of the image manipulation work
const val IMAGE_MANIPULATION_WORK_NAME = "image_manipulation_work"

// Other keys
const val OUTPUT_PATH = "blur_filter_outputs"
const val KEY_IMAGE_URI = "KEY_IMAGE_URI"
const val TAG_OUTPUT = "OUTPUT"
const val KEY_BLUR_LEVEL = "KEY_BLUR_LEVEL"
const val TAG_PROGRESS = "PROGRESS"
const val KEY_PROGRESS = "KEY_PROGRESS"
const val TITLE_IMAGE = "BlurredImage"
const val DATE_FORMAT = "yyyy.MM.dd 'at' HH:mm:ss z"

// Delay to slow down each WorkRequest so that it becomes easier to identify each WorkRequest start
const val DELAY_TIME_MILLIS: Long = 3000