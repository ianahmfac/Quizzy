package id.ianahmfac.quizzy.data.models

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QuizModel(
    @DocumentId
    var id: String? = "",
    var name: String? = "",
    var description: String? = "",
    var image: String? = "",
    var level: String? = "",
    var questions: Int = 0,
    var visibility: String? = ""
) : Parcelable