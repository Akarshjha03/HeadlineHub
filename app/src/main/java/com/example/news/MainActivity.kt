package com.example.news

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.news.adapter.NewsAdapter
import com.example.news.api.NewsApiService
import com.example.news.databinding.ActivityMainBinding
import com.example.news.model.NewsResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val newsAdapter = NewsAdapter()
    private lateinit var newsApiService: NewsApiService

    companion object {
        private const val BASE_URL = "https://newsapi.org/"
        private const val API_KEY = "595beb4f3cf340c4a6ad62b639f3896b" // Replace with your actual API key
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupSwipeRefresh()
        setupNewsApi()
        fetchNews()
    }

    private fun setupRecyclerView() {
        binding.newsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = newsAdapter
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            fetchNews()
        }
    }

    private fun setupNewsApi() {
        newsApiService = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApiService::class.java)
    }

    private fun fetchNews() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = newsApiService.getTopHeadlines(apiKey = API_KEY)
                withContext(Dispatchers.Main) {
                    newsAdapter.updateArticles(response.articles)
                    binding.swipeRefreshLayout.isRefreshing = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MainActivity,
                        "Error fetching news: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    binding.swipeRefreshLayout.isRefreshing = false
                }
            }
        }
    }
}