package com.valendev.fasttask.context

import android.content.Context
import com.valendev.fasttask.domain.constants.CONTEXT_PREFERENCE
import com.valendev.fasttask.domain.constants.KEY_FIRST_LAUNCH
import com.valendev.fasttask.domain.constants.KEY_USER_CONTEXT

class PreferenceManager(private val context: Context)
{

    private val sharedPreferences = context.getSharedPreferences(CONTEXT_PREFERENCE, Context.MODE_PRIVATE)

    // Metod para acceder a los contextos
    fun isFirstTimeLaunch(): Boolean{
        return sharedPreferences.getBoolean(KEY_FIRST_LAUNCH, false)
    }

    fun setFirstTimeLaunch(value: Boolean){
        val edit = sharedPreferences.edit()
        edit.putBoolean(KEY_FIRST_LAUNCH, value)
        edit.apply()
    }

    fun setUser(id: Int){
        val edit = sharedPreferences.edit()
        edit.putInt(KEY_USER_CONTEXT, id)
        edit.apply()
    }

    fun getIdUser(): Int{
        return sharedPreferences.getInt(KEY_USER_CONTEXT, 0)
    }

}