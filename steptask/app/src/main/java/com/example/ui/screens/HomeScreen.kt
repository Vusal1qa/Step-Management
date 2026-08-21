package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.PendingActions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.LocalAppStrings
import com.example.ui.components.CategoryManagerBottomSheet
import com.example.ui.components.StatsBanner
import com.example.ui.components.TaskCard
import com.example.ui.components.TemplatePickerBottomSheet
import com.example.ui.viewmodel.TaskFilter
import com.example.ui.viewmodel.TaskSort
import com.example.ui.viewmodel.TaskViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: TaskViewModel,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToFocusMode: (Long) -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val tasks by viewModel.filteredTasks.collectAsStateWithLifecycle()
    val allTasks by viewModel.rawTasks.collectAsStateWithLifecycle()
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val manageableCategories by viewModel.manageableCategories.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val selectedSort by viewModel.selectedSort.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    val searchFocusRequester = remember { FocusRequester() }
    var isSearchActive by remember { mutableStateOf(false) }
    var sortMenuExpanded by remember { mutableStateOf(false) }
    var showTemplateSheet by remember { mutableStateOf(false) }
    var showCategorySheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val categorySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(isSearchActive) {
        if (isSearchActive) {
            searchFocusRequester.requestFocus()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AnimatedContent(
                targetState = isSearchActive,
                transitionSpec = {
                    if (targetState) {
                        (slideInHorizontally(animationSpec = tween(280)) { width -> width / 4 } + fadeIn(animationSpec = tween(280)))
                            .togetherWith(slideOutHorizontally(animationSpec = tween(200)) { width -> -width / 4 } + fadeOut(animationSpec = tween(200)))
                    } else {
                        (slideInHorizontally(animationSpec = tween(280)) { width -> -width / 4 } + fadeIn(animationSpec = tween(280)))
                            .togetherWith(slideOutHorizontally(animationSpec = tween(200)) { width -> width / 4 } + fadeOut(animationSpec = tween(200)))
                    }
                },
                label = "topBarSearchTransition"
            ) { active ->
                if (active) {
                    TopAppBar(
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    isSearchActive = false
                                    viewModel.setSearchQuery("")
                                },
                                modifier = Modifier.testTag("search_back_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = strings.cancel
                                )
                            }
                        },
                        title = {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { viewModel.setSearchQuery(it) },
                                placeholder = {
                                    Text(
                                        text = "Search",
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .focusRequester(searchFocusRequester)
                                    .testTag("search_text_field"),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                ),
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription = strings.clearSearch,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            )
                        },
                        actions = {
                            IconButton(
                                onClick = {
                                    isSearchActive = false
                                    viewModel.setSearchQuery("")
                                },
                                modifier = Modifier.testTag("close_search_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = strings.cancel
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background
                        )
                    )
                } else {
                    TopAppBar(
                        navigationIcon = {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .padding(start = 12.dp, end = 4.dp)
                                    .size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.List,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.padding(6.dp)
                                )
                            }
                        },
                        title = {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = strings.appName,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        lineHeight = 20.sp
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = strings.appSubtitle,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp,
                                        lineHeight = 13.sp
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = { isSearchActive = true },
                                modifier = Modifier.testTag("toggle_search_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search"
                                )
                            }

                            Box {
                                IconButton(
                                    onClick = { sortMenuExpanded = true },
                                    modifier = Modifier.testTag("sort_menu_button")
                                ) {
                                    Icon(imageVector = Icons.Default.Sort, contentDescription = strings.sortBy)
                                }

                                DropdownMenu(
                                    expanded = sortMenuExpanded,
                                    onDismissRequest = { sortMenuExpanded = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text(strings.sortNewest) },
                                        onClick = {
                                            viewModel.setSelectedSort(TaskSort.NEWEST)
                                            sortMenuExpanded = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text(strings.sortProgress) },
                                        onClick = {
                                            viewModel.setSelectedSort(TaskSort.PROGRESS)
                                            sortMenuExpanded = false
                                        }
                                    )
                                }
                            }

                            IconButton(
                                onClick = { showTemplateSheet = true },
                                modifier = Modifier.testTag("open_templates_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = strings.blueprints,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            IconButton(
                                onClick = onNavigateToSettings,
                                modifier = Modifier.testTag("settings_top_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = strings.settingsTitle,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToCreate,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text(strings.newTask, fontWeight = FontWeight.Bold) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("create_task_fab")
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            // Stats Overview Banner
            item {
                StatsBanner(stats = stats)
            }

            // Status Filter Tabs
            item {
                SecondaryTabRow(
                    selectedTabIndex = selectedFilter.ordinal,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    containerColor = Color.Transparent
                ) {
                    Tab(
                        selected = selectedFilter == TaskFilter.ALL,
                        onClick = { viewModel.setSelectedFilter(TaskFilter.ALL) },
                        text = { Text("${strings.filterAll} (${stats.totalTasks})", fontWeight = FontWeight.SemiBold) }
                    )
                    Tab(
                        selected = selectedFilter == TaskFilter.ACTIVE,
                        onClick = { viewModel.setSelectedFilter(TaskFilter.ACTIVE) },
                        text = { Text("${strings.filterActive} (${stats.activeTasks})", fontWeight = FontWeight.SemiBold) }
                    )
                    Tab(
                        selected = selectedFilter == TaskFilter.COMPLETED,
                        onClick = { viewModel.setSelectedFilter(TaskFilter.COMPLETED) },
                        text = { Text("${strings.filterDone} (${stats.completedTasks})", fontWeight = FontWeight.SemiBold) }
                    )
                }
            }

            // Category Chips Row with Manage Button
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(categories) { category ->
                        val isSelected = selectedCategory.equals(category, ignoreCase = true)
                        val displayCategory = if (category.equals("All", ignoreCase = true)) strings.filterAll else category
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setSelectedCategory(category) },
                            label = { Text(displayCategory) },
                            shape = RoundedCornerShape(12.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }

                    // Manage Categories Button
                    item {
                        FilledTonalButton(
                            onClick = { showCategorySheet = true },
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(36.dp).testTag("home_manage_categories_button")
                        ) {
                            Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(strings.manageCategories, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Tasks List or Empty State
            if (tasks.isEmpty()) {
                item {
                    EmptyTasksState(
                        searchQuery = searchQuery,
                        selectedFilter = selectedFilter,
                        onCreateTask = onNavigateToCreate,
                        onOpenBlueprints = { showTemplateSheet = true }
                    )
                }
            } else {
                items(tasks, key = { it.task.id }) { taskWithSteps ->
                    TaskCard(
                        taskWithSteps = taskWithSteps,
                        onClick = { onNavigateToDetail(taskWithSteps.task.id) },
                        onStartFocusMode = { onNavigateToFocusMode(taskWithSteps.task.id) },
                        onToggleCompletion = { viewModel.toggleTaskCompletion(taskWithSteps) },
                        onEdit = { onNavigateToEdit(taskWithSteps.task.id) },
                        onDuplicate = { viewModel.duplicateTask(taskWithSteps.task.id) },
                        onReset = { viewModel.resetTaskProgress(taskWithSteps.task.id) },
                        onDelete = { viewModel.deleteTask(taskWithSteps.task.id) }
                    )
                }
            }
        }
    }

    if (showCategorySheet) {
        CategoryManagerBottomSheet(
            sheetState = categorySheetState,
            categories = manageableCategories,
            allTasks = allTasks,
            onAddCategory = { name -> viewModel.addCategory(name) },
            onDeleteCategory = { name -> viewModel.deleteCategory(name) },
            onRenameCategory = { oldName, newName -> viewModel.renameCategory(oldName, newName) },
            onDismiss = {
                coroutineScope.launch { categorySheetState.hide() }.invokeOnCompletion {
                    showCategorySheet = false
                }
            }
        )
    }

    if (showTemplateSheet) {
        TemplatePickerBottomSheet(
            sheetState = sheetState,
            onDismiss = { showTemplateSheet = false },
            onSelectTemplate = { template ->
                coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                    showTemplateSheet = false
                    viewModel.createFromTemplate(template) { newId ->
                        onNavigateToDetail(newId)
                    }
                }
            }
        )
    }
}

@Composable
private fun EmptyTasksState(
    searchQuery: String,
    selectedFilter: TaskFilter,
    onCreateTask: () -> Unit,
    onOpenBlueprints: () -> Unit
) {
    val strings = LocalAppStrings.current
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = when {
                        searchQuery.isNotBlank() -> Icons.Default.Search
                        selectedFilter == TaskFilter.COMPLETED -> Icons.Default.CheckCircle
                        else -> Icons.Outlined.PendingActions
                    },
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = when {
                    searchQuery.isNotBlank() -> strings.noMatchingTasks
                    selectedFilter == TaskFilter.COMPLETED -> strings.noCompletedTasks
                    selectedFilter == TaskFilter.ACTIVE -> strings.allTasksCompleted
                    else -> strings.noTasksTitle
                },
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = when {
                    searchQuery.isNotBlank() -> "Try searching for a different keyword or check spelling."
                    selectedFilter == TaskFilter.COMPLETED -> "Complete some step-by-step tasks to see them here."
                    else -> strings.noTasksDesc
                },
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onCreateTask,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("empty_state_create_task_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(strings.createTask)
                }

                Button(
                    onClick = onOpenBlueprints,
                    shape = RoundedCornerShape(12.dp),
                    colors = androidx.compose.material3.ButtonDefaults.filledTonalButtonColors()
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(strings.blueprints)
                }
            }
        }
    }
}
