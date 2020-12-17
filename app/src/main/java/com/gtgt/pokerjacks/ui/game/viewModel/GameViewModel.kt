package com.gtgt.pokerjacks.ui.game.viewModel

import androidx.lifecycle.MutableLiveData
import com.github.salomonbrys.kotson.get
import com.github.salomonbrys.kotson.jsonObject
import com.github.salomonbrys.kotson.string
import com.google.gson.JsonElement
import com.gtgt.pokerjacks.base.AnyModel
import com.gtgt.pokerjacks.extensions.data
import com.gtgt.pokerjacks.extensions.retrieveString
import com.gtgt.pokerjacks.extensions.showSnack
import com.gtgt.pokerjacks.extensions.to
import com.gtgt.pokerjacks.socket.ChannelCallbackType
import com.gtgt.pokerjacks.socket.SocketIOViewModel
import com.gtgt.pokerjacks.ui.game.models.*
import com.gtgt.pokerjacks.ui.game.view.slot.slotPositionMap
import com.gtgt.pokerjacks.ui.game.view.slot.slots2Positions
import com.gtgt.pokerjacks.ui.game.view.slot.slots6PositionsTable
import com.gtgt.pokerjacks.ui.game.view.slot.slots9Positions
import com.gtgt.pokerjacks.utils.SlotPositions

enum class ActionEvent(val event: String) {
    FOLD("foldCards"),
    CHECK_BET("checkBet"),
    CALL_BET("callBet"),
    RAISE_BET("raiseBet"),
    ALL_IN("allIn")
}

enum class SeatStatus(val status: String) {
    WAIT_FOR_NEXT("WAIT_FOR_NEXT"),
    WAIT_FOR_BB("WAIT_FOR_BB"),
    SIT_OUT("SIT_OUT")
}

enum class AutoGameAction(val action: String) {
    AUTO_FOLD("AUTO_FOLD"),
    AUTO_CALL("AUTO_CALL"),
    AUTO_CHECK("AUTO_CHECK"),
    AUTO_CALL_ANY("AUTO_CALL_ANY"),
    AUTO_CHECK_FOLD("AUTO_CHECK_FOLD"),
}

enum class PlayerActions(val action: String) {
    CHECK("CHECK"),
    RAISE("RAISE"),
    ALL_IN("ALL_IN"),
    FOLD("FOLD"),
    CALL("CALL")
}

class GameViewModel : SocketIOViewModel() {
    val userId = retrieveString("USER_ID")

    val tableSlotsLD = MutableLiveData<List<TableSlot>>()
    val gameTriggerLD = MutableLiveData<GameModel.GameDetails>()
    val gameDetailsLD = MutableLiveData<GameModel.GameDetails>()
    val userContestDetailsLD = MutableLiveData<GameModel.UserContestDetails>()
    val playerTurnLD = MutableLiveData<PlayerTurn>()
    val dealCommunityCardsLD = MutableLiveData<DealCommunityCards>()
    val leaderboardLD = MutableLiveData<Leaderboard>()

    var tableId: String = ""
        set(value) {
            field = value

            on<JsonElement>("gameTrigger") {
                gameTriggerLD.data = it["data"]["gameDetails"].to()
            }
            on<JsonElement>("gameStart") {
                val gameDetails = it["data"]["gameDetails"].to<GameModel.GameDetails>()

                emit<AnyModel>("getUserCards", jsonObject("table_id" to tableId)) {

                    if (!it!!.info["userContestDetails"].isJsonNull) {
                        userContestDetailsLD.data = it!!.info["userContestDetails"].to()
                    }
                    gameDetailsLD.data = gameDetails

                    playerTurnLD.data = it.info["playerTurn"].to()
                }
            }

            on<TablePlayers>("tablePlayers") {
                handleTableSlots(it.tableSlots, it.gameUsers)
            }

            on<JsonElement>("leaderboard") {
                val leaderboard = it["data"].to<Leaderboard>()
                dealCommunityCardsLD.data = leaderboard.community_cards
                leaderboardLD.data = leaderboard
            }

            on<JsonElement>("dealCommunityCards") {
                dealCommunityCardsLD.data = it["community_cards"].to()
            }

            on<JsonElement>("gameEvent") {
                val data = it["data"]

                when (it["event"].string) {
                    "endGame" -> {
                        playerTurnLD.data = null
                        activity?.showSnack("Game is ended")
                    }
                }
            }

            on<JsonElement>("playerTurn") {
                playerTurnLD.data = it["data"].to()
            }

            connectTable()
        }


    var selectedSlotPositions: List<SlotPositions>? = null

    private fun handleTableSlots(slots: List<TableSlot>, gameUsers: List<GameUser>) {
        if (selectedSlotPositions.isNullOrEmpty()) {
            selectedSlotPositions = when (slots.size) {
                9 -> slots9Positions
                6 -> slots6PositionsTable
                else -> slots2Positions
            }
        }

        me = gameUsers.find { it.user_unique_id == userId }

        if (mySlot == null) {
            mySlot = slots.firstOrNull { it.user_unique_id == userId }

            var currentSlot = mySlot ?: slots[0]

            slots.forEachIndexed { i, slot ->
                slot.user = gameUsers.firstOrNull { it.user_unique_id == slot.user_unique_id }

                slotPositionMap[currentSlot.seat_no] = selectedSlotPositions!![i]
                currentSlot = currentSlot.next(slots)
            }
        } else {
            slots.forEach { slot ->
                slot.user = gameUsers.firstOrNull { it.user_unique_id == slot.user_unique_id }
            }
        }

        tableSlotsLD.data = slots
    }

    var connectionData:GameModel? = null
    fun connectTable() {
        emit<GameModel>("connectTable", jsonObject("table_id" to tableId)) {
            connectionData = it

            if (it == null) {
                activity?.showSnack("Error in connecting to table")
            } else {
                if (it.success) {
                    val gameInfo = it.info
                    gameTriggerLD.data = gameInfo.gameDetails
                    userContestDetailsLD.data = gameInfo.userContestDetails

                    handleTableSlots(gameInfo.tableSlots, gameInfo.gameUsers)
                    socketIO.socketHandler.postDelayed({
                        playerTurnLD.data = gameInfo.playerTurn
                        gameDetailsLD.data = gameInfo.gameDetails
                    }, 300)
                } else {
                    activity?.showSnack(it.description)
                }
            }
        }
    }

    fun updateSeatStatus(status: SeatStatus, callback: ChannelCallbackType<JsonElement?>) {
        emit(
            "updateSeatStatus",
            jsonObject("table_id" to tableId, "update_status" to status.status),
            callback = callback
        )
    }

    fun autoGameAction(
        action: AutoGameAction,
        enable: Boolean,
        callback: ChannelCallbackType<JsonElement?>
    ) {
        emit(
            "autoGameAction",
            jsonObject(
                "table_id" to tableId,
                "game_id" to gameDetailsLD.data!!._id,
                "action_details" to jsonObject(
                    "action" to action.action,
                    "enable" to enable
                )
            ),
            callback = callback
        )
    }

    fun actionEvent(
        event: ActionEvent,
        total_amount: Double? = null,
        raise_amount: Double? = null,
        callback: ChannelCallbackType<JsonElement?>
    ) {
        emit(
            event.event,
            jsonObject("table_id" to tableId, "game_id" to gameDetailsLD.data!!._id).apply {
                total_amount?.let { addProperty("total_amount", it) }
                raise_amount?.let { addProperty("raise_amount", it) }
            },
            callback = callback
        )
    }

    fun leaveTable() {
        emit<JsonElement>("leaveTable", jsonObject("table_id" to tableId)) {

        }
    }

    fun resetGame() {

    }

    var mySlot: TableSlot? = null
    var me: GameUser? = null
}