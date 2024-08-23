package com.scanners.docsscanners.viewmodels

import android.app.Application
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult.Pdf
import com.scanners.docsscanners.data.repository.PdfRepository
import com.scanners.docsscanners.models.PdfEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PdfViewModel(application: Application): ViewModel() {
    var isSplashScreen by mutableStateOf(false)
    var showRenameDialog by mutableStateOf(false)
    var loadingDialog by mutableStateOf(false)

    private val pdfRepository = PdfRepository(application)
    private val _pdfStateFlow: MutableStateFlow<List<PdfEntity>> = MutableStateFlow<List<PdfEntity>>(
        arrayListOf()
    )
    val pdfStateFlow: StateFlow<List<PdfEntity>>
        get() = _pdfStateFlow

    var currentPdfEntity: PdfEntity? by mutableStateOf(null)

    init {
        viewModelScope.launch {
            delay(2000)
            isSplashScreen = false
        }

        viewModelScope.launch(Dispatchers.IO) {
            pdfRepository.getPdfList().catch {
                it.printStackTrace()
            }.collect { it
                _pdfStateFlow.emit(it)
            }
        }
    }

    fun insertPdf(pdfEntity: PdfEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            val result: Long = pdfRepository.insertPdf(pdfEntity)
        }
    }

    fun deletePdf(pdfEntity: PdfEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            val result: Int = pdfRepository.deletePdf(pdfEntity)
        }
    }

    fun updatePdf(pdfEntity: PdfEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            val result: Int = pdfRepository.updatePdf(pdfEntity)
        }
    }

}