package com.example.part4_chapter5

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.isGone
import com.example.part4_chapter5.databinding.ActivitySignInBinding
import com.example.part4_chapter5.utillity.AuthTokenProvider
import com.example.part4_chapter5.utillity.RetrofitUtil
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class SignInActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var binding: ActivitySignInBinding
    private val job: Job = Job()
    private val coroutineScope = MainScope()

    private val authTokenProvider by lazy {
        AuthTokenProvider(context = this)
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        launch(coroutineContext) {
            val response =
                RetrofitUtil.githubApiService.getLoginInfo("token ${authTokenProvider.token.toString()}")
                    .body()?.login
            Log.d("응답확인", response.toString())
            if (checkAuthCodExist()) {
                launchMainActivity()
            } else {
                initViews()
            }
        }


    }

    private fun initViews() = with(binding) {
        loginButton.setOnClickListener {
            loginGitHub()
        }
    }

    private suspend fun checkAuthCodExist(): Boolean =
        !RetrofitUtil.githubApiService
            .getLoginInfo(
                token = "token ${authTokenProvider.token.toString()}"
            ).body()?.login.isNullOrEmpty()

    private fun launchMainActivity() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }


    private fun loginGitHub() {
        val loginUri = Uri.Builder().scheme("https").authority("github.com")
            .appendPath("login")
            .appendPath("oauth")
            .appendPath("authorize")
            .appendQueryParameter("client_id", BuildConfig.GITHUB_CLIENT_ID)
            .build()

        CustomTabsIntent.Builder().build().also {
            it.launchUrl(this, loginUri)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        intent?.data?.getQueryParameter("code")?.let {
            //todo 엑세스 토큰 받아오기

            launch(coroutineContext) {
                showProgress()
                getAccessToken(it)
                dismissProgress()

            }
        }
    }

    private suspend fun showProgress() = withContext(coroutineContext) {
        with(binding) {
            loginButton.isGone = true
            progressBar.isGone = false
            progressTextView.isGone = false
        }
    }

    private suspend fun dismissProgress() = withContext(coroutineContext) {
        with(binding) {
            loginButton.isGone = false
            progressBar.isGone = true
            progressTextView.isGone = true
        }
    }


    private suspend fun getAccessToken(code: String) = with(Dispatchers.IO) {
        val response = RetrofitUtil.authApiService.getAccessToken(
            clientId = BuildConfig.GITHUB_CLIENT_ID,
            clientSecret = BuildConfig.GITHUB_CLIENT_SECRET,
            code = code
        )
        if (response.isSuccessful) {
            val accessToken = response.body()?.accessToken ?: ""
            Log.d("accessToken", accessToken.toString())
            if (accessToken.isNotEmpty()) {
                authTokenProvider.updateToken(accessToken)
                launchMainActivity()
            } else {
                Toast.makeText(this@SignInActivity, "엑세스 토큰이 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
            }

        }
    }


}
















