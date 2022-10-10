package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    // Executes each task synchronously using Architecture Components.

    //Initialize instantExecutorRule
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    //Initialize mainCoroutineRule
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    lateinit var saveReminderViewModel: SaveReminderViewModel

    @Before
    fun setup(){
        stopKoin()
    }

    @Test
    fun givenDataIsNull_validateData_returnsFalse(){
        // GIVEN
        val r: ReminderDataItem? = null     //Initialize r (ReminderDataItem) equal null
        saveReminderViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), FakeDataSource()) //Initialize saveReminderViewModel

        // WHEN Valid Entered Data
        val isValidated = saveReminderViewModel.validateEnteredData(r)

        // THEN It Returns False
        assert(!isValidated!!)

    }

    @Test
    fun givenDataLocIsNull_validateData_returnsFalse(){
        // GIVEN
        val reminders = mutableListOf<ReminderDTO>() //Initialize reminders (mutableListOf<ReminderDTO>)
        val reminder = ReminderDataItem("TestTitle", "TestDescription", null, 0.0, 0.0, "testId") //Initialize reminder
        saveReminderViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), FakeDataSource(reminders)) //Initialize saveReminderViewModel

        // WHEN Valid Entered Data
        val isValidat = saveReminderViewModel.validateEnteredData(reminder)

        // THEN It Returns False
        assert(!isValidat)

    }

    @Test
    fun givenDataTitleIsNull_validateData_returnsFalse(){
        // GIVEN
        val reminders = mutableListOf<ReminderDTO>()  //Initialize reminders (mutableListOf<ReminderDTO>)
        val reminder = ReminderDataItem(null, "descriptionTest", "loc", 0.0, 0.0, "idTest") //Initialize reminder
        saveReminderViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), FakeDataSource(reminders)) //Initialize saveReminderViewModel
        // WHEN Valid Entered Data
        val isValidat = saveReminderViewModel.validateEnteredData(reminder)
        // THEN It Returns False
        assert(!isValidat)

    }

    @Test
    fun givenValidData_validateData_returnsTrue(){
        // GIVEN
        val reminders = mutableListOf<ReminderDTO>() //Initialize reminders (mutableListOf<ReminderDTO>)
        val reminder = ReminderDataItem(null, "descriptionTest", "loc", 0.0, 0.0, "idTest") //Initialize reminder
        saveReminderViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), FakeDataSource(reminders)) //Initialize saveReminderViewModel

        // WHEN Valid Entered Data
        val isValidat = saveReminderViewModel.validateEnteredData(reminder)

        // THEN It Returns true
        assert(!isValidat)

    }

    @Test
    fun givenReminder_saveReminder_returnsTrue() {

        // GIVEN
        val reminders = mutableListOf<ReminderDTO>() //Initialize reminders (mutableListOf<ReminderDTO>)
        val reminder = ReminderDataItem(null, "descriptionTest", "loc", 0.0, 0.0, "idTest") //Initialize reminder
        saveReminderViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), FakeDataSource(reminders)) //Initialize saveReminderViewModel

        // WHEN Save Reminder
        saveReminderViewModel.saveReminder(reminder)

        // THEN Return True
        val hasSaved = reminders[0].id == "idTest"
        assert(hasSaved)

    }

}