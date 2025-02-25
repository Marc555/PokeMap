package cat.copernic.pokemap.presentation.ui.screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cat.copernic.pokemap.R
import cat.copernic.pokemap.data.DTO.Users
import cat.copernic.pokemap.presentation.ui.components.sendPasswordResetEmail
import cat.copernic.pokemap.presentation.viewModel.AuthViewModel
import cat.copernic.pokemap.presentation.viewModel.UsersViewModel
import cat.copernic.pokemap.utils.LanguageManager
import java.io.File

@Composable
fun EditProfile(navController: NavController,
                userUid: String,
                usersViewModel: UsersViewModel = viewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(userUid) {
        usersViewModel.fetchUserByUid(userUid)
    }
    val user by usersViewModel.user.collectAsStateWithLifecycle()

    var editableUser by remember { mutableStateOf(user ?: Users()) }

    // Sincronizar editableUser con los cambios de user
    LaunchedEffect(user) {
        user?.let { editableUser = it }
    }

    val isLoading by usersViewModel.isLoading.collectAsState()
    val isUploadingImage by usersViewModel.isUploadingImage.collectAsState() // Estado de subida de imagen

    if (isLoading) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Label(LanguageManager.getText("edit_profile"))

            Spacer(modifier = Modifier.height(16.dp))

            Label(LanguageManager.getText("email"))
            EditField(
                value = editableUser.email,
                onValueChange = { newEmail -> editableUser = editableUser.copy(email = newEmail) },
                label = LanguageManager.getText("email"),
                keyboardType = KeyboardType.Email,
                enabled = false // Deshabilitar el campo de email
            )

            Label(LanguageManager.getText("username"))
            EditField(
                value = editableUser.username,
                onValueChange = { newUsername -> editableUser = editableUser.copy(username = newUsername) },
                label = LanguageManager.getText("username")
            )

            Label(LanguageManager.getText("name"))
            EditField(
                value = editableUser.name,
                onValueChange = { newName -> editableUser = editableUser.copy(name = newName) },
                label = LanguageManager.getText("name")
            )

            Label(LanguageManager.getText("surname"))
            EditField(
                value = editableUser.surname,
                onValueChange = { newSurname -> editableUser = editableUser.copy(surname = newSurname) },
                label = LanguageManager.getText("surname")
            )

            Label(LanguageManager.getText("friend_code"))
            EditField(
                value = editableUser.codeFriend,
                onValueChange = { newCodeFriend -> editableUser = editableUser.copy(codeFriend = newCodeFriend) },
                label = LanguageManager.getText("codeFriend")
            )

            Spacer(modifier = Modifier.height(5.dp))

            SelectAndPreviewImage(
                currentImageUrl = editableUser.imageUrl,
                onUploadImage = { uri ->
                    usersViewModel.uploadImageToStorage(
                        uri, // URI local de la imagen seleccionada
                        previousImageUrl = editableUser.imageUrl,
                        onSuccess = { url ->
                            editableUser = editableUser.copy(imageUrl = url) // Actualizar la URL en el usuario
                        }
                    )
                }
            )
            if (isUploadingImage) {
                CircularProgressIndicator()
            }
            SaveButton(
                onSave = {
                    usersViewModel.updateUser(userUid, editableUser)
                    navController.popBackStack() // Navegar hacia atrás después de guardar
                },
                enabled = !isUploadingImage // Deshabilitar el botón si se está subiendo una imagen
            )

            Spacer(modifier = Modifier.height(5.dp))

            ResetPassword(editableUser.email)

            DeleteCredentialsText(
                onDeleteCredentials = {
                    // Eliminar las credenciales del usuario
                    editableUser = editableUser.copy(username = "User deleted", email = "", name = "", surname = "", codeFriend = "", imageUrl = "")
                    usersViewModel.deleteUserCredentials(editableUser, navController) { success ->
                        if (success) {
                            navController.navigate("login") { // Redirigir a la pantalla de inicio de sesión
                                popUpTo(navController.graph.startDestinationId) { // Limpiar la pila de navegación
                                    inclusive = true
                                }
                            }
                        } else {
                            // Mostrar un mensaje de error
                            Toast.makeText(context, "Error al eliminar credenciales", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun SaveButton(onSave: () -> Unit, enabled: Boolean = true) {
    Button(
        onClick = onSave,
        enabled = enabled, // Controlar si el botón está habilitado
        colors = ButtonDefaults.buttonColors(
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary)
    ) {
        Text(LanguageManager.getText("save_changes"))
    }
}

@Composable
fun Label(text: String, abeeZee: FontFamily = FontFamily(Font(R.font.abeezee))) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onBackground,
        fontFamily = abeeZee, fontSize = 25.sp
    )
}

@Composable
fun EditField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    enabled: Boolean = true // Nuevo parámetro para controlar si el campo es editable
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp),
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
        enabled = enabled // Deshabilitar la edición si `enabled` es false
    )
}

@Composable
fun SelectAndPreviewImage(
    currentImageUrl: String?,
    onUploadImage: (Uri) -> Unit
) {
    var imageUrl by remember { mutableStateOf("") }
    val context = LocalContext.current

    var photoFile: File? = null
    var currentPhotoUri: Uri? = null

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            currentPhotoUri?.let { uri ->
                imageUrl = uri.toString()
                onUploadImage(uri) // Subir imagen a Firebase Storage y obtener la URL HTTPS
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUrl = it.toString()
            onUploadImage(it) // Subir imagen a Firebase Storage y obtener la URL HTTPS
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val photoFileTemp = createImageFile(context)
            photoFile = photoFileTemp
            currentPhotoUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                photoFileTemp
            )
            cameraLauncher.launch(currentPhotoUri!!)
        } else {
            // Handle permission not granted case
        }
    }

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if(currentImageUrl != null) {
            ImageProfile(currentImageUrl, modifier = Modifier.size(100.dp))
        }else {
            ImageProfile(imageUrl, modifier = Modifier.size(100.dp))
        }

        Spacer(modifier = Modifier.width(5.dp))

        Button(onClick = {
            galleryLauncher.launch("image/*")
        }) {
            Text(LanguageManager.getText("gallery"))
        }

        Spacer(modifier = Modifier.width(5.dp))

        Button(onClick = {
            permissionLauncher.launch(android.Manifest.permission.CAMERA)
        }) {
            Text(LanguageManager.getText("camera"))
        }
    }
}

private fun createImageFile(context: Context): File {
    val storageDir = context.getExternalFilesDir(null)
    return File.createTempFile(
        "profile_image",
        ".jpg",
        storageDir
    )
}

@Composable
fun ResetPassword(email: String){
    val context = LocalContext.current
    Text(text = LanguageManager.getText("forgot password"),
        color = MaterialTheme.colorScheme.error,
        modifier = Modifier.clickable {
            sendPasswordResetEmail(email) { isSuccessful ->
                if (isSuccessful) {
                    Toast.makeText(context, LanguageManager.getText("email sent"), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, LanguageManager.getText("error message"), Toast.LENGTH_SHORT).show()
                }
            }
        }
    )
}

@Composable
fun DeleteCredentialsText(onDeleteCredentials: () -> Unit) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    // Diálogo de confirmación
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(LanguageManager.getText("delete_credentials_title")) },
            text = { Text(LanguageManager.getText("delete_credentials_message")) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteCredentials()
                        showDialog = false
                    }
                ) {
                    Text(
                        text = LanguageManager.getText("delete_button"),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text(LanguageManager.getText("cancel_button"))
                }
            }
        )
    }

    // Texto clicable
    Text(
        text = LanguageManager.getText("delete_account_text"),
        color = MaterialTheme.colorScheme.error,
        modifier = Modifier
            .clickable { showDialog = true } // Mostrar diálogo al hacer clic
            .padding(8.dp),
        style = MaterialTheme.typography.bodyMedium
    )
}