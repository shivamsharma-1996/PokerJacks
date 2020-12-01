package com.gtgt.pokerjacks.ui.lobby.viewmodel

import androidx.lifecycle.MutableLiveData
import com.gtgt.pokerjacks.base.BaseViewModel
import com.gtgt.pokerjacks.extensions.execute
import com.gtgt.pokerjacks.extensions.showSnack
import com.gtgt.pokerjacks.ui.lobby.model.LobbyTables

class LobbyViewModel : BaseViewModel() {
    val lobbyTables = MutableLiveData<LobbyTables>()

    var playersAllTables = listOf<LobbyTables.Info>()
    var players9Tables = listOf<LobbyTables.Info>()
    var players6Tables = listOf<LobbyTables.Info>()
    var players2Tables = listOf<LobbyTables.Info>()

    fun getActiveTables() {
        apiServicesTableManager.getActiveTables().execute(activity) {
            if (it.success) {
                playersAllTables = it.info
                players9Tables = it.info.filter { it.plan_details.max_players == 9 }
                players6Tables = it.info.filter { it.plan_details.max_players == 6 }
                players2Tables = it.info.filter { it.plan_details.max_players == 2 }
                lobbyTables.value = it
            } else {
                activity?.showSnack(it.description)
            }
        }
    }
}