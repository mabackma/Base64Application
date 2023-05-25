package com.example.base64application

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.base64application.databinding.FragmentShowPictureBinding


class ShowPictureFragment : Fragment() {
    private var _binding: FragmentShowPictureBinding? = null

    val args: ShowPictureFragmentArgs by navArgs()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShowPictureBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textViewTitle: TextView = binding.currentTitle
        textViewTitle.text = args.title
        val textViewDescription: TextView = binding.currentDescription
        textViewDescription.text = args.description

        // Näytetään kuva.
        displayImageFromBase64(args.value)

        // Nappi jolla joka vie takaisin kuvalistaan.
        binding.back.setOnClickListener {
            val action = ShowPictureFragmentDirections.actionShowPictureFragmentToPictureListFragment()
            this.findNavController().navigate(action)
        }

        // Nappi jolla voidaan poistaa kuva
        binding.delete.setOnClickListener {
            deleteFromDirectus(args.id)

            Toast.makeText(context, "Deleted picture from ${args.title}", Toast.LENGTH_LONG).show()

            val action = ShowPictureFragmentDirections.actionShowPictureFragmentToPictureListFragment()
            this.findNavController().navigate(action)
        }

        return root
    }

    private fun deleteFromDirectus(id: Int) {
        val url = "http://10.0.2.2:8055/items/mypicture/$id?access_token=xc93fqjp4VLP8OlLaColJQK9BdZJZVZY" //+ BuildConfig.STATIC_TOKEN

        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.DELETE, url,
            Response.Listener { response ->
                Log.d("RESPONSE", "Got response from DELETE " + response.toString())
            },
            Response.ErrorListener {
                // typically this is a connection error
                Log.d("ADVTECH", it.toString())
            })
        {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                // we have to specify a proper header, otherwise Apigility will block our queries!
                // define we are after JSON data!
                val headers = HashMap<String, String>()
                headers["Accept"] = "application/json"
                headers["Content-Type"] = "application/json; charset=utf-8"
                return headers
            }
        }

        // Add the request to the RequestQueue. This has to be done in both getting and sending new data.
        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(stringRequest)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Muuttaa base64 merkkijonon kuvaksi
    private fun displayImageFromBase64(base64String: String) {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        val bitmap: Bitmap? = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

        if (bitmap != null) {
            binding.currentImage.setImageBitmap(bitmap)
        }
    }
}