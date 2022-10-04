package com.udacity.project4.utils

import androidx.test.espresso.idling.CountingIdlingResource

object EspressoUtils {
    @JvmField
    val countingIdlingResource = CountingIdlingResource("GLOBAL")

}

inline fun <T> wrapEspressoIdlingResources(function: () -> T): T {
    EspressoUtils.countingIdlingResource.increment() // Set app as busy.
    return try {
        function()
    } finally {
        EspressoUtils.countingIdlingResource.decrement() // Set app as idle.
    }
}