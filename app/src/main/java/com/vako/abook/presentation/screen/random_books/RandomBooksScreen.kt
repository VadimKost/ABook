package com.vako.abook.presentation.screen.random_books

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun RandomBooksScreen(
    onAction: (RandomBooksAction) -> Unit
) {
    val viewModel: RandomBooksViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()

    RandomBooksContent()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RandomBooksContent() {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("RandomBooks") })
        },
        content = { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "Hello RandomBooks"
                )
            }
        }
    )
}