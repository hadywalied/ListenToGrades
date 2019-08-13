package com.example.listen2grades

import android.os.Parcel
import android.os.Parcelable

data class Grades(
    var id: String = "",
    var name: String = "",
    var grade: String = "",
    var address: String = "",
    var age: Int = 0
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt()
    )
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(grade)
        parcel.writeString(address)
        parcel.writeInt(age)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Grades> {
        override fun createFromParcel(parcel: Parcel): Grades {
            return Grades(parcel)
        }

        override fun newArray(size: Int): Array<Grades?> {
            return arrayOfNulls(size)
        }
    }
}