package com.ahmetakca.aurum.service

import com.ahmetakca.aurum.models.GetSnapshotAndId
import com.ahmetakca.aurum.models.GetSummary
import com.ahmetakca.aurum.models.LoginRequest
import com.ahmetakca.aurum.models.SnapshotRequest
import com.ahmetakca.aurum.models.SnapshotResponse
import com.ahmetakca.aurum.models.Summary
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("auth/login")
    fun login(
        @Body loginRequest: LoginRequest,
        @Header("Authorization") firebaseIdToken: String
    ): Observable<Int>

    @POST("snapshot")
    fun addSnapshot(
        @Body snapshotRequest: SnapshotRequest,
        @Header("Authorization") firebaseIdToken: String
    ): Observable<GetSnapshotAndId>

    @GET("snapshot")
    fun getSnapshot(@Header("Authorization") firebaseIdToken: String): Observable<SnapshotResponse>

    @GET("summary/daily")
    fun getDaily(@Header("Authorization") firebaseIdToken: String): Observable<GetSummary>
}