package com.rendox.grocerygenius.network.icons

import android.content.Context
import com.rendox.grocerygenius.file_storage.UnzipUtils
import com.rendox.grocerygenius.model.IconReference
import com.rendox.grocerygenius.network.GitHubApi
import com.rendox.grocerygenius.network.data_sources.IconNetworkDataSource
import com.rendox.grocerygenius.network.model.NetworkChangeList
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class OfflineFirstIconNetworkDataSource @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val gitHubApi: GitHubApi,
) : IconNetworkDataSource {

    override suspend fun downloadIcons(): List<IconReference> {
        val iconsArchive = appContext.filesDir.resolve("icons/all_icons_v1.zip")
            .apply { parentFile?.mkdirs() }
        gitHubApi.getIconsArchive().byteStream().use { input ->
            iconsArchive.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        val extractedFiles = UnzipUtils.unzip(
            iconsArchive, appContext.filesDir.absolutePath + "/icons"
        ).map { file ->
            IconReference(
                uniqueFileName = file.name,
                filePath = file.toRelativeString(appContext.filesDir),
            )
        }
        iconsArchive.delete()
        return extractedFiles
    }

    override suspend fun downloadIconsByIds(
        ids: List<String>
    ): List<IconReference> = ids.map { iconName ->
        val iconFile = appContext.filesDir.resolve("icons/$iconName")
            .apply { parentFile?.mkdirs() }
        gitHubApi.getIconByName(iconName).byteStream().use { input ->
            iconFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        IconReference(
            uniqueFileName = iconName,
            filePath = iconFile.toRelativeString(appContext.filesDir),
        )
    }

    override suspend fun getIconChangeList(after: Int): List<NetworkChangeList> =
        gitHubApi.getIconChangeList().filter { it.changeListVersion > after }
}