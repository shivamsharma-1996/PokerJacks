package com.gtgt.pokerjacks.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.gtgt.pokerjacks.R
import kotlinx.android.synthetic.main.check_switch.view.*

class CheckSwitch(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {
    private var checkedBg = 0
    var checkedView: TextView? = null

    var onChange: ((Int) -> Unit)? = null
    private var view: View? = null

    val item1: TextView
        get() = view!!.item1
    val item2: TextView
        get() = view!!.item2

    var selectedItem: Int = 0
        get() = try {
            if (checkedView!! == view!!.item1) 1 else 2
        } catch (ex: Exception) {
            0
        }

    init {
        val a = getContext().obtainStyledAttributes(attrs, R.styleable.CheckSwitch)

        view = LayoutInflater.from(context).inflate(R.layout.check_switch, null)

        if (a.hasValue(R.styleable.CheckSwitch_item1)) {
            view!!.item1.text = a.getText(R.styleable.CheckSwitch_item1)
        } else {
            view!!.item1.visibility = View.GONE
        }
        if (a.hasValue(R.styleable.CheckSwitch_item2)) {
            view!!.item2.text = a.getText(R.styleable.CheckSwitch_item2)
        } else {
            view!!.item2.visibility = View.GONE
        }

        if (a.hasValue(R.styleable.CheckSwitch_background)) {
            view!!.setBackgroundResource(
                a.getResourceId(
                    R.styleable.CheckSwitch_background,
                    0
                )
            )
        }

        if (a.hasValue(R.styleable.CheckSwitch_checkedItem) && a.hasValue(R.styleable.CheckSwitch_checkedBg)) {
            checkedView = if (a.getInt(
                    R.styleable.CheckSwitch_checkedItem,
                    1
                ) == 1
            ) view!!.item1 else view!!.item2

            checkedBg = a.getResourceId(
                R.styleable.CheckSwitch_checkedBg,
                0
            )

            checkedView!!.setBackgroundResource(checkedBg)
            checkedView!!.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.tick, 0)
        }

        view!!.item1.setOnClickListener { onItemClicked(view!!.item1, 1) }

        view!!.item2.setOnClickListener { onItemClicked(view!!.item2, 2) }

        addView(view)
        a.recycle()
    }

    /*override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)

    }*/

    fun changeSelection(item: Int) {
        onItemClicked(if (item == 1) view!!.item1 else view!!.item2, item)
    }

    private fun onItemClicked(view: TextView, item: Int) {
        if (isEnabled) {
            if (checkedView != view) {
                checkedView!!.setBackgroundResource(0)
                checkedView!!.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)

                view.setBackgroundResource(checkedBg)
                view.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.tick, 0)
                checkedView = view

                onChange?.invoke(item)
            }
        }
    }
}