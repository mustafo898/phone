package com.example.phone

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.phone.adapder.ContactAdapter
import com.example.phone.databinding.ActivityMainBinding
import com.example.phone.model.ContactData
import uz.micro.star.lesson_15.dialogs.AddContactDialog

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val adapter by lazy {
        ContactAdapter()
    }
    var lastPos = -1

    companion object {
        const val REQUEST_CALL = 1
    }

    var pos = -1
    lateinit var data: ContactData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.list.layoutManager = LinearLayoutManager(this)
        binding.list.adapter = adapter
        binding.add.setOnClickListener {
            val dialog = AddContactDialog(this)
            dialog.setOnAddListener { name, number, context ->
                adapter.addContact(ContactData(name, number, false, context))
            }
            dialog.show()
        }

        adapter.setCallListener { pos ->
            this@MainActivity.pos = pos
            makePhoneCall()
        }

        adapter.setMoreListener{ position, name, number, context, image ->
            adapter.showPopUpMenu(image,position,this@MainActivity,name,number)
        }

        adapter.setOnItemClickListener(object : ContactAdapter.OnItemClickListener {
            override fun onClick(position: Int) {
                Toast.makeText(this@MainActivity, "$position", Toast.LENGTH_SHORT).show()
//                if (lastPos>-1){
//                    adapter.data[lastPos].isPress = false
//                }
//                if (lastPos == position){
//                    lastPos = -1
//                }else{
//                    adapter.data[position].isPress = true
//                    lastPos = position
//                }
            }
        })
    }

    private fun makePhoneCall() {
        if (pos >= 0) {
            var number = adapter.data[pos].number
            if (number.trim().isNotEmpty()) {

                if (ContextCompat.checkSelfPermission(
                        this@MainActivity,
                        android.Manifest.permission.CALL_PHONE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.CALL_PHONE), REQUEST_CALL
                    )
                } else {
                    var dial = "tel:$number"
                    startActivity(Intent(Intent.ACTION_CALL, Uri.parse(dial)))
                }
            } else {
                Toast.makeText(this, "enter number", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}