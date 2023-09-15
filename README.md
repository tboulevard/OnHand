# OnHand

![All Checks](https://github.com/tboulevard/OnHand/actions/workflows/build.yaml/badge.svg)

OnHand is a recipe generation app written with Kotlin, Jetpack Compose, Dagger 2, and the latest Android architecture guidance laid out by Google as of 2023.

## Screenshots

<img src="https://github.com/tboulevard/OnHand/assets/5940570/2005ad20-add4-409b-aca1-b6e710b53042" width="400"/>
<img src="https://github.com/tboulevard/OnHand/assets/5940570/91e0b5b4-ec58-40b1-82d6-61c0b5c5d85c" width="400"/> 
<img src="https://github.com/tboulevard/OnHand/assets/5940570/af5126cf-9ffe-4708-aa5e-8a1f5b3346e2" width="400"/>
<img src="https://github.com/tboulevard/OnHand/assets/5940570/bd6fd7e0-30f7-4f62-937a-4cd36f262965" width="400"/>

## Features

- Pantry management (add/remove items)
- Recipe search, derived from items in pantry
- Ability to save recipes
- Automatic shopping list calculation based marked recipes
- Custom recipe creation

## File Structure

| Module                |Purpose                  |
|-----------------------|---------------------------|
|	`:app` | Contains MainActivity, navigation logic, AndroidManifest, and logic for the creation of root application component.          |
|	`:build-logic` | Defines project-specific convention plugins, used to keep a single source of truth for common configurations across multiple modules.           |
|	`:core`| Logic to the app used across multiple modules. Only `:feature` or other `:core` modules should depend on modules from here. |
|	`:core:common`| Shared utility logic for `:core` or `:feature` modules. For example, `@FeatureScope` Dagger binding or `PantryStateManager` logic. |
|	`:core:data`| Split into `:api` and `:impl`, the set of data operations the app can perform. Hides implementation details of where this data is retrieved/stored, whether it be local DB (Room) or remote. |
|	`:core:database`| Room entities and configuration. |
|	`:core:domain`| Platform agnostic business logic. For example, acting as an intermediary between a ViewModel and the data layer for removing an ingredient from the pantry. |
|	`:core:model`| Non-network models used to propagate information throughout the app. |
|	`:core:network`| Network configuration (Retrofit) and network models. |
|	`:core:ui`| Shared UI components. Primarily just `@Composable` functions that are used in multiple locations, like `OnHandAlertDialog`.|
|	`:feature`|Root for all `:feature` modules. `:feature` modules contain UI logic associated with each feature only. They _only_ depend on `:core` modules and are only depended on by `:app` for navigation purposes.            |

## Technologies

Written using  **Kotlin**  with  **Jetpack Compose**  as the UI framework.  
In addition, the following are used in the project:

- [**Compose Navigation**](https://developer.android.com/jetpack/compose/navigation)  - for navigating between features.
- [**Coil**](https://coil-kt.github.io/coil/)  - for loading images.
- [**Dagger 2**](https://dagger.dev/)  - for dependency injection.  
- [**Kotlin Coroutines**](https://kotlinlang.org/docs/coroutines-overview.html)  - for the threading model and concurrency.
- [**Retrofit**](https://square.github.io/retrofit/)  - for networking.
- [**Room**](https://developer.android.com/training/data-storage/room)  - for persistence and offline mode.

## Architecture

TBD (flow diagram breaking out application and feature scoped modules)
