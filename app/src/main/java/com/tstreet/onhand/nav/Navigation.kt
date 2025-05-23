package com.tstreet.onhand.nav

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.tstreet.onhand.core.common.CommonComponent
import com.tstreet.onhand.core.common.injectedViewModel
import com.tstreet.onhand.core.data.di.DataComponent
import com.tstreet.onhand.core.ui.RECIPE_ID_NAV_KEY
import com.tstreet.onhand.feature.customrecipe.CreateCustomRecipeScreen
import com.tstreet.onhand.feature.customrecipe.di.DaggerCustomRecipeComponent
import com.tstreet.onhand.feature.home.HomeScreenContainer
import com.tstreet.onhand.feature.home.di.DaggerHomeComponent
import com.tstreet.onhand.feature.ingredientsearch.IngredientSearchScreen
import com.tstreet.onhand.feature.ingredientsearch.di.DaggerIngredientSearchComponent
import com.tstreet.onhand.feature.recipedetail.RecipeDetailScreen
import com.tstreet.onhand.feature.recipedetail.di.DaggerRecipeDetailComponent
import com.tstreet.onhand.feature.recipesearch.RecipeSearchScreen
import com.tstreet.onhand.feature.recipesearch.di.DaggerRecipeSearchComponent
import com.tstreet.onhand.feature.savedrecipes.SavedRecipesScreen
import com.tstreet.onhand.feature.savedrecipes.di.DaggerSavedRecipesComponent
import com.tstreet.onhand.feature.shoppinglist.ShoppingListScreen
import com.tstreet.onhand.feature.shoppinglist.di.DaggerShoppingListComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigation(
    commonComponent: CommonComponent,
    dataComponent: DataComponent
) {
    Log.d("[OnHand]", "Navigation root recomposition")

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Don't show bottom navigation bar on certain screens
    val shouldShowBottomBar = remember(currentRoute) {
        // Hide bottom navigation when in ingredient search
        currentRoute != Screen.SelectableIngredientSearch.route &&
                currentRoute != Screen.PantryIngredientSearch.route
    }

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) {
                OnHandBottomNavigationBar(
                    navController,
                    BottomNavigationScreens
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(
                bottom = if (shouldShowBottomBar) padding.calculateBottomPadding() else 0.dp,
                top = padding.calculateTopPadding(),
                start = padding.calculateStartPadding(LocalLayoutDirection.current),
                end = padding.calculateEndPadding(LocalLayoutDirection.current)
            )
        ) {
            NavigationConfiguration(
                commonComponent,
                dataComponent,
                navController
            )
        }
    }
}

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
private fun NavigationConfiguration(
    commonComponent: CommonComponent,
    dataComponent: DataComponent,
    navController: NavHostController
) {

    NavHost(
        navController = navController,
        startDestination = BottomNavigationScreen.Home.route
    ) {
        composable(route = BottomNavigationScreen.Home.route) {
            HomeScreenContainer(
                injectedViewModel {
                    DaggerHomeComponent
                        .builder()
                        .commonComponent(commonComponent)
                        .dataComponent(dataComponent)
                        .build()
                        .viewModel
                },
                onIngredientSearchBarClicked = {
                    navController.navigate(Screen.PantryIngredientSearch.route)
                }
            )
        }
        composable(route = BottomNavigationScreen.RecipeSearch.route) {
            // TODO: come back to issue described here ... https://github.com/google/dagger/issues/3188
            // TODO: for some reason this broke when we upgraded compose version
            RecipeSearchScreen(
                navController,
                injectedViewModel {
                    DaggerRecipeSearchComponent
                        .builder()
                        .commonComponent(commonComponent)
                        .dataComponent(dataComponent)
                        .build()
                        .viewModel
                }
            )
        }
        composable(
            // TODO: there's nothing enforcing this route between the detail screen and screens that
            //  navigate to it, refactor in future
            route = "${Screen.RecipeDetail.route}/{$RECIPE_ID_NAV_KEY}",
            arguments = listOf(navArgument(RECIPE_ID_NAV_KEY) { type = NavType.IntType })
        ) {
            val recipeId = it.arguments?.getInt(RECIPE_ID_NAV_KEY)

            RecipeDetailScreen(
                navController,
                injectedViewModel {
                    DaggerRecipeDetailComponent.factory().create(
                        recipeId = recipeId,
                        dataComponent,
                        commonComponent
                    ).viewModel
                }
            )
        }
        composable(route = BottomNavigationScreen.SavedRecipes.route) {
            SavedRecipesScreen(
                navController,
                injectedViewModel {
                    DaggerSavedRecipesComponent
                        .builder()
                        .commonComponent(commonComponent)
                        .dataComponent(dataComponent)
                        .build()
                        .viewModel
                }
            )
        }
        composable(route = BottomNavigationScreen.ShoppingList.route) {
            ShoppingListScreen(
                injectedViewModel {
                    DaggerShoppingListComponent
                        .builder()
                        .commonComponent(commonComponent)
                        .dataComponent(dataComponent)
                        .build()
                        .viewModel
                }
            )
        }

        composable(route = Screen.PantryIngredientSearch.route) {
            IngredientSearchScreen(
                injectedViewModel {
                    DaggerIngredientSearchComponent
                        .builder()
                        .commonComponent(commonComponent)
                        .dataComponent(dataComponent)
                        .build()
                        .pantryViewModel
                }
            ) {
                navController.popBackStack()
            }
        }

        // subgraph for custom recipe creation
        // TODO: This isn't done correctly, revisit later...
        navigation(
            startDestination = Screen.CreateRecipe.route,
            route = BottomNavigationScreen.AddCustomRecipe.route,
        ) {
            composable(route = Screen.CreateRecipe.route) {
                CreateCustomRecipeScreen(
                    navController = navController,
                    viewModel = injectedViewModel {
                        DaggerCustomRecipeComponent
                            .builder()
                            .commonComponent(commonComponent)
                            .dataComponent(dataComponent)
                            .build()
                            .viewModel
                    },
                    ingredientSearchViewModel = injectedViewModel {
                        DaggerIngredientSearchComponent
                            .builder()
                            .commonComponent(commonComponent)
                            .dataComponent(dataComponent)
                            .build()
                            .selectableIngredientViewModel
                    }
                )
            }
            composable(
                route = Screen.SelectableIngredientSearch.route
            ) {
                val parentEntry = remember(it) {
                    navController.getBackStackEntry(Screen.CreateRecipe.route)
                }

                val ingredientSearchViewModel =
                    // Bind ViewModel lifecycle to parent
                    injectedViewModel(viewModelStoreOwner = parentEntry) {
                        DaggerIngredientSearchComponent
                            .builder()
                            .commonComponent(commonComponent)
                            .dataComponent(dataComponent)
                            .build()
                            .selectableIngredientViewModel
                    }

                IngredientSearchScreen(
                    ingredientSearchViewModel
                ) {
                    navController.popBackStack()
                }
            }
        }
    }
}


// TODO: Badged box for notification of new recipes available
@Composable
private fun OnHandBottomNavigationBar(
    navController: NavHostController,
    bottomNavigationItems: List<BottomNavigationScreen>
) {
    NavigationBar {
        val currentRoute = currentRoute(navController)
        val currentParentRoute = currentParentRoute(navController)
        bottomNavigationItems.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(imageVector = screen.icon, contentDescription = screen.route) },
                label = { Text(screen.displayText) },
                selected = currentRoute == screen.route,
                onClick = {
                    // For "singleTop" behavior where we do not create a second instance of the
                    // composable if we already navigated
                    if (currentRoute != screen.route &&
                        // TODO: Note - if we want to reset within Create Custom recipe flow, need
                        //  to change this logic. Otherwise this prevents us from launching second
                        // subgraph
                        currentParentRoute != screen.route
                    ) {
                        navController.navigate(screen.route)
                    }
                }
            )
        }
    }
}

@Composable
private fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

@Composable
private fun currentParentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.parent?.route
}
