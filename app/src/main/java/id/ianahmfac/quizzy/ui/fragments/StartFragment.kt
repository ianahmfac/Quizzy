package id.ianahmfac.quizzy.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import id.ianahmfac.quizzy.R
import kotlinx.android.synthetic.main.fragment_start.*

class StartFragment : Fragment(R.layout.fragment_start) {

    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        tv_start_feedback.text = getString(R.string.checking_user_account)
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Navigate to List Fragment
            findNavController().navigate(R.id.action_startFragment_to_listFragment)
        } else {
            // Create new account
            tv_start_feedback.text = getString(R.string.creating_account)
            auth.signInAnonymously().addOnCompleteListener {
                if (it.isSuccessful) {
                    tv_start_feedback.text = getString(R.string.account_created)
                    findNavController().navigate(R.id.action_startFragment_to_listFragment)
                } else {
                    Toast.makeText(requireContext(), it.exception.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}