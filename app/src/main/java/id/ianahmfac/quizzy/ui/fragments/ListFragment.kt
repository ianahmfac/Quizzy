package id.ianahmfac.quizzy.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import id.ianahmfac.quizzy.R
import id.ianahmfac.quizzy.adapters.QuizAdapter
import id.ianahmfac.quizzy.ui.MainActivity
import id.ianahmfac.quizzy.viewmodels.QuizViewModel
import kotlinx.android.synthetic.main.fragment_list.*

class ListFragment : Fragment(R.layout.fragment_list) {

    private lateinit var viewModel: QuizViewModel
    private lateinit var quizAdapter: QuizAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel

        setupRecyclerView()

        quizAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putParcelable("quiz", it)
            }
            findNavController().navigate(R.id.action_listFragment_to_detailsFragment, bundle)
        }

        viewModel.quizData.observe(viewLifecycleOwner, {
            quizAdapter.submitList(it)
        })
    }

    private fun setupRecyclerView() {
        quizAdapter = QuizAdapter()
        rv_quiz.apply {
            adapter = quizAdapter
            layoutManager = LinearLayoutManager(requireActivity())
        }
    }
}