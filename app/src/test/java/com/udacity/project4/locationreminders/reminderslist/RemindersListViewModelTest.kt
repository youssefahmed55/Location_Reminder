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

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setup() {
        stopKoin()
        ds = FakeDataSource()
        ds.reminders = createReminders()
        remindersListViewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(),
            ds)
    }

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

        // WHEN
        remindersListViewModel.loadReminders()

        // THEN
        assert(remindersListViewModel.remindersList.value?.size!! > 0)
    }

    @Test
    fun givenEmptyList_loadReminders_addsZeroRemindersToList() = runBlockingTest {
        // GIVEN
        ds.deleteAllReminders()
        // WHEN
        remindersListViewModel.loadReminders()
        // THEN
        assert(remindersListViewModel.remindersList.value?.size == 0)
    }

    @Test
    fun givenDataSourceError_loadReminders_showsError(){
        // Giving - repo with 2 reminders
        ds.setReturnError(true)
        // When -
        remindersListViewModel.loadReminders()
        // Then -
        val error = remindersListViewModel.showSnackBar.getOrAwaitValue()
        assert(error.contains("Exception"))
    }

    @Test
    fun givenRemindersInList_reminderListEmptied_showNoDataIsTrue() = runBlockingTest {

        // GIVEN
        //reminders in setup fun

        // When
        ds.deleteAllReminders()
        remindersListViewModel.loadReminders()

        // Then - showNoData should be true
        assert(remindersListViewModel.showNoData.value!!)

    }

    @Test
    fun showLoading_() {
        // Given - repo with reminders
        mainCoroutineRule.pauseDispatcher()

        // When - loading reminders
        remindersListViewModel.loadReminders()

        // Then - show loading
        assert(remindersListViewModel.showLoading.getOrAwaitValue() == true)

        // Then - hide loading
        mainCoroutineRule.resumeDispatcher()
        assert(remindersListViewModel.showLoading.getOrAwaitValue() == false)
    }

}