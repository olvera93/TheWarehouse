package org.shop.thewarehouse.ui.order

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.google.firebase.firestore.FirebaseFirestore
import io.conekta.conektasdk.Card
import io.conekta.conektasdk.Conekta
import io.conekta.conektasdk.Token
import org.json.JSONObject
import org.shop.thewarehouse.R
import org.shop.thewarehouse.databinding.FragmentOrderBinding


class OrderFragment : Fragment() {
    private val PUBLIC_KEY = "key_eYvWV7gSDkNYXsmr"
    private val API_VERSION = "0.3.0"
    private lateinit var db: FirebaseFirestore

    private var hasValidCardData: Boolean? = false
    private var cardName: String? = null
    private var cardNumber: String? = null
    private var cardCvc: String? = null
    private var cardMonth: String? = null
    private var cardYear: String? = null
    private var tokenIdTag: String? = null
    private var errorTag: String? = null
    private var uuidDeviceTag: String? = null

    private var _binding: FragmentOrderBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOrderBinding.inflate(inflater, container, false)
        _binding!!.paymentButton.setOnClickListener { onPressTokenizeButton() }
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val isLogin = sharedPref.getString("Email", "1")

        if (isLogin == "1") {
            val email = requireActivity().intent.getStringExtra("email")
            if (email != null) {
                setText(email)
                with(sharedPref.edit()) {
                    putString("Email", email)
                    apply()
                }
            }
        } else {
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

        binding.paymentButton.setOnClickListener {
            findNavController().navigate(R.id.fragment_payment, null, navigate)

        }


        val root: View = binding.root
        // Inflate the layout for this fragment
        return root
    }

    private fun onPressTokenizeButton() {

        //enableProgressBar(true)
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
                //enableProgressBar(false)
            }
        } else {
            Toast.makeText(
                activity,
                resources.getString(R.string.needInternetConnection),
                Toast.LENGTH_LONG
            ).show()
            //binding.outputView.text = resources.getString(R.string.needInternetConnection)
            //enableInputs(true)
            //enableProgressBar(false)
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
            Toast.makeText(activity, "$tokenIdTag, $tokenId", Toast.LENGTH_SHORT).show()
        } catch (error: Exception) {
            val errorMessage = "$errorTag $error"
            //binding.outputView.text = errorMessage
        }
        //enableInputs(true)
        //enableProgressBar(false)

        val uuidMessage: String = uuidDeviceTag + " " + Conekta.deviceFingerPrint(activity)
        //binding.uuidDevice.text = uuidMessage
    }

    private fun hasInternetConnection(): Boolean {
        val cm =
            requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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

    @SuppressLint("SetTextI18n")
    private fun setText(email: String?) {
        db = FirebaseFirestore.getInstance()
        if (email != null) {
            db.collection("users").document(email).get()
                .addOnSuccessListener { tasks ->
                    tasks.get("email").toString()
                    binding.txtAddress.text =
                        "${getString(R.string.txt_address)} ${
                            tasks.get("estado").toString()
                        } ${tasks.get("codigoPostal").toString()}"

                }
        }

    }
}