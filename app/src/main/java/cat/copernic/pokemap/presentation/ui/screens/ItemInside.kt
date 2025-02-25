package cat.copernic.pokemap.presentation.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.copernic.pokemap.data.DTO.Item
import cat.copernic.pokemap.presentation.viewModel.ItemViewModel
import cat.copernic.pokemap.data.DTO.Comment
import cat.copernic.pokemap.presentation.viewModel.CommentViewModel
import cat.copernic.pokemap.presentation.viewModel.UsersViewModel
import cat.copernic.pokemap.data.DTO.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbDownOffAlt
import androidx.compose.material.icons.outlined.ThumbUpOffAlt
import androidx.compose.ui.draw.shadow
import androidx.navigation.NavController
import cat.copernic.pokemap.data.DTO.Rol
import cat.copernic.pokemap.presentation.ui.components.ConfirmDeleteDialog
import cat.copernic.pokemap.presentation.ui.navigation.AppScreens
import cat.copernic.pokemap.utils.LanguageManager
import com.google.firebase.firestore.auth.User

@Composable
fun ItemInside(
    navController: NavController,
    itemId: String,
    userId: String? = FirebaseAuth.getInstance().currentUser?.uid
) {
    val userId = userId ?: run {
        navController.navigate(AppScreens.Login.rute) {
            popUpTo(AppScreens.Login.rute) { inclusive = true }
        }
    }

    val itemViewModel: ItemViewModel = viewModel()
    val commentViewModel: CommentViewModel = viewModel()
    val usersViewModel: UsersViewModel = viewModel()

    // Obtener el ítem y los comentarios cuando cambia el itemId
    LaunchedEffect(itemId) {
        itemViewModel.getItemById(itemId)
        commentViewModel.fetchComments(itemId)
        usersViewModel.fetchUserByUid(userId.toString())
    }

    val item by itemViewModel.item.collectAsState()
    val location = item?.let { LatLng(it.latitude, it.longitude) }
    var showAddComment by remember { mutableStateOf(false) }
    val user by usersViewModel.user.collectAsState()

    // Obtención de comentarios
    val comments by commentViewModel.comments.collectAsState(initial = emptyList())
    val commentCount = comments.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item?.let {
            ItemName(it)

            HorizontalDivider(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                thickness = 3.dp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                ItemsDescription(it)
                location?.let { loc -> Map(location = loc) }

                LikeDislikeButtons(
                    userLike = it.likes > 0,
                    userDislike = it.dislikes > 0,
                    likeCount = it.likes.toInt(),
                    dislikeCount = it.dislikes.toInt(),
                    commentCount = commentCount,
                    onLike = { itemViewModel.likeItem() },
                    onDislike = { itemViewModel.dislikeItem() },
                    onToggleComments = { showAddComment = !showAddComment },
                )

                CommentSection(
                    usersViewModel = usersViewModel,
                    itemId = itemId,
                    userId = userId.toString(),
                    user = user,
                    commentViewModel = commentViewModel,
                    comments = comments,
                    onAddComment = { commentText ->
                        commentViewModel.addComment(
                            comment = Comment(
                                text = commentText,
                                itemId = itemId,
                                userId = userId.toString()
                            ),
                            itemId = itemId
                        )
                    },
                    showAddComment = showAddComment
                )
            }
        }
    }
}

@Composable
fun ItemName(item: Item) {
    Text(
        text = item.name,
        style = MaterialTheme.typography.headlineLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 50.sp
        ),
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp, bottom = 10.dp)
    )
}

@Composable
fun ItemsDescription(item: Item) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = item.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun Map(location: LatLng) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location, 5f)
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        GoogleMap(
            modifier = Modifier
                .size(300.dp, 200.dp),
            cameraPositionState = cameraPositionState,
        ) {
            Marker(state = MarkerState(position = location))
        }
    }
}

@Composable
fun LikeDislikeButtons(
    userLike: Boolean,
    userDislike: Boolean,
    likeCount: Int,
    dislikeCount: Int,
    commentCount: Int,
    onLike: () -> Unit,
    onDislike: () -> Unit,
    onToggleComments: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp), // Añadido un poco de padding en los laterales
        horizontalArrangement = Arrangement.SpaceBetween, // Espacio entre los elementos
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Botón de Like (Verde)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f) // Asignamos peso para que ocupe el mismo espacio
        ) {
            IconButton(onClick = { onLike() }) {
                Icon(
                    imageVector = if (userLike) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUpOffAlt,
                    contentDescription = "Like",
                    tint = if (userLike) Color(0xFF4CAF50) else Color(0xFF4CAF50)
                )
            }
            Text(
                text = "$likeCount",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Botón para desplegar textbox para comentar (Naranja)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f) // Asignamos peso para que ocupe el mismo espacio
        ) {
            IconButton(onClick = { onToggleComments() }) {
                Icon(
                    imageVector = Icons.Filled.ChatBubbleOutline,
                    contentDescription = "Comentarios",
                    tint = Color(0xFFFF9800) // Naranja
                )
            }
            Text(
                text = "$commentCount",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Botón de Dislike (Rojo)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f) // Asignamos peso para que ocupe el mismo espacio
        ) {
            IconButton(onClick = { onDislike() }) {
                Icon(
                    imageVector = if (userDislike) Icons.Filled.ThumbDown else Icons.Outlined.ThumbDownOffAlt,
                    contentDescription = "Dislike",
                    tint = if (userDislike) Color(0xFFF44336) else Color(0xFFF44336)
                )
            }
            Text(
                text = "$dislikeCount",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun CommentSection(
    usersViewModel: UsersViewModel,
    itemId: String,
    userId: String?,
    user: Users?,
    commentViewModel: CommentViewModel,
    comments: List<Comment>,
    onAddComment: (String) -> Unit,
    showAddComment: Boolean,
) {
    var commentText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Comentarios",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        AnimatedVisibility(
            visible = showAddComment,
            enter = expandVertically(animationSpec = tween(300)),
            exit = shrinkVertically(animationSpec = tween(300))
        ) {
            Column {
                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    label = { Text("Añadir comentario") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        if (commentText.isNotBlank()) {
                            onAddComment(commentText)
                            commentText = ""
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Comentar")
                }
            }
        }

        comments.forEach { comment ->
            CommentCard(usersViewModel, itemId, userId, user,comment, commentViewModel)
        }
    }
}

@Composable
fun CommentCard(
    usersViewModel: UsersViewModel,
    itemId: String,
    currentUserId: String?,
    currentUser: Users?,
    comment: Comment,
    commentViewModel: CommentViewModel
) {
    val currentComment = comment
    val isLiked = currentComment.likedBy.contains(currentUserId)
    val isDisliked = currentComment.dislikedBy.contains(currentUserId)

    // Verificar si el usuario es el creador del comentario o tiene rol ADMIN
    val canDeleteComment = currentUserId == currentComment.userId || currentUser?.rol == Rol.ADMIN

    // Manejadores para Like y Dislike
    val onLikeClick = {
        if (isLiked) {
            commentViewModel.likeComment(currentComment.id)
        } else {
            if (isDisliked) {
                commentViewModel.dislikeComment(currentComment.id)
            }
            commentViewModel.likeComment(currentComment.id)
        }
    }

    val onDislikeClick = {
        if (isDisliked) {
            commentViewModel.dislikeComment(currentComment.id)
        } else {
            if (isLiked) {
                commentViewModel.likeComment(currentComment.id)
            }
            commentViewModel.dislikeComment(currentComment.id)
        }
    }

    usersViewModel.fetchUsersWithIds()
    val usersIds = usersViewModel.usersWithIds.collectAsState()
    val userName = usersIds.value.find { it.first == currentComment.userId }?.second?.username ?: "Usuario desconocido"

    // Estado para la confirmación de eliminación
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    // Diseño del comentario
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(color = MaterialTheme.colorScheme.background) // El mismo fondo que el background
            .border(width = 1.dp, color = MaterialTheme.colorScheme.onBackground)
            .padding(10.dp)
    ) {
        // Nombre del usuario
        Text(
            text = userName,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Texto del comentario
        Text(
            text = currentComment.text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Botones de Like y Dislike en la esquina inferior derecha
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Like
            IconButton(
                onClick = onLikeClick,
                modifier = Modifier.padding(end = 12.dp)
            ) {
                Icon(
                    imageVector = if (isLiked) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUpOffAlt,
                    contentDescription = "Like",
                    tint = if (isLiked) Color(0xFF4CAF50) else Color(0xFF4CAF50)
                )
            }

            Text(
                text = "${currentComment.likes}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Dislike
            IconButton(
                onClick = onDislikeClick,
                modifier = Modifier.padding(start = 12.dp)
            ) {
                Icon(
                    imageVector = if (isDisliked) Icons.Filled.ThumbDown else Icons.Outlined.ThumbDownOffAlt,
                    contentDescription = "Dislike",
                    tint = if (isDisliked) Color(0xFFF44336) else Color(0xFFF44336)
                )
            }

            Text(
                text = "${currentComment.dislikes}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Botón de eliminar (basura) si el usuario es el creador o ADMIN
            if (canDeleteComment) {
                IconButton(
                    onClick = { showDeleteConfirmation = true },
                    modifier = Modifier.padding(start = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,  // Asegúrate de tener el ícono correcto
                        contentDescription = "Eliminar Comentario",
                        tint = Color(0xFF9E9E9E)
                    )
                }
            }

            // Mostrar el dialogo de confirmación de eliminación
            if (showDeleteConfirmation) {
                ConfirmDeleteDialog(
                    title = LanguageManager.getText("delete confirmation"),
                    message = LanguageManager.getText("delete question"),
                    onConfirm = {
                        commentViewModel.deleteComment(comment.id, itemId)
                        showDeleteConfirmation = false
                    },
                    onDismiss = { showDeleteConfirmation = false }
                )
            }
        }
    }
}





