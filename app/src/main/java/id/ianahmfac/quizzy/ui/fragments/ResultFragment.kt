package id.ianahmfac.quizzy.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import id.ianahmfac.quizzy.R
import kotlinx.android.synthetic.main.fragment_result.*

class ResultFragment : Fragment(R.layout.fragment_result) {
    private val args: ResultFragmentArgs by navArgs()

    private lateinit var fireStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var currentUserId = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        fireStore = Firebase.firestore
        val quiz = args.quiz

        if (auth.currentUser != null) {
            currentUserId = auth.currentUser!!.uid
        }

        btn_back_to_home.setOnClickListener {
            findNavController().navigate(R.id.action_resultFragment_to_listFragment)
        }

        fireStore.collection("QuizList").document(quiz.id.toString()).collection("Results")
            .document(currentUserId).get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val document = it.result
                    document?.let { doc ->
                        val correct = doc.getLong("correct") as Long
                        val wrong = doc.getLong("wrong") as Long
                        val unanswered = doc.getLong("unanswered") as Long

                        tv_correct.text = correct.toString()
                        tv_wrong.text = wrong.toString()
                        tv_missed.text = unanswered.toString()

                        val total = correct + wrong + unanswered
                        val percent = (correct * 100) / total

                        tv_results.text = "$percent%"
                        pb_results.progress = percent.toInt()
                    }
                } else {
                    Toast.makeText(requireContext(), it.exception.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }
}