package com.amarchaud.shared.ui.resume

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.amarchaud.shared.data.ContactModel
import com.amarchaud.shared.data.displayUrl
import com.amarchaud.shared.platform.plainTextClipEntry
import com.amarchaud.shared.resources.Res
import com.amarchaud.shared.resources.contact_copy_email
import com.amarchaud.shared.resources.contact_cta_default
import com.amarchaud.shared.resources.contact_cta_title
import com.amarchaud.shared.resources.contact_email
import com.amarchaud.shared.resources.contact_email_copied
import com.amarchaud.shared.resources.contact_github
import com.amarchaud.shared.resources.contact_linkedin
import com.amarchaud.shared.resources.contact_phone
import com.amarchaud.shared.resources.contact_send_email
import com.amarchaud.shared.resources.contact_title
import com.amarchaud.shared.ui.components.IconBadge
import com.amarchaud.shared.ui.components.ResumeCard
import com.amarchaud.shared.ui.components.Reveal
import com.amarchaud.shared.ui.components.SectionHeader
import com.amarchaud.shared.ui.components.SectionList
import com.amarchaud.shared.ui.components.heroBackground
import com.amarchaud.shared.ui.components.openSafely
import com.amarchaud.shared.ui.components.rememberRevealState
import com.amarchaud.shared.ui.components.staggerDelay
import com.amarchaud.shared.ui.icons.AppIcons
import com.amarchaud.shared.ui.preview.PreviewData
import com.amarchaud.shared.ui.theme.ResumeTheme
import com.amarchaud.shared.ui.theme.ResumeAppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

private data class ContactLink(
    val icon: ImageVector,
    val label: String,
    val value: String,
    val uri: String,
)

@Composable
fun ContactSection(
    contact: ContactModel,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val revealState = rememberRevealState()
    val uriHandler = LocalUriHandler.current
    val openUri: (String) -> Unit = { uri -> uriHandler.openSafely(uri) }
    val emailUri = "mailto:${contact.email}"

    val links = buildList {
        add(ContactLink(AppIcons.Mail, stringResource(Res.string.contact_email), contact.email, emailUri))
        contact.phone?.let { add(ContactLink(AppIcons.Call, stringResource(Res.string.contact_phone), it, "tel:$it")) }
        contact.linkedInUrl?.let {
            add(ContactLink(AppIcons.LinkedIn, stringResource(Res.string.contact_linkedin), displayUrl(it), it))
        }
        contact.githubUrl?.let {
            add(ContactLink(AppIcons.GitHub, stringResource(Res.string.contact_github), displayUrl(it), it))
        }
    }

    SectionList(contentPadding = contentPadding, modifier = modifier) {
        item(key = "header") {
            SectionHeader(
                title = stringResource(Res.string.contact_title),
                modifier = Modifier.padding(bottom = 20.dp),
            )
        }
        item(key = "cta") {
            Reveal(key = "cta", state = revealState) {
                CallToActionCard(
                    message = contact.message ?: stringResource(Res.string.contact_cta_default),
                    onSendEmail = { openUri(emailUri) },
                )
            }
        }
        item(key = "links") {
            Reveal(key = "links", state = revealState, delayMillis = staggerDelay(1)) {
                ResumeCard(modifier = Modifier.padding(top = 20.dp)) {
                    links.forEachIndexed { index, link ->
                        if (index > 0) {
                            HorizontalDivider(
                                modifier = Modifier.padding(start = 76.dp),
                                color = ResumeTheme.colors.cardBorder,
                            )
                        }
                        ContactRow(
                            link = link,
                            onClick = { openUri(link.uri) },
                            trailing = if (link.uri == emailUri) {
                                { CopyEmailButton(email = contact.email) }
                            } else {
                                null
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CallToActionCard(message: String, onSendEmail: () -> Unit) {
    val colors = ResumeTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .heroBackground(colors)
            .padding(24.dp),
    ) {
        Text(
            text = stringResource(Res.string.contact_cta_title),
            style = MaterialTheme.typography.headlineSmall,
            color = colors.onHero,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = colors.onHeroMuted,
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = onSendEmail,
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.onHero,
                contentColor = colors.heroStart,
            ),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        ) {
            Icon(imageVector = AppIcons.Mail, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = stringResource(Res.string.contact_send_email), style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun ContactRow(
    link: ContactLink,
    onClick: () -> Unit,
    trailing: (@Composable () -> Unit)?,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClickLabel = link.label, onClick = onClick)
            .padding(start = 18.dp, end = 8.dp, top = 14.dp, bottom = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconBadge(icon = link.icon, size = 44.dp, iconSize = 20.dp)
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = link.label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = link.value,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (trailing != null) {
            trailing()
        } else {
            Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = AppIcons.ArrowOutward,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

/** Copies the email address, and confirms it in place for a couple of seconds. */
@Composable
private fun CopyEmailButton(email: String) {
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    var copied by remember { mutableStateOf(false) }
    LaunchedEffect(copied) {
        if (copied) {
            delay(COPY_CONFIRMATION_MILLIS)
            copied = false
        }
    }
    IconButton(
        onClick = {
            scope.launch { clipboard.setClipEntry(plainTextClipEntry(email)) }
            copied = true
        },
    ) {
        Crossfade(targetState = copied, label = "copy-email") { isCopied ->
            Icon(
                imageVector = if (isCopied) AppIcons.Check else AppIcons.ContentCopy,
                contentDescription = stringResource(
                    if (isCopied) Res.string.contact_email_copied else Res.string.contact_copy_email,
                ),
                modifier = Modifier.size(20.dp),
                tint = if (isCopied) ResumeTheme.colors.positive else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private const val COPY_CONFIRMATION_MILLIS = 2_000L

@PreviewLightDark
@Composable
private fun ContactSectionPreview() {
    ResumeAppTheme {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            ContactSection(contact = PreviewData.resume.contact, contentPadding = PaddingValues(20.dp))
        }
    }
}
