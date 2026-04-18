package com.studia.kasamate.ui.screens

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.studia.kasamate.R
import com.studia.kasamate.data.SettingsRepository
import com.studia.kasamate.data.Transaction
import com.studia.kasamate.ui.dialogs.AddTransactionDialog
import com.studia.kasamate.ui.theme.KasaMateTheme
import com.studia.kasamate.ui.viewmodel.SortType
import com.studia.kasamate.ui.viewmodel.TransactionViewModel
import com.studia.kasamate.ui.viewmodel.TransactionViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    navController: NavController,
    username: String,
    onLogout: () -> Unit,
    viewModel: TransactionViewModel = viewModel(factory = TransactionViewModelFactory(LocalContext.current.applicationContext as Application, username))
) {
    val transactions by viewModel.allTransactions.observeAsState(emptyList())
    val context = LocalContext.current
    val settingsRepository = remember { SettingsRepository(context) }
    var monthlyBudget by remember { mutableDoubleStateOf(settingsRepository.getMonthlyBudget()) }

    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }

    val filteredTransactions = transactions.filter { 
        isInSelectedMonth(it.date, selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.YEAR))
    }

    TransactionScreenContent(
        username = username,
        transactions = filteredTransactions,
        monthlyBudget = monthlyBudget,
        selectedDate = selectedDate,
        onDateChange = { selectedDate = it },
        onLogout = onLogout,
        onNavigateToSettings = { navController.navigate("settings") },
        onNavigateToAbout = { navController.navigate("about") },
        onAddTransaction = { viewModel.addTransaction(it) },
        onUpdateTransaction = { viewModel.updateTransaction(it) },
        onDeleteTransaction = { viewModel.deleteTransaction(it) },
        onSort = { viewModel.setSortType(it) },
        onAddIncome = { description, amount ->
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.format(selectedDate.time)
            
            // Add as a transaction
            viewModel.addTransaction(
                Transaction(
                    description = description,
                    price = amount,
                    date = date,
                    username = username,
                    isIncome = true
                )
            )
            
            // Increase budget
            val newBudget = monthlyBudget + amount
            settingsRepository.setMonthlyBudget(newBudget)
            monthlyBudget = newBudget
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransactionScreenContent(
    username: String,
    transactions: List<Transaction>,
    monthlyBudget: Double,
    selectedDate: Calendar,
    onDateChange: (Calendar) -> Unit,
    onLogout: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onAddTransaction: (Transaction) -> Unit,
    onUpdateTransaction: (Transaction) -> Unit,
    onDeleteTransaction: (Transaction) -> Unit,
    onSort: (SortType) -> Unit,
    onAddIncome: (String, Double) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var showIncomeDialog by remember { mutableStateOf(false) }
    var editingTransaction by remember { mutableStateOf<Transaction?>(null) }
    var showMenu by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val totalSpent = transactions.filter { !it.isIncome }.sumOf { it.price }
    val listState = rememberLazyListState()

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
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.settings)) },
                            onClick = {
                                showMenu = false
                                onNavigateToSettings()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.about)) },
                            onClick = {
                                showMenu = false
                                onNavigateToAbout()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.logout)) },
                            onClick = {
                                showMenu = false
                                onLogout()
                            }
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (showDialog || editingTransaction != null) {
            AddTransactionDialog(
                username = username,
                transaction = editingTransaction,
                onDismiss = {
                    showDialog = false
                    editingTransaction = null
                },
                onAddTransaction = { newTransaction ->
                    onAddTransaction(newTransaction)
                    showDialog = false
                },
                onUpdateTransaction = { updatedTransaction ->
                    onUpdateTransaction(updatedTransaction)
                    editingTransaction = null
                },
                onDeleteTransaction = { transactionToDelete ->
                    onDeleteTransaction(transactionToDelete)
                    editingTransaction = null
                }
            )
        }

        if (showIncomeDialog) {
            AddIncomeDialog(
                onDismiss = { showIncomeDialog = false },
                onConfirm = { description, income ->
                    onAddIncome(description, income)
                    showIncomeDialog = false
                }
            )
        }

        if (showDatePicker) {
            MonthYearPickerDialog(
                currentDate = selectedDate,
                onDismiss = { showDatePicker = false },
                onDateSelected = {
                    onDateChange(it)
                    showDatePicker = false
                }
            )
        }

        Column(modifier = Modifier.padding(paddingValues)) {
            // Month Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(onClick = {
                    val newDate = selectedDate.clone() as Calendar
                    newDate.add(Calendar.MONTH, -1)
                    onDateChange(newDate)
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Month")
                }
                
                Text(
                    text = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(selectedDate.time),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .clickable { showDatePicker = true },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = {
                    val newDate = selectedDate.clone() as Calendar
                    newDate.add(Calendar.MONTH, 1)
                    onDateChange(newDate)
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Month")
                }
            }

            MonthlySummaryChart(transactions = transactions)
            
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "${stringResource(R.string.total_spent)}: %.2f".format(totalSpent),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                if (monthlyBudget > 0) {
                    val remaining = monthlyBudget - totalSpent
                    Text(
                        text = "${stringResource(R.string.remaining_budget)}: %.2f".format(remaining),
                        fontSize = 16.sp,
                        color = if (remaining < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).scrollbar(listState)
            ) {
                items(items = transactions, key = { it.id }) { transaction ->
                    TransactionItem(transaction = transaction) {
                        editingTransaction = transaction
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.add_transaction),
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }
                Button(
                    onClick = { showIncomeDialog = true },
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.add_income),
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }
                Box(modifier = Modifier.weight(0.7f)) {
                    Button(
                        onClick = { showSortMenu = true },
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.sort),
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }
                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        DropdownMenuItem(text = { Text(stringResource(R.string.sort_name_asc)) }, onClick = { onSort(SortType.NAME_ASC); showSortMenu = false })
                        DropdownMenuItem(text = { Text(stringResource(R.string.sort_name_desc)) }, onClick = { onSort(SortType.NAME_DESC); showSortMenu = false })
                        DropdownMenuItem(text = { Text(stringResource(R.string.sort_price_asc)) }, onClick = { onSort(SortType.PRICE_ASC); showSortMenu = false })
                        DropdownMenuItem(text = { Text(stringResource(R.string.sort_price_desc)) }, onClick = { onSort(SortType.PRICE_DESC); showSortMenu = false })
                        DropdownMenuItem(text = { Text(stringResource(R.string.sort_date_asc)) }, onClick = { onSort(SortType.DATE_ASC); showSortMenu = false })
                        DropdownMenuItem(text = { Text(stringResource(R.string.sort_date_desc)) }, onClick = { onSort(SortType.DATE_DESC); showSortMenu = false })
                    }
                }
            }
        }
    }
}

@Composable
fun AddIncomeDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double) -> Unit
) {
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_income)) },
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
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text(stringResource(R.string.income_amount)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val income = amount.toDoubleOrNull()
                if (description.isNotBlank() && income != null) {
                    onConfirm(description, income)
                }
            }) {
                Text(stringResource(R.string.add))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
fun MonthYearPickerDialog(
    currentDate: Calendar,
    onDismiss: () -> Unit,
    onDateSelected: (Calendar) -> Unit
) {
    var selectedMonth by remember { mutableIntStateOf(currentDate.get(Calendar.MONTH)) }
    var selectedYear by remember { mutableIntStateOf(currentDate.get(Calendar.YEAR)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Month and Year") },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Month Picker
                Box {
                    var monthExpanded by remember { mutableStateOf(false) }
                    TextButton(onClick = { monthExpanded = true }) {
                        Text(SimpleDateFormat("MMMM", Locale.getDefault()).format(Calendar.getInstance().apply { set(Calendar.MONTH, selectedMonth) }.time))
                    }
                    DropdownMenu(expanded = monthExpanded, onDismissRequest = { monthExpanded = false }) {
                        for (i in 0..11) {
                            DropdownMenuItem(
                                text = { Text(SimpleDateFormat("MMMM", Locale.getDefault()).format(Calendar.getInstance().apply { set(Calendar.MONTH, i) }.time)) },
                                onClick = {
                                    selectedMonth = i
                                    monthExpanded = false
                                }
                            )
                        }
                    }
                }

                // Year Picker
                Box {
                    var yearExpanded by remember { mutableStateOf(false) }
                    TextButton(onClick = { yearExpanded = true }) {
                        Text(selectedYear.toString())
                    }
                    DropdownMenu(expanded = yearExpanded, onDismissRequest = { yearExpanded = false }) {
                        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                        for (i in currentYear - 5..currentYear + 5) {
                            DropdownMenuItem(
                                text = { Text(i.toString()) },
                                onClick = {
                                    selectedYear = i
                                    yearExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val newDate = Calendar.getInstance()
                newDate.set(Calendar.YEAR, selectedYear)
                newDate.set(Calendar.MONTH, selectedMonth)
                onDateSelected(newDate)
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun isInSelectedMonth(dateString: String, month: Int, year: Int): Boolean {
    val transactionCalendar = Calendar.getInstance()
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    try {
        transactionCalendar.time = sdf.parse(dateString)!!
        val transactionMonth = transactionCalendar.get(Calendar.MONTH)
        val transactionYear = transactionCalendar.get(Calendar.YEAR)
        return month == transactionMonth && year == transactionYear
    } catch (e: Exception) {
        return false
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
        Text(
            text = transaction.description,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Text(
            text = "${if (transaction.isIncome) "+" else "-"} ${transaction.price} ${transaction.currency}",
            color = if (transaction.isIncome) Color(0xFF2E7D32) else Color.Unspecified,
            fontSize = 14.sp
        )
        Text(
            text = transaction.date,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

fun Modifier.scrollbar(
    state: LazyListState,
    thickness: Dp = 4.dp,
    color: Color = Color.Gray.copy(alpha = 0.5f)
): Modifier = this.drawWithContent {
    drawContent()
    state.layoutInfo.visibleItemsInfo.let { visibleItems ->
        if (visibleItems.isNotEmpty()) {
            val totalItemsCount = state.layoutInfo.totalItemsCount
            val viewportHeight = size.height
            val itemsHeight = visibleItems.sumOf { it.size }.toFloat()
            val averageItemHeight = itemsHeight / visibleItems.size
            val estimatedTotalHeight = averageItemHeight * totalItemsCount

            if (estimatedTotalHeight > viewportHeight) {
                val knobHeight = (viewportHeight / estimatedTotalHeight) * viewportHeight
                val scrollOffset = (state.firstVisibleItemIndex * averageItemHeight + state.firstVisibleItemScrollOffset) / estimatedTotalHeight * viewportHeight

                drawRect(
                    color = color,
                    topLeft = Offset(size.width - thickness.toPx(), scrollOffset),
                    size = Size(thickness.toPx(), knobHeight)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionScreenPreview() {
    KasaMateTheme {
        TransactionScreenContent(
            username = "testuser",
            transactions = listOf(
                Transaction(1, "Groceries", 120.50, "2023-10-25", "PLN", "testuser"),
                Transaction(2, "Fuel", 300.00, "2023-10-26", "PLN", "testuser"),
                Transaction(3, "Salary", 5000.00, "2023-10-27", "PLN", "testuser", isIncome = true)
            ),
            monthlyBudget = 1000.0,
            selectedDate = Calendar.getInstance().apply { set(2023, Calendar.OCTOBER, 25) },
            onDateChange = {},
            onLogout = {},
            onNavigateToSettings = {},
            onNavigateToAbout = {},
            onAddTransaction = {},
            onUpdateTransaction = {},
            onDeleteTransaction = {},
            onSort = {},
            onAddIncome = { _, _ -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionItemPreview() {
    KasaMateTheme {
        TransactionItem(
            transaction = Transaction(
                id = 1,
                description = "Groceries",
                price = 120.50,
                date = "2023-10-25",
                currency = "PLN",
                username = "testuser"
            ),
            onClick = {}
        )
    }
}
