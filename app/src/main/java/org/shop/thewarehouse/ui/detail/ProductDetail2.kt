package org.shop.thewarehouse.ui.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import org.shop.thewarehouse.databinding.FragmentProductDetail2Binding
import org.shop.thewarehouse.ui.ShoppingApplication
import org.shop.thewarehouse.ui.home.HomeFragment
import org.shop.thewarehouse.ui.home.HomeViewModel
import org.shop.thewarehouse.ui.home.homeVMHomeFragment


class ProductDetail2 : Fragment() {
    lateinit var binding : FragmentProductDetail2Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProductDetail2Binding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.homeViewModel = homeVMHomeFragment
    }
    
}