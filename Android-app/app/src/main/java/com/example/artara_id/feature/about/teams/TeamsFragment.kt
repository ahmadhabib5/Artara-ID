package com.example.artara_id.feature.about.teams

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
import com.example.artara_id.adapter.AboutAdapter
import com.example.artara_id.databinding.FragmentTeamsBinding
import com.example.artara_id.feature.about.AboutViewModel


class TeamsFragment : Fragment() {

    private var _binding: FragmentTeamsBinding? = null
    private val binding get() = _binding!!
    private lateinit var aboutViewModel: AboutViewModel
    private lateinit var aboutAdapter: AboutAdapter
    private var loading: AlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTeamsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loading = showDialogLoading()

        aboutAdapter = AboutAdapter(binding.root.context)

        binding.apply {
            rvOwner.setHasFixedSize(true)
            rvOwner.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            rvOwner.adapter = aboutAdapter
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
        aboutViewModel.setMember(
            onProcess = onProcess,
            onError = onError
        )

        aboutViewModel.getMember().observe(viewLifecycleOwner, {
            if (it != null){
                aboutAdapter.setList(it)
                binding.include.root.visibility = View.INVISIBLE
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