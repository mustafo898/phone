package com.example.phone.model

import android.content.Context

data class ContactData(var name:String, var number:String, var isPress: Boolean = false, var context: Context)