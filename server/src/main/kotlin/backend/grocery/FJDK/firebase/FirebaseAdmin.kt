package backend.grocery.FJDK.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.FirestoreOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.cloud.FirestoreClient
import java.io.InputStream

object FirebaseAdmin {
    private val serviceAccount: InputStream? =
        this::class.java.classLoader.getResourceAsStream("ktor-firebase-auth.json")

    private var fireStoreOptions: FirestoreOptions? = null

    var db: Firestore
    var auth: FirebaseAuth

    init {
        if (System.getProperty("io.ktor.development") == "true") {
            fireStoreOptions = FirestoreOptions.newBuilder().setEmulatorHost("localhost:8080").build()
        }

        val firebaseOptionsBuilder = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))

        if (fireStoreOptions != null) {
            firebaseOptionsBuilder.setFirestoreOptions(fireStoreOptions)
        }

        val firebaseOptions = firebaseOptionsBuilder.build()

        FirebaseApp.initializeApp(firebaseOptions)
        db = FirestoreClient.getFirestore()
        auth = FirebaseAuth.getInstance()
    }
}
