package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.LocalAppStrings
import com.example.data.local.TaskStepEntity
import com.example.ui.components.CategoryManagerBottomSheet
import com.example.ui.components.TemplatePickerBottomSheet
import com.example.ui.viewmodel.TaskViewModel
import kotlinx.coroutines.launch

data class StepInputState(
    val localKey: String = java.util.UUID.randomUUID().toString(),
    val id: Long = 0,
    val whatToDo: String = "",
    val howToDoIt: String = "",
    val tipsOrAdvice: String = "",
    val isCompleted: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    taskId: Long?,
    viewModel: TaskViewModel,
    onNavigateBack: () -> Unit,
    onTaskSaved: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val isEditMode = taskId != null && taskId > 0

    val availableCategories by viewModel.manageableCategories.collectAsStateWithLifecycle()
    val allTasks by viewModel.rawTasks.collectAsStateWithLifecycle()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Work") }

    val stepList = remember {
        mutableStateListOf(
            StepInputState()
        )
    }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoaded by remember { mutableStateOf(!isEditMode) }

    // Add category dialog state
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryInput by remember { mutableStateOf("") }
    var addCategoryError by remember { mutableStateOf<String?>(null) }

    // Category manager sheet state
    var showCategoryManagerSheet by remember { mutableStateOf(false) }
    val categorySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Load existing task if in edit mode
    if (isEditMode) {
        val taskWithStepsFlow = remember(taskId) { viewModel.getTaskFlow(taskId!!) }
        val taskWithSteps by taskWithStepsFlow.collectAsStateWithLifecycle()

        LaunchedEffect(taskWithSteps) {
            if (taskWithSteps != null && !isLoaded) {
                val t = taskWithSteps!!.task
                title = t.title
                description = t.description
                category = t.category

                stepList.clear()
                taskWithSteps!!.steps.sortedBy { it.stepIndex }.forEach { stp ->
                    stepList.add(
                        StepInputState(
                            localKey = "step_${stp.id}_${stp.stepIndex}",
                            id = stp.id,
                            whatToDo = stp.whatToDo,
                            howToDoIt = stp.howToDoIt,
                            tipsOrAdvice = stp.tipsOrAdvice,
                            isCompleted = stp.isCompleted
                        )
                    )
                }
                if (stepList.isEmpty()) {
                    stepList.add(StepInputState())
                }
                isLoaded = true
            }
        }
    }

    var showTemplateSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) strings.editTaskHeader else strings.createTaskHeader,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("add_edit_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (!isEditMode) {
                        IconButton(
                            onClick = { showTemplateSheet = true },
                            modifier = Modifier.testTag("add_edit_template_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = strings.blueprints,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(strings.cancel)
                    }

                    Button(
                        onClick = {
                            if (title.isBlank()) {
                                errorMessage = strings.pleaseEnterTitle
                                return@Button
                            }

                            val validSteps = stepList.filter { it.whatToDo.isNotBlank() }
                            if (validSteps.isEmpty()) {
                                errorMessage = strings.pleaseAddStep
                                return@Button
                            }

                            errorMessage = null
                            val finalCategory = category.trim().ifEmpty { "General" }

                            val domainSteps = validSteps.mapIndexed { index, stepInput ->
                                TaskStepEntity(
                                    id = stepInput.id,
                                    taskId = taskId ?: 0,
                                    stepIndex = index,
                                    whatToDo = stepInput.whatToDo.trim(),
                                    howToDoIt = stepInput.howToDoIt.trim(),
                                    tipsOrAdvice = stepInput.tipsOrAdvice.trim(),
                                    isCompleted = stepInput.isCompleted
                                )
                            }

                            viewModel.saveTask(
                                taskId = taskId,
                                title = title.trim(),
                                description = description.trim(),
                                category = finalCategory,
                                steps = domainSteps,
                                onSaved = { savedId ->
                                    onTaskSaved(savedId)
                                }
                            )
                        },
                        modifier = Modifier.weight(1.4f).testTag("save_task_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isEditMode) strings.saveChanges else strings.createTask, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Error banner if any
            if (errorMessage != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            // Task Meta Details Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = strings.taskDetailsHeader,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 1.sp
                            )
                        )

                        // Title
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it; errorMessage = null },
                            label = { Text(strings.taskTitleLabel) },
                            placeholder = { Text(strings.taskTitlePlaceholder) },
                            modifier = Modifier.fillMaxWidth().testTag("task_title_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        // Description / Goal
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text(strings.goalContextLabel) },
                            placeholder = { Text(strings.goalContextPlaceholder) },
                            modifier = Modifier.fillMaxWidth().testTag("task_desc_input"),
                            shape = RoundedCornerShape(12.dp),
                            minLines = 2,
                            maxLines = 4
                        )

                        // Category Selector with Custom Category management
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = strings.categoryLabel,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                                )

                                TextButton(
                                    onClick = { showCategoryManagerSheet = true },
                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Tune,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(strings.manageCategories, fontSize = 12.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Add Category Chip
                                item {
                                    FilledTonalButton(
                                        onClick = {
                                            newCategoryInput = ""
                                            addCategoryError = null
                                            showAddCategoryDialog = true
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.height(36.dp).testTag("add_new_category_chip")
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(strings.addCategory, fontSize = 12.sp)
                                    }
                                }

                                items(availableCategories) { cat ->
                                    val isSelected = category.equals(cat, ignoreCase = true)
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { category = cat },
                                        label = { Text(cat) },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Step Builder Header
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "${strings.stepProcedureHeader} (${stepList.size})",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 1.sp
                            )
                        )
                        Text(
                            text = strings.stepProcedureDesc,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Dynamic Step Cards
            itemsIndexed(stepList, key = { _, item -> item.localKey }) { index, stepInput ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .testTag("step_editor_card_$index"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Step Item Header: Step number & Reorder / Delete tools
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = "${strings.stepOf.uppercase()} ${index + 1}",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Move Up
                                IconButton(
                                    onClick = {
                                        if (index > 0) {
                                            val item = stepList.removeAt(index)
                                            stepList.add(index - 1, item)
                                        }
                                    },
                                    enabled = index > 0,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        Icons.Default.ArrowUpward,
                                        contentDescription = strings.moveUp,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                // Move Down
                                IconButton(
                                    onClick = {
                                        if (index < stepList.size - 1) {
                                            val item = stepList.removeAt(index)
                                            stepList.add(index + 1, item)
                                        }
                                    },
                                    enabled = index < stepList.size - 1,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        Icons.Default.ArrowDownward,
                                        contentDescription = strings.moveDown,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                // Delete Step
                                IconButton(
                                    onClick = {
                                        if (stepList.size > 1) {
                                            stepList.removeAt(index)
                                        } else {
                                            stepList[0] = StepInputState()
                                        }
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = strings.removeStep,
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        // Field 1: WHAT TO DO
                        OutlinedTextField(
                            value = stepInput.whatToDo,
                            onValueChange = { newValue ->
                                val current = stepList.getOrNull(index) ?: return@OutlinedTextField
                                stepList[index] = current.copy(whatToDo = newValue)
                                errorMessage = null
                            },
                            label = { Text(strings.stepWhatLabel) },
                            placeholder = { Text(strings.stepWhatPlaceholder) },
                            modifier = Modifier.fillMaxWidth().testTag("step_what_input_$index"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        // Field 2: HOW TO DO IT
                        OutlinedTextField(
                            value = stepInput.howToDoIt,
                            onValueChange = { newValue ->
                                val current = stepList.getOrNull(index) ?: return@OutlinedTextField
                                stepList[index] = current.copy(howToDoIt = newValue)
                            },
                            label = { Text(strings.stepHowLabel) },
                            placeholder = { Text(strings.stepHowPlaceholder) },
                            modifier = Modifier.fillMaxWidth().testTag("step_how_input_$index"),
                            shape = RoundedCornerShape(12.dp),
                            minLines = 2,
                            maxLines = 5
                        )

                        // Field 3: PRO-TIP (Optional)
                        OutlinedTextField(
                            value = stepInput.tipsOrAdvice,
                            onValueChange = { newValue ->
                                val current = stepList.getOrNull(index) ?: return@OutlinedTextField
                                stepList[index] = current.copy(tipsOrAdvice = newValue)
                            },
                            label = { Text(strings.stepTipsLabel) },
                            placeholder = { Text(strings.stepTipsPlaceholder) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }
                }
            }

            // Add Step & Quick Preset Step Adders
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        stepList.add(StepInputState())
                    },
                    modifier = Modifier.fillMaxWidth().testTag("add_step_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(strings.addAnotherStep, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quick Step Helper chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilledTonalButton(
                        onClick = {
                            stepList.add(
                                StepInputState(
                                    whatToDo = "Verify & Test Output",
                                    howToDoIt = "Review against requirements checklist and verify no errors remain.",
                                    tipsOrAdvice = "Check edge cases before final submission."
                                )
                            )
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("+ ${strings.addCheckpoint}", fontSize = 12.sp)
                    }

                    FilledTonalButton(
                        onClick = {
                            stepList.add(
                                StepInputState(
                                    whatToDo = "Prep Workspace & Materials",
                                    howToDoIt = "Open relevant tabs, files, and gather needed tools.",
                                    tipsOrAdvice = "Eliminate notifications to stay in deep flow."
                                )
                            )
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("+ ${strings.addPrepStep}", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }

    // Quick Add Category Dialog
    if (showAddCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = { Text(strings.addCategory) },
            text = {
                Column {
                    OutlinedTextField(
                        value = newCategoryInput,
                        onValueChange = {
                            newCategoryInput = it
                            addCategoryError = null
                        },
                        placeholder = { Text(strings.newCategoryPlaceholder) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("quick_category_input"),
                        shape = RoundedCornerShape(12.dp),
                        isError = addCategoryError != null
                    )
                    if (addCategoryError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = addCategoryError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val trimmed = newCategoryInput.trim()
                        if (trimmed.isBlank()) {
                            addCategoryError = strings.categoryCannotBeEmpty
                            return@Button
                        }
                        val success = viewModel.addCategory(trimmed)
                        if (success) {
                            category = trimmed
                            showAddCategoryDialog = false
                            newCategoryInput = ""
                            addCategoryError = null
                        } else {
                            addCategoryError = strings.categoryAlreadyExists
                        }
                    },
                    modifier = Modifier.testTag("submit_quick_category_button")
                ) {
                    Text(strings.addCategory)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategoryDialog = false }) {
                    Text(strings.cancel)
                }
            }
        )
    }

    // Category Manager Modal Sheet
    if (showCategoryManagerSheet) {
        CategoryManagerBottomSheet(
            sheetState = categorySheetState,
            categories = availableCategories,
            allTasks = allTasks,
            onAddCategory = { name -> viewModel.addCategory(name) },
            onDeleteCategory = { name -> viewModel.deleteCategory(name) },
            onRenameCategory = { oldName, newName -> viewModel.renameCategory(oldName, newName) },
            onDismiss = {
                coroutineScope.launch { categorySheetState.hide() }.invokeOnCompletion {
                    showCategoryManagerSheet = false
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
                    title = template.title
                    description = template.description
                    category = template.category
                    viewModel.addCategory(template.category)
                    stepList.clear()
                    template.steps.forEach { stp ->
                        stepList.add(
                            StepInputState(
                                whatToDo = stp.whatToDo,
                                howToDoIt = stp.howToDoIt,
                                tipsOrAdvice = stp.tipsOrAdvice
                            )
                        )
                    }
                }
            }
        )
    }
}
