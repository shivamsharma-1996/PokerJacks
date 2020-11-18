package com.gtgt.pokerjacks.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.gtgt.pokerjacks.R

@BindingAdapter("app:image_src")
fun ImageView.loadImage(path: String?) {
    try {
        if (!path.isNullOrEmpty()) {
            if (path.toIntOrNull() != null) {
                Glide.with(context).load(path.toInt()).placeholder(R.drawable.ic_profile_placeholder).into(this)
            } else {
                Glide.with(context).load(path).placeholder(R.drawable.ic_profile_placeholder).into(this)
            }
        } else {
            setImageResource(R.drawable.ic_profile_placeholder)
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
}

fun ImageView.loadImage(resourse: Int) {
    Glide.with(context).load(resourse).into(this)
}