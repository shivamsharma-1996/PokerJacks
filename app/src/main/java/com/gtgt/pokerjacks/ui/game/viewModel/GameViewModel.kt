package com.gtgt.pokerjacks.ui.game.viewModel

import com.gtgt.pokerjacks.extensions.retrieveString
import com.gtgt.pokerjacks.socket.SocketIOViewModel
import com.gtgt.pokerjacks.ui.game.models.TableSlot
import com.gtgt.pokerjacks.ui.game.models.tableSlots

class GameViewModel : SocketIOViewModel() {
    private val userId = retrieveString("USER_ID")


    val mySlot: TableSlot?
        get() = tableSlots.firstOrNull { it.user_unique_id == userId }
}