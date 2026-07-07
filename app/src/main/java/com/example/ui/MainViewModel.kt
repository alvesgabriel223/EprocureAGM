package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.entity.DocumentEntity
import com.example.data.entity.MessageEntity
import com.example.data.entity.UserEntity
import com.example.data.repository.EProcureRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = EProcureRepository(
        userDao = database.userDao(),
        documentDao = database.documentDao(),
        messageDao = database.messageDao()
    )

    // --- State Expositions ---
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _currentTab = MutableStateFlow("DASHBOARD") // DASHBOARD, LOST, FOUND, MATCHES, MAP, ABOUT
    val currentTab: StateFlow<String> = _currentTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Loading State
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // All registered documents
    val allDocuments: StateFlow<List<DocumentEntity>> = repository.getAllDocuments()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Calculated matches
    val matches: StateFlow<List<Pair<DocumentEntity, DocumentEntity>>> = allDocuments
        .map { docs -> repository.calculateMatches(docs) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Active Chat State
    private val _activeChatRoomId = MutableStateFlow<String?>(null)
    val activeChatRoomId: StateFlow<String?> = _activeChatRoomId.asStateFlow()

    private val _activeChatOpponentName = MutableStateFlow("")
    val activeChatOpponentName: StateFlow<String> = _activeChatOpponentName.asStateFlow()

    private val _activeChatDocType = MutableStateFlow("")
    val activeChatDocType: StateFlow<String> = _activeChatDocType.asStateFlow()

    private val _activeChatDocOwner = MutableStateFlow("")
    val activeChatDocOwner: StateFlow<String> = _activeChatDocOwner.asStateFlow()

    private val _activeChatIsLost = MutableStateFlow(true)
    val activeChatIsLost: StateFlow<Boolean> = _activeChatIsLost.asStateFlow()

    val chatMessages: StateFlow<List<MessageEntity>> = _activeChatRoomId
        .flatMapLatest { roomId ->
            if (roomId != null) {
                repository.getMessages(roomId)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Selected Match / Document for details
    private val _selectedDocument = MutableStateFlow<DocumentEntity?>(null)
    val selectedDocument: StateFlow<DocumentEntity?> = _selectedDocument.asStateFlow()

    private val _selectedMatch = MutableStateFlow<Pair<DocumentEntity, DocumentEntity>?>(null)
    val selectedMatch: StateFlow<Pair<DocumentEntity, DocumentEntity>?> = _selectedMatch.asStateFlow()

    init {
        seedInitialData()
        // Try to load any existing user to skip onboarding if already registered
        viewModelScope.launch {
            // Simply use a default session if available, or stay on login
            database.userDao().getUserById("alves@gmail.com").collect { user ->
                if (user != null && _currentUser.value == null) {
                    _currentUser.value = user
                }
            }
        }
    }

    private fun seedInitialData() {
        viewModelScope.launch {
            val countFlow = database.documentDao().getAllDocuments()
            val currentList = countFlow.first()
            if (currentList.isEmpty()) {
                // Seed mock documents to make the experience instantly rewarding
                val seedDocs = listOf(
                    DocumentEntity(
                        type = "Bilhete de Identidade",
                        ownerName = "Alves Marizane",
                        status = "LOST",
                        location = "Avenida Eduardo Mondlane, Maputo",
                        date = "01/07/2026",
                        description = "Perdi a minha carteira preta de pele contendo o meu Bilhete de Identidade e cartões bancários.",
                        contactPhone = "+258 84 123 4567",
                        contactEmail = "alves@gmail.com",
                        registeredByUserId = "alves@gmail.com",
                        lat = -25.9653,
                        lng = 32.5892
                    ),
                    DocumentEntity(
                        type = "Bilhete de Identidade",
                        ownerName = "Alves Marizane",
                        status = "FOUND",
                        location = "Shopping 24 de Julho, Maputo",
                        date = "02/07/2026",
                        description = "Encontrei um BI na praça de alimentação com o nome Alves Marizane. Guardei comigo em segurança.",
                        contactPhone = "+258 82 987 6543",
                        contactEmail = "finder.joao@gmail.com",
                        registeredByUserId = "finder.joao@gmail.com",
                        lat = -25.9688,
                        lng = 32.5721
                    ),
                    DocumentEntity(
                        type = "Passaporte",
                        ownerName = "Maria Nhaca",
                        status = "LOST",
                        location = "Aeroporto de Mavalane, Maputo",
                        date = "28/06/2026",
                        description = "Perdi o meu passaporte moçambicano com capa vermelha antes de embarcar.",
                        contactPhone = "+258 85 555 1212",
                        contactEmail = "maria.nhaca@gmail.com",
                        registeredByUserId = "maria.nhaca@gmail.com",
                        lat = -25.9198,
                        lng = 32.5727
                    ),
                    DocumentEntity(
                        type = "Passaporte",
                        ownerName = "Maria Nhaca",
                        status = "FOUND",
                        location = "Aeroporto de Mavalane, Terminal de Chegadas",
                        date = "29/06/2026",
                        description = "Encontrado passaporte de capa vermelha no Terminal 1. Entregue à recepção de segurança.",
                        contactPhone = "+258 84 999 0000",
                        contactEmail = "seguranca.aeroporto@gmail.com",
                        registeredByUserId = "seguranca.aeroporto@gmail.com",
                        lat = -25.9202,
                        lng = 32.5735
                    ),
                    DocumentEntity(
                        type = "Cartão de Eleitor",
                        ownerName = "Celso Mucavel",
                        status = "LOST",
                        location = "Escola Josina Machel, Maputo",
                        date = "05/07/2026",
                        description = "Perdi o cartão de eleitor na sala de voto nº 5.",
                        contactPhone = "+258 87 222 3333",
                        contactEmail = "celso@gmail.com",
                        registeredByUserId = "celso@gmail.com",
                        lat = -25.9566,
                        lng = 32.5788
                    ),
                    DocumentEntity(
                        type = "Carta de Condução",
                        ownerName = "Fernando Goenha",
                        status = "FOUND",
                        location = "Portagem da Matola",
                        date = "03/07/2026",
                        description = "Encontrei uma carta de condução caída junto à portagem da Matola. Nome visível: Fernando Goenha.",
                        contactPhone = "+258 83 444 5555",
                        contactEmail = "portagem.admin@gmail.com",
                        registeredByUserId = "portagem.admin@gmail.com",
                        lat = -25.9322,
                        lng = 32.4988
                    )
                )
                for (doc in seedDocs) {
                    database.documentDao().insertDocument(doc)
                }
            }
        }
    }

    // --- Tab Navigation ---
    fun setTab(tab: String) {
        _currentTab.value = tab
        _selectedDocument.value = null
        _selectedMatch.value = null
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectDocument(doc: DocumentEntity?) {
        _selectedDocument.value = doc
    }

    fun selectMatch(match: Pair<DocumentEntity, DocumentEntity>?) {
        _selectedMatch.value = match
    }

    // --- Authentication Flow ---
    fun registerOrLogin(name: String, email: String, phone: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val user = repository.getOrCreateUser(email, name, phone)
            _currentUser.value = user
            _isLoading.value = false
            _currentTab.value = "DASHBOARD"
        }
    }

    fun logout() {
        viewModelScope.launch {
            _currentUser.value = null
            _currentTab.value = "DASHBOARD"
        }
    }

    // --- Register Document Flow ---
    fun registerDocument(
        type: String,
        ownerName: String,
        status: String,
        location: String,
        date: String,
        description: String,
        contactPhone: String,
        contactEmail: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val userEmail = _currentUser.value?.email ?: "anonimo@eprocure.com"
            
            // Generate some random coordinates near Maputo for the mock map
            val randomLat = -25.95 + (Math.random() - 0.5) * 0.1
            val randomLng = 32.57 + (Math.random() - 0.5) * 0.1

            val newDoc = DocumentEntity(
                type = type,
                ownerName = ownerName,
                status = status,
                location = location,
                date = date,
                description = description,
                contactPhone = contactPhone,
                contactEmail = contactEmail,
                registeredByUserId = userEmail,
                lat = randomLat,
                lng = randomLng
            )
            repository.registerDocument(newDoc)
            _isLoading.value = false
            onSuccess()
            setTab("DASHBOARD")
        }
    }

    // --- Delivery Confirmation ---
    fun confirmDelivery(documentId: Int) {
        viewModelScope.launch {
            repository.updateDeliveryStatus(documentId, true)
            // If we have selected a document or match, refresh them
            val currentSelDoc = _selectedDocument.value
            if (currentSelDoc != null && currentSelDoc.id == documentId) {
                _selectedDocument.value = currentSelDoc.copy(isDelivered = true)
            }
            val currentSelMatch = _selectedMatch.value
            if (currentSelMatch != null && (currentSelMatch.first.id == documentId || currentSelMatch.second.id == documentId)) {
                _selectedMatch.value = Pair(
                    currentSelMatch.first.copy(isDelivered = true),
                    currentSelMatch.second.copy(isDelivered = true)
                )
            }
        }
    }

    // --- Chat Flow ---
    fun openChat(match: Pair<DocumentEntity, DocumentEntity>) {
        val lostDoc = match.first
        val foundDoc = match.second
        val roomId = "match_${lostDoc.id}_${foundDoc.id}"
        
        // Determine if we are the owner or finder
        val userEmail = _currentUser.value?.email ?: ""
        val isLost = lostDoc.registeredByUserId.lowercase() == userEmail.lowercase()
        
        val opponentName = if (isLost) {
            // Opponent registered the found doc
            foundDoc.contactEmail.substringBefore("@")
        } else {
            // Opponent registered the lost doc
            lostDoc.contactEmail.substringBefore("@")
        }

        _activeChatRoomId.value = roomId
        _activeChatOpponentName.value = opponentName.replaceFirstChar { it.uppercase() }
        _activeChatDocType.value = lostDoc.type
        _activeChatDocOwner.value = lostDoc.ownerName
        _activeChatIsLost.value = isLost

        _currentTab.value = "CHAT"
    }

    fun sendMessage(content: String) {
        val roomId = _activeChatRoomId.value ?: return
        val user = _currentUser.value ?: return
        val userEmail = user.email

        val opponentName = _activeChatOpponentName.value
        val docType = _activeChatDocType.value
        val docOwner = _activeChatDocOwner.value
        val isLost = _activeChatIsLost.value

        viewModelScope.launch {
            // Insert user message
            val userMsg = MessageEntity(
                chatRoomId = roomId,
                senderId = userEmail,
                senderName = user.name,
                content = content
            )
            repository.insertMessage(userMsg)

            // Trigger AI response simulation
            repository.simulateOpponentReply(
                chatRoomId = roomId,
                userMsg = content,
                otherPartyName = opponentName,
                docType = docType,
                docOwner = docOwner,
                isLost = isLost
            )
        }
    }
}
