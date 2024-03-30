package com.rendox.grocerygenius.file_storage

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File

@RunWith(Parameterized::class)
class AssetToFileSaverTest(
    private val providedAssetFilePath: String,
    private val providedOutputDirPath: String?,
    private val providedOutputFileName: String?,
    private val expectedOutputFilePath: String,
) {

    private lateinit var assetToFileSaver: AssetToFileSaver
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        assetToFileSaver = AssetToFileSaver(
            appContext = context,
            ioDispatcher = Dispatchers.IO,
        )
    }

    @Test
    fun assertCopiesFileSuccessfully() = runTest {
        val outputFile = assetToFileSaver.copyAssetToInternalStorage(
            providedAssetFilePath, providedOutputDirPath, providedOutputFileName
        )
        assertThat(outputFile?.exists()).isTrue()
    }

    @Test
    fun assertResultingFilePathIsCorrect() = runTest {
        val outputFile = assetToFileSaver.copyAssetToInternalStorage(
            providedAssetFilePath, providedOutputDirPath, providedOutputFileName
        )
        val expectedOutputFile = File(context.filesDir, expectedOutputFilePath)
        assertThat(outputFile).isEqualTo(expectedOutputFile)
    }

    @Test
    fun assertReturnsNullIfFailedToCopyFile() = runTest {
        val outputFile = assetToFileSaver.copyAssetToInternalStorage(
            "non_existent_file.png", providedOutputDirPath, providedOutputFileName
        )
        assertThat(outputFile).isNull()
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<String?>> {
            return listOf(
                arrayOf(
                    "grocery/icons/milk_v1.png",
                    "images/",
                    "milk_bottle.png",
                    "images/milk_bottle.png"
                ),
                arrayOf(
                    "grocery/icons/milk_v1.png",
                    "groceries",
                    null,
                    "groceries/milk_v1.png"
                ),
                arrayOf(
                    "grocery/icons/milk_v1.png",
                    null,
                    "milk_grocery.png",
                    "grocery/icons/milk_grocery.png"
                ),
                arrayOf(
                    "grocery/icons/milk_v1.png",
                    null,
                    null,
                    "grocery/icons/milk_v1.png"
                ),
            )
        }
    }
}