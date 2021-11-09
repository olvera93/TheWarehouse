package org.shop.thewarehouse.ui.animation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import kotlinx.android.synthetic.main.fragment_payment.*
import org.shop.thewarehouse.R
import org.shop.thewarehouse.databinding.FragmentPaymentBinding
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class PaymentFragment : Fragment() {

    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPaymentBinding.inflate(layoutInflater, container, false)


        val navigate = navOptions {
            anim {
                enter = R.anim.slide_in_right
                exit = R.anim.slide_out_left
                popEnter = R.anim.slide_in_left
                popExit = R.anim.slide_out_right
            }
        }

        binding.btnReturn.setOnClickListener {
            findNavController().navigate(R.id.navigation_home, null, navigate)
        }


        // Inflate the layout for this fragment
        return binding.root
    }

}