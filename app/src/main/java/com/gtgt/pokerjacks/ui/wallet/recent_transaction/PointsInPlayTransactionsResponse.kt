package com.gtgt.pokerjacks.ui.wallet.recent_transaction

data class PointsInPlayTransactionsResponse(
    val success: Boolean,
    val errorCode: Int,
    val errorDesc: String,
    val info: PointsInPlayTransactionsInfo?
)

data class PointsInPlayTransactionsInfo(
    val buyInAmount: String,
    val moneyLost: String,
    val moneyWon: String,
    val transactionHistory: List<TransactionHistory>
)

data class TransactionHistory(
    val gameId: String,
    val amount: String,
    val txnMode: String,
    val createdAt: String
)