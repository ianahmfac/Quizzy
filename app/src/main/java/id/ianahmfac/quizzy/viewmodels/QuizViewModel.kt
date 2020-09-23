package id.ianahmfac.quizzy.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.ianahmfac.quizzy.data.models.QuizModel
import id.ianahmfac.quizzy.data.repositories.FirebaseRepository

class QuizViewModel : ViewModel(), FirebaseRepository.OnFireStoreTaskComplete {
    private val repository = FirebaseRepository(this)
    val quizData = MutableLiveData<List<QuizModel>>()

    init {
        repository.getQuizData()
    }

    override fun quizDataAdded(quiz: List<QuizModel>) {
        quizData.value = quiz
    }

    override fun onError(e: Exception?) {

    }
}