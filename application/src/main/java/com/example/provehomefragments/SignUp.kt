package com.example.provehomefragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import io.realm.mongodb.App
import io.realm.mongodb.AppConfiguration
import io.realm.mongodb.Credentials
import io.realm.mongodb.User
import io.realm.mongodb.mongo.MongoClient
import io.realm.mongodb.mongo.MongoCollection
import io.realm.mongodb.mongo.MongoDatabase
import kotlinx.android.synthetic.main.login.*
import kotlinx.android.synthetic.main.signup.*
import org.bson.Document
import org.bson.types.ObjectId

class SignUp : Fragment() {

    companion object{
        fun createInstance() = SignUp()
    }

    lateinit var app: App
    var possibleUser: MyUser? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.signup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val appId:String = "prova_weightself-jnubd"
        app = App(AppConfiguration.Builder(appId).build())

        save_register?.setOnClickListener {
            val newUser= MyUser(email_register?.text.toString(), password_register?.text.toString())
            newUser.height = height_register?.text.toString()
            newUser.name = name_register?.text.toString()
            newUser.surname = surname_register?.text.toString()
            possibleUser = newUser
            if(password_register.text.toString() == passwordC_register.text.toString())
                addNewUser(possibleUser!!)
            else{
                Log.i("Ex", "CONFIRMATION PASSWORD NOT EQUAL TO PASSWORD: ${passwordC_register.text} different from ${password_register.text} ")
            }
        }

    }

    private fun addNewUser(u:MyUser){
        app.emailPassword.registerUserAsync(u.email, u.password){
            if (it.isSuccess){
                Log.i("Ex", "Registration Successful")

                val creds : Credentials = Credentials.emailPassword(u.email, u.password)
                var user: User? = null

                //Logging in temporarily to write the custom user data document of the new user
                app.loginAsync(creds){

                    if(it.isSuccess){
                        Log.v("AUTH", "Login Successful")
                        user = app.currentUser()
                        val mongoClient : MongoClient =
                            user?.getMongoClient("mongodb-atlas")!! // service for MongoDB Atlas cluster containing custom user data
                        val mongoDatabase : MongoDatabase =
                            mongoClient.getDatabase("MyDatabase")!!
                        val mongoCollection : MongoCollection<Document> =
                            mongoDatabase.getCollection("custom_user_data")!!
                        mongoCollection.insertOne(
                            Document("my_user_id", user!!.id)
                                .append("_id", ObjectId(user!!.id))
                                .append("height", u.height)
                                .append("name", u.name)
                                .append("surname", u.surname)
                                .append("_partitionkey", "user"))
                            .getAsync { result ->
                                if (result.isSuccess) {
                                    Log.v("EXAMPLE", "Inserted custom user data document. _id of inserted document: ${result.get().insertedId}")
                                    //Logging Out once we've written the custom user data
                                    user?.logOutAsync{res->
                                        if (res.isSuccess) {
                                            Log.v("AUTH", "Successfully logged out.")
                                            val observer = activity as LogFragmentObserver
                                            observer.replaceFragment(Login.createInstance())
                                        } else {
                                            Log.e("AUTH", res.error.toString())
                                        }}
                                } else {
                                    Log.e("EXAMPLE", "Unable to insert custom user data. Error: ${result.error}")

                                    //Logging Out once we've written the custom user data
                                    user?.logOutAsync{r->
                                        if (r.isSuccess) {
                                            Log.v("AUTH", "Successfully logged out.")
                                        } else {
                                            Log.e("AUTH", r.error.toString())
                                        }}
                                }
                            }
                    }else{
                        //IN CASE OF ERROR IN THE LOGIN PHASE: Set here the alert to warn the user
                        Log.e("AUTH", "Error in the login: ${it.error}")
                    }
                }
            }else{
                //IN CASE OF ERROR IN THE SIGNUP PHASE
                Log.e("EX", "Failed to register user: ${it.error}")
            }
        }
    }

}