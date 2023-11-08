package com.androidbasics.moviesapp

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.androidbasics.moviesapp.databinding.ListItemBinding
import com.androidbasics.moviesapp.model.MovieSearchItem
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class MoviesAdapter(val context: Context)  :
    ListAdapter<MovieSearchItem, MoviesAdapter.ItemViewHolder>(MovieDiffItemCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    return ItemViewHolder.inflateFrom(parent)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, context)
    }
    class ItemViewHolder(val binding: ListItemBinding): RecyclerView.ViewHolder(binding.root){
        companion object {
            fun inflateFrom(parent: ViewGroup): ItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemBinding.inflate(layoutInflater, parent, false)
                return ItemViewHolder(binding)
            }
        }
        fun bind(movie: MovieSearchItem, context: Context) {
            binding.movieName.text = movie.Title
            if(movie.Rating!=null && movie.Rating.toFloat()>0)
                binding.ratingBar.rating = (10/movie.Rating.toFloat())*5
            else
                binding.ratingBar.rating = 0F

            binding.time.text = movie.duration
            binding.genre.text = movie.Type
            Log.d("LOG from adapter",movie.toString())
            Glide.with(context).load(movie.Poster)
                .apply(
                    RequestOptions().transform(
                        CenterCrop(), RoundedCorners(20)
                    )
                )
                .into(binding.imageView)

        }
    }
}