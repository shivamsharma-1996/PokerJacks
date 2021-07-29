package com.gtgt.pokerjacks.ui.game.view.slot

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.CountDownTimer
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.extensions.*
import com.gtgt.pokerjacks.ui.game.GameActivity
import com.gtgt.pokerjacks.ui.game.models.*
import com.gtgt.pokerjacks.ui.game.viewModel.PlayerActions
import com.gtgt.pokerjacks.ui.game.viewModel.SeatStatus
import com.gtgt.pokerjacks.utils.SlotPositions
import com.gtgt.pokerjacks.utils.SlotPositions.*
import kotlinx.android.synthetic.main.activity_game.view.*
import kotlinx.android.synthetic.main.player_new.view.*
import java.lang.Exception


class SlotViews(private val rootLayout: RelativeLayout, val onSlotClicked: (Int) -> Unit) {
    var dealerPosition: Int = 0
        set(value) {
            field = value
            resetPlayers()
        }
    private val context = rootLayout.context
    private val userId = retrieveString("USER_ID")
//    private var animateTimer: CustomCountDownTimer? = null

    val slotViews = mutableMapOf<Int, View>()
    val isFoldAnimated = mutableMapOf<Int, Boolean>()

    private val slots = mutableMapOf<Int, TableSlot>()

    private var topMargin = dpToPx(38)
    private var leftMargin = dpToPx(60)
    private var playerSize = dpToPx(66).toFloat()
    private var meSlotSize = dpToPx(73).toFloat()
    private val roundingSize = dpToPx(5).toFloat()
    private val tableWidth = rootLayout.width.toFloat()
    private var tossCardHeight = dpToPx(R.dimen._40sdp).toFloat()
    val tossCardWidth = dpToPx(R.dimen._26sdp).toFloat()

    private val tableHeight = rootLayout.playArea.height.toFloat()
    private lateinit var mySlotBottom: SlotPosition
    private lateinit var meSlotBottom: TableSlot
    var isRefillPopupVisible = false

    var isLandscape = false
        set(value) {
            field = value
            if (field) {
                topMargin = dpToPx(38)
                leftMargin = dpToPx(60)
            } else {
                topMargin = dpToPx(50)
                leftMargin = dpToPx(32)
            }
        }


    var usersBestHand: List<UsersBestHand>? = null

    var totalSlots: Int = 0
        set(value) {
            field = value

            if (slotViews.isEmpty()) {
                (1..totalSlots).forEach {
                    val v = LayoutInflater.from(context).inflate(R.layout.player_new, null)
                    v.visibility = GONE
                    rootLayout.addView(v)

                    v.playerViewNew.onOneClick {
                        if (!isJoined) {
                            val slotPosition = v.tag as Int
                            if (slots[slotPosition]!!.status == TableSlotStatus.VACANT.name) {
                                onSlotClicked(slotPosition)
                            }
                        }
                    }

                    v.x = tableWidth / 2
                    v.y = tableHeight / 2

                    //v.widthHeight(52, 52)
                    slotViews[it] = v
                    isFoldAnimated[it] = false //default values for all slotPositions would be false
                }

                //LANDSCAPE
                mySlotBottom = makeSlotPositions(
                    playerSize,
                    topMargin,
                    leftMargin,
                    tossCardWidth,
                    tossCardHeight,
                    tableWidth,
                    tableHeight,
                    roundingSize,
                    meSlotSize,
                    isLandscape
                )
            }
        }

    var animatadView: CountDownTimer? = null
    var playerTurn: PlayerTurn? = null
        set(value) {
            field = value!!
            slotViews.forEach {
                if (slots[it.key]!!.user_unique_id != "") {
                    it.value.iv_userProfile.visibility = VISIBLE
                }
                it.value.animateView.stopAnim()
                it.value.countDown.visibility = GONE
                animatadView?.cancel()
                it.value.animateView.isStopped = true
            }

            timeOut(100) {

                slotViews[value.current_bettor_position]!!.apply {
                    //iv_userProfile.visibility = GONE
//                    animateView.startAnim(value) {
//                        iv_userProfile.visibility = VISIBLE
//                    }
                    countDown.visibility = VISIBLE
                    animatadView = animateView.animate(
                        value.player_action_timer,
                        value.player_grace_timer,
                        animateView,
                        countDown,
                        true,
                        false
                    )!!
                }
            }
        }

    var isJoined = false

    fun crownTo(slotNo: Int, canHighlightCard: List<Boolean> = listOf(false, false)) {
        if (slotNo == -1) {
            slotViews.forEach {
                it.value.crown.visibility = GONE

                (it.value.revealCards.getChildAt(0) as ImageView).highlightCard(canHighlight = canHighlightCard[0])
                (it.value.revealCards.getChildAt(1) as ImageView).highlightCard(canHighlight = canHighlightCard[1])
            }
        } else {
            slotViews[slotNo]!!.crown.visibility = VISIBLE
            slotViews[slotNo]!!.crown.playAnimation()
            (slotViews[slotNo]!!.revealCards.getChildAt(0) as ImageView).highlightCard(canHighlight = canHighlightCard[0])
            (slotViews[slotNo]!!.revealCards.getChildAt(1) as ImageView).highlightCard(canHighlight = canHighlightCard[1])
        }
    }

    fun displayLeaderBoard() {
        val currentSlots = slots.values.toList()
        currentSlots?.forEach { slot ->
            val slotView = slotViews[slot.seat_no]
            slotView?.apply {
                if (userId == slot.user_unique_id) {
                    revealCards.visibility = GONE
                } else {
                    val bestHand = usersBestHand?.find { it.user_unique_id == slot.user_unique_id }
                    if (bestHand == null) {
                        revealCards.visibility = GONE
                    } else {
                        //raise_amt.visibility = INVISIBLE
                        if(!bestHand.best_hand_details.hideCards){
                            //deduceRaiseAmtSpace(raise_amt)
                            revealCards.visibility = VISIBLE
                            (revealCards.getChildAt(0) as ImageView).coloredCard(bestHand.card_1)

                            (revealCards.getChildAt(1) as ImageView).coloredCard(bestHand.card_2)
                        }
                    }
                }
                stopTurnAnimation(slotView)
            }
        }
    }

    fun stopTurnAnimation(slotView: View) {
        slotView.animateView.stopAnim()
        animatadView?.cancel()
        slotView.countDown.visibility = GONE
    }

    @SuppressLint("SetTextI18n")
    fun drawSlots(slots: List<TableSlot>) {
//        if((context as GameActivity).vm.isWaitingForOthersShown){
//            return
//        }
//        this.slots.clear()
        slots.forEach { slot ->
            val currentSlotPositionMap = if (slots.size == 6 && !isLandscape) {
                slotPosition6TableMap
            } else {
                slotPositionMap
            }
            val position = currentSlotPositionMap[slot.seat_no]
            this.slots[slot.seat_no] = slot
            val slotView = slotViews[slot.seat_no]
            meSlotSize = dpToPx(73).toFloat()
            playerSize = dpToPx(66).toFloat()
            slotView?.apply {
                tag = slot.seat_no

                if (slot.seat_no == dealerPosition) {
                    dealer.visibility = VISIBLE
                } else {
                    dealer.visibility = GONE
                }

                //if(!slot.status.equals(PlayerActions.))

                if (slot.status == TableSlotStatus.VACANT.name) {
                    iv_userProfile.visibility = GONE
                    active_indication.visibility = GONE
                    player_action.visibility = GONE
                    all_in_flag.visibility = GONE
                    in_play_amt.text = "-"
                    raise_amt.visibility = INVISIBLE
                    //deduceRaiseAmtSpace(raise_amt)
                    name_inplay_group.visibility = GONE
                    cl_player.background = context.getDrawable(R.drawable.player_view_gradient)
                    cl_player.backgroundTintList = null

                    if (isJoined && position == BOTTOM_CENTER && userId == slot.user_unique_id
                    ) {
                        isJoined = false
                    }

                    if (isJoined) {
                        this.markSeatEmpty(true)
                    } else {
                        log("ismeAya", "aya")
                        this.markSeatEmpty(false)
                    }
                } else {
                    if (isJoined && position == BOTTOM_CENTER && userId == slot.user_unique_id) {
                        cl_player.backgroundTintList =
                            ColorStateList.valueOf(Color.parseColor("#112236"))
                    } else {
                        cl_player.setBackgroundResource(R.drawable.player_view_gradient)
                    }

//                    this.markSeatEmpty(false)
                    vacant_dp.visibility = INVISIBLE
                    tv_empty_seat.visibility = INVISIBLE
                    tv_sit_here.visibility = GONE
                    noPlayer.visibility = GONE
                    //player_action.visibility = GONE
                    /*if(animateView.isGraceTime== false){
                        iv_userProfile.visibility = VISIBLE
                    }*/
                    log("animateView.time2", animateView.isStopped)
                    /*if(animateView.isStopped){
                        iv_userProfile.visibility = VISIBLE
                    }*/
                    iv_userProfile.visibility = VISIBLE
                    name_inplay_group.visibility = VISIBLE

                    // active_indication.visibility = VISIBLE
                    if (slot.game_user) {
                        when (slot.status) {
                            SeatStatus.WAIT_FOR_BB.name, SeatStatus.WAIT_FOR_NEXT.name -> {
                                if (((context as GameActivity).vm.canDisplayWaitingIcon)) {
                                    cl_player.blurOut(canBlurOut = true)
                                    countDown.visibility = INVISIBLE
                                    active_indication.setImageResource(R.drawable.waiting)
                                    active_indication.visibility = VISIBLE
                                }
                            }
                            else -> {
                                when (slot.status) {
                                    SeatStatus.SIT_OUT.name -> {
                                        cl_player.blurOut(canBlurOut = true)
                                        countDown.visibility = INVISIBLE
                                        active_indication.setImageResource(R.drawable.sitout)
                                    }
                                    else -> {
                                        active_indication.setImageResource(R.drawable.active_indication)
                                        //iv_userProfile.alpha = 1f
                                        if(!(slot.user != null && slot.user!!.status.equals(PlayerActions.FOLD.name)))
                                        cl_player.blurOut(canBlurOut = false)
                                    }
                                }

                                if ((context as GameActivity).isGameStartedAndRunning)
                                active_indication.visibility = VISIBLE
                            }
                        }
                    }

                    if (slot.user != null &&
                        ((slot.user!!.current_round_invested > 0.0) ||
                                slot.user!!.status.equals(PlayerActions.ALL_IN.name))) {

                        if (slot.user!!.status.equals(PlayerActions.ALL_IN.name)
//                            && !(isJoined && position == BOTTOM_CENTER)
                        ) {
                            if ((context as GameActivity).isGameStartedAndRunning)
                            all_in_flag.visibility = VISIBLE
                            stopTurnAnimation(slotView)
                        } else {
                            all_in_flag.visibility = GONE
                        }

                        if ((context as GameActivity).isGameStartedAndRunning && revealCards.visibility != VISIBLE)
                            raise_amt.visibility = VISIBLE

                        if(slot.user!!.current_round_invested > 0.0){
                            raise_amt.text =
                                "₹" + String.format(
                                    "%.2f",
                                    slot.user!!.current_round_invested
                                )
                        }else if(slot.user!!.current_round_invested == 0.0){
                            /*raise_amt.text =
                                "₹" + String.format(
                                    "%.2f",
                                    slot.user!!.amount_invested
                                )*/
                            raise_amt.visibility = INVISIBLE
                        }
                        /*else{
                            raise_amt.text =
                                "₹" + String.format(
                                    "%.2f",
                                    slot.user!!.amount_invested
                                )
                        }*/

                        val coin = when (slot.user!!.status) {
                            "ACTIVE" -> R.drawable.bet
                            /*PlayerActions.CHECK.name -> R.drawable.check_small
                            PlayerActions.FOLD.name -> R.drawable.fold_small*/
                            PlayerActions.CALL.name -> R.drawable.call_small
                            PlayerActions.RAISE.name -> R.drawable.raise
                            PlayerActions.ALL_IN.name -> R.drawable.raise
                            else -> null
                        }

                        if (coin != null) {
                            if (currentSlotPositionMap[slot.seat_no]!!.name.contains("RIGHT")) {
                                //active_indication.layoutGravity(Gravity.END)
                                //active_indication.rotationY = 0f
                                if (isLandscape)
                                    raise_amt.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                        0, 0,
                                        coin, 0
                                    )
                                else
                                    raise_amt.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                        coin,
                                        0,
                                        0,
                                        0
                                    )
                            } else {
                                //active_indication.layoutGravity(Gravity.START)
                                //active_indication.rotationY = 180f
                                raise_amt.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                    coin,
                                    0,
                                    0,
                                    0
                                )
                            }
                        }
                    } else {
                        raise_amt.visibility = INVISIBLE
                       // deduceRaiseAmtSpace(raise_amt)
                    }
                }

                //widthHeightRaw(WRAP_CONTENT, WRAP_CONTENT)
                visibility = VISIBLE


                if (slot.user != null/* slot.user!!.current_round_invested == 0.0 &&*/) {
                    val status = slot.user!!.status
                    log("user::status", status)
                    when (status) {
                        PlayerActions.CHECK.name -> {
                            if ((context as GameActivity).isGameStartedAndRunning)
                            player_action.visibility = VISIBLE
                            raise_amt.visibility = INVISIBLE
                            player_action.setImageResource(R.drawable.check_small)
                        }
                        else -> {
                            player_action.visibility = GONE
                        }
                    }
                }
                if (slot.user != null && slot.user!!.status.equals(PlayerActions.FOLD.name)) {
                    raise_amt.visibility = INVISIBLE
                }
                val slotPosition = if (isJoined && position == BOTTOM_CENTER) {
                    meSlotBottom = slot
                    active_indication.visibility = GONE
                    mySlotBottom
                } else {
                    if (slot.user != null && slot.user!!.status.equals(PlayerActions.FOLD.name)) {
                        animateView.stopAnim()
                        cl_player.blurOut(canBlurOut = true)
                        countDown.visibility = INVISIBLE
                        dealer.visibility = GONE
                        if ((context as GameActivity).isGameStartedAndRunning){
                            player_action.visibility = VISIBLE
                            player_action.setImageResource(R.drawable.fold)
                        }
                        if (userId == slot.user_unique_id) {
                            meSlotSize = dpToPx(63).toFloat()
                        } else {
                            playerSize = dpToPx(56).toFloat()
                        }
                        if (isFoldAnimated[slot.seat_no] == false) {
                            //playerViewNew.resizeAnimation(playerSize.toInt(), playerSize.toInt() + dpToPx(20), 1000L)
                        }
                        isFoldAnimated[slot.seat_no] = true
                    } else {
                        //   playerView1.widthHeightRaw(playerSize, playerSize + dpToPx(20))
                    }
                    slotPositions[position]!!
                }

                if (slotPosition.tossCard.alignment != -1)
                    toss_card.layoutGravity(slotPosition.tossCard.alignment)

                if (slotPosition.raiseAmt.alignment != -1)
                    raise_amt.layoutGravity(slotPosition.raiseAmt.alignment)

                checkForToss(slot, this, currentSlotPositionMap, slotPosition)

                if (slots.size == 6 && isLandscape) {
                    val fromX: Float
                    val fromY: Float
                    when (position) {
                        LEFT_TOP -> {
                            fromX = slotPosition.x / 1.6f
                            fromY = slotPosition.y + dpToPx(3)
                        }
                        RIGHT_TOP -> {
                            fromX = slotPosition.x * 1.2f
                            fromY = slotPosition.y
                        }
                        else -> {
                            fromX = slotPosition.x
                            fromY = slotPosition.y
                        }
                    }
                    animate().apply {
                        x(fromX)
                        y(fromY)
                        start()
                    }
                } else {
                    animate().apply {
                        x(slotPosition.x)
                        y(slotPosition.y)
                        start()
                    }
                }
                nameTV.text = slot.user_name
                if (isJoined && position == BOTTOM_CENTER && slot.user != null && slot.user!!.status.equals(
                        PlayerActions.FOLD.name
                    )
                ) {
                    stopTurnAnimation(slotView)
                } else {
                    playerViewNew.marginsRaw(
                        slotPosition.player.ml, slotPosition.player.mt,
                        slotPosition.player.mr, slotPosition.player.mb
                    )
                }
                if (slotPosition.player.alignment != -1) {
                    rl_player.layoutGravity(slotPosition.player.alignment)
                }

                player_action.marginsRaw(
                    slotPosition.playerAction.ml, slotPosition.playerAction.mt,
                    slotPosition.playerAction.mr, slotPosition.playerAction.mb
                )
                player_action.layoutGravity(slotPosition.playerAction.alignment)

                revealCards.marginsRaw(
                    slotPosition.revealCards.ml, slotPosition.revealCards.mt,
                    slotPosition.revealCards.mr, slotPosition.revealCards.mb
                )

                val inPlay = (slot.user?.game_inplay_amount ?: slot.inplay_amount)
                if (inPlay == 0.0) {
                    in_play_amt.text = "-"
                } else {
                    in_play_amt.text =
                        "₹${inPlay?.toDecimalFormat()}"
                }
            }
        }
    }

    private fun checkForToss(
        slot: TableSlot,
        view: View,
        currentSlotPositionMap: MutableMap<Int, SlotPositions>,
        slotPosition: SlotPosition
    ) {
        try {
            if((context as GameActivity).vm.isTossEnabledForCurrentGame
                && slot.status == TableSlotStatus.ACTIVE.name){
                if(slot.toss_card!=null){
                    view.toss_card.marginsRaw(
                        slotPosition.tossCard.ml, slotPosition.tossCard.mt,
                        slotPosition.tossCard.mr, slotPosition.tossCard.mb
                    )
                    view.toss_card.visibility = VISIBLE
                    deduceMarginSpace(view.raise_amt)
                }
            }else{
                if (slot.user != null/* && !slot.user!!.status.equals(PlayerActions.ALL_IN.name)*/)
                {
                    var topMargin = 0
                    val currentSlotPositionName = currentSlotPositionMap[slot.seat_no]!!.name
                    if (currentSlotPositionName == BOTTOM_CENTER.name
                        || (isLandscape && (currentSlotPositionName == RIGHT_BOTTOM.name
                                || currentSlotPositionName == LEFT_BOTTOM.name))
                    ) {
                        topMargin =  dpToPx(20)
                    }

                    view.raise_amt.marginsRaw(
                        slotPosition.raiseAmt.ml, slotPosition.raiseAmt.mt + topMargin,
                        slotPosition.raiseAmt.mr, slotPosition.raiseAmt.mb
                    )
                    deduceMarginSpace(view.toss_card)
                }
                view.toss_card.visibility = GONE
                //TODO remove margins of tosscard here
            }
        }catch (e:Exception){

        }
    }

    fun hideTossCards() {
        try {
            slotViews.forEach {
                it.value.toss_card.visibility = GONE
                deduceMarginSpace(it.value.toss_card)
            }
        }catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun deduceMarginSpace(view: View) {
        view.marginsRaw(0, 0, 0, 0)
    }

    fun animateSelfView(playerAction: PlayerActions, seatNo: Int) {
        if (isJoined) {
            when (playerAction.name) {
                PlayerActions.FOLD.name -> {
                    meSlotSize = dpToPx(63).toFloat()
                    slotViews[seatNo]?.player_action!!.visibility = VISIBLE
                    slotViews[seatNo]?.player_action!!.setImageResource(R.drawable.fold)
                    slotViews[seatNo]?.cl_player!!.blurOut(canBlurOut = true)
                    slotViews[seatNo]?.cl_player!!.countDown.visibility = INVISIBLE
                    //slotViews[seatNo]?.playerViewNew!!.resizeAnimation(meSlotSize.toInt(), meSlotSize.toInt() + dpToPx(20), 1000L)
                    slotViews[seatNo]?.playerViewNew!!.marginsRaw(
                        mySlotBottom.player.ml + dpToPx(5), mySlotBottom.player.mt,
                        mySlotBottom.player.mr, mySlotBottom.player.mb
                    )

                }
            }
        }

    }

    fun getPositionBySeatNumber(seatSize: Int, seatNo: Int): SlotPosition {
        val currentSlotPositionMap = if (seatSize == 6 && !isLandscape) {
            slotPosition6TableMap
        } else {
            slotPositionMap
        }
        val position = currentSlotPositionMap[seatNo]
        return if (isJoined && position == BOTTOM_CENTER) {
            mySlotBottom
        } else {
            slotPositions[position]!!
        }
    }

    fun restartGame(){
        slotViews.forEach {
            it.value.all_in_flag.visibility = GONE
            it.value.revealCards.visibility = GONE
            it.value.refund_amt.visibility = GONE
            it.value.raise_amt.visibility = INVISIBLE
        }
    }
    fun resetPlayers() {
        try {
            slotViews.forEach {
                if (slots[it.key]!!.user_unique_id != "") {
                    it.value.iv_userProfile.visibility = VISIBLE
                }
                it.value.animateView.stopAnim()
                it.value.countDown.visibility = GONE
                animatadView?.cancel()
                it.value.revealCards.getChildAt(0).alpha = 1f
                it.value.revealCards.getChildAt(1).alpha = 1f
                it.value.player_action.visibility = GONE
                it.value.dealer.visibility = GONE
                it.value.iv_userProfile.circleBackgroundColor = 0
                isFoldAnimated[it.key] = false
                drawSlots(slots.values.toList())
            }
        }catch (ex: Exception) {
            }
        }

        fun checkCanRefillWallet(slots: List<TableSlot>) {
            slots.forEach { slot ->
                val currentSlotPositionMap = if (slots.size == 6 && !isLandscape) {
                    slotPosition6TableMap
                } else {
                    slotPositionMap
                }
                val position = currentSlotPositionMap[slot.seat_no]
                if (!isRefillPopupVisible) {
                    if (isJoined && position == BOTTOM_CENTER && slot.status.equals(TableSlotStatus.REFILL.name)) {
                        meSlotBottom = slot
                        (context as GameActivity).showBuyInAlert(true, meSlotBottom.seat_no)
                        isRefillPopupVisible = true
                        log("checkCanRefill3", "checkCanRefillWallet")
                    }
                }
            }
        }

        fun resetGame() {
            slotViews.forEach {
                try {
                    if (slots[it.key]?.user_unique_id != "") {
                        it.value.iv_userProfile.visibility = VISIBLE
                    }
                } catch (ex: KotlinNullPointerException) {

                }
                it.value.animateView.stopAnim()
                it.value.countDown.visibility = GONE
                animatadView?.cancel()

                it.value.revealCards.getChildAt(0).alpha = 1f
                it.value.revealCards.getChildAt(1).alpha = 1f
                it.value.iv_userProfile.circleBackgroundColor = 0
                isFoldAnimated[it.key] = false

                it.value.dealer.visibility = GONE
                it.value.player_action.visibility = GONE
//                if (!(slots[it.key]?.user?.status.equals(PlayerActions.ALL_IN.name) &&
//                            (context as GameActivity).isGameStartedAndRunning)) {
//                    it.value.raise_amt.visibility = INVISIBLE
//                    deduceRaiseAmtSpace(it.value.raise_amt)
//                }
                //it.value.raise_amt.visibility = INVISIBLE
               // deduceRaiseAmtSpace(it.value.raise_amt)

                try {
                    val slotStatus = slots[it.key]?.status!!

                    if (!slotStatus.equals(SeatStatus.SIT_OUT.name) &&
                        !slotStatus.equals(SeatStatus.WAIT_FOR_NEXT.name) &&
                        !slotStatus.equals(SeatStatus.WAIT_FOR_BB.name)
                    ) {
                        log("ayaa", "sfvff")
                        it.value.active_indication.visibility = INVISIBLE
                    }
                    //it.value.active_indication.visibility = View.INVISIBLE
                } catch (e: java.lang.Exception) {

                }

                //it.value.active_indication.visibility = GONE
            }
        }

        fun resetDealerIcon() {
            dealerPosition = 0
        }

    fun displayRefundAmtWithAnimation(refundPlayersList: List<potRefunds>) {
        val currentSlots = slots.values.toList()
        currentSlots?.forEach { slot ->
            val refundObject: potRefunds? = refundPlayersList.find { it.user_unique_id == slot.user_unique_id }
            refundObject?.let {
                val slotView = slotViews[slot.seat_no]
                slotView?.apply {
                    refund_amt.setBackgroundColor(Color.parseColor("#AD000000"))
                    refund_amt.text =  "Refund: ₹" + String.format(
                    "%.2f",
                    refundObject.wonAmt
                    )
                    refund_amt.visibility = VISIBLE
                }
            }
        }

        Handler().postDelayed({
            currentSlots?.forEach { slot ->
                val refundObject: potRefunds? = refundPlayersList.find { it.user_unique_id == slot.user_unique_id }
                refundObject?.let {
                    val slotView = slotViews[slot.seat_no]
                    slotView?.refund_amt?.animate()?.apply {
                        x(slotView.in_play_amt.x  + slotView.in_play_amt.width/2)
                        y((slotView.height/2).toFloat())
                        duration = 2000
                        withEndAction {
                            slotView.refund_amt.visibility = GONE
                            slotView.refund_amt.text = ""
                        }
                        start()
                    }
                }
            }
        }, 1000)
    }

    fun updateInPlayOnGameEnd(slots: List<TableSlot>?) {
        slots!!.forEach { slot ->
            if(slot.status == TableSlotStatus.ACTIVE.name){
                val slotView = slotViews[slot.seat_no]
                slotView?.apply {
                    in_play_amt.text =
                        "₹${slot.inplay_amount?.toDecimalFormat()}"
                }
            }
        }
    }
}