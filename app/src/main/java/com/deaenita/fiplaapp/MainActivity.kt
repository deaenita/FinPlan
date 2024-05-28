package com.deaenita.fiplaapp

import android.os.Bundle
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.deaenita.fiplaapp.ui.theme.FiplaAppTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.deaenita.fiplaapp.database.AppDatabase
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
import androidx.compose.foundation.lazy.items


class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            FiplaAppTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "FinPlan") }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        NavigationGraph(navController = navController, paddingValues = innerPadding)
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Dompet,
        BottomNavItem.Plan,
        BottomNavItem.Akun
    )
    BottomNavigation {
        val currentRoute = currentRoute(navController)
        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}


@Composable
fun NavigationGraph(navController: NavHostController, paddingValues: PaddingValues) {
    NavHost(
        navController, startDestination = BottomNavItem.Dompet.route) {
        composable(BottomNavItem.Dompet.route) { DompetScreen(navController
            ) }
        composable(BottomNavItem.Plan.route) { PlanScreen(navController, paddingValues) }
        composable(BottomNavItem.Akun.route) { AkunScreen(navController, paddingValues) }
        composable("webview") { WebViewScreen("https://mediakeuangan.kemenkeu.go.id/article/show/7-tips-mengatur-keuangan-agar-tabunganmu-terus-bertambah") }

    }
}


@Composable
fun PlanScreen(navController: NavHostController, paddingValues: PaddingValues) {
    Box(modifier = Modifier.padding(paddingValues)) {
        var showDialog by remember { mutableStateOf(false) }
        var selectedCategory by remember { mutableStateOf(Category.JAJAN) }
        var amount by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var plans by remember { mutableStateOf(emptyList<Plan>()) }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {
            FloatingActionButton(
                onClick = { showDialog = true },
                shape = CircleShape,
                modifier = Modifier
                    .padding(16.dp)
                    .padding(end = 30.dp)
                    .padding(bottom = 20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Plan",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = {
                    Text("Tambahkan Planning")
                },
                text = {
                    Column {
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Nama Planning") }
                        )
                        OutlinedTextField(
                            value = amount,
                            onValueChange = { amount = it },
                            label = { Text("Jumlah (Rp)") }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Kategori:")
                        CategorySelector(selectedCategory) { category ->
                            selectedCategory = category
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val newPlan = Plan(description, amount, selectedCategory)
                            plans = plans + newPlan
                            showDialog = false
                        }
                    ) {
                        Text("Tambahkan")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showDialog = false }
                    ) {
                        Text("Batal")
                    }
                }
            )
        }

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(plans) { plan ->
                    PlanCard(plan = plan)
                }
            }
        }
    }
}
@Composable
fun PlanCard(plan: Plan) {
    Card(
        backgroundColor = MaterialTheme.colors.surface,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = getCategoryIcon(plan.category),
                    contentDescription = "Category Icon",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colors.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = plan.description,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "Jumlah: Rp${plan.amount}",
                style = MaterialTheme.typography.body1
            )
            Text(
                text = "Kategori: ${plan.category}",
                style = MaterialTheme.typography.body2,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun CategorySelector(selectedCategory: Category, onCategorySelected: (Category) -> Unit) {
    val categories = Category.values().toList()

    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        categories.forEach { category ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onCategorySelected(category) }
            ) {
                Icon(
                    imageVector = getCategoryIcon(category),
                    contentDescription = "Category Icon",
                    modifier = Modifier
                        .size(40.dp)
                        .padding(4.dp),
                    tint = if (category == selectedCategory) MaterialTheme.colors.primary else Color.Gray
                )
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.caption,
                    color = if (category == selectedCategory) MaterialTheme.colors.primary else Color.Gray
                )
            }
        }
    }
}


fun getCategoryIcon(category: Category): ImageVector {
    return when (category) {
        Category.JAJAN -> Icons.Default.Fastfood
        Category.BELANJA -> Icons.Default.ShoppingCart
        Category.RUMAH -> Icons.Default.Home
        Category.LAINNYA -> Icons.Default.EmojiEvents
    }
}

enum class Category {
    JAJAN, BELANJA, RUMAH, LAINNYA
}






@Composable
fun AkunScreen(navController: NavHostController, paddingValues: PaddingValues) {
    Scaffold(
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    //.verticalScroll(ScrollState(1))
            ) {
                UserProfileSection()
                SettingsSection()
                TransactionHistorySection()
            }
        }
    )
}

@Composable
fun UserProfileSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp)
            .background(color = Color.LightGray)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            shape = CircleShape,
            elevation = 4.dp,
            modifier = Modifier.size(100.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Gray)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Nama Pengguna", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text(text = "user@example.com", fontSize = 16.sp)
        Spacer(modifier = Modifier.height(16.dp))

    }
}

@Composable
fun SettingsSection() {
    Column(modifier = Modifier.padding(16.dp)) {
        SettingItem(title = "Ubah Profil")
        SettingItem(title = "Keamanan dan Privasi")
        SettingItem(title = "Notifikasi")
        SettingItem(title = "Bahasa")
    }
}

@Composable
fun SettingItem(title: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        //.clickable { navController.navigate(route) }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, fontSize = 16.sp)
            //Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null)
        }
    }
}

@Composable
fun TransactionHistorySection() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Riwayat Transaksi", style = MaterialTheme.typography.h6)
        Spacer(modifier = Modifier.height(8.dp))

        TransactionItem(description = "Pembelian di Tokopedia", amount = "-Rp 200.000", date = "25 Mei 2024")
        TransactionItem(description = "Gaji Bulanan", amount = "+Rp 5.000.000", date = "30 Mei 2024")
    }
    Spacer(modifier = Modifier.height(48.dp))

}

@Composable
fun TransactionItem(description: String, amount: String, date: String) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column (
            modifier = Modifier
                .padding(8.dp)
        ){
            Text(text = description, fontWeight = FontWeight.Bold)
            Text(text = amount, color = if (amount.startsWith("-")) Color.Red else Color.Green)
            Text(text = date, fontSize = 12.sp)
        }
    }

}

@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

enum class BottomNavItem(val route: String, val icon: ImageVector, val title: String) {
    Dompet("dompet", Icons.Filled.Home, "Dompet"),
    Plan("plan", Icons.Filled.List, "Plan"),
    Akun("akun", Icons.Filled.Person, "Akun")
}
@Composable
fun DompetScreen(navController: NavHostController) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedAction by remember { mutableStateOf<String?>(null) }

    val choices = listOf("Input Pemasukan", "Input Pengeluaran")


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End
    ) {
        FloatingActionButton(
            onClick = { showDialog = true },
            shape = CircleShape,
            modifier = Modifier
                .padding(16.dp)
                .padding(end = 30.dp)
                .padding(bottom = 70.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Record",
                modifier = Modifier.size(24.dp)
            )
        }
    }
    Column(
        modifier = Modifier
        //.padding(paddingValues)
        //.padding(horizontal = 16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.mytipsaturkeuangan),
            contentDescription = "Example Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clickable {
                    navController.navigate("webview")
                }
        )

        Box( // Wrap FinanceCard with Box
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth()
        ) {
            FinanceCard(
                financialPlan = "Rencana Pengeluaran: Rp 4.000.000",
                income = "Pemasukan: Rp 5.000.000",
                expenses = "Pengeluaran: Rp 3.000.000"
            )
        }

        Scaffold(
            content = { innerPadding ->
                Column(
                    modifier = Modifier.padding(innerPadding) // Apply the innerPadding provided by Scaffold
                ) {
                    Text(text = "Rekap Keuangan Bulanan", style = MaterialTheme.typography.body1, modifier = Modifier.padding(16.dp))
                    FinanceChart(
                        financialData = listOf(
                            40f, 45f, 30f, 50f, 48f, 47f, 50f, 53f, 49f, 51f, 15f, 48f
                        ),
                        months = listOf(
                            "Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des"
                        ),
                    )
                }
            }
        )

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = {
                    Text("Pilih Tindakan")
                },
                text = {
                    Column {
                        choices.forEach { choice ->
                            Button(
                                onClick = {
                                    showDialog = false
                                    selectedAction = choice
                                },
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth()
                            ) {
                                Text(choice)
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showDialog = false },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text("Batal")
                    }
                }
            )
        }

        selectedAction?.let { action ->
            when (action) {
                "Input Pemasukan" -> showIncomeDialog(navController)
                "Input Pengeluaran" -> showExpenseDialog(navController)
            }
        }
    }
}


@Composable
fun showIncomeDialog(navController: NavHostController) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(true) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text("Input Pemasukan")
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Tittle") }
                    )
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Jumlah (Rp)") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Lakukan sesuatu dengan data pemasukan
                        if (title.isNotEmpty() && amount.isNotEmpty()) {
                            //viewModel.addIncome(title, amount)
                            showDialog = false
                        }
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Tambahkan")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false }
                ) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun showExpenseDialog(navController: NavHostController) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(true) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text("Input Pengeluaran")
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Tittle") }
                    )
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Jumlah (Rp)") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Lakukan sesuatu dengan data pengeluaran
                        if (title.isNotEmpty() && amount.isNotEmpty()) {
                            // Lakukan sesuatu dengan data pengeluaran
                            showDialog = false
                        }
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Tambahkan")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false }
                ) {
                    Text("Batal")
                }
            }
        )
    }
}





@Composable
fun FinanceChart(
    financialData: List<Float>,
    months: List<String>,
    height: Dp = 300.dp // Default height if not specified
) {
    val maxValue = financialData.maxOrNull() ?: 0f

    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(200.dp) // Use the height parameter here
        .padding(16.dp)
        .background(Color.LightGray, shape = RoundedCornerShape(8.dp))) {
        val barWidth = (size.width - 32.dp.toPx()) / financialData.size
        val chartHeight = size.height - 64.dp.toPx()
        val topY = 16.dp.toPx()
        val bottomY = topY + chartHeight

        val paint = Paint().asFrameworkPaint().apply {
            isAntiAlias = true
            textSize = 14.sp.toPx()
            color = android.graphics.Color.BLACK
            textAlign = android.graphics.Paint.Align.CENTER
        }

        financialData.forEachIndexed { index, value ->
            val barHeight = (value / maxValue) * chartHeight
            val leftX = 16.dp.toPx() + index * barWidth
            val centerX = leftX + barWidth / 2

            drawRoundRect(
                color = Color.Magenta,
                topLeft = androidx.compose.ui.geometry.Offset(leftX, bottomY - barHeight),
                size = androidx.compose.ui.geometry.Size(barWidth - 8.dp.toPx(), barHeight),
                cornerRadius = CornerRadius(4.dp.toPx())
            )

            drawContext.canvas.nativeCanvas.drawText(
                months[index],
                centerX,
                bottomY + 16.dp.toPx(),
                paint
            )
        }
    }
}
@Composable
fun WebViewScreen(url: String) {
    val state = rememberWebViewState(url = url)
    WebView(
        state = state,
        modifier = Modifier.fillMaxSize(),
        onCreated = { webView ->
            webView.webViewClient = WebViewClient()
        }
    )
}


@Composable
fun FinanceCard(financialPlan: String, income: String, expenses: String) {
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = financialPlan, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = income, fontWeight = FontWeight.Normal)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = expenses, fontWeight = FontWeight.Normal)
        }
    }
}

//pie chart




@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FiplaAppTheme {
        MainScreen()
    }
}
