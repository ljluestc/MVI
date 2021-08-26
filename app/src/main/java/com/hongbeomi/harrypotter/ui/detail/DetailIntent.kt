package com.hongbeomi.harrypotter.ui.detail

sealed class DetailIntent {
    object FetchCharacters: DetailIntent()
}