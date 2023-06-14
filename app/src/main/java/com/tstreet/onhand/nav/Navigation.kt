package com.tstreet.onhand.nav

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.tstreet.onhand.core.common.LocalCommonProvider
import com.tstreet.onhand.core.common.injectedViewModel
import com.tstreet.onhand.core.data.api.di.LocalDataProvider
import com.tstreet.onhand.core.ui.RECIPE_ID_NAV_KEY
import com.tstreet.onhand.feature.customrecipe.CreateCustomRecipeScreen
import com.tstreet.onhand.feature.home.HomeScreen
import com.tstreet.onhand.feature.customrecipe.di.DaggerCustomRecipeComponent
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
fun Navigation() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            OnHandBottomNavigationBar(
                navController,
                BottomNavigationScreens
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding)
        ) {
            NavigationConfiguration(navController)
        }
    }
}

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
private fun NavigationConfiguration(
    navController: NavHostController
) {
    val dataProvider = LocalDataProvider.current
    val commonProvider = LocalCommonProvider.current

    NavHost(
        navController = navController,
        startDestination = BottomNavigationScreen.Home.route
    ) {
        // Note: each composable { } block is triggered for each recomposition (potentially as
        // often each new frame). Revisit whether this is a performance issue later.
        composable(route = BottomNavigationScreen.Home.route) {
            HomeScreen(
                injectedViewModel {
                    DaggerHomeComponent
                        .builder()
                        .dataComponentProvider(dataProvider)
                        .commonComponentProvider(commonProvider)
                        .build()
                        .viewModel
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
                        .dataComponentProvider(dataProvider)
                        .commonComponentProvider(commonProvider)
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
                        dataComponentProvider = dataProvider,
                        commonComponentProvider = commonProvider,
                        recipeId = recipeId
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
                        .dataComponentProvider(dataProvider)
                        .commonComponentProvider(commonProvider)
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
                        .dataComponentProvider(dataProvider)
                        .commonComponentProvider(commonProvider)
                        .build()
                        .viewModel
                }
            )
        }
        // subgraph for custom recipe creation
        // TODO: This isn't done correctly, revisit later...
        navigation(
            startDestination = Screen.CreateRecipe.route,
            route = BottomNavigationScreen.AddCustomRecipe.route
        ) {
            composable(route = Screen.CreateRecipe.route) {
                val parentEntry = remember(it) {
                    navController.getBackStackEntry(BottomNavigationScreen.AddCustomRecipe.route)
                }

                val ingredientSearchViewModel =
                    injectedViewModel(viewModelStoreOwner = parentEntry) {
                        DaggerIngredientSearchComponent
                            .builder()
                            .dataComponentProvider(dataProvider)
                            .commonComponentProvider(commonProvider)
                            .build()
                            .viewModel
                    }

                CreateCustomRecipeScreen(
                    navController = navController,
                    viewModel = injectedViewModel {
                        DaggerCustomRecipeComponent
                            .builder()
                            .dataComponentProvider(dataProvider)
                            .commonComponentProvider(commonProvider)
                            .build()
                            .viewModel
                    },
                    selectedIngredients = ingredientSearchViewModel.selectedIngredients,
                    onRemoveSelectedIngredient = ingredientSearchViewModel::onRemoveSelectedIngredient
                )
            }
            composable(
                route = Screen.IngredientSearch.route
            ) {
                val parentEntry = remember(it) {
                    navController.getBackStackEntry(BottomNavigationScreen.AddCustomRecipe.route)
                }

                val ingredientSearchViewModel =
                    injectedViewModel(viewModelStoreOwner = parentEntry) {
                        DaggerIngredientSearchComponent
                            .builder()
                            .dataComponentProvider(dataProvider)
                            .commonComponentProvider(commonProvider)
                            .build()
                            .viewModel
                    }

                IngredientSearchScreen(
                    navController,
                    ingredientSearchViewModel
                )
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
    // If the current root route doesn't require us to hide all nav bar items, display nav bar
    //  e.g.: We use this to hide the nav bar for the entire create custom recipe flow
    // Only build the nav bar if the current parent route is found in the list of screens that
    // need to hide the nav bar
    if (!RoutesWithHiddenNavBar.contains(currentParentRoute(navController))) {
        NavigationBar {
            val currentRoute = currentRoute(navController)
            bottomNavigationItems.forEach { screen ->
                NavigationBarItem(
                    icon = { Icon(imageVector = screen.icon, contentDescription = screen.route) },
                    label = { Text(screen.displayText) },
                    selected = currentRoute == screen.route,
                    onClick = {
                        // For "singleTop" behavior where we do not create a second instance of the
                        // composable if we already navigated
                        if (currentRoute != screen.route) {
                            navController.navigate(screen.route)
                        }
                    }
                )
            }
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
