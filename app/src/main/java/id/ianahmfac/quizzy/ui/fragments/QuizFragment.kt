package id.ianahmfac.quizzy.ui.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import id.ianahmfac.quizzy.R
import id.ianahmfac.quizzy.data.models.QuestionModel
import id.ianahmfac.quizzy.data.models.QuizModel
import kotlinx.android.synthetic.main.fragment_quiz.*

class QuizFragment : Fragment(R.layout.fragment_quiz), View.OnClickListener {

    private val args: QuizFragmentArgs by navArgs()
    private val questionToAnswer = arrayListOf<QuestionModel>()
    private var quizList = QuizModel()

    private lateinit var countDownTimer: CountDownTimer
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var lastQuestion = 0
    private var canAnswer = false
    private var currentQuestion = 0
    private var correctAnswer = 0
    private var wrongAnswer = 0
    private var startingPoint = 0
    private var timesUp = 0
    private var currentUserId = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fireStore = Firebase.firestore
        auth = FirebaseAuth.getInstance()
        val quiz = args.quiz
        quizList = quiz

        if (auth.currentUser != null) {
            currentUserId = auth.currentUser!!.uid
        }

        lastQuestion = quiz.questions
        fireStore.collection("QuizList").document(quiz.id.toString()).collection("Questions")
            .addSnapshotListener { value, error ->
                value?.let {
                    val listQuestion = it.toObjects<QuestionModel>()
                    pickQuestion(listQuestion.shuffled())
                    text_loading_quiz.text = quiz.name
                    loadQuestion(startingPoint)
                }
                error?.let {
                    text_loading_quiz.text = getString(R.string.error_loading_data)
                }
            }
        btn_options1.setOnClickListener(this)
        btn_options2.setOnClickListener(this)
        btn_options3.setOnClickListener(this)
        btn_next_question.setOnClickListener(this)
        btn_close_quiz.setOnClickListener(this)
    }

    private fun loadQuestion(questionNumber: Int) {
        val currQuestion = questionToAnswer[questionNumber]
        tv_number_question.text = (questionNumber + 1).toString()
        tv_question.text = currQuestion.question
        btn_options1.text = currQuestion.option_a
        btn_options2.text = currQuestion.option_b
        btn_options3.text = currQuestion.option_c

        // Question Load, set can answer
        canAnswer = true
        currentQuestion = questionNumber

        startTimer(questionNumber)
    }

    private fun startTimer(questionNumber: Int) {
        val currentTimer = questionToAnswer[questionNumber].timer
        tv_timer.text = currentTimer.toString()
        pb_timer.visibility = VISIBLE

        currentTimer?.let {
            countDownTimer = object : CountDownTimer(currentTimer * 1000, 10) {
                override fun onTick(millisUntilFinished: Long) {
                    tv_timer.text = (millisUntilFinished / 1000).toString()

                    val percent = millisUntilFinished / (currentTimer * 10)
                    pb_timer.progress = percent.toInt()
                }

                override fun onFinish() {
                    canAnswer = false
                    timesUp++
                    showNextButton()
                    tv_feedback_answer.apply {
                        text = getString(R.string.times_up)
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                    }
                }
            }
            countDownTimer.start()
        }
    }

    private fun pickQuestion(list: List<QuestionModel>) {
        for (question in 0 until lastQuestion) {
            questionToAnswer.add(list[question])
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_options1 -> {
                verifyAnswer(btn_options1)
            }
            R.id.btn_options2 -> {
                verifyAnswer(btn_options2)
            }
            R.id.btn_options3 -> {
                verifyAnswer(btn_options3)
            }
            R.id.btn_next_question -> {
                if (currentQuestion == lastQuestion - 1) {
                    submitResults()
                } else {
                    currentQuestion++
                    loadQuestion(currentQuestion)
                    resetOptions()
                }
            }
            R.id.btn_close_quiz -> {
                countDownTimer.cancel()
                requireActivity().onBackPressed()
            }
        }
    }

    private fun submitResults() {
        val result = mutableMapOf<String, Any>()
        result["correct"] = correctAnswer
        result["wrong"] = wrongAnswer
        result["unanswered"] = timesUp

        fireStore.collection("QuizList").document(quizList.id.toString()).collection("Results")
            .document(currentUserId).set(result).addOnCompleteListener {
                if (it.isSuccessful) {
                    val bundle = Bundle().apply {
                        putParcelable("quiz", quizList)
                    }
                    findNavController().navigate(R.id.action_quizFragment_to_resultFragment, bundle)
                } else {
                    text_loading_quiz.text = it.exception?.message.toString()
                }
            }
    }

    private fun resetOptions() {
        btn_options1.apply {
            background =
                ContextCompat.getDrawable(requireContext(), R.drawable.outline_light_button_bg)
            setTextColor(ContextCompat.getColor(requireContext(), R.color.colorLightText))
        }
        btn_options2.apply {
            background =
                ContextCompat.getDrawable(requireContext(), R.drawable.outline_light_button_bg)
            setTextColor(ContextCompat.getColor(requireContext(), R.color.colorLightText))
        }
        btn_options3.apply {
            background =
                ContextCompat.getDrawable(requireContext(), R.drawable.outline_light_button_bg)
            setTextColor(ContextCompat.getColor(requireContext(), R.color.colorLightText))
        }

        tv_feedback_answer.visibility = INVISIBLE
        btn_next_question.visibility = INVISIBLE
    }

    private fun verifyAnswer(btnAnswer: Button) {
        if (canAnswer) {
            questionToAnswer[currentQuestion].answer?.let {
                if (it == btnAnswer.text) {
                    btnAnswer.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.correct_answer_btn_bg
                    )
                    correctAnswer++
                    tv_feedback_answer.text = getString(R.string.correct_answers)
                } else {
                    btnAnswer.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.wrong_answer_btn_bg
                    )
                    wrongAnswer++
                    tv_feedback_answer.text = getString(R.string.wrong_answers_full, it)
                }
                btnAnswer.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorPrimaryDark
                    )
                )
                canAnswer = false
                countDownTimer.cancel()
                showNextButton()
            }
        }
    }

    private fun showNextButton() {
        if (currentQuestion == lastQuestion - 1) {
            btn_next_question.text = getString(R.string.submit)
        }
        btn_next_question.visibility = VISIBLE
        tv_feedback_answer.visibility = VISIBLE
    }

    override fun onDestroy() {
        countDownTimer.cancel()
        super.onDestroy()
    }
}