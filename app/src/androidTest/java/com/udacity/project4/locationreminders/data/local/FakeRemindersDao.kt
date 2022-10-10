package com.udacity.project4.locationreminders.data.local

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import java.lang.Exception

class FakeRemindersDao: RemindersDao {

    val reminderServiceData: LinkedHashMap<String, ReminderDTO> = LinkedHashMap()    //Initialize reminderServiceData
    var returnError = false //Initialize returnError
    override suspend fun getReminders(): List<ReminderDTO> {
        if (returnError) { //If returnError is true
            throw (Exception("exception")) //Throw Exception
        }

        val remindersList = mutableListOf<ReminderDTO>()
        remindersList.addAll(reminderServiceData.values) //add All Reminders
        return remindersList  //Return remindersList
    }

    override suspend fun getReminderById(reminderId: String): ReminderDTO? {
        if (returnError) {  //If returnError is true
            throw (Exception("exception")) //Throw Exception
        }

        reminderServiceData[reminderId]?.let {  //if reminderServiceData not equal null
            return it       //Return reminderServiceData
        }
        return null         //Return null
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminderServiceData[reminder.id] = reminder  //Save reminder
    }

    override suspend fun deleteAllReminders() {
        reminderServiceData.clear() //Clear/Delete All Reminders
    }

}