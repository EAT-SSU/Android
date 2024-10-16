package com.eatssu.android.presentation.review.list

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eatssu.android.R
import com.eatssu.android.domain.model.Review
import com.eatssu.android.databinding.ItemReviewBinding
import com.eatssu.android.presentation.review.modify.ModifyReviewActivity
import com.eatssu.android.presentation.review.report.ReportActivity


class ReviewAdapter(
    private val dataList: List<Review>,
    private val callBackReviewId: (Long) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            val data = dataList[position].apply {
                binding.tvWriterNickname.text = writerNickname
                binding.tvReviewItemComment.text = content
                binding.tvReviewItemDate.text = writeDate
                binding.tvMenuName.text = menu //TODO 리사이클러뷰로 변경
                binding.rbRate.rating = mainGrade.toFloat()
            }

            if (!data.imgUrl.isNullOrEmpty()) {
                Log.d("ReviewAdapter", data.content + data.imgUrl?.size.toString())
                data.imgUrl?.toString()?.let { Log.d("ReviewAdapter", it) }

                Glide.with(itemView)
                    .load(data.imgUrl[0])
                    .into(binding.ivReviewPhoto)
                binding.ivReviewPhoto.visibility = View.VISIBLE
                binding.cvPhotoReview.visibility = View.VISIBLE

                if (data.imgUrl[0] == "") {
                    binding.ivReviewPhoto.visibility = View.GONE
                    binding.cvPhotoReview.visibility = View.GONE
                }
            } else {
                binding.ivReviewPhoto.visibility = View.GONE
                binding.cvPhotoReview.visibility = View.GONE
            }

            binding.btnDetail.setOnClickListener { v: View ->
                if (data.isWriter) {
                    showMenu(binding.root.context, v, R.menu.menu_my_review, data)
                } else {
                    showMenu(binding.root.context, v, R.menu.menu_other_review, data)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> holder.bind(position)
        }
    }


    private fun showMenu(holdercontext: Context, v: View, @MenuRes menuRes: Int, data: Review) {
        val popup = PopupMenu(holdercontext, v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            // Respond to menu item click.
            when (menuItem.itemId) {
                R.id.report -> {
                    val intent =
                        Intent(holdercontext, ReportActivity::class.java)
                    intent.putExtra("reviewId", data.reviewId)
                    intent.putExtra("menu", data.menu)
                    ContextCompat.startActivity(holdercontext, intent, null)

                    true
                }

                R.id.fix -> {
                    val intent =
                        Intent(holdercontext, ModifyReviewActivity::class.java)
                    intent.putExtra("reviewId", data.reviewId)
                    intent.putExtra("menu", data.menu)
                    intent.putExtra("content", data.content)
                    intent.putExtra("mainGrade", data.mainGrade)
                    intent.putExtra("amountGrade", data.amountGrade)
                    intent.putExtra("tasteGrade", data.tasteGrade)

                    Log.d("ReviewFixedActivity", "전전:" + data.reviewId)
                    Log.d("ReviewFixedActivity", "전전:" + data.menu)
                    Log.d("ReviewFixedActivity", "전전:" + data.content)
////                    Timber.d("내용: "+data.content)
                    ContextCompat.startActivity(holdercontext, intent, null)
                    true
                }

                R.id.delete -> {
                    callBackReviewId(data.reviewId)
                    true
                }

                else -> {
                    true
                }
            }
        }
        popup.setOnDismissListener {
            // Respond to popup being dismissed.
        }
//             Show the popup menu.
        popup.show()
    }

    override fun getItemCount(): Int = dataList.size

}
