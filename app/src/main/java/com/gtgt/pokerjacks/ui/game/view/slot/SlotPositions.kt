package com.gtgt.pokerjacks.ui.game.view.slot

import android.view.Gravity
import com.gtgt.pokerjacks.extensions.dpToPx
import com.gtgt.pokerjacks.ui.lobby.adapter.cardHeight
import com.gtgt.pokerjacks.utils.SlotPositions
import com.gtgt.pokerjacks.utils.SlotPositions.*
import retrofit2.http.POST
import kotlin.math.round

data class Position(
    val alignment: Int = -1,
    val ml: Int = 0,
    val mt: Int = 0,
    val mr: Int = 0,
    val mb: Int = 0
)

data class SlotPosition(
    val x: Float,
    val y: Float,
    val player: Position,
    val inPlay: Position,
    val raiseAmt: Position = Position(),
    val crown: Position = Position(),
    val revealCards: Position = Position(),
    val activeIndication: Int,
    val playerAction: Position = Position(),
    val deal: Position = Position()
)

var slotPositions = mutableMapOf<SlotPositions, SlotPosition>()
var slots9Positions = listOf(
    BOTTOM_CENTER,
    LEFT_BOTTOM,
    LEFT_BOTTOM_CENTER,
    LEFT_TOP_CENTER,
    LEFT_TOP,
    RIGHT_TOP,
    RIGHT_TOP_CENTER,
    RIGHT_BOTTOM_CENTER,
    RIGHT_BOTTOM
)

var slots6Positions = listOf(
    RIGHT_BOTTOM,
    RIGHT_CENTER,
    RIGHT_TOP,
    LEFT_TOP,
    LEFT_CENTER,
    LEFT_BOTTOM
)

var slots6PositionsTable = listOf(
    BOTTOM_CENTER,
    LEFT_CENTER,
    LEFT_TOP,
    TOP_CENTER,
    RIGHT_TOP,
    RIGHT_CENTER
)

var slots6PortraitPositionsTable = listOf(
    BOTTOM_CENTER,
    LEFT_BOTTOM_CENTER,
    LEFT_TOP_CENTER,
    TOP_CENTER,
    RIGHT_TOP_CENTER,
    RIGHT_BOTTOM_CENTER
)
val slots2Positions = listOf(TOP_CENTER, BOTTOM_CENTER)

val slotPositionMap = mutableMapOf<Int, SlotPositions>()
val slotPosition6TableMap = mutableMapOf<Int, SlotPositions>()

fun makeSlotPositions(
    playerSize: Float,
    topMargin: Int,
    leftMargin: Int,
    tableWidth: Float,
    tableHeight: Float,
    roundingSize: Float,
    meSlotSize: Float,
    isLandscape: Boolean
): SlotPosition {
    val playerMargin = dpToPx(26)
    val inPlayWidth = dpToPx(65)
    val inPlayHeight = dpToPx(30)
    val crownWidth = dpToPx(40)
    val revealCardsTop = dpToPx(14)
    val dealerBottomMargin = dpToPx(10)

    if (isLandscape) {
        slotPositions[LEFT_TOP_CENTER] = SlotPosition(
            x = playerSize / 3 + leftMargin / 2,
            y = playerSize/1.5f,
            player = Position(),
            crown = Position(ml = (playerSize / 4 + (playerSize - crownWidth) *1.2f).toInt()),
            revealCards = Position(),
            inPlay = Position(),
            raiseAmt = Position(
                mt = dpToPx(10),
                ml = (playerSize + roundingSize * 10 + dpToPx(20)).toInt(),
                alignment = Gravity.END or Gravity.CENTER
            ),
            activeIndication = Gravity.START,
            playerAction =  Position(alignment = Gravity.END or Gravity.CENTER, mt = dpToPx(5)),
            deal = Position(alignment = Gravity.START or Gravity.BOTTOM, mb = topMargin-5, ml =  playerMargin - dpToPx(10))
        )
    } else {
        slotPositions[LEFT_TOP_CENTER] = SlotPosition(
            x = playerMargin.toFloat() /  4 - roundingSize * 2,
            y = (tableHeight / 3.2f), //copied from left-center-bottom
            player = Position(),
            crown = Position(
                ml = (playerSize / 4 + (playerSize - crownWidth) / 2).toInt(),
                mt = (roundingSize * 2).toInt()
            ),
            revealCards = Position(),
            inPlay = Position(),
            raiseAmt = Position(
                alignment = Gravity.END or Gravity.CENTER,
                mt = dpToPx(5),
                ml =  (playerSize + roundingSize * 10 + dpToPx(10)).toInt()),
            activeIndication = Gravity.START,
            playerAction =  Position(alignment = Gravity.END or Gravity.CENTER, mt = dpToPx(5)),
            deal = Position(alignment = Gravity.START or Gravity.BOTTOM, mb = inPlayHeight - topMargin/3 -dpToPx(5))
        )
    }
    if (isLandscape) {
        slotPositions[LEFT_TOP] = SlotPosition(
            x = 2.8f * playerSize - inPlayWidth + leftMargin + inPlayWidth,
            y = topMargin/15f - roundingSize.toInt() *1.5f,
            player = Position(),
            revealCards = Position(mt = roundingSize.toInt()),
            crown = Position(ml = (inPlayWidth + (playerSize - crownWidth) / 2f).toInt()),
            inPlay = Position(mt = topMargin / 4),
            raiseAmt = Position(
                alignment = Gravity.BOTTOM or Gravity.CENTER
            ),
            activeIndication = Gravity.END,
            playerAction =  Position(alignment = Gravity.BOTTOM or Gravity.CENTER),
            deal = Position(alignment = Gravity.START or Gravity.TOP, mt = topMargin*2 - dpToPx(12), ml = playerMargin*2 - dpToPx(3))
        )
    } else {
        slotPositions[LEFT_TOP] = SlotPosition(
            x = playerMargin.toFloat() /  4 - roundingSize * 2,
            y = 2.2f * playerSize + leftMargin - inPlayWidth,
            player = Position(),
            revealCards = Position(),
            crown = Position(
                ml = (playerSize / 4 + (playerSize - crownWidth) / 2).toInt()
            ),
            inPlay = Position(mt = topMargin / 4),
            raiseAmt = Position(
                alignment = Gravity.END or Gravity.CENTER,
                mt = dpToPx(5),
                ml =  (playerSize + roundingSize * 10 + dpToPx(10)).toInt()),
            activeIndication = Gravity.END,
            playerAction =  Position(alignment = Gravity.END or Gravity.CENTER, mt = dpToPx(5)),
            deal = Position(alignment = Gravity.START or Gravity.TOP, mt = topMargin + roundingSize.toInt()*3)
        )
    }

    if (isLandscape) {
        slotPositions[TOP_CENTER] = SlotPosition(
            x = (tableWidth - meSlotSize) / 2 - inPlayWidth/2,
            y = -dpToPx(5).toFloat(),
            player = Position(),
            inPlay = Position(mt = topMargin / 4),
            revealCards = Position(mt = roundingSize.toInt()),
            crown = Position(ml = (inPlayWidth + (playerSize - crownWidth) / 2f).toInt()),
            raiseAmt = Position(
                mt = (playerSize - roundingSize).toInt() + playerMargin / 2,
                alignment = Gravity.BOTTOM or Gravity.CENTER
            ),
            activeIndication = Gravity.END,
            playerAction =  Position(alignment = Gravity.BOTTOM or Gravity.CENTER),
            deal = Position(alignment = Gravity.END or Gravity.BOTTOM, mb = inPlayHeight - topMargin/3)
        )
    } else {
        slotPositions[TOP_CENTER] = SlotPosition(
            x = (tableWidth - playerSize) / 2.3f -inPlayWidth/4,
            y = 1.8f * playerSize - inPlayWidth, //copied from left_bottom
            player = Position(
            ),
            inPlay = Position(mt = topMargin / 4),
            revealCards = Position(),
            crown = Position(ml = (inPlayWidth + (playerSize - crownWidth) / 2f).toInt()),
            raiseAmt = Position(
                alignment = Gravity.BOTTOM or Gravity.CENTER
            ),
            activeIndication = Gravity.END,
            playerAction = Position(alignment = Gravity.BOTTOM or Gravity.CENTER),
            deal = Position(alignment = Gravity.START or Gravity.TOP , mt = topMargin*2 - dpToPx(10) - playerMargin, ml = inPlayWidth+ playerSize.toInt() + dpToPx(3))
        )
    }

    if (isLandscape) {
        slotPositions[RIGHT_TOP] =
            SlotPosition(
                x = tableWidth - 4f * playerSize - leftMargin - inPlayWidth / 2,
                y = topMargin/15f - roundingSize.toInt() *1.5f,
                player = Position(),
                crown = Position(ml = ((inPlayWidth + playerSize - crownWidth) / 2f).toInt()),
                inPlay = Position(ml = playerSize.toInt() + inPlayWidth / 2, mt = topMargin / 4),
                raiseAmt = Position(
                    alignment = Gravity.BOTTOM or Gravity.CENTER
                ),
                revealCards = Position(mt = roundingSize.toInt()),
                activeIndication = Gravity.START,
                playerAction =  Position(alignment = Gravity.BOTTOM or Gravity.CENTER),
                deal = Position(alignment = Gravity.END or Gravity.TOP, mt = topMargin*2 - dpToPx(12))
        )
    } else {
        slotPositions[RIGHT_TOP] =
            SlotPosition(
                x = tableWidth - 1.4f * playerSize - leftMargin - inPlayWidth ,
                y = 2.2f * playerSize + leftMargin - inPlayWidth,
                player = Position(
                    ml = (playerSize/1.5f).toInt()),
                revealCards = Position(),
                crown = Position(
                    ml = (playerSize / 4 + inPlayWidth + (playerSize - crownWidth) / 2).toInt()
                ),
                inPlay = Position(ml = playerSize.toInt() + inPlayWidth / 2, mt = topMargin / 4),
                raiseAmt = Position(
                    alignment = Gravity.START or Gravity.CENTER,
                    mt = dpToPx(5)
                ),
                activeIndication = Gravity.START,
                playerAction =  Position(alignment = Gravity.START or Gravity.CENTER, mt = dpToPx(5)),
                deal = Position(alignment = Gravity.END or Gravity.TOP, mt = topMargin + roundingSize.toInt()*3)
            )
    }

    if (isLandscape) {
        slotPositions[RIGHT_TOP_CENTER] =
            SlotPosition(
                x = tableWidth - playerSize - leftMargin - inPlayWidth - (roundingSize * 10).toInt(),
                y = playerSize/1.5f,
                player = Position(
                    ml = (playerSize/1.2f).toInt()
                ),
                crown = Position(
                    ml = (playerSize / 4 + inPlayWidth + (playerSize - crownWidth) / 1.5f).toInt(),
                    mt = (roundingSize * 3).toInt()
                ),
                inPlay = Position(ml = (inPlayWidth / 3.5).toInt()),
                revealCards = Position(),
                raiseAmt = Position(
                    mt = dpToPx(10),
                    alignment = Gravity.START or Gravity.CENTER
                ),
                activeIndication = Gravity.END,
                playerAction =  Position(alignment = Gravity.START or Gravity.CENTER, mt = dpToPx(5)),
                deal = Position(alignment = Gravity.END or Gravity.BOTTOM, mb = playerMargin + playerMargin/2)
        )
    } else {
        slotPositions[RIGHT_TOP_CENTER] =
            SlotPosition(
                x = tableWidth - 1.4f * playerSize - leftMargin - inPlayWidth ,
                y = (tableHeight / 3.2f), //copied from left-center-bottom
                player = Position(
                    ml = (playerSize/1.5f).toInt()),
                crown = Position(
                    ml = (playerSize / 4 + inPlayWidth + (playerSize - crownWidth) / 2).toInt(),
                    mt = (roundingSize * 2).toInt()
                ),
                inPlay = Position(ml = (inPlayWidth / 3.5).toInt()),
                revealCards = Position(),
                raiseAmt = Position(
                    alignment = Gravity.START or Gravity.CENTER,
                    mt = dpToPx(5)
                ),
                activeIndication = Gravity.START,
                playerAction =  Position(
                    alignment = Gravity.START or Gravity.CENTER,
                    mt = dpToPx(5)),
                deal = Position(alignment = Gravity.END or Gravity.BOTTOM, mb = inPlayHeight - topMargin/3 -dpToPx(5))
            )
    }

    if (isLandscape) {
        slotPositions[RIGHT_BOTTOM_CENTER] =
            SlotPosition(
                x = tableWidth - playerSize - leftMargin - inPlayWidth - (roundingSize * 10).toInt(),
                y = tableHeight - 2f * playerSize + topMargin,
                player = Position(
                    ml = (playerSize/1.2f).toInt()),
                crown = Position(
                    ml = (playerSize / 4 + inPlayWidth + (playerSize - crownWidth) / 2).toInt(),
                    mt = (roundingSize * 3).toInt()
                ),
                revealCards = Position(),
                inPlay = Position(
                    mt = playerSize.toInt() + inPlayHeight - playerMargin / 2,
                    ml = 2 * roundingSize.toInt() + inPlayWidth
                ),
                raiseAmt = Position(
                    mt = dpToPx(10),
                alignment = Gravity.START or Gravity.CENTER),
                activeIndication = Gravity.END,
                playerAction =  Position(alignment = Gravity.START or Gravity.CENTER, mt = dpToPx(5)),
                deal = Position(alignment = Gravity.END or Gravity.BOTTOM, mb = playerMargin + playerMargin/2)
        )
    } else {
        slotPositions[RIGHT_BOTTOM_CENTER] =
            SlotPosition(
                x = tableWidth - 1.4f * playerSize - leftMargin - inPlayWidth ,
                y = tableHeight / 1.8f,
                player = Position(
                    ml = (playerSize/1.5f).toInt()),
                crown = Position(
                    ml = (playerSize / 4 + inPlayWidth + (playerSize - crownWidth) / 2).toInt(),
                    mt = (roundingSize * 3).toInt()
                ),
                revealCards = Position(),
                inPlay = Position(
                    mt = playerSize.toInt() + inPlayHeight - playerMargin / 2,
                    ml = 2 * roundingSize.toInt() + inPlayWidth
                ),
                raiseAmt = Position(
                    alignment = Gravity.START or Gravity.CENTER,
                    mt = dpToPx(5)
                ),
                activeIndication = Gravity.START,
                playerAction =  Position(
                    alignment = Gravity.START or Gravity.CENTER,
                    mt = dpToPx(5)),
                deal = Position(alignment = Gravity.END or Gravity.TOP, mt = topMargin + roundingSize.toInt()*6)
            )
    }

    if (isLandscape) {
        slotPositions[RIGHT_BOTTOM] =
            SlotPosition(
                x = tableWidth - 3.3f * playerSize - leftMargin/5f - inPlayWidth,
                y = tableHeight - playerSize + topMargin * 2.2f - inPlayHeight / 1.5f,
                player = Position(),
                crown = Position(
                    ml = (inPlayWidth + (playerSize - crownWidth) / 2).toInt(),
                    mt = (roundingSize * 4).toInt()
                ),
                revealCards = Position(
                    alignment = Gravity.TOP or Gravity.END
                ),
                raiseAmt = Position(ml = 0, alignment = Gravity.TOP or Gravity.CENTER),
                inPlay = Position(
                    mt = (playerSize).toInt(),
                    ml = playerSize.toInt() + inPlayWidth
                ),
                activeIndication = Gravity.END,
                playerAction =  Position(alignment = Gravity.TOP or Gravity.CENTER, mt = dpToPx(5)),
                deal = Position(alignment = Gravity.END or Gravity.BOTTOM, mb = inPlayHeight - topMargin/2)
            )
    } else {
        slotPositions[RIGHT_BOTTOM] =
            SlotPosition(
                x = tableWidth - 1.4f * playerSize - leftMargin - inPlayWidth ,
                y = tableHeight / 1.45f,
                player = Position(
                    ml = (playerSize/1.5f).toInt()),
                crown = Position(
                    ml = (playerSize / 4 + inPlayWidth + (playerSize - crownWidth) / 2).toInt(),
                    mt = (roundingSize * 4).toInt()
                ),
                revealCards = Position(),
                inPlay = Position(
                    mt = (playerSize).toInt(),
                    ml = playerSize.toInt() + inPlayWidth
                ),
                raiseAmt = Position(
                    alignment = Gravity.START or Gravity.CENTER,
                    mt = dpToPx(5)
                ),
                activeIndication = Gravity.START,
                playerAction =  Position(
                    alignment = Gravity.START or Gravity.CENTER,
                    mt = dpToPx(5)
                ),
                deal = Position(alignment = Gravity.END or Gravity.BOTTOM, mb = inPlayHeight - topMargin/3 -dpToPx(5))
            )
    }
    if (isLandscape) {
        slotPositions[BOTTOM_CENTER] =
            SlotPosition(
                x = (tableWidth - playerSize) / 2.15f,
                y = tableHeight - playerSize * 1.3f + topMargin * 2.17f,
                player = Position(),
                crown = Position(
                    ml = ((meSlotSize - crownWidth)/1.2).toInt(),
                    mt = (topMargin / 1.5).toInt()
                ),
                inPlay = Position(
                    mt = (playerSize - inPlayHeight / 1.5 + topMargin).toInt(),
                    ml = playerSize.toInt()
                ),
                revealCards = Position(
                    alignment = -1
                ),
                raiseAmt = Position(ml = 0, alignment = Gravity.TOP or Gravity.CENTER,  mt =  dpToPx(5)),
                activeIndication = Gravity.START,
                playerAction =  Position(alignment = Gravity.TOP or Gravity.CENTER, mt = dpToPx(5)),
                deal = Position(alignment = Gravity.START or Gravity.BOTTOM, mb = inPlayHeight - topMargin/2)
            )
    } else {
        slotPositions[BOTTOM_CENTER] =
            SlotPosition(
                /* x = playerSize / 2 + leftMargin / 2 - 2,
                 y = tableHeight/1.3f ,
                 */
                x = (tableWidth - playerSize) / 2.3f -inPlayWidth/4,
                y = tableHeight / 1.2f,
                player = Position(),
                crown = Position(
                    ml = ((playerSize - crownWidth)/1.2).toInt(),
                    mt = (topMargin / 1.5).toInt()
                ),
                inPlay = Position(
                    mt = (playerSize - inPlayHeight / 1.5 + topMargin).toInt(),
                    ml = playerSize.toInt()
                ),
                raiseAmt = Position(alignment = Gravity.TOP or Gravity.CENTER, mt =  dpToPx(5)),
                activeIndication = Gravity.START,
                playerAction =  Position(alignment = Gravity.TOP or Gravity.CENTER, mt = dpToPx(5)),
                deal = Position(Gravity.BOTTOM or Gravity.START,mb = inPlayHeight - topMargin/2 + dpToPx(5))
            )
    }


    if (isLandscape) {
        slotPositions[LEFT_BOTTOM] =
            SlotPosition(
                x = 3.3f * playerSize + leftMargin/5f - inPlayWidth,
                y = tableHeight - playerSize + topMargin * 2.2f - inPlayHeight / 1.5f,
                player = Position(),
                crown = Position(
                    ml = (inPlayWidth + (playerSize - crownWidth) / 2).toInt(),
                    mt = (roundingSize * 3).toInt()
                ),
                revealCards = Position(
                    alignment = Gravity.TOP or Gravity.END
                ),
                raiseAmt = Position(ml = 0, alignment = Gravity.TOP or Gravity.CENTER),
                inPlay = Position(mt = (playerSize - inPlayHeight / 1.5 + inPlayHeight / 2).toInt()),
                activeIndication = Gravity.START,
                playerAction =  Position(alignment = Gravity.TOP or Gravity.CENTER, mt = dpToPx(5)),
                deal = Position(alignment = Gravity.START or Gravity.BOTTOM, mb = inPlayHeight - topMargin/2, ml = playerMargin*2 - dpToPx(5))
        )
    } else {
        slotPositions[LEFT_BOTTOM] =
            SlotPosition(
                x = playerMargin.toFloat() /  4 - roundingSize * 2,
                y = tableHeight / 1.45f,
                player = Position(),
                crown = Position(
                    ml = (playerSize / 4 + (playerSize - crownWidth) / 2).toInt(),
                    mt = (roundingSize * 3).toInt()
                ),
                revealCards = Position(
                ),
                inPlay = Position(mt = (playerSize - inPlayHeight / 1.5 + inPlayHeight / 2).toInt()),
                raiseAmt = Position(
                    alignment = Gravity.END or Gravity.CENTER,
                    mt = dpToPx(5),
                    ml =  (playerSize + roundingSize * 10 + dpToPx(10)).toInt()),
                activeIndication = Gravity.START,
                playerAction =  Position(alignment = Gravity.END or Gravity.CENTER, mt = dpToPx(5)),
                deal = Position(alignment = Gravity.START or Gravity.BOTTOM, mb = inPlayHeight - topMargin/3 -dpToPx(5))
            )

    }

    if (isLandscape) {
        slotPositions[LEFT_BOTTOM_CENTER] =
            SlotPosition(
                x = playerSize / 3 + leftMargin / 2,
                y = tableHeight - 2f * playerSize + topMargin,
                player = Position(),
                crown = Position(
                    ml = (playerSize / 4 + (playerSize - crownWidth)).toInt(),
                    mt = (roundingSize * 3).toInt()
                ),
                raiseAmt = Position(ml = (playerSize + roundingSize * 10 + dpToPx(20)).toInt(),
                    mt = dpToPx(10),
                    alignment = Gravity.END or Gravity.CENTER),
                inPlay = Position(
                    mt = playerSize.toInt() + inPlayHeight - playerMargin / 2,
                    ml = 2 * roundingSize.toInt()
                ),
                activeIndication = Gravity.START,
                playerAction =  Position(alignment = Gravity.END or Gravity.CENTER, mt = dpToPx(5)),
                deal = Position(alignment = Gravity.START or Gravity.BOTTOM, mb = topMargin, ml = playerMargin - dpToPx(15))
        )
    } else {
        slotPositions[LEFT_BOTTOM_CENTER] =
            SlotPosition(
                x = playerMargin.toFloat() /  4 - roundingSize * 2,
                y = tableHeight / 1.8f,
                player = Position(
                ),
                crown = Position(
                    ml = (playerSize / 4 + (playerSize - crownWidth) / 2).toInt(),
                    mt = (roundingSize * 3).toInt()
                ),
                revealCards = Position(
                ),
                inPlay = Position(
                    mt = playerSize.toInt() + inPlayHeight - playerMargin / 2,
                    ml = 2 * roundingSize.toInt()
                ),
                raiseAmt = Position(
                    alignment = Gravity.END or Gravity.CENTER,
                    mt = dpToPx(5),
                    ml =  (playerSize + roundingSize * 10 + dpToPx(10)).toInt()),
                activeIndication = Gravity.START,
                playerAction =  Position(alignment = Gravity.END or Gravity.CENTER, mt = dpToPx(5)),
                deal = Position(alignment = Gravity.START or Gravity.TOP, mt = topMargin + roundingSize.toInt()*6)
            )
    }


    slotPositions[LEFT_CENTER] =
        SlotPosition(
            x = roundingSize + leftMargin.toFloat() - roundingSize * 2,
            y = (tableHeight - playerSize) / 2 + topMargin / 2,
            player = Position(),
            revealCards = Position(),
            crown = Position(
                ml = ((playerSize - crownWidth)).toInt(),
                mt = topMargin / 2
            ),
            inPlay = Position(mt = (playerSize + roundingSize + topMargin / 2).toInt()),
            raiseAmt = Position(
                ml =  (playerSize + roundingSize * 10 + dpToPx(15)).toInt(),
                mt = dpToPx(10),
                alignment = Gravity.END or Gravity.CENTER),
            activeIndication = Gravity.START,
            playerAction =  Position(alignment = Gravity.END or Gravity.CENTER, mt = dpToPx(5)),
            deal = Position(alignment = Gravity.START or Gravity.BOTTOM, mb = inPlayHeight - topMargin/2)
        )
    slotPositions[RIGHT_CENTER] =
        SlotPosition(
            x = tableWidth - playerSize - leftMargin - inPlayWidth - (roundingSize * 10).toInt(),
            y = (tableHeight - playerSize) / 2 + topMargin / 2,
            player = Position(
                ml = (playerSize/1.2f).toInt()
            ),
            revealCards = Position(),
            crown = Position(
                ml = (inPlayWidth + (playerSize - crownWidth) / 2f).toInt(),
                mt = topMargin / 2
            ),
            inPlay = Position(
                ml = inPlayWidth,
                mt = (playerSize + roundingSize + topMargin / 2).toInt()
            ),
            raiseAmt = Position(
                mt = dpToPx(10),
                alignment = Gravity.START or Gravity.CENTER
            ),
            activeIndication = Gravity.START,
            playerAction =  Position(alignment = Gravity.START or Gravity.CENTER, mt = dpToPx(5)),
            deal = Position(alignment = Gravity.END or Gravity.BOTTOM, mb = inPlayHeight - topMargin/2)
        )

    if (isLandscape) {
        return SlotPosition(
            x = (tableWidth - meSlotSize) / 2 - inPlayWidth/2,
            y = tableHeight - playerSize * 1.3f + topMargin * 2.17f,
            player = Position(ml = inPlayWidth/4, mr = inPlayWidth/4),
            crown = Position(
                ml = ((meSlotSize - crownWidth) ).toInt(),
                mt = (topMargin / 1.5).toInt()
            ),
            inPlay = Position(
                mt = (meSlotSize - inPlayHeight / 1.5 + topMargin).toInt(),
                ml = meSlotSize.toInt()
            ),
            raiseAmt = Position(ml = 0, alignment = Gravity.TOP or Gravity.CENTER),
            activeIndication = Gravity.START,
            playerAction =  Position(alignment = Gravity.TOP or Gravity.CENTER, mt = dpToPx(5)),
            deal = Position(alignment = Gravity.START or Gravity.BOTTOM, mb = inPlayHeight - topMargin/2)
        )
    } else {
        return SlotPosition(
            x = (tableWidth - playerSize) / 2.3f -inPlayWidth/4,
            y = tableHeight / 1.2f,
            player = Position(),
            crown = Position(
                ml = ((meSlotSize - crownWidth)).toInt(),
                mt = (topMargin / 1.5).toInt()
            ),
            inPlay = Position(
                mt = (meSlotSize - inPlayHeight / 1.5 + topMargin).toInt(),
                ml = meSlotSize.toInt()
            ),
            raiseAmt = Position(alignment = Gravity.TOP or Gravity.CENTER, mt =  dpToPx(5)),
            activeIndication = Gravity.START,
            playerAction =  Position(alignment = Gravity.TOP or Gravity.CENTER, mt =  dpToPx(5)),
            deal = Position(Gravity.BOTTOM or Gravity.START,mb = inPlayHeight - topMargin/2 + dpToPx(5))
        )
    }

}