package com.gtgt.pokerjacks.ui.game.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.devs.vectorchildfinder.VectorChildFinder
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseFragment
import com.gtgt.pokerjacks.extensions.*
import com.gtgt.pokerjacks.ui.game.GameActivity
import com.gtgt.pokerjacks.ui.game.viewModel.GamePreferencesViewModel
import com.gtgt.pokerjacks.ui.game.viewModel.ThemesViewModel
import kotlinx.android.synthetic.main.fragment_game_prefs.*

class GamePreferencesFragment : BaseFragment() {
    var type: String = "menu"
        set(value) {
            field = value

            if (value == "settings") {
                settings.visibility = VISIBLE
                menu.visibility = INVISIBLE

                radioMenu.visibility = VISIBLE
                prefrencesMenu.visibility = GONE

            } else {
                settings.visibility = INVISIBLE
                menu.visibility = VISIBLE

                radioMenu.visibility = GONE
                prefrencesMenu.visibility = VISIBLE
            }
        }

    private val themesVm: ThemesViewModel by sharedViewModel()
    private val vm: GamePreferencesViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_game_prefs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        color_deck.setOnClickListener {
            isColorDeckEnabled = color_deck.toggle()
        }

        theme_menu.onOneClick {
            activity?.onBackPressed()
            themesVm.openThemeSwitcher()
        }

        exit_lobby.onOneClick {
            (activity as GameActivity)?.exitTOLobby()
        }

        soundPref.setOnClickListener {
            vm.vibrate = soundPref.toggle()
        }

        vibratePref.setOnClickListener {
            vm.vibrate = vibratePref.toggle()
        }

        val settingsVector = VectorChildFinder(context, R.drawable.settings, settings)
        val menuVector = VectorChildFinder(context, R.drawable.menu, menu)
        themesVm.onThemeSelected.observe(viewLifecycleOwner, Observer { theme ->
            if (theme != null) {
                exit_lobby.iconBg = theme.iconSolidDrawable
                soundPref.iconBg = theme.iconSolidDrawable
                vibratePref.iconBg = theme.iconSolidDrawable
                theme_menu.iconBg = theme.iconSolidDrawable
                refill_menu.iconBg = theme.iconSolidDrawable

                color_deck.iconBg = theme.iconSolidDrawable
                auto_muck.iconBg = theme.iconSolidDrawable
                auto_Post_BB.iconBg = theme.iconSolidDrawable
                auto_rotate.iconBg = theme.iconSolidDrawable
                hand_Strength.iconBg = theme.iconSolidDrawable

                menuVector.changePathStrokeColor("stroke", theme.btn2)
                menu.invalidate()

                settingsVector.changePathStrokeColor("stroke", theme.btn2)
                settings.invalidate()
            }
        })
    }
}