package com.gtgt.pokerjacks.ui.game.viewModel

import androidx.lifecycle.MutableLiveData
import com.gtgt.pokerjacks.base.BaseViewModel
import com.gtgt.pokerjacks.extensions.log
import com.gtgt.pokerjacks.extensions.putBoolean
import com.gtgt.pokerjacks.extensions.retrieveBoolean

class GamePreferencesViewModel : BaseViewModel() {
    var playSound: Boolean = false
        set(value) {
            field = value
            putBoolean("playSound", value)
            log("playSoundB", retrieveBoolean("playSound", true))
        }
        get() = retrieveBoolean("playSound", true)

    var vibrate = false
        set(value) {
            field = value
            putBoolean("vibrate", value)
        }
        get() = retrieveBoolean("vibrate", true)

    var colorDeck = false
        set(value) {
            field = value
            putBoolean("colorDeck", value)
        }
        get() = retrieveBoolean("colorDeck", false)

    var autoRotate = false
        set(value) {
            field = value
            putBoolean("autoRotate", value)
        }
        get() = retrieveBoolean("autoRotate", false)


    val exitVisibility: MutableLiveData<Int> = MutableLiveData()
}