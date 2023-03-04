package com.tstreet.onhand.core.common

import javax.inject.Scope

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class FeatureScope

// TODO: check whether this scope attached to other screens operates the same way as having
// separate scoped for each. i.e. test just having FeatureScope vs. IngredientSearchScope &
// RecipeSearchScope. If so, use this scope for all features instead of individual scopes.
