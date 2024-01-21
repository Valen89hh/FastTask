package com.valendev.fasttask.ui.welcome.firstscreen

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.valendev.fasttask.R
import com.valendev.fasttask.context.PreferenceManager
import com.valendev.fasttask.databinding.FragmentFirstBinding
import com.valendev.fasttask.ui.hometask.HomeActivity


class FirstFragment : Fragment() {

    private lateinit var binding: FragmentFirstBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFirstBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivStart.setOnClickListener {
            findNavController().navigate(R.id.action_firstFragment_to_loginFragment)
        }


    }

}