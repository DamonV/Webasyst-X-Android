package com.webasyst.x.photos.api

import com.webasyst.api.ApiClientConfiguration
import com.webasyst.api.ApiModuleFactory
import com.webasyst.api.Installation
import com.webasyst.api.WAIDAuthenticator

class PhotosApiClientFactory(
    private val config: ApiClientConfiguration,
    private val waidAuthenticator: WAIDAuthenticator,
) : ApiModuleFactory<PhotosApiClient>() {
    override val scope = PhotosApiClient.SCOPE

    override fun instanceForInstallation(installation: Installation): PhotosApiClient {
        return PhotosApiClient(
            config = config,
            waidAuthenticator = waidAuthenticator,
            installation = installation,
        )
    }
}
