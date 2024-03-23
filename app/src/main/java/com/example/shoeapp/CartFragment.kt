package com.example.shoeapp

import android.content.Context
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoeapp.Extensions.toast
import com.example.shoeapp.Models.CartModel
import com.example.shoeapp.databinding.FragmentCartpageBinding
import com.example.shoeapp.rvadapters.CartAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class CartFragment : Fragment(R.layout.fragment_cartpage), CartAdapter.OnLongClickRemove {

    private lateinit var binding: FragmentCartpageBinding
    private lateinit var cartList: ArrayList<CartModel>
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: CartAdapter
    private var subTotalPrice = 0
    private var totalPrice = 40
    private var mediaPlayer: MediaPlayer? = null

    private var orderDatabaseReference = Firebase.firestore.collection("orders")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCartpageBinding.bind(view)
        auth = FirebaseAuth.getInstance()
        mediaPlayer = MediaPlayer.create(context, R.raw.purchase_notification)

        val layoutManager = LinearLayoutManager(context)


        cartList = ArrayList()

        retrieveCartItems()


        adapter = CartAdapter(requireContext(),cartList ,this, binding.tvLastTotalPrice, binding.tvLastSubTotalprice, binding.tvLastSubTotalItems, totalPrice, subTotalPrice)
        binding.rvCartItems.adapter = adapter
        binding.rvCartItems.layoutManager = layoutManager

        binding.btnCartCheckout.setOnClickListener {

            mediaPlayer?.start()

            requireActivity().toast("Whooooa!! You've Ordered Products worth ${totalPrice}\n Your Product will be delivered in next 7 days")
            binding.tvLastSubTotalprice.text ="0"
            binding.tvLastTotalPrice.text ="Min 1 product is Required"
            binding.tvLastTotalPrice.setTextColor(Color.RED)

            for(item in cartList){
                orderDatabaseReference
                    .whereEqualTo("uid",item.uid)
                    .whereEqualTo("pid",item.pid)
                    .whereEqualTo("size",item.size)
                    .get()
                    .addOnSuccessListener { querySnapshot ->

                        for (item in querySnapshot) {
                            orderDatabaseReference.document(item.id).delete()
                        }
                    }
            }
            cartList.clear()

            adapter.notifyDataSetChanged()
        }


    }




    private fun retrieveCartItems() {

        orderDatabaseReference
            .whereEqualTo("uid",auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (item in querySnapshot) {

                        val cartProduct = item.toObject(CartModel::class.java)

                        cartList.add(cartProduct)
                        subTotalPrice += cartProduct.quantity!! * cartProduct.price!!.toInt()
                        totalPrice += cartProduct.quantity!! * cartProduct.price!!.toInt()
                        binding.tvLastSubTotalprice.text = "$ " + subTotalPrice.toString()
                        binding.tvLastTotalPrice.text = "$ " + totalPrice.toString()
                        binding.tvLastSubTotalItems.text = "SubTotal Items(${cartList.size})"
                        adapter.notifyDataSetChanged()


                }

            }
            .addOnFailureListener{
                requireActivity().toast(it.localizedMessage!!)
            }


    }

    override fun onLongRemove(item: CartModel , position:Int) {


        orderDatabaseReference
            .whereEqualTo("uid",item.uid)
            .whereEqualTo("pid",item.pid)
            .whereEqualTo("size",item.size)
            .get()
            .addOnSuccessListener { querySnapshot ->

                for (item in querySnapshot){
                    orderDatabaseReference.document(item.id).delete()
                    cartList.removeAt(position)
                    subTotalPrice -= item.toObject(CartModel::class.java).price!!.toInt()
                    totalPrice -= item.toObject(CartModel::class.java).price!!.toInt()

                    if(cartList.size == 0){
                        totalPrice = 0;
                    }

                    binding.tvLastSubTotalprice.text = "$ " + subTotalPrice.toString()
                    binding.tvLastTotalPrice.text = "$ " + totalPrice.toString()
                    binding.tvLastSubTotalItems.text = "SubTotal Items(${cartList.size})"
                    adapter.notifyItemRemoved(position)
                    requireActivity().toast("Removed Successfully!!!")

                }

            }
            .addOnFailureListener {
                requireActivity().toast("Failed to remove")
            }

    }




}