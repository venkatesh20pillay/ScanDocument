package com.scanners.docsscanners.screens.home.components

import android.content.ClipData
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.scanners.docsscanners.R
import com.scanners.docsscanners.viewmodels.PdfViewModel
import androidx.compose.material3.Button
import androidx.compose.ui.platform.LocalContext
import com.scanners.docsscanners.models.PdfEntity
import com.scanners.docsscanners.utils.deleteFile
import com.scanners.docsscanners.utils.getFileUri
import com.scanners.docsscanners.utils.renameFile
import java.util.Date


@Composable
fun RenameDeleteDialog(pdfViewModel: PdfViewModel) {
    var newNameText by remember(pdfViewModel.currentPdfEntity) {
        mutableStateOf(pdfViewModel.currentPdfEntity?.name ?: "")
    }
    val context = LocalContext.current
    if (pdfViewModel.showRenameDialog) {
        Dialog(onDismissRequest = {
            pdfViewModel.showRenameDialog = false
        }) {
            Surface(
                shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        stringResource(id = R.string.rename_pdf),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = newNameText, onValueChange = { newText ->
                        newNameText = newText
                    },
                        label = {
                            Text(text = stringResource(R.string.pdf_name))
                        })
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        IconButton(onClick = {
                            pdfViewModel.currentPdfEntity?.let {  it
                                pdfViewModel.showRenameDialog = false
                                val getFileUri: Uri = getFileUri(context, it.name)
                                val shareIntent = Intent(Intent.ACTION_SEND)
                                shareIntent.type = "application/pdf"
                                shareIntent.clipData = ClipData.newRawUri("", getFileUri)
                                shareIntent.putExtra(Intent.EXTRA_STREAM, getFileUri)
                                shareIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                                context.startActivity(Intent.createChooser(shareIntent, "Share"))
                            }
                        }) {
                            Icon(
                                painterResource(id = R.drawable.share),
                                contentDescription = null
                            )
                        }
                        IconButton(onClick = {
                            pdfViewModel.currentPdfEntity?.let {  it
                                pdfViewModel.showRenameDialog = false
                                if(deleteFile(context, it.name)) {
                                    pdfViewModel.deletePdf(it)
                                }
                            }
                        }) {
                            Icon(
                                painterResource(id = R.drawable.ic_delete),
                                contentDescription = null,
                                tint = Color.Red
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            pdfViewModel.showRenameDialog = false
                        }) {
                            Text(text = stringResource(id = R.string.cancel))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            pdfViewModel.currentPdfEntity?.let { pdf ->
                                if (!pdf.name.equals(newNameText, ignoreCase = true)) {
                                    pdfViewModel.showRenameDialog = false
                                    renameFile(context, pdf.name, newNameText)
                                    val updatePdf: PdfEntity = pdf.copy(
                                        name = newNameText, lastModifiedTime = Date()
                                    )
                                    pdfViewModel.updatePdf(updatePdf)
                                } else {
                                    pdfViewModel.showRenameDialog = false
                                }
                            }
                        }) {
                            Text(text = stringResource(id = R.string.update))
                        }
                    }
                }
            }
        }
    }
}