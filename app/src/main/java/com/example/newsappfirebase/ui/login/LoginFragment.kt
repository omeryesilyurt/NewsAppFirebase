package com.example.newsappfirebase.ui.login

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.newsappfirebase.R
import com.example.newsappfirebase.databinding.FragmentLoginBinding
import com.example.newsappfirebase.ui.base.BaseFragment
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : BaseFragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            val spannableString = SpannableString(getString(R.string.txt_register))
            val linkStartIndex = spannableString.indexOf("Kaydol")

            spannableString.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.light_green)),
                linkStartIndex,
                spannableString.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            tvRegister.text = spannableString
            tvRegister.setOnClickListener {
                if (registerLayout.visibility == View.GONE) {
                    registerLayout.visibility = View.VISIBLE
                } else {
                    registerLayout.visibility = View.GONE
                }
            }

            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                if (registerLayout.visibility == View.VISIBLE) {
                    registerLayout.visibility = View.GONE
                }
            }

            btnRegister.setOnClickListener {
                val email = edtRgtMail.text.toString().trim()
                val password = edtRgtPassword.text.toString().trim()
                val passwordRepeat = edtRgtPasswordRepeat.text.toString().trim()

                when {
                    email.isEmpty() -> {
                        Toast.makeText(
                            requireActivity(),
                            getText(R.string.txt_enter_email),
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }

                    password.isEmpty() -> {
                        Toast.makeText(
                            requireActivity(),
                            getText(R.string.txt_enter_password),
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }

                    password.length < 6 -> {
                        Toast.makeText(
                            requireActivity(),
                            getText(R.string.txt_enter_proper_password),
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }

                    passwordRepeat.isEmpty() -> {
                        Toast.makeText(
                            requireActivity(),
                            getText(R.string.txt_repeat_password),
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }

                    password != passwordRepeat -> {
                        Toast.makeText(
                            requireActivity(),
                            getText(R.string.txt_enter_same_password),
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }
                }

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                requireActivity(),
                                getText(R.string.rgt_successful),
                                Toast.LENGTH_SHORT
                            ).show()
                            registerLayout.visibility = View.GONE
                        } else {
                            val errorMessage =
                                task.exception?.message ?: getText(R.string.rgt_failed)
                            Toast.makeText(requireActivity(), errorMessage, Toast.LENGTH_LONG)
                                .show()
                        }
                    }
            }

            btnLogin.setOnClickListener {
                val email = edtMail.text.toString()
                val password = edtPassword.text.toString()

                if (email.isNotEmpty() && password.isNotEmpty()) {
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(requireActivity()) { task ->
                            if (task.isSuccessful) {
                                findNavController().navigate(R.id.actionLoginFragmentToHomeFragment)
                            } else {
                                Toast.makeText(
                                    requireActivity(),
                                    getText(R.string.login_failed),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}