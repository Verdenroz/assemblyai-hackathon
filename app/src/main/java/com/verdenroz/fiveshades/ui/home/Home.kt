package com.verdenroz.fiveshades.ui.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.verdenroz.fiveshades.model.Response
import com.verdenroz.fiveshades.model.Shade
import com.verdenroz.fiveshades.model.Transcription

@Composable
fun HomeRoute(
    homeViewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasPermission = isGranted
    }

    val shade by homeViewModel.shade.collectAsStateWithLifecycle()
    val transcriptions by homeViewModel.transcriptions.collectAsStateWithLifecycle()
    val responses by homeViewModel.responses.collectAsStateWithLifecycle()
    val isListening by homeViewModel.isListening.collectAsStateWithLifecycle()
    HomeScreen(
        shade = shade,
        hasPermission = hasPermission,
        requestPermissionLauncher = requestPermissionLauncher,
        transcriptions = transcriptions,
        responses = responses,
        isListening = isListening,
        onPreviousShade = homeViewModel::onPreviousShade,
        onNextShade = homeViewModel::onNextShade,
        startTranscription = homeViewModel::startTranscription,
        stopTranscription = homeViewModel::stopTranscription
    )
}

@Composable
fun HomeScreen(
    shade: Shade,
    hasPermission: Boolean,
    requestPermissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
    transcriptions: List<Transcription>,
    responses: List<Response>,
    isListening: Boolean,
    onPreviousShade: () -> Unit,
    onNextShade: () -> Unit,
    startTranscription: () -> Unit,
    stopTranscription: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = shade.name,
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(16.dp)
            )
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier
                    .padding(16.dp)
                    .size(128.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(transcriptions.size) { index ->
                    UserTranscription(transcriptions[index])
                    if (index < responses.size) {
                        ShadeResponse(responses[index])
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onPreviousShade) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = null
                )
            }
            IconButton(onClick = {
                if (!hasPermission) {
                    requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }

                if (isListening) {
                    stopTranscription()
                } else {
                    startTranscription()
                }
            }) {
                if (isListening) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = null
                    )
                }
            }
            IconButton(onClick = onNextShade) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowForward,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
private fun BoxScope.UserTranscription(
    transcription: Transcription,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .padding(8.dp)
            .background(Color.Blue.copy(alpha = 0.6f), shape = RoundedCornerShape(8.dp))
            .align(Alignment.CenterEnd)
    ) {
        Text(
            text = transcription.text,
            color = Color.White,
            modifier = Modifier
                .padding(8.dp)
        )
    }
}


@Composable
private fun BoxScope.ShadeResponse(
    response: Response,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .padding(8.dp)
            .background(Color.Gray.copy(alpha = 0.6f), shape = RoundedCornerShape(8.dp))
            .align(Alignment.CenterStart)
    ) {
        Text(
            text = if (response.isLoading) "Thinking..." else response.message!!,
            color = Color.Black,
            modifier = Modifier
                .padding(8.dp)
        )
    }
}

@Preview
@Composable
fun UserTranscriptionPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        UserTranscription(Transcription("Hello, World!", true))
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    var shade by remember {
        mutableStateOf(Shade.entries.toTypedArray().random())
    }
    HomeScreen(
        shade = shade,
        hasPermission = true,
        requestPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { },
        transcriptions = emptyList(),
        responses = emptyList(),
        isListening = false,
        onPreviousShade = { shade = Shade.previous(shade) },
        onNextShade = { shade = Shade.next(shade) },
        startTranscription = { },
        stopTranscription = { }
    )
}