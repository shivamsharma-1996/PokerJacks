package com.gtgt.pokerjacks.ui.game.view.slot

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.RelativeLayout
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.extensions.*
import com.gtgt.pokerjacks.ui.game.models.TableSlot
import com.gtgt.pokerjacks.ui.game.models.TableSlotStatus
import com.gtgt.pokerjacks.utils.CustomCountDownTimer
import com.gtgt.pokerjacks.utils.PlayerPositions.BOTTOM_CENTER
import kotlinx.android.synthetic.main.activity_game.view.*
import kotlinx.android.synthetic.main.player.view.*

class SlotViews(private val rootLayout: RelativeLayout) {
    private val context = rootLayout.context
    private val userId = retrieveString("USER_ID")
    private var animateTimer: CustomCountDownTimer? = null

    private val slotViews = mutableMapOf<Int, View>()
    private val slots = mutableMapOf<Int, TableSlot>()

    private val topMargin = dpToPx(38)
    private val leftMargin = dpToPx(60)
    private val playerSize = dpToPx(63).toFloat()
    private val meSlotSize = dpToPx(73).toFloat()
    private val roundingSize = dpToPx(5).toFloat()
    private val tableWidth = rootLayout.width.toFloat()
    private val tableHeight = rootLayout.playArea.height.toFloat()
    private lateinit var mySlotBottom: SlotPosition

    var totalSlots: Int = 0
        set(value) {
            field = value

            (1..totalSlots).forEach {
                val v = LayoutInflater.from(context).inflate(R.layout.player, null)
                v.visibility = GONE
                rootLayout.addView(v)
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

    var isJoined = false

    fun drawSlots(slots: List<TableSlot>) {
        val selectedSlotPositions = when (totalSlots) {
            9 -> slots9Positions
            6 -> if (isJoined) slots6PositionsJoined else slots6Positions
            else -> slots2Positions
        }

        slots.forEach { slot ->
            val position = selectedSlotPositions[slot.seat_no - 1]
            this.slots[slot.seat_no] = slot
            val slotView = slotViews[slot.seat_no]

            slotView?.apply {

                log("slotStatus", slot.status)
                if(slot.status == TableSlotStatus.VACANT.name) {
                    noPlayer.visibility = VISIBLE
                    iv_userProfile.visibility = GONE
                } else {
                    noPlayer.visibility = GONE
                    iv_userProfile.visibility = VISIBLE
                }

                widthHeightRaw(WRAP_CONTENT, WRAP_CONTENT)
                visibility = View.VISIBLE

                val slotPosition = if (isJoined && position == BOTTOM_CENTER) {
                    playerView.widthHeightRaw(meSlotSize, meSlotSize)
                    mySlotBottom
                } else {
                    playerView.widthHeightRaw(playerSize, playerSize)
                    slotPositions[position]!!
                }

                x = slotPosition.x
                y = slotPosition.y
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

                dealer.layoutGravity(Gravity.START)
            }
        }
    }
}