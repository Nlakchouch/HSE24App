package com.hse24.app.utils

import android.content.Context
import android.util.TypedValue
import androidx.fragment.app.FragmentActivity
import com.google.android.material.snackbar.Snackbar
import com.hse24.app.R
import kotlin.math.roundToInt

object UiUtils {

    /**
     * Showing SnackBar with a specific message
     */
    fun showSnackBar(context: FragmentActivity, msg: String){
        val snackbar = Snackbar.make(
            context.findViewById(android.R.id.content),
            msg, Snackbar.LENGTH_SHORT)
        snackbar.show()
    }

    /**
     * Used for supporting Tablets and Big screen devices
     */
    fun isTablet(context: Context): Boolean {
        return context.resources.getBoolean(R.bool.is_tablet)
    }

    /**
     * Converting dp to pixel
     */
    fun dpToPx(context: Context, dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics
        ).roundToInt()
    }
}