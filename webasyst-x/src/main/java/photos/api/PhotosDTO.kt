package com.webasyst.x.photos.api

import com.google.gson.annotations.SerializedName

data class PhotosDTO (
    @SerializedName("offset")
    val offset: Int,
    @SerializedName("limit")
    val limit: Int,
    @SerializedName("count")
    val count: Int,
    @SerializedName("photos")
    val photos: List<PhotoDTO>
)
