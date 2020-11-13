package com.gtgt.pokerjacks.ui.lobby.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.extensions.onOneClick
import com.gtgt.pokerjacks.ui.lobby.adapter.LobbyAdapter
import com.gtgt.pokerjacks.ui.lobby.model.Event
import kotlinx.android.synthetic.main.fragment_lobby.*


class LobbyFragment : Fragment() {
    private lateinit var selectedPlayersFilterTV: TextView
    private val lobbyAdapter by lazy { LobbyAdapter() }

    var selectedPlayersFilter = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lobby, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectedPlayersFilterTV = playersAll

        playersAll.onOneClick { playersFilterChanged(playersAll, -1) }
        players9.onOneClick { playersFilterChanged(players9, 9) }
        players6.onOneClick { playersFilterChanged(players6, 6) }
        players2.onOneClick { playersFilterChanged(players2, 2) }
        eventsRV.adapter = lobbyAdapter
        lobbyAdapter.submitList(
            listOf(
                Event(1, 2),
                Event(2, 6),
                Event(3, 6),
                Event(4, 6),
                Event(5, 6),
                Event(6, 6),
                Event(6, 9),
                Event(7, 9),
                Event(8, 9),
                Event(9, 9)
            )
        )
    }

    private fun playersFilterChanged(view: TextView, players: Int) {
        if (selectedPlayersFilterTV != view) {
            selectedPlayersFilterTV.setBackgroundResource(R.drawable.unselected_button)
            view.setBackgroundResource(R.drawable.blue_gradient_button)
            selectedPlayersFilterTV = view
        }
    }
}