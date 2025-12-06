package com.vako.abook.presentation.screen.user_profile

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vako.abook.presentation.screen.book.BookAction

@Composable
fun UserProfileScreen(
    onAction: (BookAction) -> Unit
) {
    val viewModel: UserProfileViewModel = hiltViewModel()
    UserProfileContent(
        onGoogleIdTokenReceived = {

        }
    )
}

@Composable
fun UserProfileContent(
    onGoogleIdTokenReceived: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    Button(
        modifier = Modifier.padding(top = 30.dp),
        onClick = {
        }
    ) {
        Text("Google")
    }
}

