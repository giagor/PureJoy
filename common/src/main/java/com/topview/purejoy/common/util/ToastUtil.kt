package com.topview.purejoy.common.util

import android.content.Context
import android.widget.Toast

fun showToast(context: Context, text: CharSequence, duration: Int = Toast.LENGTH_LONG) =
    Toast.makeText(context, text, duration).show()
