package com.deaenita.fiplaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.background
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable



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
    NavHost(navController, startDestination = BottomNavItem.Dompet.route) {
        composable(BottomNavItem.Dompet.route) { DompetScreen(navController, paddingValues) }
        composable(BottomNavItem.Plan.route) { PlanScreen(navController, paddingValues) }
        composable(BottomNavItem.Akun.route) { AkunScreen(navController, paddingValues) }
    }
}


@Composable
fun PlanScreen(navController: NavHostController, paddingValues: PaddingValues) {
    Box(modifier = Modifier.padding(paddingValues)) {

        var showDialog by remember { mutableStateOf(false) }
        var selectedCategory by remember { mutableStateOf(Category.JAJAN) }
        var amount by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }

        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ){
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
                    modifier = Modifier.size(24.dp))
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
                            // Lakukan sesuatu dengan data pengeluaran
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
    }
}

@Composable
fun CategorySelector(selectedCategory: Category, onCategorySelected: (Category) -> Unit) {
    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Category.values().forEach { category ->
            Button(
                onClick = { onCategorySelected(category) },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (selectedCategory == category) MaterialTheme.colors.primary else MaterialTheme.colors.surface
                ),
                modifier = Modifier.wrapContentWidth()
            ) {
                Text(category.displayName)
            }
        }
    }
}


enum class Category(val displayName: String) {
    JAJAN("Internet"),
    BELANJA("Belanja"),
    LISTRIK("Listrik"),
}



@Composable
fun AkunScreen(navController: NavHostController, paddingValues: PaddingValues) {
    Scaffold(
        content = { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding) // Apply the innerPadding provided by Scaffold
            ) {
                Text(text = "Rekap Keuangan", style = MaterialTheme.typography.h6, modifier = Modifier.padding(16.dp))
                FinanceChart(
                    financialData = listOf(
                        4000f, 4500f, 3000f, 5000f, 4800f, 4700f, 5000f, 5300f, 4900f, 5100f, 5200f, 4800f
                    ),
                    months = listOf(
                        "Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des"
                    ),
                )
            }
        }
    )
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
fun DompetScreen(navController: NavHostController, paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(horizontal = 16.dp)
    ) {
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
    }
}

@Composable
fun FinanceChart(financialData: List<Float>, months: List<String>
) {
    val maxValue = financialData.maxOrNull() ?: 0f

    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(300.dp)
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

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FiplaAppTheme {
        MainScreen()
    }
}
