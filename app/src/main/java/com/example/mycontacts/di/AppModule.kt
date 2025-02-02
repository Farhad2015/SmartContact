package com.example.mycontacts.di

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mycontacts.data.dataSource.ContactsDatabase
import com.example.mycontacts.data.repository.OfflineContactsRepository
import com.example.mycontacts.domain.repository.ContactsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideContactDatabase(app: Application): ContactsDatabase {
        return Room.databaseBuilder(
            app,
            ContactsDatabase::class.java,
            ContactsDatabase.DB_NAME
        ).addMigrations(MIGRATION_1_2).build()
    }

    @Provides
    @Singleton
    fun provideContactRepository(db: ContactsDatabase): ContactsRepository {
        return OfflineContactsRepository(db.contactDao())
    }


    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Add a new column "isFavorite" of type INTEGER with default value 0.
            database.execSQL("ALTER TABLE contacts ADD COLUMN isFavorite INTEGER NOT NULL DEFAULT 0")
        }
    }
}
