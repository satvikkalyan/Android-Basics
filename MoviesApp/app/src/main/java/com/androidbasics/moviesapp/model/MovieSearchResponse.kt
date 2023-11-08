package com.androidbasics.moviesapp.model

data class MovieSearchResponse(
    val Search: List<MovieSearchItem>,
    val totalResults: String,
    val Response: String
)

data class MovieSearchItem(
    val Title: String,
    var Year: String,
    val imdbID: String,
    var Type: String,
    val Poster: String,
    var Rating: String,
    var IMDBURL: String,
    var duration: String
)
