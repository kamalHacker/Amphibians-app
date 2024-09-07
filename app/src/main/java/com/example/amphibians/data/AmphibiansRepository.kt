package com.example.amphibians.data

import android.util.Log
import com.example.amphibians.network.AmphibiansApiService
import com.example.amphibians.network.AmphibiansPhoto
import retrofit2.Response

interface AmphibiansRepository {
    suspend fun getAmphibiansPhotos(): List<AmphibiansPhoto>
}

class NetworkAmphibiansRepository(
    private val amphibiansApiService: AmphibiansApiService
) : AmphibiansRepository {
    override suspend fun getAmphibiansPhotos(): List<AmphibiansPhoto> {
        // Log the URL before making the request
        Log.d("NetworkAmphibiansRepository", "Fetching from: ${amphibiansApiService.getPhotos().raw().request.url}")

        val photosResponse: Response<List<AmphibiansPhoto>> = amphibiansApiService.getPhotos()
        return if (photosResponse.isSuccessful) {
            photosResponse.body() ?: emptyList()
        } else {
            Log.d(
                "NetworkAmphibiansRepository",
                "Response is not successful: ${photosResponse.code()}"
            )
            emptyList()
        }
    }
}