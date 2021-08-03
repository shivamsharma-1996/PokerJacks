package com.gtgt.pokerjacks.ui.game.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseFragment
import com.gtgt.pokerjacks.extensions.onOneClick
import com.gtgt.pokerjacks.extensions.runOnMain
import com.gtgt.pokerjacks.extensions.sharedViewModel
import com.gtgt.pokerjacks.ui.game.models.TableSlotStatus
import com.gtgt.pokerjacks.ui.game.view.slot.LastHandAdapter
import com.gtgt.pokerjacks.ui.game.viewModel.GameViewModel
import kotlinx.android.synthetic.main.fragment_last_hand.*
import kotlinx.android.synthetic.main.fragment_last_hand.iv_close
import kotlinx.android.synthetic.main.fragment_last_hand.tv_no_data

class PreviousHandFragment : BaseFragment() {
    private val gameVm: GameViewModel by sharedViewModel()
    lateinit var navigationViewCloseListener:()->Unit

    private var previousHandsList: List<String> = ArrayList()
    private var currentPageIndex: Int = 0
    private val lastHandAdapter by lazy {
        context?.let {
            LastHandAdapter(it)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(navigationViewCloseListener:() -> Unit ) = PreviousHandFragment().apply {
            this.navigationViewCloseListener = navigationViewCloseListener
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_last_hand, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()

        iv_previous_page.onOneClick {
            loadCurrentPage(previousHandsList[--currentPageIndex])
        }
        iv_next_page.onOneClick {
            loadCurrentPage(previousHandsList[++currentPageIndex])
        }

        iv_close.onOneClick {
            navigationViewCloseListener()
        }

        gameVm.previousHandsLD.observe(viewLifecycleOwner, Observer { previousGameList ->
            if(previousGameList!=null){
                if(!previousGameList.isNullOrEmpty()){
                    previousHandsList = previousGameList
                    val currentGameId = previousHandsList[currentPageIndex]
                    loadCurrentPage(currentGameId)
                    updatePageStatus()

                    iv_previous_page.visibility = VISIBLE
                    iv_next_page.visibility = VISIBLE
                    tv_no_data.visibility = GONE
                }else{
                    iv_previous_page.visibility = GONE
                    iv_next_page.visibility = GONE
                    tv_no_data.visibility = VISIBLE
                }
            }
        })

        gameVm.previousHandDetailsLD.observe(viewLifecycleOwner, Observer {
            runOnMain {
                progress_bar_loading.visibility = GONE
                lastHandAdapter?.apply {
                    val isAllOpponentFolded = it.gameUsers.count { it.status == TableSlotStatus.FOLD.name} == it.gameUsers.size -1
                    submitNewInfo(it, isAllOpponentFolded)
                    updatePageStatus()
                    tv_game_id.text = it.gameDetails.game_uid
                    disableEnableActions(currentPageIndex)
                }
            }
        })
    }

    private fun updatePageStatus() {
        runOnMain {
            tv_page_status.text = String.format("%s/%s", currentPageIndex+1, previousHandsList.size)
        }
    }

    private fun loadCurrentPage(gameId: String) {
        progress_bar_loading.visibility = VISIBLE
        gameVm.getPreviousHandDetails(gameId)
    }

    private fun initUI() {
        rec_previous_hands.let {
            it.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            it.adapter = lastHandAdapter
        }
    }

    private fun disableEnableActions(currentPageIndex: Int) {
        runOnMain {
            if(previousHandsList.size == 1){
                iv_next_page.alpha = 0.5f
                iv_next_page.isClickable = false
                iv_previous_page.alpha = 0.5f
                iv_previous_page.isClickable = false
            }else{
                when (currentPageIndex) {
                    0 -> {
                        iv_next_page.alpha = 1f
                        iv_next_page.isClickable = true
                        iv_previous_page.alpha = 0.5f
                        iv_previous_page.isClickable = false
                    }
                    previousHandsList.size-1 -> {
                        iv_next_page.alpha = 0.5f
                        iv_next_page.isClickable = false
                        iv_previous_page.alpha = 1f
                        iv_previous_page.isClickable = true
                    }
                    else -> {
                        iv_next_page.alpha = 1f
                        iv_next_page.isClickable = true
                        iv_previous_page.alpha = 1f
                        iv_previous_page.isClickable = true
                    }
                }
            }
        }
    }
}