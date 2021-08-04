package com.terranullius.yellowheartwelfare.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.paykun.sdk.eventbus.Events
import com.paykun.sdk.helper.PaykunHelper
import com.terranullius.yellowheartwelfare.data.Initiative
import com.terranullius.yellowheartwelfare.data.toInitiative
import com.terranullius.yellowheartwelfare.data.toInitiativeDto
import com.terranullius.yellowheartwelfare.other.Constants
import com.terranullius.yellowheartwelfare.utils.Result
import com.terrranullius.pickcab.util.Event
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _initiativesFlow = MutableStateFlow<Result<List<Initiative>>>(Result.Loading)
    val initiativesFlow: StateFlow<Result<List<Initiative>>>
        get() = _initiativesFlow

    private val _isSignedIn = MutableStateFlow(false)
    val isSignedInFlow: StateFlow<Boolean>
        get() = _isSignedIn

    private val _paymentCallback = MutableStateFlow<Event<Result<String>>>(Event(Result.Loading))
    val paymentCallback: StateFlow<Event<Result<String>>>
        get() = _paymentCallback

    private var initiatives: List<Initiative>? = null

    private var firestoreListenerJob: Job? = null

    var isFirstRun = true

    fun onSignedIn() {
        _isSignedIn.value = true
        if (isFirstRun) {
            if (firestoreListenerJob == null) {
                viewModelScope.launch {
                    firestoreListenerJob = launch {
                        setInitiativesListener()
                    }
                }
            }
            isFirstRun = false
        }
    }

    private fun setInitiativesListener() {
        if (_isSignedIn.value) {
            val firestore = FirebaseFirestore.getInstance()
            val initiativesCollectionRef = firestore.collection(Constants.COLLECTION_INITIATIVE)

            initiativesCollectionRef.addSnapshotListener { value, error ->
                if (error != null) {
                    _initiativesFlow.value = Result.Error(error)
                } else {
                    value?.let {
                        initiatives = it.documents.mapNotNull { documentSnapshot ->
                            documentSnapshot.toInitiativeDto()
                        }.map { initiativeDto ->
                            initiativeDto.toInitiative()
                        }.sortedBy { initiative ->
                            initiative.order
                        }
                    }
                    initiatives?.let {
                        Log.d("fuck", "initiatives: $it")
                        _initiativesFlow.value = Result.Success(it)
                    }
                }
            }
        }
    }

    fun onPaymentFinished(status: Events.PaymentMessage) {
        when (status.results) {
            PaykunHelper.MESSAGE_SUCCESS -> _paymentCallback.value = Event(Result.Success("Success"))
            else -> {
                Log.d("fuck", "Paykun: ${status.results}")
                _paymentCallback.value = Event(Result.Error(data = status.results))
            }
        }
    }
}
