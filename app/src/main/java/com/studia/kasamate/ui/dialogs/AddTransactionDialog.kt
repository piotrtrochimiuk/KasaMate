package com.studia.kasamate.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.studia.kasamate.R
import com.studia.kasamate.data.Transaction
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
    transaction: Transaction? = null,
    onDismiss: () -> Unit,
    onAddTransaction: (Transaction) -> Unit,
    onUpdateTransaction: (Transaction) -> Unit,
    onDeleteTransaction: (Transaction) -> Unit
) {
    var description by remember { mutableStateOf(transaction?.description ?: "") }
    var price by remember { mutableStateOf(transaction?.price?.toString() ?: "") }
    var date by remember { mutableStateOf(transaction?.date ?: "") }

    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    if (date.isEmpty()) {
        date = sdf.format(Date())
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (transaction == null) stringResource(R.string.add_new_transaction) else stringResource(R.string.edit_transaction)) },
        text = {
            Column {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.description)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text(stringResource(R.string.price)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text(stringResource(R.string.date_format)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val priceDouble = price.toDoubleOrNull()
                    if (description.isNotBlank() && priceDouble != null && priceDouble > 0) {
                        if (transaction == null) {
                            onAddTransaction(
                                Transaction(
                                    description = description,
                                    price = priceDouble,
                                    date = date
                                )
                            )
                        } else {
                            onUpdateTransaction(
                                transaction.copy(
                                    description = description,
                                    price = priceDouble,
                                    date = date
                                )
                            )
                        }
                    }
                }
            ) {
                Text(if (transaction == null) stringResource(R.string.add) else stringResource(R.string.update))
            }
        },
        dismissButton = {
            Row {
                if (transaction != null) {
                    Button(
                        onClick = { onDeleteTransaction(transaction) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text(stringResource(R.string.delete))
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = onDismiss) {
                    Text(stringResource(R.string.cancel))
                }
            }
        }
    )
}
