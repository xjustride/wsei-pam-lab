package pl.wsei.pam.lab06

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch
import pl.wsei.pam.lab06.data.AppContainer
import pl.wsei.pam.lab06.ui.form.FormViewModel
import pl.wsei.pam.lab06.ui.form.TodoTaskInputBody
import pl.wsei.pam.lab06.ui.list.ListViewModel
import pl.wsei.pam.lab06.ui.receiver.TaskAlarmScheduler
import pl.wsei.pam.lab06.ui.theme.Lab06Theme
import java.time.LocalDate
import android.Manifest

class Lab06Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        container = (this.application as TodoApplication).container

        val alarmScheduler = TaskAlarmScheduler(this)
        alarmScheduler.scheduleAlarm(System.currentTimeMillis() + 5000, "Demo zadanie")

        setContent {
            Lab06Theme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        MainScreen()
                    } else {
                        MainScreenWithoutPermission()
                    }
                }
            }
        }
    }

    companion object {
        lateinit var container: AppContainer
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Lab06 Channel"
            val descriptionText = "Kanał powiadomień o nadchodzących zadaniach"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    enum class Priority {
        High, Medium, Low
    }

    data class TodoTask(
        val id: Int = 0,
        val title: String,
        val deadline: LocalDate,
        val isDone: Boolean,
        val priority: Priority
    )

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun MainScreen() {
        val navController = rememberNavController()
        val postNotificationPermission = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)

        LaunchedEffect(Unit) {
            if (!postNotificationPermission.status.isGranted) {
                postNotificationPermission.launchPermissionRequest()
            }
        }

        NavHost(navController = navController, startDestination = "list") {
            composable("list") { ListScreen(navController) }
            composable("form") { FormScreen(navController) }
            composable("settings") { SettingsScreen(navController) }
        }
    }

    @Composable
    fun ListScreen(
        navController: NavController,
        viewModel: ListViewModel = viewModel(factory = AppViewModelProvider.Factory)
    ) {
        val listUiState by viewModel.listUiState.collectAsState()

        Scaffold(
            topBar = {
                AppTopBar(
                    navController = navController,
                    title = "Zadania",
                    showBackIcon = false
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    shape = CircleShape,
                    onClick = { navController.navigate("form") }
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Dodaj", modifier = Modifier.scale(1.5f))
                }
            },
            content = { padding ->
                LazyColumn(modifier = Modifier.padding(padding)) {
                    items(items = listUiState.items, key = { it.id }) { task ->
                        ListItem(item = task)
                    }
                }
            }
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun FormScreen(
        navController: NavController,
        viewModel: FormViewModel = viewModel(factory = AppViewModelProvider.Factory)
    ) {
        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current

        Scaffold(
            topBar = {
                AppTopBar(
                    navController = navController,
                    title = "Dodaj zadanie",
                    showBackIcon = true,
                    onSaveClick = {
                        coroutineScope.launch {
                            viewModel.save(context)
                            navController.navigate("list") {
                                popUpTo("list") { inclusive = true }
                            }
                        }
                    }
                )
            }
        ) { padding ->
            TodoTaskInputBody(
                todoUiState = viewModel.todoTaskUiState,
                onItemValueChange = viewModel::updateUiState,
                modifier = Modifier.padding(padding)
            )
        }
    }

    @Composable
    fun SettingsScreen(navController: NavController) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Ustawienia",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AppTopBar(
        navController: NavController,
        title: String,
        showBackIcon: Boolean,
        onSaveClick: (() -> Unit)? = null
    ) {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary
            ),
            title = { Text(text = title) },
            navigationIcon = {
                if (showBackIcon) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Powrót")
                    }
                }
            },
            actions = {
                if (onSaveClick != null) {
                    OutlinedButton(onClick = onSaveClick) {
                        Text("Zapisz", fontSize = 18.sp)
                    }
                } else {
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(imageVector = Icons.Filled.Settings, contentDescription = "Ustawienia")
                    }
                    IconButton(onClick = { navController.navigate("form") }) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = "Dodaj")
                    }
                }
            }
        )
    }

    @Composable
    fun ListItem(item: TodoTask, modifier: Modifier = Modifier) {
        ElevatedCard(
            modifier = modifier
                .fillMaxWidth()
                .heightIn(120.dp)
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = item.title, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Text("Termin: ${item.deadline}")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Priorytet: ${item.priority}")
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(if (item.isDone) "Zrobione" else "Niezrobione")
            }
        }
    }

    @Composable
    fun MainScreenWithoutPermission() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Brak uprawnień do wysyłania powiadomień",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}
