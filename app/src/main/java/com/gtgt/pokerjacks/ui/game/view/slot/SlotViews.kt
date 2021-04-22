package com.gtgt.pokerjacks.ui.game.view.slot

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
import com.gtgt.pokerjacks.ui.game.models.PlayerTurn
import com.gtgt.pokerjacks.ui.game.models.TableSlot
import com.gtgt.pokerjacks.ui.game.models.TableSlotStatus
import com.gtgt.pokerjacks.ui.game.models.UsersBestHand
import com.gtgt.pokerjacks.ui.game.viewModel.PlayerActions
import com.gtgt.pokerjacks.ui.game.viewModel.SeatStatus
import com.gtgt.pokerjacks.utils.SlotPositions.BOTTOM_CENTER
import kotlinx.android.synthetic.main.activity_game.view.*
import kotlinx.android.synthetic.main.player.view.*

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
    private val slots = mutableMapOf<Int, TableSlot>()

    private var topMargin = dpToPx(38)
    private var leftMargin = dpToPx(60)
    private var playerSize = dpToPx(63).toFloat()
    private var meSlotSize = dpToPx(70).toFloat()
    private val roundingSize = dpToPx(5).toFloat()
    private val tableWidth = rootLayout.width.toFloat()
    private val tableHeight = rootLayout.playArea.height.toFloat()
    private lateinit var mySlotBottom: SlotPosition

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

                    v.onOneClick {
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
            val position = slotPositionMap[slot.seat_no]
            this.slots[slot.seat_no] = slot
            val slotView = slotViews[slot.seat_no]

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
                } else {
                    noPlayer.visibility = GONE
                    //player_action.visibility = GONE
                    iv_userProfile.visibility = VISIBLE
                    name_inplay_group.visibility = VISIBLE

                    if (slot.user != null && slot.user!!.status != TableSlotStatus.FOLD.name) {
                        active_indication.visibility = VISIBLE
                        active_indication.setImageResource(
                            when (slot.status) {
                                SeatStatus.SIT_OUT.name -> R.drawable.sitout
                                SeatStatus.ACTIVE.name -> R.drawable.active_indication
                                else -> R.drawable.waiting
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
                            PlayerActions.CHECK.name -> R.drawable.check_small
                            PlayerActions.FOLD.name -> R.drawable.fold_small
                            PlayerActions.CALL.name -> R.drawable.call_small
                            else -> R.drawable.raise
                        }

                        if (slotPositionMap[slot.seat_no]!!.name.contains("RIGHT")) {
                            active_indication.layoutGravity(Gravity.END)
                            active_indication.rotationY = 0f
                            raise_amt.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, coin, 0)
                        } else {
                            active_indication.layoutGravity(Gravity.START)
                            active_indication.rotationY = 180f
                            raise_amt.setCompoundDrawablesRelativeWithIntrinsicBounds(coin, 0, 0, 0)
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
                    when(status){
                        PlayerActions.CHECK.name -> {
                            player_action.visibility = VISIBLE
                            player_action.setImageResource(R.drawable.ic_opponent_check_turn)
                        }
                        PlayerActions.FOLD.name -> {
                            player_action.visibility = VISIBLE
                            player_action.setImageResource(R.drawable.fold)
                            iv_userProfile.alpha = 0.55f
                            iv_userProfile.circleBackgroundColor = Color.parseColor("#000000")
                            if (userId == slot.user_unique_id){
                                meSlotSize = dpToPx(60).toFloat()
                            }else{
                                playerSize = dpToPx(53).toFloat()
                            }
                        }
                        else -> {
                            player_action.visibility = GONE
                            meSlotSize = dpToPx(70).toFloat()
                            playerSize = dpToPx(63).toFloat()
                        }
                    }
                }
                val slotPosition = if (isJoined && position == BOTTOM_CENTER) {
                    playerView.widthHeightRaw(meSlotSize, meSlotSize + dpToPx(20))
                    mySlotBottom
                } else {
                    playerView.widthHeightRaw(playerSize, playerSize + dpToPx(20))
                    slotPositions[position]!!
                }

                animate().apply {
                    x(slotPosition.x)
                    y(slotPosition.y)
                    start()
                }

                /*x = slotPosition.x
                y = slotPosition.y*/

                nameTV.text = slot.user_name
                playerView.marginsRaw(
                    slotPosition.player.ml, slotPosition.player.mt,
                    slotPosition.player.mr, slotPosition.player.mb
                )
                /*name_inplay_group.marginsRaw(
                    slotPosition.player.ml, slotPosition.player.mt,
                    slotPosition.player.mr, slotPosition.player.mb
                )*/
                raise_amt.marginsRaw(
                    slotPosition.raiseAmt.ml, slotPosition.raiseAmt.mt,
                    slotPosition.raiseAmt.mr, slotPosition.raiseAmt.mb
                )

                crown.marginsRaw(
                    slotPosition.crown.ml, slotPosition.crown.mt,
                    slotPosition.crown.mr, slotPosition.crown.mb
                )

                revealCards.marginsRaw(
                    slotPosition.revealCards.ml, slotPosition.revealCards.mt,
                    slotPosition.revealCards.mr, slotPosition.revealCards.mb
                )
                player_action.layoutGravity(slotPosition.playerAction)
                dealer.layoutGravity(slotPosition.deal)

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
                raise_amt.visibility = VISIBLE*/

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

    fun getPositionBySeatNumber(seatNo: Int): SlotPosition {
        val position = slotPositionMap[seatNo]
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
                it.value.iv_userProfile.alpha = 1f
                it.value.iv_userProfile.circleBackgroundColor = 0
            }
            drawSlots(slots.values.toList())
        } catch (ex: Exception) {
        }
    }
}