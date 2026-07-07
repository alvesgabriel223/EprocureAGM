package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.DocumentEntity
import com.example.data.entity.MessageEntity
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EProcureApp(viewModel: MainViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    if (currentUser == null) {
        OnboardingScreen(
            isLoading = isLoading,
            onRegister = { name, email, phone ->
                viewModel.registerOrLogin(name, email, phone)
            }
        )
    } else {
        Scaffold(
            bottomBar = {
                EProcureBottomNavigation(
                    currentTab = currentTab,
                    onTabSelected = { tab -> viewModel.setTab(tab) }
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                when (currentTab) {
                    "DASHBOARD" -> DashboardScreen(viewModel = viewModel)
                    "LOST" -> RegisterDocumentScreen(viewModel = viewModel, isLost = true)
                    "FOUND" -> RegisterDocumentScreen(viewModel = viewModel, isLost = false)
                    "MATCHES" -> MatchesScreen(viewModel = viewModel)
                    "MAP" -> MapScreen(viewModel = viewModel)
                    "CHAT" -> ChatRoomScreen(viewModel = viewModel)
                    "ABOUT" -> AboutScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun EProcureBottomNavigation(
    currentTab: String,
    onTabSelected: (String) -> Unit
) {
    val borderGradient = androidx.compose.ui.graphics.Brush.horizontalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f)
        )
    )
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("bottom_nav"),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.90f),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderGradient),
        shadowElevation = 12.dp
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            modifier = Modifier.height(72.dp)
        ) {
            NavigationBarItem(
                selected = currentTab == "DASHBOARD",
                onClick = { onTabSelected("DASHBOARD") },
                icon = { 
                    Icon(
                        imageVector = Icons.Filled.Dashboard, 
                        contentDescription = "Painel",
                        tint = if (currentTab == "DASHBOARD") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    ) 
                },
                label = { 
                    Text(
                        "Painel", 
                        fontSize = 11.sp, 
                        fontWeight = if (currentTab == "DASHBOARD") FontWeight.Bold else FontWeight.Normal,
                        color = if (currentTab == "DASHBOARD") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    ) 
                }
            )
            NavigationBarItem(
                selected = currentTab == "MATCHES",
                onClick = { onTabSelected("MATCHES") },
                icon = { 
                    Icon(
                        imageVector = Icons.Filled.Security, 
                        contentDescription = "Matches",
                        tint = if (currentTab == "MATCHES") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    ) 
                },
                label = { 
                    Text(
                        "Matches", 
                        fontSize = 11.sp, 
                        fontWeight = if (currentTab == "MATCHES") FontWeight.Bold else FontWeight.Normal,
                        color = if (currentTab == "MATCHES") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    ) 
                }
            )
            NavigationBarItem(
                selected = currentTab == "MAP",
                onClick = { onTabSelected("MAP") },
                icon = { 
                    Icon(
                        imageVector = Icons.Filled.Map, 
                        contentDescription = "Mapa",
                        tint = if (currentTab == "MAP") MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    ) 
                },
                label = { 
                    Text(
                        "Mapa", 
                        fontSize = 11.sp, 
                        fontWeight = if (currentTab == "MAP") FontWeight.Bold else FontWeight.Normal,
                        color = if (currentTab == "MAP") MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    ) 
                }
            )
            NavigationBarItem(
                selected = currentTab == "ABOUT",
                onClick = { onTabSelected("ABOUT") },
                icon = { 
                    Icon(
                        imageVector = Icons.Filled.Info, 
                        contentDescription = "Sobre",
                        tint = if (currentTab == "ABOUT") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    ) 
                },
                label = { 
                    Text(
                        "Sobre", 
                        fontSize = 11.sp, 
                        fontWeight = if (currentTab == "ABOUT") FontWeight.Bold else FontWeight.Normal,
                        color = if (currentTab == "ABOUT") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    ) 
                }
            )
        }
    }
}

// --- ONBOARDING SCREEN ---
@Composable
fun OnboardingScreen(
    isLoading: Boolean,
    onRegister: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var hasError by remember { mutableStateOf(false) }

    val backgroundBrush = androidx.compose.ui.graphics.Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .padding(24.dp)
    ) {
        // High-tech Cyber Sonic/Sonar grid canvas background decoration
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            
            // Subtle digital scan grid
            val gridSpacing = 80f
            val gridColor = Color(0x0A00E5FF)
            for (x in 0..(w / gridSpacing).toInt()) {
                drawLine(
                    color = gridColor,
                    start = Offset(x * gridSpacing, 0f),
                    end = Offset(x * gridSpacing, h),
                    strokeWidth = 1f
                )
            }
            for (y in 0..(h / gridSpacing).toInt()) {
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y * gridSpacing),
                    end = Offset(w, y * gridSpacing),
                    strokeWidth = 1f
                )
            }

            // Radar/Sonar scanning circles representing the smart search algorithms
            drawCircle(
                color = Color(0x12BD00FF),
                radius = w * 0.35f,
                center = Offset(w * 0.5f, h * 0.25f),
                style = Stroke(width = 1.5f)
            )
            drawCircle(
                color = Color(0x1800E5FF),
                radius = w * 0.5f,
                center = Offset(w * 0.5f, h * 0.25f),
                style = Stroke(width = 2.5f)
            )
            drawCircle(
                color = Color(0x0B00FFCC),
                radius = w * 0.7f,
                center = Offset(w * 0.5f, h * 0.25f),
                style = Stroke(width = 1f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Futuristic App Logo with holographic gradient neon ring
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(140.dp)
                    .animateContentSize()
            ) {
                // Outer glowing cyber ring
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(
                            width = 2.dp,
                            brush = androidx.compose.ui.graphics.Brush.sweepGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary,
                                    MaterialTheme.colorScheme.tertiary,
                                    MaterialTheme.colorScheme.primary
                                )
                            ),
                            shape = CircleShape
                        )
                )
                
                Image(
                    painter = painterResource(id = com.example.R.drawable.img_logo),
                    contentDescription = "EProcure Logo",
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentScale = ContentScale.Crop
                )
            }

            Text(
                text = "EProcure",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                letterSpacing = 2.sp
            )

            Text(
                text = "\"Perdeu um documento? O EProcure ajuda você a encontrá-lo.\"",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Glassmorphic Registration/Login Card with futuristic neon border
            val borderGradient = androidx.compose.ui.graphics.Brush.linearGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.secondary
                )
            )

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        brush = borderGradient,
                        shape = RoundedCornerShape(24.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Iniciar Sessão / Cadastro",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nome Completo") },
                        leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("onboarding_name"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("E-mail") },
                        leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("onboarding_email"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Telefone / Contacto") },
                        leadingIcon = { Icon(Icons.Filled.Phone, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("onboarding_phone"),
                        singleLine = true
                    )

                    if (hasError) {
                        Text(
                            text = "Por favor preencha todos os campos correctamente.",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = {
                            if (name.isNotBlank() && email.isNotBlank() && phone.isNotBlank()) {
                                onRegister(name, email, phone)
                            } else {
                                hasError = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("login_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Entrar de Graça", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Simulated Google Sign in Option
                    OutlinedButton(
                        onClick = {
                            onRegister("Alves Marizane", "alves@gmail.com", "+258 84 123 4567")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Filled.AccountCircle, contentDescription = null)
                            Text("Simular Login com Conta Google")
                        }
                    }
                }
            }
        }

        // Developer footer
        Text(
            text = "Desenvolvido por Alves Marizane | EProcure © 2026",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
        )
    }
}

// --- DASHBOARD / HOME SCREEN ---
@Composable
fun DashboardScreen(viewModel: MainViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val documents by viewModel.allDocuments.collectAsStateWithLifecycle()
    val matches by viewModel.matches.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    var activeDocFilterTab by remember { mutableStateOf("ALL") } // ALL, LOST, FOUND

    val filteredDocs = remember(documents, searchQuery, activeDocFilterTab) {
        documents.filter { doc ->
            val matchesQuery = doc.ownerName.contains(searchQuery, ignoreCase = true) ||
                    doc.type.contains(searchQuery, ignoreCase = true) ||
                    doc.location.contains(searchQuery, ignoreCase = true)
            val matchesTab = when (activeDocFilterTab) {
                "LOST" -> doc.status == "LOST"
                "FOUND" -> doc.status == "FOUND"
                else -> true
            }
            matchesQuery && matchesTab
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcoming User Banner
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Olá, ${currentUser?.name ?: "Utilizador"}!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color(0xFF00FFCC), CircleShape) // Neon Emerald Active pulse
                        )
                        Text(
                            text = "Sistema Inteligente Online",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00FFCC)
                        )
                    }
                }

                IconButton(
                    onClick = { viewModel.logout() },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(Icons.Filled.ExitToApp, contentDescription = "Sair")
                }
            }
        }

        // Two Main Action Cards side-by-side with futuristic neon glow borders
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.setTab("LOST") }
                        .testTag("lost_card_btn")
                        .border(
                            width = 1.dp,
                            brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                colors = listOf(MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.error.copy(alpha = 0.2f))
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(MaterialTheme.colorScheme.error.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "Perdi um\nDocumento",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.setTab("FOUND") }
                        .testTag("found_card_btn")
                        .border(
                            width = 1.dp,
                            brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "Encontrei um\nDocumento",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Quick Matches alert banner
        val activeMatches = matches.filter { !it.first.isDelivered && !it.second.isDelivered }
        if (activeMatches.isNotEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.setTab("MATCHES") }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Filled.Security,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Novas Correspondências!",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Detectamos ${activeMatches.size} correspondência(s) automática(s) para os seus documentos.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                            )
                        }
                        Icon(Icons.Filled.ArrowForward, contentDescription = null)
                    }
                }
            }
        }

        // Document Feed & Filters
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Registos Recentes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Pesquisar documento, titular, local...") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                // Horizontal filters row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FilterChip(
                        selected = activeDocFilterTab == "ALL",
                        onClick = { activeDocFilterTab = "ALL" },
                        label = { Text("Todos") }
                    )
                    FilterChip(
                        selected = activeDocFilterTab == "LOST",
                        onClick = { activeDocFilterTab = "LOST" },
                        label = { Text("Perdidos") }
                    )
                    FilterChip(
                        selected = activeDocFilterTab == "FOUND",
                        onClick = { activeDocFilterTab = "FOUND" },
                        label = { Text("Encontrados") }
                    )
                }
            }
        }

        // List of Documents
        if (filteredDocs.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Filled.FindInPage,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Nenhum documento encontrado",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Pesquise com outros termos ou registe um documento.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(filteredDocs) { doc ->
                DocumentItemCard(doc = doc, onClick = { viewModel.selectDocument(doc) })
            }
        }
    }

    // Modal Details overlay if a document is selected
    val selectedDoc by viewModel.selectedDocument.collectAsStateWithLifecycle()
    if (selectedDoc != null) {
        DocumentDetailDialog(
            doc = selectedDoc!!,
            onClose = { viewModel.selectDocument(null) },
            onConfirmDelivery = { id -> viewModel.confirmDelivery(id) }
        )
    }
}

@Composable
fun DocumentItemCard(doc: DocumentEntity, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Document Type Visual Icon
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (doc.status == "LOST") Color(0xFFFFDAD6) else Color(0xFFD1E5F4)
                ),
                modifier = Modifier.size(50.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = when (doc.type) {
                            "Bilhete de Identidade" -> Icons.Filled.AssignmentInd
                            "Passaporte" -> Icons.Filled.Public
                            "Cartão Bancário" -> Icons.Filled.CreditCard
                            "Carta de Condução" -> Icons.Filled.DriveEta
                            "Cartão de Seguro" -> Icons.Filled.Healing
                            else -> Icons.Filled.FilePresent
                        },
                        contentDescription = null,
                        tint = if (doc.status == "LOST") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = doc.type,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (doc.isDelivered) {
                                Color(0xFFD1FADF)
                            } else if (doc.status == "LOST") {
                                Color(0xFFFFDAD6)
                            } else {
                                Color(0xFFD1E5F4)
                            }
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (doc.isDelivered) "Entregue" else if (doc.status == "LOST") "Perdido" else "Encontrado",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (doc.isDelivered) {
                                Color(0xFF027948)
                            } else if (doc.status == "LOST") {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.primary
                            },
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = "Titular: ${doc.ownerName}",
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = doc.location,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
        }
    }
}

// --- DOCUMENT REGISTRATION SCREENS ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterDocumentScreen(viewModel: MainViewModel, isLost: Boolean) {
    var docType by remember { mutableStateOf("Bilhete de Identidade") }
    var ownerName by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var contactPhone by remember { mutableStateOf("") }
    var contactEmail by remember { mutableStateOf("") }
    var photoSimulated by remember { mutableStateOf(false) }

    val docTypes = listOf(
        "Bilhete de Identidade",
        "Passaporte",
        "Cartão de Eleitor",
        "Cartão Bancário",
        "Carta de Condução",
        "Cartão de Estudante",
        "Cartão de Trabalho",
        "Cartão de Seguro",
        "Outros"
    )

    var expandedTypeMenu by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(onClick = { viewModel.setTab("DASHBOARD") }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
                }
                Text(
                    text = if (isLost) "Registar Documento Perdido" else "Registar Documento Encontrado",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Document Type Dropdown
                    ExposedDropdownMenuBox(
                        expanded = expandedTypeMenu,
                        onExpandedChange = { expandedTypeMenu = !expandedTypeMenu }
                    ) {
                        OutlinedTextField(
                            value = docType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Tipo de Documento") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTypeMenu) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .testTag("doc_type_dropdown")
                        )
                        ExposedDropdownMenu(
                            expanded = expandedTypeMenu,
                            onDismissRequest = { expandedTypeMenu = false }
                        ) {
                            docTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type) },
                                    onClick = {
                                        docType = type
                                        expandedTypeMenu = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = ownerName,
                        onValueChange = { ownerName = it },
                        label = { Text(if (isLost) "Nome do Titular (no documento)" else "Nome Visível (se legível)") },
                        leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("doc_owner"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text(if (isLost) "Onde foi perdido aproximadamente?" else "Onde foi encontrado?") },
                        leadingIcon = { Icon(Icons.Filled.LocationOn, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("doc_location"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("Data Aproximada") },
                        placeholder = { Text("Ex: 05/07/2026") },
                        leadingIcon = { Icon(Icons.Filled.DateRange, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("doc_date"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Descrição adicional / Detalhes") },
                        placeholder = { Text(if (isLost) "Ex: Estava numa capa azul com fotos" else "Ex: Encontrado perto da paragem de autocarro, ocultando dados sensíveis.") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .testTag("doc_desc"),
                        maxLines = 4
                    )

                    Divider()

                    Text(
                        "Informações de Contacto",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = contactPhone,
                        onValueChange = { contactPhone = it },
                        label = { Text("Telefone de contacto") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("doc_phone"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = contactEmail,
                        onValueChange = { contactEmail = it },
                        label = { Text("Email de contacto") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("doc_email"),
                        singleLine = true
                    )

                    // Optional Photo simulation
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { photoSimulated = !photoSimulated },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                if (photoSimulated) Icons.Filled.CheckCircle else Icons.Filled.AddAPhoto,
                                contentDescription = null,
                                tint = if (photoSimulated) Color(0xFF027948) else MaterialTheme.colorScheme.primary
                            )
                            Text(
                                if (photoSimulated) "Foto Simulado Anexado com Sucesso!" else "Simular Anexação de Foto (Opcional)",
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp,
                                color = if (photoSimulated) Color(0xFF027948) else MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Button(
                        onClick = {
                            if (ownerName.isNotBlank() && location.isNotBlank()) {
                                viewModel.registerDocument(
                                    type = docType,
                                    ownerName = ownerName,
                                    status = if (isLost) "LOST" else "FOUND",
                                    location = location,
                                    date = date,
                                    description = description,
                                    contactPhone = contactPhone,
                                    contactEmail = contactEmail,
                                    onSuccess = {
                                        // Reset
                                    }
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("register_doc_btn")
                    ) {
                        Text(
                            text = if (isLost) "Declarar Perda de Documento" else "Declarar Documento Encontrado",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}

// --- DOCUMENT DETAILS DIALOG ---
@Composable
fun DocumentDetailDialog(
    doc: DocumentEntity,
    onClose: () -> Unit,
    onConfirmDelivery: (Int) -> Unit
) {
    Dialog(onDismissRequest = onClose) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Detalhes do Documento",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onClose) {
                        Icon(Icons.Filled.Close, contentDescription = "Fechar")
                    }
                }

                // Document Banner Preview
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (doc.status == "LOST") Color(0xFFFFDAD6) else Color(0xFFD1E5F4)
                    )
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Filled.AssignmentInd,
                                contentDescription = null,
                                modifier = Modifier.size(36.dp),
                                tint = if (doc.status == "LOST") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            )
                            Text(
                                doc.type,
                                fontWeight = FontWeight.Bold,
                                color = if (doc.status == "LOST") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DetailRow(label = "Titular", value = doc.ownerName)
                    DetailRow(label = "Estado", value = if (doc.isDelivered) "Devolvido com Sucesso ✅" else if (doc.status == "LOST") "Perdido" else "Encontrado")
                    DetailRow(label = "Local", value = doc.location)
                    DetailRow(label = "Data Aproximada", value = doc.date)
                    DetailRow(label = "Contacto", value = doc.contactPhone)
                    DetailRow(label = "E-mail", value = doc.contactEmail)
                    DetailRow(label = "Descrição", value = doc.description.ifBlank { "Sem detalhes adicionais." })
                }

                if (!doc.isDelivered) {
                    Button(
                        onClick = {
                            onConfirmDelivery(doc.id)
                            onClose()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF027948)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.DoneAll, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                        Text("Confirmar Devolução / Recebimento")
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
            modifier = Modifier.widthIn(max = 200.dp),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// --- MATCHES / CORRESPONDENCIAS SCREEN ---
@Composable
fun MatchesScreen(viewModel: MainViewModel) {
    val matches by viewModel.matches.collectAsStateWithLifecycle()
    val selectedMatch by viewModel.selectedMatch.collectAsStateWithLifecycle()

    if (selectedMatch == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Correspondências Inteligentes",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                "O nosso sistema inteligente procura e cruza automaticamente informações de documentos perdidos com achados.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

            val activeMatches = matches.filter { !it.first.isDelivered && !it.second.isDelivered }

            if (activeMatches.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            Icons.Filled.Security,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                        Text(
                            "Nenhuma correspondência activa",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Continuamos a monitorizar e comparar em segundo plano. Assim que houver um cruzamento de dados, será notificado imediatamente!",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(activeMatches) { match ->
                        MatchItemCard(match = match, onClick = { viewModel.selectMatch(match) })
                    }
                }
            }
        }
    } else {
        // Detailed view of match side-by-side
        MatchDetailScreen(
            match = selectedMatch!!,
            onBack = { viewModel.selectMatch(null) },
            onStartChat = { viewModel.openChat(selectedMatch!!) },
            onConfirmDelivery = { id -> viewModel.confirmDelivery(id) }
        )
    }
}

@Composable
fun MatchItemCard(match: Pair<DocumentEntity, DocumentEntity>, onClick: () -> Unit) {
    val lost = match.first
    val found = match.second

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        lost.type,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(100.dp)
                ) {
                    Text(
                        "98% Confiança",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Divider(color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.1f))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Perdido por", fontSize = 11.sp, color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.6f))
                    Text(lost.ownerName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onTertiaryContainer)
                    Text(lost.location, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                        .align(Alignment.CenterVertically),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Sync,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("Encontrado em", fontSize = 11.sp, color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.6f))
                    Text(found.ownerName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onTertiaryContainer)
                    Text(found.location, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }

            Text(
                "Toque para ver detalhes da entrega e abrir Chat Seguro.",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
fun MatchDetailScreen(
    match: Pair<DocumentEntity, DocumentEntity>,
    onBack: () -> Unit,
    onStartChat: () -> Unit,
    onConfirmDelivery: (Int) -> Unit
) {
    val lost = match.first
    val found = match.second

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
                }
                Text("Cruzamento de Dados", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Side-by-Side Detail Cards
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        "Comparação de Dados",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Lost Card representation
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFDAD6))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("DECLARAÇÃO DE PERDA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Titular: ${lost.ownerName}", fontWeight = FontWeight.Bold)
                            Text("Local indicado: ${lost.location}", fontSize = 12.sp)
                            Text("Data aproximada: ${lost.date}", fontSize = 12.sp)
                        }
                    }

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF027948),
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    // Found Card representation
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFD1E5F4))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("DECLARAÇÃO DE ENCONTRO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Nome visível: ${found.ownerName}", fontWeight = FontWeight.Bold)
                            Text("Local encontrado: ${found.location}", fontSize = 12.sp)
                            Text("Data: ${found.date}", fontSize = 12.sp)
                        }
                    }

                    Divider()

                    // Security recommendation
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Filled.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Column {
                                Text("Aviso de Segurança", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(
                                    "Para sua segurança, marque sempre a entrega num local público movimentado. Nunca divulgue detalhes sensíveis da sua morada.",
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onStartChat,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("start_chat_btn")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.padding(end = 6.dp))
                            Text("Chat Seguro")
                        }

                        Button(
                            onClick = { onConfirmDelivery(lost.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF027948)),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                        ) {
                            Text("Confirmar Entrega")
                        }
                    }
                }
            }
        }
    }
}

// --- SECURE CHAT ROOM SCREEN ---
@Composable
fun ChatRoomScreen(viewModel: MainViewModel) {
    val messages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val opponentName by viewModel.activeChatOpponentName.collectAsStateWithLifecycle()
    val docType by viewModel.activeChatDocType.collectAsStateWithLifecycle()
    val docOwner by viewModel.activeChatDocOwner.collectAsStateWithLifecycle()

    var textState by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Chat Header
        Card(
            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = { viewModel.setTab("MATCHES") },
                    colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.onPrimary)
                ) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
                }

                Card(
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = opponentName.take(1).uppercase(),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 18.sp
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = opponentName,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Combinar entrega: $docType",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                }

                // Call attention to verification
                IconButton(
                    onClick = { viewModel.setTab("MAP") },
                    colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.onPrimary)
                ) {
                    Icon(Icons.Filled.Map, contentDescription = "Ver no Mapa")
                }
            }
        }

        // Messages Flow
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "🔒 Chat Seguro Activo: Os dados de contacto telefónico e moradas estão ocultos. Combine a entrega num ponto público para total proteção de dados.",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(10.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            items(messages) { msg ->
                val isMe = msg.senderId != "opponent"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                ) {
                    Card(
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isMe) 16.dp else 0.dp,
                            bottomEnd = if (isMe) 0.dp else 16.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            if (!isMe) {
                                Text(
                                    msg.senderName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                            }
                            Text(
                                msg.content,
                                fontSize = 14.sp,
                                color = if (isMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Typing feedback indicator simulation
            if (messages.isNotEmpty() && messages.last().senderId != "opponent") {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(start = 12.dp)
                    ) {
                        Card(
                            shape = CircleShape,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.size(8.dp)
                        ) {}
                        Text(
                            "$opponentName está a escrever...",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }

        // Bottom Input Row
        Card(
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = textState,
                    onValueChange = { textState = it },
                    placeholder = { Text("Escreva uma mensagem segura...") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_text"),
                    shape = RoundedCornerShape(100.dp),
                    singleLine = true
                )

                IconButton(
                    onClick = {
                        if (textState.isNotBlank()) {
                            viewModel.sendMessage(textState)
                            textState = ""
                            keyboardController?.hide()
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.testTag("chat_send_btn")
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar")
                }
            }
        }
    }
}

// --- INTERACTIVE MAP SCREEN ---
@Composable
fun MapScreen(viewModel: MainViewModel) {
    val documents by viewModel.allDocuments.collectAsStateWithLifecycle()
    val matches by viewModel.matches.collectAsStateWithLifecycle()
    val activeMatches = matches.filter { !it.first.isDelivered && !it.second.isDelivered }

    var selectedDocOnMap by remember { mutableStateOf<DocumentEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Mapa de Documentos",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Exibição visual aproximada de onde os documentos foram perdidos ou encontrados em Maputo.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        // Custom simulated canvas map
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFFE5F1F7), RoundedCornerShape(20.dp))
                .pointerInput(documents) {
                    // Quick gesture check to show pins
                }
        ) {
            val primaryColor = MaterialTheme.colorScheme.primary
            val errorColor = MaterialTheme.colorScheme.error

            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Draw Water body (Maputo Bay / Coast) at bottom-right corner
                drawCircle(
                    color = Color(0xFFAFD5FA),
                    radius = w * 0.4f,
                    center = Offset(w, h)
                )

                // Draw roads (Av. Eduardo Mondlane, Av. 24 de Julho)
                // Av. Eduardo Mondlane (Diagonal)
                drawLine(
                    color = Color.White,
                    start = Offset(0f, h * 0.4f),
                    end = Offset(w, h * 0.4f),
                    strokeWidth = 24f
                )
                drawLine(
                    color = Color(0xFFD0D7DE),
                    start = Offset(0f, h * 0.4f),
                    end = Offset(w, h * 0.4f),
                    strokeWidth = 2f
                )

                // Av. 24 de Julho
                drawLine(
                    color = Color.White,
                    start = Offset(0f, h * 0.6f),
                    end = Offset(w, h * 0.6f),
                    strokeWidth = 24f
                )

                // Karl Marx Avenue (Vertical)
                drawLine(
                    color = Color.White,
                    start = Offset(w * 0.3f, 0f),
                    end = Offset(w * 0.3f, h),
                    strokeWidth = 24f
                )

                // Draw a beautiful green park block (Parque dos Continuadores)
                drawRect(
                    color = Color(0xFFD4ECD5),
                    topLeft = Offset(w * 0.45f, h * 0.15f),
                    size = androidx.compose.ui.geometry.Size(w * 0.25f, h * 0.18f)
                )

                // Draw connecting dotted line for active matches to visualize the retrieval route!
                activeMatches.forEach { match ->
                    val lostDoc = match.first
                    val foundDoc = match.second

                    // Map coordinates to pixel offset
                    val lostX = w * 0.5f + (lostDoc.lng - 32.57).toFloat() * w * 4f
                    val lostY = h * 0.5f - (lostDoc.lat + 25.95).toFloat() * h * 4f

                    val foundX = w * 0.5f + (foundDoc.lng - 32.57).toFloat() * w * 4f
                    val foundY = h * 0.5f - (foundDoc.lat + 25.95).toFloat() * h * 4f

                    // Dotted line
                    drawLine(
                        color = primaryColor,
                        start = Offset(lostX, lostY),
                        end = Offset(foundX, foundY),
                        strokeWidth = 4f,
                        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f)
                    )
                }
            }

            // Overlay markers using absolutely positioned Box/Icon elements
            documents.forEach { doc ->
                // Map GPS coordinates mock center to map
                // Maputo center around: lat = -25.95, lng = 32.57
                var mapX = 150f
                var mapY = 250f
                
                if (doc.lng != 0.0) {
                    // Scale offset dynamically
                    mapX = 100f + ((doc.lng - 32.48) / 0.15).toFloat() * 500f
                    mapY = 100f + (((doc.lat + 25.90) / -0.1).toFloat() * 400f)
                }

                Box(
                    modifier = Modifier
                        .offset(x = mapX.dp, y = mapY.dp)
                        .size(36.dp)
                        .background(
                            color = if (doc.status == "LOST") Color(0xFFFFDAD6) else Color(0xFFD1E5F4),
                            shape = CircleShape
                        )
                        .clickable { selectedDocOnMap = doc },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (doc.status == "LOST") Icons.Filled.Warning else Icons.Filled.Check,
                        contentDescription = null,
                        tint = if (doc.status == "LOST") errorColor else primaryColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Selected Pin Detail overlay
        if (selectedDocOnMap != null) {
            val doc = selectedDocOnMap!!
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(doc.type, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text("Titular: ${doc.ownerName}", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                        Text("Local registado: ${doc.location}", fontSize = 12.sp, color = Color.Gray)
                    }
                    IconButton(onClick = { selectedDocOnMap = null }) {
                        Icon(Icons.Filled.Close, contentDescription = "Fechar")
                    }
                }
            }
        }
    }
}

// --- ABOUT & DEVELOPER ATTRIBUTION SCREEN ---
@Composable
fun AboutScreen(viewModel: MainViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            // Futuristic App Logo with holographic gradient neon ring
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(140.dp)
                    .animateContentSize()
            ) {
                // Outer glowing cyber ring
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(
                            width = 2.dp,
                            brush = androidx.compose.ui.graphics.Brush.sweepGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary,
                                    MaterialTheme.colorScheme.tertiary,
                                    MaterialTheme.colorScheme.primary
                                )
                            ),
                            shape = CircleShape
                        )
                )
                
                Image(
                    painter = painterResource(id = com.example.R.drawable.img_logo),
                    contentDescription = "EProcure Logo",
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentScale = ContentScale.Crop
                )
            }
        }

        item {
            Text(
                "EProcure",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )
            Text(
                "\"Perdeu um documento? O EProcure ajuda você a encontrá-lo.\"",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Objetivo Social",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "O EProcure é um aplicativo móvel que conecta pessoas que perderam documentos com pessoas ou instituições que os encontraram, de forma rápida, segura e totalmente gratuita.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                    Text(
                        "Reduz a perda definitiva de documentos e facilita a devolução aos proprietários, diminuindo custos, tempo e burocracia para os cidadãos.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Developer attribution
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Professional Developer Avatar with holographic gradient neon ring
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(100.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .border(
                                    width = 2.dp,
                                    brush = androidx.compose.ui.graphics.Brush.sweepGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary,
                                            MaterialTheme.colorScheme.secondary,
                                            MaterialTheme.colorScheme.tertiary,
                                            MaterialTheme.colorScheme.primary
                                        )
                                    ),
                                    shape = CircleShape
                                )
                        )
                        
                        Image(
                            painter = painterResource(id = com.example.R.drawable.img_developer_alves),
                            contentDescription = "Alves Marizane",
                            modifier = Modifier
                                .size(92.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Text(
                        "Desenvolvedor do Projeto",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        "Alves Marizane",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        "Desenvolvido com carinho para simplificar a vida dos cidadãos, unindo tecnologia moderna, inteligência artificial (Gemini) e responsabilidade social para reduzir burocracias de documentos perdidos.",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}
