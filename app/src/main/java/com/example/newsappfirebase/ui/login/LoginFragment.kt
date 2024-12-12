package com.example.newsappfirebase.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.newsappfirebase.databinding.FragmentLoginBinding
import com.example.newsappfirebase.ui.base.BaseFragment

class LoginFragment:BaseFragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}