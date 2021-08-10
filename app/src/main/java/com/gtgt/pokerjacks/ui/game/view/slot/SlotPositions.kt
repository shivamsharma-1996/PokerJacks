package com.gtgt.pokerjacks.ui.game.view.slot

import android.view.Gravity
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.extensions.dpToPx
import com.gtgt.pokerjacks.extensions.log
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
    val raiseAmt: Position = Position(),
    val revealCards: Position = Position(),
    val activeIndication: Int,
    val playerAction: Position = Position(),
    val tossCard: Position = Position()
)

var slotPositions = mutableMapOf<SlotPositions, SlotPosition>()

//Common list used for both orientations in 9 player table but in case of 6 player table, we would have two lists one for each orientation
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

var slots6LandscapePositionsTable = listOf(
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

//mutableMaps to track the dynamic arrangements of the slots according to their position on table
val slotPositionMap = mutableMapOf<Int, SlotPositions>()
val slotPosition6TableMap = mutableMapOf<Int, SlotPositions>()

//responsible for dynamic placements of slotViews for both 9/6 player tables (Lanscape/Portrait)
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

    if (isLandscape) {
        slotPositions[LEFT_TOP_CENTER] = SlotPosition(
            x = playerSize / 3 + leftMargin / 2,
            y = playerSize/1.5f,
            player = Position(),
            revealCards = Position(),
            raiseAmt = Position(
                mt = dpToPx(10),
                ml = (playerSize + roundingSize * 10 + dpToPx(20)).toInt(),
                alignment = Gravity.END or Gravity.CENTER
            ),
            tossCard = Position(
                mt = dpToPx(10),
                ml = (playerSize + roundingSize * 10 + dpToPx(20)).toInt(),
                alignment = Gravity.END or Gravity.CENTER_VERTICAL
            ),
            activeIndication = Gravity.START,
            playerAction =  Position(alignment = Gravity.END or Gravity.CENTER, mt = dpToPx(5)))
    } else {
        slotPositions[LEFT_TOP_CENTER] = SlotPosition(
            x = playerMargin.toFloat() /  4 - roundingSize * 2,
            y = (tableHeight / 3.2f), //copied from left-center-bottom
            player = Position(),
            revealCards = Position(),
            raiseAmt = Position(
                alignment = Gravity.END or Gravity.CENTER,
                mt = dpToPx(5),
                ml =  (playerSize + roundingSize * 10 + dpToPx(10)).toInt()),
            activeIndication = Gravity.START,
            tossCard = Position(
                mt = dpToPx(10),
                ml = (playerSize + roundingSize * 10 + dpToPx(20)).toInt(),
                alignment = Gravity.END or Gravity.CENTER_VERTICAL
            ),
            playerAction =  Position(alignment = Gravity.END or Gravity.CENTER, mt = dpToPx(5)))
    }
    if (isLandscape) {
        slotPositions[LEFT_TOP] = SlotPosition(
            x = 2.8f * playerSize - inPlayWidth + leftMargin + inPlayWidth,
            y = topMargin/15f - roundingSize.toInt() *1.5f + dpToPx(5),
            player = Position(
                mb = dpToPx(15)),
            revealCards = Position(/*mt = roundingSize.toInt()*/),
            raiseAmt = Position(
                mb = dpToPx(10),
                alignment = Gravity.BOTTOM or Gravity.CENTER
            ),
            tossCard = Position(
                mt = (playerSize - roundingSize).toInt() + playerMargin,
                alignment = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            ),
            activeIndication = Gravity.END,
            playerAction =  Position(alignment = Gravity.BOTTOM or Gravity.CENTER))
    } else {
        slotPositions[LEFT_TOP] = SlotPosition(
            x = playerMargin.toFloat() /  4 - roundingSize * 2,
            y = 2.2f * playerSize + leftMargin - inPlayWidth,
            player = Position(),
            revealCards = Position(),
            raiseAmt = Position(
                alignment = Gravity.END or Gravity.CENTER,
                mt = dpToPx(5),
                ml =  (playerSize + roundingSize * 10 + dpToPx(10)).toInt()),
            tossCard = Position(
                mt = dpToPx(10),
                ml = (playerSize + roundingSize * 10 + dpToPx(20)).toInt(),
                alignment = Gravity.END or Gravity.CENTER_VERTICAL
            ),
            activeIndication = Gravity.END,
            playerAction =  Position(alignment = Gravity.END or Gravity.CENTER, mt = dpToPx(5))
        )
    }

    if (isLandscape) {
        slotPositions[TOP_CENTER] = SlotPosition(
            x = (tableWidth - meSlotSize) / 2 - inPlayWidth/2,
            y = -dpToPx(0).toFloat(),
            player = Position(
                mb = dpToPx(15)
            ),
            revealCards = Position(),
            raiseAmt = Position(
                mt = (playerSize - roundingSize).toInt() + playerMargin / 2,
                mb = dpToPx(10),
                alignment = Gravity.BOTTOM or Gravity.CENTER
            ),
            tossCard = Position(
                mt = (playerSize - roundingSize).toInt() + playerMargin,
                alignment = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            ),
            activeIndication = Gravity.END,
            playerAction =  Position(alignment = Gravity.BOTTOM or Gravity.CENTER))
    } else {
        slotPositions[TOP_CENTER] = SlotPosition(
            x = (tableWidth - playerSize) / 2.3f -inPlayWidth/4,
            y = 1.8f * playerSize - inPlayWidth, //copied from left_bottom
            player = Position(
                mb = dpToPx(15)
            ),
            revealCards = Position(),
            raiseAmt = Position(
                alignment = Gravity.BOTTOM or Gravity.CENTER
            ),
            tossCard = Position(
                mt = (playerSize - roundingSize).toInt() + playerMargin - dpToPx(5),
                alignment = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            ),
            activeIndication = Gravity.END,
            playerAction = Position(alignment = Gravity.BOTTOM or Gravity.CENTER))
    }

    if (isLandscape) {
        slotPositions[RIGHT_TOP] =
            SlotPosition(
                x = tableWidth - 4f * playerSize - leftMargin - inPlayWidth / 2,
                y = topMargin/15f - roundingSize.toInt() *1.5f + dpToPx(5),
                player = Position(
                    mb = dpToPx(15)
                ),
                raiseAmt = Position(
                    mb = dpToPx(10),
                    alignment = Gravity.BOTTOM or Gravity.CENTER
                ),
                tossCard = Position(
                    mt = (playerSize - roundingSize).toInt() + playerMargin,
                    alignment = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                ),
                revealCards = Position(/*mt = roundingSize.toInt()*/),
                activeIndication = Gravity.START,
                playerAction =  Position(alignment = Gravity.BOTTOM or Gravity.CENTER))
    } else {
        slotPositions[RIGHT_TOP] =
            SlotPosition(
                x = tableWidth - 1.4f * playerSize - leftMargin - inPlayWidth ,
                y = 2.2f * playerSize + leftMargin - inPlayWidth,
                player = Position(
                    ml = (playerSize/1.5f).toInt()),
                revealCards = Position(),
                raiseAmt = Position(
                    alignment = Gravity.START or Gravity.CENTER,
                    mt = dpToPx(5),
                    ml = dpToPx(7)
                ),
                tossCard = Position(
                    ml = ((playerSize/1.2f)/2).toInt(),
                    mt = dpToPx(10),
                    alignment = Gravity.START or Gravity.CENTER
                ),
                activeIndication = Gravity.START,
                playerAction =  Position(alignment = Gravity.START or Gravity.CENTER, mt = dpToPx(5))
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
                revealCards = Position(),
                raiseAmt = Position(
                    mt = dpToPx(10),
                    alignment = Gravity.START or Gravity.CENTER
                ),
                tossCard = Position(
                    ml = ((playerSize/1.2f)/2).toInt(),
                    mt = dpToPx(10),
                    alignment = Gravity.START or Gravity.CENTER
                ),
                activeIndication = Gravity.END,
                playerAction =  Position(alignment = Gravity.START or Gravity.CENTER, mt = dpToPx(5))
        )
    } else {
        slotPositions[RIGHT_TOP_CENTER] =
            SlotPosition(
                x = tableWidth - 1.4f * playerSize - leftMargin - inPlayWidth ,
                y = (tableHeight / 3.2f), //copied from left-center-bottom
                player = Position(
                    ml = (playerSize/1.5f).toInt()),
                tossCard = Position(
                    ml = ((playerSize/1.2f)/2).toInt(),
                    mt = dpToPx(10),
                    alignment = Gravity.START or Gravity.CENTER
                ),
                revealCards = Position(),
                raiseAmt = Position(
                    alignment = Gravity.START or Gravity.CENTER,
                    mt = dpToPx(5),
                    ml = dpToPx(7)
                ),
                activeIndication = Gravity.START,
                playerAction =  Position(
                    alignment = Gravity.START or Gravity.CENTER,
                    mt = dpToPx(5))
            )
    }

    if (isLandscape) {
        slotPositions[RIGHT_BOTTOM_CENTER] =
            SlotPosition(
                x = tableWidth - playerSize - leftMargin - inPlayWidth - (roundingSize * 10).toInt(),
                y = tableHeight - 2f * playerSize + topMargin,
                player = Position(
                    ml = (playerSize/1.2f).toInt()),
                revealCards = Position(),
                tossCard = Position(
                    ml = ((playerSize/1.2f)/2).toInt(),
                    mt = dpToPx(10),
                    alignment = Gravity.START or Gravity.CENTER
                ),
                raiseAmt = Position(
                    mt = dpToPx(10),
                alignment = Gravity.START or Gravity.CENTER),
                activeIndication = Gravity.END,
                playerAction =  Position(alignment = Gravity.START or Gravity.CENTER, mt = dpToPx(5))
        )
    } else {
        slotPositions[RIGHT_BOTTOM_CENTER] =
            SlotPosition(
                x = tableWidth - 1.4f * playerSize - leftMargin - inPlayWidth ,
                y = tableHeight / 1.8f,
                player = Position(
                    ml = (playerSize/1.5f).toInt()),
                revealCards = Position(),
                raiseAmt = Position(
                    alignment = Gravity.START or Gravity.CENTER,
                    mt = dpToPx(5),
                    ml = dpToPx(7)
                ),
                tossCard = Position(
                    ml = ((playerSize/1.2f)/2).toInt(),
                    alignment = Gravity.START or Gravity.CENTER
                ),
                activeIndication = Gravity.START,
                playerAction =  Position(
                    alignment = Gravity.START or Gravity.CENTER,
                    mt = dpToPx(5))
            )
    }

    if (isLandscape) {
        slotPositions[RIGHT_BOTTOM] =
            SlotPosition(
                x = tableWidth - 3.3f * playerSize - leftMargin/5f - inPlayWidth,
                y = tableHeight - playerSize + topMargin * 2.2f - inPlayHeight / 1.5f - dpToPx(20),
                player = Position(
                    mt = dpToPx(20)
                ),
                revealCards = Position(
                    alignment = Gravity.TOP or Gravity.END
                ),
                raiseAmt = Position(ml = 0, alignment = Gravity.TOP or Gravity.CENTER),
                tossCard = Position(
                    alignment = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                ),
                activeIndication = Gravity.END,
                playerAction =  Position(alignment = Gravity.TOP or Gravity.CENTER, mt = dpToPx(5))
            )
    } else {
        slotPositions[RIGHT_BOTTOM] =
            SlotPosition(
                x = tableWidth - 1.4f * playerSize - leftMargin - inPlayWidth ,
                y = tableHeight / 1.45f,
                player = Position(
                    ml = (playerSize/1.5f).toInt()),
                revealCards = Position(),
                raiseAmt = Position(
                    alignment = Gravity.START or Gravity.CENTER,
                    mt = dpToPx(5),
                    ml = dpToPx(7)
                ),
                activeIndication = Gravity.START,
                playerAction =  Position(
                    alignment = Gravity.START or Gravity.CENTER,
                    mt = dpToPx(5)
                ),
                tossCard = Position(
                    ml = ((playerSize/1.2f)/2).toInt(),
                    mt = dpToPx(10),
                    alignment = Gravity.START or Gravity.CENTER
                )
            )
    }
    if (isLandscape) {
        slotPositions[BOTTOM_CENTER] =
            SlotPosition(
                x = (tableWidth - playerSize) / 2.15f,
                y = tableHeight - playerSize * 1.3f + topMargin * 2.17f- dpToPx(20),
                player = Position(
                    mt = dpToPx(20)
                ),
                revealCards = Position(
                    alignment = -1
                ),
                tossCard = Position(
                    alignment = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                ),
                raiseAmt = Position(ml = 0, alignment = Gravity.TOP or Gravity.CENTER,  mt =  dpToPx(5)),
                activeIndication = Gravity.START,
                playerAction =  Position(alignment = Gravity.TOP or Gravity.CENTER, mt = dpToPx(5)))
    } else {
        slotPositions[BOTTOM_CENTER] =
            SlotPosition(
                /* x = playerSize / 2 + leftMargin / 2 - 2,
                 y = tableHeight/1.3f ,
                 */
                x = (tableWidth - playerSize) / 2.3f -inPlayWidth/4,
                y = tableHeight / 1.2f- dpToPx(20),
                player = Position( mt = dpToPx(20)),
                tossCard = Position(
                    alignment = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                ),
                raiseAmt = Position(alignment = Gravity.TOP or Gravity.CENTER, mt =  dpToPx(5)),
                activeIndication = Gravity.START,
                playerAction =  Position(alignment = Gravity.TOP or Gravity.CENTER, mt = dpToPx(5)))
    }


    if (isLandscape) {
        slotPositions[LEFT_BOTTOM] =
            SlotPosition(
                x = 3.3f * playerSize + leftMargin/5f - inPlayWidth,
                y = tableHeight - playerSize + topMargin * 2.2f - inPlayHeight / 1.5f - dpToPx(20),
                player = Position(
                    mt = dpToPx(20)
                ),
                revealCards = Position(
                    alignment = Gravity.TOP or Gravity.END
                ),
                tossCard = Position(
                    alignment = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                ),
                raiseAmt = Position(ml = 0, alignment = Gravity.TOP or Gravity.CENTER),
                activeIndication = Gravity.START,
                playerAction =  Position(alignment = Gravity.TOP or Gravity.CENTER, mt = dpToPx(5)))
    } else {
        slotPositions[LEFT_BOTTOM] =
            SlotPosition(
                x = playerMargin.toFloat() /  4 - roundingSize * 2,
                y = tableHeight / 1.45f,
                player = Position(),
                revealCards = Position(
                ),
                raiseAmt = Position(
                    alignment = Gravity.END or Gravity.CENTER,
                    mt = dpToPx(5),
                    ml =  (playerSize + roundingSize * 10 + dpToPx(10)).toInt()),
                tossCard = Position(
                    mt = dpToPx(10),
                    ml = (playerSize + roundingSize * 10 + dpToPx(20)).toInt(),
                    alignment = Gravity.END or Gravity.CENTER_VERTICAL
                ),
                activeIndication = Gravity.START,
                playerAction =  Position(alignment = Gravity.END or Gravity.CENTER, mt = dpToPx(5)))

    }

    if (isLandscape) {
        slotPositions[LEFT_BOTTOM_CENTER] =
            SlotPosition(
                x = playerSize / 3 + leftMargin / 2,
                y = tableHeight - 2f * playerSize + topMargin,
                player = Position(),
                raiseAmt = Position(ml = (playerSize + roundingSize * 10 + dpToPx(20)).toInt(),
                    mt = dpToPx(10),
                    alignment = Gravity.END or Gravity.CENTER),
                tossCard = Position(
                    mt = dpToPx(10),
                    ml = (playerSize + roundingSize * 10 + dpToPx(20)).toInt(),
                    alignment = Gravity.END or Gravity.CENTER_VERTICAL
                ),
                activeIndication = Gravity.START,
                playerAction =  Position(alignment = Gravity.END or Gravity.CENTER, mt = dpToPx(5)))
    } else {
        slotPositions[LEFT_BOTTOM_CENTER] =
            SlotPosition(
                x = playerMargin.toFloat() /  4 - roundingSize * 2,
                y = tableHeight / 1.8f,
                player = Position(
                ),
                revealCards = Position(
                ),
                tossCard = Position(
                    mt = dpToPx(10),
                    ml = (playerSize + roundingSize * 10 + dpToPx(20)).toInt(),
                    alignment = Gravity.END or Gravity.CENTER_VERTICAL
                ),
                raiseAmt = Position(
                    alignment = Gravity.END or Gravity.CENTER_VERTICAL,
                    mt = dpToPx(5),
                    ml =  (playerSize + roundingSize * 10 + dpToPx(10)).toInt()),
                activeIndication = Gravity.START,
                playerAction =  Position(alignment = Gravity.END or Gravity.CENTER, mt = dpToPx(5)))
    }


    slotPositions[LEFT_CENTER] =
        SlotPosition(
            x = roundingSize + leftMargin.toFloat() - roundingSize * 2,
            y = (tableHeight - playerSize) / 2 + topMargin / 2,
            player = Position(),
            revealCards = Position(),
            raiseAmt = Position(
                ml =  (playerSize + roundingSize * 10 + dpToPx(15)).toInt(),
                mt = dpToPx(10),
                alignment = Gravity.END or Gravity.CENTER),
            tossCard = Position(
                mt = dpToPx(10),
                ml = (playerSize + roundingSize * 10 + dpToPx(20)).toInt(),
                alignment = Gravity.END or Gravity.CENTER_VERTICAL
            ),
            activeIndication = Gravity.START,
            playerAction =  Position(alignment = Gravity.END or Gravity.CENTER, mt = dpToPx(5)))
    slotPositions[RIGHT_CENTER] =
        SlotPosition(
            x = tableWidth - playerSize - leftMargin - inPlayWidth - (roundingSize * 10).toInt(),
            y = (tableHeight - playerSize) / 2 + topMargin / 2,
            player = Position(
                ml = (playerSize/1.2f).toInt()
            ),
            revealCards = Position(),
            raiseAmt = Position(
                mt = dpToPx(10),
                alignment = Gravity.START or Gravity.CENTER
            ),
            tossCard = Position(
                ml = ((playerSize/1.2f)/2).toInt(),
                mt = dpToPx(10),
                alignment = Gravity.START or Gravity.CENTER
            ),
            activeIndication = Gravity.START,
            playerAction =  Position(alignment = Gravity.START or Gravity.CENTER, mt = dpToPx(5)))

    if (isLandscape) {
        return SlotPosition(
            x = (tableWidth - meSlotSize) / 2 - inPlayWidth/2,
            y = tableHeight - playerSize * 1.3f + topMargin * 2.17f - dpToPx(20),
            player = Position(ml = inPlayWidth/4, mr = inPlayWidth/4, mt = dpToPx(20)),
            tossCard = Position(
                alignment = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            ),
            raiseAmt = Position(ml = 0, alignment = Gravity.TOP or Gravity.CENTER),
            activeIndication = Gravity.START,
            playerAction =  Position(alignment = Gravity.TOP or Gravity.CENTER, mt = dpToPx(5)))
    } else {
        return SlotPosition(
            x = (tableWidth - playerSize) / 2.3f -inPlayWidth/4,
            y = tableHeight / 1.2f - dpToPx(20),
            player = Position( mt = dpToPx(20)),
            tossCard = Position(
                alignment = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            ),
            raiseAmt = Position(alignment = Gravity.TOP or Gravity.CENTER, mt =  dpToPx(5)),
            activeIndication = Gravity.START,
            playerAction =  Position(alignment = Gravity.TOP or Gravity.CENTER, mt =  dpToPx(5)))
    }

}