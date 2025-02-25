package cat.copernic.pokemap.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import cat.copernic.pokemap.data.DTO.UserFromGoogle

class SharedUserViewModel : ViewModel() {
    var userFromGoogle: UserFromGoogle? by mutableStateOf(null)
}
