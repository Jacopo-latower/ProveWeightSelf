package com.example.provehomefragments

import android.util.Log
import androidx.lifecycle.MutableLiveData
import io.realm.Realm
import io.realm.mongodb.App
import io.realm.mongodb.AppConfiguration
import io.realm.mongodb.sync.SyncConfiguration

class RepositoryManager {

    companion object{
        val instance = RepositoryManager()
    }

    var data: MutableList<Recipe> = mutableListOf()
    var activeRealm:Realm? = null

    fun init(recipesConfig:SyncConfiguration, weightsConfiguration: SyncConfiguration, trainingConfig: SyncConfiguration ){
        loadRecipes(recipesConfig)
        //WriteWeights(weightsConfig) gli passo la configurazione per i weights
    }

    fun getRecipes():MutableLiveData<List<Recipe>>{
        val mData:MutableLiveData<List<Recipe>> = MutableLiveData<List<Recipe>>()
        mData.value = data
        for(r in data){ Log.v("Recipes", "${r.recipeName}")}
        return mData
    }

    private fun loadRecipes(config: SyncConfiguration){

        Realm.getInstanceAsync(config, object : Realm.Callback(){
            override fun onSuccess(realm: Realm) {
                Log.v("SUCCESS", "Realm successfully opened")
                val recipes = realm.where(Recipe::class.java).findAll()
                for (w in recipes){
                    data.add(w)
                    Log.v("Recipes", "${w.recipeName}")
                }
                activeRealm = realm
            }
        })

    }

    fun onClear(){
        activeRealm?.close()
    }

}