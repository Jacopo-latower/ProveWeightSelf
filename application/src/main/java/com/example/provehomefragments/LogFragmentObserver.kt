package com.example.provehomefragments

import androidx.fragment.app.Fragment

interface LogFragmentObserver {
    fun replaceFragment(f: Fragment)
    fun loadNextActivity()
}