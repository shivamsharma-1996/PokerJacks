package com.gtgt.pokerjacks.ui.game.view.slot

import android.view.Gravity
import com.gtgt.pokerjacks.extensions.dpToPx
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
    val activeIndication: Int,
    val deal: Int
)

var slotPositions = mutableMapOf<SlotPositions, SlotPosition>()
var slots9Positions = listOf(
    BOTTOM_CENTER,
    RIGHT_BOTTOM,
    RIGHT_BOTTOM_CENTER,
    RIGHT_TOP_CENTER,
    RIGHT_TOP,
    LEFT_TOP,
    LEFT_TOP_CENTER,
    LEFT_BOTTOM_CENTER,
    LEFT_BOTTOM
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
    RIGHT_CENTER,
    RIGHT_TOP,
    TOP_CENTER,
    LEFT_TOP,
    LEFT_CENTER
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
    meSlotSize: Float
): SlotPosition {
    val playerMargin = dpToPx(26)
    val inPlayWidth = dpToPx(65)
    val inPlayHeight = dpToPx(30)

    slotPositions[LEFT_TOP_CENTER] = SlotPosition(
        x = leftMargin.toFloat(),
        y = playerSize + topMargin / 2,
        player = Position(
            ml = (playerSize / 4).toInt(),
            mt = playerMargin - topMargin / 4,
            mb = playerMargin
        ),
        inPlay = Position(),
        raiseAmt = Position(
            mt = playerSize.toInt() + inPlayHeight / 2 - topMargin / 4,
            ml = playerSize.toInt()
        ),
        activeIndication = Gravity.START,
        deal = Gravity.END
    )
    slotPositions[LEFT_TOP] = SlotPosition(
        x = 2 * playerSize - inPlayWidth + leftMargin,
        y = topMargin / 2f,
        player = Position(ml = inPlayWidth),
        inPlay = Position(),
        raiseAmt = Position(
            mt = (playerSize - roundingSize * 1.5).toInt(),
            ml = (playerSize + inPlayWidth / 1.5).toInt()
        ),
        activeIndication = Gravity.END,
        deal = Gravity.END or Gravity.BOTTOM
    )
    slotPositions[TOP_CENTER] = SlotPosition(
        x = (tableWidth - playerSize) / 2 - inPlayWidth,
        y = topMargin / 2f,
        player = Position(ml = inPlayWidth),
        inPlay = Position(),
        raiseAmt = Position(
            ml = (inPlayWidth + playerSize - roundingSize).toInt(),
            mt = playerSize.toInt() - inPlayHeight / 2
        ),
        activeIndication = Gravity.END,
        deal = Gravity.BOTTOM
    )
    slotPositions[RIGHT_TOP] =
        SlotPosition(
            x = tableWidth - 3 * playerSize - leftMargin - inPlayWidth / 2,
            y = topMargin / 2f,
            player = Position(ml = inPlayWidth / 2),
            inPlay = Position(ml = playerSize.toInt() + inPlayWidth / 2),
            raiseAmt = Position(
                mt = (playerSize - roundingSize * 1.5).toInt()
            ),
            activeIndication = Gravity.START,
            deal = Gravity.BOTTOM
        )
    slotPositions[RIGHT_TOP_CENTER] =
        SlotPosition(
            x = tableWidth - 1.5f * playerSize - leftMargin - inPlayWidth / 2,
            y = playerSize + topMargin / 2f,
            player = Position(
                ml = (playerSize / 4 + inPlayWidth / 2).toInt(),
                mt = playerMargin - topMargin / 4,
                mb = playerMargin
            ),
            inPlay = Position(ml = (inPlayWidth / 2 + roundingSize).toInt()),
            raiseAmt = Position(
                mt = playerSize.toInt() + inPlayHeight / 2 - topMargin / 4
            ),
            activeIndication = Gravity.END,
            deal = Gravity.START
        )

    slotPositions[RIGHT_BOTTOM_CENTER] =
        SlotPosition(
            x = tableWidth - 1.5f * playerSize - leftMargin - inPlayWidth / 2,
            y = tableHeight - 2 * playerSize + topMargin,
            player = Position(
                ml = (playerSize / 4 + inPlayWidth / 2).toInt(),
                mt = playerMargin / 2,
                mb = playerMargin
            ),
            inPlay = Position(
                mt = playerSize.toInt() + inPlayHeight - playerMargin / 2,
                ml = 2 * roundingSize.toInt() + inPlayWidth / 2
            ),
            activeIndication = Gravity.END,
            deal = Gravity.START
        )
    slotPositions[RIGHT_BOTTOM] =
        SlotPosition(
            x = tableWidth - 3 * playerSize - leftMargin - inPlayWidth / 2,
            y = tableHeight - playerSize + topMargin * 2f - inPlayHeight / 1.5f,
            player = Position(ml = inPlayWidth / 2, mt = (inPlayHeight / 1.5).toInt()),
            inPlay = Position(
                mt = (playerSize).toInt(),
                ml = playerSize.toInt() + inPlayWidth / 2
            ),
            activeIndication = Gravity.END,
            deal = Gravity.START
        )
    slotPositions[BOTTOM_CENTER] =
        SlotPosition(
            x = (tableWidth - playerSize) / 2,
            y = tableHeight - playerSize + topMargin * 2f,
            player = Position(),
            inPlay = Position(
                mt = (playerSize - inPlayHeight / 1.5).toInt(),
                ml = playerSize.toInt()
            ),
            raiseAmt = Position(ml = playerSize.toInt()),
            activeIndication = Gravity.START,
            deal = Gravity.END
        )
    slotPositions[LEFT_BOTTOM] =
        SlotPosition(
            x = 2 * playerSize + leftMargin - inPlayWidth,
            y = tableHeight - playerSize + topMargin * 2f - inPlayHeight / 2,
            player = Position(ml = inPlayWidth, mt = inPlayHeight / 2),
            inPlay = Position(mt = (playerSize - inPlayHeight / 1.5 + inPlayHeight / 2).toInt()),
            raiseAmt = Position(ml = (playerSize + inPlayWidth / 1.5).toInt()),
            activeIndication = Gravity.START,
            deal = Gravity.END
        )
    slotPositions[LEFT_BOTTOM_CENTER] =
        SlotPosition(
            x = playerSize / 2 + leftMargin / 2,
            y = tableHeight - 2 * playerSize + topMargin,
            player = Position(
                ml = (playerSize / 4).toInt(),
                mt = playerMargin / 2,
                mb = playerMargin
            ),
            inPlay = Position(
                mt = playerSize.toInt() + inPlayHeight - playerMargin / 2,
                ml = 2 * roundingSize.toInt()
            ),
            raiseAmt = Position(ml = playerSize.toInt()),
            activeIndication = Gravity.START,
            deal = Gravity.END
        )

    slotPositions[LEFT_CENTER] =
        SlotPosition(
            x = roundingSize + leftMargin.toFloat(),
            y = (tableHeight - playerSize) / 2 + topMargin,
            player = Position(),
            inPlay = Position(mt = (playerSize + roundingSize).toInt()),
            raiseAmt = Position(
                ml = (playerSize + roundingSize).toInt(),
                mt = ((playerSize - inPlayHeight) / 2).toInt()
            ),
            activeIndication = Gravity.START,
            deal = Gravity.END
        )
    slotPositions[RIGHT_CENTER] =
        SlotPosition(
            x = tableWidth - playerSize - roundingSize - leftMargin - inPlayWidth,
            y = (tableHeight - playerSize) / 2 + topMargin,
            player = Position(ml = inPlayWidth),
            inPlay = Position(ml = inPlayWidth, mt = (playerSize + roundingSize).toInt()),
            raiseAmt = Position(mt = ((playerSize - inPlayHeight) / 2).toInt()),
            activeIndication = Gravity.START,
            deal = Gravity.END
        )

    return SlotPosition(
        x = (tableWidth - meSlotSize) / 2,
        y = tableHeight - meSlotSize + topMargin * 2f,
        player = Position(),
        inPlay = Position(mt = (meSlotSize - inPlayHeight / 1.5).toInt(), ml = meSlotSize.toInt()),
        raiseAmt = Position(ml = meSlotSize.toInt()),
        activeIndication = Gravity.START,
        deal = Gravity.END
    )
}