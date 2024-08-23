package com.scanners.docsscanners.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.scanners.docsscanners.data.local.converters.DateTypeConverter
import com.scanners.docsscanners.data.local.dao.PdfDao
import com.scanners.docsscanners.models.PdfEntity

@Database (
    entities = [PdfEntity::class], version = 1, exportSchema = false
)
@TypeConverters(DateTypeConverter::class)
abstract class PdfDatabase: RoomDatabase() {
    abstract val pdfDao: PdfDao

    companion object {

        @Volatile
        private var INSTANCES: PdfDatabase? = null

        fun getInstance(context: Context): PdfDatabase {
            synchronized(this) {
                return INSTANCES ?: Room.databaseBuilder (
                    context.applicationContext,
                    PdfDatabase::class.java,
                    "pdf_db"
                ).build().also { it
                    INSTANCES = it
                }
            }
        }
    }
}