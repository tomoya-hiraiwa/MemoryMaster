package edu.ws2024.taskmaster.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayout
import edu.ws2024.taskmaster.childReplaceFragment
import edu.ws2024.taskmaster.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
private lateinit var b: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        b = FragmentHomeBinding.inflate(inflater)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        b.apply {
            homeTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when(tab?.position){
                        0 -> childReplaceFragment(this@HomeFragment, SiteFragment())
                        1 -> childReplaceFragment(this@HomeFragment, PictureFragment())
                        2 -> childReplaceFragment(this@HomeFragment, OptionsFragment())
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {

                }
            })
        }
    }


}