package org.shop.thewarehouse.ui.cart

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import org.intellij.lang.annotations.JdkConstants
import org.shop.thewarehouse.R
import org.shop.thewarehouse.data.model.CartItem
import org.shop.thewarehouse.data.repository.ProductRepository
import org.shop.thewarehouse.databinding.CartRowBinding
import org.shop.thewarehouse.databinding.FragmentCartBinding

import org.shop.thewarehouse.ui.ShoppingApplication
import org.shop.thewarehouse.ui.home.HomeViewModel
import org.shop.thewarehouse.ui.home.HomeViewModelFactory
import kotlin.math.round


class CartFragment : Fragment(),CartListAdapter.CartInterface{
    private val TAG = "CartFragment"
    private val application by lazy { requireActivity().applicationContext as ShoppingApplication }
    val repository : ProductRepository by lazy { application.productRepository }
    lateinit var binding : FragmentCartBinding
    lateinit var homeViewModel: HomeViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val HVMFactory = HomeViewModelFactory(repository)
        homeViewModel = ViewModelProvider(requireActivity(),HVMFactory)[HomeViewModel::class.java]
        binding = FragmentCartBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val cartListAdapter : CartListAdapter = CartListAdapter(this)
        var emptyCartComponent = binding.emptyCartView
        var paymentCart = binding.paymentCart
        var itemsInCart = binding.itemsCart
        binding.cartRecyclerView.adapter = cartListAdapter
        binding.cartRecyclerView.addItemDecoration(DividerItemDecoration(context,DividerItemDecoration.VERTICAL))
        homeViewModel.let{
            it.getCart().observe(viewLifecycleOwner) { cartItems ->
                Log.d("cartitems: ",cartItems.size.toString())
                if(cartItems.size==0){
                    emptyCartComponent.visibility= View.VISIBLE
                    paymentCart.visibility = View.GONE
                    itemsInCart.visibility = View.GONE
                }else{
                    emptyCartComponent.visibility = View.GONE
                    paymentCart.visibility = View.VISIBLE
                    itemsInCart.visibility = View.VISIBLE
                }
                cartListAdapter.submitList(cartItems)
            }
        }
        homeViewModel.let{
            it.getTotalPrice().observe(viewLifecycleOwner){ total->
                binding.orderTotalTextView.text = "Total: $"+round(total * 100) / 100
            }
        }
    }

    override fun deleteItem(cartItem: CartItem) {
        homeViewModel.removeItemFromCart(cartItem)
    }

    override fun changeQuantity(cartItem: CartItem, quantity: Int) {
        homeViewModel.changeQuantity(cartItem,quantity)
    }

}