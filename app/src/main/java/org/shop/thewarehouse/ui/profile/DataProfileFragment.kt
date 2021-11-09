package org.shop.thewarehouse.ui.profile

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.navOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import io.conekta.conektasdk.Card
import io.conekta.conektasdk.Conekta
import io.conekta.conektasdk.Token
import org.json.JSONObject
import org.shop.thewarehouse.R
import org.shop.thewarehouse.databinding.FragmentDataProfileBinding
import shortbread.Shortcut
import javax.crypto.Cipher.PUBLIC_KEY


class DataProfileFragment: Fragment() {

    private var _binding: FragmentDataProfileBinding? = null
    private val binding get() = _binding!!
    private val PUBLIC_KEY = "key_eYvWV7gSDkNYXsmr"
    private val API_VERSION = "0.3.0"

    private var hasValidCardData: Boolean? = false
    private var cardName: String? = null
    private var cardNumber: String? = null
    private var cardCvc: String? = null
    private var cardMonth: String? = null
    private var cardYear: String? = null
    private var tokenIdTag: String? = null
    private var errorTag: String? = null
    private var uuidDeviceTag: String? = null

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDataProfileBinding.inflate(layoutInflater, container, false)

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
        _binding!!.paymentButton.setOnClickListener {onPressTokenizeButton()}


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
                binding.editTextDirection.setText(tasks.get("direccion").toString())
            }
        }
    }
    private fun onPressTokenizeButton() {

        enableProgressBar(true)
        if (hasInternetConnection()) {
            Conekta.setPublicKey(PUBLIC_KEY)
            Conekta.setApiVersion(API_VERSION)
            Conekta.collectDevice(activity)
            getCardData()
            if (hasValidCardData!!) {
                val card = Card(cardName, cardNumber, cardCvc, cardMonth, cardYear)
                val token = Token(activity)

                //Listen when token is returned
                token.onCreateTokenListener { data -> showTokenResult(data) }

                //Request for create token
                token.create(card)
            } else {
                Toast.makeText(
                    activity,
                    resources.getString(R.string.cardDataIncomplete),
                    Toast.LENGTH_LONG
                ).show()
                //enableInputs(true)
                enableProgressBar(false)
            }
        } else {
            Toast.makeText(
                activity,
                resources.getString(R.string.needInternetConnection),
                Toast.LENGTH_LONG
            ).show()
            //binding.outputView.text = resources.getString(R.string.needInternetConnection)
            //enableInputs(true)
            enableProgressBar(false)
        }
    }
    private fun showTokenResult(data: JSONObject) {
        try {
            Log.e("TAG", "showTokenResult: $data")
            val tokenId: String = if (data.has("id")) {
                data.getString("id")
            } else {
                data.getString("message")
            }
            val tokenMessage = "$tokenIdTag $tokenId"
           // binding.outputView.text = tokenMessage
            Toast.makeText(activity,"$tokenIdTag, $tokenId",Toast.LENGTH_SHORT).show()
        } catch (error: Exception) {
            val errorMessage = "$errorTag $error"
            //binding.outputView.text = errorMessage
        }
        //enableInputs(true)
        enableProgressBar(false)

        val uuidMessage: String = uuidDeviceTag + " " + Conekta.deviceFingerPrint(activity)
        //binding.uuidDevice.text = uuidMessage
    }
    private fun hasInternetConnection(): Boolean {
        val cm = requireActivity().getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }
    private fun getCardData() {
        hasValidCardData = true
        cardName = binding.nameText.text.toString()
        cardNumber = binding.numberText.text.toString()
        cardCvc = binding.cvcText.text.toString()
        cardMonth = binding.monthText.text.toString()
        cardYear = binding.yearText.text.toString()
        if (cardName!!.isEmpty() || cardNumber!!.isEmpty() || cardCvc!!.isEmpty()
            || cardMonth!!.isEmpty() || cardYear!!.isEmpty()
        ) {
            hasValidCardData = false
        }
    }
    private fun enableProgressBar(show: Boolean) {
        //binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.shadowView.visibility = if (show) View.VISIBLE else View.GONE
    }
}