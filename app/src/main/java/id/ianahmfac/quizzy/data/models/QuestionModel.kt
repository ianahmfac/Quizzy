package id.ianahmfac.quizzy.data.models

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QuestionModel(
    @DocumentId
    val id: String? = "",
    val question: String? = "",
    val option_a: String? = "",
    val option_b: String? = "",
    val option_c: String? = "",
    val answer: String? = "",
    val timer: Long? = 0L
) : Parcelable