package com.gtgt.pokerjacks.ui.game.viewModel

import androidx.lifecycle.MutableLiveData
import com.github.salomonbrys.kotson.*
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.gtgt.pokerjacks.base.AnyModel
import com.gtgt.pokerjacks.extensions.*
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
    SIT_OUT("SIT_OUT"),
    ACTIVE("ACTIVE")
}

enum class AutoGameAction(val action: String) {
    AUTO_FOLD("AUTO_FOLD"),
    AUTO_CALL("AUTO_CALL"),
    AUTO_CHECK("AUTO_CHECK"),
    AUTO_CHECK_FOLD("AUTO_CHECK_FOLD"),
    AUTO_CALL_ANY("AUTO_CALL_ANY"),
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

    val userDetailsLD = MutableLiveData<JoinModel.UserDetails>()

    val tableSlotsLD = MutableLiveData<List<TableSlot>>()
    val gameTriggerLD = MutableLiveData<GameModel.GameDetails>()
    val gameDetailsLD = MutableLiveData<GameModel.GameDetails>()
    val userContestDetailsLD = MutableLiveData<GameModel.UserContestDetails>()
    val playerTurnLD = MutableLiveData<PlayerTurn>()
    val dealCommunityCardsLD = MutableLiveData<DealCommunityCards>()
    val leaderboardLD = MutableLiveData<Leaderboard>()
    val iamBackLD = MutableLiveData<Boolean>()
    val enableActions = MutableLiveData<Boolean>()

    var tableId: String = ""
        set(value) {
            field = value

            on<JsonElement>("gameTrigger") {
                coloredImages.clear()
                gameTriggerLD.data = it["data"]["gameDetails"].to()
            }
            on<JsonElement>("gameStart") {

                val gameInfo = it["data"].to<GameModel.Info>()

                val mySlot = gameInfo.tableSlots.find { it.user_unique_id == userId }

                if (mySlot != null && mySlot.status == SeatStatus.SIT_OUT.status
                    && mySlot.sitout_details["game_id"].string != gameInfo.gameDetails._id
                ) {
                    iamBackLD.data = true
                }

                val gameDetails = it["data"]["gameDetails"].to<GameModel.GameDetails>()

                emit<AnyModel>("getUserCards", jsonObject("table_id" to tableId)) {

                    if (!it!!.info["userContestDetails"].isJsonNull) {
                        userContestDetailsLD.data = it.info["userContestDetails"].to()
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

                try {
                    val previousCommunityCards = dealCommunityCardsLD.data
                    if (previousCommunityCards != null
                        && previousCommunityCards.card_1 == leaderboard.community_cards.card_1
                        && previousCommunityCards.card_2 == leaderboard.community_cards.card_2
                        && previousCommunityCards.card_3 == leaderboard.community_cards.card_3
                        && previousCommunityCards.card_4 == leaderboard.community_cards.card_4
                        && previousCommunityCards.card_5 == leaderboard.community_cards.card_5
                    ) {

                    } else {
                        dealCommunityCardsLD.data = leaderboard.community_cards
                    }
                } catch (ex: Exception) {
                    dealCommunityCardsLD.data = leaderboard.community_cards
                }


                socketIO.socketHandler.postDelayed({
                    leaderboardLD.data = leaderboard
                }, 1000)
            }

            on<JsonElement>("dealCommunityCards") {
                val community_cards: DealCommunityCards = it["community_cards"].to()
                try {
                    val previousCommunityCards = dealCommunityCardsLD.data
                    if (previousCommunityCards != null
                        && previousCommunityCards.card_1 == community_cards.card_1
                        && previousCommunityCards.card_2 == community_cards.card_2
                        && previousCommunityCards.card_3 == community_cards.card_3
                        && previousCommunityCards.card_4 == community_cards.card_4
                        && previousCommunityCards.card_5 == community_cards.card_5
                    ) {

                    } else {
                        dealCommunityCardsLD.data = community_cards
                    }
                } catch (ex: Exception) {
                    dealCommunityCardsLD.data = community_cards
                }
            }

            on<JsonElement>("gameEvent") {
                val data = it["data"]

                when (it["event"].string) {
                    "endGame" -> {
                        val gameInfo = data.to<GameModel.Info>()

                        handleTableSlots(gameInfo.tableSlots, gameInfo.gameUsers)

                        if (gameInfo.canCreateNewGame) {

                        } else {
                            iamBackLD.data = true
                        }

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

    fun updateUserGameSettings(setting: JsonObject) {
        emit<AnyModel>(
            "updateUserGameSettings", jsonObject(
                "settings_details" to setting
            )
        ) {
            if (it?.success == true) {
                setting.forEach { key, value ->
                    when (key) {
                        "auto_muck" -> {
                            userDetailsLD.value?.let {
                                it.auto_muck = value.bool
                            }
                        }
                        "auto_next_game" -> {
                            userDetailsLD.value?.let {
                                it.auto_next_game = value.bool
                            }
                        }
                        "hand_strength" -> {
                            userDetailsLD.value?.let {
                                it.hand_strength = value.bool
                            }
                        }
                    }
                }
                userDetailsLD.notify()
            } else {
                activity?.showSnack(it?.description ?: "Error in changing setting")
            }
        }
    }

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
            mySlot = slots.firstOrNull { it.user_unique_id == userId }
            slots.forEach { slot ->
                slot.user = gameUsers.firstOrNull { it.user_unique_id == slot.user_unique_id }
            }
        }

        tableSlotsLD.data = slots
    }

    var connectionData: JsonElement? = null
    fun connectTable() {
        emit<JsonElement>("connectTable", jsonObject("table_id" to tableId)) {
            connectionData = it

            if (it == null) {
                activity?.showSnack("Error in connecting to table")
            } else {
                coloredImages.clear()

                val data = it.to<GameModel>()

                if (data.success) {
                    val gameInfo = data.info
                    gameTriggerLD.data = gameInfo.gameDetails
                    userContestDetailsLD.data = gameInfo.userContestDetails

                    handleTableSlots(gameInfo.tableSlots, gameInfo.gameUsers)
                    socketIO.socketHandler.postDelayed({
                        playerTurnLD.data = gameInfo.playerTurn
                        gameDetailsLD.data = gameInfo.gameDetails
                        if (gameInfo.leaderboard != null)
                            leaderboardLD.data = gameInfo.leaderboard
                    }, 300)
                } else {
                    activity?.showSnack(data.description)
                }
            }
        }
    }

    fun getHandStrength() {
        emit<JsonElement>(
            "getHandStrength", jsonObject(
                "table_id" to tableId,
                "game_id" to gameDetailsLD.data!!._id
            )
        )
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
        callback: ChannelCallbackType<JsonElement?>
    ) {
        emit(
            "autoGameAction",
            jsonObject(
                "table_id" to tableId,
                "game_id" to gameDetailsLD.data!!._id,
                "action_details" to jsonObject(
                    "action" to action.action,
                    "enable" to true
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
        socketIO.socketHandler.postDelayed({ enableActions.data = true }, 5000)
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