package edu.ws2024.taskmaster.ui

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.gson.reflect.TypeToken
import edu.ws2024.taskmaster.databinding.FragmentSiteBinding
import edu.ws2024.taskmaster.databinding.SiteItemBinding
import edu.ws2024.taskmaster.homeAddFragment
import edu.ws2024.taskmaster.homeReplaceFragment
import edu.ws2024.taskmaster.model.Site
import edu.ws2024.taskmaster.model.accountName
import edu.ws2024.taskmaster.model.gson
import edu.ws2024.taskmaster.model.originSiteData
import edu.ws2024.taskmaster.model.siteDetailData
import edu.ws2024.taskmaster.database.photoFile
import edu.ws2024.taskmaster.database.siteFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.use
import java.io.File


class SiteFragment : Fragment() {
    private lateinit var b: FragmentSiteBinding
    private var showSiteData = mutableListOf<Site>()
    private lateinit var adapter: SiteListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        b = FragmentSiteBinding.inflate(inflater)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        siteFile = File(requireContext().filesDir, "${accountName}_site.json")
        photoFile = File(requireContext().filesDir, "${accountName}_photo.json")
        if (!siteFile.exists()) {
            siteFile.createNewFile()
        }
        if (!photoFile.exists()) {
            photoFile.createNewFile()
        }
        b.apply {
            list.layoutManager = LinearLayoutManager(requireContext())
            adapter =
                SiteListAdapter(showSiteData, { favChanger(it, true) }, { favChanger(it, false) }, {
                    deleteDialog(it)
                }) {
                    siteDetailData = it
                    homeAddFragment(this@SiteFragment, SiteDetailFragment())
                }
            list.adapter = adapter
            addFab.setOnClickListener {
                homeReplaceFragment(this@SiteFragment, AddSiteFragment())
            }
            catChipGroup.setOnCheckedChangeListener { group, checkedId ->
                if (checkedId != -1) {
                    changeCategory(view.findViewById<Chip>(checkedId).text.toString())
                } else {
                    changeCategory("")
                }
            }

        }
        lifecycleScope.launch {
            getSiteData()
        }
    }

    private suspend fun getSiteData() {
        showSiteData.clear()
        withContext(Dispatchers.Default) {
            val inputData = siteFile.inputStream().bufferedReader().use { it.readText() }
            if (inputData.isNotEmpty()) {
                originSiteData =
                    gson.fromJson(
                        siteFile.inputStream().bufferedReader().use { it.readText() },
                        object : TypeToken<MutableList<Site>>() {}.type
                    )
            }
        }
        withContext(Dispatchers.Main) {
            var addData = mutableListOf<Site>()
            if (b.catChipGroup.checkedChipId != -1) {
                val selectType =
                    view?.findViewById<Chip>(b.catChipGroup.checkedChipId)?.text.toString()
                addData = originSiteData.filter { it.type == selectType }.toMutableList()
            } else addData = originSiteData
            showSiteData.addAll(addData)
            adapter.notifyDataSetChanged()
        }
    }


    private fun deleteDialog(data: Site) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Notice")
            setMessage("Delete Site in this app")
            setNegativeButton("Cancel") { _, _ -> }
            setPositiveButton("OK") { _, _ ->
                lifecycleScope.launch {
                    deleteSiteData(data)
                }
            }
        }.show()
    }

    private suspend fun deleteSiteData(data: Site) {
        withContext(Dispatchers.IO) {
            originSiteData.remove(data)
            siteFile.outputStream().use {
                it.write(gson.toJson(originSiteData).toByteArray())
                it.flush()
            }
            getSiteData()
        }
    }

    private fun changeCategory(category: String) {
        println(category)
        showSiteData.clear()
        if (category.isNotEmpty()) {
            showSiteData.addAll(originSiteData.filter { it.type == category })
        } else showSiteData.addAll(originSiteData)
        adapter.notifyDataSetChanged()
    }

    private fun favChanger(data: Site, isFav: Boolean) {
        val originIndex = originSiteData.indexOf(data)
        val showIndex = showSiteData.indexOf(data)
        originSiteData[originIndex].isFav = isFav
        showSiteData[showIndex].isFav = isFav
        adapter.notifyItemChanged(showIndex)
    }

    private suspend fun saveData() {
        withContext(Dispatchers.Default) {
            siteFile.outputStream().use {
                it.write(gson.toJson(originSiteData).toByteArray())
                it.flush()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        lifecycleScope.launch {
            saveData()
        }
    }

}

class SiteListAdapter(
    private val dataList: MutableList<Site>,
    val isFavClick: (Site) -> Unit,
    val isNotFavClick: (Site) -> Unit,
    val onDelete: (Site) -> Unit,
    val onClick: (Site) -> Unit
) : RecyclerView.Adapter<SiteListAdapter.SiteListViewHolder>() {
    inner class SiteListViewHolder(private val b: SiteItemBinding) :
        RecyclerView.ViewHolder(b.root) {
        fun bindData(data: Site) {
            b.apply {
                yesFav.isVisible = data.isFav
                title.text = data.name
                url.text = data.url
                noFav.setOnClickListener {
                    isFavClick(data)
                }
                yesFav.setOnClickListener {
                    isNotFavClick(data)
                }
                root.setOnClickListener {
                    onClick(data)
                }
                root.setOnLongClickListener {
                    onDelete(data)
                    true
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SiteListViewHolder {
        return SiteListViewHolder(
            SiteItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: SiteListViewHolder, position: Int) {
        holder.bindData(data = dataList[position])
    }
}