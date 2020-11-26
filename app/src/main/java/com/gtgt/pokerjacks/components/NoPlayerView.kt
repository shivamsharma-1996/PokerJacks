package com.gtgt.pokerjacks.components

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.gtgt.pokerjacks.R

class NoPlayerView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val bounce = AnimationUtils.loadAnimation(context, R.anim.bounce)

    init {
        bounce.repeatMode = Animation.REVERSE
        bounce.duration = 500L

        setBackgroundResource(R.drawable.no_player_game)
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)

        if (visibility == VISIBLE) {
            startAnimation(bounce)
        }

    }


}
