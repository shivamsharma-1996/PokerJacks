package com.gtgt.pokerjacks.ui.game.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseFragment
import com.gtgt.pokerjacks.extensions.onOneClick
import com.gtgt.pokerjacks.extensions.runOnMain
import com.gtgt.pokerjacks.extensions.sharedViewModel
import com.gtgt.pokerjacks.ui.game.viewModel.GameViewModel
import kotlinx.android.synthetic.main.fragment_offers.*
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
     /*   val value = "[\n" +
                "{\n" +
                "        'seat_no': 1,\n" +
                "        'inplay_amount': 800.0,\n" +
                "        'join_id': 'join-11d1c240-68f7-4ba6-90bf-2f1682b72662',\n" +
                "        'user_unique_id': 'e8c1502e-90a2-4f28-8cf7-92af779eae99',\n" +
                "        'game_id': '',\n" +
                "        'table_id': 'table-33205040-7480-4d61-a81c-e373a631dc81',\n" +
                "        'user_name': 'mohan123',\n" +
                "        'buyin_amount': 100,\n" +
                "        'winnings': 20,\n" +
                "        'status': 'ACTIVE'\n" +
                "},\n" +
                "{\n" +
                "        'seat_no': 1,\n" +
                "        'inplay_amount': 800.0,\n" +
                "        'join_id': 'join-11d1c240-68f7-4ba6-90bf-2f1682b72662',\n" +
                "        'user_unique_id': 'e8c1502e-90a2-4f28-8cf7-92af779eae99',\n" +
                "        'game_id': '',\n" +
                "        'table_id': 'table-33205040-7480-4d61-a81c-e373a631dc81',\n" +
                "        'user_name': 'mohan123',\n" +
                "        'buyin_amount': 100,\n" +
                "        'winnings': -20,\n" +
                "        'status': 'ACTIVE'\n" +
                "}\n" +
                "]"

        val result = Gson().fromJson(
            value,
            Array<TableUserStatsItem>::class.java
        ).toList()

        tableUserStatsAdapter?.addHeaderObject()
        tableUserStatsAdapter?.submitList(ArrayList(result))
        log("tableUserStatsLD12", "tableUserStatsLD: " +result)*/


        iv_close.onOneClick {
            navigationViewCloseListener()
        }

        gameVm.tableUserStatsLD.observe(viewLifecycleOwner, Observer {
            if(it!=null){
                if(!it.tableUserStatsItem.isNullOrEmpty()){
                    runOnMain {
                        tableUserStatsAdapter?.apply {
                            addHeaderObject()
                            submitList(it.tableUserStatsItem)
                        }
                    }
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