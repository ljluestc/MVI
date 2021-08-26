package com.hongbeomi.harrypotter.ui.detail

/**
 * Copyright 2020 Hongbeom Ahn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **/

import androidx.lifecycle.*
import com.hongbeomi.harrypotter.data.repository.Repository
import com.hongbeomi.harrypotter.ui.HouseType
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow

class DetailViewModel(private val house: HouseType, private val repository: Repository) : ViewModel() {

    val userIntent = Channel<DetailIntent>(Channel.UNLIMITED)
    private val _detailState = MutableStateFlow<DetailState>(DetailState.Idle)
    val detailState: StateFlow<DetailState> = _detailState

    init {
        handleIntent()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            userIntent.consumeAsFlow().collect {
                when (it) {
                    is DetailIntent.FetchCharacters -> fetchCharacterList()
                }
            }
        }
    }

    private fun fetchCharacterList() {
        viewModelScope.launch {
            _detailState.value = DetailState.Loading
            _detailState.value = try {
                DetailState.Fetch(repository.getCharacters(house.name))
            } catch (e: Exception) {
                DetailState.Error(e)
            }
        }
    }

}