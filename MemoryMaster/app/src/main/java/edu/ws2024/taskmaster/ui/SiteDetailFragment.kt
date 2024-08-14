package edu.ws2024.taskmaster.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.transition.platform.MaterialSharedAxis
import edu.ws2024.taskmaster.databinding.FragmentSiteDetailBinding
import edu.ws2024.taskmaster.model.siteDetailData
import edu.ws2024.taskmaster.removeFragment

class SiteDetailFragment : Fragment() {
   private lateinit var b: FragmentSiteDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y,true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Y,false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        b = FragmentSiteDetailBinding.inflate(inflater)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
        b.apply {
            b.root.setOnClickListener{}
            backIcon.setOnClickListener {
                removeFragment(this@SiteDetailFragment)
            }
            copyButton.setOnClickListener {
                val clip = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE)as ClipboardManager
                clip.setPrimaryClip(ClipData.newPlainText("url", siteDetailData.url))
            }
        }
    }
    private fun setData(){
        b.apply {
            siteName.text = siteDetailData.name
            siteType.text = siteDetailData.type
            siteDescription.text = siteDetailData.description
            siteUrl.text = siteDetailData.url
        }
    }


}