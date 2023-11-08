package com.androidbasics.moviesapp

import androidx.recyclerview.widget.DiffUtil
import com.androidbasics.moviesapp.model.MovieSearchItem

class MovieDiffItemCallback:DiffUtil.ItemCallback<MovieSearchItem>() {
    override fun areItemsTheSame(oldItem: MovieSearchItem, newItem: MovieSearchItem): Boolean {
        return oldItem.Title == newItem.Title
    }

    override fun areContentsTheSame(oldItem: MovieSearchItem, newItem: MovieSearchItem): Boolean {
        return oldItem == newItem
    }
}