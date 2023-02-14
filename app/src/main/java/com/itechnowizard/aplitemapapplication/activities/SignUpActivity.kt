package com.itechnowizard.aplitemapapplication.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isEmpty
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.itechnowizard.aplitemapapplication.R
import com.itechnowizard.aplitemapapplication.Utils.PrefUtil
import com.itechnowizard.aplitemapapplication.databinding.ActivitySignUpBinding
import com.itechnowizard.aplitemapapplication.model.Users
import com.itechnowizard.aplitemapapplication.resource.Status
import com.itechnowizard.aplitemapapplication.viewmodel.SignUpViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking

private const val TAG = "SignUpActivity"
@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private val viewModel: SignUpViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            spinner.adapter = ArrayAdapter(this@SignUpActivity,
                com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
                resources.getStringArray(R.array.Gender))
            spinner.prompt = "Select gender"

            btnSignup.setOnClickListener {
                onSignUpClick()
            }

        }
    }

    private fun onSignUpClick() {
        if (validation()) {
            val users = Users(
                fname = binding.etName.text.toString(),
                lname = binding.etLastName.text.toString(),
                username = binding.etUsername.text.toString(),
                email = binding.etEmail.text.toString(),
                password = binding.etPassword.text.toString(),
                gender = binding.spinner.selectedItem.toString(),
                address = binding.etAddress.text.toString()
            )
            viewModel.insertUserData(users)
            viewModel.insertUsersDataStatus.observe(this, Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        startActivity(Intent(this, HomeActivity::class.java))
                        runBlocking {
                            PrefUtil(this@SignUpActivity).saveLastLogin("Yes")
                            PrefUtil(this@SignUpActivity).saveLastLoginID(it.data!!)
                        }
                        finish()
                    }
                    Status.LOADING -> {

                    }
                    Status.ERROR -> {
                        Log.d(TAG, "onSignUpClick: it.message = ${it.message}")

                        Snackbar.make(
                            binding.root,
                            it.message.toString(),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                    else -> {

                    }
                }
            })
        }else
            Toast.makeText(this,"Incomplete info",Toast.LENGTH_SHORT).show()
    }

    private fun validation(): Boolean {
        return when {
            binding.etName.text.isNullOrEmpty() -> {
                false
            }
            binding.etLastName.text.isNullOrEmpty() -> {
                false
            }
            binding.etUsername.text.isNullOrEmpty() -> {
                false
            }
            binding.etEmail.text.isNullOrEmpty() -> {
                false
            }
            binding.etAddress.text.isNullOrEmpty() -> {
                false
            }
            binding.etPassword.text.isNullOrEmpty() -> {
                false
            }
            binding.spinner.isEmpty() -> {
                false
            }
            else -> {
                true
            }
        }
    }

}