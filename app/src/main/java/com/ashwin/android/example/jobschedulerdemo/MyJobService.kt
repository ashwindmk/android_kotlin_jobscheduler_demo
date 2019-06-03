package com.ashwin.android.example.jobschedulerdemo

import android.annotation.TargetApi
import android.app.job.JobParameters
import android.app.job.JobService
import android.os.AsyncTask
import android.os.Build
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class MyJobService : JobService() {
    override fun onStartJob(params: JobParameters?): Boolean {
        Log.w("debug-log", "on-start-job: thread: ${Thread.currentThread().name}")

        object : AsyncTask<String, Void, String>() {
            override fun doInBackground(vararg params: String): String? {
                return getResponse(params[0])
            }

            override fun onPostExecute(result: String?) {
                if (result != null) {
                    Log.w("debug-log", "response: $result")
                    jobFinished(params, false)
                } else {
                    jobFinished(params, true)
                }
            }
        }.execute("https://gist.githubusercontent.com/ashwindmk/1e2097ac3de60a40c469ec1a60f35b41/raw/profile.json")

        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.w("debug-log", "on-stop-job: thread: ${Thread.currentThread().name}")
        return false
    }

    fun getResponse(reqUrl: String): String? {
        // Maintain http url connection.
        var httpConn: HttpURLConnection? = null

        // Read text input stream.
        var isReader: InputStreamReader? = null

        // Read text into buffer.
        var bufReader: BufferedReader? = null

        // Save server response text.
        val readTextBuf = StringBuffer()

        try {
            // Create a URL object use page url.
            val url = URL(reqUrl)

            // Open http connection to web server.
            httpConn = url.openConnection() as HttpURLConnection

            // Set http request method to get.
            httpConn!!.requestMethod = "GET"

            // Set connection timeout and read timeout value.
            httpConn!!.connectTimeout = 10000
            httpConn!!.readTimeout = 10000

            // Get input stream from web url connection.
            val inputStream = httpConn!!.inputStream

            // Create input stream reader based on url connection input stream.
            isReader = InputStreamReader(inputStream)

            // Create buffered reader.
            bufReader = BufferedReader(isReader)

            // Read line of text from server response.
            var line = bufReader!!.readLine()

            // Loop while return line is not null.
            while (line != null) {
                // Append the text to string buffer.
                readTextBuf.append(line)

                // Continue to read text line.
                line = bufReader!!.readLine()
            }

            return readTextBuf.toString()
        } catch (ex: MalformedURLException) {
            Log.e("debug-log", ex.message, ex)
        } catch (ex: IOException) {
            Log.e("debug-log", ex.message, ex)
        } finally {
            try {
                if (bufReader != null) {
                    bufReader!!.close()
                    bufReader = null
                }

                if (isReader != null) {
                    isReader!!.close()
                    isReader = null
                }

                if (httpConn != null) {
                    httpConn!!.disconnect()
                    httpConn = null
                }
            } catch (ex: IOException) {
                Log.e("debug-log", ex.message, ex)
            }
        }

        return null
    }
}
