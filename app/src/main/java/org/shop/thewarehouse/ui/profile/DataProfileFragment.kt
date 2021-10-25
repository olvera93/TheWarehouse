package org.shop.thewarehouse.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.navOptions
import com.google.firebase.firestore.FirebaseFirestore
import org.shop.thewarehouse.R
import org.shop.thewarehouse.databinding.FragmentDataProfileBinding



class DataProfileFragment: Fragment() {

    private var _binding: FragmentDataProfileBinding? = null
    private val binding get() = _binding!!


    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDataProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val isLogin=sharedPref.getString("Email","1")

        val navigate = navOptions {
            anim {
                enter = R.anim.slide_in_right
                exit = R.anim.slide_out_left
                popEnter = R.anim.slide_in_left
                popExit = R.anim.slide_out_right
            }
        }

        if(isLogin=="1") {

            var email=requireActivity().intent.getStringExtra("email")
            if(email!=null) {
                dataProfile(email)
                with(sharedPref.edit()) {
                    putString("Email",email)
                    apply()
                }
            }
        }
        else {
            dataProfile(isLogin)
        }

        return root
    }

    private fun dataProfile(email: String?) {
        db= FirebaseFirestore.getInstance()
        if (email != null) {
            db.collection("users").document(email).get().addOnSuccessListener { tasks ->
                binding.editTextUser.setText(tasks.get("usuario").toString())
                binding.editTextEmail.setText(tasks.get("email").toString())
                binding.editTextName.setText(tasks.get("nombre").toString())
                binding.editTextLastName.setText(tasks.get("apellido").toString())
            }
        }
    }
}