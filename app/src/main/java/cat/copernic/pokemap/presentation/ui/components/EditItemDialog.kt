package cat.copernic.pokemap.presentation.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import cat.copernic.pokemap.data.DTO.Item
import cat.copernic.pokemap.presentation.ui.screens.uploadCategoryImage
import cat.copernic.pokemap.presentation.ui.theme.LocalCustomColors
import cat.copernic.pokemap.presentation.viewModel.ItemViewModel
import cat.copernic.pokemap.utils.LanguageManager
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import java.io.File

@Composable
fun EditItemDialog(
    context: Context,
    errorMessage: String?,
    item: Item,
    itemViewModel: ItemViewModel,
    onDismiss: () -> Unit,
    onConfirm: (Item, String, Uri?) -> Unit
) {
    val customColors = LocalCustomColors.current

    var itemName by remember { mutableStateOf(item.name) }
    var description by remember { mutableStateOf(item.description) }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageUrl by remember { mutableStateOf(item.imageUrl) }

    var localErrorMessage by remember { mutableStateOf<String?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    var currentPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var photoFile by remember { mutableStateOf<File?>(null) }

    var latitude by remember { mutableStateOf(item.latitude) }
    var longitude by remember { mutableStateOf(item.longitude) }
    var selectedLocation by remember { mutableStateOf(LatLng(item.latitude, item.longitude)) }

    // Estado para la confirmación de eliminación
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
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
            currentPhotoUri = photoUri

            cameraLauncher.launch(photoUri)
        } else {
            localErrorMessage = LanguageManager.getText("camera denied acces")
        }
    }

    // Mostrar el dialogo de confirmación de eliminación
    if (showDeleteConfirmation) {
        ConfirmDeleteDialog(
            title = LanguageManager.getText("delete confirmation"),
            message = LanguageManager.getText("delete question"),
            onConfirm = {
                itemViewModel.deleteItem(item.id, item.categoryId)
                onDismiss()
                showDeleteConfirmation = false
            },
            onDismiss = { showDeleteConfirmation = false }
        )
    }

    AlertDialog(
        containerColor = customColors.popUpsMenu,
        onDismissRequest = onDismiss,
        title = { Text(text = LanguageManager.getText("edit item")) },
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
                    context = context,
                    initialLocation = selectedLocation,
                    onLocationSelected = {
                        selectedLocation = it
                        latitude = it.latitude
                        longitude = it.longitude
                    }
                )

                Spacer(modifier = Modifier.height(5.dp))

                //Boton para abrir galería
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
                    } else if (imageUrl.isNotEmpty()) {
                        Image(
                            painter = rememberAsyncImagePainter(model = imageUrl),
                            contentDescription = LanguageManager.getText("current image"),
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(LanguageManager.getText("select image"), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(5.dp))

                // Botón para abrir la cámara
                Button(
                    onClick = {
                        permissionLauncher.launch(android.Manifest.permission.CAMERA)
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
            Column(modifier = Modifier.fillMaxWidth()) {
                // Indicador de carga mientras se sube la imagen
                if (isUploading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Button(
                        onClick = {
                            val updatedItem = item.copy(
                                name = itemName,
                                description = description,
                                imageUrl = imageUrl,
                                latitude = selectedLocation.latitude,
                                longitude = selectedLocation.longitude
                            )
                            onConfirm(updatedItem, item.categoryId, imageUri)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = customColors.confirmButton
                        ),
                        enabled = itemName.isNotEmpty() && description.isNotEmpty()
                    ) {
                        Text(text = LanguageManager.getText("save"), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Button(
                        onClick = { showDeleteConfirmation = true }, // Mostrar la confirmación
                        colors = ButtonDefaults.buttonColors(
                            containerColor = customColors.deleteButton
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isUploading
                    ) {
                        Text(text = LanguageManager.getText("delete"), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    )
}


@Composable
fun MapPicker(
    context: Context,
    initialLocation: LatLng,
    onLocationSelected: (LatLng) -> Unit
) {
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    var selectedLocation by remember { mutableStateOf(initialLocation) }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    currentLocation = LatLng(it.latitude, it.longitude)
                }
            }
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(selectedLocation, 10f)
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
        Marker(state = MarkerState(position = selectedLocation))
    }
}
