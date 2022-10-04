package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest: AutoCloseKoinTest() {

    private lateinit var repo: ReminderDataSource
    private lateinit var context: Application

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()


    @Before
    fun init(){
        stopKoin()
        context = getApplicationContext()

        // Setup Koin - same as in MyApp module
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    context,
                    get() as ReminderDataSource
                )
            }
            single {
                SaveReminderViewModel(
                    get(),
                    get() as ReminderDataSource
                )
            }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(context) }
        }

        startKoin {
            androidContext(context)
            modules(listOf(myModule))
        }

        //Get our real repository
        repo = get()

        //clear the data to start fresh
        runBlocking {
            repo.deleteAllReminders()
        }
    }

    @Test
    fun givenData_reminderListFragmentOnScreen_dataIsDisplayed() {
        runBlocking {
            // GIVEN
            val r = ReminderDTO("t1","d1",
                "l1",
                0.0,0.0)

            repo.saveReminder(r)

            // WHEN
            launchFragmentInContainer<ReminderListFragment>(null,R.style.AppTheme)

            // THEN
            onView(ViewMatchers.withText(r.title)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            onView(ViewMatchers.withText(r.description)).check(
                ViewAssertions.matches(
                    ViewMatchers.isDisplayed()
                )
            )
            onView(ViewMatchers.withText(r.location)).check(
                ViewAssertions.matches(
                    ViewMatchers.isDisplayed()
                )
            )
        }
    }

    @Test
    fun givenReminderListScreen_clickFAB_navigatesToSaveReminderScreen(){
        // GIVEN
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navigationController = mock(NavController::class.java)

        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navigationController)
        }

        // WHEN
        onView(withId(R.id.addReminderFAB)).perform(click())

        // THEN
        verify(navigationController).navigate(ReminderListFragmentDirections.toSaveReminder())

    }
}