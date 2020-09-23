package id.ianahmfac.quizzy.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import id.ianahmfac.quizzy.R
import id.ianahmfac.quizzy.viewmodels.QuizViewModel

class MainActivity : AppCompatActivity() {
    lateinit var viewModel: QuizViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[QuizViewModel::class.java]
    }
}