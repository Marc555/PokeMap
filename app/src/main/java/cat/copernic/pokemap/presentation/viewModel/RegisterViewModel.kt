import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import cat.copernic.pokemap.data.DTO.Rol


class RegisterViewModel : ViewModel() {
    var orden by mutableStateOf(1)
    var email by mutableStateOf("")
    var name by mutableStateOf("")
    var surname by mutableStateOf("")
    var usurname by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var messageError by mutableStateOf("")
    val rol: Rol = Rol.USER
}