package com.example.provehomefragments

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import io.realm.Realm
import io.realm.mongodb.App
import io.realm.mongodb.AppConfiguration
import io.realm.mongodb.User
import io.realm.mongodb.mongo.MongoDatabase
import io.realm.mongodb.sync.SyncConfiguration
import org.bson.types.ObjectId
import org.w3c.dom.Document
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

//Observer implemented by all tehf ragments to observe the data loading
interface RepositoryAsyncTaskObserver{
    fun onAsyncLoadingFinished()
}

class RepositoryManager {

    private lateinit var weightConfig : SyncConfiguration
    private lateinit var recipeConfig : SyncConfiguration
    private lateinit var trainingConfig : SyncConfiguration


    companion object{
        val instance = RepositoryManager()
    }

    lateinit var currentUser : User

    var dataRecipes : MutableList<Recipe> = mutableListOf()
    var dataTrainings : MutableList<Trainings> = mutableListOf()
    var dataWeights : MutableList<Weights> = mutableListOf()
    var recipeRealm : Realm? = null
    var trainingRealm : Realm? = null
    var weightRealm : Realm ? = null

    fun init(recipesConfig : SyncConfiguration, weightConfig : SyncConfiguration, trainingConfig : SyncConfiguration){

        this.weightConfig = weightConfig
        this.recipeConfig = recipesConfig
        this.trainingConfig = trainingConfig

        val appId : String = "prova_weightself-jnubd"
        val app = App(AppConfiguration.Builder(appId).build())
        currentUser = app.currentUser()!!
        Log.v("EX", "In repository manager custom data called: ${currentUser.customData}")
    }

    fun loadTrainings(fragment: RepositoryAsyncTaskObserver) {
        Realm.getInstanceAsync(trainingConfig, object : Realm.Callback(){
            override fun onSuccess(realm: Realm) {
                Log.v("SUCCESS", "Training Realm successfully opened")
                val trainings = realm.where(Trainings::class.java).findAll()
                dataTrainings = mutableListOf()
                for (w in trainings){
                    dataTrainings.add(w)
                    Log.v("Trainings", "${w.trainingName}")
                }
                trainingRealm = realm
                fragment.onAsyncLoadingFinished()
            }
        })
    }

    fun loadRecipes(fragment: RepositoryAsyncTaskObserver){
        Realm.getInstanceAsync(recipeConfig, object : Realm.Callback(){
            override fun onSuccess(realm: Realm) {
                Log.v("SUCCESS", "Recipe Realm successfully opened")
                val recipes = realm.where(Recipe::class.java).findAll()
                dataRecipes = mutableListOf()
                for (w in recipes){
                    dataRecipes.add(w)
                    Log.v("Recipes", "${w.recipeName}")
                }
                recipeRealm = realm
                fragment.onAsyncLoadingFinished()
            }
        })
    }

    fun loadWeights(fragment: RepositoryAsyncTaskObserver) {
        //TODO: to implement if there are weights in the database; remember to do a refresh method when a user insert a new weight
        Realm.getInstanceAsync(weightConfig, object : Realm.Callback(){
            override fun onSuccess(realm: Realm) {
                val weights = realm.where(Weights::class.java).findAll()
                dataWeights = weights
                weightRealm = realm
                fragment.onAsyncLoadingFinished()
            }
        })

    }

    fun loadUserData() : org.bson.Document{
        Log.v("USER", "${currentUser.customData["name"]}")
        return currentUser.customData
    }

    fun getRecipes() : MutableLiveData<List<Recipe>>{
        val mData:MutableLiveData<List<Recipe>> = MutableLiveData<List<Recipe>>()
        mData.value = dataRecipes
        for(r in dataRecipes){ Log.v("Recipes", "${r.recipeName}")}
        return mData
    }

    fun getTrainings(): MutableLiveData<List<Trainings>> {
        val mData:MutableLiveData<List<Trainings>> = MutableLiveData<List<Trainings>>()
        mData.value = dataTrainings
        for(r in dataTrainings){ Log.v("Trainings", "${r.trainingName}")}
        return mData
    }

    fun writeWeight(peso: String) {

        Realm.getInstanceAsync(weightConfig, object : Realm.Callback() {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onSuccess(realm : Realm) {
                Log.v("EXAMPLE", "Successfully opened a realm with reads and writes allowed on the UI thread.")

                /*
                var formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                var date = LocalDate.parse(System.currentTimeMillis().toString(), formatter)

                Log.v("DATA", "Oggi è il $date")
                */

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

                    /*
                    if(r.){

                    }

                    else { r.insertOrUpdate(newWeight) }

                     */
                    r.insertOrUpdate(newWeight)
                    /*

                    val weights = r.where(Weights::class.java).equalTo("date", date).findAll()

                    for(w in weights){
                        Log.v("EX", "Weight found: ${w.weight}")
                    }

                    */
                }
                weightRealm = realm
            }
        })
    }

    fun onClear(){
        recipeRealm?.close()
        trainingRealm?.close()
    }

}