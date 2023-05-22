package com.eatssu.android.ui.review

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eatssu.android.data.model.response.GetReviewListResponse
import com.eatssu.android.databinding.ItemReviewBinding


class ReviewAdapter(private val dataList: List<GetReviewListResponse.Data>):
    RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            binding.tvReviewItemId.text = dataList[position].writerId.toString()
            binding.tvReviewItemComment.text =dataList[position].content.toString()
            binding.tvReviewItemDate.text = dataList[position].writeDate.toString()

            if(dataList[position].tagList.size==0){

            }
            else if(dataList[position].tagList.size==1){
                binding.tvReviewItemTag1.text=dataList[position].tagList[1]
            }
            else if(dataList[position].tagList.size==2){
                binding.tvReviewItemTag1.text=dataList[position].tagList[1]
                binding.tvReviewItemTag2.text=dataList[position].tagList[2]
            }
            else if(dataList[position].tagList.size==3){
                binding.tvReviewItemTag1.text=dataList[position].tagList[1]
                binding.tvReviewItemTag2.text=dataList[position].tagList[2]
                binding.tvReviewItemTag3.text=dataList[position].tagList[3]
            }


        //사진 바인딩
//            if (dataList[position].imgUrlList == "trainerProfile"){
//                binding.ivTrainerProfile.setImageResource(R.drawable.ic_profile)
//            }
//            else if (data?.profile != "trainerProfile"|| data.profile != null) {
//                Glide.with(this)
//                    .load("${data?.profile}")
//                    .into(binding.ivTrainerProfile)
//                binding.ivTrainerProfile.clipToOutline = true
//                Log.d("post", data?.profile.toString())
//            }
//
//            if (data?.background != null) {
//                Glide.with(this)
//                    .load("${data.background}")
//                    .into(binding.ivBackgroundPhoto)
//            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding =
            ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)

        //서버 연결
//        holder.itemView.setOnClickListener {
//            val intent = Intent(holder.itemView.context, ReviewListActivity::class.java)
//            intent.putExtra(
//                "menuId", dataList[position].menuId
//            )
//            ContextCompat.startActivity(holder.itemView.context, intent, null)
//        }
    }

    override fun getItemCount(): Int = dataList.size
}