package com.example.data

import android.content.Context
import android.graphics.Typeface
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.Typeface as ComposeTypeface
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import com.example.ui.theme.BebasNeueFamily
import com.example.ui.theme.MontserratFamily
import com.example.ui.theme.PacificoFamily
import com.example.ui.theme.PlayfairDisplayFamily
import com.example.ui.theme.RobotoMonoFamily
import com.example.ui.theme.SpaceGroteskFamily
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY updatedAt DESC")
    fun getAllProjects(): Flow<List<VideoProjectEntity>>

    @Query("SELECT COUNT(*) FROM projects")
    suspend fun getProjectCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: VideoProjectEntity): Long

    @Update
    suspend fun updateProject(project: VideoProjectEntity)

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteProjectById(id: Int)
}

@Dao
interface CustomFontDao {
    @Query("SELECT * FROM custom_fonts ORDER BY addedAt DESC")
    fun getAllCustomFonts(): Flow<List<CustomFontEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomFont(font: CustomFontEntity): Long

    @Query("DELETE FROM custom_fonts WHERE id = :id")
    suspend fun deleteCustomFontById(id: Int)
}

@Database(
    entities = [VideoProjectEntity::class, CustomFontEntity::class],
    version = 1,
    exportSchema = false
)
abstract class WsEditorDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun customFontDao(): CustomFontDao

    companion object {
        @Volatile
        private var INSTANCE: WsEditorDatabase? = null

        fun getInstance(context: Context): WsEditorDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WsEditorDatabase::class.java,
                    "ws_editor_pro.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}

data class BuiltInFontOption(
    val id: String,
    val displayName: String,
    val styleTag: String,
    val fontFamily: FontFamily
)

object FontRegistry {
    val builtInFonts = listOf(
        BuiltInFontOption("space_grotesk", "Space Grotesk", "Modern Tech", SpaceGroteskFamily),
        BuiltInFontOption("bebas_neue", "Bebas Neue", "Bold Headline", BebasNeueFamily),
        BuiltInFontOption("montserrat", "Montserrat", "Clean Sans", MontserratFamily),
        BuiltInFontOption("pacifico", "Pacifico", "Neon Script", PacificoFamily),
        BuiltInFontOption("playfair", "Playfair Display", "Cinema Serif", PlayfairDisplayFamily),
        BuiltInFontOption("roboto_mono", "Roboto Mono", "Timecode Mono", RobotoMonoFamily)
    )

    private val loadedCustomFontCache = mutableMapOf<String, FontFamily>()

    fun resolveFontFamily(fontId: String, customFontPath: String?): FontFamily {
        if (!customFontPath.isNullOrBlank()) {
            loadedCustomFontCache[customFontPath]?.let { return it }
            val file = File(customFontPath)
            if (file.exists() && file.length() > 0L) {
                val loaded = runCatching {
                    val androidTypeface = Typeface.createFromFile(file)
                    FontFamily(ComposeTypeface(androidTypeface))
                }.getOrNull()
                if (loaded != null) {
                    loadedCustomFontCache[customFontPath] = loaded
                    return loaded
                }
            }
        }
        return builtInFonts.firstOrNull { it.id == fontId }?.fontFamily ?: SpaceGroteskFamily
    }
}

class WsEditorRepository(
    private val context: Context,
    private val projectDao: ProjectDao,
    private val customFontDao: CustomFontDao
) {
    val allProjects: Flow<List<VideoProjectEntity>> = projectDao.getAllProjects()
    val allCustomFonts: Flow<List<CustomFontEntity>> = customFontDao.getAllCustomFonts()

    suspend fun seedInitialDataIfEmpty() = withContext(Dispatchers.IO) {
        if (projectDao.getProjectCount() == 0) {
            projectDao.insertProject(
                VideoProjectEntity(
                    title = "Cyberpunk_Reel_4K",
                    aspectRatio = "16:9",
                    resolution = "4K 60FPS",
                    durationSec = 16.0f,
                    filterId = "cyber_neon",
                    playbackSpeed = 1.0f,
                    musicTrackName = "Cyberpunk Pulse 128BPM",
                    customFontCount = 2,
                    clipsCount = 4
                )
            )
            projectDao.insertProject(
                VideoProjectEntity(
                    title = "Night_Drift_Short",
                    aspectRatio = "9:16",
                    resolution = "4K 60FPS",
                    durationSec = 12.5f,
                    filterId = "purple_haze",
                    playbackSpeed = 1.5f,
                    musicTrackName = "Synthwave Horizon",
                    customFontCount = 1,
                    clipsCount = 3,
                    updatedAt = System.currentTimeMillis() - 3600_000L
                )
            )
        }
    }

    suspend fun insertProject(project: VideoProjectEntity): Long =
        projectDao.insertProject(project)

    suspend fun deleteProject(id: Int) =
        projectDao.deleteProjectById(id)

    suspend fun deleteCustomFont(font: CustomFontEntity) = withContext(Dispatchers.IO) {
        runCatching {
            val f = File(font.filePath)
            if (f.exists()) f.delete()
        }
        customFontDao.deleteCustomFontById(font.id)
    }

    /**
     * Copies a user-selected font file (.ttf or .otf) from a content:// Uri into
     * internal storage, validates it with Android's Typeface.createFromFile, and
     * persists it in Room so it can be used across any video project.
     */
    suspend fun importCustomFontFromUri(uri: Uri): Result<CustomFontEntity> =
        withContext(Dispatchers.IO) {
            runCatching {
                val contentResolver = context.contentResolver
                var originalFileName = "CustomFont_${System.currentTimeMillis()}.ttf"
                contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (cursor.moveToFirst() && nameIndex >= 0) {
                        val queried = cursor.getString(nameIndex)
                        if (!queried.isNullOrBlank()) {
                            originalFileName = queried
                        }
                    }
                }

                val lower = originalFileName.lowercase()
                val ext = when {
                    lower.endsWith(".otf") -> ".otf"
                    lower.endsWith(".ttf") -> ".ttf"
                    else -> ".ttf"
                }

                val baseName = originalFileName
                    .substringBeforeLast(".")
                    .replace(Regex("[^a-zA-Z0-9_\\- ]"), "")
                    .trim()
                    .ifEmpty { "Uploaded_Font" }

                val fontsDir = File(context.filesDir, "custom_fonts").apply { mkdirs() }
                val safeFileName = "${baseName.replace(" ", "_")}_${System.currentTimeMillis()}$ext"
                val destFile = File(fontsDir, safeFileName)

                contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(destFile).use { output ->
                        input.copyTo(output)
                    }
                } ?: error("Unable to open selected font file.")

                if (!destFile.exists() || destFile.length() < 64L) {
                    destFile.delete()
                    error("Selected file is empty or unreadable.")
                }

                // Validate with Android Typeface parser
                val parsedTypeface = try {
                    Typeface.createFromFile(destFile)
                } catch (e: Exception) {
                    destFile.delete()
                    throw IllegalArgumentException("Invalid .TTF/.OTF font file: ${e.message}")
                }

                if (parsedTypeface == Typeface.DEFAULT && destFile.length() < 256L) {
                    destFile.delete()
                    error("Selected file is not a valid TrueType or OpenType font.")
                }

                val sizeKb = (destFile.length() / 1024L).toInt().coerceAtLeast(1)
                val fontId = "custom_${System.currentTimeMillis()}"
                val entity = CustomFontEntity(
                    fontId = fontId,
                    displayName = baseName,
                    fileName = originalFileName,
                    filePath = destFile.absolutePath,
                    fileSizeKb = sizeKb,
                    isDeviceUpload = true
                )
                val rowId = customFontDao.insertCustomFont(entity)
                entity.copy(id = rowId.toInt())
            }
        }

    /**
     * Imports a real .ttf font file from the app's bundled sample_fonts assets into
     * filesDir/custom_fonts so users can also test custom font file loading with one tap.
     */
    suspend fun importSampleFontFromAssets(
        assetFileName: String,
        displayName: String
    ): Result<CustomFontEntity> = withContext(Dispatchers.IO) {
        runCatching {
            val fontsDir = File(context.filesDir, "custom_fonts").apply { mkdirs() }
            val destFile = File(fontsDir, assetFileName)
            context.assets.open("sample_fonts/$assetFileName").use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
            // Validate Typeface creation from the copied file
            Typeface.createFromFile(destFile)
            val sizeKb = (destFile.length() / 1024L).toInt().coerceAtLeast(1)
            val fontId = "custom_asset_${assetFileName.substringBeforeLast(".").lowercase()}"
            val entity = CustomFontEntity(
                fontId = fontId,
                displayName = displayName,
                fileName = assetFileName,
                filePath = destFile.absolutePath,
                fileSizeKb = sizeKb,
                isDeviceUpload = false
            )
            val rowId = customFontDao.insertCustomFont(entity)
            entity.copy(id = rowId.toInt())
        }
    }
}
