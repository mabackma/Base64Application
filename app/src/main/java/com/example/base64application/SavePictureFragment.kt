package com.example.base64application

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.base64application.databinding.FragmentSavePictureBinding
import com.example.base64application.datatypes.MyPicture
import com.google.gson.GsonBuilder
import java.io.UnsupportedEncodingException
import android.Manifest
import android.util.Base64
import com.example.base64application.BuildConfig
import java.io.ByteArrayOutputStream
import java.io.InputStream


class SavePictureFragment : Fragment() {
    companion object {
        private const val REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 2
    }

    private var _binding: FragmentSavePictureBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // Kuvan sijainti
    private lateinit var selectedImageUri: Uri

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSavePictureBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val imageView = binding.imageView

        // Valitaan kuva kännykän galleriasta.
        imageView.setOnClickListener {
            selectPictureFromGallery()
        }

        binding.buttonSendPicture.setOnClickListener {
            val title = binding.editTextPictureTitle.text.toString()
            val description = binding.editTextPictureDescription.text.toString()
            var value = ""
            if (::selectedImageUri.isInitialized) {
                value = convertImageToBase64(selectedImageUri)
            }

            Log.d("BASE64", value)

            if(title.isBlank() || description.isBlank() || value.isBlank()) {
                binding.requirements.text = "Please fill all fields!"
            }
            else {
                // Lähetetään uusi kuva rajapintaan.
                sendDataToDirectus(title, description, value)

                // Näytetään ponnahdusviesti.
                Toast.makeText(context, "Picture saved!", Toast.LENGTH_LONG).show()

                // Siirrytään PictureList fragmenttiin
                val action =
                    SavePictureFragmentDirections.actionSavePictureFragmentToPictureListFragment()
                this.findNavController().navigate(action)
            }
        }

        return root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun sendDataToDirectus(title: String, description: String, value: String) {
        val queue = Volley.newRequestQueue(requireContext())
        val url = "http://192.168.1.100:8055/items/mypicture?access_token=xc93fqjp4VLP8OlLaColJQK9BdZJZVZY"
        //val url = "http://10.0.2.2:8055/items/mypicture?access_token=xc93fqjp4VLP8OlLaColJQK9BdZJZVZY" //+ BuildConfig.STATIC_TOKEN
        val gson = GsonBuilder().create()

        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                Log.d("RESPONSE", "got response from POST")
            },
            { Log.d("DIRECTUS", "Error getting POST response") })
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

            @Throws(AuthFailureError::class)
            override fun getBody(): ByteArray {
                var body = ByteArray(0)
                try{
                    val newPicture = MyPicture()
                    newPicture.title = title
                    newPicture.description = description
                    newPicture.value = value

                    val newData = gson.toJson(newPicture)
                    Log.d("DATA", newData)
                    body = newData.toByteArray(Charsets.UTF_8)
                } catch (e: UnsupportedEncodingException) {
                }
                return body
            }
        }

        queue.add(stringRequest)
    }

    // Valitaan kuva galleriasta.
    private fun selectPictureFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, 1)
    }

    // Laitetaan valitun kuvan sijainti selectedImageUri muuttujaan ja näytetään kuva imageViewssa.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = data?.data
            if (imageUri != null) {
                selectedImageUri = imageUri // Assign the selected image URI to the variable
                binding.imageView.setImageURI(imageUri)
            }
        }
    }

    // Muuttaa kuvan Base64 muotoon.
    private fun convertImageToBase64(imageUri: Uri): String {
        val inputStream: InputStream? = context?.contentResolver?.openInputStream(imageUri)
        val bytes = inputStream?.readBytes()
        inputStream?.close()

        if (bytes != null) {
            val outputStream = ByteArrayOutputStream()
            outputStream.write(bytes)
            val imageBytes = outputStream.toByteArray()
            outputStream.close()

            return Base64.encodeToString(imageBytes, Base64.DEFAULT)
        }

        return ""
    }
}