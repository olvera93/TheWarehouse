package org.shop.thewarehouse.ui.order

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import io.conekta.conektasdk.Card
import io.conekta.conektasdk.Conekta
import io.conekta.conektasdk.Token
import kotlinx.android.synthetic.main.activity_main_navigation.*
import org.json.JSONObject
import org.shop.thewarehouse.R
import org.shop.thewarehouse.ui.animation.PaymentFragment
import android.text.TextWatcher
import androidx.lifecycle.ViewModelProvider
import org.shop.thewarehouse.data.repository.ProductRepository
import org.shop.thewarehouse.databinding.FragmentOrderBinding
import org.shop.thewarehouse.ui.ShoppingApplication
import org.shop.thewarehouse.ui.home.HomeViewModel
import org.shop.thewarehouse.ui.home.HomeViewModelFactory
import kotlin.math.round

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
    private val application by lazy { requireActivity().applicationContext as ShoppingApplication }
    val repository : ProductRepository by lazy { application.productRepository }
    lateinit var homeViewModel: HomeViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOrderBinding.inflate(inflater, container, false)
        _binding!!.paymentButton.setOnClickListener { onPressTokenizeButton() }
        val HVMFactory = HomeViewModelFactory(repository)
        homeViewModel = ViewModelProvider(requireActivity(),HVMFactory)[HomeViewModel::class.java]
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
        binding.numberText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence, i: Int, i1: Int, i2: Int) {
                checkRequiredFields()
            }
            override fun afterTextChanged(p0: Editable?) {}
        })
        binding.nameText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence, i: Int, i1: Int, i2: Int) {
                checkRequiredFields()
            }
            override fun afterTextChanged(p0: Editable?) {}
        })
        binding.monthText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence, i: Int, i1: Int, i2: Int) {
                checkRequiredFields()
            }
            override fun afterTextChanged(p0: Editable?) {}
        })
        homeViewModel.let{
            it.getTotalPrice().observe(viewLifecycleOwner){ total->
                binding.paymentQuantity.text = "${getString(R.string.paymentQuantity)} "+round(total * 100) / 100
            }
        }
        binding.yearText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence, i: Int, i1: Int, i2: Int) {
                checkRequiredFields()
            }
            override fun afterTextChanged(p0: Editable?) {}
        })
        binding.paymentButton.setOnClickListener {
            var dialog = PaymentFragment()
            dialog.show(requireFragmentManager(),"dialog")
        }


        val root: View = binding.root
        // Inflate the layout for this fragment
        return root
    }
    private fun checkRequiredFields() {
        binding.paymentButton.isEnabled = binding.numberText.text.toString().isNotEmpty() &&
                binding.nameText.text.toString().isNotEmpty() &&
                binding.monthText.text.toString().isNotEmpty() &&
                binding.yearText.text.toString().isNotEmpty() &&
                binding.cvcText.text.toString().isNotEmpty()
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