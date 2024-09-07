package com.example.amphibians.network

//@Serializable
data class AmphibiansPhoto(
    val name: String,
    val type: String,
    val description: String,
    //@SerialName(value = "img_src")
    val img_src: String?
)