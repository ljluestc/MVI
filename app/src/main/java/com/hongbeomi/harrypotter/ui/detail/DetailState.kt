package com.hongbeomi.harrypotter.ui.detail

import com.hongbeomi.harrypotter.model.Character
import java.lang.Exception

sealed class DetailState {
    object Idle: DetailState()
    object Loading: DetailState()
    data class Fetch(val characterList : List<Character>) : DetailState()
    data class Error(val exception: Exception) : DetailState()
}
