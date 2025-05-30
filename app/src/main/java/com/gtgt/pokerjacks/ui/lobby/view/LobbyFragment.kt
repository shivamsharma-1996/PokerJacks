package com.gtgt.pokerjacks.ui.lobby.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import com.github.salomonbrys.kotson.double
import com.github.salomonbrys.kotson.get
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseFragment
import com.gtgt.pokerjacks.extensions.*
import com.gtgt.pokerjacks.ui.HomeActivity
import com.gtgt.pokerjacks.ui.game.GameActivity
import com.gtgt.pokerjacks.ui.lobby.HomeViewModel
import com.gtgt.pokerjacks.ui.lobby.adapter.LobbyAdapter
import com.gtgt.pokerjacks.ui.lobby.viewmodel.LobbyViewModel
import com.gtgt.pokerjacks.ui.wallet.wallet.WalletViewModel
import com.gtgt.pokerjacks.utils.LinearLayoutManagerWrapper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.chips_added.view.*
import kotlinx.android.synthetic.main.fragment_lobby.*


class LobbyFragment : BaseFragment() {
    private lateinit var selectedPlayersFilterTV: TextView
    private val lobbyAdapter by lazy {
        LobbyAdapter {
            launchActivity<GameActivity> {
                putExtra("table_id", it.table_id)
                putExtra("plan", it.plan_details)
            }
        }
    }

    private val lobbyVM: LobbyViewModel by viewModel()
    private val viewModel: HomeViewModel by sharedViewModel()
    private val walletViewModel: WalletViewModel by store()

    var selectedPlayersFilter = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lobby, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()

        selectedPlayersFilterTV = playersAll

        playersAll.onOneClick { playersFilterChanged(playersAll, -1) }
        players9.onOneClick { playersFilterChanged(players9, 9) }
        players6.onOneClick { playersFilterChanged(players6, 6) }
        players2.onOneClick { playersFilterChanged(players2, 2) }

        if (eventsRV.layoutManager == null) {
            eventsRV.layoutManager = LinearLayoutManagerWrapper(context)
        }
        eventsRV.adapter = lobbyAdapter


        lobbyVM.lobbyTables.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it?.success) {
                    log("selectedPlayersFilter", lobbyVM.playersAllTables)
                    when (selectedPlayersFilter) {
                        -1 -> lobbyAdapter.submitList(lobbyVM.playersAllTables)
                        2 -> lobbyAdapter.submitList(lobbyVM.players2Tables)
                        6 -> lobbyAdapter.submitList(lobbyVM.players6Tables)
                        9 -> lobbyAdapter.submitList(lobbyVM.players9Tables)
                    }
                }
            }
        })
    }

    private fun initUI() {
        iv_hamberger.onOneClick {
            (activity as HomeActivity).drawer_layout.openDrawer(GravityCompat.START)
        }

        modeSwitch.onChange = {
            viewModel.changePlayMode(it == 1)
        }
        modeSwitch.changeSelection(1)
        var isFirst = true
        viewModel.isCash.observe(viewLifecycleOwner, Observer {
            putBoolean("isCash", it)
            if (!isFirst && retrieveBoolean("isUserBlocked")) {
//                activity?.suspendedPopup()
                modeSwitch.changeSelection(2)
            } else if (it) {
                context?.showComingSoonDialog {
                    modeSwitch.changeSelection(2)
                }
                tv_addMoney.text = "Add Money"

                if (walletViewModel.walletDetailsResponse.value == null) {
                    walletViewModel.getWalletDetailsByToken()
                } else {
                    walletViewModel.walletDetailsResponse.notify()
                }

            } else {

                if (walletViewModel.playWalletDetailsResponse.value == null) {
                    walletViewModel.getPlayWalletDetailsByToken()
                } else {
                    walletViewModel.playWalletDetailsResponse.notify()
                }

                tv_addMoney.text = "Add Coins"
            }
            isFirst = false
        })

        walletViewModel.walletDetailsResponse.observe(viewLifecycleOwner, Observer {
            tv_totalBalance.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            if (it.success) {
                tv_totalBalance.text =
                    "${getString(R.string.ruppe)}${(it.info.total).toDecimalFormat()}"

                putInt("WINNINGS_AMT", it.info.winnings.toInt())
            } else {
            }
        })

        walletViewModel.playWalletDetailsResponse.observe(viewLifecycleOwner, Observer {
            tv_totalBalance.text = it.toDecimalFormat()
            tv_totalBalance.setCompoundDrawablesWithIntrinsicBounds(R.drawable.coin, 0, 0, 0)
        })

        tv_addMoney.onOneClick {
            if (retrieveBoolean("isUserBlocked")) {
//                activity?.suspendedPopup()
            } else {
                if (viewModel.isCash.value!!) {
                    (activity as HomeActivity).onWalletClicked()
                } else {
                    walletViewModel.addChips {
                        walletViewModel._playWalletDetailsResponse.value =
                            it.info["totalChips"].double
                        showChipAddedPopup(it.info["totalChips"].double)
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showChipAddedPopup(chips: Double) {
        val builder = AlertDialog.Builder(context)
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.chips_added, null)
        dialogView.message.text =
            "${chips.toDecimalFormat()} have been added to your account!"

        builder.setView(dialogView)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogView.ok.onOneClick {
            dialog.dismiss()
        }
        dialogView.close.onOneClick {
            dialog.dismiss()
        }
        dialog.show()
    }

    var isFirst = true

    override fun onResume() {
        super.onResume()
        if (!isFirst) {
            if (viewModel.isCash.value!!) {
                walletViewModel.getWalletDetailsByToken()
            } else {
                walletViewModel.getPlayWalletDetailsByToken()
            }
        }
        isFirst = false

        lobbyVM.getActiveTables()
    }

    private fun playersFilterChanged(view: TextView, players: Int) {
        selectedPlayersFilter = players
        if (selectedPlayersFilterTV != view) {
            selectedPlayersFilterTV.setBackgroundResource(R.drawable.unselected_button)
            view.setBackgroundResource(R.drawable.blue_gradient_button)
            selectedPlayersFilterTV = view

            lobbyVM.lobbyTables.notify()

        }
    }
}