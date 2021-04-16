package com.gtgt.pokerjacks.ui.game.view

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.devs.vectorchildfinder.VectorChildFinder
import com.github.salomonbrys.kotson.jsonObject
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseFragment
import com.gtgt.pokerjacks.extensions.changePathStrokeColor
import com.gtgt.pokerjacks.extensions.isColorDeckEnabled
import com.gtgt.pokerjacks.extensions.onOneClick
import com.gtgt.pokerjacks.extensions.sharedViewModel
import com.gtgt.pokerjacks.ui.game.GameActivity
import com.gtgt.pokerjacks.ui.game.viewModel.GamePreferencesViewModel
import com.gtgt.pokerjacks.ui.game.viewModel.GameViewModel
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
    private val gameVm: GameViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_game_prefs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!vm.playSound) {
            soundPref.toggle()
        }
        if (!vm.vibrate) {
            vibratePref.toggle()
        }
        if (!vm.autoRotate) {
            autorotatePref.toggle()
        }
        gameVm.isAutoRotateOn.value = vm.autoRotate
        gameVm.userDetailsLD.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                if (auto_muck.isOn != it.auto_muck) {
                    auto_muck.toggle()
                }

                if (auto_Post_BB.isOn != it.auto_next_game) {
                    auto_Post_BB.toggle()
                }

                if (hand_Strength.isOn != it.hand_strength) {
                    hand_Strength.toggle()
                }
            }
        })

        auto_muck.onOneClick {
            gameVm.updateUserGameSettings(jsonObject("auto_muck" to !auto_muck.isOn))
        }

        auto_Post_BB.onOneClick {
            gameVm.updateUserGameSettings(jsonObject("auto_next_game" to !auto_Post_BB.isOn))
        }

        hand_Strength.onOneClick {
            gameVm.updateUserGameSettings(jsonObject("hand_strength" to !hand_Strength.isOn))
        }

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
            //vm.vibrate = soundPref.toggle()
            vm.playSound = soundPref.toggle()
        }

        vibratePref.setOnClickListener {
            vm.vibrate = vibratePref.toggle()
        }

        autorotatePref.setOnClickListener {
            vm.autoRotate = autorotatePref.toggle()
            gameVm.isAutoRotateOn.value = vm.autoRotate
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
                autorotatePref.iconBg = theme.iconSolidDrawable

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

/*    private fun updateUI() {
        gameVm.currentOrientation.observe(viewLifecycleOwner, Observer { isLandscape ->
            if(isLandscape){
                orientationPref.setTitle("Portrait")
            }else{
                orientationPref.setTitle("Landscape")
            }
        })
    }*/
}