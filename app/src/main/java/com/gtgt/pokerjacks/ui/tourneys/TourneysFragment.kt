package com.gtgt.pokerjacks.ui.tourneys

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.extensions.onOneClick
import com.gtgt.pokerjacks.ui.HomeActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar_layout_nav.*

class TourneysFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tourneys, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_CommonTitleHome.text="Tourneys"

        iv_hb.onOneClick {
            (activity as HomeActivity).drawer_layout.openDrawer(GravityCompat.START)
        }
    }
}