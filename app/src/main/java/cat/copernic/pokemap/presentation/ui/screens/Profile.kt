package cat.copernic.pokemap.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cat.copernic.pokemap.data.DTO.Users
import cat.copernic.pokemap.presentation.viewModel.UsersViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.copernic.pokemap.R
import cat.copernic.pokemap.data.DTO.Follow
import cat.copernic.pokemap.presentation.ui.navigation.AppScreens
import cat.copernic.pokemap.presentation.viewModel.FollowViewModel
import com.google.firebase.auth.FirebaseAuth
import cat.copernic.pokemap.utils.LanguageManager
import coil.compose.rememberAsyncImagePainter

@Composable
fun Profile(navController: NavController, userUid: String? = FirebaseAuth.getInstance().currentUser?.uid) {
    val profileViewModel: UsersViewModel = viewModel(key = "profileViewModel")
    val viewerViewModel: UsersViewModel = viewModel(key = "viewerViewModel")
    val followViewModel: FollowViewModel = viewModel()

    val userUid = userUid ?: run {
        navController.navigate(AppScreens.Login.rute){
            popUpTo(AppScreens.Login.rute) { inclusive = true }
        }
        return
    }

    val isLoadingProfile by profileViewModel.isLoading.collectAsState()
    val isLoadingViewer by viewerViewModel.isLoading.collectAsState()

    LaunchedEffect(userUid) {
        if (userUid != null) {
            FirebaseAuth.getInstance().currentUser?.uid?.let { viewerViewModel.fetchUserByUid(it) }
            profileViewModel.fetchUserByUid(userUid)
        }
    }

    val viewer = viewerViewModel.user.value
    val user = profileViewModel.user.value

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        if (isLoadingProfile && isLoadingViewer) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator()
            }
        } else {
            if (user != null && viewer != null) {

                // Verificar si el usuario es seguido y actualizar el botón
                LaunchedEffect(Unit) {
                    followViewModel.checkIfUserXIsFollowedByUserY(user.email, viewer.email)
                    followViewModel.fetchFollows()
                }

                Row(
                    modifier = Modifier
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ImageProfile(user.imageUrl)
                    Spacer(modifier = Modifier.width(16.dp))
                    Nombres(user, userUid, onClick = {
                        navController.navigate(AppScreens.EditProfile.createRoute(userUid))
                    })
                }

                SeguidosSeguidores(followViewModel, user.email, navController)

                if (userUid != FirebaseAuth.getInstance().currentUser?.uid) {
                    // Observar el estado isFollowed del ViewModel
                    val isFollowed by followViewModel.isFollowed.collectAsState()
                    val followedObjectId by followViewModel.followedObjectId.observeAsState()

                    // Mostrar el botón correspondiente
                    if (isFollowed) {
                        BotonSeguir(LanguageManager.getText("unfollow"),
                            onClick = {
                                if (followedObjectId != null) {
                                    followViewModel.deleteFollow(followedObjectId!!)
                                }
                            })
                    } else {
                        BotonSeguir(LanguageManager.getText("follow"), onClick = {
                            followViewModel.addFollow(Follow(followed = user.email, follower = viewer.email))
                        })
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                PublicationsNumber()
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(LanguageManager.getText("loading_user"), color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun Nombres(user: Users,
            userUid: String,
            abeeZee: FontFamily = FontFamily(Font(R.font.abeezee)),
            onClick: () -> Unit = {}) {
    Column {
        Text(text = "${user.name} ${user.surname}", fontFamily = abeeZee, fontSize = 25.sp, color = MaterialTheme.colorScheme.onBackground)
        Text(text = "@${user.username}", fontFamily = abeeZee, fontSize = 23.sp, color = Color.Gray)
        Text(text = "${user.codeFriend}", fontFamily = abeeZee, fontSize = 23.sp, color = Color.Gray)
        if (userUid == FirebaseAuth.getInstance().currentUser?.uid) {
            IconButton(onClick = onClick) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = LanguageManager.getText("edit_profile"))
            }
        }
    }
}

@Composable
fun ImageProfile(imageUrl: String? = null, modifier: Modifier = Modifier) {
    val painter = if (imageUrl.isNullOrEmpty()) {
        painterResource(id = R.drawable.logo)
    } else {
        rememberAsyncImagePainter(model = imageUrl)
    }

    Box(modifier = modifier.size(150.dp), contentAlignment = Alignment.Center) {
        Image(
            painter = painter,
            contentDescription = LanguageManager.getText("profile_image"),
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .border(2.dp, color = MaterialTheme.colorScheme.onBackground, CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun SeguidosSeguidores(followViewModel: FollowViewModel, email: String, navController: NavController) {
    // Actualiza el email en el ViewModel y carga los datos
    LaunchedEffect(email) {
        followViewModel.setEmail(email)
        followViewModel.fetchFollows()
    }

    // Observa el estado del ViewModel
    val followersList by followViewModel.followersList.collectAsState()
    val followingList by followViewModel.followingList.collectAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .border(1.dp, color = MaterialTheme.colorScheme.onBackground)
                .clickable {
                    navController.navigate(AppScreens.FollowersUsersScreen.createRoute(email))
                }
        ) {
            Text(
                text = "${LanguageManager.getText("followers")} ${followersList.size}",
                modifier = Modifier.padding(5.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Box(
            modifier = Modifier
                .wrapContentSize()
                .border(1.dp, color = MaterialTheme.colorScheme.onBackground)
                .clickable {
                    navController.navigate(AppScreens.FollowingUsersScreen.createRoute(email))
                }
        ) {
            Text(
                text = "${LanguageManager.getText("following")} ${followingList.size}",
                modifier = Modifier.padding(5.dp)
            )
        }
    }
}

@Composable
fun PublicationsNumber() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "${LanguageManager.getText("posts")} ")
        Box(
            modifier = Modifier
                .wrapContentSize()
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Text(
                text = "12",
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(3.dp),
            )
        }
    }
}

@Composable
fun BotonSeguir(text: String,
                abeeZee: FontFamily = FontFamily(Font(R.font.abeezee)),
                onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable(onClick = {
                onClick()
            }),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                fontFamily = abeeZee,
                fontSize = 20.sp,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}