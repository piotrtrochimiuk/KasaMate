package com.studia.kasamate.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.studia.kasamate.R
import com.studia.kasamate.data.Transaction
import com.studia.kasamate.ui.dialogs.AddTransactionDialog
import com.studia.kasamate.ui.viewmodel.SortType
import com.studia.kasamate.ui.viewmodel.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(navController: NavController, viewModel: TransactionViewModel = viewModel()) {
    val transactions by viewModel.allTransactions.observeAsState(emptyList())
    var showDialog by remember { mutableStateOf(false) }
    var editingTransaction by remember { mutableStateOf<Transaction?>(null) }
    var showMenu by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.settings))
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(text = { Text(stringResource(R.string.settings)) }, onClick = { navController.navigate("settings") })
                        DropdownMenuItem(text = { Text(stringResource(R.string.about)) }, onClick = { navController.navigate("about") })
                    }
                }
            )
        }
    ) { paddingValues ->
        if (showDialog || editingTransaction != null) {
            AddTransactionDialog(
                transaction = editingTransaction,
                onDismiss = {
                },
                onAddTransaction = { newTransaction ->
                    viewModel.addTransaction(newTransaction)
                },
                onUpdateTransaction = { updatedTransaction ->
                    viewModel.updateTransaction(updatedTransaction)
                },
                onDeleteTransaction = { transactionToDelete ->
                    viewModel.deleteTransaction(transactionToDelete)
                }
            )
        }

        Column(modifier = Modifier.padding(paddingValues)) {
            MonthlySummaryChart(transactions = transactions)
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(items = transactions, key = { it.id }) { transaction ->
                    TransactionItem(transaction = transaction) {
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = { }) {
                    Text(text = stringResource(R.string.add_transaction))
                }
                Box {
                    Button(onClick = { showSortMenu = true }) {
                        Text(text = stringResource(R.string.sort))
                    }
                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        DropdownMenuItem(text = { Text(stringResource(R.string.sort_name_asc)) }, onClick = { viewModel.setSortType(SortType.NAME_ASC); showSortMenu = false })
                        DropdownMenuItem(text = { Text(stringResource(R.string.sort_name_desc)) }, onClick = { viewModel.setSortType(SortType.NAME_DESC); showSortMenu = false })
                        DropdownMenuItem(text = { Text(stringResource(R.string.sort_price_asc)) }, onClick = { viewModel.setSortType(SortType.PRICE_ASC); showSortMenu = false })
                        DropdownMenuItem(text = { Text(stringResource(R.string.sort_price_desc)) }, onClick = { viewModel.setSortType(SortType.PRICE_DESC); showSortMenu = false })
                        DropdownMenuItem(text = { Text(stringResource(R.string.sort_date_asc)) }, onClick = { viewModel.setSortType(SortType.DATE_ASC); showSortMenu = false })
                        DropdownMenuItem(text = { Text(stringResource(R.string.sort_date_desc)) }, onClick = { viewModel.setSortType(SortType.DATE_DESC); showSortMenu = false })
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction, onClick: (Transaction) -> Unit) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable { onClick(transaction) }
    ) {
        Text(text = transaction.description)
        Text(text = "${transaction.price} ${transaction.currency}")
        Text(text = transaction.date)
    }
}
