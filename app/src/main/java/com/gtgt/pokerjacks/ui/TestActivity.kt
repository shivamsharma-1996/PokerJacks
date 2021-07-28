package com.gtgt.pokerjacks.ui

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.FullScreenScreenOnActivity
import com.gtgt.pokerjacks.extensions.onOneClick
import com.gtgt.pokerjacks.extensions.replaceFragment
import com.gtgt.pokerjacks.extensions.viewModel
import com.gtgt.pokerjacks.ui.game.view.GamePreferencesFragment
import com.gtgt.pokerjacks.ui.game.view.SelectThemesFragment
import com.gtgt.pokerjacks.ui.game.viewModel.ThemesViewModel
import com.gtgt.pokerjacks.utils.loadImage
import kotlinx.android.synthetic.main.activity_game.*

class TestActivity : FullScreenScreenOnActivity() {
    private val themesViewModel: ThemesViewModel by viewModel()
    private val gamePreferencesFragment by lazy { GamePreferencesFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        replaceFragment(gamePreferencesFragment, R.id.settingsFragment)
        replaceFragment(SelectThemesFragment(), R.id.themeSelectFragment)

        settings.onOneClick {
            gamePreferencesFragment.type = "settings"
            openRightDrawer()
        }

        menu.onOneClick {
            gamePreferencesFragment.type = "menu"
            openRightDrawer()
        }

        themesViewModel.onThemeSelected.observe(this, Observer { theme ->
            if (theme != null) {
                rootLayout.setBackgroundResource(theme.bg)
                ivTable.loadImage(theme.landscapeTable)
                //gameInfoIv.imageTintList = ColorStateList.valueOf(theme.dark)
            }
        })
    }

    private fun openRightDrawer() {
        if (drawer_layout.isDrawerOpen(GravityCompat.END)) {
            drawer_layout.closeDrawer(GravityCompat.END)
        } else {
            drawer_layout.openDrawer(GravityCompat.END)
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.END)) {
            drawer_layout.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }

}