package com.panic.handler

import android.content.Context
import android.content.SharedPreferences
import java.util.Date

class PanicDataStorage(context: Context) {

    companion object {
        private const val SHARED_PREFS_MODE = "SHARED_PREFS"
        private const val PREF_NAME_SIZE = "panicDateSize"
        private const val PREF_COUNTER_VALUE = "pref_counter_value"
        private const val PREF_NAME_ITEM_ = "panicDateItem_"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(SHARED_PREFS_MODE, Context.MODE_PRIVATE)

    fun addItem(date: Date) {
        val currentList = getList()
        clearList()
        val mutableList = currentList.toMutableList()
        mutableList.add(date)
        saveList(mutableList)
    }

    private fun saveList(list: MutableList<Date>) {
        val editor = prefs.edit()
        editor.putInt(PREF_NAME_SIZE, list.size);

        for (i in list.indices) {
            editor.putLong("${PREF_NAME_ITEM_}$i", list[i].time)
        }

        editor.apply()
    }

    private fun clearList() {
        val size = prefs.getInt(PREF_NAME_SIZE, 0)

        val editor: SharedPreferences.Editor = prefs.edit()
        for (i in 0 until size) {
            editor.remove("${PREF_NAME_ITEM_}$i")
        }

        editor.apply()
    }

    fun getList(): List<Date> {
        val list = mutableListOf<Date>()
        val size = prefs.getInt(PREF_NAME_SIZE, 0)

        for (i in 0 until size) {
            val element = prefs.getLong("${PREF_NAME_ITEM_}$i", 0)
            if (element != 0L) {
                list.add(Date(element))
            }
        }
        return list
    }

    fun increaseCounter() {
        val value = getCounterValue() + 1
        saveValue(value)
    }

    private fun saveValue(value: Int) {
        val editor = prefs.edit()
        editor.putInt(PREF_COUNTER_VALUE, value);
        editor.apply()
    }

    fun resetCounter() {
        saveValue(0)
    }

    fun getCounterValue(): Int {
        return prefs.getInt(PREF_COUNTER_VALUE, 0)
    }
}