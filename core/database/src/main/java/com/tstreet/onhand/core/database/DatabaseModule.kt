package com.tstreet.onhand.core.database

import android.content.Context
import android.util.Log
import androidx.room.Room
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun providesOnHandDatabase(
        context: Context,
    ): OnHandDatabase = Room.databaseBuilder(
        context,
        OnHandDatabase::class.java,
        "onhand-database"
    )
            .createFromAsset("database/local_mock_ingredients.db")
        .build()
        .also { Log.d("[OnHand]", "onhand-database created") }
}
