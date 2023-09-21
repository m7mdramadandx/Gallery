package com.example.gallery.presentation.videos

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.gallery.databinding.FragmentGalleryBinding
import com.example.gallery.presentation.GalleryAdapter
import com.example.gallery.presentation.GalleryViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class VideosFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private var viewModel: GalleryViewModel? = null
    private var galleryAdapter: GalleryAdapter? = null

    private val binding get() = _binding!!
    private val permission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) android.Manifest.permission.READ_MEDIA_VIDEO
        else android.Manifest.permission.READ_EXTERNAL_STORAGE

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) fetchVideosFromGallery()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[GalleryViewModel::class.java]
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        requestPermissionLauncher.launch(permission)

        when {
            ContextCompat.checkSelfPermission(requireContext(), permission)
                    == PackageManager.PERMISSION_GRANTED -> {
                fetchVideosFromGallery()
            }

            shouldShowRequestPermissionRationale(permission) -> {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Permission")
                    .setMessage("Videos permission is needed")
                    .setPositiveButton("Ok") { _, i ->
                        showApplicationDetailsSettings()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }

            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }

        galleryAdapter = GalleryAdapter()
        binding.rcGallery.adapter = galleryAdapter

        lifecycleScope.launchWhenStarted {
            viewModel?.allVideosFromGallery?.collectLatest {
                if (it.isNotEmpty()) {
                    galleryAdapter?.updateMediaFiles(it)
                }else {
                    Toast.makeText(requireContext(), "Sorry, you don't have videos.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return binding.root
    }

    private fun fetchVideosFromGallery() {
        viewModel?.getAllVideos(requireActivity().application)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showApplicationDetailsSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.data = Uri.fromParts("package", requireActivity().packageName, null)
        requireActivity().startActivity(intent)
    }

}