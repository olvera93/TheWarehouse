package org.shop.thewarehouse.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import org.shop.thewarehouse.databinding.FragmentProfileBinding
import org.shop.thewarehouse.view.MainActivity
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import java.io.File


class ProfileFragment: Fragment() {

    private var _binding: FragmentProfileBinding? = null
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



        _binding = FragmentProfileBinding.inflate(inflater, container, false)
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
                val intent = Intent(requireActivity(),MainActivity::class.java)
                startActivity(intent)

            }
        }
        else {
            setText(isLogin)
        }


        binding.apply {
            logoutButton.setOnClickListener {
                sharedPref.edit().clear().commit()
                val intent = Intent(context, MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()

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
                    binding.textUserName.text = tasks.get("usuario").toString()
                   binding.textEmail.text = tasks.get("email").toString()
                }
        }

    }



}