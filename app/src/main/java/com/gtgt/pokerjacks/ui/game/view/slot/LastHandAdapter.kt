package com.gtgt.pokerjacks.ui.game.view.slot

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gtgt.pokerjacks.BR
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.databinding.ItemLastHandBinding
import com.gtgt.pokerjacks.ui.game.models.PreviousHandDetails

class LastHandAdapter(
    val context: Context
) : RecyclerView.Adapter<LastHandAdapter.LastHandHolder>() {

    private lateinit var lastHandDetails: PreviousHandDetails.Info
    private var previousHandGameUsers: ArrayList<PreviousHandDetails.GameUserX> = ArrayList()
    private lateinit var previousHandGameDetails: PreviousHandDetails.GameDetails

    class LastHandHolder(itemLastHandBinding: ItemLastHandBinding) : RecyclerView.ViewHolder(
        itemLastHandBinding.root
    ) {
        var itemRowBinding: ItemLastHandBinding = itemLastHandBinding

        fun bind(
            previousHandGameUser: PreviousHandDetails.GameUserX,
            previousHandGameDetails: PreviousHandDetails.GameDetails
        ) {
            itemRowBinding.setVariable(BR.gameUser, previousHandGameUser)
            itemRowBinding.setVariable(BR.gameDetail, previousHandGameDetails)
            itemRowBinding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LastHandHolder {
        val binding: ItemLastHandBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_last_hand, parent, false
        )
        return LastHandHolder(binding)
    }

    override fun onBindViewHolder(holder: LastHandHolder, position: Int) {
        val previousHandGameUsers = previousHandGameUsers[position]
        holder.bind(previousHandGameUsers, previousHandGameDetails)
    }

    override fun getItemCount(): Int {
        return previousHandGameUsers.size
    }

    fun submitNewInfo(newInfo: PreviousHandDetails.Info) {
        lastHandDetails = newInfo
        previousHandGameDetails = newInfo.gameDetails
        previousHandGameUsers.clear()
        previousHandGameUsers.addAll(newInfo.gameUsers)

        notifyDataSetChanged()
    }
}