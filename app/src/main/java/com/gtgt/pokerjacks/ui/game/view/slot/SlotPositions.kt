package com.gtgt.pokerjacks.ui.game.view.slot

import android.view.Gravity
import com.gtgt.pokerjacks.extensions.dpToPx
import com.gtgt.pokerjacks.ui.lobby.adapter.cardHeight
import com.gtgt.pokerjacks.utils.SlotPositions
import com.gtgt.pokerjacks.utils.SlotPositions.*

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
    val deal: Int
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

    if (isLandscape) {
        slotPositions[LEFT_TOP_CENTER] = SlotPosition(
            x = leftMargin.toFloat(),
            y = playerSize,
            player = Position(
                ml = (playerSize / 4).toInt(),
                mb = playerMargin
            ),
            crown = Position(ml = (playerSize / 4 + (playerSize - crownWidth) / 2).toInt()),
            revealCards = Position(ml = (playerSize / 4 + playerSize).toInt(), mt = cardHeight / 2),
            inPlay = Position(),
            raiseAmt = Position(
                mt = playerSize.toInt() - topMargin / 4,
                ml = (playerSize + roundingSize * 4).toInt()
            ),
            activeIndication = Gravity.START,
            deal = Gravity.END
        )
    } else {
        slotPositions[LEFT_TOP_CENTER] = SlotPosition(
            x = playerMargin.toFloat() / 2,
            y = (tableHeight / 3f), //copied from left-center-bottom
            player = Position(
                mt = playerMargin / 4,
                ml = (playerMargin / 3)
            ),
            crown = Position(
                ml = (playerSize / 4 + (playerSize - crownWidth) / 4).toInt(),
                mt = (roundingSize * 2).toInt()
            ),
            revealCards = Position(ml = (playerSize / 4 + playerSize).toInt(), mt = cardHeight / 2),
            inPlay = Position(),
            raiseAmt = Position(
                mt = playerSize.toInt() - topMargin / 4,
                ml = (playerSize + roundingSize * 4).toInt()
            ),
            activeIndication = Gravity.START,
            deal = Gravity.END
        )
    }
    if (isLandscape) {
        slotPositions[LEFT_TOP] = SlotPosition(
            x = 2 * playerSize - inPlayWidth + leftMargin,
            y = 0f,
            player = Position(ml = inPlayWidth),
            revealCards = Position(ml = (inPlayWidth + playerSize).toInt(), mt = revealCardsTop),
            crown = Position(ml = (inPlayWidth + (playerSize - crownWidth) / 2f).toInt()),
            inPlay = Position(mt = topMargin / 4),
            raiseAmt = Position(
                mt = (playerSize - roundingSize + topMargin / 4).toInt() + playerMargin / 2,
                ml = (playerSize + roundingSize * 3).toInt()
            ),
            activeIndication = Gravity.END,
            deal = Gravity.END or Gravity.BOTTOM
        )
    } else {
        slotPositions[LEFT_TOP] = SlotPosition(
            x = leftMargin.toFloat(),
            y = 2 * playerSize + leftMargin - inPlayWidth, //copied from left_bottom
            player = Position(
                ml = (playerSize / 4).toInt(),
                mb = playerMargin
            ),
            revealCards = Position(ml = (inPlayWidth + playerSize).toInt(), mt = revealCardsTop),
            crown = Position(
                ml = (playerSize / 4 + (playerSize - crownWidth) / 2).toInt()
            ),
            inPlay = Position(mt = topMargin / 4),
            raiseAmt = Position(
                mt = (playerSize - roundingSize + topMargin / 4).toInt() + playerMargin / 2,
                ml = (playerSize + roundingSize * 3).toInt()
            ),
            activeIndication = Gravity.END,
            deal = Gravity.END or Gravity.BOTTOM
        )
    }

    if (isLandscape) {
        slotPositions[TOP_CENTER] = SlotPosition(
            x = (tableWidth - playerSize) / 2 - inPlayWidth,
            y = 0f,
            player = Position(ml = inPlayWidth),
            inPlay = Position(mt = topMargin / 4),
            revealCards = Position(ml = (inPlayWidth + playerSize).toInt()),
            crown = Position(ml = (inPlayWidth + (playerSize - crownWidth) / 2f).toInt()),
            raiseAmt = Position(
                ml = (inPlayWidth + playerSize - roundingSize).toInt() + playerMargin / 2,
                mt = playerSize.toInt() - inPlayHeight / 2 + topMargin / 4
            ),
            activeIndication = Gravity.END,
            deal = Gravity.BOTTOM
        )
    } else {
        slotPositions[TOP_CENTER] = SlotPosition(
            x = (tableWidth - playerSize) / 2 - inPlayWidth,
            y = 1.8f * playerSize - inPlayWidth, //copied from left_bottom
            player = Position(ml = inPlayWidth),
            inPlay = Position(mt = topMargin / 4),
            revealCards = Position(ml = (inPlayWidth + playerSize).toInt()),
            crown = Position(ml = (inPlayWidth + (playerSize - crownWidth) / 2f).toInt()),
            raiseAmt = Position(
                ml = (inPlayWidth + playerSize - roundingSize).toInt() + playerMargin / 2,
                mt = playerSize.toInt() - inPlayHeight / 2 + topMargin / 4
            ),
            activeIndication = Gravity.END,
            deal = Gravity.BOTTOM
        )
    }

    if (isLandscape) {
        slotPositions[RIGHT_TOP] =
            SlotPosition(
                x = tableWidth - 3 * playerSize - leftMargin - inPlayWidth / 2,
                y = 0f,
                player = Position(ml = inPlayWidth / 2),
                revealCards = Position(mt = revealCardsTop),
                crown = Position(ml = ((inPlayWidth + playerSize - crownWidth) / 2f).toInt()),
                inPlay = Position(ml = playerSize.toInt() + inPlayWidth / 2, mt = topMargin / 4),
                raiseAmt = Position(
                    mt = (playerSize - roundingSize + topMargin / 4).toInt() + playerMargin / 2,
                    ml = (roundingSize * 9).toInt()
                ),
                activeIndication = Gravity.START,
                deal = Gravity.BOTTOM
            )
    } else {
        slotPositions[RIGHT_TOP] =
            SlotPosition(
                x = tableWidth - 1.5f * playerSize - leftMargin - inPlayWidth,
                y = 2 * playerSize + leftMargin - inPlayWidth,
                player = Position(ml = (playerSize / 4 + inPlayWidth).toInt()),
                revealCards = Position(mt = revealCardsTop),
                crown = Position(
                    ml = (playerSize / 3.5 + inPlayWidth + (playerSize - crownWidth) / 2).toInt()
                ),
                inPlay = Position(ml = playerSize.toInt() + inPlayWidth / 2, mt = topMargin / 4),
                raiseAmt = Position(
                    mt = (playerSize - roundingSize + topMargin / 4).toInt() + playerMargin / 2,
                    ml = (roundingSize * 9).toInt()
                ),
                activeIndication = Gravity.START,
                deal = Gravity.BOTTOM
            )
    }

    if (isLandscape) {
        slotPositions[RIGHT_TOP_CENTER] =
            SlotPosition(
                x = tableWidth - 1.5f * playerSize - leftMargin - inPlayWidth,
                y = playerSize - topMargin / 2f,
                player = Position(
                    ml = (playerSize / 4 + inPlayWidth).toInt(),
                    mt = playerMargin - topMargin / 4,
                    mb = playerMargin
                ),
                crown = Position(
                    ml = (playerSize / 4 + inPlayWidth + (playerSize - crownWidth) / 2).toInt(),
                    mt = (roundingSize * 3).toInt()
                ),
                inPlay = Position(ml = (inPlayWidth / 3.5).toInt()),
                revealCards = Position(ml = roundingSize.toInt() * 2, mt = cardHeight / 2),
                raiseAmt = Position(
                    mt = (playerSize + inPlayHeight / 2 - topMargin / 4 + roundingSize).toInt()
                ),
                activeIndication = Gravity.END,
                deal = Gravity.START
            )
    } else {
        slotPositions[RIGHT_TOP_CENTER] =
            SlotPosition(
                x = tableWidth - 1.1f * playerSize - leftMargin - inPlayWidth,
                y = tableHeight / 3f,
                player = Position(
                    ml = (playerSize / 4 + inPlayWidth).toInt(),
                    mt = playerMargin / 4,
                    mb = playerMargin
                ),
                crown = Position(
                    ml = (playerSize / 4 + inPlayWidth + (playerSize - crownWidth) / 2).toInt(),
                    mt = (roundingSize * 2).toInt()
                ),
                inPlay = Position(ml = (inPlayWidth / 3.5).toInt()),
                revealCards = Position(ml = roundingSize.toInt() * 2, mt = cardHeight / 2),
                raiseAmt = Position(
                    mt = (playerSize + inPlayHeight / 2 - topMargin / 4 + roundingSize).toInt()
                ),
                activeIndication = Gravity.END,
                deal = Gravity.START
            )
    }

    if (isLandscape) {
        slotPositions[RIGHT_BOTTOM_CENTER] =
            SlotPosition(
                x = tableWidth - 1.3f * playerSize - leftMargin - inPlayWidth,
                y = tableHeight - 1.7f * playerSize + topMargin,
                player = Position(
                    ml = (playerSize / 4 + inPlayWidth).toInt(),
                    mt = playerMargin / 2,
                    mb = playerMargin
                ),
                crown = Position(
                    ml = (playerSize / 4 + inPlayWidth + (playerSize - crownWidth) / 2).toInt(),
                    mt = (roundingSize * 3).toInt()
                ),
                revealCards = Position(ml = roundingSize.toInt() * 2, mt = -cardHeight / 2),
                inPlay = Position(
                    mt = playerSize.toInt() + inPlayHeight - playerMargin / 2,
                    ml = 2 * roundingSize.toInt() + inPlayWidth
                ),
                raiseAmt = Position(mt = (playerSize / 2 + roundingSize).toInt()),
                activeIndication = Gravity.END,
                deal = Gravity.START
            )
    } else {
        slotPositions[RIGHT_BOTTOM_CENTER] =
            SlotPosition(
                x = tableWidth - 1.1f * playerSize - leftMargin - inPlayWidth,
                y = tableHeight / 1.8f,
                player = Position(
                    ml = (playerSize / 4 + inPlayWidth).toInt(),
                    mt = playerMargin / 2,
                    mb = playerMargin
                ),
                crown = Position(
                    ml = (playerSize / 4 + inPlayWidth + (playerSize - crownWidth) / 2).toInt(),
                    mt = (roundingSize * 3).toInt()
                ),
                revealCards = Position(ml = roundingSize.toInt() * 2, mt = -cardHeight / 2),
                inPlay = Position(
                    mt = playerSize.toInt() + inPlayHeight - playerMargin / 2,
                    ml = 2 * roundingSize.toInt() + inPlayWidth
                ),
                raiseAmt = Position(mt = (playerSize / 2 + roundingSize).toInt()),
                activeIndication = Gravity.END,
                deal = Gravity.START
            )
    }

    if (isLandscape) {
        slotPositions[RIGHT_BOTTOM] =
            SlotPosition(
                x = tableWidth - 3 * playerSize - leftMargin - inPlayWidth,
                y = tableHeight - playerSize + topMargin * 1.5f - inPlayHeight / 1.5f,
                player = Position(ml = inPlayWidth, mt = (inPlayHeight / 1.5).toInt()),
                crown = Position(
                    ml = (inPlayWidth + (playerSize - crownWidth) / 2).toInt(),
                    mt = (roundingSize * 4).toInt()
                ),
                revealCards = Position(mt = (-cardHeight / 3).toInt()),
                inPlay = Position(
                    mt = (playerSize).toInt(),
                    ml = playerSize.toInt() + inPlayWidth
                ),
                raiseAmt = Position(mt = playerMargin),
                activeIndication = Gravity.END,
                deal = Gravity.START
            )
    } else {
        slotPositions[RIGHT_BOTTOM] =
            SlotPosition(
                x = tableWidth - 1.1f * playerSize - leftMargin - inPlayWidth,
                y = tableHeight / 1.3f,
                player = Position(
                    ml = (playerSize / 4 + inPlayWidth).toInt(),
                    mt = (inPlayHeight / 1.5).toInt()
                ),
                crown = Position(
                    ml = (playerSize / 4 + inPlayWidth + (playerSize - crownWidth) / 2).toInt(),
                    mt = (roundingSize * 4).toInt()
                ),
                revealCards = Position(mt = (-cardHeight / 3).toInt()),
                inPlay = Position(
                    mt = (playerSize).toInt(),
                    ml = playerSize.toInt() + inPlayWidth
                ),
                raiseAmt = Position(mt = playerMargin),
                activeIndication = Gravity.END,
                deal = Gravity.START
            )
    }
    if (isLandscape) {
        slotPositions[BOTTOM_CENTER] =
            SlotPosition(
                x = (tableWidth - playerSize) / 2,
                y = tableHeight - playerSize * 1.5f + topMargin * 1.5f,
                player = Position(mt = topMargin),
                crown = Position(
                    ml = ((playerSize - crownWidth) / 2f).toInt(),
                    mt = (topMargin)
                ),
                inPlay = Position(
                    mt = (playerSize - inPlayHeight / 1.5 + topMargin).toInt(),
                    ml = playerSize.toInt()
                ),
                raiseAmt = Position(ml = playerSize.toInt(), mt = topMargin + playerMargin),
                activeIndication = Gravity.START,
                deal = Gravity.END
            )
    } else {
        slotPositions[BOTTOM_CENTER] =
            SlotPosition(
                /* x = playerSize / 2 + leftMargin / 2 - 2,
                 y = tableHeight/1.3f ,
                 */
                x = (tableWidth - playerSize) / 2,
                y = tableHeight - playerSize * 1.5f + topMargin / 2.5f,
                player = Position(mt = topMargin),
                crown = Position(
                    ml = ((playerSize - crownWidth) / 2f).toInt(),
                    mt = (topMargin)
                ),
                inPlay = Position(
                    mt = (playerSize - inPlayHeight / 1.5 + topMargin).toInt(),
                    ml = playerSize.toInt()
                ),
                raiseAmt = Position(ml = playerSize.toInt(), mt = topMargin + playerMargin),
                activeIndication = Gravity.START,
                deal = Gravity.END
            )
    }


    if (isLandscape) {
        slotPositions[LEFT_BOTTOM] =
            SlotPosition(
                x = 2 * playerSize + leftMargin - inPlayWidth,
                y = tableHeight - playerSize + topMargin * 1.5f - inPlayHeight / 2,
                player = Position(ml = inPlayWidth, mt = inPlayHeight / 2),
                crown = Position(
                    ml = (inPlayWidth + (playerSize - crownWidth) / 2).toInt(),
                    mt = (roundingSize * 3).toInt()
                ),
                revealCards = Position(
                    ml = (inPlayWidth + playerSize).toInt(),
                    mt = -cardHeight / 3
                ),
                inPlay = Position(mt = (playerSize - inPlayHeight / 1.5 + inPlayHeight / 2).toInt()),
                raiseAmt = Position(mt = playerMargin, ml = (playerSize + inPlayWidth).toInt()),
                activeIndication = Gravity.START,
                deal = Gravity.END
            )
    } else {
        slotPositions[LEFT_BOTTOM] =
            SlotPosition(
                x = playerMargin.toFloat() / 2,
                y = tableHeight / 1.3f,
                player = Position(ml = (playerMargin / 3), mt = inPlayHeight / 2),
                crown = Position(
                    ml = (playerSize / 4 + (playerSize - crownWidth) / 4).toInt(),
                    mt = (roundingSize * 3).toInt()
                ),
                revealCards = Position(
                    ml = (inPlayWidth + playerSize).toInt(),
                    mt = -cardHeight / 3
                ),
                inPlay = Position(mt = (playerSize - inPlayHeight / 1.5 + inPlayHeight / 2).toInt()),
                raiseAmt = Position(mt = playerMargin, ml = (playerSize + inPlayWidth).toInt()),
                activeIndication = Gravity.START,
                deal = Gravity.END
            )

    }

    if (isLandscape) {
        slotPositions[LEFT_BOTTOM_CENTER] =
            SlotPosition(
                x = playerSize / 2 + leftMargin / 2 - 2,
                y = tableHeight - 1.7f * playerSize + topMargin,
                player = Position(
                    ml = (playerMargin / 3),
                    mt = playerMargin / 2,
                    mb = playerMargin
                ),
                crown = Position(
                    ml = (playerSize / 4 + (playerSize - crownWidth) / 2).toInt(),
                    mt = (roundingSize * 3).toInt()
                ),
                revealCards = Position(
                    ml = (playerSize / 4 + playerSize).toInt(),
                    mt = -cardHeight / 2
                ),
                inPlay = Position(
                    mt = playerSize.toInt() + inPlayHeight - playerMargin / 2,
                    ml = 2 * roundingSize.toInt()
                ),
                raiseAmt = Position(
                    mt = (playerMargin + roundingSize * 2).toInt(),
                    ml = (playerSize + roundingSize * 3).toInt()
                ),
                activeIndication = Gravity.START,
                deal = Gravity.END
            )
    } else {
        slotPositions[LEFT_BOTTOM_CENTER] =
            SlotPosition(
                x = playerMargin.toFloat() / 2,
                y = tableHeight / 1.8f,
                player = Position(
                    ml = (playerMargin / 3),
                    mt = playerMargin / 2,
                    mb = playerMargin
                ),
                crown = Position(
                    ml = (playerSize / 4 + (playerSize - crownWidth) / 4).toInt(),
                    mt = (roundingSize * 3).toInt()
                ),
                revealCards = Position(
                    ml = (playerSize / 4 + playerSize).toInt(),
                    mt = -cardHeight / 2
                ),
                inPlay = Position(
                    mt = playerSize.toInt() + inPlayHeight - playerMargin / 2,
                    ml = 2 * roundingSize.toInt()
                ),
                raiseAmt = Position(
                    mt = (playerMargin + roundingSize * 2).toInt(),
                    ml = (playerSize + roundingSize * 3).toInt()
                ),
                activeIndication = Gravity.START,
                deal = Gravity.END
            )
    }


    slotPositions[LEFT_CENTER] =
        SlotPosition(
            x = roundingSize + leftMargin.toFloat() - roundingSize * 2,
            y = (tableHeight - playerSize) / 2 + topMargin / 2,
            player = Position(mt = topMargin / 2),
            revealCards = Position(ml = (playerSize).toInt()),
            crown = Position(
                ml = ((playerSize - crownWidth) / 2f).toInt(),
                mt = topMargin
            ),
            inPlay = Position(mt = (playerSize + roundingSize + topMargin / 2).toInt()),
            raiseAmt = Position(
                ml = (playerSize + roundingSize).toInt(),
                mt = ((playerSize - inPlayHeight + topMargin) / 2).toInt() + playerMargin
            ),
            activeIndication = Gravity.START,
            deal = Gravity.END
        )
    slotPositions[RIGHT_CENTER] =
        SlotPosition(
            x = tableWidth - playerSize - leftMargin - inPlayWidth + roundingSize * 2,
            y = (tableHeight - playerSize) / 2 + topMargin / 2,
            player = Position(ml = inPlayWidth, mt = topMargin / 2),
            revealCards = Position(ml = (inPlayWidth - revealCardsTop * 3).toInt()),
            crown = Position(
                ml = (inPlayWidth + (playerSize - crownWidth) / 2f).toInt(),
                mt = topMargin / 2
            ),
            inPlay = Position(
                ml = inPlayWidth,
                mt = (playerSize + roundingSize + topMargin / 2).toInt()
            ),
            raiseAmt = Position(mt = ((playerSize - inPlayHeight + topMargin) / 2).toInt() + playerMargin),
            activeIndication = Gravity.START,
            deal = Gravity.END
        )

    if (isLandscape) {
        return SlotPosition(
            x = (tableWidth - meSlotSize) / 2,
            y = tableHeight - meSlotSize * 1.5f + topMargin * 1.5f,
            player = Position(mt = topMargin),
            crown = Position(
                ml = ((meSlotSize - crownWidth) / 2f).toInt(),
                mt = (topMargin / 1.5).toInt()
            ),
            inPlay = Position(
                mt = (meSlotSize - inPlayHeight / 1.5 + topMargin).toInt(),
                ml = meSlotSize.toInt()
            ),
            raiseAmt = Position(ml = meSlotSize.toInt(), mt = topMargin + playerMargin),
            activeIndication = Gravity.START,
            deal = Gravity.END
        )
    } else {
        return SlotPosition(
            x = (tableWidth - meSlotSize) / 2,
            y = tableHeight - playerSize * 1.5f + topMargin / 2.5f,
            player = Position(mt = topMargin),
            crown = Position(
                ml = ((meSlotSize - crownWidth) / 2f).toInt(),
                mt = (topMargin / 1.5).toInt()
            ),
            inPlay = Position(
                mt = (meSlotSize - inPlayHeight / 1.5 + topMargin).toInt(),
                ml = meSlotSize.toInt()
            ),
            raiseAmt = Position(ml = meSlotSize.toInt(), mt = topMargin + playerMargin),
            activeIndication = Gravity.START,
            deal = Gravity.END
        )
    }

}