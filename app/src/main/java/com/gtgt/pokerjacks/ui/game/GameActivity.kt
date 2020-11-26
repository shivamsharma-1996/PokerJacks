package com.gtgt.pokerjacks.ui.game

import android.os.Bundle
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.FullScreenScreenOnActivity
import com.gtgt.pokerjacks.extensions.*
import com.gtgt.pokerjacks.socket.socketInstance
import com.gtgt.pokerjacks.ui.game.models.tableSlots
import com.gtgt.pokerjacks.ui.game.view.slot.SlotViews
import com.gtgt.pokerjacks.ui.game.viewModel.GameViewModel
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : FullScreenScreenOnActivity() {
    private lateinit var slotViews: SlotViews
    private val vm: GameViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        mActivityTopLevelView = drawer_layout

        socketInstance.connect()

        c5.onRendered {
            slotViews = SlotViews(rootLayout)

            slotViews.totalSlots = tableSlots.size
            slotViews.isJoined = vm.mySlot != null

            slotViews.drawSlots(tableSlots)

            val cWidth = it.width / 1.5f
            mc1.widthHeightRaw(cWidth)
            mc2.widthHeightRaw(cWidth)
            mc2.marginsRaw(left = (cWidth / 2).toInt())
        }



        timeOut(2000) {
            /*animateView.visibility = VISIBLE
            iv_userProfile.visibility = GONE
            animateView.startAnim(1000 * 20)*/
        }

    }
}