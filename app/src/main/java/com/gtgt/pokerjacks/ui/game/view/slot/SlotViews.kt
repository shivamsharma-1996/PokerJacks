package com.gtgt.pokerjacks.ui.game.view.slot

import android.animation.ValueAnimator
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.RelativeLayout
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.extensions.*
import com.gtgt.pokerjacks.ui.game.GameActivity
import com.gtgt.pokerjacks.ui.game.models.PlayerTurn
import com.gtgt.pokerjacks.ui.game.models.TableSlot
import com.gtgt.pokerjacks.ui.game.models.TableSlotStatus
import com.gtgt.pokerjacks.ui.game.models.UsersBestHand
import com.gtgt.pokerjacks.ui.game.viewModel.PlayerActions
import com.gtgt.pokerjacks.ui.game.viewModel.SeatStatus
import com.gtgt.pokerjacks.utils.SlotPositions.*
import kotlinx.android.synthetic.main.activity_game.view.*
import kotlinx.android.synthetic.main.player.view.*
import kotlin.math.roundToInt


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
                    val v = LayoutInflater.from(context).inflate(R.layout.player, null)
                    v.visibility = GONE
                    rootLayout.addView(v)

                    v.playerView.onOneClick {
                        if (!isJoined) {
                            val slotPosition = v.tag as Int
                            if (slots[slotPosition]!!.status == TableSlotStatus.VACANT.name) {
                                onSlotClicked(slotPosition)
                            }
                        }
                    }

                    v.x = tableWidth / 2
                    v.y = tableHeight / 2

                    v.widthHeight(52, 52)
                    slotViews[it] = v
                    isFoldAnimated[it] = false //default values for all slotPositions would be false
                }

                //LANDSCAPE
                mySlotBottom = makeSlotPositions(
                    playerSize,
                    topMargin,
                    leftMargin,
                    tableWidth,
                    tableHeight,
                    roundingSize,
                    meSlotSize,
                    isLandscape
                )
            }
        }

    var playerTurn: PlayerTurn? = null
        set(value) {
            field = value!!
            slotViews.forEach {
                if (slots[it.key]!!.user_unique_id != "") {
                    it.value.iv_userProfile.visibility = VISIBLE
                }
                it.value.animateView.stopAnim()
            }

            timeOut(100) {
                slotViews[value.current_bettor_position]!!.apply {
                    iv_userProfile.visibility = GONE
                    animateView.startAnim(value) {
                        iv_userProfile.visibility = VISIBLE
                    }
                }
            }
        }

    var isJoined = false

    fun crownTo(slotNo: Int, cardsAlpha: List<Float> = listOf(1f, 1f)) {
        if (slotNo == -1) {
            slotViews.forEach {
                it.value.crown.visibility = GONE
                it.value.revealCards.getChildAt(0).alpha = cardsAlpha[0]
                it.value.revealCards.getChildAt(1).alpha = cardsAlpha[1]
            }
        } else {
            slotViews[slotNo]!!.crown.visibility = VISIBLE
            slotViews[slotNo]!!.revealCards.getChildAt(0).alpha = cardsAlpha[0]
            slotViews[slotNo]!!.revealCards.getChildAt(1).alpha = cardsAlpha[1]
        }
    }

    fun drawSlots(slots: List<TableSlot>) {
//        this.slots.clear()
        slots.forEach { slot ->
            val currentSlotPositionMap = if(slots.size == 6 && isLandscape){
                slotPosition6TableMap
            }else{
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
                if (userId == slot.user_unique_id) {
                    revealCards.visibility = GONE
                } else {
                    val bestHand = usersBestHand?.find { it.user_unique_id == slot.user_unique_id }
                    if (bestHand == null) {
                        revealCards.visibility = GONE
                    } else {
                        revealCards.visibility = VISIBLE
                        (revealCards.getChildAt(0) as ImageView).coloredCard(bestHand.card_1)

                        (revealCards.getChildAt(1) as ImageView).coloredCard(bestHand.card_2)
                    }
                }

                if (slot.status == TableSlotStatus.VACANT.name) {
                    noPlayer.visibility = if (isJoined) GONE else VISIBLE
                    iv_userProfile.visibility = GONE
                    active_indication.visibility = GONE
                    player_action.visibility = GONE
                    in_play_amt.text = "-"
                    raise_amt.visibility = GONE
                    name_inplay_group.visibility = GONE
                }
                else {
                    noPlayer.visibility = GONE
                    //player_action.visibility = GONE
                    iv_userProfile.visibility = VISIBLE
                    name_inplay_group.visibility = VISIBLE

                    if (slot.user != null && slot.user!!.status != TableSlotStatus.FOLD.name) {
                        active_indication.visibility = VISIBLE
                        active_indication.setImageResource(
                            when (slot.status) {
                                SeatStatus.WAIT_FOR_BB.name, SeatStatus.WAIT_FOR_NEXT.name -> {
                                    iv_userProfile.blurOut()
                                    R.drawable.waiting
                                }
                                SeatStatus.SIT_OUT.name, SeatStatus.SIT_OUT_NEXT.name-> {
                                    iv_userProfile.blurOut()
                                    R.drawable.sitout
                                }
                                SeatStatus.ACTIVE.name -> R.drawable.active_indication
                                else -> {
                                    log("poker::active_indication", slot.status)
                                    R.drawable.active_indication
                                    /*iv_userProfile.blurOut()
                                    R.drawable.waiting*/
                                }
                            }
                        )
                    } else {
                        active_indication.visibility = GONE
                    }

                    if (slot.user != null && slot.user!!.current_round_invested > 0.0) {
                        raise_amt.visibility = VISIBLE
                        raise_amt.text =
                            "₹" + String.format(
                                "%.2f",
                                (slot.user?.current_round_invested ?: 1234.00)
                            )

                        val coin = when (slot.user!!.status) {
                            "ACTIVE" -> R.drawable.bet
                            /*PlayerActions.CHECK.name -> R.drawable.check_small
                            PlayerActions.FOLD.name -> R.drawable.fold_small*/
                            PlayerActions.CALL.name -> R.drawable.call_small
                            PlayerActions.RAISE.name -> R.drawable.raise
                            else -> null
                        }

                        if(coin!=null){
                            if (currentSlotPositionMap[slot.seat_no]!!.name.contains("RIGHT")) {
                                active_indication.layoutGravity(Gravity.END)
                                active_indication.rotationY = 0f
                                if(isLandscape)
                                    raise_amt.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0,
                                        coin, 0)
                                else
                                    raise_amt.setCompoundDrawablesRelativeWithIntrinsicBounds(coin, 0, 0, 0)
                            } else {
                                active_indication.layoutGravity(Gravity.START)
                                active_indication.rotationY = 180f
                                raise_amt.setCompoundDrawablesRelativeWithIntrinsicBounds(coin, 0, 0, 0)
                            }
                        }
                    } else {
                        raise_amt.visibility = GONE
                    }
                }

                widthHeightRaw(WRAP_CONTENT, WRAP_CONTENT)
                visibility = VISIBLE

                if(slot.user!=null && slot.user!!.status.equals(PlayerActions.FOLD.name)){

                }else{

                }
                if (slot.user != null/* slot.user!!.current_round_invested == 0.0 &&*/) {
                    val status = slot.user!!.status
                    log("user::status", status)
                    when(status){
                        PlayerActions.CHECK.name -> {
                            player_action.visibility = VISIBLE
                            player_action.setImageResource(R.drawable.check_small)
                        }
                        PlayerActions.FOLD.name -> {
//                            player_action.visibility = VISIBLE
//                            player_action.setImageResource(R.drawable.fold)
                          /*  if(!isJoined || position != BOTTOM_CENTER){
                                iv_userProfile.blurOut()
                                if (userId == slot.user_unique_id){
                                    meSlotSize = dpToPx(63).toFloat()
                                }else{
                                    playerSize = dpToPx(56).toFloat()
                                }
                            }*/
                        }
                        else -> {
                            player_action.visibility = GONE
                        }
                    }
                }
                val slotPosition = if (isJoined && position == BOTTOM_CENTER) {
                    meSlotBottom = slot
                    active_indication.visibility = GONE
                    if(slot.user!=null && slot.user!!.status.equals(PlayerActions.FOLD.name)){
                        //playerView.resizeAnimation(meSlotSize.toInt(), meSlotSize.toInt() + dpToPx(20), 1000L)
                    }else{
                        playerView.widthHeightRaw(meSlotSize, meSlotSize + dpToPx(20))
                    }
                    mySlotBottom
                } else {
                    if(slot.user!=null &&slot.user!!.status.equals(PlayerActions.FOLD.name)){
                        animateView.stopAnim()
                        iv_userProfile.blurOut()
                        dealer.visibility = GONE
                        player_action.visibility = VISIBLE
                        player_action.setImageResource(R.drawable.fold)
                        if (userId == slot.user_unique_id){
                            meSlotSize = dpToPx(63).toFloat()
                        }else{
                            playerSize = dpToPx(56).toFloat()
                        }
                        if(isFoldAnimated[slot.seat_no] == false){
                            playerView.resizeAnimation(playerSize.toInt(), playerSize.toInt() + dpToPx(20), 1000L)
                        }
                        isFoldAnimated[slot.seat_no] = true
                    }else{
                        playerView.widthHeightRaw(playerSize, playerSize + dpToPx(20))
                    }
                    slotPositions[position]!!
                }

                if(slots.size == 6 && isLandscape){
                    val fromX: Float
                    val fromY: Float
                    when(position){
                        LEFT_TOP ->{
                            fromX = slotPosition.x/1.6f
                            fromY = slotPosition.y + dpToPx(3)
                        }
                        RIGHT_TOP -> {
                            fromX = slotPosition.x*1.1f
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
                }else{
                    animate().apply {
                        x(slotPosition.x)
                        y(slotPosition.y)
                        start()
                    }
                }
                /*x = slotPosition.x
                y = slotPosition.y*/

                nameTV.text = slot.user_name
                if (isJoined && position == BOTTOM_CENTER && slot.user!=null && slot.user!!.status.equals(PlayerActions.FOLD.name)){
                    //do nothing
                }else{
                    playerView.marginsRaw(
                        slotPosition.player.ml, slotPosition.player.mt,
                        slotPosition.player.mr, slotPosition.player.mb
                    )
                }
                /*name_inplay_group.marginsRaw(
                    slotPosition.player.ml, slotPosition.player.mt,
                    slotPosition.player.mr, slotPosition.player.mb
                )*/
                raise_amt.marginsRaw(
                    slotPosition.raiseAmt.ml, slotPosition.raiseAmt.mt,
                    slotPosition.raiseAmt.mr, slotPosition.raiseAmt.mb
                )
                if(slotPosition.raiseAmt.alignment!=-1)
                raise_amt.layoutGravity(slotPosition.raiseAmt.alignment)
                dealer.marginsRaw(
                    slotPosition.deal.ml, slotPosition.deal.mt,
                    slotPosition.deal.mr, slotPosition.deal.mb
                )
                dealer.layoutGravity(slotPosition.deal.alignment)
                player_action.marginsRaw(
                    slotPosition.playerAction.ml, slotPosition.playerAction.mt,
                    slotPosition.playerAction.mr, slotPosition.playerAction.mb
                )
                player_action.layoutGravity(slotPosition.playerAction.alignment)

                crown.marginsRaw(
                    slotPosition.crown.ml, slotPosition.crown.mt,
                    slotPosition.crown.mr, slotPosition.crown.mb
                )

                revealCards.marginsRaw(
                    slotPosition.revealCards.ml, slotPosition.revealCards.mt,
                    slotPosition.revealCards.mr, slotPosition.revealCards.mb
                )
                //dealer.layoutGravity(slotPosition.deal)

                /*if (slotPositionMap[slot.seat_no]!!.name.contains("RIGHT")) {
                    active_indication.layoutGravity(Gravity.END)
                    active_indication.rotationY = 0f
                    raise_amt.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.coin_small, 0)
                } else {
                    active_indication.layoutGravity(Gravity.START)
                    active_indication.rotationY = 180f
                    raise_amt.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.coin_small, 0, 0, 0)
                }

                dealer.visibility = VISIBLE
                active_indication.visibility = VISIBLE
              */

                val inPlay = (slot.user?.game_inplay_amount ?: slot.inplay_amount)
                if (inPlay == 0.0) {
                    in_play_amt.text = "-"
                } else {
                    in_play_amt.text =
                        "₹${inPlay.toDecimalFormat()}"
                }
            }
        }
    }

    fun animateSelfView(playerAction: PlayerActions, seatNo: Int){
        if(isJoined){
            when(playerAction.name){
                PlayerActions.FOLD.name -> {
                    meSlotSize = dpToPx(63).toFloat()
                    slotViews[seatNo]?.player_action!!.visibility = VISIBLE
                    slotViews[seatNo]?.player_action!!.setImageResource(R.drawable.fold)
                    slotViews[seatNo]?.iv_userProfile!!.blurOut()
                    slotViews[seatNo]?.playerView!!.resizeAnimation(meSlotSize.toInt(), meSlotSize.toInt() + dpToPx(20), 1000L)
                    slotViews[seatNo]?.playerView!!.marginsRaw(
                        mySlotBottom.player.ml + dpToPx(5), mySlotBottom.player.mt,
                        mySlotBottom.player.mr, mySlotBottom.player.mb
                    )

                }
            }
        }

    }

    fun getPositionBySeatNumber(seatSize: Int, seatNo: Int): SlotPosition {
        val currentSlotPositionMap = if(seatSize == 6 && isLandscape){
            slotPosition6TableMap
        }else{
            slotPositionMap
        }
        val position = currentSlotPositionMap[seatNo]
        return if (isJoined && position == BOTTOM_CENTER) {
            mySlotBottom
        } else {
            slotPositions[position]!!
        }
    }

    fun resetPlayers() {
        try {
            slotViews.forEach {
                if (slots[it.key]!!.user_unique_id != "") {
                    it.value.iv_userProfile.visibility = VISIBLE
                }
                it.value.animateView.stopAnim()

                it.value.revealCards.getChildAt(0).alpha = 1f
                it.value.revealCards.getChildAt(1).alpha = 1f
                it.value.player_action.visibility = GONE
                it.value.dealer.visibility = GONE
                it.value.iv_userProfile.alpha = 1f
                it.value.iv_userProfile.circleBackgroundColor = 0
                isFoldAnimated[it.key] = false
            }
            drawSlots(slots.values.toList())
        } catch (ex: Exception) {
        }
    }

    fun checkCanRefillWallet(slots: List<TableSlot>){
        slots.forEach { slot ->
            val currentSlotPositionMap = if(slots.size == 6 && isLandscape){
                slotPosition6TableMap
            }else{
                slotPositionMap
            }
            val position =  currentSlotPositionMap[slot.seat_no]
            if(!isRefillPopupVisible){
                if(isJoined && position == BOTTOM_CENTER && slot.status.equals(TableSlotStatus.REFILL.name)){
                    meSlotBottom = slot
                    (context as GameActivity).showBuyInAlert(true, meSlotBottom.seat_no)
                    isRefillPopupVisible = true
                    log("checkCanRefill3", "checkCanRefillWallet")
                }
            }
        }
    }

    fun resetGame(){
        slotViews.forEach {
            try {
                if (slots[it.key]?.user_unique_id != "") {
                    it.value.iv_userProfile.visibility = VISIBLE
                }
            }catch (ex : KotlinNullPointerException){

            }
            it.value.animateView.stopAnim()

            it.value.revealCards.getChildAt(0).alpha = 1f
            it.value.revealCards.getChildAt(1).alpha = 1f
            it.value.revealCards.visibility = GONE
            // it.value.player_action.visibility = GONE
            it.value.iv_userProfile.alpha = 1f
            it.value.iv_userProfile.circleBackgroundColor = 0
            isFoldAnimated[it.key] = false

            it.value.dealer.visibility = GONE
            it.value.player_action.visibility = GONE
            it.value.raise_amt.visibility = GONE
            it.value.active_indication.visibility = GONE
        }
    }
}