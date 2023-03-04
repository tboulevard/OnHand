package com.tstreet.onhand.core.database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DatabaseModule {

    // TODO: check when this is created, i.e. how early in lifecycle
    @Provides
    @Singleton
    fun providesOnHandDatabase(
        context: Context,
    ): OnHandDatabase = Room.databaseBuilder(
        context,
        OnHandDatabase::class.java,
        "onhand-database"
    )
        .createFromAsset("database/ingredient_catalog.db")
        .build()
        .also { println("[OnHand] onhand-database created") }
}
