package com.example.android_pagination_api_app.data.source.mapper

import com.example.android_pagination_api_app.data.source.local.BeerEntity
import com.example.android_pagination_api_app.data.source.remote.BeerDto
import com.example.android_pagination_api_app.domain.Beer

fun BeerDto.toBeerEntity(): BeerEntity{
    return BeerEntity(
        id = id,
        name = name,
        description = description,
        tagline = tagline,
        firstBrewed = first_brewed,
        imageUrl = image_url
    )
}

fun BeerEntity.toBeer(): Beer {
    return Beer(
        id = id,
        name = name,
        tagline = tagline,
        description = description,
        firstBrewed = firstBrewed,
        imageUrl = imageUrl
    )
}