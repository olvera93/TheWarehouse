package org.shop.thewarehouse.ui.animation

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import kotlinx.android.synthetic.main.fragment_payment.*
import org.shop.thewarehouse.R
import org.shop.thewarehouse.data.repository.ProductRepository
import org.shop.thewarehouse.databinding.FragmentCartBinding
import org.shop.thewarehouse.databinding.FragmentPaymentBinding
import org.shop.thewarehouse.ui.ShoppingApplication
import org.shop.thewarehouse.ui.home.HomeViewModel
import org.shop.thewarehouse.ui.home.HomeViewModelFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class PaymentFragment : DialogFragment() {
    lateinit var homeViewModel: HomeViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val application by lazy { requireActivity().applicationContext as ShoppingApplication }
        val repository : ProductRepository by lazy { application.productRepository }
        var rootView: View = inflater.inflate(R.layout.fragment_payment,container,false)
        val HVMFactory = HomeViewModelFactory(repository)
        homeViewModel = ViewModelProvider(requireActivity(),HVMFactory)[HomeViewModel::class.java]
        return rootView
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val navigate = navOptions {
            anim {
                enter = R.anim.slide_in_right
                exit = R.anim.slide_out_left
                popEnter = R.anim.slide_in_left
                popExit = R.anim.slide_out_right
            }
        }
        homeViewModel.resetCart()
        findNavController().navigate(R.id.navigation_home, null, navigate)
    }
}