package io.github.vvb2060.keyattestation.home

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import io.github.vvb2060.keyattestation.app.AppFragment
import io.github.vvb2060.keyattestation.databinding.HomeBinding
import io.github.vvb2060.keyattestation.util.Status
import rikka.material.widget.BorderView.OnBorderVisibilityChangedListener

class HomeFragment : AppFragment() {

    private var _binding: HomeBinding? = null

    private val binding: HomeBinding get() = _binding!!

    private val viewModel by viewModels<HomeViewModel>({ requireActivity() })

    private val adapter by lazy {
        HomeAdapter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = HomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = view.context

        binding.root.borderVisibilityChangedListener = OnBorderVisibilityChangedListener { top: Boolean, _: Boolean, _: Boolean, _: Boolean -> appActivity?.appBar?.setRaised(!top) }
        binding.root.adapter = adapter
        binding.root.addItemDecoration(HomeItemDecoration(context))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val context = requireContext()

        viewModel.attestationResult.observe(viewLifecycleOwner) {
            when (it?.status) {
                Status.SUCCESS -> {
                    adapter.updateData(it.data!!)
                }
                Status.ERROR -> {
                }
                Status.LOADING -> {
                }
            }
        }
        val useStrongBox = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P &&
                context.packageManager.hasSystemFeature(PackageManager.FEATURE_STRONGBOX_KEYSTORE)
        if (savedInstanceState == null) {
            viewModel.invalidateAttestation(context, useStrongBox)
        }
    }
}