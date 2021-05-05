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
            x = leftMargin.toFloat()/1.1f,
            y = playerSize/1.2f,
            player = Position(
                ml = (playerSize / 2).toInt(),
                mb = playerMargin
            ),
            crown = Position(ml = (playerSize / 4 + (playerSize - crownWidth) *1.2f).toInt()),
            revealCards = Position(ml = (playerSize / 4 + playerSize).toInt(), mt = cardHeight / 2),
            inPlay = Position(),
            raiseAmt = Position(
                mt = playerSize.toInt() - topMargin / 7,
                ml = (playerSize + roundingSize * 10).toInt()
            ),
            activeIndication = Gravity.START,
            playerAction =  Position(alignment = Gravity.START or Gravity.BOTTOM , mb = topMargin-5, ml =  playerMargin+inPlayWidth + dpToPx(10)),
            deal = Position(alignment = Gravity.START or Gravity.BOTTOM, mb = topMargin-5, ml =  playerMargin - dpToPx(10))
        )
    } else {
        slotPositions[LEFT_TOP_CENTER] = SlotPosition(
            x = playerMargin.toFloat() / 3,
            y = (tableHeight / 3f), //copied from left-center-bottom
            player = Position(
                mt = playerMargin / 4,
                ml = (playerMargin / 2) + dpToPx(2)
            ),
            crown = Position(
                ml = (playerSize / 4 + (playerSize - crownWidth) / 4).toInt(),
                mt = (roundingSize * 2).toInt()
            ),
            revealCards = Position(ml = (playerSize / 4 + playerSize).toInt()),
            inPlay = Position(),
            raiseAmt = Position(
                alignment = Gravity.TOP or Gravity.END,
                ml = (playerSize).toInt()
            ),
            activeIndication = Gravity.START,
            playerAction =  Position(alignment = Gravity.START or Gravity.BOTTOM, mb = inPlayHeight - topMargin/3 - dpToPx(5), ml = playerMargin +inPlayWidth - dpToPx(5)),
            deal = Position(alignment = Gravity.START or Gravity.BOTTOM, mb = inPlayHeight - topMargin/3 -dpToPx(5))
        )
    }
    if (isLandscape) {
        slotPositions[LEFT_TOP] = SlotPosition(
            x = 2f * playerSize - inPlayWidth + leftMargin,
            y = topMargin/5f,
            player = Position(ml = inPlayWidth ),
            revealCards = Position(ml = (inPlayWidth + playerSize).toInt(), mt = revealCardsTop),
            crown = Position(ml = (inPlayWidth + (playerSize - crownWidth) / 2f).toInt()),
            inPlay = Position(mt = topMargin / 4),
            raiseAmt = Position(
                mt = (playerSize - roundingSize + topMargin / 4).toInt() + playerMargin / 2,
                ml = (playerSize + roundingSize * 4).toInt()
            ),
            activeIndication = Gravity.END,
            playerAction =  Position(alignment = Gravity.START or Gravity.TOP , mt = topMargin*2 - dpToPx(12), ml = inPlayWidth+ playerSize.toInt() + dpToPx(3)),
            deal = Position(alignment = Gravity.START or Gravity.TOP, mt = topMargin*2 - dpToPx(12), ml = playerMargin*2 - dpToPx(3))
        )
    } else {
        slotPositions[LEFT_TOP] = SlotPosition(
            x = leftMargin.toFloat(),
            y = 2 * playerSize + leftMargin - inPlayWidth, //copied from left_bottom
            player = Position(
                ml = (playerSize / 4).toInt(),
                mb = playerMargin
            ),
            revealCards = Position(ml = (playerSize / 4 + playerSize).toInt(), mt = -cardHeight / 2),
            crown = Position(
                ml = (playerSize / 4 + (playerSize - crownWidth) / 2).toInt()
            ),
            inPlay = Position(mt = topMargin / 4),
            raiseAmt = Position(
                mt = (playerSize - roundingSize + topMargin / 4).toInt() + playerMargin / 2,
                ml = (playerSize + roundingSize ).toInt()
            ),
            activeIndication = Gravity.END,
            playerAction =  Position(alignment = Gravity.START or Gravity.TOP, mt = topMargin + roundingSize.toInt()*3, ml = playerMargin +inPlayWidth - dpToPx(5)),
            deal = Position(alignment = Gravity.START or Gravity.TOP, mt = topMargin + roundingSize.toInt()*3)
        )
    }

    if (isLandscape) {
        slotPositions[TOP_CENTER] = SlotPosition(
            x = (tableWidth - playerSize) / 2 - inPlayWidth,
            y = 0f,
            player = Position(ml = inPlayWidth, mr = inPlayWidth/4, mb = topMargin / 4),
            inPlay = Position(mt = topMargin / 4),
            revealCards = Position(ml = (inPlayWidth + playerSize).toInt()),
            crown = Position(ml = (inPlayWidth + (playerSize - crownWidth) / 2f).toInt()),
            raiseAmt = Position(
                alignment = Gravity.START or Gravity.BOTTOM,
                mt = playerSize.toInt() - inPlayHeight / 2 + topMargin / 4
            ),
            activeIndication = Gravity.END,
            playerAction =  Position(alignment = Gravity.END or Gravity.BOTTOM, mb = inPlayHeight - topMargin/3, mr = playerMargin+ inPlayWidth - dpToPx(5)),
            deal = Position(alignment = Gravity.END or Gravity.BOTTOM, mb = inPlayHeight - topMargin/3)
        )
    } else {
        slotPositions[TOP_CENTER] = SlotPosition(
            x = (tableWidth - playerSize) / 2 - inPlayWidth,
            y = 1.8f * playerSize - inPlayWidth, //copied from left_bottom
            player = Position(ml = inPlayWidth,
                mb = playerMargin
            ),
            inPlay = Position(mt = topMargin / 4),
            revealCards = Position(ml = (inPlayWidth + playerSize).toInt()),
            crown = Position(ml = (inPlayWidth + (playerSize - crownWidth) / 2f).toInt()),
            raiseAmt = Position(
                alignment = Gravity.BOTTOM or Gravity.END,
                 mr = inPlayWidth/3,
                mb = roundingSize.toInt()
            ),
            activeIndication = Gravity.END,
            playerAction = Position(alignment = Gravity.START or Gravity.TOP, mt = topMargin*2 - dpToPx(10) - playerMargin, ml = playerMargin*2 - dpToPx(3)),
            deal = Position(alignment = Gravity.START or Gravity.TOP , mt = topMargin*2 - dpToPx(10) - playerMargin, ml = inPlayWidth+ playerSize.toInt() + dpToPx(3))
        )
    }

    if (isLandscape) {
        slotPositions[RIGHT_TOP] =
            SlotPosition(
                x = tableWidth - 3f * playerSize - leftMargin - inPlayWidth / 2,
                y = topMargin/5f,
                player = Position(ml = inPlayWidth / 2, mr = inPlayWidth/4),
                revealCards = Position(mt = revealCardsTop),
                crown = Position(ml = ((inPlayWidth + playerSize - crownWidth) / 2f).toInt()),
                inPlay = Position(ml = playerSize.toInt() + inPlayWidth / 2, mt = topMargin / 4),
                raiseAmt = Position(
                    mt = (playerSize - roundingSize + topMargin / 4).toInt() + playerMargin / 2,
                    ml = (roundingSize * 6).toInt()
                ),
                activeIndication = Gravity.START,
                playerAction =  Position(alignment = Gravity.END or Gravity.TOP, mt = topMargin*2 - dpToPx(12), mr = playerSize.toInt() + inPlayWidth/3),
                deal = Position(alignment = Gravity.END or Gravity.TOP, mt = topMargin*2 - dpToPx(12))
        )
    } else {
        slotPositions[RIGHT_TOP] =
            SlotPosition(
                x = tableWidth - 1.5f * playerSize - leftMargin - inPlayWidth,
                y = 2 * playerSize + leftMargin - inPlayWidth,
                player = Position(ml = (playerSize / 4 + inPlayWidth).toInt(), mr = inPlayWidth/4),
                revealCards = Position(ml = (inPlayWidth - playerSize/2.5).toInt(), mt = -cardHeight / 6),
                crown = Position(
                    ml = (playerSize / 3.5 + inPlayWidth + (playerSize - crownWidth) / 2).toInt()
                ),
                inPlay = Position(ml = playerSize.toInt() + inPlayWidth / 2, mt = topMargin / 4),
                raiseAmt = Position(
                    mt = (playerSize - roundingSize + topMargin / 4).toInt() + playerMargin / 2,
                    ml = (roundingSize * 9).toInt()
                ),
                activeIndication = Gravity.START,
                playerAction =  Position(alignment = Gravity.END or Gravity.TOP, mt = topMargin + roundingSize.toInt()*3, mr = playerMargin +inPlayWidth - dpToPx(5)),
                deal = Position(alignment = Gravity.END or Gravity.TOP, mt = topMargin + roundingSize.toInt()*3)
            )
    }

    if (isLandscape) {
        slotPositions[RIGHT_TOP_CENTER] =
            SlotPosition(
                x = tableWidth - 1.7f * playerSize - leftMargin - inPlayWidth,
                y = playerSize - topMargin / 1.3f,
                player = Position(
                    ml = (playerSize / 3 + inPlayWidth).toInt(),
                    mt = playerMargin - topMargin / 6,
                    mb = playerMargin,
                    mr = inPlayWidth/4
                ),
                crown = Position(
                    ml = (playerSize / 4 + inPlayWidth + (playerSize - crownWidth) / 1.5f).toInt(),
                    mt = (roundingSize * 3).toInt()
                ),
                inPlay = Position(ml = (inPlayWidth / 3.5).toInt()),
                revealCards = Position(ml = roundingSize.toInt() * 2, mt = cardHeight / 2),
                raiseAmt = Position(
                    mt = (playerSize + inPlayHeight / 2 - topMargin / 4 + roundingSize).toInt()
                ),
                activeIndication = Gravity.END,
                playerAction =  Position(alignment = Gravity.END or Gravity.BOTTOM, mb = playerMargin + playerMargin/2, mr = playerSize.toInt() + inPlayWidth/3),
                deal = Position(alignment = Gravity.END or Gravity.BOTTOM, mb = playerMargin + playerMargin/2)
        )
    } else {
        slotPositions[RIGHT_TOP_CENTER] =
            SlotPosition(
                x = tableWidth - 1.1f * playerSize - leftMargin - inPlayWidth,
                y = tableHeight / 3f,
                player = Position(
                    ml = (playerSize / 4 + inPlayWidth).toInt(),
                    mt = playerMargin / 4,
                    mr = inPlayWidth/4
                    ),
                crown = Position(
                    ml = (playerSize / 4 + inPlayWidth + (playerSize - crownWidth) / 2).toInt(),
                    mt = (roundingSize * 2).toInt()
                ),
                inPlay = Position(ml = (inPlayWidth / 3.5).toInt()),
                revealCards = Position(ml = (inPlayWidth - playerSize/2.5).toInt(), mt = -cardHeight / 6),
                raiseAmt = Position(
                    alignment = Gravity.TOP or Gravity.END,
                    mr = playerMargin +inPlayWidth/2 + roundingSize.toInt()
                ),
                activeIndication = Gravity.START,
                playerAction =  Position(alignment = Gravity.END or Gravity.BOTTOM, mb = inPlayHeight - topMargin/3 - dpToPx(5), mr = playerMargin +inPlayWidth - dpToPx(5)),
                deal = Position(alignment = Gravity.END or Gravity.BOTTOM, mb = inPlayHeight - topMargin/3 -dpToPx(5))
            )
    }

    if (isLandscape) {
        slotPositions[RIGHT_BOTTOM_CENTER] =
            SlotPosition(
                x = tableWidth - 1.3f * playerSize - leftMargin - inPlayWidth,
                y = tableHeight - 2f * playerSize + topMargin,
                player = Position(
                    ml = (playerSize / 4 + inPlayWidth).toInt(),
                    mt = playerMargin / 2,
                    mb = playerMargin,
                    mr = inPlayWidth/4
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
                raiseAmt = Position(mt = (playerSize / 2 + roundingSize).toInt(),
                    ml = (roundingSize * 3).toInt()
                ),
                activeIndication = Gravity.END,
                playerAction =  Position(alignment = Gravity.END or Gravity.BOTTOM, mb = playerMargin + playerMargin/2, mr = playerSize.toInt() + inPlayWidth/3),
                deal = Position(alignment = Gravity.END or Gravity.BOTTOM, mb = playerMargin + playerMargin/2)
        )
    } else {
        slotPositions[RIGHT_BOTTOM_CENTER] =
            SlotPosition(
                x = tableWidth - 1.1f * playerSize - leftMargin - inPlayWidth,
                y = tableHeight / 1.8f,
                player = Position(
                    ml = (playerSize / 4 + inPlayWidth).toInt(),
                    mt = playerMargin / 2,
                    mb = playerMargin,
                    mr = inPlayWidth/4
                ),
                crown = Position(
                    ml = (playerSize / 4 + inPlayWidth + (playerSize - crownWidth) / 2).toInt(),
                    mt = (roundingSize * 3).toInt()
                ),
                revealCards = Position(ml = (inPlayWidth - playerSize/3).toInt(), mt = cardHeight / 2),
                inPlay = Position(
                    mt = playerSize.toInt() + inPlayHeight - playerMargin / 2,
                    ml = 2 * roundingSize.toInt() + inPlayWidth
                ),
                raiseAmt = Position(
                    alignment = Gravity.BOTTOM or Gravity.END,
                    mt = (playerMargin).toInt(),
                    mr = (playerSize + roundingSize / 3).toInt(),
                    mb = inPlayHeight - topMargin/3 - dpToPx(5)
                ),
                activeIndication = Gravity.START,
                playerAction =  Position(alignment = Gravity.END or Gravity.TOP, mt = topMargin + roundingSize.toInt()*6, mr = playerMargin +inPlayWidth - dpToPx(5)),
                deal = Position(alignment = Gravity.END or Gravity.TOP, mt = topMargin + roundingSize.toInt()*6)
            )
    }

    if (isLandscape) {
        slotPositions[RIGHT_BOTTOM] =
            SlotPosition(
                x = tableWidth - 3 * playerSize - leftMargin/5f - inPlayWidth,
                y = tableHeight - playerSize + topMargin * 2f - inPlayHeight / 1.5f,
                player = Position(ml = inPlayWidth, mt = (inPlayHeight / 1.5).toInt(), mr = inPlayWidth/4
                ),
                crown = Position(
                    ml = (inPlayWidth + (playerSize - crownWidth) / 2).toInt(),
                    mt = (roundingSize * 4).toInt()
                ),
                revealCards = Position(mt = (-cardHeight / 3).toInt()),
                inPlay = Position(
                    mt = (playerSize).toInt(),
                    ml = playerSize.toInt() + inPlayWidth
                ),
                raiseAmt = Position(mt = inPlayWidth - playerMargin - dpToPx(3)),
                activeIndication = Gravity.END,
                playerAction =  Position(alignment = Gravity.END or Gravity.BOTTOM, mb = inPlayHeight - topMargin/2, mr = playerSize.toInt() + inPlayWidth/3),
                deal = Position(alignment = Gravity.END or Gravity.BOTTOM, mb = inPlayHeight - topMargin/2)
            )
    } else {
        slotPositions[RIGHT_BOTTOM] =
            SlotPosition(
                x = tableWidth - 1.1f * playerSize - leftMargin - inPlayWidth,
                y = tableHeight / 1.3f,
                player = Position(
                    ml = (playerSize / 4 + inPlayWidth).toInt(),
                    mt = (inPlayHeight / 1.5).toInt(),
                    mr = inPlayWidth/4
                ),
                crown = Position(
                    ml = (playerSize / 4 + inPlayWidth + (playerSize - crownWidth) / 2).toInt(),
                    mt = (roundingSize * 4).toInt()
                ),
                revealCards = Position(ml = (inPlayWidth - playerSize/2.5).toInt()),
                inPlay = Position(
                    mt = (playerSize).toInt(),
                    ml = playerSize.toInt() + inPlayWidth
                ),
                raiseAmt = Position(
                    alignment = Gravity.TOP or Gravity.END,
                    mr = (playerSize + roundingSize / 3).toInt()
                ),
                activeIndication = Gravity.START,
                playerAction =  Position(alignment = Gravity.END or Gravity.BOTTOM, mb = inPlayHeight - topMargin/3 - dpToPx(5), mr = playerMargin +inPlayWidth - dpToPx(5)),
                deal = Position(alignment = Gravity.END or Gravity.BOTTOM, mb = inPlayHeight - topMargin/3 -dpToPx(5))
            )
    }
    if (isLandscape) {
        slotPositions[BOTTOM_CENTER] =
            SlotPosition(
                x = (tableWidth - meSlotSize) / 2 - inPlayWidth/4,
                y = tableHeight - playerSize * 1.5f + topMargin * 2.3f,
                player = Position(mt = topMargin, ml = inPlayWidth/4, mr = inPlayWidth/4),
                crown = Position(
                    ml = ((playerSize - crownWidth) / 2f).toInt(),
                    mt = (topMargin)
                ),
                inPlay = Position(
                    mt = (playerSize - inPlayHeight / 1.5 + topMargin).toInt(),
                    ml = playerSize.toInt()
                ),
                raiseAmt = Position(ml = inPlayWidth/6 + meSlotSize.toInt(), mt = inPlayWidth + dpToPx(7) - playerMargin ),
                activeIndication = Gravity.START,
                playerAction =  Position(alignment = Gravity.START or Gravity.BOTTOM, mb = inPlayHeight - topMargin/2, ml = playerMargin +inPlayWidth - dpToPx(5)),
                deal = Position(alignment = Gravity.START or Gravity.BOTTOM, mb = inPlayHeight - topMargin/2)
            )
    } else {
        slotPositions[BOTTOM_CENTER] =
            SlotPosition(
                /* x = playerSize / 2 + leftMargin / 2 - 2,
                 y = tableHeight/1.3f ,
                 */
                x = (tableWidth - playerSize) / 2 -inPlayWidth/4,
                y = tableHeight - playerSize * 1.5f + topMargin / 2.5f,
                player = Position(mt = topMargin,  ml = inPlayWidth/4, mr = inPlayWidth/4),
                crown = Position(
                    ml = ((playerSize - crownWidth) / 2f).toInt(),
                    mt = (topMargin)
                ),
                inPlay = Position(
                    mt = (playerSize - inPlayHeight / 1.5 + topMargin).toInt(),
                    ml = playerSize.toInt()
                ),
                raiseAmt = Position(alignment = Gravity.TOP, ml = inPlayWidth/4 + roundingSize.toInt()),
                activeIndication = Gravity.START,
                playerAction =  Position(alignment = Gravity.END or Gravity.BOTTOM, mb = inPlayHeight - topMargin/2 + dpToPx(5)),
                deal = Position(Gravity.BOTTOM or Gravity.START,mb = inPlayHeight - topMargin/2 + dpToPx(5))
            )
    }


    if (isLandscape) {
        slotPositions[LEFT_BOTTOM] =
            SlotPosition(
                x = 2 * playerSize + leftMargin/5f - inPlayWidth,
                y = tableHeight - playerSize + topMargin * 2f - inPlayHeight / 1.5f,
                player = Position(ml = inPlayWidth, mt = inPlayHeight / 2, mr = inPlayWidth/4),
                crown = Position(
                    ml = (inPlayWidth + (playerSize - crownWidth) / 2).toInt(),
                    mt = (roundingSize * 3).toInt()
                ),
                revealCards = Position(
                    ml = (inPlayWidth + playerSize).toInt(),
                    mt = -cardHeight / 3
                ),
                inPlay = Position(mt = (playerSize - inPlayHeight / 1.5 + inPlayHeight / 2).toInt()),
                raiseAmt = Position(mt = inPlayWidth - playerMargin - dpToPx(3), ml = (playerSize + inPlayWidth).toInt()),
                activeIndication = Gravity.START,
                playerAction =  Position(alignment = Gravity.START or Gravity.BOTTOM , mb = inPlayHeight - topMargin/2, ml = inPlayWidth + playerSize.toInt() + dpToPx(5)),
                deal = Position(alignment = Gravity.START or Gravity.BOTTOM, mb = inPlayHeight - topMargin/2, ml = playerMargin*2 - dpToPx(5))
        )
    } else {
        slotPositions[LEFT_BOTTOM] =
            SlotPosition(
                x = playerMargin.toFloat() / 2,
                y = tableHeight / 1.3f,
                player = Position(ml = (playerMargin / 2) + dpToPx(2),
                     mt = inPlayHeight / 2),
                crown = Position(
                    ml = (playerSize / 4 + (playerSize - crownWidth) / 4).toInt(),
                    mt = (roundingSize * 3).toInt()
                ),
                revealCards = Position(
                    ml = (playerSize / 1.2 + (playerSize - crownWidth)).toInt()
                ),
                inPlay = Position(mt = (playerSize - inPlayHeight / 1.5 + inPlayHeight / 2).toInt()),
                raiseAmt = Position(
                    alignment = Gravity.TOP or Gravity.END,
                    ml = (playerSize).toInt()
                ),
                activeIndication = Gravity.START,
                playerAction =  Position(alignment = Gravity.START or Gravity.BOTTOM, mb = inPlayHeight - topMargin/3 - dpToPx(5), ml = playerMargin +inPlayWidth - dpToPx(5)),
                deal = Position(alignment = Gravity.START or Gravity.BOTTOM, mb = inPlayHeight - topMargin/3 -dpToPx(5))
            )

    }

    if (isLandscape) {
        slotPositions[LEFT_BOTTOM_CENTER] =
            SlotPosition(
                x = playerSize / 3 + leftMargin / 3 - 2,
                y = tableHeight - 2f * playerSize + topMargin,
                player = Position(
                    ml = (playerMargin),
                    mt = playerMargin / 2,
                    mb = playerMargin
                ),
                crown = Position(
                    ml = (playerSize / 4 + (playerSize - crownWidth)).toInt(),
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
                    ml = (playerSize + roundingSize * 5).toInt()
                ),
                activeIndication = Gravity.START,
                playerAction =  Position(alignment = Gravity.START or Gravity.BOTTOM , mb = topMargin, ml = playerMargin+inPlayWidth + dpToPx(5)),
                deal = Position(alignment = Gravity.START or Gravity.BOTTOM, mb = topMargin, ml = playerMargin - dpToPx(15))
        )
    } else {
        slotPositions[LEFT_BOTTOM_CENTER] =
            SlotPosition(
                x = playerMargin.toFloat() / 3,
                y = tableHeight / 1.8f,
                player = Position(
                    ml = (playerMargin / 2) + dpToPx(2),
                    mt = playerMargin / 2,
                    mb = playerMargin
                ),
                crown = Position(
                    ml = (playerSize / 4 + (playerSize - crownWidth) / 4).toInt(),
                    mt = (roundingSize * 3).toInt()
                ),
                revealCards = Position(
                    ml = (playerSize *2 / 2).toInt(),
                    mt = (playerSize/3).toInt()
                ),
                inPlay = Position(
                    mt = playerSize.toInt() + inPlayHeight - playerMargin / 2,
                    ml = 2 * roundingSize.toInt()
                ),
                raiseAmt = Position(
                    alignment = Gravity.BOTTOM,
                    mt = (playerMargin).toInt(),
                    ml = (playerSize + roundingSize / 3).toInt(),
                    mb = inPlayHeight - topMargin/3 - dpToPx(5)

                ),
                activeIndication = Gravity.START,
                playerAction =  Position(alignment = Gravity.START or Gravity.TOP, mt = topMargin + roundingSize.toInt()*6, ml = playerMargin +inPlayWidth - dpToPx(5)),
                deal = Position(alignment = Gravity.START or Gravity.TOP, mt = topMargin + roundingSize.toInt()*6)
            )
    }


    slotPositions[LEFT_CENTER] =
        SlotPosition(
            x = roundingSize + leftMargin.toFloat() - roundingSize * 2,
            y = (tableHeight - playerSize) / 2 + topMargin / 2,
            player = Position(mt = topMargin / 2, ml = inPlayWidth/4),
            revealCards = Position(ml = (playerSize).toInt()),
            crown = Position(
                ml = ((playerSize - crownWidth) / 2f).toInt(),
                mt = topMargin
            ),
            inPlay = Position(mt = (playerSize + roundingSize + topMargin / 2).toInt()),
            raiseAmt = Position(
                ml = (playerSize + roundingSize).toInt() + inPlayWidth/4,
                mt = ((playerSize - inPlayHeight + topMargin) / 2).toInt()
            ),
            activeIndication = Gravity.START,
            playerAction =  Position(alignment = Gravity.START or Gravity.BOTTOM, mb = inPlayHeight - topMargin/2, ml = playerMargin+ inPlayWidth - dpToPx(5)),
            deal = Position(alignment = Gravity.START or Gravity.BOTTOM, mb = inPlayHeight - topMargin/2)
        )
    slotPositions[RIGHT_CENTER] =
        SlotPosition(
            x = tableWidth - playerSize - leftMargin - inPlayWidth + roundingSize * 2,
            y = (tableHeight - playerSize) / 2 + topMargin / 2,
            player = Position(ml = inPlayWidth, mt = topMargin / 2, mr = inPlayWidth/4),
            revealCards = Position(ml = (inPlayWidth - revealCardsTop * 3).toInt()),
            crown = Position(
                ml = (inPlayWidth + (playerSize - crownWidth) / 2f).toInt(),
                mt = topMargin / 2
            ),
            inPlay = Position(
                ml = inPlayWidth,
                mt = (playerSize + roundingSize + topMargin / 2).toInt()
            ),
            raiseAmt = Position(mt = ((playerSize - inPlayHeight + topMargin) / 2).toInt()),
            activeIndication = Gravity.START,
            playerAction =  Position(alignment = Gravity.END or Gravity.BOTTOM, mb = inPlayHeight - topMargin/2, mr = playerSize.toInt() + inPlayWidth/3),
            deal = Position(alignment = Gravity.END or Gravity.BOTTOM, mb = inPlayHeight - topMargin/2)
        )

    if (isLandscape) {
        return SlotPosition(
            x = (tableWidth - meSlotSize) / 2 - inPlayWidth/4,
            y = tableHeight - meSlotSize * 1.5f + topMargin * 2.3f,
            player = Position(mt = topMargin, ml = inPlayWidth/4, mr = inPlayWidth/4),
            crown = Position(
                ml = ((meSlotSize - crownWidth) ).toInt(),
                mt = (topMargin / 1.5).toInt()
            ),
            inPlay = Position(
                mt = (meSlotSize - inPlayHeight / 1.5 + topMargin).toInt(),
                ml = meSlotSize.toInt()
            ),
            raiseAmt = Position(ml = inPlayWidth/4 + meSlotSize.toInt(), mt = inPlayWidth + dpToPx(7) - playerMargin ),
            activeIndication = Gravity.START,
            playerAction =  Position(alignment = Gravity.START or Gravity.BOTTOM, mb = inPlayHeight - topMargin/2, ml = playerMargin+ inPlayWidth),
            deal = Position(alignment = Gravity.START or Gravity.BOTTOM, mb = inPlayHeight - topMargin/2)
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
            playerAction =  Position(Gravity.END or Gravity.CENTER),
            deal = Position()
        )
    }

}