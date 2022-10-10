package com.udacity.project4

import android.app.Application
import android.os.Bundle
import android.os.IBinder
import android.view.WindowManager
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.Root
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.savereminder.SaveReminderFragment
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.monitorActivity
import com.udacity.project4.util.monitorFragment
import com.udacity.project4.utils.EspressoUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get

@RunWith(AndroidJUnit4::class)
@LargeTest
//END TO END test to black box test the app
class RemindersActivityTest : AutoCloseKoinTest() {

    private lateinit var repo: ReminderDataSource


    /**
     * Initialize Koin to inject dependencies for our test code, just as we did in production code
     */

    @Before
    fun init() {
        stopKoin()   //stop koin

        // Setup koin module
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    getApplicationContext(),
                    get() as ReminderDataSource
                )
            }
            single {
                SaveReminderViewModel(
                    getApplicationContext(),
                    get() as ReminderDataSource
                )
            }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(getApplicationContext()) }
        }

        // start koin using above declared module
        startKoin {
            modules(listOf(myModule))
        }
        //Get our real repository
        repo = get()

        //clear the data to start fresh
        runBlocking {
            repo.deleteAllReminders()
        }
    }


    /**
     * Idling resources tell Espresso that the app is idle or busy. This is needed when operations
     * are not scheduled in the main Looper (for example when executed on a different thread).
     */
    //Initialize dataBindingIdlingResource
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoUtils.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoUtils.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }
    //fun to get Reminder Data
    private fun getReminder(): ReminderDTO {
        return ReminderDTO(
            title = "title",
            description = "description",
            location = "location",
            latitude = 0.0,
            longitude = 0.0
        )
    }

    @Test
    fun givenReminderInDb_remindersActivityLaunched_reminderOnScreen() = runBlocking {
        // GIVEN - reminder in db
        val reminder = getReminder()  //Initialize reminder
        repo.saveReminder(reminder)   //Save Reminder

        // WHEN - RemindersActivity is launched
        val scenario = ActivityScenario.launch(RemindersActivity::class.java) //Initialize scenario to launch Reminder Activity
        dataBindingIdlingResource.monitorActivity(scenario)   //Sets Reminder Activity Used From dataBindingIdlingResource

        // THEN - reminder is visible on screen
        Espresso.onView(ViewMatchers.withText(reminder.title)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))        //Check Reminder Title Appears on Screen
        Espresso.onView(ViewMatchers.withText(reminder.description)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))  //Check description Appears on Screen
        Espresso.onView(ViewMatchers.withText(reminder.location)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))     //Check location Appears on Screen

        // Delay 2 sec
        delay(2000)

    }

    @Test
    fun givenSaveReminderFragment_saveEmptyReminder_showsSnackbarError() { runBlocking {

        // GIVEN - saveReminderFragment launched
        val scenario = launchFragmentInContainer<SaveReminderFragment>(Bundle(), R.style.AppTheme)  //Initialize scenario to launch SaveReminderFragment
        dataBindingIdlingResource.monitorFragment(scenario) //Sets SaveReminderFragment Used From dataBindingIdlingResource

        // WHEN - save empty reminder
        Espresso.onView(ViewMatchers.withId(R.id.saveReminder)).perform(ViewActions.click())  //Click On Floating Button to Save Reminder

        // THEN - snackbar error is visible
        Espresso.onView(ViewMatchers.withId(com.google.android.material.R.id.snackbar_text)).check(ViewAssertions.matches(ViewMatchers.withText(R.string.err_enter_title))) //Check Snack Bar Appears with Same Message

        // Delay 2 sec
        delay(3000)

    }
    }

    @Test
    fun givenRemindersActivityLaunched_createAndSaveReminder_reminderOnScreen() = runBlocking {

        // GIVEN - reminders activity is launched
        val scenario = ActivityScenario.launch(RemindersActivity::class.java)  //Initialize scenario to launch Reminder Activity
        dataBindingIdlingResource.monitorActivity(scenario)                    //Sets Reminder Activity Used From dataBindingIdlingResource

        // WHEN - create and save reminder
        Espresso.onView(ViewMatchers.withId(R.id.noDataTextView)).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))) //Check if noDataTextView Appears On Screen
        Espresso.onView(ViewMatchers.withId(R.id.addReminderFAB)).perform(ViewActions.click())  //Click on Add Reminder Floating Button
        Espresso.onView(ViewMatchers.withId(R.id.reminderTitle)).perform(ViewActions.typeText("youssef")) //Write title in First EditText
        Espresso.onView(ViewMatchers.withId(R.id.reminderDescription)).perform(ViewActions.typeText("ahmed")) //Write description in Second EditText
        Espresso.closeSoftKeyboard() //Close or Hide Keyboard
        Espresso.onView(ViewMatchers.withId(R.id.selectLocation)).perform(ViewActions.click()) //Click on Select Location
        // Delay 5 sec
        delay(5000)
        Espresso.onView(ViewMatchers.withId(R.id.map)).perform(ViewActions.longClick())  //Click Long Click in Screen To Select Location

        Espresso.onView(ViewMatchers.withId(R.id.save_location)).perform(ViewActions.click()) //Click On Save Button
        // Delay 2 sec
        delay(2000)

        // click on the Save Reminder Floating button
        Espresso.onView(ViewMatchers.withId(R.id.saveReminder)).perform(ViewActions.click())

        // THEN - Check reminder is visible on screen with toast
        Espresso.onView(ViewMatchers.withText(R.string.reminder_saved)).inRoot(ToastMatcher()).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // Delay 2 sec
        delay(2000)

    }
}

class ToastMatcher : TypeSafeMatcher<Root?>() {
    override fun describeTo(description: Description) {
        description.appendText("is toast")
    }

    override fun matchesSafely(root: Root?): Boolean {
        if (root != null) {
            val type: Int = root.windowLayoutParams.get().type
            if (type == WindowManager.LayoutParams.TYPE_TOAST) {
                val windowToken: IBinder = root.decorView.windowToken
                val appToken: IBinder = root.decorView.applicationWindowToken
                if (windowToken === appToken) {
                    //means this window isn't contained by any other windows.
                    return true
                }
            }
        }
        return false
    }
}