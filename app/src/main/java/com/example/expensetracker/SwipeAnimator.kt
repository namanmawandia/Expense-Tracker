package com.example.expensetracker

import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils

object SwipeAnimator {

    fun animateSwipe(
        context: Context,
        targetView: View,
        direction: Direction,
        onComplete: () -> Unit
    ) {
        val (outAnimId, inAnimId) = when (direction) {
            Direction.LEFT -> R.anim.slide_out_left to R.anim.slide_in_right
            Direction.RIGHT -> R.anim.slide_out_right to R.anim.slide_in_left
        }

        val outAnim = AnimationUtils.loadAnimation(context, outAnimId)
        val inAnim = AnimationUtils.loadAnimation(context, inAnimId)

        outAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                onComplete()
                targetView.startAnimation(inAnim)
            }
        })

        targetView.startAnimation(outAnim)
    }

    enum class Direction {
        LEFT, RIGHT
    }
}
