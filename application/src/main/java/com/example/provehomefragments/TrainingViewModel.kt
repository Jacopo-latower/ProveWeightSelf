package com.example.provehomefragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TrainingViewModel : ViewModel() {

    private lateinit var trainings : MutableLiveData<List<Trainings>>
    private lateinit var rManager: RepositoryManager

    fun init(){
        rManager = RepositoryManager.instance
        trainings = rManager.getTrainings()
    }

    fun getLiveTrainings(): LiveData<List<Trainings>> {
        return trainings
    }

}