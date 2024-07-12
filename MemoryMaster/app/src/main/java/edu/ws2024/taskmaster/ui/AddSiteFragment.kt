package edu.ws2024.taskmaster.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.lifecycle.lifecycleScope
import edu.ws2024.taskmaster.R
import edu.ws2024.taskmaster.databinding.FragmentAddSiteBinding
import edu.ws2024.taskmaster.homeReplaceFragment
import edu.ws2024.taskmaster.model.Site
import edu.ws2024.taskmaster.model.gson
import edu.ws2024.taskmaster.model.originSiteData
import edu.ws2024.taskmaster.database.siteFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AddSiteFragment : Fragment() {
    private lateinit var b: FragmentAddSiteBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        b = FragmentAddSiteBinding.inflate(inflater)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        b.apply {
            backIcon.setOnClickListener {
                homeReplaceFragment(this@AddSiteFragment, SiteFragment())
            }
            addButton.setOnClickListener {
                lifecycleScope.launch {
                    checkData()
                    homeReplaceFragment(this@AddSiteFragment, SiteFragment())
                }
            }
        }
    }
    private suspend fun checkData(){
        b.apply {
            val siteName = siteEdit.text.toString()
            val type = view?.findViewById<RadioButton>(typeRgp.checkedRadioButtonId)?.text.toString()
            println(type)
            val description = descEdit.text.toString()
            val url = urlEdit.text.toString()
            when{
                siteName.isEmpty() -> siteBox.error = getString(R.string.box_empty_error)
                url.isEmpty() -> urlBox.error = getString(R.string.box_empty_error)
                else -> addData(Site(siteName,type,description,url))
            }
        }
    }
    private suspend fun addData(data: Site){
        originSiteData.add(data)
        withContext(Dispatchers.Default){
            siteFile.outputStream().use {
                it.write(gson.toJson(originSiteData).toByteArray())
                it.flush()
            }
        }
    }


}