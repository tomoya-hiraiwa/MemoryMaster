package edu.ws2024.taskmaster.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.transition.MaterialSharedAxis
import edu.ws2024.taskmaster.R
import edu.ws2024.taskmaster.database.AccountHelper
import edu.ws2024.taskmaster.databinding.FragmentCreateAccountBinding
import edu.ws2024.taskmaster.model.Account
import edu.ws2024.taskmaster.replaceFragment
import edu.ws2024.taskmaster.viewmodel.AccountViewModel


class CreateAccountFragment : Fragment() {
    private lateinit var v: AccountViewModel
    private lateinit var db: AccountHelper
    private lateinit var b: FragmentCreateAccountBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        b = FragmentCreateAccountBinding.inflate(inflater)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = AccountHelper(requireContext())
        v = ViewModelProvider(requireActivity())[AccountViewModel::class.java]
        b.apply {
            setInputBoxListener()
            //tap back icon
            backIcon.setOnClickListener {
                replaceFragment(this@CreateAccountFragment, LoginFragment())
            }
            //tap sign up button
            signUpButton.setOnClickListener {
                checkFormat()
            }
        }
    }

    //check input format
    private fun checkFormat() {
        b.apply {
            val name = nameEdit.text.toString().replace(Regex("""\s"""), "")
            val password = passEdit.text.toString().replace(Regex("""\s"""), "")
            val rePassword = passAgainEdit.text.toString().replace(Regex("""\s"""), "")
            val passWordRegex = Regex("""^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)[A-Za-z\d]{8,16}${'$'}""")
            val nameRegex = Regex("""[A-Za-z\d]{5,12}""")
            when {
                name.isEmpty() -> nameBox.error = getString(R.string.box_empty_error)
                !nameRegex.matches(name) -> nameBox.error = getString(R.string.format_error)
                password.isEmpty() -> passBox.error = getString(R.string.box_empty_error)
                !passWordRegex.matches(password) -> passBox.error = getString(R.string.format_error)
                rePassword.isEmpty() -> passAgainBox.error = getString(R.string.box_empty_error)
                rePassword != password -> passAgainBox.error = "Passwords do not match each input box."
                else -> createAccount(name,password)
            }
        }
    }
    //create account and back to login fragment
    private fun createAccount(name: String,pass: String){
        v.insert(db, Account(name,pass))
        Toast.makeText(requireContext(), "Account create successfully.", Toast.LENGTH_SHORT).show()
        replaceFragment(this, LoginFragment())
    }
    //set Input box listener
    private fun setInputBoxListener(){
        b.apply {
            nameEdit.doAfterTextChanged {
                nameBox.error = null
            }
            passEdit.doAfterTextChanged {
                passBox.error = null
            }
            passAgainEdit.doAfterTextChanged {
                passAgainBox.error = null
            }
        }
    }


}