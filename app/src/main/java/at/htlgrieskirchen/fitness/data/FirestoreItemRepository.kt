package at.htlgrieskirchen.fitness.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

data class FsItem(val id: String = "", val name: String = "")

class FirestoreItemRepository {

    private val collection = FirebaseFirestore.getInstance().collection("items")

    fun listen(onChange: (List<FsItem>) -> Unit): ListenerRegistration {
        return collection
            .orderBy("name", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val items = snapshot.documents.map { doc ->
                        FsItem(id = doc.id, name = doc.getString("name") ?: "")
                    }
                    onChange(items)
                }
            }
    }

    fun add(name: String) {
        collection.add(mapOf("name" to name))
    }

    fun update(id: String, newName: String) {
        collection.document(id).update("name", newName)
    }

    fun delete(id: String) {
        collection.document(id).delete()
    }
}