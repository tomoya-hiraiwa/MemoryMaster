package edu.ws2024.taskmaster.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.transition.MaterialSharedAxis
import edu.ws2024.taskmaster.R
import edu.ws2024.taskmaster.database.AccountHelper
import edu.ws2024.taskmaster.databinding.FragmentLoginBinding
import edu.ws2024.taskmaster.model.Account
import edu.ws2024.taskmaster.model.accountName
import edu.ws2024.taskmaster.replaceFragment
import edu.ws2024.taskmaster.viewmodel.AccountViewModel

class LoginFragment : Fragment() {
    private lateinit var db: AccountHelper
    private lateinit var b: FragmentLoginBinding
    private lateinit var v: AccountViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X,true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X,false)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        b = FragmentLoginBinding.inflate(inflater)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //init db and viewModel
        db = AccountHelper((requireContext()))
        v = ViewModelProvider(requireActivity())[AccountViewModel::class.java]
        b.apply {
            setInputBoxListener()
            //tap create account button
            createAccountButton.setOnClickListener {
                replaceFragment(this@LoginFragment, CreateAccountFragment())
            }
            //tap login button
            loginButton.setOnClickListener {
                login()
            }
        }
    }

    //login function
    private fun login(){
        b.apply {
            val name = nameEdit.text.toString()
            val password = passEdit.text.toString()
            when{
                name.isEmpty() -> nameBox.error = getString(R.string.box_empty_error)
                password.isEmpty() -> passBox.error = getString(R.string.box_empty_error)
                else ->{
                    v.getAccount(db, Account(name,password)){
                        if (it.name.isEmpty()){
                            nameBox.error = getString(R.string.login_error)
                            passBox.error = getString(R.string.login_error)
                        }
                        else{
                            accountName = it.name
                            replaceFragment(this@LoginFragment, HomeFragment())
                        }
                    }
                }
            }
        }
    }

    private fun setInputBoxListener(){
        b.apply {
            nameEdit.doAfterTextChanged {
                nameBox.error = null
            }
            passEdit.doAfterTextChanged {
                passBox.error = null
            }
        }
    }
}