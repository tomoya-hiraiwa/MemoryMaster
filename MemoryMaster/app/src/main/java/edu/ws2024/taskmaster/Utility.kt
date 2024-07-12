package edu.ws2024.taskmaster

import androidx.fragment.app.Fragment

fun replaceFragment(from: Fragment,to: Fragment){
    from.parentFragmentManager.beginTransaction()
        .replace(R.id.fragmentContainerView,to)
        .commit()
}

fun addFragment(from: Fragment,to: Fragment){
    from.parentFragmentManager.beginTransaction()
        .add(R.id.fragmentContainerView,to)
        .commit()
}

fun removeFragment(target: Fragment){
    target.parentFragmentManager.beginTransaction()
        .remove(target)
        .commit()
}


fun childReplaceFragment(from: Fragment,to: Fragment){
    from.childFragmentManager.beginTransaction()
        .replace(R.id.homeContainer,to)
        .commit()
}

fun homeReplaceFragment(from: Fragment,to: Fragment){
    from.parentFragmentManager.beginTransaction()
        .replace(R.id.homeContainer,to)
        .commit()
}

fun homeAddFragment(from: Fragment,to: Fragment){
    from.parentFragmentManager.beginTransaction()
        .add(R.id.homeContainer,to)
        .commit()
}