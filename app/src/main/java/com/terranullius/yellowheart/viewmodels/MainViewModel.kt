package com.terranullius.yellowheart.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.terranullius.yellowheart.data.Initiative
import com.terranullius.yellowheart.data.toInitiative
import com.terranullius.yellowheart.data.toInitiativeDto
import com.terranullius.yellowheart.other.Constants
import com.terranullius.yellowheart.utils.Result
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
                    Log.d("fuck", "firestore listener error: $error")
                }
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
