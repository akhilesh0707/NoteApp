package com.aqube.notes.feature_note.presentation.add_edit_notes.components

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.aqube.notes.core.presentation.components.TransparentHintTextField
import com.aqube.notes.feature_note.domain.model.Note
import com.aqube.notes.feature_note.presentation.add_edit_notes.AddEditNoteEvent
import com.aqube.notes.feature_note.presentation.add_edit_notes.AddEditNoteViewModel
import com.aqube.notes.feature_note.presentation.add_edit_notes.UiEvent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun AddEditNoteScreen(
    navController: NavController,
    noteColor: Int,
    viewModel: AddEditNoteViewModel = hiltViewModel()
) {
    val titleState = viewModel.title.value
    val contentState = viewModel.content.value
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    val noteBackgroundAnimatable = remember {
        Animatable(Color(if (noteColor != -1) noteColor else viewModel.color.value))
    }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.SaveNote -> {
                    navController.navigateUp()
                }
                is UiEvent.ShowSnackBar -> {
                    scaffoldState.snackbarHostState.showSnackbar(message = event.message)
                }
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.onEvent(AddEditNoteEvent.SaveNote)
                },
                backgroundColor = MaterialTheme.colors.primary
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = "Save note")
            }
        },
        scaffoldState = scaffoldState
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(noteBackgroundAnimatable.value)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            )
            {
                Note.noteColors.forEach { color ->
                    val colorInt = color.toArgb()
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .shadow(10.dp)
                            .background(color)
                            .border(
                                width = 1.dp,
                                color = if (viewModel.color.value == colorInt)
                                    Color.Black
                                else
                                    Color.Transparent,
                            )
                            .clickable {
                                scope.launch {
                                    noteBackgroundAnimatable.animateTo(
                                        targetValue = Color(colorInt),
                                        animationSpec = tween(500)
                                    )
                                }
                                viewModel.onEvent(AddEditNoteEvent.ChangeColor(colorInt))
                            }
                    )

                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            TransparentHintTextField(
                text = titleState.text,
                hint = titleState.hint,
                onValueChange = { viewModel.onEvent(AddEditNoteEvent.EnteredTitle(it)) },
                onFocusChange = { viewModel.onEvent(AddEditNoteEvent.ChangeTitleFocus(it)) },
                isHintVisible = titleState.isHintVisible,
                textStyle = MaterialTheme.typography.h3,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))
            TransparentHintTextField(
                text = contentState.text,
                hint = contentState.hint,
                onValueChange = { viewModel.onEvent(AddEditNoteEvent.EnteredContent(it)) },
                onFocusChange = { viewModel.onEvent(AddEditNoteEvent.ChangeContentFocus(it)) },
                isHintVisible = contentState.isHintVisible,
                textStyle = MaterialTheme.typography.body1,
                modifier = Modifier.fillMaxHeight()
            )
        }
    }

}