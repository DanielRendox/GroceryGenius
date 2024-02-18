<h1 align="center" line-height:1"><b>Grocery Genius</b></h1>

<div align="center">
  <img alt="Grocery Genious logo" src="images/app_logo_rounded_corners.png" height="150px">
</div>

<h3 align="center" line-height:1"><b>Not a complete project, the outlined features are under development.</b></h3>

<br />

Grocery Genious is a shopping list app that anticipates products you're running low on before they run out. After you've indicated the purchase of a certain item at least twice, the app determines the duration until you'll need to buy this product again. The more you interact with the app the more accurate these predictions will be. Apart from that, Grocery Genious offers a visually appealing interface featuring grocery images, autocomplete suggestions, and an intuitive user experience.

## Motivation

I want to go on a little sprint to create a simple Android application as quickly as possible, utilizing the technologies I already know. This endeavor will improve my skills in working faster, planning smarter, and meeting deadlines on time. It will also provide me with the opportunity to gain experience working with Backend as a Service providers like Firebase, as I intend to implement a feature for sharing shopping lists with friends later. Additionally, it will add another non-traditional project to my portfolio. On the other hand, I haven’t encountered any shopping list app that predicts the products you’re running out of, except for [Bring](https://www.getbring.com/), from which I intend to draw inspiration. 

## Features

- Easily create and sort your grocery lists
- Use already available products instead of typing the name of each or create your own items
- See which products are in stock, which products will be over soon, and which ones have already run out
- Modern Material UI with customizability options such as item sorting, dark/light mode, etc.

## Tech stack

- Jetpack Compose for the user interface, with a single Activity and no Fragments.
- Official Compose navigation library
- View model for business logic separation
- SQLDelight database for local data storage
- Kotlin coroutines and flow for asynchronous requests
- Koin for dependency injection
- MVVM pattern
- CLEAN architecture with both repository and use cases
- SOLID principles
