package com.example.gallery.presentation

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.gallery.R
import com.example.gallery.databinding.ItemImageBinding


class GalleryAdapter : RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {

    private var images = listOf<Uri?>()

    fun updateMediaFiles(data: List<Uri?>) {
        images = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryAdapter.ViewHolder {
        return ViewHolder(
            ItemImageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: GalleryAdapter.ViewHolder, position: Int) =
        holder.bind(images[position])

    override fun getItemCount(): Int = images.size

    inner class ViewHolder(private val binding: ItemImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val ctx: Context = binding.root.context

        fun bind(uri: Uri?) {
            if (uri?.path?.contains("video") == true) binding.ivPlay.visibility = View.VISIBLE
            else binding.ivPlay.visibility = View.GONE

            Glide.with(ctx)
                .load(uri)
                .placeholder(circularDrawable(ctx))
                .error(R.drawable.ic_error_image)
                .into(binding.iv)
        }

    }

    fun circularDrawable(context: Context): CircularProgressDrawable {
        val drawable = CircularProgressDrawable(context)
        drawable.setColorSchemeColors(android.R.attr.colorAccent)
        drawable.centerRadius = 30f
        drawable.strokeWidth = 5f
        drawable.start()
        return drawable
    }
}