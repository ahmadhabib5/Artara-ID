package com.example.artara_id.feature.about.support

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.artara_id.R
import com.example.artara_id.adapter.SupportAdapter
import com.example.artara_id.databinding.FragmentSupportBinding
import com.example.artara_id.feature.about.AboutViewModel

class SupportFragment : Fragment() {

    private var _binding : FragmentSupportBinding? = null
    private val binding get() = _binding!!
    private lateinit var aboutViewModel: AboutViewModel
    private lateinit var supportAdapter: SupportAdapter
    private var loading: AlertDialog? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSupportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loading = showDialogLoading()

        supportAdapter = SupportAdapter(binding.root.context)

        binding.apply {
            rvCakeSupport.setHasFixedSize(true)
            rvCakeSupport.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            rvCakeSupport.adapter = supportAdapter
        }
        aboutViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[AboutViewModel::class.java]
        val onProcess = object : AboutViewModel.OnProses {
            override fun onProses(boolean: Boolean) {
                onLoading(boolean)
            }
        }

        val onError = object: AboutViewModel.OnError {
            override fun onError(message: String) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                onLoading(false)
            }

        }

        aboutViewModel.setSupportCake(onProcess, onError)
        aboutViewModel.getSupportCake().observe(viewLifecycleOwner, {
            if (it != null){
                supportAdapter.setList(it)
                binding.include.root.visibility = View.GONE
            }
        })
    }

    private fun onLoading(boolean: Boolean){
        if (boolean) {
            loading?.show()
        } else {
            loading?.dismiss()
        }
    }
    private fun showDialogLoading(): AlertDialog {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.item_layout, null, false)
        return AlertDialog.Builder(requireContext())
            .setView(view)
            .setCancelable(false)
            .create()
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}