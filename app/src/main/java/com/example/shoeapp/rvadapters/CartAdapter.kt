package com.example.shoeapp.rvadapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoeapp.Extensions.toast
import com.example.shoeapp.Models.CartModel
import com.example.shoeapp.SwipeToDelete
import com.example.shoeapp.databinding.CartproductItemBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class CartAdapter(
    private val context : Context,
    private val list:ArrayList<CartModel> ,
    private val onLongClickRemove: OnLongClickRemove,
    private val lastTotalPrice: TextView,
    private val lastSubTotalPrice: TextView,
    private val lastSubTotalItem: TextView,
    private val totalPrice: Int,
    private val subTotalPrice: Int
    ):RecyclerView.Adapter<CartAdapter.ViewHolder>(){



    private var orderDatabaseReference = Firebase.firestore.collection("orders")

    inner class ViewHolder(val binding:CartproductItemBinding):RecyclerView.ViewHolder(binding.root){

        private val onSwipeDelete = object : SwipeToDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                list.removeAt(position)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CartproductItemBinding.inflate(LayoutInflater.from(parent.context) , parent , false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentItem = list[position]

        Glide
            .with(context)
            .load(currentItem.imageUrl)
            .into(holder.binding.ivCartProduct)


        holder.binding.tvCartProductName.text = currentItem.name
        holder.binding.tvCartProductPrice.text = "$ ${currentItem.price}"
        holder.binding.tvCartItemCount.text = currentItem.quantity.toString()
        holder.binding.tvCartProductSize.text = currentItem.size

        var count = holder.binding.tvCartItemCount.text.toString().toInt()

        holder.binding.btnCartItemAdd.setOnClickListener {
            count++
            // TODO: Update Quantity in Database also
            holder.binding.tvCartItemCount.text = count.toString()

            orderDatabaseReference
                .whereEqualTo("uid", currentItem.uid)
                .whereEqualTo("pid", currentItem.pid)
                .whereEqualTo("size", currentItem.size)
                .get()
                .addOnSuccessListener { querySnapshot ->

                    for (item in querySnapshot) {
                        orderDatabaseReference.document(item.id)
                            .update("quantity", count);
                        val obj = item.toObject(CartModel::class.java);
                        lastSubTotalItem.text = "SubTotal Items(${list.size + count - 1})"
                        lastTotalPrice.text = "$ " + (totalPrice + (count * obj.price!!.toInt())).toString()
                        lastSubTotalPrice.text = "$ " + (subTotalPrice + (count * obj.price!!.toInt())).toString()

                    }

                }
        }

        holder.binding.btnCartItemMinus.setOnClickListener {
            if (count == 1) {
                list.remove(currentItem)

                //TODO: remove from database
                orderDatabaseReference
                    .whereEqualTo("uid",currentItem.uid)
                    .whereEqualTo("pid",currentItem.pid)
                    .whereEqualTo("size",currentItem.size)
                    .get()
                    .addOnSuccessListener { querySnapshot ->

                        for (item in querySnapshot) {
                            orderDatabaseReference.document(item.id).delete()
                        }
                    }

//                lastSubTotalItem.text = "SubTotal Items 0"
                if(list.size == 0){
                    lastTotalPrice.text = "$ 0"
                    lastSubTotalPrice.text = "$ 0"
                }else{
                    lastTotalPrice.text = "$ " + (totalPrice - currentItem.price!!.toInt()).toString()
                    lastSubTotalPrice.text = "$ " + (subTotalPrice - currentItem.price!!.toInt()).toString()
                }

            } else {
                count--

                orderDatabaseReference
                    .whereEqualTo("uid", currentItem.uid)
                    .whereEqualTo("pid", currentItem.pid)
                    .whereEqualTo("size", currentItem.size)
                    .get()
                    .addOnSuccessListener { querySnapshot ->

                        for (item in querySnapshot) {
                            orderDatabaseReference.document(item.id)
                                .update("quantity", count);
                            val obj = item.toObject(CartModel::class.java)
                            lastSubTotalItem.text = "SubTotal Items(${list.size + count - 1})"
                            lastTotalPrice.text = "$ " + (totalPrice + (count * obj.price!!.toInt())).toString()
                            lastSubTotalPrice.text = "$ " + (subTotalPrice + (count * obj.price!!.toInt())).toString()
                        }

                    }
            }

            // TODO: Update Quantity in Database also
            holder.binding.tvCartItemCount.text = count.toString()


        }

        holder.itemView.setOnLongClickListener {
            onLongClickRemove.onLongRemove(currentItem , position)
            true
        }


    }

    override fun getItemCount(): Int {
        return list.size
    }


    interface OnLongClickRemove{
        fun onLongRemove(item:CartModel , position: Int)
    }



}