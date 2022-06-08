/*
 * Copyright (c) 2022 Indra Azimi. All rights reserved.
 *
 * Dibuat untuk kelas Pemrograman untuk Perangkat Bergerak 1.
 * Dilarang melakukan penggandaan dan atau komersialisasi,
 * sebagian atau seluruh bagian, baik cetak maupun elektronik
 * terhadap project ini tanpa izin pemilik hak cipta.
 */

package com.indraazimi.galerihewan.ui.main

import android.app.Application
import android.icu.util.TimeUnit
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.indraazimi.galerihewan.R
import com.indraazimi.galerihewan.model.Hewan
import com.indraazimi.galerihewan.network.HewanApi
import com.indraazimi.galerihewan.network.UpdateWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val data = MutableLiveData<List<Hewan>>()
    init {
        retrieveData()
    }

    private fun retrieveData() {
        viewModelScope.launch (Dispatchers.IO) {
            try {
                data.postValue(HewanApi.service.getHewan())
            } catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
            }
        }
    }
    // Data ini akan kita ambil dari server di langkah selanjutnya
    fun getData(): LiveData<List<Hewan>> = data

    fun scheduleUpdater(app: Application) {
        val req = OneTimeWorkRequestBuilder<UpdateWorker>()
            .setInitialDelay(1, java.util.concurrent.TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(app).enqueueUniqueWork(
            "updater",
            ExistingWorkPolicy.REPLACE,
            req
        )
    }
}