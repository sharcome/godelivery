package com.sharcome.addforgodelivery.restorant

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.sharcome.addforgodelivery.R
import com.sharcome.addforgodelivery.databinding.FragmentAddRestorantBinding
import com.sharcome.addforgodelivery.domain.models.AddRestoran.AddModel
import com.sharcome.addforgodelivery.domain.preference.ResPreferenceManager
import java.util.*

class AddRestaurantFragment : Fragment() {

    lateinit var viewBinding: FragmentAddRestorantBinding
    lateinit var databaseReference: DatabaseReference
    lateinit var mProfileUri: Uri

    val storage = Firebase.storage
    val storageRef = storage.reference

    lateinit var dialog: Dialog

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    //Image Uri will not be null for RESULT_OK
                    val fileUri = data?.data!!

                    mProfileUri = fileUri
                    viewBinding.mainImage.setImageURI(fileUri)
                }

                com.github.dhaval2404.imagepicker.ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(
                        requireContext(),
                        com.github.dhaval2404.imagepicker.ImagePicker.getError(data),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    Toast.makeText(requireContext(), "Bekor qilindi!", Toast.LENGTH_SHORT).show()
                }
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        viewBinding = FragmentAddRestorantBinding.inflate(layoutInflater,container,false)
        return viewBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        databaseReference = FirebaseDatabase.getInstance().getReference()

        setDialog()
        viewBinding.buttonEnterGalery.setOnClickListener {
            com.github.dhaval2404.imagepicker.ImagePicker.with(requireActivity()).galleryOnly()

                .cropSquare()
                .compress(1024)
                .maxResultSize(1080,1080)
                .createIntent{ intent ->
                    startForProfileImageResult.launch(intent)

                }

        }
        viewBinding.buttonNext.setOnClickListener {
            val restaurantName = viewBinding.textInputLayout.editText?.text.toString()
            val model = AddModel(
                restaurantName = restaurantName,
            )
            uploadImageToFirebase(mProfileUri,model)
        }

    }

    private fun uploadImageToFirebase(fileUri: Uri, model: AddModel) {
        val fileName = UUID.randomUUID().toString()
        val ref = storageRef.child("images/$fileName")

        dialog.show()
        ref.putFile(fileUri)
            .addOnSuccessListener{
                ref.downloadUrl.addOnSuccessListener{uri->
                    val imageUrl = uri.toString()
                    model.imageUri = imageUrl
                    saveImageInfoToDatabase(model)
                }
            }
    }

    private fun saveImageInfoToDatabase(model: AddModel) {
       val databaseRef = model.key?.let { FirebaseDatabase.getInstance().getReference("restorants").child(it) }

        val imageId  = databaseRef?.push()?.key ?: return
        model.key = imageId
        databaseRef.child(imageId).setValue(model).addOnCompleteListener{
            if (it.isSuccessful){
                dialog.dismiss()
                val bundle = Bundle()
                bundle.putString("key",imageId)
                val navController = findNavController()
                navController.navigate(R.id.addFoodFragment,bundle)
            }
        }
    }


        private fun setDialog() {
            dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.dialog_loading)

            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialog.window?.setGravity(Gravity.CENTER)

        }
    }