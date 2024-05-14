package com.rendox.grocerygenius

import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until

/**
 * Waits until an object with [selector] if visible on screen and returns the object.
 * If the element is not available in [timeout], throws [AssertionError]
 */
fun UiDevice.waitAndFindObject(
    selector: BySelector,
    timeout: Long,
    throwTimeOutException: Boolean = true,
): UiObject2 {
    if (!wait(Until.hasObject(selector), timeout) && throwTimeOutException) {
        throw AssertionError("Element not found on screen in ${timeout}ms (selector=$selector)")
    }
    return findObject(selector)
}

fun UiDevice.flingElementDownUp(element: UiObject2) {
    // Set some margin from the sides to prevent triggering system navigation
    element.setGestureMargin(displayWidth / 5)

    element.fling(Direction.DOWN)
    waitForIdle()
    element.fling(Direction.UP)
}
