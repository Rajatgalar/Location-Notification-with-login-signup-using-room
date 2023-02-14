package com.itechnowizard.aplitemapapplication.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isEmpty
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.R
import com.google.android.material.snackbar.Snackbar
import com.itechnowizard.aplitemapapplication.Utils.PrefUtil
import com.itechnowizard.aplitemapapplication.activities.HomeActivity
import com.itechnowizard.aplitemapapplication.databinding.FragmentUpdateBinding
import com.itechnowizard.aplitemapapplication.model.Users
import com.itechnowizard.aplitemapapplication.resource.Status
import com.itechnowizard.aplitemapapplication.viewmodel.LoginViewModel
import com.itechnowizard.aplitemapapplication.viewmodel.SignUpViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

private const val TAG = "UpdateFragment"

@AndroidEntryPoint
class UpdateFragment : Fragment() {


    private var _binding: FragmentUpdateBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()
    private val signUpViewModelviewModel: SignUpViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateBinding.inflate(inflater, container, false)

        binding.apply {
            spinner.adapter = ArrayAdapter(requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                resources.getStringArray(com.itechnowizard.aplitemapapplication.R.array.Gender))
            spinner.prompt = "Select gender"

            btnUpdate.setOnClickListener {
                updateData()
            }
        }

        setupProfileData()
        return binding.root
    }

    private fun updateData() {
        if (validation()) {

            Log.d(TAG, "updateData: validation complete")

            val users = Users(
                fname = binding.etName.text.toString(),
                lname = binding.etLastName.text.toString(),
                username = binding.etUsername.text.toString(),
                email = binding.etEmail.text.toString(),
                password = binding.etPassword.text.toString(),
                gender = binding.spinner.selectedItem.toString(),
                address = binding.etAddress.text.toString()
            )
            signUpViewModelviewModel.updateUserData(users)

            signUpViewModelviewModel.updateUsersDataStatus.observe(requireActivity(), Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        Toast.makeText(requireContext(),"Data Updated",Toast.LENGTH_SHORT).show()
                    }
                    Status.LOADING -> {
                        Log.d(TAG, "updateData: updating")

                    }
                    Status.ERROR -> {
                        Log.d(TAG,
                            "onSignUpClick: it.message = ${it.message}"
                        )

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
        } else
            Toast.makeText(requireContext(), "Incomplete info", Toast.LENGTH_SHORT).show()
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


    private fun setupProfileData() {
        runBlocking {
            viewModel.getUserProfileData(PrefUtil(requireContext()).getLastLoginId.first())
        }
        viewModel.getUserProfileDataStatus.observe(requireActivity(), Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d(TAG, "setupProfileData: it.data__${it.data!!.email}")
                    binding.etName.setText("First Name : " + it.data.fname)
                    binding.etLastName.setText("Last Name : " + it.data.lname)
                    binding.etUsername.setText("User Name : " + it.data.username)
                    binding.etEmail.setText("Email : " + it.data.email)
                    if (it.data.gender == "Male")
                        binding.spinner.setSelection(0)
                    else
                        binding.spinner.setSelection(1)
                    binding.etAddress.setText("Address : " + it.data.address)
                    binding.etPassword.setText("Password : " + it.data.password)
                }
                Status.ERROR -> {
                    Log.d("Error", it.message!!)
                }
                else -> {

                }
            }
        })


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}