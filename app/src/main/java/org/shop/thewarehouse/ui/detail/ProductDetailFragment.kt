package org.shop.thewarehouse.ui.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import org.shop.thewarehouse.data.repository.ProductRepository
import org.shop.thewarehouse.databinding.FragmentProductDetailBinding
import org.shop.thewarehouse.ui.ShoppingApplication
import org.shop.thewarehouse.ui.home.HomeViewModel
import org.shop.thewarehouse.ui.home.HomeViewModelFactory


class ProductDetailFragment : Fragment() {
    lateinit var binding : FragmentProductDetailBinding
    private val application by lazy { requireActivity().applicationContext as ShoppingApplication }
    val repository : ProductRepository by lazy { application.productRepository }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProductDetailBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var HVMFactory = HomeViewModelFactory(repository)
        var homeViewModel = ViewModelProvider(requireActivity(),HVMFactory)[HomeViewModel::class.java]
        binding.homeViewModel = homeViewModel
    }
    
}