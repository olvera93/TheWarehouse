package org.shop.thewarehouse.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import org.shop.thewarehouse.R

import org.shop.thewarehouse.data.model.Product
import org.shop.thewarehouse.databinding.FragmentHomeBinding
import org.shop.thewarehouse.ui.ShoppingApplication


class HomeFragment : Fragment(), ProductAdapterListener {


    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore

    private lateinit var productAdapter: ProductAdapter
    private val application by lazy { activity?.applicationContext as ShoppingApplication }
    private val homeViewModel: HomeViewModel by lazy { HomeViewModel(application.productRepository) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        binding.lifecycleOwner = this

        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val isLogin=sharedPref.getString("Email","1")

        setupRecyclerView()

        homeViewModel.let{
            it.products.observe(viewLifecycleOwner) { productList ->
                Log.d("JEJEJO",productList.toString())
                productAdapter.submitList(productList)
            }

            it.getProducts()
        }

        if(isLogin=="1") {
            val email=requireActivity().intent.getStringExtra("email")
            if(email!=null) {
                setText(email)
                with(sharedPref.edit()) {
                    putString("Email",email)
                    apply()
                }
            }
        }
        else {
            setText(isLogin)
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

    @SuppressLint("SetTextI18n")
    private fun setText(email: String?) {
        db = FirebaseFirestore.getInstance()
        if (email != null) {
            db.collection("users").document(email).get()
                .addOnSuccessListener { tasks ->
                    tasks.get("email").toString()
                    binding.name.text =
                        "¿${getString(R.string.zip_information)} ${tasks.get("estado").toString()} ${tasks.get("codigoPostal").toString()}?"

                }
        }

    }

    override fun onProductClicked(view: View, product: Product) {

    }
}