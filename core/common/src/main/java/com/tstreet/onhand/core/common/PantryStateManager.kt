package com.tstreet.onhand.core.common

import android.content.SharedPreferences
import androidx.core.content.edit
import com.tstreet.onhand.core.common.CommonModule.SHARED_PREF_FILE
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Named

// TODO: Does this belong in the common module?
interface PantryStateManager {

    /**
     * Returns true if items in pantry has changed since last recipe search query, false otherwise.
     */
    fun hasPantryStateChanged(): Boolean

    /**
     * Triggers each time an item is added or removed from the pantry.
     */
    fun onPantryStateChange()

    /**
     * Resets pantry state changed to false. For example, this is triggered when a recipe search
     * query happens to avoid unncessary api calls.
     */
    fun onResetPantryState()
}

class PantryStateManagerImpl @Inject constructor(
    @Named(SHARED_PREF_FILE) private val sharedPreferences: SharedPreferences
) : PantryStateManager {

    init {
        println("[OnHand] Creating ${this.javaClass.simpleName}")
    }

    // Default to true to eagerly say pantry state has updated in case of error
    private val pantryStateChanged =
        AtomicBoolean(sharedPreferences.getBoolean(PANTRY_STATE_KEY, true))

    override fun hasPantryStateChanged(): Boolean {
        return pantryStateChanged.get()
    }

    override fun onPantryStateChange() {
        // So we only trigger saving to shared prefs once per state change
        if (!pantryStateChanged.getAndSet(true)) {
            sharedPreferences.edit {
                putBoolean(PANTRY_STATE_KEY, pantryStateChanged.get())
                commit()
            }
        }
    }

    override fun onResetPantryState() {
        // So we only trigger saving to shared prefs once per reset
        if (pantryStateChanged.getAndSet(false)) {
            sharedPreferences.edit {
                putBoolean(PANTRY_STATE_KEY, pantryStateChanged.get())
                commit()
            }
        }
    }
}

private const val PANTRY_STATE_KEY = "pantry_state_changed"
