package com.vako.abook.presentation.screen.splash_login

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.vako.abook.R
import com.vako.abook.presentation.theme.ABookTheme
import kotlinx.coroutines.launch

@Composable
fun SplashLoginScreen(
    onAction: (SplashLoginAction) -> Unit
) {
    val viewModel: SplashLoginViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.isShowingLogo,state.isLogged) {
        if (!state.isShowingLogo && state.isLogged) {
            onAction(SplashLoginAction.NavigateHome)
        }
    }

    SplashLoginContent(
        isShowingLogo = state.isShowingLogo,
        isLoggedIn = state.isLogged,
        onContinueAsGuest = { viewModel.onEvent(SplashLoginEvent.ContinueAsGuest) },
        onLoginViaGoogle = {
            scope.launch {
                val token = getGoogleIdToken(context)
                token?.let {
                    viewModel.onEvent(SplashLoginEvent.SignInViaGoogle(token))
                }
            }
        }
    )
}

@Composable
fun SplashLoginContent(
    isShowingLogo: Boolean,
    isLoggedIn: Boolean,
    onContinueAsGuest: () -> Unit,
    onLoginViaGoogle: () -> Unit,
) {
    if (isShowingLogo) {
        SplashContent()
    } else if (!isLoggedIn) {
        LoginContent(
            onContinueAsGuest = onContinueAsGuest,
            onLoginViaGoogle = onLoginViaGoogle
        )
    }
}

@Composable
fun LoginContent(
    onContinueAsGuest: () -> Unit,
    onLoginViaGoogle: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.app_logo),
                contentDescription = null
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Welcome", style = MaterialTheme.typography.headlineLarge)

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = "email",
                onValueChange = {},
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = "password",
                onValueChange = {},
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                enabled = false
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                enabled = false,
                shape = RoundedCornerShape(8.dp),
                onClick = {
                    onLoginViaGoogle()
                },
                modifier = Modifier
                    .height(48.dp)
                    .fillMaxWidth()
            ) {
                Text("Login")
            }

            Spacer(modifier = Modifier.height(16.dp))

            GoogleSignInButton(
                onClick = onLoginViaGoogle
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = onContinueAsGuest
            ) {
                Text("Continue as guest")
            }

        }
    }
}

@Composable
fun GoogleSignInButton(
    text: String = "Sign in with Google",
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.google_logo), // Add your Google logo
            contentDescription = "Google logo",
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, color = MaterialTheme.colorScheme.onSurface)
    }
}


@Composable
fun SplashContent() {
    val centerDarkBlue = Color(0xFF283250)
    val edgeDarkBlue = Color(0xFF1E253A)

    val radialGradientBrush = Brush.radialGradient(
        colors = listOf(centerDarkBlue, edgeDarkBlue),
        center = Offset.Unspecified
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = radialGradientBrush)
    ) {
        Image(
            modifier = Modifier.align(Alignment.Center),
            painter = painterResource(R.drawable.app_logo),
            contentDescription = null
        )
    }
}

suspend fun getGoogleIdToken(context: Context): String? {
    val credentialManager = CredentialManager.create(context)

    val googleOption = GetGoogleIdOption.Builder()
        .setServerClientId("153080114828-u7674jfetpt5sgu0mevs8gka9qctn420.apps.googleusercontent.com")
        .setFilterByAuthorizedAccounts(false)
        .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleOption)
        .build()

    val result = credentialManager.getCredential(
        context = context,
        request = request
    )

    val cred = result.credential

    if (cred is CustomCredential &&
        cred.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
    ) {
        val googleCred = GoogleIdTokenCredential.createFrom(cred.data)
        return googleCred.idToken
    }

    return null
}

@Preview
@Composable
private fun SplashLoginPreview() {
    ABookTheme() {
        Surface {
            SplashLoginContent(
                isShowingLogo = false,
                isLoggedIn = false,
                onContinueAsGuest = {},
                onLoginViaGoogle = {}
            )
        }
    }
}