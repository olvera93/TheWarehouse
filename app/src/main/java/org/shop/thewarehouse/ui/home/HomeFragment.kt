package org.shop.thewarehouse.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment

import com.example.beducompras.ui.home.ProductAdapterListener
import org.shop.thewarehouse.data.model.Product
import org.shop.thewarehouse.databinding.FragmentHomeBinding
import org.shop.thewarehouse.ui.ShoppingApplication



class HomeFragment : Fragment(), ProductAdapterListener {


    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    //private var products: List<Product>? = null

    private lateinit var productAdapter: ProductAdapter
    private val application by lazy { activity?.applicationContext as ShoppingApplication }
    private val homeViewModel: HomeViewModel by lazy { HomeViewModel(application.productRepository) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        binding.lifecycleOwner = this

        setupRecyclerView()

        homeViewModel.let{
            it.products.observe(viewLifecycleOwner) { productList ->
                Log.d("JEJEJO",productList.toString())
                productAdapter.submitList(productList)
            }

            it.getProducts()
        }


        return binding.root
    }



    private fun setupRecyclerView(){
        productAdapter = ProductAdapter()
        binding.productList.apply{
            adapter = productAdapter

            binding.textHeader.visibility = View.VISIBLE
            binding.productList.visibility = View.VISIBLE
        }
    }

    override fun onProductClicked(view: View, product: Product) {
        //
    }
}