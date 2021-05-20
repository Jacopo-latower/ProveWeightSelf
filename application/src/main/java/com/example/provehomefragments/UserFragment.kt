package com.example.provehomefragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.user_frag_layout.*

interface UserFragObserver{
    fun userFragCreatedNotify()
}

class UserFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.user_frag_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val observer = activity as? UserFragObserver
        observer?.userFragCreatedNotify()

        btnLogout.setOnClickListener { logout() }
    }

}

private fun logout(){
   //TODO: pass the User to the intent in the Login Phase to avoid access problems
}