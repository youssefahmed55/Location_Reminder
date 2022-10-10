package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import androidx.test.platform.app.InstrumentationRegistry
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test
import java.io.IOException
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    //Initialize instantExecutorRule
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var remindersDaoo: RemindersDao
    private lateinit var dataBase: RemindersDatabase

    @Before
    fun setupDb(){
        val context = InstrumentationRegistry.getInstrumentation().context  //Initialize context
        dataBase = Room.inMemoryDatabaseBuilder(context, RemindersDatabase::class.java).allowMainThreadQueries().build() //Initialize dataBase
        remindersDaoo = dataBase.reminderDao() //Initialize remindersDaoo
    }

    @After
    @Throws(IOException::class)
    fun tearDownDB(){
        dataBase.close() //close RoomDataBase
    }

    @Test
    @Throws(Exception::class)
    fun givenReminderWithId_getReminderById_returnsCorrectReminder() = runBlockingTest{
        // GIVEN insert a reminder
        val reminderId = UUID.randomUUID().toString()  //Initialize reminderId
        val reminder = ReminderDTO("Title","Desc", "Location", 0.0,0.0, reminderId)  //Initialize reminder

        remindersDaoo.saveReminder(reminder) //Save reminder

        // WHEN Get the reminder by id from the database
        val loaded = remindersDaoo.getReminderById(reminderId) //get Reminder by id
        val loadedFromRandId = remindersDaoo.getReminderById(UUID.randomUUID().toString()) //get Reminder by Random Id

        // THEN The loaded data contains the expected values
        assertThat(loaded as ReminderDTO, notNullValue())            //Check if loaded Not Equal Null Value
        assertThat(loaded.id, `is`(reminder.id))                     //Check if loaded id Equal reminder id
        assertThat(loaded.description, `is`(reminder.description))   //Check if loaded description Equal reminder description
        assertThat(loaded.title, `is`(reminder.title))               //Check if title description Equal reminder title
        assertThat(loaded.latitude, `is`(reminder.latitude))         //Check if latitude description Equal reminder latitude
        assertThat(loaded.longitude, `is`(reminder.longitude))       //Check if loaded longitude Equal reminder longitude
        assertThat(loadedFromRandId, `is`(CoreMatchers.nullValue())) //Check if loadedFromRandId Equal Null Value
    }

    @Test
    fun givenReminders_getReminders_returnsRemindersInDb() = runBlockingTest {
        // GIVEN - three reminders inserted in db
        val r1 = ReminderDTO("Title1","Desc1", "Location1", 0.0,0.0)
        val r2 = ReminderDTO("Title2","Desc2", "Location2", 0.0,0.0)
        val r3 = ReminderDTO("Title3","Desc3", "Location3", 0.0,0.0)

        remindersDaoo.saveReminder(r1)  //Save r1
        remindersDaoo.saveReminder(r2)  //Save r2
        remindersDaoo.saveReminder(r3)  //Save r3

        // WHEN getReminders()
        val loadedReminders = remindersDaoo.getReminders()

        // THEN returns the reminders in the db
        assertThat(loadedReminders.count(), `is`(3))                    //Check loadedReminders count is 3
        assertThat(loadedReminders.first().title, `is`(r1.title))             //Check title of First Reminder in loadedReminders count is r1 title
        assertThat(loadedReminders.first().location, `is`(r1.location))       //Check location of First Reminder in loadedReminders count is r1 location
        assertThat(loadedReminders.first().description, `is`(r1.description)) //Check description of First Reminder in loadedReminders count is r1 description

        assertThat(loadedReminders.last().title, `is`(r3.title))              //Check title of Last Reminder in loadedReminders count is r3 title
        assertThat(loadedReminders.last().location, `is`(r3.location))        //Check location of Last Reminder in loadedReminders count is r3 location
        assertThat(loadedReminders.last().description, `is`(r3.description))  //Check description of Last Reminder in loadedReminders count is r3 description

    }

    @Test
    fun givenReminders_deleteReminders_remindersCountReturnsZero() = runBlockingTest {
        // GIVEN three reminders
        val r1 = ReminderDTO("Title1","Desc1", "Location1", 0.0,0.0)
        val r2 = ReminderDTO("Title2","Desc2", "Location2", 0.0,0.0)
        val r3 = ReminderDTO("Title3","Desc3", "Location3", 0.0,0.0)


        remindersDaoo.saveReminder(r1)   //Save r1
        remindersDaoo.saveReminder(r2)   //Save r2
        remindersDaoo.saveReminder(r3)   //Save r3

        // WHEN deleteAllReminders()
        remindersDaoo.deleteAllReminders()

        // THEN check the database is empty, It Returns Zero
        val loadedReminders = remindersDaoo.getReminders()
        assertThat(loadedReminders.count(), `is`(0))

    }

}