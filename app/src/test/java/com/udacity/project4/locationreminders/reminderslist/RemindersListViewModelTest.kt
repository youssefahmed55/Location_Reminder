package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher

import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    private lateinit var remindersListViewModel: RemindersListViewModel
    private lateinit var ds: FakeDataSource

    // Initialize instantExecutorRule
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Initialize mainCoroutineRule
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setup() {
        stopKoin()
        ds = FakeDataSource()            // Initialize ds (FakeDataSource)
        ds.reminders = createReminders() // set reminders to ds (FakeDataSource)
        remindersListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), ds)  // Initialize remindersListViewModel
    }

    //Create Reminders to Save them in ds (FakeDataSource)
    private fun createReminders(): MutableList<ReminderDTO> {

        val reminder1 = ReminderDTO("TestTitle1","TestDescirption1",
            "TestLocation",
            0.0,0.0)

        val reminder2 = ReminderDTO("TestTitle2","TestDescirption2",
            "TestLocation2",
            0.0,0.0)

        return mutableListOf(reminder1, reminder2)
    }

    @Test
    fun givenRemindersToLoad_loadReminders_addsRemindersToReminderList()= runBlockingTest {
        // GIVEN
        //reminders in setup fun

        // WHEN load Reminders
        remindersListViewModel.loadReminders()

        // THEN Check Size Of remindersList is > 0
        assert(remindersListViewModel.remindersList.value?.size!! > 0)
    }

    @Test
    fun givenEmptyList_loadReminders_addsZeroRemindersToList() = runBlockingTest {
        // GIVEN Deleted Reminders
        ds.deleteAllReminders()
        // WHEN load Reminders
        remindersListViewModel.loadReminders()
        // THEN Check Size Of remindersList is 0
        assert(remindersListViewModel.remindersList.value?.size == 0)
    }

    @Test
    fun givenDataSourceError_loadReminders_showsError(){
        // Giving - repo with 2 reminders
        ds.setReturnError(true) //Set return Error true
        // When load Reminders
        remindersListViewModel.loadReminders()
        // Then Show Error in Snack Bar
        val error = remindersListViewModel.showSnackBar.getOrAwaitValue()
        assert(error.contains("Exception"))
    }

    @Test
    fun givenRemindersInList_reminderListEmptied_showNoDataIsTrue() = runBlockingTest {

        // GIVEN
        //reminders in setup fun

        // When Delete All Reminders
        ds.deleteAllReminders()
        remindersListViewModel.loadReminders()

        // Then show No Data is true
        assert(remindersListViewModel.showNoData.value!!)

    }

    @Test
    fun showLoading_() {
        // Given repo with reminders
        mainCoroutineRule.pauseDispatcher()

        // When loading reminders
        remindersListViewModel.loadReminders()

        // Then show loading
        assert(remindersListViewModel.showLoading.getOrAwaitValue() == true)

        // Then hide loading
        mainCoroutineRule.resumeDispatcher()
        assert(remindersListViewModel.showLoading.getOrAwaitValue() == false)
    }

}