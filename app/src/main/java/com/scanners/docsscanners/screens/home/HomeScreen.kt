package com.scanners.docsscanners.screens.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.google.mlkit.vision.documentscanner.GmsDocumentScanner
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import com.scanners.docsscanners.R
import com.scanners.docsscanners.models.PdfEntity
import com.scanners.docsscanners.screens.common.ErrorScreen
import com.scanners.docsscanners.screens.home.components.PdfLayout
import com.scanners.docsscanners.screens.home.components.RenameDeleteDialog
import com.scanners.docsscanners.screens.common.LoadingDialog
import com.scanners.docsscanners.utils.copyPdfFileToAppDirectory
import com.scanners.docsscanners.utils.getFileSize
import com.scanners.docsscanners.utils.showToast
import com.scanners.docsscanners.viewmodels.PdfViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(pdfViewModel: PdfViewModel) {
    LoadingDialog(pdfViewModel = pdfViewModel)
    RenameDeleteDialog(pdfViewModel = pdfViewModel)

    val activity = LocalContext.current as Activity
    val context: Context = LocalContext.current

    val pdfList by pdfViewModel.pdfStateFlow.collectAsState()

    val scannerLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult> =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartIntentSenderForResult()) {
            it
            if (it.resultCode == Activity.RESULT_OK) {
                val scanningResult: GmsDocumentScanningResult? = GmsDocumentScanningResult.fromActivityResultIntent(it.data)
                scanningResult?.pdf?.let {
                    pdf ->
                    Log.d("pdfName", pdf.uri.lastPathSegment.toString())
                    val date = Date()
                    val fileName = SimpleDateFormat(
                        "dd-MMM-yyyy HH:mm:ss a",
                        Locale.getDefault()
                    ).format(date) + ".pdf"

                    copyPdfFileToAppDirectory(context, pdf.uri, fileName)
                    val pdfEntity = PdfEntity(UUID.randomUUID().toString(), fileName, getFileSize(context, fileName), date)
                    pdfViewModel.insertPdf(
                        pdfEntity
                    )
                }
            }
        }

    val scanner: GmsDocumentScanner = remember {
        GmsDocumentScanning.getClient(
            GmsDocumentScannerOptions.Builder().setGalleryImportAllowed(true).setResultFormats(GmsDocumentScannerOptions.RESULT_FORMAT_PDF).setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL).build()
        )
    }

    Scaffold (
        topBar = {
            TopAppBar(title = { 
                Text(text = stringResource(id = R.string.app_name))

            })
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = stringResource(id = R.string.scan)) },
                icon = { Icon(painter = painterResource(id = R.drawable.baseline_camera_alt_24), contentDescription = "Camera") },
                onClick = { scanner.getStartScanIntent(activity).addOnSuccessListener {
                    scannerLauncher.launch(
                        IntentSenderRequest.Builder(it).build()
                    )
                }.addOnFailureListener {
                    it.printStackTrace()
                    context.showToast(it.message.toString())
                } })
        }) { it
        if(pdfList.isEmpty()) {
            ErrorScreen(message = "No Pdf")
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                items(pdfList, key = { pdfEntity ->
                    pdfEntity.id
                }) { pdfEntity ->
                    PdfLayout(pdfEntity = pdfEntity, pdfViewModel = pdfViewModel)
                }
            }
        }
    }
    
}