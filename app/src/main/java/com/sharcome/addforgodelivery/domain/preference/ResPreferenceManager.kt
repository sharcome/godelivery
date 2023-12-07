package com.sharcome.addforgodelivery.domain.preference

import android.content.Context
import android.preference.PreferenceManager
import com.sharcome.addforgodelivery.domain.models.AddRestoran.AddModel

class ResPreferenceManager(private val context: Context) {
    companion object {
        val RESTORANT_NAME = "restorant_name"
        val KEY = "key"
        val FOODS = "foods"

    }
    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)


    fun saveResData(model: AddModel){
        with(prefs.edit()){
            putString(RESTORANT_NAME,model.restaurantName)
            putString(KEY,model.key)
            putString(FOODS,model.foods)

        }.apply()

    }

    fun getResKey(): String? = prefs.getString(KEY,null)

}