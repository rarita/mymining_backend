package com.raritasolutions.mymining.pdf_processor

import com.fasterxml.jackson.databind.ObjectMapper
import com.raritasolutions.mymining.model.filesystem.CachedFile
import com.raritasolutions.mymining.model.filesystem.toCachedFile
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component("proc_simplyPDF")
class SimplyPDFProcessor @Autowired constructor(private val okHttpClient: OkHttpClient,
                                                private val mapper: ObjectMapper,
                                                @Value("\${info.app.name}") private var appName: String = "MyMiningApplication",
                                                @Value("\${info.app.version}") private var appVersion: String = "0.9.2-test_build") : BasePDFProcessor {

    // Options that should be injected
    private val serviceOrigin = HttpUrl.Builder()
            .scheme("https")
            .host("simplypdf.com")
            .build()

    private val headers = Headers.Builder()
            .add("Accept", "application/json")
            .add("User-Agent", "$appName/$appVersion")
            .build()

    private val cacheControl = CacheControl.FORCE_NETWORK

    // Using get() to generate object every time it is requested
    val requestBodyTemplate
            get() = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("SuccessPage", "/Success/{ID}")
            .addFormDataPart("OutputFormat", "Excel")
            .addFormDataPart("TablesOnSheetMode", "combine-logical")
            .addFormDataPart("NonTableDataMode", "exclude")
            .addFormDataPart("DataSeparators", "auto")
            .addFormDataPart("ThousandsSeparator", "comma")
            .addFormDataPart("DecimalSymbol", "period")
            .addFormDataPart("AutoRotate", "true")
            .addFormDataPart("TextRecoveryMode", "0")
            .addFormDataPart("NSEMode", "0")
            .addFormDataPart("ocrLanguage", "au")
            .addFormDataPart("PageRange", "1-100")
            .addFormDataPart("Password", "")


    /**
     * Use SimplyPDF (https://simplypdf.com) as PDF processor
     * and convert PDF to Excel file which is accessible by Apache POI tools
     * @param cachedFile Source PDF file that needs to be converted (stored in the DB)
     * @throws IllegalStateException If there was any error during the file conversion.
     */
    @Throws(IllegalStateException::class)
    override fun processFile(cachedFile: CachedFile): CachedFile {

        val taskID = submitFile(cachedFile)
        val conversionResult = processPDF(taskID)

        if (conversionResult == "ready")
            return downloadToCachedFile(taskID, cachedFile.fileAlias, cachedFile.originDigest)
        else
            throw IllegalStateException("There was an error during the $cachedFile conversion. SimplyPDF responded with non-OK $conversionResult code")
    }

    /**
     * Sends a query to the SimplyPDF server to convert given file
     * Cookies are automatically provided by OkHTTPClient implementation
     * @param target File that should be converted
     * @return SimplyPDF query ID used to check and retrieve processed file
     */
    private fun submitFile(target: CachedFile): Int {
        val fileBody = target.fileContents.toRequestBody("application/octet-stream".toMediaTypeOrNull())

        val requestBody = requestBodyTemplate
                .addFormDataPart("File", target.fileName, fileBody)
                .build()

        val request = Request.Builder()
                .headers(headers)
                .cacheControl(cacheControl)
                .url(serviceOrigin.newBuilder().addPathSegments("api/convert").build())
                .post(requestBody)
                .build()

        val response = okHttpClient.newCall(request).execute()
        return mapper.readTree(response.body?.bytes())["id"]?.asInt()
                ?: throw IllegalStateException("Conversion server didn't respond with an id")
    }

    /**
     * Wait for completion of the given PDF processing job
     * @param taskID Processing ID assigned by SimplyPDF
     * @return Status of the given task upon completion
     */
    private fun processPDF(taskID: Int): String {
        // TODO rename me
        val request = Request.Builder()
                .cacheControl(cacheControl)
                .url(serviceOrigin.newBuilder().addPathSegments("api/status/$taskID").build())
                .headers(headers)
                .get()
                .build()

        var attempts = 0
        while (attempts++ < 60) {
            // Take a break
            Thread.sleep(getStatusCheckInterval(attempts))
            // Ask server if it is done processing the file
            val response = okHttpClient.newCall(request).execute()
            val bodyBytes = response.body?.bytes() ?: throw IllegalStateException("Processing server didn't respond. taskID: $taskID")
            val tree = mapper.readTree(bodyBytes)
            val taskStatus = tree["error"]?.asText() ?: tree["status"].asText()

            // Quit if it is
            if (taskStatus != "ongoing")
                return taskStatus
        }

        return "timed-out"
    }

    /**
     * Returns the time interval for PDF conversion status checks
     * @param attempts Number of performed status checks
     * @return Time (ms) before next status check
     */
    private fun getStatusCheckInterval(attempts: Int): Long
            = when {
        (attempts < 16) -> 1000
        (attempts < 49) -> 5000
        else -> 30000
    }

    /**
     * Download processed file and return a [CachedFile] that is ready to use
     * @param taskID of the processing task
     * @param fileAlias Alias that was given to origin file by the university website
     * @param originDigest MD5 checksum of the original PDF file
     * @return Processed Excel [CachedFile]
     */
    private fun downloadToCachedFile(taskID: Int, fileAlias: String, originDigest: String): CachedFile {
        val request = Request.Builder()
                .headers(headers)
                .cacheControl(cacheControl)
                .url(serviceOrigin.newBuilder().addPathSegments("api/download/$taskID").build())
                .get()
                .build()

        val response = okHttpClient.newCall(request).execute()
        check(response.isSuccessful || response.body != null)
            { "File with id $taskID was not downloaded, reason: ${response.message}" }

        return response.toCachedFile(fileAlias, originDigest)
    }

}