package com.webasyst.x.photos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.webasyst.x.R
import com.webasyst.x.databinding.RowPhotosListBinding
import java.lang.ref.WeakReference

class PhotoListAdapter(lifecycleOwner: LifecycleOwner) : ListAdapter<Photo, PhotoListAdapter.PhotoViewHolder>(Companion) {
    private val lifecycleOwnerRef = WeakReference(lifecycleOwner)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder =
        DataBindingUtil.inflate<RowPhotosListBinding>(
            LayoutInflater.from(parent.context),
            R.layout.row_photos_list,
            parent,
            false
        ).let { binding ->
            binding.lifecycleOwner = lifecycleOwnerRef.get()
            PhotoViewHolder(binding)
        }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) =
        holder.bind(getItem(position))

    class PhotoViewHolder(private val binding: RowPhotosListBinding) :
        RecyclerView.ViewHolder(binding.root)
    {
        fun bind(photo: Photo) {
            binding.photo = photo
            binding.executePendingBindings()
        }
    }

    companion object : DiffUtil.ItemCallback<Photo>() {
        override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean =
            oldItem == newItem
    }
}
