package id.ianahmfac.quizzy.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import id.ianahmfac.quizzy.R
import id.ianahmfac.quizzy.data.models.QuizModel
import kotlinx.android.synthetic.main.fragment_details.*

class DetailsFragment : Fragment(R.layout.fragment_details) {

    private val args: DetailsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val quizData = args.quiz
        populateQuiz(quizData)

        btn_start_quiz.setOnClickListener {
            val bundle = Bundle().apply {
                putParcelable("quiz", quizData)
            }
            findNavController().navigate(R.id.action_detailsFragment_to_quizFragment, bundle)
        }
    }

    private fun populateQuiz(quizData: QuizModel) {
        Glide.with(requireContext()).load(quizData.image)
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.placeholder_image)
            .into(iv_detail_banner)
        tv_detail_title.text = quizData.name
        tv_detail_desc.text = quizData.description
        tv_difficulty.text = quizData.level
        tv_total.text = quizData.questions.toString()

        loadResultsData(quizData.id.toString())
    }

    private fun loadResultsData(quizId: String) {
        Firebase.firestore.collection("QuizList")
            .document(quizId).collection("Results")
            .document(FirebaseAuth.getInstance().currentUser!!.uid).get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val doc = it.result
                    if (doc != null && doc.exists()) {
                        val correct = doc.getLong("correct") as Long
                        val wrong = doc.getLong("wrong") as Long
                        val missed = doc.getLong("unanswered") as Long

                        val total = correct + wrong + missed
                        val percent = (correct * 100) / total
                        tv_last_score.text = "$percent%"
                    }
                }

                btn_start_quiz.visibility = View.VISIBLE
            }
    }
}