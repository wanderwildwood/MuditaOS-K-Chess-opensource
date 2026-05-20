package com.mudita.chess.gameloader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class GameLoaderActivity : ComponentActivity() {

    private val viewModel: GameLoaderViewModel by viewModels(factoryProducer = { ViewModelFactory })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                viewModel.process(intent)
            }
            finish()
        }
    }

    private companion object ViewModelFactory : KoinComponent, ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            check(modelClass == GameLoaderViewModel::class.java)
            return GameLoaderViewModel(get(), get()) as T
        }
    }
}
