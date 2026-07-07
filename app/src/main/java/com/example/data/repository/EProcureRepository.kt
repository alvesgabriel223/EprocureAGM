package com.example.data.repository

import android.util.Log
import com.example.BuildConfig
import com.example.data.dao.DocumentDao
import com.example.data.dao.MessageDao
import com.example.data.dao.UserDao
import com.example.data.entity.DocumentEntity
import com.example.data.entity.MessageEntity
import com.example.data.entity.UserEntity
import com.example.data.network.GeminiClient
import com.example.data.network.GeminiContent
import com.example.data.network.GeminiPart
import com.example.data.network.GeminiRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class EProcureRepository(
    private val userDao: UserDao,
    private val documentDao: DocumentDao,
    private val messageDao: MessageDao
) {
    // --- User Session ---
    suspend fun getOrCreateUser(email: String, name: String, phone: String): UserEntity = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim().lowercase()
        val existing = userDao.getUserById(cleanEmail)
        // Wait for first flow value or return new
        val user = UserEntity(
            id = cleanEmail,
            name = name.trim(),
            phone = phone.trim(),
            email = cleanEmail
        )
        userDao.insertUser(user)
        user
    }

    // --- Document Persistence ---
    fun getAllDocuments(): Flow<List<DocumentEntity>> {
        return documentDao.getAllDocuments()
    }

    suspend fun registerDocument(doc: DocumentEntity): Long = withContext(Dispatchers.IO) {
        documentDao.insertDocument(doc)
    }

    suspend fun getDocumentById(id: Int): DocumentEntity? = withContext(Dispatchers.IO) {
        documentDao.getDocumentById(id)
    }

    suspend fun updateDeliveryStatus(id: Int, isDelivered: Boolean) = withContext(Dispatchers.IO) {
        documentDao.updateDeliveryStatus(id, isDelivered)
    }

    suspend fun deleteDocument(doc: DocumentEntity) = withContext(Dispatchers.IO) {
        documentDao.deleteDocument(doc)
    }

    // --- Chat Messages ---
    fun getMessages(chatRoomId: String): Flow<List<MessageEntity>> {
        return messageDao.getMessagesForChatRoom(chatRoomId)
    }

    suspend fun insertMessage(message: MessageEntity): Long = withContext(Dispatchers.IO) {
        messageDao.insertMessage(message)
    }

    // --- Automated Matching Algorithm ---
    fun calculateMatches(allDocs: List<DocumentEntity>): List<Pair<DocumentEntity, DocumentEntity>> {
        val lostDocs = allDocs.filter { it.status == "LOST" && !it.isDelivered }
        val foundDocs = allDocs.filter { it.status == "FOUND" && !it.isDelivered }

        val matches = mutableListOf<Pair<DocumentEntity, DocumentEntity>>()

        for (lost in lostDocs) {
            for (found in foundDocs) {
                // Must be of the same type
                if (lost.type.lowercase().trim() == found.type.lowercase().trim()) {
                    val lostName = lost.ownerName.lowercase().trim()
                    val foundName = found.ownerName.lowercase().trim()

                    // Names are considered similar if they share a significant word (>= 3 chars)
                    // or if one is contained in the other.
                    val lostWords = lostName.split("\\s+".toRegex()).filter { it.length >= 3 }
                    val foundWords = foundName.split("\\s+".toRegex()).filter { it.length >= 3 }

                    val nameMatches = lostName.contains(foundName) || 
                                      foundName.contains(lostName) ||
                                      lostWords.any { word -> foundWords.contains(word) }

                    if (nameMatches) {
                        matches.add(Pair(lost, found))
                    }
                }
            }
        }
        return matches
    }

    // --- Simulated Chat opponent replies (utilizing Gemini with fallback) ---
    suspend fun simulateOpponentReply(
        chatRoomId: String,
        userMsg: String,
        otherPartyName: String,
        docType: String,
        docOwner: String,
        isLost: Boolean // True if user is LOST, meaning opponent is FOUND. False if user is FOUND, meaning opponent is LOST.
    ) = withContext(Dispatchers.IO) {
        // Wait 1.5 seconds to simulate a realistic reading/typing delay
        delay(1500)

        val opponentRole = if (isLost) "encontrou" else "perdeu"
        val userRole = if (isLost) "proprietário" else "encontrador"

        val systemPrompt = """
            Você é um utilizador do aplicativo EProcure chamado $otherPartyName que $opponentRole o documento $docType em nome de $docOwner.
            Você está a falar no chat interno e seguro do aplicativo com o $userRole.
            O seu objetivo é combinar a devolução de forma rápida, educada e muito segura (recomende sempre locais públicos como shoppings, esquadras ou recepções de empresas).
            A outra pessoa acabou de dizer: "$userMsg"
            Responda de forma extremamente natural, prestativa e realista, no português de Moçambique/Angola/Portugal.
            Limite a sua resposta a no máximo duas frases curtas. Não adicione saudações repetitivas se já estiver no meio da conversa.
            Nunca mencione que é uma Inteligência Artificial ou um modelo de linguagem. Seja breve e objetivo.
        """.trimIndent()

        val replyText = try {
            val key = BuildConfig.GEMINI_API_KEY
            if (key.isNotEmpty() && key != "MY_GEMINI_API_KEY") {
                val request = GeminiRequest(
                    contents = listOf(
                        GeminiContent(
                            parts = listOf(
                                GeminiPart(text = "$systemPrompt\n\nMensagem do utilizador: $userMsg\n\nSua resposta:")
                            )
                        )
                    )
                )
                val response = GeminiClient.service.generateContent(key, request)
                val generated = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!generated.isNullOrEmpty()) {
                    generated.trim()
                } else {
                    getFallbackReply(userMsg, otherPartyName, docType, isLost)
                }
            } else {
                getFallbackReply(userMsg, otherPartyName, docType, isLost)
            }
        } catch (e: Exception) {
            Log.e("EProcureRepository", "Gemini call failed, falling back", e)
            getFallbackReply(userMsg, otherPartyName, docType, isLost)
        }

        // Insert opponent message
        val opponentMsg = MessageEntity(
            chatRoomId = chatRoomId,
            senderId = "opponent",
            senderName = otherPartyName,
            content = replyText
        )
        messageDao.insertMessage(opponentMsg)
    }

    private fun getFallbackReply(
        userMsg: String,
        opponentName: String,
        docType: String,
        isLost: Boolean
    ): String {
        val msg = userMsg.lowercase()
        return when {
            msg.contains("olá") || msg.contains("bom dia") || msg.contains("boa tarde") || msg.contains("boa noite") -> {
                if (isLost) {
                    "Olá! Sim, eu encontrei o seu $docType. Fiquei muito feliz em ajudar. Como podemos fazer para lhe entregar?"
                } else {
                    "Olá! Muito obrigado por entrar em contacto. Eu perdi esse $docType e estou muito preocupado. Onde podemos nos encontrar?"
                }
            }
            msg.contains("onde") || msg.contains("local") || msg.contains("combinar") || msg.contains("encontrar") || msg.contains("suger") -> {
                "Acho mais seguro nos encontrarmos num local público e movimentado, como no shopping ou perto de uma esquadra de polícia. O que acha?"
            }
            msg.contains("horas") || msg.contains("quando") || msg.contains("tempo") || msg.contains("hoje") || msg.contains("amanhã") -> {
                "Para mim fica bem no final da tarde, por volta das 17h ou 18h. Pode ser para si?"
            }
            msg.contains("obrigado") || msg.contains("agradeço") || msg.contains("valeu") || msg.contains("graças") -> {
                if (isLost) {
                    "De nada! Fico muito contente por poder devolver. Até já!"
                } else {
                    "Eu é que agradeço do fundo do coração! Até já!"
                }
            }
            msg.contains("entreg") || msg.contains("receb") || msg.contains("confirma") || msg.contains("já cá está") -> {
                "Excelente! Por favor, confirme a entrega no aplicativo EProcure para darmos o caso como encerrado. Um abraço!"
            }
            else -> {
                if (isLost) {
                    "Sim, podemos combinar isso. O mais importante é garantir que o $docType seja entregue em segurança. Onde prefere se encontrar?"
                } else {
                    "Entendido. Fico aguardando a sua sugestão de local e hora para podermos fazer a entrega do meu $docType. Muito obrigado!"
                }
            }
        }
    }
}
