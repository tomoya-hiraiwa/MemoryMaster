package edu.ws2024.taskmaster.viewmodel

import android.database.sqlite.SQLiteOpenHelper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.ws2024.taskmaster.database.AccountHelper
import edu.ws2024.taskmaster.model.Account
import kotlinx.coroutines.launch

class AccountViewModel(): ViewModel() {
  fun insert(db: AccountHelper,data: Account){
      viewModelScope.launch {
          db.insert(data)
      }
  }
    fun getAccount(db: AccountHelper,data: Account, callBack: (Account) -> Unit){
        viewModelScope.launch {
            val account = db.getAccount(data)
            callBack(account)
        }
    }
}

