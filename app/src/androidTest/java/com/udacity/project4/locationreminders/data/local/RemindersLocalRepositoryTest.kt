package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    //Initialize instantExecutorRule
    @get:Rule
    var instantExecutorRule=InstantTaskExecutorRule()


    private lateinit var remindersDaoo: FakeRemindersDao
    private lateinit var remindersRepository: RemindersLocalRepository

    @Before
    fun setupRepo() {
        remindersDaoo = FakeRemindersDao()       //Initialize remindersDaoo (FakeRemindersDao)
        remindersRepository = RemindersLocalRepository(remindersDaoo, Dispatchers.Unconfined) //Initialize remindersRepository

    }

    @Test
    fun givenReminders_getReminderById_returnsCorrectReminder() =runBlocking {
        // Given
        val r1 = ReminderDTO("t1", "d1", "l1", 0.0, 0.0)
        val r2 = ReminderDTO("t2", "d2", "l2", 0.0, 0.0)

        remindersDaoo.saveReminder(r1)  //Save r1
        remindersDaoo.saveReminder(r2)  //Save r2

        // When
        val lr1 = remindersRepository.getReminder(r1.id) //Get reminder by Id
        val lr2 = remindersRepository.getReminder(r2.id) //Get reminder by Id

        lr1 as Result.Success
        lr2 as Result.Success

        // Then lr1
        assertThat(lr1, Matchers.`is`(Matchers.notNullValue()))     //Check if lr1 not null value
        assertThat(lr1.data.location, Matchers.`is`(r1.location))   //Check if lr1 location equal r1 location
        assertThat(lr1.data.title, Matchers.`is`(r1.title))         //Check if lr1 title equal r1 title
        assertThat(lr1.data.latitude, Matchers.`is`(r1.latitude))   //Check if lr1 latitude equal r1 latitude
        assertThat(lr1.data.longitude, Matchers.`is`(r1.longitude)) //Check if lr1 longitude equal r1 longitude

        // Then lr2
        assertThat(lr2, Matchers.`is`(Matchers.notNullValue()))     //Check if lr2 not null value
        assertThat(lr2.data.location, Matchers.`is`(r2.location))   //Check if lr2 location equal r2 location
        assertThat(lr2.data.title, Matchers.`is`(r2.title))         //Check if lr2 title equal r2 title
        assertThat(lr2.data.latitude, Matchers.`is`(r2.latitude))   //Check if lr2 latitude equal r2 latitude
        assertThat(lr2.data.longitude, Matchers.`is`(r2.longitude)) //Check if lr2 longitude equal r2 longitude
    }



    @Test
    fun givenEmpty_getReminderById_returnsReminderNotFoundError() = runBlocking {

        // GIVEN
        val r1 = ReminderDTO("t1", "desc1", "loc1", 0.0, 0.0)
        remindersDaoo.deleteAllReminders() //Delete All Reminders

        // WHEN
        val lrr = remindersRepository.getReminder(r1.id) //Get reminder by Id
        lrr as Result.Error

        // THEN Returns Reminder not found
        assertThat(lrr.message, Matchers.`is`("Reminder not found!"))
    }

    @Test
    fun givenReminders_deleteAllReminders_deletesRemindersInDb() =runBlocking {
        // GIVEN
        val r1 = ReminderDTO("t1", "desc1", "loc1", 0.0, 0.0)
        val r2 = ReminderDTO("t2", "desc2", "loc2", 0.0, 0.0)

        remindersDaoo.saveReminder(r1)    //Save/Add r1
        remindersDaoo.saveReminder(r2)    //Save/Add r2

        // WHEN
        remindersDaoo.deleteAllReminders() //Delete All Reminders

        val lr1 = remindersRepository.getReminder(r1.id) //Get reminder by Id
        val lr2 = remindersRepository.getReminder(r2.id) //Get reminder by Id
        lr1 as Result.Error
        lr2 as Result.Error


        // THEN Returns Reminder not found
        assertThat(lr1.message, Matchers.`is`("Reminder not found!"))
        assertThat(lr2.message, Matchers.`is`("Reminder not found!"))
    }


}