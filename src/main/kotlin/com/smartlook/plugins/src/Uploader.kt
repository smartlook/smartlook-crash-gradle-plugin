package com.smartlook.plugins.src

import java.io.File
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Files

// Source: https://stackoverflow.com/questions/2469451/upload-files-from-java-client-to-a-http-server
fun uploadFile(apiKey: String, appVersion: String, file: File, force: Boolean) {
    val charset = "UTF-8"
    // encode the appVersion for URL string sanitation
    val encodedEndpoint = java.net.URLEncoder.encode(appVersion, charset)
    val boundary = java.lang.Long.toHexString(System.currentTimeMillis()) // Just generate some unique random value.
    val CRLF = "\r\n" // Line separator required by multipart/form-data.
    val connection = URL("https://api.smartlook.cloud/api/v1/releases/$encodedEndpoint/mapping-files?force=$force").openConnection()

    connection.doOutput = true
    connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
    // @To-Do: Not sure if the Crash API accepts the tokens in the Bearer token format - this may need adjustment
    connection.setRequestProperty("Authorization", "Bearer $apiKey")
    connection.getOutputStream().use { output ->
        PrintWriter(OutputStreamWriter(output, charset), true).use { writer ->
            // Send text file.
            writer.append("--$boundary").append(CRLF)
            writer.append("Content-Disposition: form-data; name=\"mappingFile\"; filename=\"" + file.name + "\"")
                .append(CRLF)
            writer.append("Content-Type: text/plain; charset=$charset")
                .append(CRLF) // Text file itself must be saved in this charset!
            writer.append(CRLF).flush()
            Files.copy(file.toPath(), output)
            output.flush() // Important before continuing with writer!
            writer.append(CRLF).flush() // CRLF is important! It indicates end of boundary
            // End of multipart/form-data.
            writer.append("--$boundary--").append(CRLF).flush()
        }
    }

    // Request is lazily fired whenever you need to obtain information about response.
    val responseCode = (connection as HttpURLConnection).responseCode
    val message = connection.responseMessage

    if(responseCode >= 400) {
        throw Exception("> Smartlook Plugin: Upload has failed! Build cycle unsuccessful! STATUS: $responseCode, MESSAGE: '$message'")
    }

    println("> Smartlook Plugin: STATUS: $responseCode, MESSAGE: '$message'")
}