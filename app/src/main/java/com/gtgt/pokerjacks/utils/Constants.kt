package com.gtgt.pokerjacks.utils

enum class SlotPositions {
    LEFT_TOP_CENTER, LEFT_TOP, TOP_CENTER, RIGHT_TOP, RIGHT_TOP_CENTER,
    LEFT_BOTTOM_CENTER, LEFT_BOTTOM, BOTTOM_CENTER, RIGHT_BOTTOM, RIGHT_BOTTOM_CENTER,
    LEFT_CENTER, RIGHT_CENTER
}

object Constants {

    const val SELECTED_THEME = "SELECTED_THEME"
    const val PRODUCT_NAME = "POKER"

    enum class ErrorCodes(val code: Int, val description: String) {
        USER_PAN_DETAILS_NOT_FOUND(1, "Sorry, user document details not found in the system"),
    }

    enum class DocumentErrorCodes(val code: String) {
        USER_DETAILS_PENDING("PENDING"),
        USER_DETAILS_REJECTED("REJECTED"),
        USER_DETAILS_APPROVED("APPROVED"),
        USER_DETAILS_NORECORD("NORECORD")
    }

    enum class PopupEvents {
        HOME,
        WALLET,
        BONUS,
        ANNOUNCEMENT,
        WEBPAGE
    }

    enum class TopicName {
        ALL,
        NEWUSER,
        PAIDUSER
    }
}