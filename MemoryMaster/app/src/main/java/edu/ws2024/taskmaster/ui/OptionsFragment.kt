package edu.ws2024.taskmaster.ui

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import edu.ws2024.taskmaster.R
import edu.ws2024.taskmaster.database.photoFile
import edu.ws2024.taskmaster.database.siteFile
import edu.ws2024.taskmaster.databinding.DeleteDialogBinding
import edu.ws2024.taskmaster.databinding.FragmentOptionsBinding
import edu.ws2024.taskmaster.model.DeleteType
import edu.ws2024.taskmaster.model.gson
import edu.ws2024.taskmaster.model.originSiteData
import edu.ws2024.taskmaster.model.pictureData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class OptionsFragment : Fragment() {
private lateinit var b: FragmentOptionsBinding

private val saveFileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){res ->
    if (res.resultCode == Activity.RESULT_OK){
        res.data?.data?.let {
            requireContext().contentResolver.openOutputStream(it).use {
                it?.write(gson.toJson(originSiteData).toByteArray())
                it?.flush()
            }
        }
        Toast.makeText(requireContext(), "Export success", Toast.LENGTH_SHORT).show()
    }
}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        b = FragmentOptionsBinding.inflate(inflater)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        b.apply {
            exportButton.setOnClickListener {
                exportFile()
            }
            deleteSiteButton.setOnClickListener {
                deleteDialog(DeleteType.Site)
            }
            deletePictureButton.setOnClickListener {
                deleteDialog(DeleteType.Picture)
            }

        }
    }
    private fun exportFile(){
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/plain"
            putExtra(Intent.EXTRA_TITLE,"site_json.txt")
        }
        saveFileLauncher.launch(intent)
    }
    private fun deleteDialog(type: DeleteType){
        Dialog(requireContext()).apply {
            val db = DeleteDialogBinding.inflate(layoutInflater)
            setContentView(db.root)
            db.apply {
                checkBox.setOnCheckedChangeListener { compoundButton, b ->
                    deleteButton.isEnabled = b
                }
                deleteButton.setOnClickListener {
                    lifecycleScope.launch {
                        if (type == DeleteType.Site) {
                            deleteSiteData()
                        }
                        else {
                            deletePhotoData()
                        }
                        cancel()
                    }
                }
            }
        }.show()
    }
    private suspend fun deleteSiteData(){
        withContext(Dispatchers.IO){
            originSiteData.clear()
            siteFile.outputStream().use {
                it.write("".toByteArray())
                it.flush()
            }
        }
    }
    private suspend fun deletePhotoData(){
        withContext(Dispatchers.IO){
            pictureData.forEach{item ->
                val file = File(item.filePath)
                file.delete()
            }
            pictureData.clear()
            photoFile.outputStream().use {
                it.write("".toByteArray())
                it.flush()
            }
        }
    }


}