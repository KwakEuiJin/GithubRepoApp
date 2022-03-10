package com.example.part4_chapter5

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import com.example.part4_chapter5.data.database.DatabaseProvider
import com.example.part4_chapter5.data.entity.GithubRepoEntity
import com.example.part4_chapter5.databinding.ActivityRepositoryBinding
import com.example.part4_chapter5.extensions.loadCenterInside
import com.example.part4_chapter5.utillity.RetrofitUtil
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class RepositoryActivity : AppCompatActivity(), CoroutineScope {

    val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    private val repositoryDao by lazy {
        DatabaseProvider.provideDB(applicationContext).repositoryDao()
    }
    private lateinit var binding: ActivityRepositoryBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRepositoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repositoryOwner = intent.getStringExtra(REPOSITORY_OWNER_KEY) ?: kotlin.run {
            Toast.makeText(this, "이름이 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        val repositoryName = intent.getStringExtra(REPOSITORY_NAME_KEY) ?: kotlin.run {
            Toast.makeText(this, "이름이 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        launch {
            loadRepository(repositoryOwner, repositoryName)?.let {
                setData(it)
            } ?: kotlin.run {
                Toast.makeText(this@RepositoryActivity, "정보가 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private suspend fun loadRepository(
        repositoryOwner: String,
        repositoryName: String
    ): GithubRepoEntity? =
        withContext(coroutineContext) {
            var repositoryEntity: GithubRepoEntity? = null
            withContext(Dispatchers.IO) {
                val response = RetrofitUtil.githubApiService.getRepository(
                    ownerLogin = repositoryOwner,
                    repoName = repositoryName
                )
                if (response.isSuccessful) {
                    val body = response.body()
                    withContext(Dispatchers.Main) {
                        body?.let { repo ->
                            repositoryEntity = repo
                        }
                    }
                }
            }
            repositoryEntity
        }

    private fun setData(githubRepoEntity: GithubRepoEntity) = with(binding) {
        showLoading(false)
        ownerProfileImageView.loadCenterInside(githubRepoEntity.owner.avatarUrl, 42f)
        ownerNameAndRepoNameTextView.text =
            "${githubRepoEntity.owner.login} / ${githubRepoEntity.name}"
        stargazersCountText.text = githubRepoEntity.stargazerCount.toString()
        githubRepoEntity.language?.let {
            languageText.isGone = false
            languageText.text = it
        } ?: kotlin.run {
            languageText.isGone = true
            languageText.text = ""
        }
        descriptionTextView.text = githubRepoEntity.description
        updateTimeTextView.text = githubRepoEntity.updatedAt

        setLikeState(githubRepoEntity)
    }

    private fun setLikeState(githubRepoEntity: GithubRepoEntity) = launch {
        withContext(Dispatchers.IO) {
            val repo = repositoryDao.getRepository(githubRepoEntity.fullName)
            val isLike = repo != null
            withContext(Dispatchers.Main) {
                setLikeImage(isLike)
                binding.likeButton.setOnClickListener {
                    likeRepo(githubRepoEntity,isLike)
                }
            }
        }
    }

    private fun setLikeImage(isLike: Boolean) {
        binding.likeButton.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                if (isLike) {
                    R.drawable.ic_like
                } else {
                    R.drawable.ic_dislike
                }
            )
        )
    }

    private fun likeRepo(githubRepoEntity: GithubRepoEntity, isLike: Boolean)= launch{
        withContext(Dispatchers.IO){
            if (isLike){
                repositoryDao.remove(githubRepoEntity.fullName)
            }else{
                repositoryDao.insert(githubRepoEntity)
            }
            withContext(Dispatchers.Main){
                setLikeImage(isLike.not())
            }
        }


    }


    private fun showLoading(isShown: Boolean) = with(binding) {
        progressBar.isGone = !isShown
    }


    companion object {
        const val REPOSITORY_OWNER_KEY = "REPOSITORY_OWNER_KEY"
        const val REPOSITORY_NAME_KEY = "REPOSITORY_NAME_KEY"
    }


}