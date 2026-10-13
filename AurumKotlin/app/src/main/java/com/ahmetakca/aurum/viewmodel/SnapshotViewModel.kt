package com.ahmetakca.aurum.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ahmetakca.aurum.NetworkClient
import com.ahmetakca.aurum.models.GetSnapshotAndId
import com.ahmetakca.aurum.models.GetSummary
import com.ahmetakca.aurum.models.SnapshotRequest
import com.ahmetakca.aurum.models.SnapshotResponse
import com.ahmetakca.aurum.models.Summary
import com.ahmetakca.aurum.service.ApiService
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class SnapshotViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val apiService: ApiService = NetworkClient.createApiService()
    private val compositeDisposable = CompositeDisposable()
    private val _snapshots = MutableLiveData<SnapshotResponse>()
    val snapshots: LiveData<SnapshotResponse> = _snapshots
    private val _snapshotAdded = MutableLiveData<GetSnapshotAndId>()
    private val _summaries = MutableLiveData<GetSummary>()
    val summaries: LiveData<GetSummary> = _summaries
    val snapshotAdded: LiveData<GetSnapshotAndId> = _snapshotAdded
    private val _isSaving = MutableLiveData<Boolean>()
    val isSaving: LiveData<Boolean> = _isSaving
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    fun addSnapshot(snapshotRequest: SnapshotRequest) {
        val user = auth.currentUser ?: return
        user.getIdToken(false).addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@addOnCompleteListener
            }
            val token = task.result?.token ?: return@addOnCompleteListener
            _isSaving.value = true
            //setSaveLoading(true)
            compositeDisposable.add(
                apiService.addSnapshot(snapshotRequest, "Bearer $token")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe({ response ->
                        //setSaveLoading(false)
                        _isSaving.value = false
                        _snapshotAdded.value = response
                    }, { error ->
                        _isSaving.value = false
                        //setSaveLoading(false)
                        error.printStackTrace()
                    })
            )
        }
    }

    fun getSnapshots() {
        val user = auth.currentUser ?: return
        user.getIdToken(false).addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@addOnCompleteListener
            }
            val token = task.result?.token ?: return@addOnCompleteListener
            _isLoading.value = true
            compositeDisposable.add(
                apiService.getSnapshot("Bearer ${token}").subscribeOn(Schedulers.io()).observeOn(
                    AndroidSchedulers.mainThread()
                ).subscribe({ response ->
                    _isLoading.value = false
                    _snapshots.value = response
                }, { error ->
                    _isLoading.value = false
                    error.printStackTrace()
                })
            )
        }
    }

    fun getSummary() {
        val user = auth.currentUser ?: return
        user.getIdToken(false).addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@addOnCompleteListener
            }
            val token = task.result?.token ?: return@addOnCompleteListener
            _isLoading.value = true
            compositeDisposable.add(
                apiService.getDaily("Bearer ${token}").subscribeOn(Schedulers.io()).observeOn(
                    AndroidSchedulers.mainThread()
                ).subscribe({ response ->
                    _isLoading.value = false
                    _summaries.value = response
                }, { error ->
                    _isLoading.value = false
                    error.printStackTrace()
                })
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}