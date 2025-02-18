package cat.copernic.pokemap.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.pokemap.data.DTO.Contact
import cat.copernic.pokemap.data.Repository.ContactRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ContactViewModal : ViewModel() {

    private val repository = ContactRepository()

    // Track success state
    private val _isSuccess = MutableStateFlow(false)
    val isSuccess = _isSuccess.asStateFlow()

    fun addContact(contact: Contact) {
        viewModelScope.launch {
            try {
                val documentId = repository.addContact(contact)
                if (documentId != null) {
                    _isSuccess.value = true // Update success state
                } else {
                    _isSuccess.value = false // Handle failure
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _isSuccess.value = false
            }
        }
    }

    // Reset success state after handling it
    fun resetSuccessState() {
        _isSuccess.value = false
    }
}
