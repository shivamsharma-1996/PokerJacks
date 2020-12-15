package com.gtgt.pokerjacks.components

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.gtgt.pokerjacks.R
import kotlinx.android.synthetic.main.game_radio_menu.view.*
import kotlinx.android.synthetic.main.game_radio_menu.view.title

class GameRadioMenu(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {
    private var view: View? = null

    private val selectedColor = Color.WHITE
    private val unSelectedColor = Color.parseColor("#AB000000")
    var isOn = false

    var iconBg: Drawable? = null
        set(value) {
            field = value
            view?.option?.background = value
        }


    init {
        val a = getContext().obtainStyledAttributes(attrs, R.styleable.GameRadioMenu)
        view = LayoutInflater.from(context).inflate(R.layout.game_radio_menu, null)

        isOn = a.getBoolean(R.styleable.GameRadioMenu_isOn, false)

        view!!.title.text = a.getText(R.styleable.GameRadioMenu_text)
        view!!.selectIndiicator.backgroundTintList = ColorStateList.valueOf(
            if (isOn) selectedColor else unSelectedColor
        )

        view!!.setOnClickListener {
            if (isOn) {
                view!!.selectIndiicator.backgroundTintList = ColorStateList.valueOf(unSelectedColor)
                isOn = false
            } else {
                view!!.selectIndiicator.backgroundTintList = ColorStateList.valueOf(selectedColor)
                isOn = true
            }
        }

        addView(view)
        a.recycle()
    }

    fun setTitle(title: String) {
        view!!.title.text = title
    }
}