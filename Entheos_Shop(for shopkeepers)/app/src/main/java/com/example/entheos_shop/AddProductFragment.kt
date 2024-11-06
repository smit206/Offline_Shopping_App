package com.example.entheos_shop

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class AddProductFragment : Fragment() {

    private lateinit var etProductName: EditText
    private lateinit var etPrice: EditText
    private lateinit var etDescription:EditText
    private lateinit var btnSelectPhotos:Button
    private lateinit var btnuploadProduct:Button
    private lateinit var rvPhotos:RecyclerView
    private lateinit var rvyourproduct:RecyclerView
    private lateinit var tvyourproduct:TextView
    private lateinit var photoAdapter: PhotoAdapter
    private lateinit var userProductAdapter: UserProductAdapter
    private var selectedPhotos:MutableList<Uri> = mutableListOf()
    private var userProducts: MutableList<Product> = mutableListOf()

    private lateinit var storageReference: StorageReference
    private lateinit var databaseReference: DatabaseReference

    private val PICK_IMAGES_REQUEST = 1

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_product, container, false)

        etProductName = view.findViewById(R.id.addpname)
        etPrice = view.findViewById(R.id.addpprice)
        etDescription = view.findViewById(R.id.addpdesc)
        btnSelectPhotos =view.findViewById(R.id.addpphotos)
        btnuploadProduct = view.findViewById(R.id.addpupload)
        tvyourproduct = view.findViewById(R.id.addtvyourproduct)
        rvPhotos = view.findViewById(R.id.addrvphotos)
        rvyourproduct = view.findViewById(R.id.addrvyourproduct)


        storageReference = FirebaseStorage.getInstance().reference
        databaseReference = FirebaseDatabase.getInstance().reference.child("products")

        photoAdapter = PhotoAdapter(selectedPhotos) {position ->
            selectedPhotos.removeAt(position)
            photoAdapter.notifyItemRemoved(position)
        }
        rvPhotos.adapter = photoAdapter
        rvPhotos.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)


        userProductAdapter = UserProductAdapter(userProducts){product ->
            removeProductFromFirebase(product)
        }
        rvyourproduct.layoutManager = LinearLayoutManager(context)
        rvyourproduct.adapter = userProductAdapter

        btnSelectPhotos.setOnClickListener{
            openImagePicker()
        }

        btnuploadProduct.setOnClickListener {
            uploadProduct()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        fetchUserProducts()
    }

    private fun openImagePicker(){
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent,"Select Pictures"),PICK_IMAGES_REQUEST)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGES_REQUEST && resultCode == Activity.RESULT_OK){
            data?.let {
                if(data.clipData != null){
                    val count = data.clipData!!.itemCount
                    for(i in 0 until count){
                        val imageUri = data.clipData!!.getItemAt(i).uri
                        selectedPhotos.add(imageUri)
                    }
                }else if(data.data != null){
                    val imageUri = data.data!!
                    selectedPhotos.add(imageUri)
                }
                photoAdapter.notifyDataSetChanged()
            }
        }
    }
    private fun uploadProduct(){
        val productName = etProductName.text.toString().trim()
        val price = etPrice.text.toString().trim()
        val description = etDescription.text.toString().trim()
        val currentUser = FirebaseAuth.getInstance().currentUser
        var shopname: String? = null

        if(productName.isEmpty() || price.isEmpty() || description.isEmpty() || selectedPhotos.isEmpty()){
            Toast.makeText(context, "Please fill all fields and select photos", Toast.LENGTH_SHORT).show()
            return
        }

        if(currentUser != null){
            val uid = currentUser.uid

            val shopRef = FirebaseDatabase.getInstance().reference.child("shops").child(uid).child("fullname")
            shopRef.addListenerForSingleValueEvent(object: ValueEventListener{
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        shopname = snapshot.getValue(String::class.java)

                        if(shopname != null){

                            val photoUrls = mutableListOf<String>()
                            for (photoUri in selectedPhotos){
                                val fileRef = storageReference.child("product_photos/${System.currentTimeMillis()}.${getFileExtension(photoUri)}")
                                fileRef.putFile(photoUri).addOnSuccessListener {
                                    fileRef.downloadUrl.addOnSuccessListener { uri ->
                                        photoUrls.add(uri.toString())
                                        if(photoUrls.size == selectedPhotos.size){
                                            saveProductToDatabase(productName,price,description,photoUrls,shopname!!,uid)
                                            fetchUserProducts()
                                            // Clear all the edittext and photo reclerview
                                            etProductName.text = null
                                            etPrice.text = null
                                            etDescription.text = null
                                            selectedPhotos.clear()
                                            photoAdapter.notifyDataSetChanged()
                                        }
                                    }
                                }
                            }
                        }else{
                            Toast.makeText(context, "Failed to retrieve shop name", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "User not authenticated!", Toast.LENGTH_SHORT).show()
                }

            })
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun saveProductToDatabase(name: String, price: String, description: String, photoUrls: List<String>,shopname: String,userId: String){
        val productRef = FirebaseDatabase.getInstance().reference.child("products").push()

        val productData = mapOf(
            "name" to name,
            "price" to price,
            "description" to description,
            "photoUrls" to photoUrls,
            "shopName" to shopname,
            "userId" to userId
        )

        productRef.setValue(productData).addOnCompleteListener { task ->
            if(task.isSuccessful){
                Toast.makeText(context, "Product uploaded successfully", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, "Failed to upload product", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchUserProducts(){
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if(currentUserId != null){
            databaseReference.orderByChild("userId").equalTo(currentUserId)
                .addListenerForSingleValueEvent(object: ValueEventListener{
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onDataChange(snapshot: DataSnapshot) {
                        userProducts.clear()

                        for(productSnapshot in snapshot.children){
                            val product = productSnapshot.getValue(Product::class.java)
                            product?.let {
                                userProducts.add(it)
                            }
                        }
                        userProductAdapter.notifyDataSetChanged()
                        rvyourproduct.visibility = if(userProducts.isEmpty()) View.GONE else View.VISIBLE
                        tvyourproduct.visibility = if(userProducts.isEmpty()) View.GONE else View.VISIBLE
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(context, "Failed to load user products: ${error.message}", Toast.LENGTH_SHORT).show()
                    }

                })
        }else{
            Toast.makeText(context, "Current user is not authenticated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeProductFromFirebase(product: Product){
        val userProductRef = FirebaseDatabase.getInstance().reference.child("products")
        val query = userProductRef.orderByChild("name").equalTo(product.name)

        query.addListenerForSingleValueEvent(object: ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                for (productSnapshot in snapshot.children){
                    productSnapshot.ref.removeValue().addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            userProducts.remove(product)
                            userProductAdapter.notifyDataSetChanged()
                        }else{
                            Toast.makeText(requireContext(), "Failed to remove product", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to remove product", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun getFileExtension(uri: Uri):String{
        val contentResolver = requireContext().contentResolver
        val mineTypeMap = MimeTypeMap.getSingleton()
        return mineTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)).orEmpty()
    }
}

data class Product(
    val name: String = "",
    val price: String = "",
    val description: String = "",
    val photoUrls: List<String> = emptyList(),
    val shopname: String = "",
    val userId: String = ""
)