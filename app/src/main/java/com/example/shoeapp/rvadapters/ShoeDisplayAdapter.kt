package com.example.shoeapp.rvadapters

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoeapp.Models.LikeModel
import com.example.shoeapp.Models.ShoeDisplayModel
import com.example.shoeapp.databinding.ShoedisplaymainItemBinding

class ShoeDisplayAdapter(
   private val context:Context ,
   private val list: List<ShoeDisplayModel>,
   private val productClickInterface: ProductOnClickInterface,
   private val likeClickInterface: LikeOnClickInterface,
   private val likelist: List<LikeModel>

    ) : RecyclerView.Adapter<ShoeDisplayAdapter.ViewHolder>() {


    inner class ViewHolder(val binding: ShoedisplaymainItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ShoedisplaymainItemBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = list[position]
        holder.binding.tvNameShoeDisplayItem.text = "${currentItem.brand} ${currentItem.name}"
        holder.binding.tvPriceShoeDisplayItem.text = "$ ${currentItem.price}"

        for(item in likelist){
            if(currentItem.id == item.pid){
                Log.d("MSG", "Yes Liked it")
                holder.binding.btnLike.backgroundTintList = ColorStateList.valueOf(Color.RED)
//                likeClickInterface.onClickLike(currentItem)
            }
        }


        Glide
            .with(context)
            .load(currentItem.imageUrl)
            .into(holder.binding.ivShoeDisplayItem)


        holder.itemView.setOnClickListener {
            productClickInterface.onClickProduct(list[position])
        }

        holder.binding.btnLike.setOnClickListener {
            if(holder.binding.btnLike.isChecked){
                holder.binding.btnLike.backgroundTintList = ColorStateList.valueOf(Color.RED)

                likeClickInterface.onClickLike(currentItem)
            }
            else{
                holder.binding.btnLike.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
//                likeClickInterface.onClickLike(currentItem)
            }

        }

    }

    override fun getItemCount(): Int {
        return list.size
    }


}

interface ProductOnClickInterface {
    fun onClickProduct(item: ShoeDisplayModel)
}

interface LikeOnClickInterface{
    fun onClickLike(item :ShoeDisplayModel)
}