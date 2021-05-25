package com.gtgt.pokerjacks.ui.game.viewModel

import android.content.res.Configuration
import android.widget.CheckBox
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.salomonbrys.kotson.*
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.gtgt.pokerjacks.base.AnyModel
import com.gtgt.pokerjacks.extensions.*
import com.gtgt.pokerjacks.socket.ChannelCallbackType
import com.gtgt.pokerjacks.socket.SocketIOViewModel
import com.gtgt.pokerjacks.ui.game.models.*
import com.gtgt.pokerjacks.ui.game.view.slot.*
import com.gtgt.pokerjacks.utils.Event
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
    SIT_OUT_NEXT("SIT_OUT_NEXT"),
    ACTIVE("ACTIVE")
}

enum class AutoGameAction(val action: String) {
//    AUTO_FOLD("AUTO_FOLD"),
//    AUTO_CALL("AUTO_CALL"),
//AUTO_CALL_ANY("AUTO_CALL_ANY"),
    AUTO_FOLD_CHECK("AUTO_FOLD_CHECK"),
    AUTO_CHECK("AUTO_CHECK"),
    AUTO_CALLANY_CHECK("AUTO_CALLANY_CHECK"),
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
    val leaderboardLD = MutableLiveData<Event<Leaderboard>>()
    val iamBackLD = MutableLiveData<Boolean>()
    val enableActions = MutableLiveData<Boolean>()
    var tableSlots : List<TableSlot>? = null
    var gameUsers : List<GameUser>? = null
    var totalPotAmount : String? = null
    var isCommunityCardsOpened : Boolean = false
    var isAutoRotateOn = MutableLiveData<Boolean>()
    var autoActionView: CheckBox? = null
    private var _isGameEnded = MutableLiveData<Boolean>()
    val isGameEnded : LiveData<Boolean>
      get() = _isGameEnded
    var refillNextInPlayAmount : Boolean = false
    var _refillInPlayAmount = MutableLiveData<Boolean>()
    val refillInPlayAmount : LiveData<Boolean>
        get() = _refillInPlayAmount
    //var isConfigurationChanged = false
    var isDealCommunityCardsEventReceived = false
    var previousOrientation = Configuration.ORIENTATION_LANDSCAPE
    var gameCountdownTimeLeft : Long = 0
    var isLandscape: Boolean = false
    set(value) {
        field = value
        selectedSlotPositions?.let {
            if(tableSlots!=null && gameUsers!=null){
                handleTableSlots(tableSlots!!, gameUsers!!)
            }
        }
    }
    var currentTableId: String = ""
    var currentGameId: String = ""

    var tableId: String = ""
        set(value) {
            field = value

            on<JsonElement>("gameTrigger") {
                coloredImages.clear()
                _isGameEnded.postValue(false)
                gameTriggerLD.data = it["data"]["gameDetails"].to()
                log("poker:gameEvent", "gameTrigger "+ it["data"]["gameDetails"].to())

                currentGameId = gameTriggerLD.data!!._id

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
                log("poker:gameEvent", "gameStart "+ gameDetails)

                emit<AnyModel>("getUserCards", jsonObject("table_id" to tableId)) {

                    if (!it!!.info["userContestDetails"].isJsonNull) {
                        userContestDetailsLD.data = it.info["userContestDetails"].to()
                    }
                    gameDetailsLD.data = gameDetails

                    playerTurnLD.data = it.info["playerTurn"].to()
                }
            }

            on<TablePlayers>("tablePlayers") {
                log("poker::tablePlayers", "tablePlayers")
                tableSlots = it.tableSlots
                gameUsers = it.gameUsers
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
                    leaderboardLD.data = Event(leaderboard)
                }, 1000)
            }

            on<JsonElement>("dealCommunityCards") {
                val community_cards: DealCommunityCards = it["community_cards"].to()
                community_cards.total_pot_value = it["total_pot_value"].double
                community_cards.side_pots = it["side_pots"].to()
                isDealCommunityCardsEventReceived = true
                //isConfigurationChanged = false
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

           /* on<JsonElement>("refill") {
                val data = it["data"]
                _refillInPlayAmount.postValue(true)
                log("poker::refill", it)
                    //show buyin popup here
            }*/

            on<JsonElement>("gameEvent") {
                val data = it["data"]

                when (it["event"].string) {
                    "endGame" -> {
                        val gameInfo = data.to<GameModel.Info>()
                        tableSlots = gameInfo.tableSlots
                        gameUsers = gameInfo.gameUsers
                        handleTableSlots(gameInfo.tableSlots, gameInfo.gameUsers)
                        _isGameEnded.postValue(true)

                        resetVmResources()

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

    private fun resetVmResources() {
        gameCountdownTimeLeft = 0L
        isCommunityCardsOpened = false
        dealCommunityCardsLD.postValue(null)
        gameDetailsLD.postValue(null)
        userContestDetailsLD.postValue(null)
        tableSlotsLD.postValue(null)
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
        setSelectedSlotPositions(slots.size)

        me = gameUsers.find { it.user_unique_id == userId }

        val currentSlotPositionMap = if(slots.size == 6 && !isLandscape){
            slotPosition6TableMap
        }else{
            slotPositionMap
        }
        if (mySlot == null || (slots.size == 6 && !isLandscape && !isSlot6PostitionMapInitialized)){
            mySlot = slots.firstOrNull { it.user_unique_id == userId }

            if(slots.size == 6 && !isLandscape){
                isSlot6PostitionMapInitialized = true
            }
            var currentSlot = mySlot ?: slots[0]

            slots.forEachIndexed { i, slot ->
                slot.user = gameUsers.firstOrNull { it.user_unique_id == slot.user_unique_id }
                currentSlotPositionMap[currentSlot.seat_no] = selectedSlotPositions!![i]
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

    private fun setSelectedSlotPositions(size: Int) {
        selectedSlotPositions =  when (size) {
            9 -> slots9Positions
            6 -> if(isLandscape){
                log("poker::handleTableSlots" , "isLandscape : " +isLandscape)

                slots6PositionsTable
            }else{
                log("poker::handleTableSlots" , "isLandscape : " +isLandscape)
                slots6PortraitPositionsTable
            }
            else -> slots2Positions
        }
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
                    log("gameInfo", "gameInfo: " + gameInfo)
                    if(gameInfo.gameDetails!=null){
                        if(!gameInfo.gameDetails._id.equals(currentGameId)){
                            if(currentTableId.isEmpty()){
                                //here, if there is any ongoing game, then user will be able to resume
                                restoreGame(gameInfo)
                            }
                            //here, new game-timer is going to trigger
                            gameInfo.gameDetails._id.let {
                                gameTriggerLD.data = gameInfo.gameDetails
                                currentTableId = gameInfo.gameDetails._id
                                gameCountdownTimeLeft = 0L
                            }
                        }else{
                            restoreGame(gameInfo)
                        }
                    }
                    tableSlots = gameInfo.tableSlots
                    gameUsers = gameInfo.gameUsers
                    handleTableSlots(gameInfo.tableSlots, gameInfo.gameUsers)
                    currentTableId = tableId
                } else {
                    activity?.showSnack(data.description)
                }
            }
        }
    }

    fun restoreGame(gameInfo: GameModel.Info) {
        if(gameCountdownTimeLeft==0L){
            gameDetailsLD.data = gameInfo.gameDetails
        }
        userContestDetailsLD.data = gameInfo.userContestDetails
        socketIO.socketHandler.delayedHandler(300) {
            if (gameInfo.leaderboard != null)
                leaderboardLD.data = Event(gameInfo.leaderboard)
            playerTurnLD.data = gameInfo.playerTurn
        }
    }

    fun getHandStrength() {
        emit<JsonElement>(
            "getHandStrength", jsonObject(
                "table_id" to tableId,
                "game_id" to gameDetailsLD.data?.let {
                    gameDetailsLD.data!!._id
                }
            )
        )
    }

    fun refillInPlayAmount(refillAmount: Double, callback: ChannelCallbackType<JsonElement?>) {
        emit(
            "refill",
            jsonObject("amount" to refillAmount, "table_id" to tableId, "join_id" to mySlot!!.join_id),
            callback = callback
        )
    }

    fun refillNextInPlayAmount(refillAmount: Double, callback: ChannelCallbackType<JsonElement?>) {
        emit(
            "refillNext",
            jsonObject("amount" to refillAmount, "table_id" to tableId, "join_id" to mySlot!!.join_id, "game_id" to gameDetailsLD.data?.let {
                gameDetailsLD.data!!._id
            }),
            callback = callback
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
        enable: Boolean = true,
        callback: ChannelCallbackType<JsonElement?>
    ) {
        log("poker::autoGameAction", "enable : $enable")
        emit(
            "autoGameAction",
            jsonObject(
                "table_id" to tableId,
                "game_id" to gameDetailsLD?.data!!._id,
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

    fun resetRefillInPlayAmount(){
        _refillInPlayAmount.postValue(false)
    }
    var mySlot: TableSlot? = null
    var isSlot6PostitionMapInitialized = false
    var me: GameUser? = null
}