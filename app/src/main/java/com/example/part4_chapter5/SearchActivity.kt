package com.example.part4_chapter5

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isGone
import com.example.part4_chapter5.data.entity.GithubRepoEntity
import com.example.part4_chapter5.databinding.ActivitySearchBinding
import com.example.part4_chapter5.utillity.RetrofitUtil
import com.example.part4_chapter5.view.RepositoryRecyclerAdapter
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class SearchActivity : AppCompatActivity(), CoroutineScope {

    private val job = Job()
    private val adapter by lazy {
        RepositoryRecyclerAdapter(clicked = {
            startActivity(
                Intent(this, RepositoryActivity::class.java).apply {
                    putExtra(RepositoryActivity.REPOSITORY_OWNER_KEY, it.owner.login)
                    putExtra(RepositoryActivity.REPOSITORY_NAME_KEY, it.name)
                })
            Toast.makeText(this, "클릭", Toast.LENGTH_SHORT).show()
        })
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var binding: ActivitySearchBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initAdapter()
        initViews()
        bindViews()

    }

    private fun initAdapter() {
        binding.recyclerView.adapter = adapter
    }

    private fun initViews() = with(binding) {
        emptyResultTextView.isGone = true
        recyclerView
    }

    private fun bindViews() = with(binding) {
        searchButton.setOnClickListener {
            searchKeyword(searchBarInputView.text.toString())
        }

    }

    private fun searchKeyword(keyword: String) = launch {
        withContext(Dispatchers.IO) {
            val response = RetrofitUtil.githubApiService.searchRepositories(keyword)
            if (response.isSuccessful) {
                val body = response.body()
                withContext(Dispatchers.Main) {
                    Log.d("결과", body.toString())
                    setData(body?.items)
                }
            } else {
                binding.emptyResultTextView.isGone = true
            }
        }

    }

    private fun setData(items: List<GithubRepoEntity>?) {
        adapter.submitList(items)
    }


}