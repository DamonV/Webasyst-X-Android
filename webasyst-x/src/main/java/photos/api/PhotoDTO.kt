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

/*{
id: "308"
name: "IMG_2556"
description: ""
ext: "jpg"
size: "2376124"
type: "2"
rate: "0.00"
width: "3264"
height: "2448"
contact_id: "3"
upload_datetime: "2014-12-28 18:28:46"
edit_datetime: null
status: "1"
hash: ""
url: "img_2556"
parent_id: "0"
stack_count: "13"
sort: "0"
source: "backend"
app_id: null
image_url: "/wa-data/public/photos/08/03/308/308.300x0.jpg"*/
