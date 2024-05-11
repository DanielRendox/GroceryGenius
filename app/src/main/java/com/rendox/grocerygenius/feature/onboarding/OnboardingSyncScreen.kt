package com.rendox.grocerygenius.feature.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rendox.grocerygenius.R
import com.rendox.grocerygenius.sync.work.status.SyncStatus
import com.rendox.grocerygenius.ui.theme.GroceryGeniusTheme

@Composable
fun OnboardingSyncRoute(
    viewModel: OnboardingViewModel = hiltViewModel(),
    closeOnboarding: () -> Unit,
) {
    val syncStatus by viewModel.syncStatusFlow.collectAsStateWithLifecycle()

    LaunchedEffect(syncStatus) {
        if (syncStatus == SyncStatus.SUCCEEDED) {
            closeOnboarding()
        }
    }

    OnboardingSyncScreen(
        syncStatus = syncStatus,
        onRetrySync = viewModel::onRetrySync,
        closeOnboarding = closeOnboarding,
    )
}

@Composable
private fun OnboardingSyncScreen(
    syncStatus: SyncStatus,
    onRetrySync: () -> Unit,
    closeOnboarding: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = when (syncStatus) {
                    SyncStatus.RUNNING -> stringResource(R.string.onboarding_sync_status_running_title)
                    SyncStatus.SUCCEEDED -> stringResource(R.string.onboarding_sync_status_succeeded_title)
                    SyncStatus.FAILED -> stringResource(R.string.onboarding_sync_status_failed_title)
                    SyncStatus.OFFLINE -> stringResource(R.string.onboarding_sync_status_offline_title)
                },
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .height(72.dp),
                contentAlignment = Alignment.Center,
            ) {
                when (syncStatus) {
                    SyncStatus.RUNNING -> {
                        CircularProgressIndicator()
                    }

                    SyncStatus.SUCCEEDED -> {
                        Icon(
                            modifier = Modifier.fillMaxSize(),
                            painter = painterResource(R.drawable.checkmark_circle),
                            contentDescription = null,
                        )
                    }

                    SyncStatus.OFFLINE -> {
                        Icon(
                            modifier = Modifier.fillMaxSize(),
                            painter = painterResource(R.drawable.cloud_off),
                            contentDescription = null,
                        )
                    }

                    SyncStatus.FAILED -> {
                        Icon(
                            modifier = Modifier.fillMaxSize(),
                            painter = painterResource(R.drawable.cancel_circle),
                            contentDescription = null,
                        )
                    }
                }
            }

            Text(
                modifier = Modifier
                    .padding(horizontal = 32.dp, vertical = 16.dp),
                text = stringResource(R.string.onboarding_sync_description),
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
            )

            Column(
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .width(IntrinsicSize.Max)
            ) {
                AnimatedVisibility(visible = syncStatus == SyncStatus.FAILED) {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        onClick = onRetrySync,
                        enabled = syncStatus == SyncStatus.FAILED,
                    ) {
                        Text(text = stringResource(R.string.retry))
                    }
                }
                FilledTonalButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = closeOnboarding,
                ) {
                    Text(
                        text = stringResource(R.string.onboarding_skip_sync_button_title)
                    )
                }
            }
        }
    }
}

class OnboardingSyncScreenPreviewParameter : PreviewParameterProvider<SyncStatus> {
    override val values: Sequence<SyncStatus>
        get() = SyncStatus.entries.asSequence()
}

@Preview
@Composable
private fun OnboardingSyncScreenPreview(
    @PreviewParameter(provider = OnboardingSyncScreenPreviewParameter::class) syncStatus: SyncStatus
) {
    GroceryGeniusTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            OnboardingSyncScreen(
                syncStatus = syncStatus,
                onRetrySync = {},
                closeOnboarding = {},
            )
        }
    }
}
