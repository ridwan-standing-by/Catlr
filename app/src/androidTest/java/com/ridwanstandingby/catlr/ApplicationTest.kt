package com.ridwanstandingby.catlr

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ApplicationTest {
    @Test
    fun app_installsCorrectly() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.ridwanstandingby.catlr", appContext.packageName)
    }
}