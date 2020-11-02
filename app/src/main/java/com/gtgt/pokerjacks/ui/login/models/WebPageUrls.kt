package com.gtgt.pokerjacks.ui.login.models

import com.gtgt.pokerjacks.base.BaseModel

data class WebPageUrls(val info: WebPageUrlsInfo) : BaseModel()

class WebPageUrlsInfo(
    val terms_and_conditions_url: String?="",
    val how_to_play_url: String?="",
    val privacy_policy_url: String?="",
    val points_system_url: String?="",
    val FAQs_url: String?="",
    val refer_and_earn_url: String?=""
)