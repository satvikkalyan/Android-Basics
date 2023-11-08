package com.androidbasics.moviesapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.androidbasics.moviesapp.model.MovieDetails
import com.androidbasics.moviesapp.model.MovieSearchItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.await
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://www.omdbapi.com/"
private const val API_KEY = "531b7d20"

class MainActivity : AppCompatActivity() {
    private val TAG = "MoviesApp"

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
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

        coroutineScope.launch {
            try {
                val response = omdbService.getMovies("Spider", API_KEY).await()
                val movieSearchItems = response.Search ?: emptyList()
                val updatedMovieSearchItems = fetchMovieDetailsForItems(movieSearchItems)
                adapter.submitList(updatedMovieSearchItems)
            } catch (e: Exception) {
                handleFailure(e)
            }
        }
    }

    private suspend fun fetchMovieDetailsForItems(items: List<MovieSearchItem>): List<MovieSearchItem> {
        return withContext(Dispatchers.IO) {
            val updatedItems = mutableListOf<MovieSearchItem>()

            for (item in items) {
                val movieDetails = fetchMovieDetails(item.imdbID)
                if (movieDetails != null) {
                    item.Type = movieDetails.Genre
                    item.Rating = movieDetails.imdbRating
                    item.IMDBURL = "https://www.imdb.com/title/${movieDetails.imdbID}/"
                    item.duration = movieDetails.Runtime
                }
                updatedItems.add(item)
            }

            updatedItems
        }
    }

    private suspend fun fetchMovieDetails(imdbID: String): MovieDetails? {
        val retrofit = createRetrofitInstance()
        val omdbService = retrofit.create(omniDBService::class.java)

        try {
            val response = omdbService.getMovieDetails(imdbID, API_KEY).await()
            return response
        } catch (e: Exception) {
            Log.i(TAG, "onFailure $e")
        }

        return null
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

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
}
