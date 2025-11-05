package com.darkzodiak.kontrol.core.presentation.delayDialog

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@HiltViewModel
class DelayDialogViewModel: ViewModel() {

    private val scope = CoroutineScope(Dispatchers.Default)

    private var currentTimeWithDelay =
}