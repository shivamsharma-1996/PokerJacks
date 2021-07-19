package com.gtgt.pokerjacks.ui.game.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gtgt.pokerjacks.BR
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.databinding.TableStatsItemsBinding
import com.gtgt.pokerjacks.ui.game.models.TableUserStatsHeader
import com.gtgt.pokerjacks.ui.game.models.TableUserStatsItem


class StatsAdapter(
    val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_HEADER = 0
    private val TYPE_ITEM = 1

    private var tableUserStatsItemList: ArrayList<TableUserStatsItem> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_ITEM) {
            val binding: TableStatsItemsBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.table_stats_items, parent, false
            )
            return VHItem(binding)
        } else if (viewType == TYPE_HEADER) {
            return VHHeader(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.header_table_stats_items, parent, false)
            );
        }
        throw IllegalArgumentException()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, basePosition: Int) {
        when(holder){
            is VHHeader -> return
            is VHItem -> {
                val itemPosition: Int = basePosition - mHeaderList.size
                val tableUserStatsItem = tableUserStatsItemList[itemPosition]
                holder.bind(tableUserStatsItem)
            }
        }
    }

    private val mHeaderList: ArrayList<TableUserStatsHeader> = ArrayList()
    fun addHeaderObject() {
        mHeaderList.add(TableUserStatsHeader())
    }

    override fun getItemCount(): Int {
        return mHeaderList.size + tableUserStatsItemList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (isPositionHeader(position)) TYPE_HEADER else TYPE_ITEM
    }

    private fun isPositionHeader(position: Int): Boolean {
        return position == 0
    }

    class VHHeader(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    class VHItem(tableStatsItemsBinding: TableStatsItemsBinding) : RecyclerView.ViewHolder(
        tableStatsItemsBinding.root
    ) {
        var itemRowBinding: TableStatsItemsBinding = tableStatsItemsBinding

        fun bind(tableUserStatsItem: TableUserStatsItem) {
            itemRowBinding.setVariable(BR.data, tableUserStatsItem)
            itemRowBinding.executePendingBindings()
        }
    }

    fun submitList(newList: ArrayList<TableUserStatsItem>) {
        tableUserStatsItemList.addAll(newList)
        notifyDataSetChanged()
    }

}