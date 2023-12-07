package com.sharcome.addforgodelivery.domain.models.AddRestoran

data class AddModel(
    val restaurantName: String? ="",
    var key: String? = "",
    var imageUri: String = "",
    val foods: String? = ""
)