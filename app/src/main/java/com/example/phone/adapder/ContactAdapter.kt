package com.example.phone.adapder

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.phone.MainActivity
import com.example.phone.R
import com.example.phone.databinding.ItemContactBinding
import com.example.phone.model.ContactData
import com.google.android.material.snackbar.Snackbar
import uz.micro.star.lesson_15.dialogs.EditContactDialog

class ContactAdapter : RecyclerView.Adapter<ContactAdapter.ViewHolder>() {
    var data = ArrayList<ContactData>()
    lateinit var OnitemClickListener: OnItemClickListener

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.OnitemClickListener = onItemClickListener
    }

    fun showPopUpMenu(
        view: ImageView,
        position: Int,
        context: Context,
        name: String,
        number: String
    ) {
        val menu = PopupMenu(context, view)
        menu.inflate(R.menu.pop_up_menu)
        menu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.edit -> {
                    val edit = EditContactDialog(
                        context,
                        name,
                        number
                    )
                    edit.setOnAddListener { namex, numberx ->
                        editContact(ContactData(namex, numberx, false, context), position)
                    }
                    edit.show()
                    Toast.makeText(view.context, "edit", Toast.LENGTH_SHORT).show()
                    return@setOnMenuItemClickListener true
                }
                R.id.delete -> {
                    deleteDialog(context, position)
                    return@setOnMenuItemClickListener true
                }
                R.id.share -> {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "text/plain"
                    val body = "Save this contact"
                    intent.putExtra(Intent.EXTRA_TEXT, body)
                    view.context.startActivity(Intent.createChooser(intent, "Send To"))
                    return@setOnMenuItemClickListener true
                }
                else -> {
                    return@setOnMenuItemClickListener false
                }
            }
        }
        menu.show()
    }

    private var callListener: ((position: Int) -> Unit)? = null

    fun setCallListener(f: (position: Int) -> Unit) {
        callListener = f
    }

    private var cardListener: ((position: Int) -> Unit)? = null

    fun setCardListener(f: (position: Int) -> Unit) {
        callListener = f
    }

    var itemSelection = -1
    private var moreListener: ((position: Int, name: String, number: String, context: Context, image: ImageView) -> Unit)? =
        null

    fun setMoreListener(f: (position: Int, name: String, number: String, context: Context, amage: ImageView) -> Unit) {
        moreListener = f
    }


    interface OnItemClickListener {
        fun onClick(position: Int)
    }

    fun addContact(contact: ContactData) {
        data.add(contact)
//        notifyDataSetChanged() bu listni to`liq yangilaydi
        notifyItemInserted(data.size - 1)//bu aynan positsiyani yangilaydi
    }

    fun deleteContact(position: Int) {
        data.removeAt(position)
//        notifyDataSetChanged()
        notifyItemRemoved(position)
    }

    fun editContact(newContact: ContactData, position: Int) {
        data[position] = newContact
        notifyItemChanged(position)//positsiyadagini yangilaydi
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            ItemContactBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(data[position])
    }

    override fun getItemCount(): Int = data.size

    inner class ViewHolder(var binding: ItemContactBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: ContactData) {
            binding.name.text = data.name
            binding.number.text = data.number

            if (itemSelection == layoutPosition) {
                if (data.isPress){
                    binding.card2.visibility = View.VISIBLE
                }else{
                    binding.card2.visibility = View.GONE
                }
            } else {
                binding.card2.visibility = View.GONE
            }
            binding.more.setOnClickListener {
                moreListener?.invoke(
                    adapterPosition,
                    data.name,
                    data.number,
                    data.context,
                    it as ImageView
                )
//                showPopUpMenu()
            }

            itemView.setOnClickListener {
                OnitemClickListener.onClick(layoutPosition)
            }

            binding.call.setOnClickListener {
                callListener?.invoke(adapterPosition)
            }

            itemView.setOnClickListener {
                itemSelection = layoutPosition
                data.isPress = true
                Toast.makeText(binding.root.context, "$layoutPosition", Toast.LENGTH_SHORT).show()
                notifyDataSetChanged();
            }
        }
    }

    fun deleteDialog(context: Context, position: Int) {
        val delete = AlertDialog.Builder(context)
        delete.setTitle("Confirm delete")
        delete.setMessage("You want delete ${data[position].name} contact")
        delete.setPositiveButton("Yes") { p0, p1 ->
            deleteContact(position)
            p0?.dismiss()
        }
        delete.setNegativeButton(
            "No"
        ) { p0, p1 -> p0?.dismiss() }
        delete.show()
    }
}
