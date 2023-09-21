package com.example.gallery.presentation

import android.app.Application
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor() : ViewModel() {

    private val _allImagesFromGallery: MutableStateFlow<List<Uri>> = MutableStateFlow(listOf())
    val allImagesFromGallery: StateFlow<List<Uri>> = _allImagesFromGallery

    private val _allVideosFromGallery: MutableStateFlow<List<Uri>> = MutableStateFlow(listOf())
    val allVideosFromGallery: StateFlow<List<Uri>> = _allVideosFromGallery

    fun getAllImages(application: Application) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val images = mutableListOf<Uri>()
                val cursor = application.contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    arrayOf(MediaStore.Images.Media._ID),
                    null,
                    null,
                    "${MediaStore.Images.Media.DATE_ADDED} DESC"
                )

                cursor?.use {
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                    while (cursor.moveToNext()) {
                        images.add(
                            ContentUris.withAppendedId(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                cursor.getLong(idColumn)
                            )
                        )
                    }
                    _allImagesFromGallery.value = images
                }
            }
        }
    }

    fun getAllVideos(application: Application) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val videos = mutableListOf<Uri>()
                val cursor = application.contentResolver.query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    arrayOf(MediaStore.Video.Media._ID),
                    null,
                    null,
                    "${MediaStore.Video.Media.DATE_ADDED} DESC"
                )

                cursor?.use {
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                    while (cursor.moveToNext()) {
                        videos.add(
                            ContentUris.withAppendedId(
                                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                cursor.getLong(idColumn)
                            )
                        )
                    }
                    _allVideosFromGallery.value = videos
                }
            }
        }
    }

}