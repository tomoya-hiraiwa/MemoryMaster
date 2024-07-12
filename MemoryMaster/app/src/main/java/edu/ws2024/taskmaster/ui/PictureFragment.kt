package edu.ws2024.taskmaster.ui

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.gson.reflect.TypeToken
import edu.ws2024.taskmaster.databinding.FragmentPictureBinding
import edu.ws2024.taskmaster.databinding.PictureItemBinding
import edu.ws2024.taskmaster.homeAddFragment
import edu.ws2024.taskmaster.homeReplaceFragment
import edu.ws2024.taskmaster.model.PicData
import edu.ws2024.taskmaster.model.gson
import edu.ws2024.taskmaster.model.pictureData
import edu.ws2024.taskmaster.model.pictureDetailData
import edu.ws2024.taskmaster.database.photoFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class PictureFragment : Fragment() {
 private lateinit var b: FragmentPictureBinding
    private lateinit var adapter: PictureListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        b = FragmentPictureBinding.inflate(inflater)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        b.apply {
            addFab.setOnClickListener {
                homeReplaceFragment(this@PictureFragment, AddPictureFragment())
            }
            list.layoutManager = GridLayoutManager(requireContext(),2)
            adapter = PictureListAdapter(pictureData,requireContext(),{deleteDialog(it)}){
                pictureDetailData = it
                homeAddFragment(this@PictureFragment, PictureDetailFragment())
            }
            list.adapter = adapter
            lifecycleScope.launch {
                getPictureData()
            }
        }
    }
    private suspend fun getPictureData(){
        withContext(Dispatchers.Default) {
            pictureData.clear()
            val inputData = photoFile.inputStream().bufferedReader().use { it.readText() }
            if (inputData.isNotEmpty()) {
                pictureData.addAll(
                    gson.fromJson(
                        photoFile.inputStream().bufferedReader().use { it.readText() },
                        object : TypeToken<MutableList<PicData>>() {}.type
                    )
                )
            }
        }
        withContext(Dispatchers.Main) {
            b.noPictureText.isVisible = pictureData.isEmpty()
            adapter.notifyDataSetChanged()
        }
    }

    private fun deleteDialog(data: PicData){
        AlertDialog.Builder(requireContext()).apply{
            setTitle("Notice")
            setMessage("Delete picture in this app")
            setNegativeButton("Cancel"){_,_->}
            setPositiveButton("OK"){_,_->
                lifecycleScope.launch {
                    deleteData(data)
                }
            }
        }.show()
    }
    private suspend fun deleteData(data: PicData){
        withContext(Dispatchers.IO){
            val file = File(data.filePath)
            file.delete()
            pictureData.remove(data)
            photoFile.outputStream().use {
                it.write(gson.toJson(pictureData).toByteArray())
                it.flush()
            }
        }
        getPictureData()
    }
}

class PictureListAdapter(private val dataList: MutableList<PicData>,private val context: Context,val onDelete:(PicData)-> Unit, val onClick:(PicData)-> Unit): RecyclerView.Adapter<PictureListAdapter.PictureListViewHolder>(){
    inner class PictureListViewHolder(private val b: PictureItemBinding): RecyclerView.ViewHolder(b.root){
        fun bindData(data: PicData){
            b.apply {
                image.load(data.filePath)
                root.setOnLongClickListener {
                    deleteButton.isVisible = !deleteButton.isVisible
                    true
                }
                deleteButton.setOnClickListener {
                    onDelete(data)
                }
                root.setOnClickListener {
                    onClick(data)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictureListViewHolder {
        return PictureListViewHolder(PictureItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: PictureListViewHolder, position: Int) {
        holder.bindData(data =  dataList[position])
    }
}