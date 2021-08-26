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

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.lifecycleScope
import com.hongbeomi.harrypotter.R
import com.hongbeomi.harrypotter.base.BaseActivity
import com.hongbeomi.harrypotter.databinding.ActivityDetailBinding
import com.hongbeomi.harrypotter.ui.HouseType
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class DetailActivity : BaseActivity() {

    companion object {
        private const val KEY_HOUSE = "house"
        fun startActivityWithTransition(
            activity: Activity,
            imageView: ImageView,
            type: HouseType
        ) {
            val intent = Intent(activity, DetailActivity::class.java)
            intent.putExtra(KEY_HOUSE, type)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity, imageView, imageView.transitionName
            )
            activity.startActivity(intent, options.toBundle())
        }
    }

    private val binding by binding<ActivityDetailBinding>(R.layout.activity_detail)
    private val house by lazy { intent.getSerializableExtra(KEY_HOUSE) as HouseType }
    private val viewModel: DetailViewModel by viewModel { parametersOf(house) }
    private val adapter = DetailAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            house = this@DetailActivity.house
            lifecycleOwner = this@DetailActivity
            viewModel = this@DetailActivity.viewModel
            recyclerViewDetail.adapter = adapter
        }

        lifecycleScope.launch {
            viewModel.apply {
                userIntent.send(DetailIntent.FetchCharacters)
                detailState.collect {
                    when(it) {
                        is DetailState.Fetch -> {
                            adapter.submitList(it.characterList)
                            binding.animationViewLoading.visibility = View.INVISIBLE
                        }
                        is DetailState.Loading -> binding.animationViewLoading.visibility = View.VISIBLE
                        is DetailState.Error -> {
                            Log.e("Detail", it.exception.toString())
                            Toast.makeText(this@DetailActivity, R.string.toast_fail_fetch, Toast.LENGTH_SHORT).show()
                            binding.animationViewLoading.visibility = View.INVISIBLE
                        }
                        is DetailState.Idle -> binding.animationViewLoading.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

}