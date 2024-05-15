<h1 align="center" line-height:1"><b>Grocery Genius</b></h1>

<div align="center">
  <img alt="Grocery Genius logo" src="images/app_logo_rounded_corners.svg" height="150px">
</div>

<br />

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
