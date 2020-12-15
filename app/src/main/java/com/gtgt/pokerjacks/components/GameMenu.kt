package com.gtgt.pokerjacks.components

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.gtgt.pokerjacks.R
import kotlinx.android.synthetic.main.game_menu.view.*

object Mode {
    const val ON = 1
    const val OFF = -1
    const val NOTHING = 0
}

class GameMenu(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {
    private var view: View? = null
    var icon = 0
    var disabledIcon = 0
    var mode = Mode.NOTHING

    var iconBg: Drawable? = null
        set(value) {
            field = value
            view?.icon?.background = value
        }

    init {
        val a = getContext().obtainStyledAttributes(attrs, R.styleable.GameMenu)
        view = LayoutInflater.from(context).inflate(R.layout.game_menu, null)

        mode = a.getInt(R.styleable.GameMenu_mode, Mode.NOTHING)

        icon = a.getResourceId(R.styleable.GameMenu_normalIcon, 0)
        disabledIcon = a.getResourceId(R.styleable.GameMenu_disabledIcon, 0)

        if (disabledIcon != 0) {
            view!!.isOn.visibility = View.VISIBLE
        }

        view!!.title.text = a.getText(R.styleable.GameMenu_name)

        if (mode == Mode.OFF) {
            view!!.icon.setImageResource(disabledIcon)
        } else {
            view!!.icon.setImageResource(icon)
        }

        addView(view)
        a.recycle()
    }

    fun setTitle(title: String) {
        view!!.title.text = title
    }

    fun toggle() =
        if (mode == Mode.OFF) {
            mode = Mode.ON
            view!!.icon.setImageResource(icon)
            view!!.isOn.setImageResource(R.drawable.game_menu_tick)
            true
        } else {
            mode = Mode.OFF
            view!!.icon.setImageResource(disabledIcon)
            view!!.isOn.setImageResource(R.drawable.game_menu_cross)
            false
        }
}