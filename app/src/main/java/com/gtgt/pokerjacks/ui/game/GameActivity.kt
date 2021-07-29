package com.gtgt.pokerjacks.ui.game

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ActivityInfo.*
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface.BOLD
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.os.ResultReceiver
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View.*
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import com.devs.vectorchildfinder.VectorChildFinder
import com.github.salomonbrys.kotson.*
import com.google.gson.Gson
import com.gtgt.pokerjacks.BuildConfig
import com.gtgt.pokerjacks.InsufficientBalanceActivity
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.FullScreenScreenOnActivity
import com.gtgt.pokerjacks.extensions.*
import com.gtgt.pokerjacks.socket.SocketIoInstance
import com.gtgt.pokerjacks.socket.socketInstance
import com.gtgt.pokerjacks.ui.game.models.*
import com.gtgt.pokerjacks.ui.game.view.GamePreferencesFragment
import com.gtgt.pokerjacks.ui.game.view.LastHandFragment
import com.gtgt.pokerjacks.ui.game.view.SelectThemesFragment
import com.gtgt.pokerjacks.ui.game.view.StatsFragment
import com.gtgt.pokerjacks.ui.game.view.slot.SlotViews
import com.gtgt.pokerjacks.ui.game.viewModel.*
import com.gtgt.pokerjacks.ui.lobby.model.LobbyTables
import com.gtgt.pokerjacks.ui.wallet.wallet.WalletViewModel
import com.gtgt.pokerjacks.utils.CustomCountDownTimer
import com.gtgt.pokerjacks.utils.EventObserver
import com.gtgt.pokerjacks.utils.loadImage
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.activity_game.menu
import kotlinx.android.synthetic.main.activity_game.settings
import kotlinx.android.synthetic.main.byin_popup.view.*
import kotlinx.android.synthetic.main.byin_popup.view.close
import kotlinx.android.synthetic.main.byin_popup.view.insufficient
import kotlinx.android.synthetic.main.byin_popup.view.join
import kotlinx.android.synthetic.main.fragment_game_prefs.*
import kotlinx.android.synthetic.main.join_status_popup.view.*
import kotlinx.android.synthetic.main.player_new.view.*
import kotlinx.android.synthetic.main.raise_amt.*
import java.lang.Math.abs


class GameActivity : FullScreenScreenOnActivity(), SocketIoInstance.SocketConnectionChangeListener {

    override fun connectionAvailable() {
        reconnect.setImageResource(R.drawable.wifi_off)
        //reconnect.text = "Go Offline"
        offlineMsg.visibility = GONE
        vm.tableId = tableId
    }

    object Screen {
        val width: Int
            get() = Resources.getSystem().displayMetrics.widthPixels

        val height: Int
            get() = Resources.getSystem().displayMetrics.heightPixels
    }

    override fun reconnected() {
        offlineMsg.visibility = GONE
        vm.tableId = tableId
    }

    override fun connectionUnavailable() {
        reconnect.setImageResource(R.drawable.wifi_on)
        //reconnect.text = "Go Online"
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

    private var isAmountRaisedViaRaiseBar = false
    private var isAmountRaisedViaAllInBtn = false
    open var isGameStartedAndRunning = false
    private lateinit var slotViews: SlotViews
    public val vm: GameViewModel by viewModel()
    private val preferencesvm: GamePreferencesViewModel by viewModel()

    private val tableId by lazy { intent.getStringExtra("table_id") }
    private val plan by lazy { intent.getParcelableExtra<LobbyTables.PlanDetails>("plan")!! }
    private val walletVM: WalletViewModel by store()

    private val themesViewModel: ThemesViewModel by viewModel()
    private val gamePreferencesFragment by lazy { GamePreferencesFragment() }
    private val statsFragment by lazy {
        StatsFragment.newInstance {
            toggleDrawer(GravityCompat.START)
        }
    }
    private val lastHandFragment by lazy {
        LastHandFragment.newInstance {
            toggleDrawer(GravityCompat.START)
        }
    }


    val topMarginLandscape = dpToPx(56).toFloat()
    val topMarginPortrait = dpToPx(50).toFloat()

    private var tableDetailsTimer: CountDownTimer? = null
    private var gameTriggerTimer: CustomCountDownTimer? = null
    private var tossTriggerTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureOrientation(retrieveBoolean("autoRotate", false))
        setContentView(R.layout.activity_game)

        mActivityTopLevelView = drawer_layout
        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, leftDrawer)

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
        socketInstance.addSocketChangeListener(this)


        themeSelectFragment.z = 11f
        topPanel.z = 11f
        raiseLL.z = 100f

        vm.isAutoRotateOn.observe(this, Observer {
            configureOrientation(it)
            if (vm.isCommunityCardsOpened) {
                totalPot.visibility = VISIBLE
            }
        })

        c5.onRendered { it ->
            slotViews = SlotViews(rootLayout) { seatNo ->
                showBuyInAlert(false, seatNo)
            }
            drawer_layout.closeDrawers()
            val orientation = resources.configuration.orientation
            slotViews.isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE
            vm.isLandscape = slotViews.isLandscape

            //vm.isConfigurationChanged = orientation != vm.previousOrientation
            vm.previousOrientation = orientation


            slotViews.totalSlots = plan.max_players

            updatePotAmount()

            var cWidth = it.width / 1.3f
            mc2.marginsRaw(left = (cWidth / 1.5).toInt())
            if (!vm.isLandscape) {
                cWidth = it.width.toFloat()
            }
            mc1.widthHeightRaw(cWidth)
            mc2.widthHeightRaw(cWidth)

            replaceFragment(gamePreferencesFragment, R.id.settingsFragment, true)
            replaceFragment(SelectThemesFragment(), R.id.themeSelectFragment, true)

            settings.onOneClick {
                gamePreferencesFragment.type = "settings"
                toggleDrawer(GravityCompat.END)
            }

            stats.onOneClick {
                if (vm.isLandscape) {
                    leftDrawer.setPercentageWidth(0.35f)
                } else {
                    leftDrawer.setPercentageWidth(0.7f)
                }
                replaceFragment(statsFragment, R.id.leftNavFragment, true)
                vm.getTableUserStats()
                toggleDrawer(GravityCompat.START)
            }

            previousHandIv.onOneClick {
                if (vm.isLandscape) {
                    leftDrawer.setPercentageWidth(0.65f)
                } else {
                    leftDrawer.setPercentageWidth(0.75f)
                }
                replaceFragment(lastHandFragment, R.id.leftNavFragment, true)
                vm.getPreviousHandsList()
                toggleDrawer(GravityCompat.START)
            }

            menu.onOneClick {
                gamePreferencesFragment.type = "menu"
                toggleDrawer(GravityCompat.END)
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
                it?.let {
                    slotViews.isJoined = vm.mySlot != null

                    log("tableSlotsLD", "slots:/n" + Gson().toJson(it))
                    if (vm.mySlot != null) {

                        when (vm.mySlot!!.status) {
                            SeatStatus.SIT_OUT.status -> {
                                //   iAMBack.visibility = VISIBLE
                                handleIamBack()
                            }
                            SeatStatus.ACTIVE.status -> {
                                joinBBcb.visibility = GONE
                            }
                            SeatStatus.WAIT_FOR_BB.status -> {
                                //joinBBcb.visibility = VISIBLE
                                joinBBcb.isChecked = true
                            }
                            SeatStatus.WAIT_FOR_NEXT.status -> {
                                //joinBBcb.visibility = VISIBLE
                                joinBBcb.isChecked = false
                            }
                        }
                    }

                    vm.isWaitingForOthersShown = false
                    val activeSlotsCount =
                        vm.getFilteredSlotsCount(status = TableSlotStatus.ACTIVE.name)
                    val inactiveSlotsCount = vm.getInactiveSlotsCount()
                    val vacantSlotsCount =
                        vm.getFilteredSlotsCount(status = TableSlotStatus.VACANT.name)
                    val totalSlotsCount = vm.tableSlots!!.size
                    val playerCountSatOnTable = totalSlotsCount - vacantSlotsCount

                    if (vm.getFilteredSlotsCount(status = TableSlotStatus.VACANT.name) == totalSlotsCount) {
                        resetGameToDefault()
                        slotViews.resetGame()
                        waitingTv.visibility = GONE
                        iAMBack.visibility = GONE
                    } else if (vm.mySlot != null) {
                        /*if(activeSlotsCount == playerCountSatOnTable && allInSlotsCount == (playerCountSatOnTable - 1)){
                            bottomPannel_new.visibility = INVISIBLE
                        }*/
                        if ((vacantSlotsCount == totalSlotsCount - 1)) {
                            // if one player himself is active while others are inactive
                            resetGameToDefault()
                            displayMessage(getString(R.string.waiting_for_opponents))
                        } else if ((activeSlotsCount == 1 && inactiveSlotsCount == (playerCountSatOnTable - activeSlotsCount) &&
                                    vm.checkIfMySlotActive())
                        ) {
                            displayMessage(getString(R.string.waiting_for_opponents))
                        }
                    } else if (activeSlotsCount >= 2) {
                        vm.canDisplayWaitingIcon = true
                    } else if (activeSlotsCount == 0 && inactiveSlotsCount > 0 && vm.isFirstGameStarted) {
                        resetGameToDefault()
                    }
                    slotViews.checkCanRefillWallet(it)
                    slotViews.drawSlots(it)
                }
            })

            vm.inPlayAmtOnGameEnd.observe(this, Observer {
                slotViews.updateInPlayOnGameEnd(it)
            })
            vm.userDetailsLD.observe(this, Observer {
                it.hand_strength?.let { handStrength ->
                    vm.isHandStrengthEnabled = handStrength
                }
            })

            vm.isGameEnded.observe(this, Observer { isGameEnded ->
                if (isGameEnded) {
                    slotViews.resetGame()
                    isGameStartedAndRunning = false
                }
            })

            vm.gameTriggerLD.observe(this, Observer {
                log("poker::gameTriggerLD", it)
                resetAutoOptions()
                vm.currentAutoButton = null

                isGameStartedAndRunning = false
                it?.let {
                    if (gameIDTV != null/* && vm.isLandscape*/) {
                        gameIDTV.text = it.game_uid
                    } else if (portrait_gameIDTV != null) {
                        portrait_gameIDTV.text = it.game_uid
                    }
                    if (it.small_blind != null && it.big_blind != null) {
                        gameBlindInfo.text = "" + it.small_blind + " /" + it.big_blind + "  | "
                    }

//                gameIDTV.invalidate()

                    /*gamePreferencesFragment.dismissExitLobbyDialog()

                          gameIdTV.visibility = View.VISIBLE
                          gameIdTV.text = it.gameUID*/

                    slotViews.usersBestHand = null
                    isAmountRaisedViaAllInBtn = false
                    isAmountRaisedViaRaiseBar = false
                    slotViews.crownTo(-1)
                    slotViews.restartGame()
                    slotViews.resetGame()

                    mc1.highlightCard(canHighlight = false)
                    mc2.highlightCard(canHighlight = false)
                    c1.highlightCard(canHighlight = false)
                    c2.highlightCard(canHighlight = false)
                    c3.highlightCard(canHighlight = false)
                    c4.highlightCard(canHighlight = false)
                    c5.highlightCard(canHighlight = false)

                    slotViews.resetDealerIcon()
                    tv_toss_message.visibility = GONE

                    if (it.start_time > (System.currentTimeMillis() - timeDiffWithServer) || vm.gameCountdownTimeLeft != 0L) {
                        //leaderboardView.visibility = GONE

                        bottomPannel_new.visibility = INVISIBLE
                        user_best_hand.visibility = GONE
                        tv_rankOrder.visibility = GONE
                        community_cards_ll.visibility = INVISIBLE
                        user_cards_fl.visibility = INVISIBLE
                        totalPot.visibility = INVISIBLE
                        pot_split.visibility = INVISIBLE

//                exit.visibility = View.GONE
//                playArea.visibility = GONE


                        /*slotViews.showTime = 0L
                slotViews.stopTimers()*/

                        raiseLL.visibility = INVISIBLE

                        //afterSubmit.visibility = GONE
                        vm.resetGame()

                        waitingTv.visibility = VISIBLE
                        //gameStartsTimer.visibility = VISIBLE
                        iAMBack.visibility = GONE
//                gamePreferencesViewModel.exitVisibility.value = View.GONE

                        closeShow.visibility = GONE
                        blur.visibility = GONE

                        try {
                            tableDetailsTimer?.cancel()
                            gameTriggerTimer?.cancel()
                        } catch (ex: Exception) {
                        }
                        startGameCountdownTicker(it, vm.gameCountdownTimeLeft)
                    } else {
                        try {
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
                log("poker::userContestDetailsLD", it)
                it?.let {
                    onUserContestDetailsLDObserved(it)

                    /*if(vm.gameCountdownTimeLeft == 0L){
                        onUserContestDetailsLDObserved(it)
                    }*/
                }
            })

            vm.tossEnabledLD.observe(this, Observer { gameDetails ->
                if (vm.isGameStartEventReceived && gameDetails.toss_enabled) {
                    tv_toss_message.text = "${gameDetails.toss_won_player.user_name} won the toss and will start the hand"
                    tv_toss_message.visibility = VISIBLE
                    vibrate(this) {}

                    gameTriggerTimer?.stop()

                    val totalMillisUntilFinished = 4000L
                    tossTriggerTimer = object : CountDownTimer(totalMillisUntilFinished, 1000){
                        override fun onTick(p0: Long) {

                        }

                        override fun onFinish() {
                            vm.getUserCards()
                            slotViews.hideTossCards()
                            tv_toss_message.text = ""
                            tv_toss_message.visibility = GONE
                        }

                    }.start()
                }
                vm.isGameStartEventReceived = false
            })
            vm.gameDetailsLD.observe(this, Observer {
                it?.let {
                    isGameStartedAndRunning = true
                    onGameDetailsLdObserved(it)
                    /*if(vm.gameCountdownTimeLeft == 0L){
                        onGameDetailsLdObserved(it)
                    }*/
                }
            })

            vm.userBestHandLd.observe(this, Observer {
                it?.let {
                    onBestHandReveal(it)
                }
            })

            vm.dealCommunityCardsLD.observe(this, Observer { it ->
                if (it != null) {
                    if (/*!vm.isConfigurationChanged && */vm.isDealCommunityCardsEventReceived) {
                        val slots = vm.tableSlotsLD.value
                        resetAutoOptions()
                        handleAllIn()
                        log(
                            "poker::dealCommunityCardsLD",
                            "dealCommunityCardsLD : ${it.toString()}"
                        )
//            val potSplitPadding = dpToPx(15).toFloat()
                        val potSplitTopLandscape = dpToPx(40).toFloat()
                        val potSplitTopPortrait = playArea.height / 4

                        try {
                            slots?.forEach { slot ->
                                if (slot.user != null && slot.status == TableSlotStatus.ACTIVE.name) {
                                    val slotPosition = slotViews.getPositionBySeatNumber(
                                        slots.size,
                                        slot.seat_no
                                    )
                                    val slotView = slotViews.slotViews[slot.seat_no]

                                    log(
                                        "check123",
                                        "" + it.total_pot_value + " " + slot.user!!.current_round_invested
                                    )
                                    if (slot.user!!.current_round_invested != 0.0) {
                                        val coinsTv = TextView(this)
                                        rootLayout.addView(coinsTv)

                                        //Animation towards totalPot from betAmount
                                        coinsTv.apply {
                                            drawableLeft(R.drawable.coin_small)
                                            compoundDrawablePadding = dpToPx(3)
                                            x = slotPosition.x + slotPosition.raiseAmt.ml
                                            y = slotPosition.y + slotPosition.raiseAmt.mt
                                            text = String.format(
                                                "%.2f",
                                                slot.user!!.current_round_invested
                                            )

                                            animate().apply {
                                                x(totalPot.x + playArea.paddingTop + totalPot.width / 2)
                                                y(
                                                    if (vm.isLandscape) {
                                                        topMarginLandscape + potSplitTopLandscape
                                                    } else {
                                                        val y =
                                                            (totalPot.layoutParams as ConstraintLayout.LayoutParams).topMargin + totalPot.measuredHeight / 2 + totalPot.top
                                                        y.toFloat()
                                                    }
                                                )

                                                duration = 1000
                                                withEndAction {

                                                    try {
                                                        if (it.total_pot_value > 0.0) {
                                                            totalPot.visibility = VISIBLE
                                                            vm.isCommunityCardsOpened = true
                                                            /*totalPot.text =
                                                                                        "Total Pot: ₹${it.total_pot_value.toDecimalFormat()}"*/
                                                            updatePotAmount(it.total_pot_value.toDecimalFormat())

                                                            pot_split.removeAllViews()
                                                            it.side_pots
                                                                .filter { it.pot_value > 0 }
                                                                .filter { it.pot_type == PotType.GAME_POT.type }
                                                                .forEach {
                                                                    pot_split.addView(TextView(this@GameActivity).apply {
                                                                        text =
                                                                            "  ₹ ${it.pot_value.toDecimalFormat()}  "
                                                                    })
                                                                    log(
                                                                        "finalPotForDistribution0123",
                                                                        " " + it
                                                                    )
                                                                }
                                                            log(
                                                                "finalPotForDistribution01234",
                                                                it.side_pots.toString()
                                                            )

                                                            pot_split.visibility =
                                                                if (pot_split.childCount >= 2)
                                                                    VISIBLE else INVISIBLE
                                                        }
                                                    } catch (ex: Exception) {

                                                    }

                                                    rootLayout.removeView(coinsTv)
                                                }
                                                start()
                                            }
                                        }

                                        //Animation towards inplayAmt from betAmount
                                        val refundAmtSidePot = it.side_pots.filter { it.pot_type == PotType.REFUND.type }
                                        val targetSidePot = refundAmtSidePot.find { it.players[0] == slot.user_unique_id }
                                        if(targetSidePot!=null){
                                            val refundAmtTv = TextView(this)
                                            refundAmtTv.setBackgroundColor(Color.parseColor("#AD000000"))
                                            refundAmtTv.setTextColor(resources.getColor(R.color.green))
                                            refundAmtTv.type(BOLD)
                                            refundAmtTv.size(12f)
                                            rootLayout.addView(refundAmtTv)
                                            slotView?.let {
                                                refundAmtTv.apply {
                                                    x = slotPosition.x + slotPosition.raiseAmt.ml
                                                    y = slotPosition.y + slotPosition.raiseAmt.mt
                                                    text =  "Refund: ₹" + String.format(
                                                        "%.2f",
                                                        targetSidePot.pot_value
                                                    )
                                                    gravity =  slotPosition.raiseAmt.alignment
                                                    z = 100f
                                                    animate().apply {
                                                        x(slotView.x + slotView.width / 2)
                                                        y(slotView.y + slotView.height / 2)

                                                        duration = 2000
                                                        withEndAction {
                                                            rootLayout.removeView(refundAmtTv)
                                                        }
                                                        start()
                                                    }
                                                }

                                            }
                                        }
                                    }
                                }
                            }

                        }catch (e:java.lang.Exception){
                            e.printStackTrace()
                        }
                    }
                    vm.isDealCommunityCardsEventReceived = false
                    timeOut(1000) {
                        c1.coloredCard(it.card_1)
                        c2.coloredCard(it.card_2)
                        c3.coloredCard(it.card_3)
                        c4.coloredCard(it.card_4)
                        c5.coloredCard(it.card_5)

                        if (vm.isUserBestHandAvailable(it)) {
                            vm.getHandStrength()
                        }
                    }
                }
            })

            vm.refillInPlayAmount.observe(this, Observer { isRefill ->
                if (isRefill) {
                    vm.resetRefillInPlayAmount()
                    vm.mySlot?.let {
                        showBuyInAlert(true, vm.mySlot!!.seat_no)
                    }
                }
            })
            vm.playerTurnLD.observe(this, Observer {
                joinBBcb.visibility = GONE
                log("poker::playerTurnLD", "playerTurnLD : $it")

//            log("playerTurnLD", it)

                if (it == null) {
                    raiseLL.visibility = INVISIBLE
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
                        log("poker::userId", vm.userId)

                        if (preferencesvm.vibrate)
                            vibrate(this) {}

                        checkCB.visibility = GONE
                        checkOrCallAnyCB.visibility = GONE
                        fold_checkCB.visibility = GONE

                        //auto_options_rg.clearCheck()
                        //resetting the autoActions here
                        if (checkCB.isChecked) {
                            checkCB.isChecked = false
                        }
                        if (checkOrCallAnyCB.isChecked) {
                            checkOrCallAnyCB.isChecked = false
                        }
                        if (fold_checkCB.isChecked) {
                            fold_checkCB.isChecked = false
                        }

                        if (vm.mySlot?.user != null && vm.mySlot?.user!!.status.startsWith("AUTO_")) {
                            foldBtn.visibility = GONE
                            callLL.visibility = GONE
                            raiseBtnLL.visibility = GONE
                            checkBtn.visibility = GONE
                            //allinLL.visibility = GONE
                            allinBtnLL.visibility = GONE

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
                                    raiseLL.visibility = INVISIBLE
                                    GONE
                                }

                            checkBtn.visibility =
                                if (it.action_choices.contains(PlayerActions.CHECK.action)) VISIBLE else GONE

                            /*allinLL.visibility =
                                if (it.action_choices.contains(PlayerActions.ALL_IN.action)) VISIBLE else GONE*/


                            if (!it.action_choices.contains(PlayerActions.RAISE.action) && it.action_choices.contains(
                                    PlayerActions.ALL_IN.action
                                ) &&
                                it.action_choices.contains(PlayerActions.FOLD.action)
                            ) {
                                allinBtnLL.visibility = VISIBLE
                                allInValue.text = "₹${(it.allin_amount).toDecimalFormat()}"
                            } else {
                                allinBtn.visibility =
                                    if (it.action_choices.contains(PlayerActions.ALL_IN.action)) VISIBLE else GONE
                                allinBtnLL.visibility = GONE
                            }
                        }
                        callBtn.text = "₹${it.player_min_amount_to_call.toDecimalFormat()}"
                        callBtn.tag = it.player_min_amount_to_call


                        val maxPossibleRaise = it.current_max_raise
                        max_raise_value.text = maxPossibleRaise.toString()
                        val minPossibleRaise = it.current_min_raise

                        seek_raise.max = ((maxPossibleRaise - minPossibleRaise).toInt())

                        pot_raise.onOneClick {
                            isAmountRaisedViaAllInBtn = false
                            /*seek_raise.progress =
                                ((minPossibleRaise) * 100).toInt()*/
                            if (it.full_pot_value > it.game_inplay_amount) {
                                onSeekChangeBtnClicked(it.game_inplay_amount)
                            } else {
                                onSeekChangeBtnClicked(it.full_pot_value)
                            }
                        }
                        raise_3_4.onOneClick {
                            isAmountRaisedViaAllInBtn = false
//                                ((minPossibleRaise) * 300 / 4).toInt()
                            if (it.three_fourth_pot_value > it.game_inplay_amount) {
                                onSeekChangeBtnClicked(it.game_inplay_amount)
                            } else {
                                onSeekChangeBtnClicked(it.three_fourth_pot_value)
                            }
                        }
                        raise_1_2.onOneClick {
                            isAmountRaisedViaAllInBtn = false
                            // seek_raise.progress = ((minPossibleRaise) * 50).toInt()
                            if (it.half_pot_value > it.game_inplay_amount) {
                                onSeekChangeBtnClicked(it.game_inplay_amount)
                            } else {
                                onSeekChangeBtnClicked(it.half_pot_value)
                            }
                        }

                        min_raise.onOneClick {
                            isAmountRaisedViaAllInBtn = false
                            //seek_raise.progress = 0
                            onSeekChangeBtnClicked(it.current_min_raise)
                        }

                        allinBtn.onOneClick {
                            isAmountRaisedViaAllInBtn = true
                            // seek_raise.progress = seek_raise.max
                            onSeekChangeBtnClicked(it.allin_amount)
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
                                isAmountRaisedViaAllInBtn = progress == seek_raise.max

                                if (!isAmountRaisedViaRaiseBar) {
                                    raiseBtn.text =
                                        "₹${(progress /*/ 100.0*/ + minPossibleRaise).toDecimalFormat()}"
                                    raiseBtn.tag = (progress /*/ 100.0*/ + minPossibleRaise)
                                } else {
                                    isAmountRaisedViaRaiseBar = false  //resetting the value
                                }
                            }

                            override fun onStartTrackingTouch(seekBar: SeekBar?) {

                            }

                            override fun onStopTrackingTouch(seekBar: SeekBar?) {

                            }
                        })
                        seek_raise.progress = 0

                        raiseBtn.text =
                            "₹${(it.current_min_raise).toDecimalFormat()}"

                        /*allinBtn.text =
                            "₹${(it.allin_amount).toDecimalFormat()}"*/
                        raiseBtn.tag = minPossibleRaise
                    } else {
                        raiseLL.visibility = INVISIBLE

                        if (vm.me != null) {
                            when {
                                vm.me!!.status == PlayerActions.ALL_IN.action || vm.me!!.status == PlayerActions.FOLD.action -> {
                                    fold_checkCB.visibility = GONE
                                    checkCB.visibility = GONE
                                    checkOrCallAnyCB.visibility = GONE
                                    raiseBtnLL.visibility = INVISIBLE
                                    bottomPannel_new.visibility = INVISIBLE
                                }
                                vm.me!!.amount_invested == it.game_max_bet_amount -> {
                                    fold_checkCB.visibility = VISIBLE
                                    checkCB.visibility = VISIBLE
                                    checkOrCallAnyCB.visibility = VISIBLE

                                    raiseBtnLL.visibility = INVISIBLE
                                    bottomPannel_new.visibility = VISIBLE

                                    restoreAutoOptions()

                                }
                                else -> {
                                    fold_checkCB.visibility = VISIBLE
                                    checkCB.visibility = VISIBLE
                                    checkOrCallAnyCB.visibility = VISIBLE
                                    raiseBtnLL.visibility = INVISIBLE
                                    bottomPannel_new.visibility = VISIBLE

                                    restoreAutoOptions()
                                }
                            }
                        }

                        foldBtn.visibility = GONE
                        callLL.visibility = GONE
                        raiseBtnLL.visibility = GONE
                        checkBtn.visibility = GONE
//                        allinLL.visibility = GONE
                        allinBtn.visibility = GONE
                        allinBtnLL.visibility = GONE
                    }

                    slotViews.playerTurn = it
                }
            })

            vm.enableActions.observe(this, Observer {
                disableEnableActions(it)
            })

            vm.leaderboardLD.observe(this, Observer { leaderboard ->
                if (leaderboard == null)
                    return@Observer

                if(vm.isLeaderBoardEventReceived){
                    var isPotAnimationDone = true

                    log("poker::leaderboardLD", leaderboard)
                    bottomPannel_new.visibility = INVISIBLE

                    if (leaderboard.cards_reveal) {
                        c5.coloredCard(leaderboard.community_cards.card_5)
                    }
                    //slotViews.resetPlayers()
                    raiseLL.visibility = INVISIBLE


//                pot_split.removeAllViews()
//                if(leaderboard.pot_distributions.size >= 2){
                    val splitPot = leaderboard.pot_distributions
                        .filter { leaderboard.pot_distributions.size >= 2 }
                        .filter { it.pot_type == PotType.GAME_POT.type }
                        .filter { it.pot_value > 0 }
                        .reversed()

                    if (splitPot.size >= 2) {
                        pot_split.removeAllViews()
                        splitPot.forEach {
                            val textView = TextView(this@GameActivity)
                            pot_split.addView(textView.apply {
                                text =
                                    "  ₹ ${it.pot_value.toDecimalFormat()}  "
                            })
                            pot_split.visibility = VISIBLE
                        }
                    } else {
                        pot_split.visibility = INVISIBLE
                    }
                    val potSplitTop = dpToPx(50).toFloat() + totalPot.height
                    val playAreaPadding = playArea.paddingStart
                    val potSplitPadding = dpToPx(15).toFloat()

                    val winningPots =
                        leaderboard.pot_winnings.filter { it.wonAmt > 0 }.sortedBy { it.rank }

                    var finalPotForDistribution = mutableListOf<PotWinnerDistribution>()

                    val refundPlayersList = leaderboard.pot_refunds.filter { it.wonAmt > 0 }
                    if (refundPlayersList.isNotEmpty())
                        slotViews.displayRefundAmtWithAnimation(refundPlayersList)

                    winningPots.forEach { pair ->
                        finalPotForDistribution.add(
                            PotWinnerDistribution(
                                pair.user_unique_id,
                                pair.wonAmt.toDouble(),
                                PotDistributionType.WINNER
                            )
                        )
                    }


                    fun animatePots(potIndex: Int) {
                        log("poker::animatePots1", "" + finalPotForDistribution.size + " " + potIndex)

                        if (finalPotForDistribution != null && finalPotForDistribution.size > potIndex) {
                            isPotAnimationDone = false
                            slotViews.crownTo(-1)

//                        val c1 = pot_split.getChildAt(potIndex) ?: return
                            log("poker::animatePots2", "pot_split.getChildAt(0) b4")

                            val c1 = pot_split.getChildAt(0) ?: return
                            log("poker::animatePots3", "pot_split.getChildAt(0) after")

                            val c = c1 as TextView

                            val currentPotView = TextView(this)
                            rootLayout.addView(currentPotView)

                            /*if(pot_split.childCount == 1){
                                currentPotView.visibility = INVISIBLE
                            }*/
                            val potSplitTopPortrait = playArea.height / 4

                            currentPotView.apply {
                                text = c.text
                                setBackgroundColor(Color.parseColor("#AD000000"))
                                x = pot_split.x + c.x + playAreaPadding + potSplitPadding
                                y = if (vm.isLandscape) {
                                    topMarginLandscape + potSplitTop
                                } else {
                                    topMarginPortrait + potSplitTopPortrait
                                }
                                z = 100f
                            }


                            slotViews.resetPlayers()
                            if (leaderboard.cards_reveal) {
                                slotViews.usersBestHand = leaderboard.users_best_hand
                                //slotViews.resetPlayers()
                                slotViews.displayLeaderBoard()
                            }

                            val potDistribution = finalPotForDistribution[potIndex]
                            log("poker::animatePots4", "potRefunds meaya2 niche : " + potDistribution)

                            vm.tableSlotsLD.value?.let { slots ->
                                slots.find { it.user_unique_id == potDistribution.user_unique_id }
                                    ?.let {
                                        slotViews.slotViews[it.seat_no]?.let { slot ->
                                            //c.visibility = GONE
                                            log(
                                                "poker::animatePots5",
                                                potDistribution.type == PotDistributionType.WINNER
                                            )

                                            if (potDistribution.type == PotDistributionType.WINNER) {
                                                showLeaderBoard(potDistribution, leaderboard)
                                            }

                                            currentPotView.animate().apply {
                                                x(slot.x + slot.width / 2)
                                                y(slot.y + slot.height / 2)
                                                duration = 1000
                                                withEndAction {
                                                    pot_split.removeView(c1)
                                                    rootLayout.removeView(currentPotView)
//                                            val updatedInPlayAmt = it.inplay_amount + potWinning.wonAmt
//                                            slot.in_play_amt.text = "₹${updatedInPlayAmt?.toDecimalFormat()}"
                                                    animatePots(potIndex + 1)
                                                }
                                                start()
                                            }
                                        }
                                    }
                            }
                        } else {
                            isPotAnimationDone = true
                            if (pot_split.visibility == VISIBLE && pot_split.childCount == 0) {
                                pot_split.visibility = INVISIBLE
                            }
                        }
                    }

                    if (isPotAnimationDone) {
                        animatePots(0)
                    }
                }
                vm.isLeaderBoardEventReceived = false
            })

            foldBtn.onOneClick {
                isAmountRaisedViaAllInBtn = false
                disableEnableActions(false)
                vm.actionEvent(ActionEvent.FOLD) {
                    runOnMain {
                        disableEnableActions(true)

                        if (it!!["success"].bool) {
                            slotViews.resetGame()
                            /*mc1.setImageResource(R.drawable.deck_card)
                            mc2.setImageResource(R.drawable.deck_card)*/
                            raiseLL.visibility = INVISIBLE
                            bottomPannel_new.visibility = INVISIBLE

                            if (vm.mySlot != null) {
                                vm.mySlot!!.seat_no.apply {
                                    slotViews.animateSelfView(
                                        PlayerActions.FOLD,
                                        vm.mySlot!!.seat_no
                                    )
                                }
                            }
                        } else {
                            showSnack(it["description"].string)
                        }
                    }
                }
            }

            callLL.onOneClick {
                disableEnableActions(false)
                vm.actionEvent(ActionEvent.CALL_BET, total_amount = callBtn.tag as Double) {
                    disableEnableActions(true)
                }
            }
            checkBtn.onOneClick {
                isAmountRaisedViaAllInBtn = false
                disableEnableActions(false)
                vm.actionEvent(ActionEvent.CHECK_BET, total_amount = callBtn.tag as Double) {
                    disableEnableActions(true)
                }
            }

//            allinBtn.onOneClick {
//                allInClicked()
//            }

            allinBtnLL.onOneClick {
                goForAllIn()
            }

            raiseBtnLL.onOneClick {
                disableEnableActions(false)
                if (isAmountRaisedViaAllInBtn) {
                    goForAllIn()
                    isAmountRaisedViaAllInBtn = false
                } else {
                    vm.actionEvent(
                        ActionEvent.RAISE_BET,
                        total_amount = raiseBtn.tag as Double,
                        raise_amount = raiseBtn.tag as Double - callBtn.tag as Double
                    ) {
                        disableEnableActions(true)
                    }
                }
            }

            sitOutCB.onOneClick {
                vm.updateSeatStatus(if (sitOutCB.isChecked) SeatStatus.SIT_OUT_NEXT else SeatStatus.ACTIVE) {
                    runOnMain {
                        if (it!!["success"].bool) {
                            /*iAMBack.visibility = VISIBLE
                        bottomPannel_new.visibility = GONE*/
                        } else {
                            sitOutCB.isChecked = !sitOutCB.isChecked
                            showSnack(it["description"].string)
                        }
                    }
                }
            }

            iAMBack.onOneClick {
                val slots = vm.tableSlotsLD.value

                slots?.forEach {
                    if (it.user_unique_id == vm.userId && it.inplay_amount == 0.0) {
                        showBuyInAlert(true, it.seat_no)
                        return@onOneClick
                    }
                }
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
                    }
                }
            }

            vm.iamBackLD.observe(this, Observer {
                if (vm.mySlot != null && vm.mySlot!!.status == SeatStatus.SIT_OUT.status) {
                    if (it) {
                        handleIamBack()
                    } else {
                        iAMBack.visibility = GONE
                        bottomPannel_new.visibility = VISIBLE
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

            fun setCurrentAutoActionView(actionView: CheckBox) {
                if (actionView.isChecked) {
                    vm.currentAutoButton = actionView
                } else {
                    vm.currentAutoButton = null
                }
            }
            fold_checkCB.onOneClick {
                setCurrentAutoActionView(fold_checkCB)
                checkCB.isChecked = false
                checkOrCallAnyCB.isChecked = false

                vm.autoGameAction(AutoGameAction.AUTO_FOLD_CHECK, fold_checkCB.isChecked) {}
            }
            checkCB.onOneClick {
                setCurrentAutoActionView(checkCB)
                fold_checkCB.isChecked = false
                checkOrCallAnyCB.isChecked = false

                vm.autoGameAction(AutoGameAction.AUTO_CHECK, checkCB.isChecked) {}
            }
            checkOrCallAnyCB.onOneClick {
                setCurrentAutoActionView(checkOrCallAnyCB)
                fold_checkCB.isChecked = false
                checkCB.isChecked = false

                vm.autoGameAction(AutoGameAction.AUTO_CALLANY_CHECK, checkOrCallAnyCB.isChecked) {}
            }
        }
    }

    private fun configureOrientation(autorotate: Boolean = false) {
        requestedOrientation = if (autorotate) {
            SCREEN_ORIENTATION_FULL_SENSOR
        } else {
            //requestedOrientation = SCREEN_ORIENTATION_SENSOR_LANDSCAPE

            SCREEN_ORIENTATION_LANDSCAPE
        }
    }

    private fun restoreAutoOptions() {
        if (!vm.isPlayerEventReceived) {
            vm.currentAutoButton?.let {
                when (it.id) {
                    fold_checkCB.id -> fold_checkCB.isChecked = true
                    checkCB.id -> checkCB.isChecked = true
                    checkOrCallAnyCB.id -> checkOrCallAnyCB.isChecked = true
                }
            }
        } else {
            vm.currentAutoButton = null //playerturn event received
        }
        vm.isPlayerEventReceived = false
    }

    private fun handleIamBack() {
        sitOutCB.visibility = INVISIBLE
        iAMBack.visibility = VISIBLE
        user_cards_fl.visibility = INVISIBLE
        waitingTv.visibility = INVISIBLE
        bottomPannel_new.visibility = INVISIBLE
        //totalPot.visibility = INVISIBLE
    }

    private fun goForAllIn() {
        disableEnableActions(false)
        vm.actionEvent(ActionEvent.ALL_IN, total_amount = callBtn.tag as Double) {
            disableEnableActions(true)
            runOnMain {
                bottomPannel_new.visibility = INVISIBLE
                if (it!!["success"].bool) {
                    slotViews.resetGame()
                }
            }
        }
    }

    private fun displayMessage(message: String) {
        // <--this is because gameTrigger event will not be triggered in this case
        vm.isWaitingForOthersShown = true
        waitingTv.visibility = VISIBLE
        waitingTv.text = message
        iAMBack.visibility = GONE
        tv_rankOrder.visibility = GONE
        user_best_hand.visibility = GONE
        user_cards_fl.visibility = INVISIBLE
    }

    private fun onBestHandReveal(handStrength: UserBestHand) {
        if (vm.isHandStrengthEnabled != null && vm.isHandStrengthEnabled == false) {
            tv_rankOrder.visibility = GONE
            user_best_hand.visibility = GONE
            return
        }
        val userBestHand = handStrength.handDetails
        tv_rankOrder.visibility = VISIBLE
        user_best_hand.visibility = VISIBLE
        try {
            tv_rankOrder.text = userBestHand.rankOrder
            hand_c1.coloredCard(userBestHand.cards.get(0))
            hand_c2.coloredCard(userBestHand.cards.get(1))
            hand_c3.coloredCard(userBestHand.cards.get(2))
            hand_c4.coloredCard(userBestHand.cards.get(3))
            hand_c5.coloredCard(userBestHand.cards.get(4))
        } catch (e: java.lang.Exception) {

        }
    }

    private fun resetGameToDefault() {
        isGameStartedAndRunning = false
        slotViews.crownTo(-1)
        slotViews.resetGame()
        slotViews.restartGame()

        user_best_hand.visibility = GONE
        tv_rankOrder.visibility = GONE

        if (vm.isFirstGameStarted) {  //this condition is used reset the resources and to omit the case  when VACANT players count are "initially" equal to total size
            vm.resetVmResources()
            vm.gameCountdownTimeLeft = 0L
        }

        vm.canDisplayWaitingIcon = false
        slotViews.resetDealerIcon()

        bottomPannel_new.visibility = INVISIBLE
        community_cards_ll.visibility = INVISIBLE
        user_cards_fl.visibility = INVISIBLE
        totalPot.visibility = INVISIBLE
        pot_split.visibility = INVISIBLE
        raiseLL.visibility = INVISIBLE
        //iAMBack.visibility = GONE
    }

    private fun resetAutoOptions() {
        fold_checkCB.isChecked = false
        checkCB.isChecked = false
        checkOrCallAnyCB.isChecked = false
    }

    private fun onUserContestDetailsLDObserved(userContentDetails: GameModel.UserContestDetails) {
        user_cards_fl.visibility = VISIBLE
        mc1.coloredCard(userContentDetails.card_1)
        mc2.coloredCard(userContentDetails.card_2)
    }

    private fun onGameDetailsLdObserved(it: GameModel.GameDetails) {
        disableEnableActions(true)
        log("poker::gameDetailsLD", it)
        slotViews.dealerPosition = it.dealer_position

        gameTriggerTimer?.stop()
        val slots = vm.tableSlotsLD.value
        if (slots != null && slots.count { !it.user_unique_id.isNullOrEmpty() } >= 2) {
//                    sitOutCB.visibility = VISIBLE
        } else {
            sitOutCB.visibility = GONE
        }
        bottomPannel_new.visibility = VISIBLE

        if (it.total_pot_value > 0.0 && it.community_cards?.size != 0) {
            updatePotAmount(it.total_pot_value.toDecimalFormat())
            totalPot.visibility = VISIBLE
        } else {
            totalPot.visibility = INVISIBLE
        }
        community_cards_ll.visibility = VISIBLE

        c1.coloredCard(it.card_1)
        c2.coloredCard(it.card_2)
        c3.coloredCard(it.card_3)
        c4.coloredCard(it.card_4)
        c5.coloredCard(it.card_5)
    }

    private fun startGameCountdownTicker(
        gameDetails: GameModel.GameDetails,
        gameCountdownTimeLeft: Long = 0L
    ) {

        val gameStartCountdownTime = if (gameCountdownTimeLeft == 0L) {
            gameDetails.start_time - (System.currentTimeMillis() - timeDiffWithServer)
        } else {
            gameCountdownTimeLeft
        }
        gameTriggerTimer = object :
            CustomCountDownTimer(
                gameStartCountdownTime,
                1000
            ) {
            override fun onTick(millisUntilFinished: Long) {
                log("vm.gameCountdownTimeLeft4", millisUntilFinished)
                vm.gameCountdownTimeLeft = millisUntilFinished //in millis
                waitingTv.text =
                    "Game starts in ${millisUntilFinished / 1000} seconds..."
            }

            override fun onStop() {
                //gameStartsTimer.visibility = GONE
                waitingTv.visibility = GONE
            }

            override fun onFinish() {
                vm.gameCountdownTimeLeft = 0L
                //gameStartsTimer.visibility = GONE
                waitingTv.visibility = GONE
//
//                if(community_cards_ll.visibility != VISIBLE){
//                    vm.gameDetailsLD.value?.let {
//                        onGameDetailsLdObserved(vm.gameDetailsLD.value!!)
//                    }
//                    vm.userContestDetailsLD.value?.let {
//                        onUserContestDetailsLDObserved(vm.userContestDetailsLD.value!!)
//                    }
//                }
            }
        }
        gameTriggerTimer!!.start()
    }

    fun showBuyInAlert(isRefillAmt: Boolean, seatNo: Int) {
        if (plan.mode == "REAL") {
            walletVM.getRealAmount { byIn(isRefillAmt, it, seatNo, true) }
        } else {
            walletVM.getPlayAmount { byIn(isRefillAmt, it, seatNo, false) }
        }
    }

    private fun onSeekChangeBtnClicked(newAmount: Double) {
        isAmountRaisedViaRaiseBar = true
        seek_raise.progress = newAmount.toInt()
        raiseBtn.text = "₹${(newAmount).toDecimalFormat()}"
        raiseBtn.tag = newAmount
    }

    private fun updatePotAmount(amount: String? = vm.totalPotAmount) {
        if (amount != null) {
            totalPot.text =
                "Total Pot: ₹${amount}"
            vm.totalPotAmount = amount
        }
    }

    private fun disableEnableActions(isEnabled: Boolean) {
        runOnMain {
            foldBtn.isEnabled = isEnabled
            checkBtn.isEnabled = isEnabled
            callBtn.isEnabled = isEnabled
            raiseBtn.isEnabled = isEnabled
            allinBtn.isEnabled = isEnabled
            allinBtnLL.isEnabled = isEnabled
        }
    }

    private fun toggleDrawer(gravity: Int) {
        if (drawer_layout.isDrawerOpen(gravity)) {
            drawer_layout.closeDrawer(gravity)
        } else {
            drawer_layout.openDrawer(gravity)
        }
    }

    private fun byIn(isRefillAmt: Boolean, balance: Double, seatNo: Int, isCash: Boolean) {
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

        fun refillAmount() {
            val amt = dialogView.seek.progress + plan.min_buyin
            if (!vm.refillNextInPlayAmount) {
                vm.refillInPlayAmount(amt) {
                    runOnMain {
                        onBackPressed()
                        if (it!!["success"].bool) {
                            dialog.dismiss()
                            slotViews.isRefillPopupVisible = false
                            if (iAMBack.visibility == VISIBLE) {
                                iAMBack.visibility = GONE
                            }
                        } else {
                            showSnack(it["description"].string)
                        }
                    }
                }
            } else {
                vm.refillNextInPlayAmount(amt) {
                    runOnMain {
                        onBackPressed()
                        if (it!!["success"].bool) {
                            vm.refillNextInPlayAmount = false
                            slotViews.isRefillPopupVisible = false
                            dialog.dismiss()
                        } else {
                            showSnack(it["description"].string)
                        }
                    }
                }
            }
        }

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
                    if (isRefillAmt) {
                        dialogView.join.text = "Re-fill"
                    } else {
                        dialogView.join.text = "Add Money"
                    }

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

                                                if (isRefillAmt) {
                                                    dialogView.join.text = "Re-fill"
                                                } else {
                                                    dialogView.join.text =
                                                        "Join Table"
                                                }
                                                dialogView.join.onOneClick {
                                                    when (dialogView.join.text) {
                                                        "Join Table" -> joinNow()
                                                        "Re-fill" -> refillAmount()
                                                    }
                                                }
                                            }
                                        }
                                    }
                                })
                        }
                    }
                } else {
                    if (isRefillAmt) {
                        dialogView.join.text = "Re-fill"
                    } else {
                        dialogView.join.text = "Add Coins"
                    }
                    dialogView.join.drawableLeft(R.drawable.coin)
                    dialogView.join.onOneClick {
                        walletVM.addChips {
                            dialogView.insufficient.visibility = GONE

                            walletVM._playWalletDetailsResponse.value =
                                it.info["totalChips"].double

                            dialogView.availableBal.text =
                                (availableBalance + 5000).toDecimalFormat()

                            dialogView.join.drawableLeft(0)

                            if (isRefillAmt) {
                                dialogView.join.text = "Re-fill"
                            } else {
                                dialogView.join.text = "Join Table"
                            }

                            dialogView.join.onOneClick {
                                when (dialogView.join.text) {
                                    "Join Table" -> joinNow()
                                    "Re-fill" -> refillAmount()
                                }
                            }
                        }
                    }
                }
            } else {
                dialogView.insufficient.visibility = GONE
                if (isRefillAmt) {
                    dialogView.join.text = "Re-fill"
                } else {
                    dialogView.join.text = "Join Table"
                }
                dialogView.join.onOneClick {
                    when (dialogView.join.text) {
                        "Join Table" -> joinNow()
                        "Re-fill" -> refillAmount()
                    }
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

        dialogView.close.onOneClick {
            if (isRefillAmt) {
                //exitTOLobby()
                vm.updateSeatStatus(SeatStatus.SIT_OUT) {
                    runOnMain {
                        if (it!!["success"].bool) {
                            waitingTv.visibility = GONE
                            iAMBack.visibility = VISIBLE
                            bottomPannel_new.visibility = INVISIBLE
                        } else {
                            sitOutCB.isChecked = !sitOutCB.isChecked
                            showSnack(it["description"].string)
                        }
                    }
                }
            }
            dialog.dismiss()
        }

        dialog.show()
        if (slotViews.isLandscape) {
            dialog.window?.setLayout(dpToPx(375), WindowManager.LayoutParams.WRAP_CONTENT)
        }
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
                            //joinBBcb.visibility = VISIBLE

                            joinBBcb.isChecked = !dialogView.join_from_next.isChecked

                            if (dialogView.join_from_next.isChecked && dialogView.makeDefault.isChecked) {
                                vm.updateUserGameSettings(jsonObject("auto_next_game" to true))
                            }

                            sitOutCB.isChecked = false
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
                socketInstance.disConnect()
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

    override fun onStop() {
        super.onStop()
        removeSocketChangeListeners()
    }

    private fun removeSocketChangeListeners() {
        socketInstance.listeners?.let {
            if (socketInstance.listeners.size != 0) {
                socketInstance.removeSocketChangeListener(this)
            }
        }
    }

    private fun handleAllIn() {
        val activeSlotsCount = vm.getFilteredSlotsCount(status = TableSlotStatus.ACTIVE.name)
        val vacantSlotsCount = vm.getFilteredSlotsCount(status = TableSlotStatus.VACANT.name)
        val totalSlotsCount = vm.tableSlots!!.size
        val playerCountSatOnTable = totalSlotsCount - vacantSlotsCount
        val allInSlotsCount = vm.getFilteredSlotsCount(
            PlayerActions.ALL_IN.name,
            SlotsFilter.PLAYER_ACTION
        )

        if (activeSlotsCount == playerCountSatOnTable && allInSlotsCount == (playerCountSatOnTable - 1)) {
            bottomPannel_new.visibility = INVISIBLE
        }
    }

    private fun showLeaderBoard(
        potDistribution: PotWinnerDistribution,
        leaderBoard: Leaderboard
    ) {
        leaderBoard.users_best_hand
            .find { it.user_unique_id == potDistribution.user_unique_id }
            ?.let {
                val cards = it.best_hand_details.cards

                slotViews.crownTo(
                    it.seat_no,
                    if (leaderBoard.cards_reveal)
                        listOf(
                            !cards.contains(it.card_1),
                            !cards.contains(it.card_2)
                        )
                    else
                        listOf(false, false)
                )

                if (leaderBoard.cards_reveal) {
                    if (it.user_unique_id == vm.userId) {
                        mc1.highlightCard(canHighlight = !cards.contains(it.card_1))
                        mc2.highlightCard(canHighlight = !cards.contains(it.card_2))
                    }
                    c1.highlightCard(canHighlight = !cards.contains(leaderBoard.community_cards.card_1))
                    c2.highlightCard(canHighlight = !cards.contains(leaderBoard.community_cards.card_2))
                    c3.highlightCard(canHighlight = !cards.contains(leaderBoard.community_cards.card_3))
                    c4.highlightCard(canHighlight = !cards.contains(leaderBoard.community_cards.card_4))
                    c5.highlightCard(canHighlight = !cards.contains(leaderBoard.community_cards.card_5))
                }
            }
    }
}