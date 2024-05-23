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

Grocery Genius is a free, customizable shopping list app with a modern design, autocomplete suggestions, offline capabilities, and feature-rich functionality.

## Features

- **Add groceries with a few clicks.** The app has a database of over 130 predefined groceries, each with its own icon. Type as few as two letters, and the best matching items will quickly appear.
- **Add, Edit, and Delete Groceries.** If your grocery item isn’t in the database, it will be created automatically. You can then categorize it, assign an icon, remove it from the list, or delete it entirely.
- **Modern Design.** Unlike many shopping list apps that use a list view, Grocery Genius features a grid view with attractive icons and color-coded separation for purchased and unpurchased items. Groceries are sorted by category, mirroring their placement in stores. The app’s design follows Material You practices, offering dynamic color, dark and light modes, and six color schemes to choose from.
- **Manage Separate Lists.** Create multiple grocery lists and easily reorder them on the main screen with drag-and-drop functionality.
- **Customization.** You can choose a default grocery list, reorder categories, switch between dark and light modes, and choose a different color scheme in the settings.
- **Offline Mode.** Initially, the app requires an internet connection to fetch predefined groceries. After that, you can use it fully offline.
- **Completely Free.** Grocery Genius is free and open source, with no limitations. Add as many grocery lists as you want. All features available now will remain free forever.

## Roadmap

These features may or may not be implemented in the long term.
- Sharing grocery lists with other people
- Location reminders
- Adding photos to items
- Adding items using voice

## Get the app

You can install the app from the [GitHub releases](https://github.com/DanielRendox/GroceryGenius/releases) page or build it yourself by [cloning the project](https://docs.github.com/articles/cloning-a-repository) and launching it in the latest version of [Android Studio](https://developer.android.com/studio).

## Tech stack

- Jetpack Compose for the user interface, with a single Activity and no Fragments
- Room database for local data storage
- Retrofit for working with REST API
- Preferences DataStore for storing simple data in key-value form
- Kotlin coroutines and flow for asynchronous requests
- Work Manager for synching data in the background
- Moshi for decoding JSON files into Kotlin objects
- Coil for loading images from files in a performant way
- RecyclerView for lists with drag-and-drop functionality
- MVI pattern
- CLEAN architecture with data and presentation layer

## Some Technical Stuff

1. The app has predefined groceries that the database should be prepopulated with after installation. Grocery Genius aims to further scale towards online grocery list sharing, so this data has to be fetched from the server, rather than being bundled in the APK. Since there is currently no backend for this app, the data is fetched directly from the top-level "assets" folder in this GitHub repository (production branch) using GitHub REST API. While GitHub may not be very suitable for file hosting, this is convenient as it only takes to update the data here and it will be reflected on all devices. Here is how it works:
    - A WorkManager task gets executed on each app startup and synchronizes each repository's data sequentially. The synchronization logic for each repository is defined in its sync function. Some helper functions are used to unify the sync process and catch exceptions that may happen. 
    - During the synchronization process each repository checks for updates by fetching change list versions and comparing the latest version with the local one, which is saved in Preferences DataStore. If the local data is outdated, it fetches the latest data from the network and then caches it in the local database.
    - The app doesn't use Coil to fetch images from the server directly. That's because although Coil can store images permanently on the device's internal storage, it loads images on demand. This means that if we relied solely on Coil, some grocery images might not have been displayed when the user's device went offline because they hadn't been fetched previously. Since the app is designed to be fully functional without an internet connection, we instead manually fetch all images from the network, save them to the app's internal storage, and then use Coil to load them in the UI performantly.
    - If the sync is happening for the first time, the app detects that and fetches the archive of grocery images instead of making an HTTP request for each of them.

2. Drag and drop functionality in the Dashboard and Settings screens is achieved by integrating RecyclerView and ItemTouchHelper with Jetpack Compose. In this setup, composables are items in the RecyclerView, which is in turn a child view in the composable hierarchy. RecyclerView is used because Jetpack Compose doesn't have an official drag-and-drop feature yet.

## Let’s work together!

Grocery Genius is an open-source project that welcomes contributions! If you're inclined to offer support in any of the following areas:

- Building a backend for this app to introduce real-time grocery list-sharing
- Developing features in the [Roadmap](https://github.com/DanielRendox/GroceryGenius/tree/develop#roadmap)
- Porting to iOS
- Design enhancements,
- Translation to different languages,
- Promotion, and spreading the word about the app
- Identifying and reporting bugs,
- Or any other contributions you might have in mind,

and are willing to do so **voluntarily**, please don't hesitate to open an issue, submit a PR, or reach out to [me](https://github.com/DanielRendox) directly.

Whether you're a seasoned developer or just looking to hone your skills, your contributions are much appreciated.
