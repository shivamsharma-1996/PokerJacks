package com.gtgt.pokerjacks.ui.game.view.slot

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

    private val topMargin = dpToPx(38)
    private val leftMargin = dpToPx(60)
    private val playerSize = dpToPx(63).toFloat()
    private val meSlotSize = dpToPx(73).toFloat()
    private val roundingSize = dpToPx(5).toFloat()
    private val tableWidth = rootLayout.width.toFloat()
    private val tableHeight = rootLayout.playArea.height.toFloat()
    private lateinit var mySlotBottom: SlotPosition

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

                mySlotBottom = makeSlotPositions(
                    playerSize,
                    topMargin,
                    leftMargin,
                    tableWidth,
                    tableHeight,
                    roundingSize,
                    meSlotSize
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
            slotViews[value.current_bettor_position]!!.apply {
                iv_userProfile.visibility = GONE
                animateView.startAnim(value) {
                    iv_userProfile.visibility = VISIBLE
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
                    in_play_amt.visibility = GONE
                    raise_amt.visibility = GONE
                } else {
                    noPlayer.visibility = GONE
                    iv_userProfile.visibility = VISIBLE
                    active_indication.visibility = VISIBLE
                    in_play_amt.visibility = VISIBLE

                    if (slot.user != null && slot.user!!.current_round_invested > 0.0) {
                        raise_amt.visibility = VISIBLE
                    } else {
                        raise_amt.visibility = GONE
                    }
                }

                widthHeightRaw(WRAP_CONTENT, WRAP_CONTENT)
                visibility = VISIBLE

                val slotPosition = if (isJoined && position == BOTTOM_CENTER) {
                    playerView.widthHeightRaw(meSlotSize, meSlotSize)
                    mySlotBottom
                } else {
                    playerView.widthHeightRaw(playerSize, playerSize)
                    slotPositions[position]!!
                }

                animate().apply {
                    x(slotPosition.x)
                    y(slotPosition.y)
                    start()
                }

                /*x = slotPosition.x
                y = slotPosition.y*/

                playerView.marginsRaw(
                    slotPosition.player.ml, slotPosition.player.mt,
                    slotPosition.player.mr, slotPosition.player.mb
                )
                in_play_amt.marginsRaw(
                    slotPosition.inPlay.ml, slotPosition.inPlay.mt,
                    slotPosition.inPlay.mr, slotPosition.inPlay.mb
                )
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

                dealer.layoutGravity(Gravity.START)

                /*active_indication.visibility = VISIBLE
                in_play_amt.visibility = VISIBLE
                raise_amt.visibility = VISIBLE
                crown.visibility = VISIBLE
                revealCards.visibility = VISIBLE*/

                raise_amt.text =
                    "${String.format("%.2f", (slot.user?.current_round_invested ?: 0.00))}"

                in_play_amt.text =
                    "₹${(slot.user?.game_inplay_amount ?: slot.inplay_amount).toDecimalFormat()}"
            }
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
            }
            drawSlots(slots.values.toList())
        } catch (ex: Exception) {
        }
    }
}