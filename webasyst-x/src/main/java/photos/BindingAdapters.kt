package com.webasyst.x.photos

import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.webasyst.x.MainActivity.Companion.TAG
import com.webasyst.x.R

object ImageBindingAdapter {
    @JvmStatic
    @BindingAdapter("imageUrl")
    fun setImage(view: ImageView, imageUrl: String?) {
        imageUrl?.let {
            Log.d(TAG, it)

            Glide.with(view.context).load(it).placeholder(R.drawable.ic_image_placeholder).into(view)
        }
    }
}
