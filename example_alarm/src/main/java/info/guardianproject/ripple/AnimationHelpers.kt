package info.guardianproject.ripple

import android.animation.Animator
import android.annotation.SuppressLint
import android.view.View
import android.view.animation.ScaleAnimation
import android.view.animation.Animation
import kotlin.jvm.JvmOverloads
import android.view.animation.AnimationSet

@SuppressLint("NewApi")
object AnimationHelpers {
    fun translateY(view: View, fromY: Float, toY: Float, duration: Long) {
        if (duration == 0L) view.translationY = toY else view.animate().translationY(toY).setDuration(duration).start()
    }

    fun scale(view: View, fromScale: Float, toScale: Float, duration: Long, whenDone: Runnable?) {
        if (duration == 0L) {
            view.scaleX = toScale
            view.scaleY = toScale
            whenDone?.run()
        } else {
            val animation = view.animate().scaleX(toScale).scaleY(toScale).setDuration(duration)
            if (whenDone != null) {
                animation.setListener(object : Animator.AnimatorListener {
                    override fun onAnimationCancel(animation: Animator) {
                        whenDone.run()
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        whenDone.run()
                    }

                    override fun onAnimationRepeat(animation: Animator) {}
                    override fun onAnimationStart(animation: Animator) {}
                })
            }
            animation.start()
        }
    }

    @JvmOverloads
    fun addAnimation(view: View, animation: Animation, first: Boolean = false) {
        var previousAnimation = view.animation
        if (previousAnimation == null || previousAnimation.javaClass == animation.javaClass) {
            if (animation.startTime == Animation.START_ON_FIRST_FRAME.toLong()) view.startAnimation(animation) else view.animation =
                animation
            return
        }
        if (previousAnimation !is AnimationSet) {
            val newSet = AnimationSet(false)
            newSet.addAnimation(previousAnimation)
            previousAnimation = newSet
        }

        // Remove old of same type
        //
        val set = previousAnimation
        for (i in set.animations.indices) {
            val anim = set.animations[i]
            if (anim.javaClass == animation.javaClass) {
                set.animations.removeAt(i)
                break
            }
        }

        // Add this (first if it is a scale animation ,else at end)
        if (animation is ScaleAnimation || first) {
            set.animations.add(0, animation)
        } else {
            set.animations.add(animation)
        }
        animation.startNow()
        view.animation = set
    }
}