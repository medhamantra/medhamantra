package com.example.ui.util

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.Toast

/**
 * Utility functions for safely opening external links, dialing phone numbers,
 * launching WhatsApp, viewing PDFs, and initiating downloads.
 */
object IntentHelpers {

    fun openDialer(context: Context, phoneNumber: String) {
        try {
            val cleanNumber = phoneNumber.replace(Regex("[^0-9+]"), "")
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$cleanNumber")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Unable to open phone dialer: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    fun openWhatsApp(context: Context, phoneNumber: String, prefilledText: String = "Hello MEDHA MANTRA, I want admission enquiry.") {
        try {
            val cleanNumber = phoneNumber.replace(Regex("[^0-9]"), "")
            val formatted = if (cleanNumber.length == 10) "91$cleanNumber" else cleanNumber
            val url = "https://api.whatsapp.com/send?phone=$formatted&text=${Uri.encode(prefilledText)}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "WhatsApp is not installed or unavailable", Toast.LENGTH_SHORT).show()
        }
    }

    fun openEmail(context: Context, emailAddress: String, subject: String = "Inquiry: MEDHA MANTRA Course Admission") {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$emailAddress")
                putExtra(Intent.EXTRA_SUBJECT, subject)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "No email app found on device", Toast.LENGTH_SHORT).show()
        }
    }

    fun openBrowser(context: Context, rawUrl: String) {
        try {
            val url = if (rawUrl.startsWith("http://") || rawUrl.startsWith("https://")) rawUrl else "https://$rawUrl"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Cannot open link: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    fun openGoogleMaps(context: Context, address: String) {
        try {
            val uri = Uri.parse("geo:0,0?q=" + Uri.encode(address))
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback to web browser Google maps
            openBrowser(context, "https://maps.google.com/?q=" + Uri.encode(address))
        }
    }

    fun viewPdfDocument(context: Context, documentUrl: String, noticeTitle: String = "Notice Document") {
        if (documentUrl.isBlank()) {
            Toast.makeText(context, "No document attached to this notice", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            // Using Google Docs Viewer as standard web viewer fallback for raw PDFs
            val viewUrl = if (documentUrl.endsWith(".pdf", ignoreCase = true)) {
                "https://docs.google.com/gview?embedded=true&url=" + Uri.encode(documentUrl)
            } else {
                documentUrl
            }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(viewUrl))
            context.startActivity(intent)
        } catch (e: Exception) {
            openBrowser(context, documentUrl)
        }
    }

    fun downloadPdfDocument(context: Context, documentUrl: String, title: String = "Medha_Mantra_Notice.pdf") {
        if (documentUrl.isBlank()) {
            Toast.makeText(context, "No file URL provided", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val uri = Uri.parse(documentUrl)
            val request = DownloadManager.Request(uri).apply {
                setTitle(title)
                setDescription("Downloading document from MEDHA MANTRA")
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    "${title.replace(Regex("[^a-zA-Z0-9_]"), "_")}.pdf"
                )
            }
            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)
            Toast.makeText(context, "Download started: Check notification bar", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            // Fallback: Open in browser so browser handles download
            openBrowser(context, documentUrl)
        }
    }
}
