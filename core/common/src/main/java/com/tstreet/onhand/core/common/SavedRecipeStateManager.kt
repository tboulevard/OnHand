package com.tstreet.onhand.core.common

import android.content.SharedPreferences
import androidx.core.content.edit
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Named


/**
 *  TODO: this and [PantryStateManager] have a lot of overlap, could probably combine into one class
 *   that takes in shared pref key as param instead
 */
interface SavedRecipeStateManager {

    /**
     * Returns true if items in saved recipes have changed since the last call to
     * [onResetSavedRecipeState], false otherwise.
     */
    fun hasSavedRecipeStateChanged(): Boolean

    /**
     * Triggers each time there's a change in the list of saved recipes.
     */
    fun onSavedRecipeStateChange()

    /**
     * Resets saved recipe state changed to false. For example, this is triggered when a the
     * shopping list is generated.
     */
    fun onResetSavedRecipeState()
}

class SavedRecipeStateManagerImpl @Inject constructor(
    @Named(CommonModule.SHARED_PREF_FILE) private val sharedPreferences: SharedPreferences
) : SavedRecipeStateManager {

    init {
        println("[OnHand] ${this.javaClass.simpleName} created")
    }

    // Default to true to eagerly say state has changed in case of error
    private val savedRecipeStateChanged =
        AtomicBoolean(sharedPreferences.getBoolean(SAVED_RECIPE_STATE_KEY, true))
            .also {
                println("[OnHand] savedRecipeStateChanged init, value=${it.get()}")
            }

    override fun hasSavedRecipeStateChanged(): Boolean {
        return savedRecipeStateChanged.get()
    }

    override fun onSavedRecipeStateChange() {
        // So we only trigger saving to shared prefs once per state change
        if (!savedRecipeStateChanged.getAndSet(true)) {
            sharedPreferences.edit {
                putBoolean(SAVED_RECIPE_STATE_KEY, savedRecipeStateChanged.get())
                commit()
            }
            println("[OnHand] Saved recipe state changed, savedRecipeStateChanged=${true}")
        }
    }

    override fun onResetSavedRecipeState() {
        // So we only trigger saving to shared prefs once per reset
        if (savedRecipeStateChanged.getAndSet(false)) {
            sharedPreferences.edit {
                putBoolean(SAVED_RECIPE_STATE_KEY, savedRecipeStateChanged.get())
                commit()
            }
            println("[OnHand] Saved recipe state reset, savedRecipeStateChanged=${false}")
        }
    }
}

private const val SAVED_RECIPE_STATE_KEY = "saved_recipes_state_changed"
