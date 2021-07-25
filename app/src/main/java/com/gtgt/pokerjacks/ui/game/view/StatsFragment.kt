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
import com.gtgt.pokerjacks.ui.game.models.TableUserStatsItem
import com.gtgt.pokerjacks.ui.game.viewModel.GameViewModel
import kotlinx.android.synthetic.main.fragment_stats.*

class StatsFragment  : BaseFragment() {

    lateinit var navigationViewCloseListener:()->Unit

    private val tableUserStatsAdapter by lazy {
        context?.let {
            StatsAdapter(it)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(navigationViewCloseListener:() -> Unit ) = StatsFragment().apply {
           this.navigationViewCloseListener = navigationViewCloseListener
        }
    }

    private val gameVm: GameViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()

        iv_close.onOneClick {
            navigationViewCloseListener()
        }
        progress_bar_loading.visibility = VISIBLE

        gameVm.tableUserStatsLD.observe(viewLifecycleOwner, Observer {
            if(it!=null){
                progress_bar_loading.visibility = GONE

                if(!it.isNullOrEmpty()){
                    runOnMain {
                        tableUserStatsAdapter?.apply {
                            addHeader()
                            submitList(it as ArrayList<TableUserStatsItem>)
                        }
                    }
                    tv_no_data.visibility = GONE
                }else{
                    tv_no_data.visibility = VISIBLE
                }
            }
        })
    }

    private fun initUI() {
        rv_TableUserStats.let {
            it.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            it.adapter = tableUserStatsAdapter
        }

    }

}