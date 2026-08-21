package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.LocalAppStrings
import com.example.data.local.TaskWithSteps

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManagerBottomSheet(
    sheetState: SheetState,
    categories: List<String>,
    allTasks: List<TaskWithSteps> = emptyList(),
    onAddCategory: (String) -> Boolean,
    onDeleteCategory: (String) -> Unit,
    onRenameCategory: (String, String) -> Boolean,
    onDismiss: () -> Unit
) {
    val strings = LocalAppStrings.current
    var newCategoryInput by remember { mutableStateOf("") }
    var addError by remember { mutableStateOf<String?>(null) }
    var categoryToDelete by remember { mutableStateOf<String?>(null) }
    var categoryToRename by remember { mutableStateOf<String?>(null) }
    var renameInput by remember { mutableStateOf("") }
    var renameError by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = strings.manageCategories,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("dismiss_category_sheet")
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = strings.cancel)
                }
            }

            Text(
                text = strings.manageCategoriesDesc,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Add Category Form
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newCategoryInput,
                            onValueChange = {
                                newCategoryInput = it
                                addError = null
                            },
                            placeholder = { Text(strings.newCategoryPlaceholder) },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("new_category_input"),
                            shape = RoundedCornerShape(12.dp),
                            isError = addError != null
                        )

                        Button(
                            onClick = {
                                val trimmed = newCategoryInput.trim()
                                if (trimmed.isBlank()) {
                                    addError = strings.categoryCannotBeEmpty
                                    return@Button
                                }
                                val success = onAddCategory(trimmed)
                                if (success) {
                                    newCategoryInput = ""
                                    addError = null
                                } else {
                                    addError = strings.categoryAlreadyExists
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("add_category_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(strings.addCategory)
                        }
                    }

                    if (addError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = addError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Categories List
            Text(
                text = "${strings.allCategories} (${categories.size})",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 36.dp)
            ) {
                items(categories, key = { it }) { category ->
                    val isRenaming = categoryToRename == category
                    val taskCount = allTasks.count { it.task.category.equals(category, ignoreCase = true) }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("category_item_$category"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    ) {
                        if (isRenaming) {
                            // Inline Rename View
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = renameInput,
                                        onValueChange = {
                                            renameInput = it
                                            renameError = null
                                        },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f).testTag("rename_input_$category"),
                                        shape = RoundedCornerShape(10.dp),
                                        isError = renameError != null
                                    )

                                    FilledTonalIconButton(
                                        onClick = {
                                            val trimmed = renameInput.trim()
                                            if (trimmed.isBlank()) {
                                                renameError = strings.categoryCannotBeEmpty
                                                return@FilledTonalIconButton
                                            }
                                            val success = onRenameCategory(category, trimmed)
                                            if (success) {
                                                categoryToRename = null
                                                renameInput = ""
                                                renameError = null
                                            } else {
                                                renameError = strings.categoryAlreadyExists
                                            }
                                        },
                                        modifier = Modifier.testTag("confirm_rename_$category")
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = strings.save)
                                    }

                                    IconButton(
                                        onClick = {
                                            categoryToRename = null
                                            renameInput = ""
                                            renameError = null
                                        }
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = strings.cancel)
                                    }
                                }

                                if (renameError != null) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = renameError!!,
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        } else {
                            // Normal Category Row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.secondaryContainer
                                    ) {
                                        Text(
                                            text = category,
                                            style = MaterialTheme.typography.labelLarge.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer
                                            ),
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }

                                    if (taskCount > 0) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "$taskCount ${strings.totalTasks.lowercase()}",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = {
                                            categoryToRename = category
                                            renameInput = category
                                            renameError = null
                                        },
                                        modifier = Modifier.size(36.dp).testTag("edit_category_$category")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = strings.edit,
                                            modifier = Modifier.size(18.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    IconButton(
                                        onClick = { categoryToDelete = category },
                                        modifier = Modifier.size(36.dp).testTag("delete_category_$category")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = strings.deleteCategory,
                                            modifier = Modifier.size(18.dp),
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    if (categoryToDelete != null) {
        val cat = categoryToDelete!!
        val taskCount = allTasks.count { it.task.category.equals(cat, ignoreCase = true) }

        AlertDialog(
            onDismissRequest = { categoryToDelete = null },
            title = { Text(strings.deleteCategoryConfirmTitle) },
            text = {
                Column {
                    Text(
                        text = if (taskCount > 0) {
                            "'$cat' has $taskCount tasks. ${strings.deleteCategoryConfirmMsg}"
                        } else {
                            "Are you sure you want to delete '$cat'?"
                        }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteCategory(cat)
                        categoryToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("confirm_delete_category_button")
                ) {
                    Text(strings.confirmDelete)
                }
            },
            dismissButton = {
                TextButton(onClick = { categoryToDelete = null }) {
                    Text(strings.cancel)
                }
            }
        )
    }
}
