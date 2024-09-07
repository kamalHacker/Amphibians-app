package com.example.amphibians.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.amphibians.R
import com.example.amphibians.network.AmphibiansPhoto

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    amphibiansViewModel: AmphibiansViewModel,
    retryAction: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(8.dp),
){
    var amphibiansUiState by remember { mutableStateOf<AmphibiansUiState>(AmphibiansUiState.Loading) }

    LaunchedEffect(Unit) {
        amphibiansViewModel.amphibiansUiState.collect { newState ->
            amphibiansUiState = newState // Update the value within the State
        }
    }

    when (amphibiansUiState) {
        is AmphibiansUiState.Loading -> LoadingScreen(modifier)
        is AmphibiansUiState.Success -> AmphibiansCardList(
            photos = (amphibiansUiState as AmphibiansUiState.Success).photos,
            modifier = modifier
        )
        else -> ErrorScreen(modifier, retryAction)
    }

}

@Composable
fun AmphibiansCardList(
    photos: List<AmphibiansPhoto>,
    modifier: Modifier = Modifier,
) {
    LazyColumn {
        items(items = photos, key = { photo -> photo.name }) { photo ->
            AmphibiansPhotoCard(photo, modifier.padding(4.dp))
        }
    }
}


@Composable
fun AmphibiansPhotoCard(photo: AmphibiansPhoto, modifier: Modifier = Modifier) {
    Log.d("MarsPhotoCard", "Photo URL: ${photo.img_src}")
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = photo.name + " (" + photo.type + ")",
                style = MaterialTheme.typography.titleLarge,
                modifier = modifier.padding(8.dp)
            )
            AsyncImage(
                model = ImageRequest.Builder(context = LocalContext.current).data(photo.img_src)
                    .crossfade(true).build(),
                error = painterResource(R.drawable.ic_broken_image),
                placeholder = painterResource(R.drawable.loading_img),
                contentDescription = stringResource(R.string.amphibians_photo),
                contentScale = ContentScale.Crop,
                modifier = modifier.fillMaxWidth(),
                onError = { exception ->
                    Log.e("AsyncImage", "Error loading image: $exception")
                }
            )
            Text(
                text = photo.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun LoadingScreen(modifier: Modifier) {
    Image(
        modifier = modifier
            .height(200.dp)
            .width(200.dp),
        painter = painterResource(R.drawable.loading_img),
        contentDescription = stringResource(R.string.loading)
    )
}

@Composable
fun ErrorScreen(
    modifier:Modifier = Modifier,
    retryAction: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_connection_error),
            contentDescription = ""
        )
        Text(
            text = stringResource(R.string.loading_failed),
            modifier = Modifier.padding(16.dp)
        )
        Button(onClick = retryAction) {
            Text(stringResource(R.string.retry))
        }
    }
}