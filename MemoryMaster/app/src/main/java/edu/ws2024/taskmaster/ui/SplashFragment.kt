package edu.ws2024.taskmaster.ui

import android.animation.Animator
import android.animation.ValueAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import edu.ws2024.taskmaster.R
import edu.ws2024.taskmaster.databinding.FragmentSplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SplashFragment : Fragment() {
    private lateinit var b: FragmentSplashBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        b = FragmentSplashBinding.inflate(inflater)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        b.apply {
            circlCard.doOnLayout {
                lifecycleScope.launch {
                    inflateAnimation()
                    delay(1500)
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, LoginFragment())
                        .commit()
                }
            }
        }
    }

    private fun inflateAnimation() {
        b.apply {
            val initSize = circlCard.width
            ValueAnimator.ofInt(initSize, initSize * 15).apply {
                addUpdateListener {
                    val params = circlCard.layoutParams
                    params.width = it.animatedValue as Int
                    params.height = it.animatedValue as Int
                    circlCard.layoutParams = params
                }
                duration = 500
                addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {

                    }

                    override fun onAnimationEnd(animation: Animator) {
                        titleFrame.isVisible = true
                    }

                    override fun onAnimationCancel(animation: Animator) {
                    }

                    override fun onAnimationRepeat(animation: Animator) {
                    }

                })
            }.start()
        }
    }

}