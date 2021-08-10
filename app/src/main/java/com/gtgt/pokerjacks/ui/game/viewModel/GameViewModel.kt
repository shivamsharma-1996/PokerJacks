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
import com.gtgt.pokerjacks.utils.SlotPositions

enum class ActionEvent(val event: String) {
    FOLD("foldCards"),
    CHECK_BET("checkBet"),
    CALL_BET("callBet"),
    RAISE_BET("raiseBet"),
    ALL_IN("allIn")
}

enum class SlotsFilter(val event: String) {
    SEAT_STATUS("seatuStatus"),
    PLAYER_ACTION("playerAction"),
}

enum class SeatStatus(val status: String) {
    WAIT_FOR_NEXT("WAIT_FOR_NEXT"),
    WAIT_FOR_BB("WAIT_FOR_BB"),
    SIT_OUT("SIT_OUT"),
    SIT_OUT_NEXT("SIT_OUT_NEXT"),
    ACTIVE("ACTIVE")
}

enum class AutoGameAction(val action: String) {
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

    var currentTableId: String = ""
    var currentGameId: String = ""

    var isGameTriggered = false
    var isWaitingForOthersShown = false;
    var isFirstGameStarted = false
    var isTossEnabledForCurrentGame = false
    var canDisplayWaitingIcon = false

    //Flag vars(Starting): used to apply lock on LiveData observers while orientation changes
    var isDealCommunityCardsEventReceived = false
    var isPlayerEventReceived = false
    var isGameStartEventReceived = false
    var isLeaderBoardEventReceived = false
    //Flag vars(Ended)

    val userDetailsLD = MutableLiveData<UserDetails>()

    private var _previousHandsLD = MutableLiveData<List<String>>()
    val previousHandsLD : LiveData<List<String>>
        get() = _previousHandsLD

    private var _previousHandDetailsLD = MutableLiveData<PreviousHandDetails.Info>()
    val previousHandDetailsLD : LiveData<PreviousHandDetails.Info>
        get() = _previousHandDetailsLD

    private var _tableUserStatsLD = MutableLiveData<List<TableUserStatsItem>>()
    val tableUserStatsLD : LiveData<List<TableUserStatsItem>>
        get() = _tableUserStatsLD

    private var _tossEnabledLD = MutableLiveData<GameModel.GameDetails>()
    val tossEnabledLD : LiveData<GameModel.GameDetails>
        get() = _tossEnabledLD

    val tableSlotsLD = MutableLiveData<List<TableSlot>>()
    val gameTriggerLD = MutableLiveData<GameModel.GameDetails>()
    val gameDetailsLD = MutableLiveData<GameModel.GameDetails>()
    val userContestDetailsLD = MutableLiveData<GameModel.UserContestDetails>()
    val playerTurnLD = MutableLiveData<PlayerTurn>()
    val dealCommunityCardsLD = MutableLiveData<DealCommunityCards>()
    val userBestHandLd = MutableLiveData<UserBestHand>()
    val leaderboardLD = MutableLiveData<Leaderboard>()
    val iamBackLD = MutableLiveData<Boolean>()
    val enableActions = MutableLiveData<Boolean>()
    var tableSlots : List<TableSlot>? = null
    var gameUsers : List<GameUser>? = null
    var totalPotAmount : String? = null
    var isCommunityCardsOpened : Boolean = false
    var isAutoRotateOn = MutableLiveData<Boolean>()
    var currentAutoButton: CheckBox? = null
    private var _isGameEnded = MutableLiveData<Boolean>()
    val isGameEnded : LiveData<Boolean>
        get() = _isGameEnded

    private var _inPlayAmtOnGameEnd = MutableLiveData<List<TableSlot>>()
    val inPlayAmtOnGameEnd : LiveData<List<TableSlot>>
        get() = _inPlayAmtOnGameEnd

    var isHandStrengthEnabled : Boolean? = null

    var refillNextInPlayAmount : Boolean = false
    var _refillInPlayAmount = MutableLiveData<Boolean>()
    val refillInPlayAmount : LiveData<Boolean>
        get() = _refillInPlayAmount

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


    var tableId: String = ""
        set(value) {
            field = value

            //This is the initial event triggers when atLeast two players joined the table
            //Toss functionality depends upon the <gameDetails.toss_enabled> received here
            //if toss_enabled is true {
                //1) Toss cards along with the toss winner reveals upto 4000L millis
                //2) Once timer stops, getUserCards event emits to fetch the userContestDetailsLD & gameDetails
                //3) Later, gameTrigger event receives from BE to let the user know that game is going to start in N seconds.
            //} else {
            //      2) and 3) performs directly
            //}
            on<JsonElement>("gameStart") {
                isFirstGameStarted = true
                isGameStartEventReceived = true
                val gameInfo = it["data"].to<GameModel.Info>()

                val mySlot = gameInfo.tableSlots.find { it.user_unique_id == userId }

                if (mySlot != null && mySlot.status == SeatStatus.SIT_OUT.status
                    && mySlot.sitout_details["game_id"].string != gameInfo.gameDetails._id
                ) {
                    iamBackLD.data = true
                }

                val gameDetails = it["data"]["gameDetails"].to<GameModel.GameDetails>()
                if(gameDetails.toss_enabled){
                    isTossEnabledForCurrentGame = gameDetails.toss_enabled
                    _tossEnabledLD.data = gameDetails
                }else{
                    getUserCards()
                }
            }

            //this event triggers later getUserCards emit
            on<JsonElement>("gameTrigger") {
                coloredImages.clear()
                _isGameEnded.postValue(false)
                gameTriggerLD.data = it["data"]["gameDetails"].to()
                log("poker:gameEvent", "gameTrigger "+ it["data"]["gameDetails"].to())
                currentGameId = gameTriggerLD.data!!._id
            }

            //It will trigger each time when a player joins the table, playerTurn switches or status of any player gets changed
            on<TablePlayers>("tablePlayers") {
                log("poker::tablePlayers", "tablePlayers : " + Gson().toJson(it))
                tableSlots = it.tableSlots
                gameUsers = it.gameUsers
                handleTableSlots(it.tableSlots, it.gameUsers)
            }

            //this event triggers to display game winner declares before the 'gameEnd' event
            on<JsonElement>("leaderboard") {
                val leaderboard = it["data"].to<Leaderboard>()
                isLeaderBoardEventReceived = true
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

            //Triggers 3 times in a game to reveal the CommunityCards
            on<JsonElement>("dealCommunityCards") {
                val community_cards: DealCommunityCards = it["community_cards"].to()
                community_cards.total_pot_value = it["total_pot_value"].double
                community_cards.side_pots = it["side_pots"].to()
                isDealCommunityCardsEventReceived = true
                try {
                    dealCommunityCardsLD.data = community_cards
                } catch (ex: Exception) {
                    dealCommunityCardsLD.data = community_cards
                }
            }

            on<JsonElement>("gameEvent") {
                val data = it["data"]

                when (it["event"].string) {
                    "endGame" -> {
                        val gameInfo = data.to<GameModel.Info>()
                        tableSlots = gameInfo.tableSlots
                        _inPlayAmtOnGameEnd.data = gameInfo.tableSlots
                        gameUsers = gameInfo.gameUsers
                        _isGameEnded.postValue(true)

                        resetVmResources()

                        playerTurnLD.data = null
                    }
                }
            }

            //triggers when player's turn switches
            on<JsonElement>("playerTurn") {
                isPlayerEventReceived = true
                playerTurnLD.data = it["data"].to()
            }


            connectTable()
        }

    fun resetVmResources() {
        currentAutoButton = null
        gameCountdownTimeLeft = 0L
        isCommunityCardsOpened = false
        userDetailsLD.value?.let {
            it.auto_next_game = false
        }
        _tossEnabledLD.postValue(null)
        userDetailsLD.forceRefresh()
        dealCommunityCardsLD.postValue(null)
        gameDetailsLD.postValue(null)
        userContestDetailsLD.postValue(null)
        //tableSlotsLD.postValue(null)
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
                                isHandStrengthEnabled = value.bool
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

    var mySlot: TableSlot? = null
    var me: GameUser? = null
    var isSlot6PostitionPortraitMapInitialized = false
    var isSlot6PostitionLandscapeMapInitialized = false
    var isSlot9PostitionLandscapeMapInitialized = false
    var isSlotPositionsInitializedForWatchUser = false

    //This is the heart of handling the arrangement and positioning of the slots for,
    // 9/6 player tables in both (Landscape/Portrait) orientations
    private fun handleTableSlots(slots: List<TableSlot>, gameUsers: List<GameUser>) {
        setSelectedSlotPositions(slots.size)

        me = gameUsers.find { it.user_unique_id == userId }

        val currentSlotPositionMap = if(slots.size == 6 && !isLandscape){
            slotPosition6TableMap
        }else{
            slotPositionMap
        }

        isSlotPositionsInitializedForWatchUser = slots.firstOrNull { it.user_unique_id == userId } == null &&
                ((slots.size == 6  && slotPosition6TableMap.isNotEmpty()) ||
                        (slots.size == 9 && slotPositionMap.isNotEmpty()))

        if (mySlot == null || (slots.size == 6 &&
                    ((isLandscape && !isSlot6PostitionLandscapeMapInitialized) || (!isLandscape && !isSlot6PostitionPortraitMapInitialized)))){

            mySlot = slots.firstOrNull { it.user_unique_id == userId }
            if(mySlot == null && isGameTriggered){
                return
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

        if(slots.size == 6 && (mySlot != null)){
            setSlotPositionsFlag()
        }
        if(slots.size == 9 && mySlot != null){
            isSlot9PostitionLandscapeMapInitialized = true
        }

        tableSlotsLD.data = slots
    }

    private fun setSlotPositionsFlag() {
        if(isLandscape)
            isSlot6PostitionLandscapeMapInitialized = true
        else
            isSlot6PostitionPortraitMapInitialized = true
    }

    private fun setSelectedSlotPositions(size: Int) {
        selectedSlotPositions =  when (size) {
            9 -> slots9Positions
            6 -> if(isLandscape){
                slots6LandscapePositionsTable
            }else{
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

                    //function the restore the game State on each reconnection
                    restoreGame(gameInfo)

                    if(gameInfo.gameDetails!=null){
                        gameInfo.gameDetails._id.let {
                            currentTableId = gameInfo.gameDetails._id
                        }
                    }
                } else {
                    activity?.showSnack(data.description)
                }
            }
        }
    }

    private fun checkIsNewGameStarting(gameInfo: GameModel.Info): Boolean{
        gameInfo?.gameDetails?.let {
            return it.start_time > (System.currentTimeMillis() - timeDiffWithServer) || gameCountdownTimeLeft != 0L
        }
    }

    private fun restoreGame(gameInfo: GameModel.Info) {
        gameCountdownTimeLeft = 0L
        if (mySlot != null && mySlot!!.status == SeatStatus.SIT_OUT.status
            && (gameInfo.gameDetails!=null && mySlot!!.sitout_details["game_id"].string != gameInfo.gameDetails._id)
            || currentTableId.isEmpty()) {
            iamBackLD.data = true
        }
        if(gameInfo.gameDetails!=null){
            if(!checkIsNewGameStarting(gameInfo))
            gameDetailsLD.data = gameInfo.gameDetails
            else
            gameTriggerLD.data = gameInfo.gameDetails
        }

        userContestDetailsLD.data = gameInfo.userContestDetails
        socketIO.socketHandler.delayedHandler(300) {
            if (gameInfo.leaderboard != null)
                leaderboardLD.data = gameInfo.leaderboard
            playerTurnLD.data = gameInfo.playerTurn
        }
        tableSlots = gameInfo.tableSlots
        gameUsers = gameInfo.gameUsers
        gameInfo.userDetails?.let {
            log("userDetailsLD.data", userDetailsLD.data )
            userDetailsLD.data = it
        }
        handleTableSlots(gameInfo.tableSlots, gameInfo.gameUsers)
        currentTableId = tableId
    }

    fun getTableUserStats(){
        if(tableId.isNotEmpty())
        emit<AnyModel>(
            "getTableUserStats",
            jsonObject( "table_id" to tableId)
        ){
            if(it?.success == true && it.info.isJsonArray){
                val tableUserStatsList = it.info.to<List<TableUserStatsItem>>()
                log("getTableUserStats", tableUserStatsList)
                _tableUserStatsLD.data = tableUserStatsList
            }
        }
    }

    fun getPreviousHandsList(){
        if(tableId.isNotEmpty())
            emit<AnyModel>(
                "getPreviousHandsList",
                jsonObject( "table_id" to tableId)
            ){
                if(it?.success == true){
                    val gamesList = it.info.to<PreviousHands.Info>()
                    _previousHandsLD.data = gamesList.gamesList
                }
            }
    }

    fun getUserCards(){
        if(tableId.isNotEmpty())
        emit<AnyModel>("getUserCards", jsonObject("table_id" to tableId)) {
            if (!it!!.info["userContestDetails"].isJsonNull) {
                userContestDetailsLD.data = it.info["userContestDetails"].to()
            }
            if(!it!!.info["gameDetails"].isJsonNull)
            {
                val gameDetails = it.info["gameDetails"].to<GameModel.GameDetails>()
                gameDetailsLD.data = gameDetails
                isTossEnabledForCurrentGame = false
            }
            playerTurnLD.data = it.info["playerTurn"].to()
        }
    }

    fun getPreviousHandDetails(gameId: String){
        if(tableId.isNotEmpty() && gameId.isNotEmpty()){
            emit<AnyModel>(
                "getPreviousHandDetails",
                jsonObject( "table_id" to tableId,
                    "game_id" to gameId)
            ){
                if(it?.success == true){
                    val gameDetailsInfo = it.info.to<PreviousHandDetails.Info>()
                    _previousHandDetailsLD.data = gameDetailsInfo

                }
            }
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
        ){
            val userBestHand = it?.to<UserBestHand>()
            log("getHandStrength", "it: {$userBestHand}")
            userBestHandLd.data = userBestHand
        }
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
            jsonObject("amount" to refillAmount,
                "table_id" to tableId, "join_id" to mySlot!!.join_id,
                "game_id" to gameDetailsLD.data?.let {
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

    fun leaveTable(callback: ChannelCallbackType<JsonElement?>) {
        emit<JsonElement>(
            "leaveTable",
            jsonObject("table_id" to tableId),
        callback = callback)
    }

    fun getFilteredSlotsCount(status: String, filterType: SlotsFilter = SlotsFilter.SEAT_STATUS) : Int {
        when (filterType) {
            SlotsFilter.SEAT_STATUS -> return (tableSlots!!.count { it.status == status })
            SlotsFilter.PLAYER_ACTION -> {
                val list = tableSlots!!.filter { it.user != null && it.user!!.status != null }
                if(list.isNotEmpty()){
                    return list.count {
                        it.user!!.status == status
                    }
                }else{
                    return 0
                }
            }
        }
    }

    fun getInactiveSlotsCount() : Int{
        val sitoutSlotsCount = (tableSlots!!.count { it.status == SeatStatus.SIT_OUT.name })
        val waitForNextCount = (tableSlots!!.count { it.status == SeatStatus.WAIT_FOR_NEXT.name })
        val waitForBBSlotsCount = (tableSlots!!.count { it.status == SeatStatus.WAIT_FOR_BB.name })
        return sitoutSlotsCount + waitForNextCount + waitForBBSlotsCount
    }

    fun resetRefillInPlayAmount(){
        _refillInPlayAmount.postValue(false)
    }

    fun checkIfMySlotActive() : Boolean{
        val meSlot = tableSlots!!.firstOrNull { it.user_unique_id == userId }
        return meSlot?.status == SeatStatus.ACTIVE.name
    }

    fun isUserBestHandAvailable(dealCommunityCards: DealCommunityCards): Boolean{
        var openCommunityCardsCount = 0;
        val USER_CARDS_COUNT = 2
        if(dealCommunityCards.card_1.isNotEmpty()){
            openCommunityCardsCount+=1
        }
        if(dealCommunityCards.card_2.isNotEmpty()){
                openCommunityCardsCount+=1
        }
        if(dealCommunityCards.card_3.isNotEmpty()){
            openCommunityCardsCount+=1
        }
        if(dealCommunityCards.card_4.isNotEmpty()){
            openCommunityCardsCount+=1
        }
        if(dealCommunityCards.card_5.isNotEmpty()){
            openCommunityCardsCount+=1
        }
        return (openCommunityCardsCount+ USER_CARDS_COUNT) >=5
    }

}


