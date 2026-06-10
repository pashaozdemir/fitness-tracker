package at.htlgrieskirchen.fitness.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

data class FsItem(
    val id: String = "",
    val name: String = "",
    val type: String = "Sonstiges",
    val durationMinutes: Int = 0,
    val timestamp: Long = 0L
)

class FirestoreItemRepository {

    private val collection = FirebaseFirestore.getInstance().collection("items")

    fun listen(onChange: (List<FsItem>) -> Unit): ListenerRegistration {
        return collection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val items = snapshot.documents.map { doc ->
                        FsItem(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            type = doc.getString("type") ?: "Sonstiges",
                            durationMinutes = (doc.getLong("durationMinutes") ?: 0L).toInt(),
                            timestamp = doc.getLong("timestamp") ?: 0L
                        )
                    }
                    onChange(items)
                }
            }
    }

    fun add(name: String, type: String, durationMinutes: Int) {
        collection.add(mapOf(
            "name" to name,
            "type" to type,
            "durationMinutes" to durationMinutes,
            "timestamp" to System.currentTimeMillis()
        ))
    }

    fun update(id: String, newName: String, newType: String, newDurationMinutes: Int) {
        collection.document(id).update(mapOf<String, Any>(
            "name" to newName,
            "type" to newType,
            "durationMinutes" to newDurationMinutes
        ))
    }

    fun delete(id: String) {
        collection.document(id).delete()
    }
}