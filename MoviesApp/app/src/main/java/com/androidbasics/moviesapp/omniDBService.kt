package com.androidbasics.moviesapp

import com.androidbasics.moviesapp.model.MovieDetails
import com.androidbasics.moviesapp.model.MovieSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

public interface omniDBService {
    @GET("/")
    fun getMovies(
    @Query("s") movieName: String,
    @Query("apikey") apiKey: String
    ): Call<MovieSearchResponse>

    @GET("/")
    fun getMovieDetails(
        @Query("i") imdbID: String,
        @Query("apikey") apiKey: String
    ): Call<MovieDetails>

}
