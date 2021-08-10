package com.gtgt.pokerjacks.ui.game.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gtgt.pokerjacks.BR
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.databinding.ItemLastHandBinding
import com.gtgt.pokerjacks.ui.game.models.PreviousHandDetails

class PreviousHandAdapter(
    val context: Context
) : RecyclerView.Adapter<PreviousHandAdapter.LastHandHolder>() {

    private lateinit var lastHandDetails: PreviousHandDetails.Info
    private var previousHandGameUsers: ArrayList<PreviousHandDetails.GameUserX> = ArrayList()
    private lateinit var previousHandGameDetails: PreviousHandDetails.GameDetails
    private var isAllOpponentFolded: Boolean = false
    private var revealCards: Boolean = false

    class LastHandHolder(itemLastHandBinding: ItemLastHandBinding) : RecyclerView.ViewHolder(
        itemLastHandBinding.root
    ) {
        var itemRowBinding: ItemLastHandBinding = itemLastHandBinding

        fun bind(
            previousHandGameUser: PreviousHandDetails.GameUserX,
            previousHandGameDetails: PreviousHandDetails.GameDetails,
            isAllOpponentFolded: Boolean,
            revealCards: Boolean
        ) {
            itemRowBinding.setVariable(BR.gameUser, previousHandGameUser)
            itemRowBinding.setVariable(BR.gameDetail, previousHandGameDetails)
            itemRowBinding.setVariable(BR.isAllOpponentFolded, isAllOpponentFolded)
            itemRowBinding.setVariable(BR.revealCards, revealCards)
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
        holder.bind(previousHandGameUsers, previousHandGameDetails, isAllOpponentFolded, revealCards)
    }

    override fun getItemCount(): Int {
        return previousHandGameUsers.size
    }

    fun submitNewInfo(newInfo: PreviousHandDetails.Info, isAllOpponentFolded: Boolean, revealCards: Boolean) {
        this.isAllOpponentFolded = isAllOpponentFolded
        this.revealCards = revealCards
        lastHandDetails = newInfo
        previousHandGameDetails = newInfo.gameDetails
        previousHandGameUsers.clear()
        previousHandGameUsers.addAll(newInfo.gameUsers)

        notifyDataSetChanged()
    }
}