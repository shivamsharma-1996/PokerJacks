package com.gtgt.pokerjacks.extensions

import android.view.View
import android.view.View.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.ui.game.models.PreviousHandDetails
import com.gtgt.pokerjacks.ui.game.models.TableSlotStatus

@BindingAdapter("gameDetails", "targetGameUser", "showCard", "isAllOpponentsFolded", "revealCards")
fun ImageView.revealLastHandCards(gameDetails: PreviousHandDetails.GameDetails, targetGameUser: PreviousHandDetails.GameUserX,
                                  showCard: String, isAllOpponentFolded: Boolean, revealCards: Boolean){
    val currentUserId = retrieveString("USER_ID")
    if(currentUserId != targetGameUser.user_unique_id){
        //these are opponents
        //if opponents is winner(wonAmt>0) and rest of all has FOLD status
        if(targetGameUser.won_amt > 0 && isAllOpponentFolded){
            setImageResource(R.drawable.deck_card)
            return
        }
       if((revealCards!=null && revealCards)) {
            if(targetGameUser.status == TableSlotStatus.FOLD.name){
                setImageResource(R.drawable.deck_card)
            }else{
                coloredCard(showCard)
            }
        } else {
           setImageResource(R.drawable.deck_card)
        }

    }else{
        coloredCard(showCard)
    }
}

/*@BindingAdapter("isAllOpponentsFolded", "gameUser")
fun View.isAllOpponentsFolded(isAllOpponentsFolded: Boolean, targetGameUser: PreviousHandDetails.GameUserX){
     if(isAllOpponentsFolded *//*&& targetGameUser.won_amt > 0*//*){
         setImageResource(R.drawable.deck_card)
//         INVISIBLE
    }else{
        //VISIBLE
    }
}*/

@BindingAdapter("handStrengthVisibility", "isAllOpponentFolded")
fun TextView.bindHandStrengthVisibility(targetGameUser: PreviousHandDetails.GameUserX, isAllOpponentFolded: Boolean){
    val currentUserId = retrieveString("USER_ID")
    if(currentUserId != targetGameUser.user_unique_id){
        if(targetGameUser.status == TableSlotStatus.FOLD.name || isAllOpponentFolded){
            visibility = GONE
        }else{
            visibility = VISIBLE
        }
    }

}

