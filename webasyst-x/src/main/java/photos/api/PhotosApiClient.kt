package com.webasyst.x.photos.api

import com.webasyst.api.*
import io.ktor.client.request.*
import io.ktor.http.*

class PhotosApiClient (
    config: ApiClientConfiguration,
    installation: Installation,
    waidAuthenticator: WAIDAuthenticator,
) : ApiModule(
    config = config,
    installation = installation,
    waidAuthenticator = waidAuthenticator,
) {
    override val appName get() = SCOPE

    suspend fun getPhotos(): Response<PhotosDTO> =
        client.doGet("$urlBase/api.php/photos.photo.getList") {
            headers {
                accept(ContentType.Application.Json)
            }
        }

    companion object {
        const val SCOPE = "photos"
    }
}
