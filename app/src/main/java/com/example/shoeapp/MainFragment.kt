package com.example.shoeapp

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoeapp.Extensions.toast
import com.example.shoeapp.Models.LikeModel
import com.example.shoeapp.Models.ShoeDisplayModel
import com.example.shoeapp.databinding.FragmentMainpageBinding
import com.example.shoeapp.databinding.ShoedisplaymainItemBinding
import com.example.shoeapp.rvadapters.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.NonDisposableHandle.parent


class MainFragment : Fragment(R.layout.fragment_mainpage),
    CategoryOnClickInterface,
    ProductOnClickInterface, LikedProductOnClickInterface, LikeOnClickInterface {


    private lateinit var binding: FragmentMainpageBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var productList: ArrayList<ShoeDisplayModel>
    private lateinit var likeList: ArrayList<LikeModel>
    private lateinit var categoryList: ArrayList<String>
    private lateinit var productsAdapter: ShoeDisplayAdapter
    private lateinit var categoryAdapter: MainCategoryAdapter
    private lateinit var auth: FirebaseAuth
    private var likeDBRef = Firebase.firestore.collection("LikedProducts")



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentMainpageBinding.bind(view)

        categoryList = ArrayList()
        productList = ArrayList()
        likeList = ArrayList()

        databaseReference = FirebaseDatabase.getInstance().getReference("products")

        auth = FirebaseAuth.getInstance()



        // region implements category Recycler view

        categoryList.add("Trending")
        binding.rvMainCategories.setHasFixedSize(true)
        val categoryLayoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        binding.rvMainCategories.layoutManager = categoryLayoutManager
        categoryAdapter = MainCategoryAdapter(categoryList, this)
        binding.rvMainCategories.adapter = categoryAdapter
        setCategoryList()

        // endregion implements category Recycler view


        // region implements products Recycler view
        setLikeData()
        val productLayoutManager : GridLayoutManager;
        val orientation = resources.configuration.orientation

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            productLayoutManager = GridLayoutManager(context, 2)
        }else{
            productLayoutManager = GridLayoutManager(context, 3)
        }

        productsAdapter = ShoeDisplayAdapter(requireContext(), productList, this,this, likeList)
//        LikeAdapter = LikeAdapter(requireContext(), likeList, this, this)
        binding.rvMainProductsList.layoutManager = productLayoutManager
//        binding.rvMainProductsList.adapter = LikeAdapter
        binding.rvMainProductsList.adapter = productsAdapter

        setProductsData()

        // endregion implements products Recycler view




    }

    override fun onClickProduct(item: LikeModel) {

    }


    private fun setCategoryList() {

        val valueEvent = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                categoryList.clear()
                categoryList.add("Trending")

                if (snapshot.exists()) {
                    for (dataSnapshot in snapshot.children) {
                        val products = dataSnapshot.getValue(ShoeDisplayModel::class.java)
                        if(!categoryList.contains(products!!.brand!!)){
                            categoryList.add(products!!.brand!!)
                        }
                    }

                    categoryAdapter.notifyDataSetChanged()
                }


            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "$error", Toast.LENGTH_SHORT).show()
            }

        }


        databaseReference.addValueEventListener(valueEvent)




    }


    private fun setProductsData() {

        val valueEvent = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                productList.clear()

                if (snapshot.exists()) {
                    for (dataSnapshot in snapshot.children) {
                        val products = dataSnapshot.getValue(ShoeDisplayModel::class.java)
                        productList.add(products!!)
                    }

                    productsAdapter.notifyDataSetChanged()
                }


            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "$error", Toast.LENGTH_SHORT).show()
            }

        }

        databaseReference.addValueEventListener(valueEvent)

    }

    private fun setLikeData() {
        likeDBRef
            .whereEqualTo("uid",auth.uid)
            .get()
            .addOnSuccessListener { querySnapshot ->

                for (item in querySnapshot) {
                    val temp = item.toObject(LikeModel::class.java)
                    likeList.add(temp)
                }
            }

//        productsAdapter.notifyDataSetChanged()

//        likeDBRef.addSnapshotListener { snapshot, e ->
//            if (e != null) {
//                Log.w("Error", "Listen failed.", e)
//                return@addSnapshotListener
//            }
//
//            if (snapshot != null) {
//                likeList.clear()
//
//                for (snap in snapshot) {
//                    val temp = snap.toObject(LikeModel::class.java)
//                    likeList.add(temp)
//                }
//                productsAdapter.notifyDataSetChanged()
//
////                Log.d("Success", "Current data: ${snapshot.data}")
//            } else {
//                Log.d("Warning", "Current data: null")
//            }
//        }

    }

    override fun onClickCategory(button: Button) {
        binding.tvMainCategories.text = button.text

        val valueEvent = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                productList.clear()

                if (snapshot.exists()) {
                    for (dataSnapshot in snapshot.children) {
                        val products = dataSnapshot.getValue(ShoeDisplayModel::class.java)

                        if (products!!.brand == button.text) {
                            productList.add(products)
                        }

                        if (button.text == "Trending") {
                            productList.add(products)
                        }

                    }

                    productsAdapter.notifyDataSetChanged()
                }


            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "$error", Toast.LENGTH_SHORT).show()
            }

        }

        databaseReference.addValueEventListener(valueEvent)


    }


    override fun onClickProduct(item: ShoeDisplayModel) {

        val temp = DetailsFragment()
        val args = Bundle()
        args.putString("productId", item.id)
        temp.arguments = args
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, temp)
            .addToBackStack(null)
        fragmentTransaction.commit()


    }

    private fun refresh(){


    }

    override fun onClickLike(item: ShoeDisplayModel) {

//        onClickLike(LikeModel(item.id , auth.currentUser!!.uid , item.brand , item.description , item.imageUrl , item.name ,item.price));
        if(!likeList.contains(LikeModel(item.id , auth.currentUser!!.uid , item.brand , item.description , item.imageUrl , item.name ,item.price))){
            likeDBRef
                .add(LikeModel(item.id , auth.currentUser!!.uid , item.brand , item.description , item.imageUrl , item.name ,item.price))
                .addOnSuccessListener {
                    likeList.add(LikeModel(item.id , auth.currentUser!!.uid , item.brand , item.description , item.imageUrl , item.name ,item.price));
//                    productsAdapter.notifyDataSetChanged()
                    requireActivity().toast("Added to Liked Items")
                }
                .addOnFailureListener {
                    requireActivity().toast("Failed to Add to Liked")
                }
        }else {
            likeDBRef
                .whereEqualTo("uid", auth.currentUser!!.uid).get()
                .addOnSuccessListener { querySnapshot ->

                    for (item in querySnapshot) {
                        likeDBRef.document(item.id).delete()
                        likeList.remove(item.toObject(LikeModel::class.java));
                        requireActivity().toast("Remove from Liked Items")
//                        productsAdapter.notifyDataSetChanged()
                    }
                }
        }
        productsAdapter.notifyDataSetChanged()
//        setLikeData()

    }


}




