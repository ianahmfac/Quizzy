package id.ianahmfac.quizzy.data.repositories

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import id.ianahmfac.quizzy.data.models.QuizModel

class FirebaseRepository(private val onFireStoreTaskComplete: OnFireStoreTaskComplete) {

    private val fireStore = Firebase.firestore
    private val quizRef = fireStore.collection("QuizList").whereEqualTo("visibility", "public")

    fun getQuizData() {
        quizRef.addSnapshotListener { value, error ->
            value?.let {
                onFireStoreTaskComplete.quizDataAdded(it.toObjects())
            }
            error?.let {
                onFireStoreTaskComplete.onError(it)
            }
        }
    }

    interface OnFireStoreTaskComplete {
        fun quizDataAdded(quiz: List<QuizModel>)
        fun onError(e: Exception?)
    }
}