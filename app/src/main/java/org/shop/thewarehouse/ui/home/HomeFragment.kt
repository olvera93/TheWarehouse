package org.shop.thewarehouse.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.adapters.ListenerUtil.getListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.beducompras.ui.home.ProductAdapterListener
import org.shop.thewarehouse.data.model.Product
import org.shop.thewarehouse.data.room.ProductDB
import org.shop.thewarehouse.databinding.FragmentHomeBinding
import org.shop.thewarehouse.ui.ShoppingApplication
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class HomeFragment : Fragment(), ProductAdapterListener {
    private lateinit var productAdapter: ProductAdapter
    private var _binding: FragmentHomeBinding? = null
    private val application by lazy { activity?.applicationContext as ShoppingApplication }
    private val homeViewModel: HomeViewModel by lazy { HomeViewModel(application.productRepository) }
    private lateinit var adapter: ProductAdapter
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View{
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        Executors
            .newSingleThreadExecutor()
            .execute(Runnable {
                ProductDB
                    .getDatabase(context = requireContext())
                    ?.productDao()
                    ?.insertProduct(Product())

        setupRecyclerView()
        val executor: ExecutorService = Executors.newSingleThreadExecutor()
        homeViewModel.let{
            it.products.observe(viewLifecycleOwner) { productList ->

                productAdapter.submitList(productList)
            }

            it.getProducts()
        }
        /*
        executor.execute(Runnable {
            val productsArray = ProductDB
                .getDatabase(context = requireContext())
                ?.productDao()
                ?.getProducts() as MutableList<Product>
        })
         */

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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