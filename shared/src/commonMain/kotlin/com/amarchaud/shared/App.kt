package com.amarchaud.shared

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.amarchaud.shared.platform.isDynamicColorAvailable
import com.amarchaud.shared.ui.ResumeUiState
import com.amarchaud.shared.ui.ResumeViewModel
import com.amarchaud.shared.ui.resume.DynamicColorAction
import com.amarchaud.shared.ui.resume.ResumeScreen
import com.amarchaud.shared.ui.theme.ResumeAppTheme
import org.koin.compose.viewmodel.koinViewModel

/** Root of the app, shared by every platform. */
@Composable
fun App(viewModel: ResumeViewModel = koinViewModel()) {
    var dynamicColor by rememberSaveable { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()

    ResumeAppTheme(dynamicColor = dynamicColor) {
        Crossfade(targetState = uiState, label = "resume-state") { state ->
            when (state) {
                ResumeUiState.Loading -> Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                )

                is ResumeUiState.Error -> Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                    )
                }

                is ResumeUiState.Success -> ResumeScreen(
                    resume = state.resume,
                    dynamicColor = if (isDynamicColorAvailable) {
                        DynamicColorAction(enabled = dynamicColor, onToggle = { dynamicColor = it })
                    } else {
                        null
                    },
                )
            }
        }
    }
}
