package org.shop.thewarehouse.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.google.firebase.firestore.FirebaseFirestore
import org.shop.thewarehouse.R
import androidx.navigation.NavController
import androidx.navigation.Navigation
import es.dmoral.toasty.Toasty

import org.shop.thewarehouse.data.model.Product
import org.shop.thewarehouse.data.repository.ProductRepository
import org.shop.thewarehouse.databinding.FragmentHomeBinding

import org.shop.thewarehouse.ui.ShoppingApplication
import org.shop.thewarehouse.view.NavigationActivity

class HomeFragment : Fragment(), ProductAdapter.ShopInterface {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private lateinit var productAdapter: ProductAdapter
    private lateinit var navController : NavController
    private val application by lazy { requireActivity().applicationContext as ShoppingApplication }
    val repository : ProductRepository by lazy { application.productRepository }

    lateinit var homeViewModel : HomeViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val isLogin=sharedPref.getString("Email","1")
        setupRecyclerView()
        val HVMFactory = HomeViewModelFactory(repository)
        homeViewModel = ViewModelProvider(requireActivity(),HVMFactory)[HomeViewModel::class.java]
        homeViewModel.let{
            it.products.observe(viewLifecycleOwner) { productList ->
                Log.d("Productos",productList.toString())
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


    override fun addItem(product: Product) {
        val isAdded:Boolean = homeViewModel.addItemToCart(product)
    }

    override fun onItemClick(product: Product) {
        homeViewModel.setProduct(product)
        navController.navigate(R.id.action_navigation_home_to_productDetail22)
    }

}