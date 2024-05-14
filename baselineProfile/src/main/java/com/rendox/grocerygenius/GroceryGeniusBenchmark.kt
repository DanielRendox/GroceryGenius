package com.rendox.grocerygenius

import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class GroceryGeniusBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun benchmark() = benchmarkRule.measureRepeated(
        packageName = "com.rendox.grocerygenius",
        metrics = listOf(FrameTimingMetric()),
        iterations = 1,
        startupMode = StartupMode.WARM,
        setupBlock = {
            pressHome()
            startActivityAndWait()
        },
    ) {
        val groceryListName = "TestGroceryList"
        createNewGroceryList(groceryListName)
        navigateToCategory()
        addGroceries()
        device.pressBack()
        device.waitForIdle()
        device.pressBack()
        navigateToSettings()
        device.pressBack()
        navigateToGroceryList(groceryListName)
        device.waitForIdle()
        deleteGroceryList()
        device.waitForIdle()
    }
}