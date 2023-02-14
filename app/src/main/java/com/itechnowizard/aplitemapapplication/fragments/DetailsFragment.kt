package com.itechnowizard.aplitemapapplication.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.itechnowizard.aplitemapapplication.R
import com.itechnowizard.aplitemapapplication.Utils.PrefUtil
import com.itechnowizard.aplitemapapplication.databinding.FragmentDetailsBinding
import com.itechnowizard.aplitemapapplication.resource.Status
import com.itechnowizard.aplitemapapplication.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking


private const val TAG = "DetailsFragment"
@AndroidEntryPoint
class DetailsFragment : Fragment() {


    private var _binding : FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel:LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater,container,false)

        setupProfileData()
        return binding.root
    }

    private fun setupProfileData() {
        runBlocking {
            viewModel.getUserProfileData(PrefUtil(requireContext()).getLastLoginId.first())
        }
        viewModel.getUserProfileDataStatus.observe(requireActivity(), Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d(TAG, "setupProfileData: it.data__${it.data!!.email}")
                    binding.etName.text = "First Name : " + it.data.fname
                    binding.etLastName.text = "Last Name : " + it.data.lname
                    binding.etUsername.text = "User Name : " + it.data.username
                    binding.etEmail.text = "Email : " + it.data.email
                    binding.tvSpinner.text = "Gender : " + it.data.gender
                    binding.etAddress.text = "Address : " + it.data.address
                    binding.etPassword.text = "Password : " + it.data.password
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