package com.itechnowizard.aplitemapapplication.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.itechnowizard.aplitemapapplication.Utils.PrefUtil
import com.itechnowizard.aplitemapapplication.databinding.ActivityLoginBinding
import com.itechnowizard.aplitemapapplication.resource.Status
import com.itechnowizard.aplitemapapplication.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking

private const val TAG = "LoginActivity"

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.apply {

            btnLogin.setOnClickListener {
                setUpViewModel()
            }

            btnSignup.setOnClickListener {
                startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
            }
        }
    }

    private fun setUpViewModel() {
        viewModel.getUserLoginDataStatus(
            binding.etUsername.text.toString(),
            binding.etPassword.text.toString()
        )

        viewModel.getUserLoginDataStatus.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d(TAG, "setUpViewModel: it.messageSuccess = ${it.message}")
                    Log.d(TAG, "setUpViewModel: it.data = ${it.data}")

                    if (it.data != null) {
                        runBlocking {
                            PrefUtil(this@LoginActivity).saveLastLoginID(it.data.id)
                            PrefUtil(this@LoginActivity).saveLastLogin("Yes")
                        }
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    } else {
//                        Toast.makeText(this, "User does not exist in database.", Toast.LENGTH_LONG)
//                            .show()
                        Snackbar.make(
                            binding.root,
                            "User does not exist in database.",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                }
                Status.ERROR -> {
                    Log.d(TAG, "it.message = ${it.message}")
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }

                else -> {

                }
            }
        })
    }
}