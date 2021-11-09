package org.shop.thewarehouse.ui.order

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.shop.thewarehouse.R



/**
 * A simple [Fragment] subclass.
 * Use the [orderFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class orderFragment : Fragment() {




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order, container, false)
    }


}