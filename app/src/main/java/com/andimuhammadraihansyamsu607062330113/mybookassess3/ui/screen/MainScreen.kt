package com.andimuhammadraihansyamsu607062330113.mybookassess3.ui.screen

import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.andimuhammadraihansyamsu607062330113.mybookassess3.BuildConfig
import com.andimuhammadraihansyamsu607062330113.mybookassess3.R
import com.andimuhammadraihansyamsu607062330113.mybookassess3.model.Buku
import com.andimuhammadraihansyamsu607062330113.mybookassess3.model.User
import com.andimuhammadraihansyamsu607062330113.mybookassess3.network.ApiStatus
import com.andimuhammadraihansyamsu607062330113.mybookassess3.network.UserDataStore
import com.andimuhammadraihansyamsu607062330113.mybookassess3.ui.theme.MyBookAssess2Theme
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val dataStore = UserDataStore(context)
    val user by dataStore.userFlow.collectAsState(User())

    val viewModel: MainViewModel = viewModel()
    val errorMessage by viewModel.errorMessage

    var showDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }
    var selectedBuku by remember { mutableStateOf<Buku?>(null) }

    val openBukuDialog = { buku: Buku ->
        selectedBuku = buku
        showDialog = true
    }

    var showAddBukuDialog by remember { mutableStateOf(false) }
    val openAddBukuDialog = { showAddBukuDialog = true }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                actions = {
                    IconButton(onClick = {
                        if (user.email.isEmpty()) {
                            CoroutineScope(Dispatchers.IO).launch { signIn(context, dataStore) }
                        } else {
                            showProfileDialog = true
                        }
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_account_circle_24),
                            contentDescription = stringResource(R.string.profil),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = openAddBukuDialog) {
                Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(id = R.string.tambah_hewan))
            }
        }
    ) { innerPadding ->
        ScreenContent(viewModel, user.email, modifier = Modifier.padding(innerPadding), openBukuDialog = openBukuDialog)

        selectedBuku?.let { buku ->
            if (showDialog) {
                BukuDialog(
                    buku = buku,
                    onDismissRequest = { showDialog = false },
                    onEditClick = {

                    },
                    onDeleteClick = {
                        viewModel.deleteBook(buku.id)
                        showDialog = false
                    }
                )
            }
        }

        if (showProfileDialog) {
            ProfilDialog(
                user = user,
                onDismissRequest = { showProfileDialog = false }) {
                CoroutineScope(Dispatchers.IO).launch { signOut(context, dataStore) }
                showProfileDialog = false
            }
        }

        if (showAddBukuDialog) {
            AddBukuDialog(
                onDismissRequest = { showAddBukuDialog = false },
                onSave = { judul, penulis, tahunTerbit, description, selectedImage ->
                    viewModel.addNewBuku(judul, user.email, penulis, tahunTerbit, description, selectedImage)
                    showAddBukuDialog = false
                }
            )
        }

        if (errorMessage != null) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            viewModel.clearMessage()
        }
    }
}

@Composable
fun ScreenContent(viewModel: MainViewModel, userId: String, modifier: Modifier = Modifier, openBukuDialog: (Buku) -> Unit) {
    val data by viewModel.data
    val status by viewModel.status.collectAsState()
    val context = LocalContext.current
    var isConnected by remember { mutableStateOf(checkInternetConnection(context)) }

    LaunchedEffect(userId, isConnected) {
        if (isConnected) {
            if (userId.isNotEmpty()) {
                viewModel.retrieveData(userId)
            }
        }
    }

    if (!isConnected) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "No Internet Connection", color = Color.Red)

            Button(
                onClick = {
                    isConnected = checkInternetConnection(context)
                    if (isConnected) {
                        Log.d("Internet", "berhasil connect internet.")
                    } else {
                        Log.d("Internet", "gagal connect internet.")
                    }
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Reconnect")
            }
        }
    } else if (userId.isEmpty()) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
                CoroutineScope(Dispatchers.IO).launch { signIn(context, UserDataStore(context)) }
            }) {
                Text(text = "sign in untuk melihat buku")
            }
        }
    } else {
        when (status) {
            ApiStatus.LOADING -> {
                Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            ApiStatus.SUCCESS -> {
                LazyVerticalGrid(
                    modifier = modifier.fillMaxSize().padding(4.dp),
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(data.size) { index ->
                        val buku = data[index]
                        ListItem(buku) {
                            openBukuDialog(buku)
                        }
                    }
                }
            }
            ApiStatus.FAILED -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = stringResource(id = R.string.error))
                    Button(
                        onClick = { viewModel.retrieveData(userId) },
                        modifier = Modifier.padding(top = 16.dp),
                        contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
                    ) {
                        Text(text = stringResource(id = R.string.try_again))
                    }
                }
            }
        }
    }
}

fun checkInternetConnection(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkCapabilities = connectivityManager.activeNetwork?.let {
        connectivityManager.getNetworkCapabilities(it)
    }

    return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
}


@Composable
fun ListItem(buku: Buku, onClick: () -> Unit) {
    val statusColor = when (buku.status) {
        "belum baca" -> Color(0xFFE57373)
        "sedang baca" -> Color(0xFFFFEB3B)
        "sudah baca" -> Color(0xFF81C784)
        else -> Color.Gray
    }

    Box(
        modifier = Modifier
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.medium)
            .shadow(8.dp, shape = MaterialTheme.shapes.medium, clip = false)
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = MaterialTheme.shapes.medium)
                .clip(MaterialTheme.shapes.medium)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(buku.coverUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Cover buku: ${buku.judul}",
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.loading_img),
                error = painterResource(R.drawable.baseline_broken_image_24),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(MaterialTheme.shapes.medium)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0f, 0f, 0f, 0.5f))
                    .padding(8.dp)
                    .clip(MaterialTheme.shapes.medium)
            ) {
                Text(
                    text = buku.judul,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = buku.status,
                    fontStyle = FontStyle.Italic,
                    fontSize = 14.sp,
                    color = Color.White,
                    modifier = Modifier
                        .background(statusColor, shape = MaterialTheme.shapes.small)
                        .padding(6.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}

private suspend fun signIn(context : Context, dataStore: UserDataStore) {
    val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(BuildConfig.API_KEY)
        .build()

    val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    try {
        val credentialManager = CredentialManager.create(context)
        val result = credentialManager.getCredential(context,request)
        handleSignIn(result,dataStore)
    } catch (e: GetCredentialException) {
        Log.e("SIGN_IN", "Error:${e.localizedMessage}")
    }
}

private suspend fun handleSignIn(result: GetCredentialResponse, dataStore: UserDataStore) {
    val credential = result.credential
    if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
        try {
            val googleId = GoogleIdTokenCredential.createFrom(credential.data)
            val nama = googleId.displayName ?: ""
            val email = googleId.id
            val photoUrl = googleId.profilePictureUri.toString()
            dataStore.saveData(User(nama, email, photoUrl))
        } catch (e: GoogleIdTokenParsingException) {
            Log.e("SIGN_IN", "Error: ${e.localizedMessage}")
        }
    } else {
        Log.e("SIGN_IN", "Error: unrecognized custom credential type")
    }
}

private suspend fun signOut(context: Context,dataStore: UserDataStore) {
    try {
        val credentialManager = CredentialManager.create(context)
        credentialManager.clearCredentialState(
            ClearCredentialStateRequest()
        )
        dataStore.saveData(User())
    } catch (e: ClearCredentialException) {
        Log.e("SIGN-IN", "Error: ${e.errorMessage}")
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun GreetingPreview() {
    MyBookAssess2Theme {
        MainScreen()
    }
}
