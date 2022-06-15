package com.qlcd.loggertools.widget.dialog

import android.os.Parcel
import android.os.Parcelable
import android.view.View
import androidx.fragment.app.DialogFragment

abstract class DialogViewConverter() : Parcelable {
    constructor(parcel: Parcel) : this() {
    }

    abstract fun convertView(dialogView: View, dialog: DialogFragment)

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DialogViewConverter> {
        override fun createFromParcel(parcel: Parcel): DialogViewConverter {
            return object : DialogViewConverter(parcel) {
                override fun convertView(dialogView: View, dialog: DialogFragment) {

                }
            }
        }

        override fun newArray(size: Int): Array<DialogViewConverter?> {
            return arrayOfNulls(size)
        }
    }
}