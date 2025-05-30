package com.gtgt.pokerjacks.ui.game.view

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import androidx.lifecycle.Observer
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseFragment
import com.gtgt.pokerjacks.extensions.*
import com.gtgt.pokerjacks.ui.game.models.themes
import com.gtgt.pokerjacks.ui.game.viewModel.GameViewModel
import com.gtgt.pokerjacks.ui.game.viewModel.ThemesViewModel
import com.gtgt.pokerjacks.utils.Constants
import com.gtgt.pokerjacks.utils.loadImage
import kotlinx.android.synthetic.main.fragment_select_theme.*
import kotlinx.android.synthetic.main.theme_item.view.*


class SelectThemesFragment : BaseFragment() {
    private lateinit var selectedTheme: View
    private val themesViewModel: ThemesViewModel by sharedViewModel()
    private val gameViewModel: GameViewModel by sharedViewModel()

    private val enterAnim by lazy {
        AnimationUtils.loadAnimation(
            context,
            R.anim.top_enter
        )
    }
    private val leaveAnim by lazy {
        AnimationUtils.loadAnimation(
            context,
            R.anim.top_exit
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_select_theme, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val selectedThemeName = retrieveString(Constants.SELECTED_THEME, "blue")
        val selectedThemeIndex =
            themes.indexOfFirst { it.name == selectedThemeName }.let { if (it == -1) 0 else it }

        themes.forEachIndexed { index, theme ->
            val themeItem = LayoutInflater.from(context).inflate(R.layout.theme_item, null)
            if (!gameViewModel.isLandscape) {
                val parms: FrameLayout.LayoutParams =
                    FrameLayout.LayoutParams(dpToPx(80), dpToPx(74))
                themeItem.card_theme.setLayoutParams(parms)
            }
            themesLL.addView(themeItem)
            themeItem.apply {
//                main.backgroundTintList = ColorStateList.valueOf(theme.light)

                bg.loadImage(theme.bg)
                if (gameViewModel.isLandscape) {
                    table.loadImage(theme.landscapeTable)
                } else {
                    table.loadImage(theme.portraitTable)
                }

                margins(right = 15)

                if (index == selectedThemeIndex) {
                    selectedTheme = themeItem
                }

                setOnClickListener {
                    if (selectedTheme != it) {
                        selectedTheme.selected.visibility = GONE
                        selectedTheme = it
                        selectedTheme.selected.visibility = VISIBLE
                        themesViewModel.theme = theme
                    }
                }
            }
        }

        selectedTheme.selected.visibility = VISIBLE
        themesViewModel.theme = themes[selectedThemeIndex]

        themesViewModel.openThemeSwitcher.observe(viewLifecycleOwner, Observer { openThemeSwitcher ->
            if(openThemeSwitcher){
                root.visibility = VISIBLE
                root.startAnimation(enterAnim)
            }
        })
        close.onOneClick {
            root.startAnimation(leaveAnim)
            themesViewModel.openThemeSwitcher(false)
            Handler().postDelayed({
                root.visibility = GONE
            }, 500)
        }
    }

}