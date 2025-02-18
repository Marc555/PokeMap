package cat.copernic.pokemap.presentation.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cat.copernic.pokemap.data.DTO.Contact
import cat.copernic.pokemap.data.DTO.Users
import cat.copernic.pokemap.presentation.viewModel.AuthViewModel
import cat.copernic.pokemap.presentation.viewModel.ContactViewModal
import cat.copernic.pokemap.presentation.viewModel.UsersViewModel
import cat.copernic.pokemap.utils.LanguageManager
import coil.compose.rememberAsyncImagePainter
import com.android.identity.util.UUID
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage


@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ContactForm(NavController: NavController, viewModel: UsersViewModel = viewModel()) {
    val context = LocalContext.current
    val userUid = FirebaseAuth.getInstance().currentUser?.uid

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") } // Store email
    var description by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf(LanguageManager.getText("complaints or suggestions")) } // Initialize subject

    var imageUri by remember { mutableStateOf<Uri?>(null) } // Store selected image URI

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUri = uri // Store selected image URI
        }

    // Fetch user details when UID is available
    LaunchedEffect(userUid) {
        if (userUid != null) {
            viewModel.fetchUserByUid(userUid)
        }
    }

    val user: Users? = viewModel.user.value

    // Automatically fill email when user data is loaded
    LaunchedEffect(user) {
        if (user != null) {
            email = user.email // Auto-fill the email field
            name = user.name // Optionally auto-fill name
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, end = 20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(LanguageManager.getText("contact form"))

            FormInput(name, LanguageManager.getText("name"), { name = it })
            FormInput(
                email,
                LanguageManager.getText("email"),
                { email = it },
                readOnly = true
            ) // Read-only email

            DropdownMenuForm(
                option1 = "complaints or suggestions",
                option2 = "error email account",
                option3 = "other",
                selectedOption = subject,
                onOptionSelected = { subject = it } // Update state when an option is selected
            )
            FormInputLarge(description, LanguageManager.getText("description")) { description = it }

            // Image Picker Button
            Button(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.CameraAlt, // Camera icon
                    contentDescription = "Camera Icon",
                    modifier = Modifier.padding(end = 8.dp) // Space between icon and text
                )
                Text(LanguageManager.getText("upload pics"))
            }
            // Image Preview
            imageUri?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = "Selected Image",
                    modifier = Modifier
                        .size(150.dp)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(2.dp, MaterialTheme.colorScheme.primary)
                )
            }
            FormButton(context, name, email, subject, description, imageUri, NavController, user)
        }
    }
}

//One line input
@Composable
fun FormInput(
    value: String,
    label: String,
    onInputChange: (String) -> Unit,
    readOnly: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onInputChange,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        readOnly = readOnly, // Prevent editing if readOnly is true
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp),
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground)
    )
}


//Multi line input
@Composable
fun FormInputLarge(value: String, label: String, onInputChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onInputChange,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp),
        singleLine = false, // Allow multiple lines
        minLines = 2, // Minimum 4 lines tall
        maxLines = 6, // Maximum height limit
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground)
    )
}

//Dropdown menu
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuForm(
    option1: String,
    option2: String,
    option3: String,
    selectedOption: String, // Receive current selection
    onOptionSelected: (String) -> Unit // Callback to update selection in parent
) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf(option1, option2, option3)

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp),
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(LanguageManager.getText("subject")) },
            textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                    contentDescription = "Dropdown Icon",
                    modifier = Modifier.padding(end = 12.dp)
                )
            }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(LanguageManager.getText(option)) },
                    onClick = {
                        onOptionSelected(LanguageManager.getText(option)) // Update subject in parent
                        expanded = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp),
                )
            }
        }
    }
}

//Send button
@Composable
fun FormButton(
    context: Context,
    name: String,
    email: String,
    subject: String,
    description: String,
    imageUri: Uri?,
    navController: NavController,
    user: Users?
) {
    val contactViewModel = remember { ContactViewModal() }
    val isSuccess by contactViewModel.isSuccess.collectAsState()
    val storageRef = FirebaseStorage.getInstance().reference // Get Firebase Storage instance
    Button(
        onClick = {
            if (imageUri != null) {
                val imageRef = storageRef.child("contact_images/${UUID.randomUUID()}.jpg")

                imageRef.putFile(imageUri)
                    .addOnSuccessListener { taskSnapshot ->
                        imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                            val imageUrl = downloadUrl.toString()

                            // Now send form data along with image URL
                            contactViewModel.addContact(
                                Contact(
                                    name = name,
                                    emailFrom = user?.email,
                                    emailTo = "pokemapdeveloper@gmail.com",
                                    subject = subject,
                                    description = description,
                                    imageUrl = imageUrl // Save image URL in Firestore
                                )
                            )
                        }
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(
                            context,
                            "Image Upload Failed: ${exception.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            } else {
                // If no image, just send form data
                contactViewModel.addContact(
                    Contact(
                        name = name,
                        emailFrom = user?.email,
                        emailTo = "pokemapdeveloper@gmail.com",
                        subject = subject,
                        description = description
                    )
                )
            }
        },
        enabled = name.isNotBlank() && email.isNotBlank() && description.isNotBlank(),
        colors = ButtonDefaults.buttonColors(
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text(LanguageManager.getText("send"))
    }

    // Show success message and navigate when successful
    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            Toast.makeText(context, "Message sent successfully!", Toast.LENGTH_SHORT).show()
            contactViewModel.resetSuccessState() // Reset state after handling
            navController.navigate("home") // Navigate after success
        }
    }
}
