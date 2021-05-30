package com.edwin.contacts.presentation

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.edwin.contacts.R
import lv.chi.photopicker.loader.ImageLoader

class GlideImageLoader : ImageLoader {

    override fun loadImage(context: Context, view: ImageView, uri: Uri) {
        Glide.with(context)
            .load(uri)
            .placeholder(ColorDrawable(Color.WHITE))
            .error(R.drawable.ic_person_with_background)
            .centerCrop()
            .into(view)
    }
}