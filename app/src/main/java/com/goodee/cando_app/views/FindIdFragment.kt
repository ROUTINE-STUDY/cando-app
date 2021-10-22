package com.goodee.cando_app.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.goodee.cando_app.R
import com.goodee.cando_app.databinding.FragmentFindIdBinding
import com.goodee.cando_app.viewmodel.UserViewModel

class FindIdFragment : Fragment() {
    private lateinit var binding: FragmentFindIdBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentFindIdBinding>(inflater, R.layout.fragment_find_id, container, false)
        setEvent()

        return binding.root
    }

    private fun setEvent() {
        binding.buttonFindidSubmit.setOnClickListener {
            val email = binding.edittextFindidEmailinput.text.toString()
            val name = binding.edittextFindidNameinput.text.toString()

            if (email.isNullOrEmpty() || email.isBlank()) {
                Toast.makeText(requireActivity(), "이메일을 입력해주세요", Toast.LENGTH_SHORT).show()
            } else if (name.isNullOrEmpty() || name.isBlank()) {
                Toast.makeText(requireActivity(), "이름을 입력해주세요", Toast.LENGTH_SHORT).show()
            } else {
                val userViewModel = UserViewModel(requireActivity().application)
                userViewModel.findUserId(email = email, name = name)
            }
        }
    }
}