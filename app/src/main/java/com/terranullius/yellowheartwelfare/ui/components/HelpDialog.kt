package com.terranullius.yellowheartwelfare.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.terranullius.yellowheartwelfare.ui.InitiativeTitle

@Composable
fun HelpDialog(
    modifier: Modifier = Modifier,
    isDonatable: Boolean = true,
    description: String = "placeholder",
    link: String? = null,
    onHelpClicked: (Int) -> Unit
) {
        Box(modifier = modifier
            .background(color = Color.Transparent.copy(alpha = 0f))
            .padding(12.dp)) {
            Surface(shape = RoundedCornerShape(12.dp)) {
                Column(
                    Modifier.padding(8.dp)
                ) {
                    val textFieldText = remember {
                        mutableStateOf("")
                    }
                    val isTextFieldTextError = remember {
                        mutableStateOf(false)
                    }
                    InitiativeTitle(text = description, size = MaterialTheme.typography.h5)
                    Divider(Modifier.height(12.dp), color = Color.Transparent)
                    if (isDonatable) {
                        OutlinedTextField(
                            isError = isTextFieldTextError.value,
                            value = textFieldText.value,
                            onValueChange = {
                                if (it.toIntOrNull() == null) {
                                    isTextFieldTextError.value = true
                                    textFieldText.value = it
                                } else {
                                    isTextFieldTextError.value = false
                                    textFieldText.value = it
                                }
                            },
                            singleLine = true,
                            maxLines = 1,
                            modifier = Modifier
                                .fillMaxWidth(),
                            label = { Text(text = "Amount") },
                            keyboardOptions =
                            KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))

                    HelpButton(modifier = Modifier.fillMaxWidth()) {
                        if (isDonatable) {
                            if (!isTextFieldTextError.value && textFieldText.value.isNotBlank()) {
                                onHelpClicked(
                                    textFieldText.value.toInt()
                                )
                            }
                        } else {
                            onHelpClicked(10)
                        }
                    }
                }}

    }
}
