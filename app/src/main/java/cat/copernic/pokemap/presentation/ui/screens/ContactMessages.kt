package cat.copernic.pokemap.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Drafts
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cat.copernic.pokemap.data.DTO.Contact
import cat.copernic.pokemap.presentation.viewModel.ContactViewModal
import cat.copernic.pokemap.utils.LanguageManager
import cat.copernic.pokemap.data.DTO.FilterType
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ContactMessages(navController: NavController) {
    val contactViewModel: ContactViewModal = viewModel()

    LaunchedEffect(Unit) {
        contactViewModel.fetchContactsById()
    }

    val contacts by contactViewModel.contactsWithIds.collectAsState()
    var selectedContact by remember { mutableStateOf<Contact?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var filter by remember { mutableStateOf(FilterType.UNREAD) } // 🔥 Track selected filter

    Column {
        FilterDropdown(filter) { selectedFilter ->
            filter = selectedFilter // Update filter when user selects a new option
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            items(
                contacts
                    .filter { (_, contact) ->
                        when (filter) {
                            FilterType.ALL -> true
                            FilterType.READ -> contact.read
                            FilterType.UNREAD -> !contact.read
                            else -> true
                        }
                    }
                    .sortedWith { (_, contact1), (_, contact2) ->
                        when (filter) {
                            FilterType.NEWEST_FIRST -> (contact2.timestamp
                                ?: 0).compareTo(contact1.timestamp ?: 0)

                            FilterType.OLDEST_FIRST -> (contact1.timestamp
                                ?: 0).compareTo(contact2.timestamp ?: 0)

                            else -> 0
                        }
                    }
            ) { (id, contact) ->
                CardMessageTitle(
                    contact = contact,
                    onClick = {
                        contactViewModel.updateReadState(id, contact)
                        selectedContact = contact.copy(read = true)
                        showDialog = true
                    }
                )
                Spacer(modifier = Modifier.height(15.dp))
            }
        }

        if (showDialog && selectedContact != null) {
            ContactDialog(contact = selectedContact!!, onDismiss = { showDialog = false })
        }
    }
}



    @Composable
    fun CardMessageTitle(contact: Contact, onClick: () -> Unit) {
        Row(
            modifier = Modifier
                .clickable { onClick() }
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onBackground,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Push the Text to the right
            Text(
                text = contact.subject,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f) // Makes text take available space
            )

            Icon(
                imageVector = if (contact.read) Icons.Default.Drafts else Icons.Default.Mail,
                contentDescription = "Mail Icon"
            )
        }


    }


    @Composable
    fun CardMessageWhole(contact: Contact, onClick: () -> Unit) {
        val imageUrl = contact.imageUrl

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onBackground,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = LanguageManager.getText("name") + ": ${contact.name}")
                Spacer(modifier = Modifier.height(8.dp))

                Text(text = LanguageManager.getText("email") + ": ${contact.emailFrom}")
                Spacer(modifier = Modifier.height(8.dp))

                Text(text = LanguageManager.getText("description") + ": ${contact.description}")
                Spacer(modifier = Modifier.height(8.dp))


                contact.timestamp?.let {
                    Text(text = "Timestamp: ${formatTimestamp(it)}")
                }

                if (!imageUrl.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentAlignment = Alignment.Center // Center loader while image loads
                    ) {
                        SubcomposeAsyncImage(
                            model = imageUrl,
                            contentDescription = "Message Image",
                            contentScale = ContentScale.Fit
                        ) {
                            val state = painter.state
                            if (state is coil.compose.AsyncImagePainter.State.Loading) {
                                CircularProgressIndicator() // Show spinner while loading
                            } else {
                                SubcomposeAsyncImageContent() // Show the image when ready
                            }
                        }
                    }
                }
            }
        }
    }


    @Composable
    fun ContactDialog(contact: Contact, onDismiss: () -> Unit) {
        Dialog(onDismissRequest = { onDismiss() }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onBackground,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp),
            ) {
                Column {
                    CardMessageWhole(contact, onClick = {}) // Display Contact Message
                    Spacer(modifier = Modifier.height(15.dp))

                    // Dismiss Button
                    Button(onClick = { onDismiss() }) {
                        Text(LanguageManager.getText("back"))
                    }
                }
            }
        }
    }

    @Composable
    fun FilterDropdown(currentFilter: FilterType, onFilterSelected: (FilterType) -> Unit) {
        var expanded by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Button(onClick = { expanded = true }) {
                Text(LanguageManager.getText("order by") + " : ${currentFilter.name}")
            }

            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(text = { Text("All") }, onClick = {
                    onFilterSelected(FilterType.ALL)
                    expanded = false
                })
                DropdownMenuItem(text = { Text("Read") }, onClick = {
                    onFilterSelected(FilterType.READ)
                    expanded = false
                })
                DropdownMenuItem(text = { Text("Unread") }, onClick = {
                    onFilterSelected(FilterType.UNREAD)
                    expanded = false
                })
                DropdownMenuItem(text = { Text("Newest First") }, onClick = {
                    onFilterSelected(FilterType.NEWEST_FIRST)
                    expanded = false
                })
                DropdownMenuItem(text = { Text("Oldest First") }, onClick = {
                    onFilterSelected(FilterType.OLDEST_FIRST)
                    expanded = false
                })
            }
        }
    }

fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
