package com.example.provehomefragments

import android.util.Log
import androidx.lifecycle.MutableLiveData
import io.realm.Realm
import io.realm.mongodb.App
import io.realm.mongodb.AppConfiguration
import io.realm.mongodb.User
import io.realm.mongodb.sync.SyncConfiguration
import org.bson.types.ObjectId
import java.util.*

class RepositoryManager {

    lateinit var weightConfig : SyncConfiguration

    companion object{
        val instance = RepositoryManager()
    }

    lateinit var currentUser : User

    var peso : String? = null
    var data : MutableList<Recipe> = mutableListOf()
    var activeRealm : Realm? = null

    fun init(recipesConfig : SyncConfiguration, weightConfig : SyncConfiguration, trainingConfig : SyncConfiguration){
        loadRecipes(recipesConfig)
        readWeights(weightConfig)
        //loadTrainings(trainingConfig)

        this.weightConfig = weightConfig

        val appId : String = "prova_weightself-jnubd"
        val app = App(AppConfiguration.Builder(appId).build())
        currentUser = app.currentUser()!!
    }

    private fun readWeights(weightConfig: SyncConfiguration) {

    }

    fun getRecipes() : MutableLiveData<List<Recipe>>{
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

    fun writeWeight(peso: String) {

        Realm.getInstanceAsync(weightConfig, object : Realm.Callback() {
            override fun onSuccess(realm : Realm) {
                Log.v("EXAMPLE", "Successfully opened a realm with reads and writes allowed on the UI thread.")

                realm.executeTransaction{ r : Realm ->
                    val id = ObjectId()
                    val partitionKey = "weights"
                    val date : Date = Date(System.currentTimeMillis())
                    val newWeight = Weights()

                    newWeight._id = id
                    newWeight._partitionkey = partitionKey
                    newWeight.weight = peso.toDouble()
                    newWeight.user_id = currentUser.id
                    newWeight.date = date

                    r.insertOrUpdate(newWeight)

                    /*

                    val weights = r.where(Weights::class.java).equalTo("date", date).findAll()

                    for(w in weights){
                        Log.v("EX", "Weight found: ${w.weight}")
                    }

                    */
                }
                realm.close()
            }
        })
    }

    fun onClear(){
        activeRealm?.close()
    }

}