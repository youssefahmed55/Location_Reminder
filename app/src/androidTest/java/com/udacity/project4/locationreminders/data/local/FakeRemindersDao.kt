package com.udacity.project4.locationreminders.data.local

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import java.lang.Exception

class FakeRemindersDao: RemindersDao {

    val reminderServiceData: LinkedHashMap<String, ReminderDTO> = LinkedHashMap()
    var returnError = false
    override suspend fun getReminders(): List<ReminderDTO> {
        if (returnError) {
            throw (Exception("exception"))
        }

        val remindersList = mutableListOf<ReminderDTO>()
        remindersList.addAll(reminderServiceData.values)
        return remindersList
    }

    override suspend fun getReminderById(reminderId: String): ReminderDTO? {
        if (returnError) {
            throw (Exception("exception"))
        }

        reminderServiceData[reminderId]?.let {
            return it
        }
        return null
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminderServiceData[reminder.id] = reminder
    }

    override suspend fun deleteAllReminders() {
        reminderServiceData.clear()
    }

}