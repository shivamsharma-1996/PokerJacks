package com.gtgt.pokerjacks.ui.offers.scratch_card

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseActivity

class AllScratchCardActivity : BaseActivity() {
//    private val viewModel: OffersViewModel by viewModel()
//    var stopApiCall = false
//    private val allScratchCardsAdapter = AllScratchCardsAdapter(this, ::onItemClicked)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_scratch_card)
    }
}