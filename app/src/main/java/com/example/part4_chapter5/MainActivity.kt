package com.example.part4_chapter5

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isGone
import com.example.part4_chapter5.data.database.DatabaseProvider
import com.example.part4_chapter5.data.entity.GithubOwner
import com.example.part4_chapter5.data.entity.GithubRepoEntity
import com.example.part4_chapter5.databinding.ActivityMainBinding
import com.example.part4_chapter5.view.RepositoryRecyclerAdapter
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    private val job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val repositoryDao by lazy {
        DatabaseProvider.provideDB(applicationContext).repositoryDao()
    }

    private lateinit var repositoryRecyclerAdapter: RepositoryRecyclerAdapter

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        initAdapter()

        /*launch {
            addMockData()
            val githubRepositories = loadGithubRepositories()
            withContext(coroutineContext) {
                Log.d("repositories", githubRepositories.toString())
            }
        }*/

    }
    private fun initAdapter(){
        repositoryRecyclerAdapter = RepositoryRecyclerAdapter{
            startActivity(
                Intent(this, RepositoryActivity::class.java).apply {
                    putExtra(RepositoryActivity.REPOSITORY_OWNER_KEY, it.owner.login)
                    putExtra(RepositoryActivity.REPOSITORY_NAME_KEY, it.name)
                })
        }
        binding.recyclerView.adapter = repositoryRecyclerAdapter
    }

    override fun onResume() {
        super.onResume()
        launch(coroutineContext) {
            loadLikedRepositoryList()
        }

    }

    private suspend fun loadLikedRepositoryList() = withContext(Dispatchers.IO){
        val repositoryList = repositoryDao.getHistory()
        withContext(Dispatchers.Main){
            setData(repositoryList)
        }
    }

    private fun setData(githubRepositoryList:List<GithubRepoEntity>) = with(binding) {
        if (githubRepositoryList.isEmpty()){
            emptyResultTextView.isGone =false
            recyclerView.isGone = true
        }else{
            emptyResultTextView.isGone =true
            recyclerView.isGone = false
            repositoryRecyclerAdapter.submitList(githubRepositoryList)

        }
    }

    private fun initViews() = with(binding) {
        searchButton.setOnClickListener {
            startActivity(
                Intent(this@MainActivity, SearchActivity::class.java)
            )
        }
    }






}