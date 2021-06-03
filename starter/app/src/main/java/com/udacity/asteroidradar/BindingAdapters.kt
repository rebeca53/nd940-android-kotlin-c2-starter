package com.udacity.asteroidradar

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.main.AsteroidListAdapter
import com.udacity.asteroidradar.main.MainViewModel

@BindingAdapter("statusIcon")
fun bindAsteroidStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.ic_status_potentially_hazardous)
        imageView.contentDescription = R.string.potentially_hazardous_asteroid_image.toString()
    } else {
        imageView.setImageResource(R.drawable.ic_status_normal)
        imageView.contentDescription = R.string.not_hazardous_asteroid_image.toString()

    }
}

@BindingAdapter("asteroidList")
fun bindRecyclerViewToAsteroidList(recyclerView: RecyclerView, data: List<Asteroid>?) {
    val adapter = recyclerView.adapter as AsteroidListAdapter
    adapter.submitList(data)
}

@BindingAdapter("asteroidStatusImage")
fun bindDetailsStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.asteroid_hazardous)
        imageView.contentDescription = R.string.potentially_hazardous_asteroid_image.toString()
    } else {
        imageView.setImageResource(R.drawable.asteroid_safe)
        imageView.contentDescription = R.string.not_hazardous_asteroid_image.toString()
    }
}

@BindingAdapter("astronomicalUnitText")
fun bindTextViewToAstronomicalUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.astronomical_unit_format), number)
}

@BindingAdapter("kmUnitText")
fun bindTextViewToKmUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_unit_format), number)
}

@BindingAdapter("velocityText")
fun bindTextViewToDisplayVelocity(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_s_unit_format), number)
}

@BindingAdapter("pictureOfDay")
fun bindImageViewToPictureOfDay(imageView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        Picasso.with(imageView.context)
            .load(imgUri)
            .placeholder(R.drawable.loading_animation)
            .error(R.drawable.ic_broken_image)
            .into(imageView)
    }
}

@BindingAdapter("pictureOfDayContentDescription")
fun bindContentDescriptionToPictureOfDay(imageView: ImageView, title: String?) {
    title?.let {
        val context = imageView.context
        imageView.contentDescription = String.format(context.getString(R.string.nasa_picture_of_day_content_description_format), title)
    }
}

@BindingAdapter("asteroidNASAApiStatus")
fun bindAsteroidStatus(statusImageView: ImageView, status: MainViewModel.NASAApiStatus?) {
    when (status) {
        MainViewModel.NASAApiStatus.LOADING -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.loading_animation)
        }
        MainViewModel.NASAApiStatus.ERROR -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.ic_connection_error)
        }
        MainViewModel.NASAApiStatus.DONE -> {
            statusImageView.visibility = View.GONE
        }
    }
}

@BindingAdapter("imageOfDayNASAApiStatus")
fun bindImageOfDayStatus(statusImageView: ImageView, status: MainViewModel.NASAApiStatus?) {
    when (status) {
        MainViewModel.NASAApiStatus.LOADING -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.loading_animation)
        }
        MainViewModel.NASAApiStatus.ERROR -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.ic_broken_image)
        }
        MainViewModel.NASAApiStatus.DONE -> {
            statusImageView.visibility = View.GONE
        }
    }
}


