package com.example.provehomefragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RecipeViewModel : ViewModel() {

    private lateinit var recipes : MutableLiveData<List<Recipe>>
    private lateinit var rManager: RepositoryManager

    fun init(){
        rManager = RepositoryManager.instance
        recipes = rManager.getRecipes()
    }

    fun getLiveRecipes():LiveData<List<Recipe>>{
        return recipes
    }

}