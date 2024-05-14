package com.rendox.grocerygenius

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction

fun MacrobenchmarkScope.createNewGroceryList(name: String) {
    val createListButtonSelector = By.text("Add new grocery list")
    device.waitAndFindObject(createListButtonSelector, 10_000).click()
    device.waitForIdle()

    val listNameFieldSelector = By.res("GroceryListNameField")
    device.waitAndFindObject(listNameFieldSelector, 5_000).text = name
    device.pressBack()
}

fun MacrobenchmarkScope.addGroceries() {
    val groceryGrid = device.waitAndFindObject(By.res("LazyGroceryGrid"), 5_000)
    groceryGrid.findObjects(By.res("GroceryGridItem")).forEach { it.click() }
    device.waitForIdle()
}

fun MacrobenchmarkScope.groceryGridScrollDown() {
    val feedList = device.waitAndFindObject(By.res("grouped_lazy_grocery_grid"), 5_000)
    feedList.setGestureMargin(device.displayWidth / 5)
    feedList.fling(Direction.DOWN)
}

fun MacrobenchmarkScope.navigateToCategory() {
    groceryGridScrollDown()
    val categoryButton = device.waitAndFindObject(
        selector = By.text("Health & Beauty"),
        timeout = 5_000,
    )
    categoryButton.click()
}

fun MacrobenchmarkScope.navigateToGroceryList(groceryListName: String) {
    val groceryListButton = device.waitAndFindObject(
        selector = By.text(groceryListName),
        timeout = 10_000,
    )
    groceryListButton.click()
    device.waitForIdle()
}

fun MacrobenchmarkScope.deleteGroceryList() {
    device.waitAndFindObject(By.desc("Delete"), 5_000).click()
    device.waitForIdle()
    device.waitAndFindObject(By.text("Delete"), 5_000).click()
}

fun MacrobenchmarkScope.navigateToSettings() {
    device.waitAndFindObject(By.desc("Settings"), 5_000).click()
    device.waitForIdle()
}

