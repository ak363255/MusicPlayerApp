package com.example.musicplayerapp.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.musicplayerapp.R
import com.example.musicplayerapp.databinding.BaseFragmentLayoutBinding

abstract class BaseFragment:Fragment() {

    var binding:ViewBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,provideLayoutResourceId(),container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }


    abstract fun initialize()
    abstract fun provideLayoutResourceId():Int


}