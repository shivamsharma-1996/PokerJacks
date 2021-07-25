package com.gtgt.pokerjacks.ui.game.models

import com.gtgt.pokerjacks.base.BaseModel

data class PreviousHands(val info: Info): BaseModel(){
    data class Info(
        val gamesList: List<String>
    )
}

