package edu.ws2024.taskmaster.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.load
import edu.ws2024.taskmaster.R
import edu.ws2024.taskmaster.databinding.FragmentAddPictureBinding
import edu.ws2024.taskmaster.homeReplaceFragment
import edu.ws2024.taskmaster.model.PicData
import edu.ws2024.taskmaster.model.gson
import edu.ws2024.taskmaster.model.pictureData
import edu.ws2024.taskmaster.database.photoFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID


class AddPictureFragment : Fragment() {
    private lateinit var b: FragmentAddPictureBinding
    private var imageUri: Uri? = null
    private val newPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { bool ->
            if (bool) {
                newImagePick()
            }
        }
    private val oldImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
            if (res.resultCode == Activity.RESULT_OK) {
                res?.let {
                    imageUri = it.data?.data
                    b.image.load(imageUri)
                }
            }
        }
    private val imageLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { res ->

            res?.let {
                imageUri = res
                b.image.load(imageUri)

            }
        }
    private val oldPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { bool ->
            if (bool) {
                oldImagePick()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        b = FragmentAddPictureBinding.inflate(inflater)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        b.apply {
            backIcon.setOnClickListener {
                homeReplaceFragment(this@AddPictureFragment, PictureFragment())
            }
            selectPictureButton.setOnClickListener {
                if (Build.VERSION.SDK_INT >= 33) {
                    newPermissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                } else {
                    oldPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
            addButton.setOnClickListener {
                lifecycleScope.launch {
                    saveFile()
                }
            }
        }
    }

    private fun newImagePick() {
        imageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun oldImagePick() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        oldImageLauncher.launch(intent)
    }

    private suspend fun saveFile() {
        b.apply {
            val imageName = pictureNameEdit.text.toString()
            if (imageName.isNotEmpty()) {
                val filePath = createImageFile()
                val fileData = PicData(imageName, filePath)
                withContext(Dispatchers.IO) {
                    pictureData.add(fileData)
                    photoFile.outputStream().use {
                        it.write(gson.toJson(pictureData).toByteArray())
                        it.flush()
                    }
                }
                withContext(Dispatchers.Main) {
                    homeReplaceFragment(this@AddPictureFragment, PictureFragment())
                }
            } else pictureNameBox.error = getString(R.string.box_empty_error)
        }
    }

    private suspend fun createImageFile(): String {
        return withContext(Dispatchers.IO) {
            val fileNameUUid = UUID.randomUUID().toString()
            val file = File(requireContext().filesDir, fileNameUUid)
            val input = requireContext().contentResolver.openInputStream(imageUri!!)
            val bitmap = BitmapFactory.decodeStream(input)
            file.outputStream().use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
            return@withContext file.absolutePath
        }
    }
}