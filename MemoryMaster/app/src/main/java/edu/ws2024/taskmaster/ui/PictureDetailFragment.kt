package edu.ws2024.taskmaster.ui

import android.content.ContentValues
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import coil.load
import com.google.android.material.transition.platform.MaterialSharedAxis
import edu.ws2024.taskmaster.databinding.FragmentPictureDetailBinding
import edu.ws2024.taskmaster.model.pictureDetailData
import edu.ws2024.taskmaster.removeFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class PictureDetailFragment : Fragment() {
    private lateinit var b: FragmentPictureDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        b = FragmentPictureDetailBinding.inflate(inflater)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        b.apply {
            root.setOnClickListener{}
            backIcon.setOnClickListener {
                removeFragment(this@PictureDetailFragment)
            }
            title.text = pictureDetailData.name

            image.load(pictureDetailData.filePath)

            dwButton.setOnClickListener {
                lifecycleScope.launch {
                    savePhotoFolder()
                }
            }
        }
    }

    private suspend fun savePhotoFolder() {
        val file = File(pictureDetailData.filePath)
        val input = file.inputStream()
        withContext(Dispatchers.IO) {
            val con = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, pictureDetailData.name)
                put(MediaStore.Images.Media.MIME_TYPE,"image/png")
            }
            val uri = requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,con)
            requireContext().contentResolver.openOutputStream(uri!!).use {
                input.copyTo(it!!)
            }
        }
        withContext(Dispatchers.Main){
            Toast.makeText(requireContext(), "Save in Photo Folder", Toast.LENGTH_SHORT).show()
        }
    }


}