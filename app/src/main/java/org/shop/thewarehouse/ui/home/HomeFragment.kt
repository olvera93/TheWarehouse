package org.shop.thewarehouse.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast


import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation


import com.example.beducompras.ui.home.ProductAdapterListener
import org.shop.thewarehouse.R
import org.shop.thewarehouse.data.model.Product
import org.shop.thewarehouse.databinding.FragmentHomeBinding

import org.shop.thewarehouse.ui.ShoppingApplication



lateinit var homeVMHomeFragment : HomeViewModel
class HomeFragment : Fragment(), ProductAdapterListener,ProductAdapter.ShopInterface {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    //private var products: List<Product>? = null

    private lateinit var productAdapter: ProductAdapter
    private lateinit var navController : NavController
    private val application by lazy { activity?.applicationContext as ShoppingApplication }
    val homeViewModel : HomeViewModel by lazy { HomeViewModel(application.productRepository) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        //binding.lifecycleOwner = this
        setupRecyclerView()

        homeViewModel.let{
            it.products.observe(viewLifecycleOwner) { productList ->
                Log.d("Productos",productList.toString())
                productAdapter.submitList(productList)
            }

            it.getProducts()
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }



    private fun setupRecyclerView(){
        productAdapter = ProductAdapter(this)
        binding.productList.apply{
            adapter = productAdapter

            binding.productList.visibility = View.VISIBLE
        }
    }

    override fun onProductClicked(view: View, product: Product) {

    }

    override fun addItem(product: Product) {
        TODO("Not yet implemented")
    }

    override fun onItemClick(product: Product) {
        homeViewModel.setProduct(product)
        homeVMHomeFragment = homeViewModel
        navController.navigate(R.id.action_navigation_home_to_productDetail22)
    }

}