package com.androidbasics.moviesapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.androidbasics.moviesapp.databinding.ActivityMainBinding
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
    private lateinit var binding: ActivityMainBinding
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val recyclerView = binding.recyclerView
        val adapter = MoviesAdapter(this)
        recyclerView.adapter = adapter
        binding.imageButton3.setOnClickListener {
            val txtField = binding.editTextMovieTitle
            val searchString = txtField.text.toString()
            initRetrofitAndFetchMovies(adapter, searchString)
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(txtField.windowToken, 0)
        }
        binding.imageButton4.setOnClickListener{
            val subject = "Feedback"
            val developerEmail = "developer@example.com"
            Log.i("Debug","Entered Here")
            val emailIntent = Intent(Intent.ACTION_SENDTO)
            emailIntent.data = Uri.parse("mailto:$developerEmail")
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
            it.context.startActivity(emailIntent)
        }
    }

    private fun initRetrofitAndFetchMovies(adapter: MoviesAdapter, searchString: String) {
        val retrofit = createRetrofitInstance()
        val omdbService = retrofit.create(omniDBService::class.java)

        coroutineScope.launch {
            try {
                val response = omdbService.getMovies(searchString, API_KEY).await()
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
