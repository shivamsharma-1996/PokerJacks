package com.gtgt.pokerjacks.ui.profile.manage_account.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.gtgt.pokerjacks.base.BaseModel
import com.gtgt.pokerjacks.base.BaseViewModel
import com.gtgt.pokerjacks.extensions.execute
import com.gtgt.pokerjacks.ui.profile.manage_account.model.BankFromIFSC
import com.gtgt.pokerjacks.ui.profile.manage_account.model.CreateBankDetails
import com.gtgt.pokerjacks.ui.profile.manage_account.model.GetBankDetails
import com.gtgt.pokerjacks.ui.profile.manage_account.model.GetVerification

class ManageAccountViewModel : BaseViewModel() {
    val _createBankDetails: MutableLiveData<CreateBankDetails> = MutableLiveData()
    val createBankDetails: LiveData<CreateBankDetails> = _createBankDetails

    val _getBankDetails: MutableLiveData<GetBankDetails> = MutableLiveData()
    val getBankDetails: LiveData<GetBankDetails> = _getBankDetails

    val _deleteBankAccount: MutableLiveData<BaseModel> = MutableLiveData()
    val deleteBankAccount: LiveData<BaseModel> = _deleteBankAccount

    val _getVerification: MutableLiveData<GetVerification> = MutableLiveData()
    val getVerification: LiveData<GetVerification> = _getVerification

    val _bankFromIFSC: MutableLiveData<BankFromIFSC> = MutableLiveData()
    val bankFromIFSC: LiveData<BankFromIFSC> = _bankFromIFSC

    fun createBankDetails(
        userName: String? = null,
        accNumber: String? = null,
        ifsc: String? = null,
        bankName: String? = null,
        branchName: String? = null,
        upi: String? = null,
        paytmNumber: String? = null
    ) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("name", userName)
        jsonObject.addProperty("bankName", bankName)
        jsonObject.addProperty("branch", branchName)
        jsonObject.addProperty("accountNo", accNumber)
        jsonObject.addProperty("ifsc", ifsc)
        jsonObject.addProperty("upi", upi)
        jsonObject.addProperty("paytm_wallet_number", paytmNumber)
        apiServicesPlatform.createBankDetails(jsonObject).execute(activity, true){
            _createBankDetails.value = it
        }
    }

    fun getBankDetails() {
        apiServicesPlatform.getBankDetails().execute(activity, true) {
            _getBankDetails.value = it
        }
    }

    fun deleteBankDetails(detailId: String) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("bankId", detailId)
        apiServicesPlatform.deleteBankDetails(jsonObject).execute(activity, true) {
            _deleteBankAccount.value = it
        }
    }

    fun getVerification(bankId: String) {
        apiServicesPlatform.getVerification(bankId = bankId).execute(activity, true) {
            _getVerification.value = it
        }
    }

    fun getBankDetailsFromIFSC(ifscCode: String) {
        apiServicesPlatform.getBankDetailsfromIFSC(ifsc = ifscCode).execute(activity, true) {
            _bankFromIFSC.value = it
        }
    }
}