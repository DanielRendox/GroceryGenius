<h1 align="center" style="font-size:28px; line-height:1"><b>Grocery Genius</b></h1>

<div align="center">
  <img alt="Grocery Genius logo" src="images/app_logo_rounded_corners.svg" height="150px">
</div>

<br />

<div align="center">
  <a href="https://github.com/DanielRendox/GroceryGenius/releases">
    <img alt="GitHub Badge" src="images/banners/banner_github.png" height="60">
  </a>
</div>

<br />
<br />

![Routine Tracker GitHub cover image](images/readme/readme_cover_image.png)

|                                                 |                                               |                                              |
|-------------------------------------------------|-----------------------------------------------|----------------------------------------------|
| ![](images/readme/feature_search_groceries.png) | ![](images/readme/feature_separate_lists.png) | ![](images/readme/feature_customization.png) |

Grocery Genius is a reliable shopping list Android app for your grocery needs. It offers a visually appealing interface featuring grocery images, autocomplete suggestions, and an intuitive user experience.

## Motivation

My motivation for this project is to learn how to talk to the server effectively and implement the functionality of live data sharing while preserving the offline-first mode, which is known to be tricky for many mobile devs. Additionally, it will add another non-traditional project to my portfolio and allow me to practice the Android tech stack. 

## Features

- Easily create and sort your grocery lists
- Use already available products instead of typing the name of each or create your own items
- Modern Material UI with customizability options such as item sorting, dark/light mode, etc.

## Tech stack

- Jetpack Compose for the user interface, with a single Activity and no Fragments
- View model for business logic separation
- RecyclerView for lists with drag-and-drop functionality
- Room database for local data storage
- Kotlin coroutines and flow for asynchronous requests
- Hilt for dependency injection
- MVI pattern
- CLEAN architecture data and presentation layer
- Retrofit for working with REST API
- Work Manager for synching data in the background
