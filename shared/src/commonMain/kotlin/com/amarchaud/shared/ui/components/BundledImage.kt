package com.amarchaud.shared.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.ImageBitmap
import com.amarchaud.shared.resources.Res
import org.jetbrains.compose.resources.decodeToImageBitmap

/** State of an image bundled with the app: company logos, profile photo. */
sealed interface BundledImage {
    data object Loading : BundledImage
    data class Loaded(val image: ImageBitmap) : BundledImage
    data object Missing : BundledImage
}

/** Decodes an image of `composeResources/files`: [BundledImage.Missing] when there is none or it cannot be read. */
@Composable
fun rememberBundledImage(path: String?): BundledImage {
    val state by produceState<BundledImage>(
        initialValue = if (path == null) BundledImage.Missing else BundledImage.Loading,
        path,
    ) {
        if (path != null) {
            value = runCatching { Res.readBytes("files/$path").decodeToImageBitmap() }
                .getOrNull()
                ?.let { BundledImage.Loaded(it) }
                ?: BundledImage.Missing
        }
    }
    return state
}

/** Profile photo of the resume, loaded once and shared by every avatar of the screen. */
val LocalProfilePhoto = compositionLocalOf<BundledImage> { BundledImage.Missing }
