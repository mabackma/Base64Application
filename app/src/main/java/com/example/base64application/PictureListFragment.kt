package com.example.base64application

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.base64application.databinding.FragmentPictureListBinding
import com.example.base64application.datatypes.MyPicture
import com.google.gson.GsonBuilder
import org.json.JSONObject


class PictureListFragment : Fragment() {
    private var _binding: FragmentPictureListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MyPictureAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPictureListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.scrollToPosition(0)
        binding.recyclerView.layoutManager = linearLayoutManager

        adapter = MyPictureAdapter(emptyList())
        binding.recyclerView.adapter = adapter

        binding.buttonRecyclerView.setOnClickListener {
            val textViewComment: TextView = binding.comment
            textViewComment.text = "Press to view picture:"
            textViewComment.gravity = Gravity.CENTER

            getPicturesForRecycleView()
        }

        return root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun getPicturesForRecycleView() {
        val queue = Volley.newRequestQueue(requireContext())
        val url = "http://10.0.2.2:8055/items/mypicture?access_token=xc93fqjp4VLP8OlLaColJQK9BdZJZVZY" //+ BuildConfig.STATIC_TOKEN
        val gson = GsonBuilder().setPrettyPrinting().create()

        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.GET, url,
            Response.Listener { response ->
                val jObject = JSONObject(response)
                val jArray = jObject.getJSONArray("data")
                val rows : List<MyPicture> = gson.fromJson(jArray.toString(), Array<MyPicture>::class.java).toList()

                adapter = MyPictureAdapter(rows)
                binding.recyclerView.adapter = adapter
            },
            { Log.d("DIRECTUS", "Error getting GET response") })
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

        queue.add(stringRequest)
    }
}