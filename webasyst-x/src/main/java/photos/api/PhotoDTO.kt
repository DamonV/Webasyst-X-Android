package com.webasyst.x.photos.api

import com.google.gson.annotations.SerializedName

data class PhotoDTO(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("ext")
    val ext: String,

    @SerializedName("image_url")
    val imageUrl: String
)
