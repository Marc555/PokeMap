package cat.copernic.pokemap.presentation.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import cat.copernic.pokemap.utils.LanguageManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import cat.copernic.pokemap.presentation.ui.theme.LocalCustomColors
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.UUID

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddItemDialog(
    context: Context,
    userLocation: Location,
    errorMessage: String? = null,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Double, Double, Uri) -> Unit
) {
    val customColors = LocalCustomColors.current

    var itemName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var localErrorMessage by remember { mutableStateOf<String?>(null) }

    var currentPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var photoFile by remember { mutableStateOf<File?>(null) }

    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
            localErrorMessage = null
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            currentPhotoUri?.let { uri ->
                imageUri = uri
                localErrorMessage = null
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val photoFileTemp = createImageFile(context)
            val photoUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                photoFileTemp
            )

            photoFile = photoFileTemp
            currentPhotoUri = photoUri // Guardamos la URI globalmente

            cameraLauncher.launch(photoUri) // Se usa la variable local
        } else {
            localErrorMessage = LanguageManager.getText("camera denied acces")
        }
    }

    AlertDialog(
        containerColor = customColors.popUpsMenu,
        onDismissRequest = onDismiss,
        title = { Text(text = LanguageManager.getText("add item"), color = MaterialTheme.colorScheme.onBackground) },
        text = {
            Column {
                TextField(
                    value = itemName,
                    onValueChange = {
                        if (it.length <= 15) { // Límite de 50 caracteres
                            itemName = it
                        }
                    },
                    label = { Text(LanguageManager.getText("name")) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )

                // Contador de caracteres
                Text(
                    text = "${itemName.length}/50",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.End)
                )

                errorMessage?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }

                TextField(
                    value = description,
                    onValueChange = {
                        if (it.length <= 200) { // Límite de 200 caracteres
                            description = it
                        }
                    },
                    label = { Text(text = LanguageManager.getText("description"), color = MaterialTheme.colorScheme.onSurface) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )

                // Contador de caracteres restantes
                Text(
                    text = "${description.length}/200",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.End)
                )

                MapPicker(
                    defaultLocation = LatLng(userLocation.latitude, userLocation.longitude),
                    onLocationSelected = { latLng ->
                        selectedLocation = latLng
                    }
                )

                Spacer(modifier = Modifier.height(5.dp))

                // Seleccionar imagen desde la galería
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(model = imageUri),
                            contentDescription = LanguageManager.getText("select image"),
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(LanguageManager.getText("select image"), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Botón para abrir la cámara
                Button(
                    onClick = {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(LanguageManager.getText("photo with camera"), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                localErrorMessage?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
                }

            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when {
                        imageUri == null -> localErrorMessage = LanguageManager.getText("no image error")
                        selectedLocation == null -> localErrorMessage = LanguageManager.getText("no location error")
                        else -> {
                            onConfirm(
                                itemName,
                                description,
                                selectedLocation!!.latitude,
                                selectedLocation!!.longitude,
                                imageUri!!
                            )
                        }
                    }
                },
                enabled = itemName.isNotBlank() && description.isNotBlank() && selectedLocation != null && imageUri != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = customColors.confirmButton
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = LanguageManager.getText("save"), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}

// Función para crear un archivo de imagen en el almacenamiento local
fun createImageFile(context: Context): File {
    val storageDir = context.getExternalFilesDir(null)
    return File.createTempFile(
        "item_image",
        ".jpg",
        storageDir
    )
}

@Composable
fun MapPicker(
    defaultLocation: LatLng?,
    onLocationSelected: (LatLng) -> Unit
) {
    val cameraPositionState = rememberCameraPositionState()
    var selectedLocation by remember { mutableStateOf(defaultLocation) }

    LaunchedEffect(defaultLocation) {
        defaultLocation?.let {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 20f)
        }
    }

    GoogleMap(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        cameraPositionState = cameraPositionState,
        onMapClick = { latLng ->
            selectedLocation = latLng
            onLocationSelected(latLng)
        }
    ) {
        selectedLocation?.let {
            Marker(state = MarkerState(position = it))
        }
    }
}











