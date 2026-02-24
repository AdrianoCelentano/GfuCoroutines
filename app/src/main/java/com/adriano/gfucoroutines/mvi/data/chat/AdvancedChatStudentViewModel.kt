package com.adriano.gfucoroutines.mvi.data.chat

import androidx.lifecycle.ViewModel
import com.adriano.gfucoroutines.chat.ChatUiState
import com.adriano.gfucoroutines.chat.FakeChatApi
import com.adriano.gfucoroutines.chat.FakeChatDb
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ÜBUNG: Fortgeschrittenes Laden von Chat-Daten
 *
 * Anforderungen:
 * 
 * 1. Lade `ChatData` von der `FakeChatApi`.
 * 
 * 2. Die `ChatData` enthalten eine Liste von `MessageData` (IDs). Du musst die tatsächlichen
 *    `User`- und `Message`-Details für jedes Item laden.
 * 
 * 3. WICHTIG: Begrenze die gleichzeitige Verarbeitung, sodass NIE MEHR ALS 4 Nachrichten
 *    zur exakt gleichen Zeit geladen werden.
 * 
 * 4. Lade für jede Nachricht die `User`- und `Message`-Details gleichzeitig (parallel).
 * 
 * 5. Caching-Strategie: Bevor du einen API-Aufruf machst, prüfe, ob die Daten in der `FakeChatDb` existieren.
 *    Wenn nicht, lade sie von der `FakeChatApi`, speichere sie in der DB und gib sie dann zurück.
 * 
 * 6. Resilienz: Die API-Aufrufe schlagen zufällig fehl. Implementiere einen Retry-Mechanismus, der
 *    bei Netzwerkfehlern oder Timeouts (z.B. Anfragen, die länger als 2 Sekunden dauern) bis zu 2 Mal wiederholt.
 * 
 * 7. Wenn das Laden einer Nachricht oder eines Benutzers komplett fehlschlägt (alle Retries aufgebraucht),
 *    lasse NICHT den gesamten Ladevorgang abstürzen. Liefere einen `ChatItemUI.Error` Platzhalter für dieses spezifische Item.
 * 
 * 8. Stelle einen `MutableStateFlow<String>` für eine Suchanfrage (search query) bereit.
 * 
 * 9. Stelle einen `StateFlow<ChatUiState>` für die UI bereit. Der UI-State sollte die geladenen
 *    Nachrichten und die Suchanfrage kombinieren. Entprelle (debounce) die Suchanfrage um 300ms, damit
 *    nicht bei jedem einzelnen Tastendruck gefiltert wird.
 * 
 * 10. Filtere die angezeigten Items basierend darauf, ob der Suchstring mit dem Benutzernamen
 *    oder dem Nachrichtentext übereinstimmt.
 */
class AdvancedChatStudentViewModel : ViewModel() {

    private val api = FakeChatApi()
    private val db = FakeChatDb()

}
