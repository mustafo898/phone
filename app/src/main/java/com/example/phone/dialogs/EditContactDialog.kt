package uz.micro.star.lesson_15.dialogs

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import com.example.phone.databinding.DialogAddContactBinding
//import uz.micro.star.lesson_15.databinding.DialogAddContactBinding

class EditContactDialog(context: Context, name: String, number: String) : AlertDialog(context) {
    var binding: DialogAddContactBinding = DialogAddContactBinding.inflate(layoutInflater)
    private var addListener: ((name: String, number: String) -> Unit)? = null

    fun setOnAddListener(f: (name: String, number: String) -> Unit) {
        addListener = f
    }

    init {
        binding.name.setText(name)
        binding.number.setText(number)

        setButton(BUTTON_POSITIVE, "Save", object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                addListener?.invoke(binding.name.text.toString(), binding.number.text.toString())
                dismiss()
            }
        })
        setButton(BUTTON_NEGATIVE, "Cancel", object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                dismiss()
            }
        })
        setView(binding.root)
    }


}