package com.gtgt.pokerjacks.ui.game.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gtgt.pokerjacks.base.BaseViewModel
import com.gtgt.pokerjacks.extensions.putString
import com.gtgt.pokerjacks.ui.game.models.Theme
import com.gtgt.pokerjacks.utils.Constants

var selectedTheme: Theme? = null

class ThemesViewModel : BaseViewModel() {
    private val _onThemeSelected: MutableLiveData<Theme> = MutableLiveData()
    val onThemeSelected: LiveData<Theme> = _onThemeSelected

    private val _openThemeSwitcher: MutableLiveData<Boolean> = MutableLiveData()
    val openThemeSwitcher: LiveData<Boolean> = _openThemeSwitcher

    var theme: Theme? = null
        set(value) {
            field = value
            _onThemeSelected.value = theme
            selectedTheme = value
            putString(Constants.SELECTED_THEME, value!!.name)
        }

    fun openThemeSwitcher(doOpen: Boolean) {
        _openThemeSwitcher.value = doOpen
    }
}