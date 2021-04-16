package com.gtgt.pokerjacks.ui.game

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.ActivityInfo.*
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.os.ResultReceiver
import android.view.LayoutInflater
import android.view.View.*
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import com.devs.vectorchildfinder.VectorChildFinder
import com.github.salomonbrys.kotson.*
import com.gtgt.pokerjacks.BuildConfig
import com.gtgt.pokerjacks.InsufficientBalanceActivity
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.FullScreenScreenOnActivity
import com.gtgt.pokerjacks.extensions.*
import com.gtgt.pokerjacks.socket.SocketIoInstance
import com.gtgt.pokerjacks.socket.socketInstance
import com.gtgt.pokerjacks.ui.game.models.TableSlotStatus
import com.gtgt.pokerjacks.ui.game.view.GamePreferencesFragment
import com.gtgt.pokerjacks.ui.game.view.SelectThemesFragment
import com.gtgt.pokerjacks.ui.game.view.slot.SlotViews
import com.gtgt.pokerjacks.ui.game.viewModel.*
import com.gtgt.pokerjacks.ui.lobby.model.LobbyTables
import com.gtgt.pokerjacks.ui.wallet.wallet.WalletViewModel
import com.gtgt.pokerjacks.utils.CustomCountDownTimer
import com.gtgt.pokerjacks.utils.loadImage
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.byin_popup.view.*
import kotlinx.android.synthetic.main.byin_popup.view.close
import kotlinx.android.synthetic.main.byin_popup.view.insufficient
import kotlinx.android.synthetic.main.byin_popup.view.join
import kotlinx.android.synthetic.main.join_status_popup.view.*
import kotlinx.android.synthetic.main.raise_amt.*
import java.lang.Math.abs


class GameActivity : FullScreenScreenOnActivity(), SocketIoInstance.SocketConnectionChangeListener {

    override fun connectionAvailable() {
        reconnect.text = "Go Offline"
        offlineMsg.visibility = GONE
        vm.tableId = tableId
    }

    override fun reconnected() {
        offlineMsg.visibility = GONE
        vm.tableId = tableId
    }

    override fun connectionUnavailable() {
        reconnect.text = "Go Online"
        offlineMsg.visibility = VISIBLE
    }

    override fun networkSpeed(speed: Int) {
        signalIv.setImageResource(
            when (speed) {
                in 0..150 -> R.drawable.signal_4
                in 151..250 -> R.drawable.signal_3
                in 251..350 -> R.drawable.signal_2
                in 351..500 -> R.drawable.signal_1
                else -> R.drawable.signal_0
            }
        )
    }

    private lateinit var slotViews: SlotViews
    private val vm: GameViewModel by viewModel()
    private val preferencesvm: GamePreferencesViewModel by viewModel()

    private val tableId by lazy { intent.getStringExtra("table_id") }
    private val plan by lazy { intent.getParcelableExtra<LobbyTables.PlanDetails>("plan")!! }
    private val walletVM: WalletViewModel by store()

    private val themesViewModel: ThemesViewModel by viewModel()
    private val gamePreferencesFragment by lazy { GamePreferencesFragment() }

    val topMarginLandscape = dpToPx(56).toFloat()
    val topMarginPortrait = dpToPx(100).toFloat()


    private var tableDetailsTimer: CountDownTimer? = null
    private var gameTriggerTimer: CustomCountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val orientation = resources.configuration.orientation

        mActivityTopLevelView = drawer_layout

        if (BuildConfig.DEBUG) {
            reconnect.visibility = VISIBLE
            shareReconnect.visibility = VISIBLE

            reconnect.onOneClick {
                if (socketInstance.socket.connected()) {
                    socketInstance.socket.disconnect()
                } else {
                    reconnected()
                }
            }

            shareReconnect.onOneClick {
                if (vm.connectionData == null) {
                    showSnack("Not Reconnected")
                } else {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "Reconnect:\n" + vm.connectionData!!.toPrettyJson()
                        )
                        type = "text/plain"
                    }

                    val shareIntent = Intent.createChooser(sendIntent, null)
                    startActivity(shareIntent)
                }
            }

        } else {
            reconnect.visibility = GONE
            shareReconnect.visibility = GONE
        }

        refresh.onOneClick { reconnected() }

        socketInstance.connect()
        if (socketInstance.listeners.isEmpty()) {
            socketInstance.addSocketChangeListener(this)
        }

        themeSelectFragment.z = 11f
        topPanel.z = 11f
        raiseLL.z = 100f

        vm.isAutoRotateOn.observe(this, Observer {
            if(it){
                requestedOrientation = SCREEN_ORIENTATION_FULL_SENSOR
            }else{
                requestedOrientation = SCREEN_ORIENTATION_NOSENSOR
            }
        })
        c5.onRendered {
            slotViews = SlotViews(rootLayout) { seatNo ->
                if (plan.mode == "REAL") {
                    walletVM.getRealAmount { byIn(it, seatNo, true) }
                } else {
                    walletVM.getPlayAmount { byIn(it, seatNo, false) }
                }
            }

            slotViews.isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE
            vm.isLandscape = slotViews.isLandscape
            drawer_layout.closeDrawers()
            slotViews.totalSlots = plan.max_players

            val cWidth = it.width / 1.5f
            mc1.widthHeightRaw(cWidth)
            mc2.widthHeightRaw(cWidth)
            mc2.marginsRaw(left = (cWidth / 1.5).toInt())

            replaceFragment(gamePreferencesFragment, R.id.settingsFragment, true)
            replaceFragment(SelectThemesFragment(), R.id.themeSelectFragment, true)

            settings.onOneClick {
                gamePreferencesFragment.type = "settings"
                openRightDrawer()
            }

            menu.onOneClick {
                gamePreferencesFragment.type = "menu"
                openRightDrawer()
            }

            val settingsVector = VectorChildFinder(this, R.drawable.settings, settings)
            val menuVector = VectorChildFinder(this, R.drawable.menu, menu)
            val statsVector = VectorChildFinder(this, R.drawable.stats, stats)
            val previousVector = VectorChildFinder(this, R.drawable.previous, previous)
            themesViewModel.onThemeSelected.observe(this, Observer { theme ->
                if (theme != null) {
                    rootLayout.setBackgroundResource(theme.bg)
//                gameInfoIv.imageTintList = ColorStateList.valueOf(theme.dark)

                    iAMBack.background = theme.btnDrawable

                    menuVector.changePathStrokeColor("stroke", theme.btn2)
                    menu.invalidate()

                    settingsVector.changePathStrokeColor("stroke", theme.btn2)
                    settings.invalidate()

                    statsVector.changePathStrokeColor("stroke", theme.btn2)
                    stats.invalidate()

                    previousVector.changePathStrokeColor("stroke", theme.btn2)
                    previous.invalidate()

                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        ivTable.loadImage(theme.landscapeTable)
                    } else {
                        ivTable.loadImage(theme.portraitTable)
                        /*val matrix = Matrix()
                        matrix.postRotate(90f)
                        val myImg = BitmapFactory.decodeResource(resources, theme.table)

                        val rotated = Bitmap.createBitmap(
                            myImg, 0, 0, myImg.width, myImg.height,
                            matrix, true
                        )

                        ivTable.setImageBitmap(rotated)*/
                    }
                }
            })

            vm.tableSlotsLD.observe(this, Observer {
                slotViews.isJoined = vm.mySlot != null

                log("Poker::tableSlotsLD", "slots:/n" +it)
                if (vm.mySlot != null) {

                    when (vm.mySlot!!.status) {
                        SeatStatus.SIT_OUT.status -> {
                            iAMBack.visibility = VISIBLE
                        }
                        SeatStatus.ACTIVE.status -> {
                            joinBBcb.visibility = GONE
                        }
                        SeatStatus.WAIT_FOR_BB.status -> {
                            joinBBcb.visibility = VISIBLE
                            joinBBcb.isChecked = true
                        }
                        SeatStatus.WAIT_FOR_NEXT.status -> {
                            joinBBcb.visibility = VISIBLE
                            joinBBcb.isChecked = false
                        }
                    }
                }

                slotViews.drawSlots(it)
            })

            vm.gameTriggerLD.observe(this, Observer {

                it?.let {
                    if(vm.isLandscape){
                        gameIDTV.text = it.game_uid
                    }else{
                        portrait_gameIDTV.text = it.game_uid
                    }

//                gameIDTV.invalidate()

                    /*gamePreferencesFragment.dismissExitLobbyDialog()

                          gameIdTV.visibility = View.VISIBLE
                          gameIdTV.text = it.gameUID*/

                    slotViews.usersBestHand = null
                    slotViews.crownTo(-1)
                    slotViews.resetPlayers()

                    c1.alpha = 1f
                    c2.alpha = 1f
                    c3.alpha = 1f
                    c4.alpha = 1f
                    c5.alpha = 1f

                    mc1.alpha = 1f
                    mc2.alpha = 1f

                    messageFL.visibility = VISIBLE

                    if (vm.isCommunityCardsOpened) {
                        totalPot.visibility = VISIBLE
                    }
                    if (it.start_time > (System.currentTimeMillis() - timeDiffWithServer)) {
                        leaderboardView.visibility = GONE

                        bottomPannel.visibility = INVISIBLE
                        if(!vm.isLandscape && bottomPannel1!=null){
                            bottomPannel1.visibility = INVISIBLE
                        }
                        community_cards_ll.visibility = INVISIBLE
                        user_cards_fl.visibility = INVISIBLE
                        totalPot.visibility = INVISIBLE
                        pot_split.visibility = INVISIBLE

//                exit.visibility = View.GONE
//                playArea.visibility = GONE


                        /*slotViews.showTime = 0L
                slotViews.stopTimers()*/

                        raiseLL.visibility = GONE

                        afterSubmit.visibility = GONE
                        vm.resetGame()

                        messageFL.visibility = VISIBLE
                        waitingTv.visibility = GONE
                        gameStartsTimer.visibility = VISIBLE
//                gamePreferencesViewModel.exitVisibility.value = View.GONE

                        closeShow.visibility = GONE
                        blur.visibility = GONE

                        try {
                            tableDetailsTimer?.cancel()
                            gameTriggerTimer?.cancel()
                        } catch (ex: Exception) {
                        }

                        gameTriggerTimer = object :
                            CustomCountDownTimer(
                                it.start_time - (System.currentTimeMillis() - timeDiffWithServer),
                                1000
                            ) {
                            override fun onTick(millisUntilFinished: Long) {
                                gameStartsTimer.text =
                                    "Game starts in ${millisUntilFinished / 1000} seconds..."
                            }

                            override fun onStop() {
                                messageFL.visibility = GONE
                            }

                            override fun onFinish() {
                                messageFL.visibility = GONE
                            }
                        }
                        gameTriggerTimer!!.start()
                    } else {
                        try {
                            messageFL.visibility = GONE
//                    gamePreferencesViewModel.exitVisibility.value = View.VISIBLE
                            playArea.visibility = VISIBLE

                            tableDetailsTimer?.cancel()
                            gameTriggerTimer?.cancel()
                        } catch (ex: Exception) {
                        }
                    }
                }
            })

            vm.userContestDetailsLD.observe(this, Observer {
                it?.let {
                    user_cards_fl.visibility = VISIBLE
                    mc1.coloredCard(it.card_1)
                    mc2.coloredCard(it.card_2)
                }
            })

            vm.gameDetailsLD.observe(this, Observer {
                it?.let {
                    disableEnableActions(true)

                    slotViews.dealerPosition = it.dealer_position

                    gameTriggerTimer?.stop()
                    val slots = vm.tableSlotsLD.value
                    if (slots != null && slots.count { !it.user_unique_id.isNullOrEmpty() } >= 2) {
//                    sitOutCB.visibility = VISIBLE
                    } else {
                        sitOutCB.visibility = GONE
                    }
                    bottomPannel.visibility = VISIBLE
                    if(!vm.isLandscape && bottomPannel1!=null){
                        bottomPannel1.visibility = VISIBLE
                    }
                    community_cards_ll.visibility = VISIBLE

                    c1.coloredCard(it.card_1)
                    c2.coloredCard(it.card_2)
                    c3.coloredCard(it.card_3)
                    c4.coloredCard(it.card_4)
                    c5.coloredCard(it.card_5)
                }
            })

            vm.dealCommunityCardsLD.observe(this, Observer {
                val slots = vm.tableSlotsLD.value

//            val potSplitPadding = dpToPx(15).toFloat()
                val potSplitTopLandscape = dpToPx(40).toFloat()
                val potSplitTopPortrait = playArea.height/4

                if (slots != null) {
                    slots.forEach { slot ->
                        if (slot.user != null && slot.status == TableSlotStatus.ACTIVE.name
                            && slot.user!!.current_round_invested!=0.0) {
                            val position = slotViews.getPositionBySeatNumber(slot.seat_no)
                            val coinsTv = TextView(this)
                            rootLayout.addView(coinsTv)
                            coinsTv.apply {
                                drawableLeft(R.drawable.coin_small)
                                compoundDrawablePadding = dpToPx(3)
                                x = position.x + position.raiseAmt.ml
                                y = position.y + position.raiseAmt.mt
                                text = String.format(
                                    "%.2f",
                                    slot.user!!.current_round_invested
                                )

                                animate().apply {
                                    log("poker::animatePots", "potAnim")
                                    x(totalPot.x + playArea.paddingTop + totalPot.width / 2)
                                    y(if(vm.isLandscape){
                                        topMarginLandscape +  potSplitTopLandscape
                                    }else{
                                        topMarginPortrait + potSplitTopPortrait
                                    })

                                    duration = 1000
                                    withEndAction {

                                        try {
                                            if (it.total_pot_value > 0.0) {
                                                totalPot.visibility = VISIBLE
                                                vm.isCommunityCardsOpened = true
                                                totalPot.text =
                                                    "Total Pot: ₹${it.total_pot_value.toDecimalFormat()}"

                                                pot_split.removeAllViews()
                                                it.side_pots.forEach {
                                                    pot_split.addView(TextView(this@GameActivity).apply {
                                                        text =
                                                            "  ₹ ${it.pot_value.toDecimalFormat()}  "
                                                    })
                                                }

                                                pot_split.visibility =
                                                    if (it.side_pots.size >= 2) VISIBLE else INVISIBLE
                                            }
                                        } catch (ex: Exception) {

                                        }

                                        rootLayout.removeView(coinsTv)
                                    }
                                    start()
                                }
                            }
                        }
                    }
                }

                timeOut(1000) {
                    c1.coloredCard(it.card_1)
                    c2.coloredCard(it.card_2)
                    c3.coloredCard(it.card_3)
                    c4.coloredCard(it.card_4)
                    c5.coloredCard(it.card_5)
                    vm.getHandStrength()
                }
            })

            vm.playerTurnLD.observe(this, Observer {
                joinBBcb.visibility = GONE

//            log("playerTurnLD", it)

                if (it == null) {
                    raiseLL.visibility = GONE
                } else {

                    if (vm.mySlot == null) {
//                iAMBack.visibility = GONE
                        sitOutCB.visibility = GONE
                    } else {
                        val slots = vm.tableSlotsLD.value
                        if (slots != null && slots.count { !it.user_unique_id.isNullOrEmpty() } >= 2) {
                            sitOutCB.visibility = VISIBLE
                        }
                    }

                    if (it.player_turn == vm.userId) {

                        if (preferencesvm.vibrate)
                            vibrate(this) {}

                        foldCB.visibility = GONE
                        checkCB.visibility = GONE
                        checkOrCallAnyCB.visibility = GONE
                        fold_checkCB.visibility = GONE
                        callCB.visibility = GONE
                        callAnyCB.visibility = GONE

                        auto_options_rg.clearCheck()

                        if (vm.mySlot!!.user!!.status.startsWith("AUTO_")) {
                            foldBtn.visibility = GONE
                            callLL.visibility = GONE
                            raiseBtnLL.visibility = GONE
                            checkBtn.visibility = GONE
                            allinLL.visibility = GONE
                        } else {
                            foldBtn.visibility =
                                if (it.action_choices.contains(PlayerActions.FOLD.action)) VISIBLE else GONE

                            callLL.visibility =
                                if (it.action_choices.contains(PlayerActions.CALL.action)) VISIBLE else GONE

                            raiseBtnLL.visibility =
                                if (it.action_choices.contains(PlayerActions.RAISE.action)) {
                                    raiseLL.visibility = VISIBLE
                                    VISIBLE
                                } else {
                                    raiseLL.visibility = GONE
                                    GONE
                                }

                            checkBtn.visibility =
                                if (it.action_choices.contains(PlayerActions.CHECK.action)) VISIBLE else GONE

                            allinLL.visibility =
                                if (it.action_choices.contains(PlayerActions.ALL_IN.action)) VISIBLE else GONE
                        }
                        callBtn.text = "₹${it.player_min_amount_to_call.toDecimalFormat()}"
                        callBtn.tag = it.player_min_amount_to_call


                        val maxPossibleRaise = it.current_max_raise
                        max_raise_value.text = maxPossibleRaise.toString()
                        val minPossibleRaise = it.current_min_raise

                        seek_raise.max = ((maxPossibleRaise - minPossibleRaise) * 100).toInt()

                        pot_raise.onOneClick {
                            seek_raise.progress =
                                ((minPossibleRaise) * 100).toInt()
                        }
                        raise_3_4.onOneClick {
                            seek_raise.progress =
                                ((minPossibleRaise) * 300 / 4).toInt()
                        }
                        raise_1_2.onOneClick {
                            seek_raise.progress = ((minPossibleRaise) * 50).toInt()
                        }


                        min_raise.onOneClick {
                            seek_raise.progress = 0
                        }

                        max_raise.onOneClick {
                            seek_raise.progress = seek_raise.max
                        }

                        increase_seek_raise.onOneClick {
                            seek_raise.progress += 100
                        }

                        decrease_seek_raise.onOneClick {
                            seek_raise.progress -= 100
                        }

                        seek_raise.setOnSeekBarChangeListener(object :
                            SeekBar.OnSeekBarChangeListener {
                            override fun onProgressChanged(
                                seekBar: SeekBar?,
                                progress: Int,
                                fromUser: Boolean
                            ) {
                                log("minPossibleRaiseprogress", progress)
                                raiseBtn.text =
                                    "₹${(progress / 100.0 + minPossibleRaise).toDecimalFormat()}"
                                raiseBtn.tag = (progress / 100.0 + minPossibleRaise)
                            }

                            override fun onStartTrackingTouch(seekBar: SeekBar?) {

                            }

                            override fun onStopTrackingTouch(seekBar: SeekBar?) {

                            }
                        })
                        seek_raise.progress = 0

                        raiseBtn.text =
                            "₹${(it.current_min_raise).toDecimalFormat()}"

                        allinBtn.text =
                            "₹${(it.allin_amount).toDecimalFormat()}"
                        raiseBtn.tag = minPossibleRaise
                    } else {
                        raiseLL.visibility = GONE

                        if (vm.me != null) {
                            when {
                                vm.me!!.status == PlayerActions.ALL_IN.action || vm.me!!.status == PlayerActions.FOLD.action -> {
                                    fold_checkCB.visibility = GONE
                                    checkCB.visibility = GONE
                                    checkOrCallAnyCB.visibility = GONE

                                    foldCB.visibility = GONE
                                    callCB.visibility = GONE
                                    callAnyCB.visibility = GONE
                                }
                                vm.me!!.amount_invested == it.game_max_bet_amount -> {
                                    fold_checkCB.visibility = VISIBLE
                                    checkCB.visibility = VISIBLE
                                    checkOrCallAnyCB.visibility = VISIBLE

                                    foldCB.visibility = GONE
                                    callCB.visibility = GONE
                                    callAnyCB.visibility = GONE
                                }
                                else -> {
                                    fold_checkCB.visibility = GONE
                                    checkCB.visibility = GONE
                                    checkOrCallAnyCB.visibility = GONE

                                    foldCB.visibility = VISIBLE
                                    callCB.visibility = VISIBLE
                                    callAnyCB.visibility = VISIBLE
                                }
                            }
                        }

                        foldBtn.visibility = GONE
                        callLL.visibility = GONE
                        raiseBtnLL.visibility = GONE
                        checkBtn.visibility = GONE
                        allinLL.visibility = GONE
                    }

                    slotViews.playerTurn = it
                }
            })

            vm.enableActions.observe(this, Observer {
                disableEnableActions(it)
            })

            var isPotAnimationDone = true
            vm.leaderboardLD.observe(this, Observer { leaderboard ->
                if (leaderboard == null)
                    return@Observer

                bottomPannel.visibility = INVISIBLE
                if(!vm.isLandscape && bottomPannel1!=null){
                    bottomPannel1.visibility = INVISIBLE
                }

                slotViews.resetPlayers()

                pot_split.visibility = VISIBLE
                val potSplitTop = dpToPx(50).toFloat() + totalPot.height
                val playAreaPadding = playArea.paddingStart
                val potSplitPadding = dpToPx(15).toFloat()

                val pots =
                    leaderboard.pot_winnings.filter { it.wonAmt > 0 }.sortedBy { it.pot_index }

                fun animatePots(potIndex: Int) {
                    if (pots.size > potIndex) {
                        log("poker::animatePots", "potIndex : " +potIndex)
                        isPotAnimationDone = false
                        slotViews.crownTo(-1)

                        c1.alpha = 1f
                        c2.alpha = 1f
                        c3.alpha = 1f
                        c4.alpha = 1f
                        c5.alpha = 1f

                        mc1.alpha = 1f
                        mc2.alpha = 1f

                        val potWinning = pots[potIndex]

                        val c1 = pot_split.getChildAt(0) ?: return

                        val c = c1 as TextView

                        val currentPotView = TextView(this)
                        rootLayout.addView(currentPotView)

                        currentPotView.apply {
                            text = c.text
                            setBackgroundColor(Color.parseColor("#AD000000"))
                            x = pot_split.x + c.x + playAreaPadding + potSplitPadding
                            y = topMarginLandscape + potSplitTop
                            z = 100f
                        }

                        if (leaderboard.cards_reveal) {
                            slotViews.usersBestHand = leaderboard.users_best_hand
                            slotViews.resetPlayers()
                        }

                        vm.tableSlotsLD.value?.let { slots ->
                            slots.find { it.user_unique_id == potWinning.user_unique_id }?.let {
                                slotViews.slotViews[it.seat_no]?.let { slot ->
                                    c.visibility = INVISIBLE

                                    leaderboard.users_best_hand
                                        .find { it.user_unique_id == potWinning.user_unique_id }
                                        ?.let {
                                            val cards = it.best_hand_details.cards

                                            slotViews.crownTo(
                                                it.seat_no,
                                                if (leaderboard.cards_reveal)
                                                    listOf(
                                                        if (cards.contains(it.card_1)) 1f else 0.4f,
                                                        if (cards.contains(it.card_2)) 1f else 0.4f
                                                    )
                                                else
                                                    listOf(1f, 1f)
                                            )

                                            if (leaderboard.cards_reveal) {

                                                if (it.user_unique_id == vm.userId) {
                                                    mc1.alpha =
                                                        if (cards.contains(it.card_1)) 1f else 0.4f
                                                    mc2.alpha =
                                                        if (cards.contains(it.card_2)) 1f else 0.4f
                                                }

                                                c1.alpha =
                                                    if (cards.contains(leaderboard.community_cards.card_1)) 1f else 0.4f
                                                c2.alpha =
                                                    if (cards.contains(leaderboard.community_cards.card_2)) 1f else 0.4f
                                                c3.alpha =
                                                    if (cards.contains(leaderboard.community_cards.card_3)) 1f else 0.4f
                                                c4.alpha =
                                                    if (cards.contains(leaderboard.community_cards.card_4)) 1f else 0.4f
                                                c5.alpha =
                                                    if (cards.contains(leaderboard.community_cards.card_5)) 1f else 0.4f

                                            }
                                        }

                                    currentPotView.animate().apply {
                                        x(slot.x + slot.width / 2)
                                        y(slot.y + slot.height / 2)
                                        duration = 1000
                                        withEndAction {
                                            rootLayout.removeView(currentPotView)
                                            animatePots(potIndex + 1)
                                        }
                                        start()
                                    }
                                }
                            }
                        }
                    } else {
//                    showToast("true")
                        isPotAnimationDone = true
                    }
                }

                if (isPotAnimationDone) {
                    animatePots(0)
                }
            })

            foldBtn.onOneClick {
                disableEnableActions(false)
                vm.actionEvent(ActionEvent.FOLD) {
                    runOnMain {
                        disableEnableActions(true)

                        if (it!!["success"].bool) {
                            slotViews.resetPlayers()
                            mc1.setImageResource(R.drawable.deck_card)
                            mc2.setImageResource(R.drawable.deck_card)
                        } else {
                            showSnack(it["description"].string)
                        }
                    }
                }
            }

            callBtn.onOneClick {
                disableEnableActions(false)
                vm.actionEvent(ActionEvent.CALL_BET, total_amount = callBtn.tag as Double) {
                    disableEnableActions(true)
                }
            }
            checkBtn.onOneClick {
                disableEnableActions(false)
                vm.actionEvent(ActionEvent.CHECK_BET, total_amount = callBtn.tag as Double) {
                    disableEnableActions(true)
                }
            }

            allinBtn.onOneClick {
                disableEnableActions(false)
                vm.actionEvent(ActionEvent.ALL_IN, total_amount = callBtn.tag as Double) {
                    disableEnableActions(true)

                    if (it!!["success"].bool) {
                        slotViews.resetPlayers()
                    }
                }
            }

            raiseBtn.onOneClick {
                disableEnableActions(false)
                vm.actionEvent(
                    ActionEvent.RAISE_BET,
                    total_amount = raiseBtn.tag as Double,
                    raise_amount = raiseBtn.tag as Double - callBtn.tag as Double
                ) {
                    disableEnableActions(true)
                }
            }

            sitOutCB.onOneClick {
                vm.updateSeatStatus(if (sitOutCB.isChecked) SeatStatus.SIT_OUT else SeatStatus.ACTIVE) {
                    runOnMain {
                        if (it!!["success"].bool) {
                            /*iAMBack.visibility = VISIBLE
                        bottomPannel.visibility = GONE*/
                        } else {
                            sitOutCB.isChecked = !sitOutCB.isChecked
                            showSnack(it["description"].string)
                        }
                    }
                }
            }

            iAMBack.onOneClick {
                val slots = vm.tableSlotsLD.value

                if (slots != null && slots.count { !it.user_unique_id.isNullOrEmpty() } == 2) {
                    vm.updateSeatStatus(SeatStatus.ACTIVE) {
                        runOnMain {
                            if (it!!["success"].bool) {
                                sitOutCB.isChecked = false
                                iAMBack.visibility = GONE
                            } else {
                                showSnack(it["description"].string)
                            }
                        }
                    }
                } else {
                    joinTableActions {
                        iAMBack.visibility = GONE
                        bottomPannel.visibility = VISIBLE
                        if(!vm.isLandscape && bottomPannel1!=null){
                            bottomPannel1.visibility = VISIBLE
                        }
                    }
                }
            }

            vm.iamBackLD.observe(this, Observer {
                if (vm.mySlot != null && vm.mySlot!!.status == SeatStatus.SIT_OUT.status) {
                    if (it) {
                        iAMBack.visibility = VISIBLE
                        bottomPannel.visibility = GONE
                        if(!vm.isLandscape && bottomPannel1!=null){
                            bottomPannel1.visibility = GONE
                        }
                    } else {
                        iAMBack.visibility = GONE
                        bottomPannel.visibility = VISIBLE
                        if(!vm.isLandscape && bottomPannel1!=null){
                            bottomPannel1.visibility = VISIBLE
                        }
                    }
                }
            })

            joinBBcb.setOnClickListener {
                vm.updateSeatStatus(
                    if (joinBBcb.isChecked) SeatStatus.WAIT_FOR_BB
                    else SeatStatus.WAIT_FOR_NEXT
                ) {
                    if (it!!["success"].bool) {

                    } else {
                        showSnack(it["description"].string)
                    }
                }
            }

            foldCB.onOneClick {
                vm.autoGameAction(AutoGameAction.AUTO_FOLD) {}
            }
            checkCB.onOneClick {
                vm.autoGameAction(AutoGameAction.AUTO_CHECK) {}
            }
            checkOrCallAnyCB.onOneClick {
                vm.autoGameAction(AutoGameAction.AUTO_CALL) {}
            }

            fold_checkCB.onOneClick {
                vm.autoGameAction(AutoGameAction.AUTO_CHECK_FOLD) {}
            }
            callCB.onOneClick {
                vm.autoGameAction(AutoGameAction.AUTO_CALL) {}
            }
            callAnyCB.onOneClick {
                vm.autoGameAction(AutoGameAction.AUTO_CALL_ANY) {}
            }
        }
    }

    private fun disableEnableActions(isEnabled: Boolean) {
        runOnMain {
            foldBtn.isEnabled = isEnabled
            checkBtn.isEnabled = isEnabled
            callBtn.isEnabled = isEnabled
            raiseBtn.isEnabled = isEnabled
            allinBtn.isEnabled = isEnabled
        }
    }

    private fun openRightDrawer() {
        if (drawer_layout.isDrawerOpen(GravityCompat.END)) {
            drawer_layout.closeDrawer(GravityCompat.END)
        } else {
            drawer_layout.openDrawer(GravityCompat.END)
        }
    }

    private fun byIn(balance: Double, seatNo: Int, isCash: Boolean) {
        var availableBalance = balance

        val builder = AlertDialog.Builder(this)
        val dialogView =
            LayoutInflater.from(this)
                .inflate(R.layout.byin_popup, null)
        builder.setView(dialogView)
        builder.setCancelable(false)
        val dialog = builder.create()

        val amtDrawable =
            if (isCash) R.drawable.rupee else R.drawable.coin

        dialogView.availableBal.text = availableBalance.toDecimalFormat()
        dialogView.availableBal.drawableLeft(amtDrawable)


        dialogView.minByin.text = plan.min_buyin.toInt().toDecimalFormat()
        dialogView.minByin.drawableLeft(amtDrawable)

        dialogView.maxByin.text = plan.max_buyin.toInt().toDecimalFormat()
        dialogView.maxByin.drawableLeft(amtDrawable)

        dialogView.seek.max = plan.max_buyin.toInt() - plan.min_buyin.toInt()

        dialogView.byInAmt.text = plan.min_buyin.toInt().toString()
        dialogView.byInAmt.drawableLeft(amtDrawable)

        dialogView.join.background = selectedTheme?.btnDrawable

        fun joinNow() {
            val amt = dialogView.seek.progress + plan.min_buyin
            apiInterfaceTableManager.joinTable(
                jsonObject(
                    "plan_id" to plan._id,
                    "table_id" to tableId,
                    "seat_no" to seatNo,
                    "amount" to amt
                )
            ).execute(this) {
                if (isActivityRunning()) {
                    if (it.success) {
                        dialog.dismiss()
                        vm.userDetailsLD.data = it.info.user_details
                        vm.connectTable()

                        val slots = vm.tableSlotsLD.value
                        if (slots != null && slots.count { !it.user_unique_id.isNullOrEmpty() } >= 2) {
                            joinTableActions { }
                        }

//                        joinTableActions()

                    } else {
                        showToast(it.description)
                    }
                }
            }
        }

        fun validatedBalanceSufficient(amt: Double) {
            if (availableBalance < amt) {
                dialogView.insufficient.visibility = VISIBLE

                if (isCash) {
                    dialogView.join.text = "Add Money"

                    dialogView.join.onOneClick {
                        launchActivity<InsufficientBalanceActivity> {
                            putExtra("balance", availableBalance)
                            putExtra(
                                "buyIn",
                                dialogView.seek.progress + plan.min_buyin
                            )
                            putExtra(
                                "insufficientBalance",
                                abs(
                                    availableBalance - dialogView.byInAmt.text.toString()
                                        .toDouble()
                                )
                            )

                            putExtra(
                                "resultReceiver",
                                object : ResultReceiver(null) {
                                    override fun onReceiveResult(
                                        resultCode: Int,
                                        resultData: Bundle?
                                    ) {
                                        if (resultData != null && resultData.containsKey(
                                                "addedAmt"
                                            )
                                        ) {
                                            availableBalance += resultData.getDouble(
                                                "addedAmt"
                                            )

                                            dialogView.availableBal.text =
                                                availableBalance.toDecimalFormat()

                                            if (availableBalance >= dialogView.seek.progress + plan.min_buyin) {
                                                dialogView.insufficient.visibility =
                                                    GONE
                                                dialogView.join.text =
                                                    "Join Table"

                                                dialogView.join.onOneClick {
                                                    joinNow()
                                                }
                                            }
                                        }
                                    }
                                })
                        }
                    }
                } else {
                    dialogView.join.text = "Add Coins"
                    dialogView.join.drawableLeft(R.drawable.coin)
                    dialogView.join.onOneClick {
                        walletVM.addChips {
                            dialogView.insufficient.visibility = GONE

                            walletVM._playWalletDetailsResponse.value =
                                it.info["totalChips"].double

                            dialogView.availableBal.text =
                                (availableBalance + 5000).toDecimalFormat()

                            dialogView.join.drawableLeft(0)
                            dialogView.join.text = "Join Table"

                            dialogView.join.onOneClick {
                                joinNow()
                            }
                        }
                    }
                }
            } else {
                dialogView.insufficient.visibility = GONE
                dialogView.join.text = "Join Table"
                dialogView.join.onOneClick {
                    joinNow()
                }
            }
        }

        dialogView.seek.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                val buyIn = progress + plan.min_buyin
                dialogView.byInAmt.text =
                    "${buyIn.toInt()}"

                validatedBalanceSufficient(buyIn)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        validatedBalanceSufficient(availableBalance)

        dialogView.decreaseIv.setOnClickListener {
            dialogView.seek.progress = dialogView.seek.progress - 1
        }
        dialogView.increaseIv.setOnClickListener {
            dialogView.seek.progress = dialogView.seek.progress + 1
        }

        dialogView.close.onOneClick { dialog.dismiss() }

        dialog.show()
    }

    private fun joinTableActions(callback: () -> Unit) {
        if (vm.userDetailsLD.value?.auto_next_game == null || vm.userDetailsLD.value?.auto_next_game == false) {
            val builder = AlertDialog.Builder(this)
            val dialogView =
                LayoutInflater.from(this)
                    .inflate(R.layout.join_status_popup, null)
            builder.setView(dialogView)
            builder.setCancelable(false)
            val dialog = builder.create()

            dialogView.rg.setOnCheckedChangeListener { group, checkedId ->
                dialogView.makeDefault.visibility =
                    if (checkedId == R.id.join_from_next) VISIBLE else GONE
            }

            dialogView.join.onOneClick {
                vm.updateSeatStatus(
                    if (dialogView.join_from_next.isChecked) SeatStatus.WAIT_FOR_NEXT
                    else SeatStatus.WAIT_FOR_BB
                ) {
                    if (it!!["success"].bool) {
                        runOnMain {
                            joinBBcb.visibility = VISIBLE

                            joinBBcb.isChecked = !dialogView.join_from_next.isChecked

                            if (dialogView.join_from_next.isChecked && dialogView.makeDefault.isChecked) {
                                vm.updateUserGameSettings(jsonObject("auto_next_game" to true))
                            }

                            dialog.dismiss()

                            callback()
                        }
                    } else {
                        showSnack(it["description"].string)
                    }
                }
            }
            dialogView.close.onOneClick { dialog.dismiss() }

            dialog.show()
        }
    }

    fun exitTOLobby() {
        gameAlert(
            "Exit to Lobby",
            "Are you sure you want to return to lobby?"
        ) { isPositive, dialog ->
            if (isPositive) {
                dialog.dismiss()
                vm.leaveTable()
                socketInstance.removeSocketChangeListener(this)
                vm.isCommunityCardsOpened = false
                super.onBackPressed()
            } else {
                dialog.dismiss()
            }
        }

    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.END)) {
            drawer_layout.closeDrawer(GravityCompat.END)
        }
    }
}