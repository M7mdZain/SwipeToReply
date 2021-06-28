package com.shain.messenger

import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation

class ResizeAnim(var view: View, private val startHeight: Int, private val targetHeight: Int) :
    Animation() {

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        if (startHeight == 0 || targetHeight == 0) {
            view.layoutParams.height =
                (startHeight + (targetHeight - startHeight) * interpolatedTime).toInt()
        } else {
            view.layoutParams.height = (startHeight + targetHeight * interpolatedTime).toInt()
            Log.d("TAG_TAG", "applyTransformation: height = ${view.layoutParams.height}")
        }
        view.requestLayout()
    }

    override fun willChangeBounds(): Boolean {
        return true
    }
}
