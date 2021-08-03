package com.gtgt.pokerjacks.extensions

import android.view.View
import android.view.View.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.gtgt.pokerjacks.ui.game.models.PreviousHandDetails
import com.gtgt.pokerjacks.ui.game.models.TableSlotStatus

@BindingAdapter("gameDetails", "targetGameUser", "showCard")
fun ImageView.revealLastHandCards(gameDetails: PreviousHandDetails.GameDetails, targetGameUser: PreviousHandDetails.GameUserX,
                                  showCard: String){
    val currentUserId = retrieveString("USER_ID")
    val cardsReveal = gameDetails.cards_reveal
    if(currentUserId != targetGameUser.user_unique_id){
        visibility = if((cardsReveal!=null && cardsReveal) || targetGameUser.status != TableSlotStatus.FOLD.name) {
            coloredCard(showCard)
            VISIBLE
        } else {
            INVISIBLE
        }

        //need to hide Winner's cards and hand strength if  his all opponents are folded

    }else{
        visibility = VISIBLE
        coloredCard(showCard)
    }
}

@BindingAdapter("isAllOpponentsFolded", "gameUser")
fun View.isAllOpponentsFolded(isAllOpponentsFolded: Boolean, targetGameUser: PreviousHandDetails.GameUserX){
    visibility = if(isAllOpponentsFolded && targetGameUser.won_amt > 0){
        INVISIBLE
    }else{
        VISIBLE
    }
}

@BindingAdapter("handStrengthVisibility")
fun TextView.bindHandStrengthVisibility(targetGameUser: PreviousHandDetails.GameUserX){
    if(targetGameUser.status != TableSlotStatus.FOLD.name){
        visibility = VISIBLE
    }else{
        visibility = GONE
    }
}

