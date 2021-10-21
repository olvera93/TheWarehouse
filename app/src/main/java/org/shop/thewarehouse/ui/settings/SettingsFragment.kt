package org.shop.thewarehouse.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.google.firebase.firestore.FirebaseFirestore
import org.shop.thewarehouse.R
import org.shop.thewarehouse.databinding.FragmentSettingsBinding
import org.shop.thewarehouse.view.MainActivity

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
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

        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val isLogin=sharedPref.getString("Email","1")

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        if(isLogin=="1") {

            var email=requireActivity().intent.getStringExtra("email")
            if(email!=null) {
                setText(email)
                with(sharedPref.edit()) {
                    putString("Email",email)
                    apply()
                }
            } else{
                val intent = Intent(requireActivity(), MainActivity::class.java)
                startActivity(intent)

            }
        }
        else {
            setText(isLogin)
        }

        val navigate = navOptions {
            anim {
                enter = R.anim.slide_in_right
                exit = R.anim.slide_out_left
                popEnter = R.anim.slide_in_left
                popExit = R.anim.slide_out_right
            }
        }

        binding.apply {

            cardViewHome.setOnClickListener {
                findNavController().navigate(R.id.navigation_home, null, navigate)
            }

            cardViewProfile.setOnClickListener {
                findNavController().navigate(R.id.fragment_profile, null, navigate)
            }
        }




        return root
    }

    private fun setText(email:String?) {
        db= FirebaseFirestore.getInstance()
        if (email != null) {
            db.collection("users").document(email).get()
                .addOnSuccessListener {
                        tasks->
                    binding.textUserName.text = "${getString(R.string.txt_hello)} ${tasks.get("usuario").toString()}"
                    //binding.textEmail.text = tasks.get("email").toString()
                }
        }
    }
}