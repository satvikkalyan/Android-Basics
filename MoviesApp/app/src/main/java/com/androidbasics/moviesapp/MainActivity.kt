package com.androidbasics.moviesapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.androidbasics.moviesapp.model.MovieDetails
import com.androidbasics.moviesapp.model.MovieSearchResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://www.omdbapi.com/"
private const val API_KEY = "531b7d20"

class MainActivity : AppCompatActivity() {
    private val TAG = "MoviesApp"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val adapter = MoviesAdapter(this)
        recyclerView.adapter = adapter

        initRetrofitAndFetchMovies(adapter)
    }

    private fun initRetrofitAndFetchMovies(adapter: MoviesAdapter) {
        val retrofit = createRetrofitInstance()
        val omdbService = retrofit.create(omniDBService::class.java)

        omdbService.getMovies("Spider", API_KEY).enqueue(object : Callback<MovieSearchResponse> {
            override fun onResponse(call: Call<MovieSearchResponse>, response: Response<MovieSearchResponse>) {
                var respBody = response.body()?.Search?.toMutableList()
                if (respBody != null) {
                    for (i in 0 until respBody.size) {
                        val movieSearchItem = respBody[i]
                        val movieDetails = fetchMovieDetails(movieSearchItem.imdbID)
                        if (movieDetails != null) {
                            movieSearchItem.Type = movieDetails.Genre
                            movieSearchItem.Rating = movieDetails.imdbRating
                            movieSearchItem.IMDBURL = "https://www.imdb.com/title/${movieDetails.imdbID}/"
                            movieSearchItem.duration = "${movieDetails.Runtime} MINS"
                        }
                        respBody[i] = movieSearchItem
                    }
                }
                Log.d("Response From Main",respBody.toString())
                adapter.submitList(respBody)
            }
            override fun onFailure(call: Call<MovieSearchResponse>, t: Throwable) {
                handleFailure(t)
            }
        })
    }

    private fun fetchMovieDetails(imdbID: String): MovieDetails? {
        var movieDetails : MovieDetails? = null;
        val retrofit = createRetrofitInstance()
        val omdbService = retrofit.create(omniDBService::class.java)

        omdbService.getMovieDetails(imdbID, API_KEY).enqueue(object : Callback<MovieDetails> {
            override fun onResponse(call: Call<MovieDetails>, response: Response<MovieDetails>) {
                if (response.isSuccessful) {
                    movieDetails =  response.body()
                } else {
                    Log.i(TAG, "on Error")
                }
            }

            override fun onFailure(call: Call<MovieDetails>, t: Throwable) {
                Log.i(TAG, "onFailure $t")
            }
        })
        return movieDetails
    }

    private fun createRetrofitInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun handleFailure(t: Throwable) {
        Log.i(TAG, "onFailure $t")
    }
}
