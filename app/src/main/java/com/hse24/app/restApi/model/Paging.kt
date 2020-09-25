package com.hse24.app.restApi.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Paging (
    @SerializedName("pageSize")
    var pageSize : Int,
    @SerializedName("page")
    var page : Int,
    @SerializedName("numPages")
    var numPages : Int
) : Parcelable {

    constructor(source: Parcel) : this(source.readInt(), source.readInt(), source.readInt())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(pageSize)
        parcel.writeInt(page)
        parcel.writeInt(numPages)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Paging> {
        override fun createFromParcel(parcel: Parcel): Paging {
            return Paging(parcel)
        }

        override fun newArray(size: Int): Array<Paging?> {
            return arrayOfNulls(size)
        }
    }
}