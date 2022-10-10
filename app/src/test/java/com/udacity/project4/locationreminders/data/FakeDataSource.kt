package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var reminders: MutableList<ReminderDTO>? = mutableListOf()) :
    ReminderDataSource {
    //Initialize shouldReturnError
    private var shouldReturnError = false

    fun setReturnError (value: Boolean){
        shouldReturnError = value
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if(shouldReturnError){  //if shouldReturnError is true
            return Result.Error("Exception getReminders")
        }
        return Result.Success(ArrayList(reminders as ArrayList<ReminderDTO>)) //else return Result Success With reminders
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders?.add(reminder)  //add reminder to reminders MutableList
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {

        if(shouldReturnError){   //if shouldReturnError is true
            return Result.Error("Exception getReminder")
        }

        val reminder = reminders?.find { it.id == id }    //Get reminder that equal same id
        return if(reminder != null){                      //if reminder not equal null
            Result.Success(reminder)                      //return Success with reminder
        }else {                                           //else
            Result.Error("Did not find Reminder") //return Error Message
        }
    }

    override suspend fun deleteAllReminders() {
        reminders?.clear() //Clear All Reminders
    }


}