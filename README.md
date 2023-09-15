# OnHand

![All Checks](https://github.com/tboulevard/OnHand/actions/workflows/build.yaml/badge.svg)

OnHand is a recipe generation app written with Kotlin, Jetpack Compose, Dagger 2, and the latest Android architecture guidance laid out by Google as of 2023.

## Screenshots

![Screenshot_20230915-144346](https://github.com/tboulevard/OnHand/assets/5940570/a81cd7a1-8a53-47a4-906f-6dedbffa0069)
![Screenshot_20230915-144500](https://github.com/tboulevard/OnHand/assets/5940570/26217912-4c11-4631-95c2-36ef2b0a222c)
![Screenshot_20230915-144356](https://github.com/tboulevard/OnHand/assets/5940570/c6a97d9b-58d1-4b90-adfe-8ca7ac125c7d)
![Screenshot_20230915-144418](https://github.com/tboulevard/OnHand/assets/5940570/e52055af-a904-41a1-ba8e-8c802d977e33)

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
