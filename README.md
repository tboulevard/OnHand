# OnHand

![All Checks](https://github.com/tboulevard/OnHand/actions/workflows/build.yaml/badge.svg)

OnHand is a simple recipe generation app for Android written with Kotlin, Jetpack Compose, and Dagger.

## Screenshots

<img src="https://github.com/tboulevard/OnHand/assets/5940570/2005ad20-add4-409b-aca1-b6e710b53042" width="185"/>
<img src="https://github.com/tboulevard/OnHand/assets/5940570/91e0b5b4-ec58-40b1-82d6-61c0b5c5d85c" width="185"/> 
<img src="https://github.com/tboulevard/OnHand/assets/5940570/af5126cf-9ffe-4708-aa5e-8a1f5b3346e2" width="185"/>
<img src="https://github.com/tboulevard/OnHand/assets/5940570/b071ffd4-0bd7-404d-a234-ceebc8f96b22" width="185"/>
<img src="https://github.com/tboulevard/OnHand/assets/5940570/bd6fd7e0-30f7-4f62-937a-4cd36f262965" width="185"/>

## Features

- Pantry management (add/remove ingredients)
- Recipe search, derived from items in pantry
- Ability to save recipes
- Shopping list calculation on recipes for missing pantry ingredients
- Custom recipe creation

## File Structure

| Module                |Purpose                  |
|-----------------------|---------------------------|
|	`:app` | Contains MainActivity, navigation logic, AndroidManifest, and logic for the creation of root application component.          |
|	`:build-logic` | Defines project-specific convention plugins, used to keep a single source of truth for common configurations across multiple modules.           |
|	`:core`| Logic to the app used across multiple modules. Only `:feature` or other `:core` modules should depend on modules from here. |
|	`:core:common`| Shared utility logic for all other modules. |
|	`:core:data`| Contains implementation details about repositories (which determine the source of truth for data). |
|	`:core:database`| Room entities and configuration. |
|	`:core:domain`| Platform agnostic business logic. For example, acting as an intermediary between a ViewModel and the data layer for removing an ingredient from the pantry. Also contains Repository interfaces for data communication model. |
|	`:core:model`| Models (non-network) used to propagate information throughout the app. |
|	`:core:network`| Network configuration (Retrofit) and network models. |
|	`:core:ui`| Shared UI components. Primarily just `@Composable` functions that are used in multiple locations, like `OnHandAlertDialog`.|
|	`:feature`| Root for all `:feature` modules. `:feature` modules contain UI logic associated with each feature only. Contains feature specific UI rendering logic. |

## Technologies

Written using  **Kotlin**  with  **Jetpack Compose**  as the UI framework.  
In addition, the following are used in the project:

- [**Compose Navigation**](https://developer.android.com/jetpack/compose/navigation)  - for navigating between features.
- [**Coil**](https://coil-kt.github.io/coil/)  - for loading images.
- [**Dagger 2**](https://dagger.dev/)  - for dependency injection.  
- [**Kotlin Coroutines**](https://kotlinlang.org/docs/coroutines-overview.html)  - for the threading model and concurrency.
- [**Retrofit**](https://square.github.io/retrofit/)  - for networking.
- [**Room**](https://developer.android.com/training/data-storage/room)  - for persistence and offline mode.

## Building Locally

Generate an API key for [Spoonacular](https://rapidapi.com/spoonacular/api/recipe-food-nutrition) and add it as a property like this in `local.properties`:

```xml
spoonacular_api_key="<YOUR_API_KEY>"
```

## Architecture

TBD
