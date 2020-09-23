package id.ianahmfac.quizzy.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.ianahmfac.quizzy.R
import id.ianahmfac.quizzy.data.models.QuizModel
import kotlinx.android.synthetic.main.recycler_list_item.view.*

class QuizAdapter() : RecyclerView.Adapter<QuizAdapter.QuizViewHolder>() {

    inner class QuizViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val diffCallback = object : DiffUtil.ItemCallback<QuizModel>() {
        override fun areItemsTheSame(oldItem: QuizModel, newItem: QuizModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: QuizModel, newItem: QuizModel): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(list: List<QuizModel>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        return QuizViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        val quiz = differ.currentList[position]
        holder.itemView.apply {
            tv_quiz_title_item.text = quiz.name
            tv_quiz_desc_item.text = quiz.description
            tv_difficulty_item.text = quiz.level
            Glide.with(this.context)
                .load(quiz.image)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .into(iv_quiz_banner_item)

            btn_view_quiz.setOnClickListener {
                onItemClickListener?.let {
                    it(quiz)
                }
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    private var onItemClickListener: ((QuizModel) -> Unit)? = null

    fun setOnItemClickListener(listener: (QuizModel) -> Unit) {
        onItemClickListener = listener
    }
}