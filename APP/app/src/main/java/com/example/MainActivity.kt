package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AppBottomBar
import com.example.ui.components.AppTopBar
import com.example.ui.components.DataSourceConfigDialog
import com.example.ui.navigation.Screen
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.ContactScreen
import com.example.ui.screens.CoursesScreen
import com.example.ui.screens.FacultyScreen
import com.example.ui.screens.GalleryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MoreScreen
import com.example.ui.screens.NoticesScreen
import com.example.ui.screens.ResultsScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.InstituteViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScreen()
            }
        }
    }
}

@Composable
fun MainAppScreen(
    viewModel: InstituteViewModel = viewModel()
) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    var showConfigDialog by remember { mutableStateOf(false) }

    val baseUrl by viewModel.baseUrl.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val globalError by viewModel.globalError.collectAsState()
    val settingsState by viewModel.settingsState.collectAsState()
    val coursesState by viewModel.coursesState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // Intercept back button to return to Home or close Search
    BackHandler(enabled = currentScreen != Screen.Home) {
        currentScreen = Screen.Home
    }

    // Show snackbar if error occurs
    LaunchedEffect(globalError) {
        globalError?.let { msg ->
            val result = snackbarHostState.showSnackbar(
                message = msg,
                actionLabel = "Retry"
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.refresh()
            }
            viewModel.clearGlobalError()
        }
    }

    val lastUpdated = coursesState.lastUpdated
    val lastUpdatedStr = remember(lastUpdated) {
        InstituteViewModel.formatTimestamp(lastUpdated)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (currentScreen != Screen.Search) {
                AppTopBar(
                    title = settingsState.data.instituteName,
                    subtitle = settingsState.data.tagline,
                    isRefreshing = isRefreshing,
                    lastUpdatedText = lastUpdatedStr,
                    onSearchClick = { currentScreen = Screen.Search },
                    onRefreshClick = { viewModel.refresh() },
                    onConfigClick = { showConfigDialog = true }
                )
            }
        },
        bottomBar = {
            if (currentScreen != Screen.Search) {
                AppBottomBar(
                    currentRoute = currentScreen.route,
                    onNavigate = { target ->
                        currentScreen = target
                    }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition"
            ) { screen ->
                when (screen) {
                    is Screen.Home -> HomeScreen(
                        viewModel = viewModel,
                        onNavigate = { currentScreen = it }
                    )
                    is Screen.Courses -> CoursesScreen(
                        viewModel = viewModel
                    )
                    is Screen.Notices -> NoticesScreen(
                        viewModel = viewModel
                    )
                    is Screen.Results -> ResultsScreen(
                        viewModel = viewModel
                    )
                    is Screen.Faculty -> FacultyScreen(
                        viewModel = viewModel
                    )
                    is Screen.Gallery -> GalleryScreen(
                        viewModel = viewModel
                    )
                    is Screen.About -> AboutScreen(
                        viewModel = viewModel
                    )
                    is Screen.Contact -> ContactScreen(
                        viewModel = viewModel
                    )
                    is Screen.More -> MoreScreen(
                        viewModel = viewModel,
                        onNavigate = { currentScreen = it },
                        onOpenConfig = { showConfigDialog = true }
                    )
                    is Screen.Search -> SearchScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = Screen.Home }
                    )
                }
            }
        }
    }

    if (showConfigDialog) {
        DataSourceConfigDialog(
            currentBaseUrl = baseUrl,
            onSaveUrl = { newUrl ->
                viewModel.updateBaseUrl(newUrl)
            },
            onResetDefault = {
                viewModel.resetBaseUrl()
            },
            onDismiss = { showConfigDialog = false }
        )
    }
}
