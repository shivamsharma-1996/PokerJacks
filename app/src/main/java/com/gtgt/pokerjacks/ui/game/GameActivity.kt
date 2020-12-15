package com.gtgt.pokerjacks.ui.game

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.os.ResultReceiver
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.widget.SeekBar
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import com.devs.vectorchildfinder.VectorChildFinder
import com.github.salomonbrys.kotson.*
import com.gtgt.pokerjacks.InsufficientBalanceActivity
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.FullScreenScreenOnActivity
import com.gtgt.pokerjacks.extensions.*
import com.gtgt.pokerjacks.socket.SocketIoInstance
import com.gtgt.pokerjacks.socket.socketInstance
import com.gtgt.pokerjacks.ui.game.view.GamePreferencesFragment
import com.gtgt.pokerjacks.ui.game.view.SelectThemesFragment
import com.gtgt.pokerjacks.ui.game.view.slot.SlotViews
import com.gtgt.pokerjacks.ui.game.viewModel.*
import com.gtgt.pokerjacks.ui.lobby.model.LobbyTables
import com.gtgt.pokerjacks.ui.wallet.wallet.WalletViewModel
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
        offlineMsg.visibility = GONE
        vm.tableId = tableId
    }

    override fun reconnected() {
        offlineMsg.visibility = GONE
        vm.tableId = tableId
    }

    override fun connectionUnavailable() {
        offlineMsg.visibility = VISIBLE
    }

    override fun networkSpeed(speed: Int) {
    }

    private lateinit var slotViews: SlotViews
    private val vm: GameViewModel by viewModel()

    private val tableId by lazy { intent.getStringExtra("table_id") }
    private val plan by lazy { intent.getParcelableExtra<LobbyTables.PlanDetails>("plan")!! }
    private val walletVM: WalletViewModel by store()

    private val themesViewModel: ThemesViewModel by viewModel()
    private val gamePreferencesFragment by lazy { GamePreferencesFragment() }


    private var tableDetailsTimer: CountDownTimer? = null
    private var gameTriggerTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        mActivityTopLevelView = drawer_layout

        socketInstance.connect()
        socketInstance.addSocketChangeListener(this)

        themeSelectFragment.z = 11f
//        raiseLL.z = 11f

        c5.onRendered {
            slotViews = SlotViews(rootLayout) { seatNo ->
                if (plan.mode == "REAL") {
                    walletVM.getRealAmount { byIn(it, seatNo, true) }
                } else {
                    walletVM.getPlayAmount { byIn(it, seatNo, false) }
                }
            }

            slotViews.totalSlots = plan.max_players

            val cWidth = it.width / 1.5f
            mc1.widthHeightRaw(cWidth)
            mc2.widthHeightRaw(cWidth)
            mc2.marginsRaw(left = (cWidth / 2).toInt())
        }

        replaceFragment(gamePreferencesFragment, R.id.settingsFragment)
        replaceFragment(SelectThemesFragment(), R.id.themeSelectFragment)

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
                ivTable.loadImage(theme.table)
                gameInfoIv.imageTintList = ColorStateList.valueOf(theme.dark)

                foldFL.background = theme.textDrawable
                fold_checkFL.background = theme.textDrawable
                checkFL.background = theme.textDrawable
                callFL.background = theme.textDrawable
                checkOrCallAnyFL.background = theme.textDrawable
                callAnyFL.background = theme.textDrawable

                menuVector.changePathStrokeColor("stroke", theme.btn2)
                menu.invalidate()

                settingsVector.changePathStrokeColor("stroke", theme.btn2)
                settings.invalidate()

                statsVector.changePathStrokeColor("stroke", theme.btn2)
                stats.invalidate()

                previousVector.changePathStrokeColor("stroke", theme.btn2)
                previous.invalidate()

            }
        })

        vm.tableSlotsLD.observe(this, Observer {
            slotViews.isJoined = vm.mySlot != null
            slotViews.drawSlots(it)
        })

        vm.gameTriggerLD.observe(this, Observer {
            it?.let {
                /*gamePreferencesFragment.dismissExitLobbyDialog()

            gameIdTV.visibility = View.VISIBLE
            gameIdTV.text = it.gameUID*/
                messageFL.visibility = VISIBLE

                if (it.start_time > (System.currentTimeMillis() - timeDiffWithServer)) {
                    leaderboardView.visibility = GONE

                    bottomPannel.visibility = INVISIBLE
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
                        CountDownTimer(
                            it.start_time - (System.currentTimeMillis() - timeDiffWithServer),
                            1000
                        ) {
                        override fun onTick(millisUntilFinished: Long) {
                            gameStartsTimer.text =
                                "Game starts in ${millisUntilFinished / 1000} seconds..."
                        }

                        override fun onFinish() {
                            messageFL.visibility = GONE
                        }
                    }.start()
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
                mc1.setImageResource(Card.getResource(it.card_1))
                mc2.setImageResource(Card.getResource(it.card_2))
            }
        })

        vm.gameDetailsLD.observe(this, Observer {
            it?.let {
                bottomPannel.visibility = VISIBLE
                community_cards_ll.visibility = VISIBLE

                c1.setImageResource(Card.getResource(it.card_1))
                c2.setImageResource(Card.getResource(it.card_2))
                c3.setImageResource(Card.getResource(it.card_3))
                c4.setImageResource(Card.getResource(it.card_4))
                c5.setImageResource(Card.getResource(it.card_5))
            }
        })

        vm.dealCommunityCardsLD.observe(this, Observer {
            c1.setImageResource(Card.getResource(it.card_1))
            c2.setImageResource(Card.getResource(it.card_2))
            c3.setImageResource(Card.getResource(it.card_3))
            c4.setImageResource(Card.getResource(it.card_4))
            c5.setImageResource(Card.getResource(it.card_5))
        })

        vm.playerTurnLD.observe(this, Observer {
            if (it == null) {
                raiseLL.visibility = GONE
            } else {
                totalPot.visibility = VISIBLE
                totalPot.text = "Total Pot: ₹${it.total_pot_value.toDecimalFormat()}"

                if (it.side_pots.size >= 2) {
                    pot_split.visibility = VISIBLE
                    pot_split.text = it.side_pots.joinToString(" ") { "₹" + it.pot_value }
                }

                if (it.player_turn == vm.userId) {
                    foldFL.visibility = GONE
                    checkFL.visibility = GONE
                    checkOrCallAnyFL.visibility = GONE
                    fold_checkFL.visibility = GONE
                    callFL.visibility = GONE
                    callAnyFL.visibility = GONE

                    foldBtn.visibility =
                        if (it.action_choices.contains(PlayerActions.FOLD.action)) VISIBLE else GONE

                    callBtn.visibility =
                        if (it.action_choices.contains(PlayerActions.CALL.action)) VISIBLE else GONE

                    raiseBtn.visibility =
                        if (it.action_choices.contains(PlayerActions.RAISE.action)) {
                            raiseLL.visibility = VISIBLE
                            VISIBLE
                        } else {
                            raiseLL.visibility = GONE
                            GONE
                        }

                    checkBtn.visibility =
                        if (it.action_choices.contains(PlayerActions.CHECK.action)) VISIBLE else GONE

                    allinBtn.visibility =
                        if (it.action_choices.contains(PlayerActions.ALL_IN.action)) VISIBLE else GONE

                    callBtn.text = "Call\n₹${it.player_min_amount_to_call.toDecimalFormat()}"
                    callBtn.tag = it.player_min_amount_to_call
                    raiseBtn.text =
                        "Raise\n₹${(it.player_min_amount_to_call + it.current_min_raise).toDecimalFormat()}"
                    raiseBtn.tag = (it.player_min_amount_to_call + it.current_min_raise)

                    val maxPossibleRaise =
                        if (it.game_inplay_amount < it.total_pot_value) {
//                            pot_raise.visibility = GONE
//                            max_raise.visibility = VISIBLE
                            it.total_pot_value
                        } else {
//                            pot_raise.visibility = GONE
//                            max_raise.visibility = VISIBLE
                            it.game_inplay_amount
                        }
                    max_raise_value.text = maxPossibleRaise.toString()
                    val minPossibleRaise = it.player_min_amount_to_call + it.current_min_raise

                    seek_raise.max = ((maxPossibleRaise - minPossibleRaise) * 100).toInt()

                    pot_raise.onOneClick {
                        seek_raise.progress =
                            ((it.total_pot_value - minPossibleRaise) * 100).toInt()
                    }
                    raise_3_4.onOneClick {
                        seek_raise.progress =
                            ((it.total_pot_value - minPossibleRaise) * 300 / 4).toInt()
                    }
                    raise_1_2.onOneClick {
                        seek_raise.progress = ((it.total_pot_value - minPossibleRaise) * 50).toInt()
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

                    seek_raise.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(
                            seekBar: SeekBar?,
                            progress: Int,
                            fromUser: Boolean
                        ) {
                            raiseBtn.text =
                                "Raise\n₹${(progress / 100.0 + minPossibleRaise).toDecimalFormat()}"
                            raiseBtn.tag = (progress / 100.0 + minPossibleRaise)
                        }

                        override fun onStartTrackingTouch(seekBar: SeekBar?) {

                        }

                        override fun onStopTrackingTouch(seekBar: SeekBar?) {

                        }
                    })

                } else {
                    raiseLL.visibility = GONE

                    if (vm.me != null) {
                        when {
                            vm.me!!.status == PlayerActions.ALL_IN.action || vm.me!!.status == PlayerActions.FOLD.action -> {
                                fold_checkFL.visibility = GONE
                                checkFL.visibility = GONE
                                checkOrCallAnyFL.visibility = GONE

                                foldFL.visibility = GONE
                                callFL.visibility = GONE
                                callAnyFL.visibility = GONE
                            }
                            vm.me!!.amount_invested == it.game_max_bet_amount -> {
                                fold_checkFL.visibility = VISIBLE
                                checkFL.visibility = VISIBLE
                                checkOrCallAnyFL.visibility = VISIBLE

                                foldFL.visibility = GONE
                                callFL.visibility = GONE
                                callAnyFL.visibility = GONE
                            }
                            else -> {
                                fold_checkFL.visibility = GONE
                                checkFL.visibility = GONE
                                checkOrCallAnyFL.visibility = GONE

                                foldFL.visibility = VISIBLE
                                callFL.visibility = VISIBLE
                                callAnyFL.visibility = VISIBLE
                            }
                        }
                    }

                    foldBtn.visibility = GONE
                    callBtn.visibility = GONE
                    raiseBtn.visibility = GONE
                    checkBtn.visibility = GONE
                    allinBtn.visibility = GONE
                }

                slotViews.playerTurn = it
            }
        })

        foldBtn.onOneClick {
            vm.actionEvent(ActionEvent.FOLD) {
                runOnMain {
                    if (it!!["success"].bool) {
                        mc1.setImageResource(R.drawable.deck_card)
                        mc2.setImageResource(R.drawable.deck_card)
                    } else {
                        showSnack(it["description"].string)
                    }
                }
            }
        }

        callBtn.onOneClick {
            vm.actionEvent(ActionEvent.CALL_BET, total_amount = callBtn.tag as Double) {

            }
        }
        checkBtn.onOneClick {
            vm.actionEvent(ActionEvent.CHECK_BET, total_amount = callBtn.tag as Double) {

            }
        }

        allinBtn.onOneClick {
            vm.actionEvent(ActionEvent.ALL_IN, total_amount = callBtn.tag as Double) {

            }
        }

        raiseBtn.onOneClick {
            vm.actionEvent(
                ActionEvent.RAISE_BET,
                total_amount = raiseBtn.tag as Double,
                raise_amount = raiseBtn.tag as Double - callBtn.tag as Double
            ) {

            }
        }

        //autoGameAction

        sitOutCB.onOneClick { }

        foldCB.onOneClick {
            vm.autoGameAction(AutoGameAction.AUTO_FOLD, foldCB.isChecked) {}
        }
        checkCB.onOneClick {
            vm.autoGameAction(AutoGameAction.AUTO_CHECK, checkCB.isChecked) {}
        }
        checkOrCallAnyCB.onOneClick {
            vm.autoGameAction(AutoGameAction.AUTO_CALL, checkOrCallAnyCB.isChecked) {}
        }

        fold_checkCB.onOneClick {
            vm.autoGameAction(AutoGameAction.AUTO_CHECK, fold_checkCB.isChecked) {}
        }
        callCB.onOneClick {
            vm.autoGameAction(AutoGameAction.AUTO_CALL, callCB.isChecked) {}
        }
        callAnyCB.onOneClick {
            vm.autoGameAction(AutoGameAction.AUTO_CALL_ANY, callAnyCB.isChecked) {}
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
                        vm.connectTable()

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

    fun joinTableActions() {
        val builder = AlertDialog.Builder(this)
        val dialogView =
            LayoutInflater.from(this)
                .inflate(R.layout.join_status_popup, null)
        builder.setView(dialogView)
        builder.setCancelable(false)
        val dialog = builder.create()

        dialogView.join.onOneClick {
            if (dialogView.join_from_next.isChecked) {

            } else {

            }
        }
        dialogView.close.onOneClick { dialog.dismiss() }

        dialog.show()
    }

    fun exitTOLobby() {
        vm.leaveTable()
        socketInstance.removeSocketChangeListener(this)
        finish()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.END)) {
            drawer_layout.closeDrawer(GravityCompat.END)
        } else {
            vm.leaveTable()
            socketInstance.removeSocketChangeListener(this)
            super.onBackPressed()
        }
    }
}