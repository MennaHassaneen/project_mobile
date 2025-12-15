package com.example.iamjustgirl.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val price: Double,
    val category: String,
    val image: Int // Drawable resource ID
) : Parcelable {
    // حقول runtime للـ Compose/logic فقط
    @Transient
    var isFavorite: Boolean = false

    @Transient
    var inCart: Boolean = false
}
