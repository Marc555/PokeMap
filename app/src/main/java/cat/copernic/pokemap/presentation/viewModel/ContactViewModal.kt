package cat.copernic.pokemap.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.pokemap.data.DTO.Category
import cat.copernic.pokemap.data.DTO.Contact
import cat.copernic.pokemap.data.Repository.ContactRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ContactViewModal : ViewModel() {

    private val repository = ContactRepository()

    // Track success state
    private val _isSuccess = MutableStateFlow(false)
    val isSuccess = _isSuccess.asStateFlow()

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts = _contacts.asStateFlow()

    private val _contact = MutableStateFlow<Contact?>(null)
    val contact: StateFlow<Contact?> = _contact

    private val _contactsWithIds = MutableStateFlow<List<Pair<String,Contact>>>(emptyList())
    val contactsWithIds: StateFlow<List<Pair<String,Contact>>> = _contactsWithIds

    fun fetchContactsById() {
        viewModelScope.launch {
            _contactsWithIds.value = repository.getContactsWithId().map { (id, contact) ->
                id to contact.copy(timestamp = contact.timestamp ?: System.currentTimeMillis())
            }
        }
    }

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

    fun updateReadState(uid: String, contact: Contact) {
        viewModelScope.launch {
            try {
                val result = repository.updateReadState(uid)
                if (result) {
                    _contactsWithIds.value = repository.getContactsWithId()
                } else {
                    ///
                }
            } catch (e: Exception) {
            }
        }
    }

    // Reset success state after handling it
    fun resetSuccessState() {
        _isSuccess.value = false
    }
}
