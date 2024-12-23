package edu.skku.cs.team13

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException

class DetailActivity : AppCompatActivity() {


    companion object {
        const val SERVER_IP: String = "http://43.201.24.186:80"
    }

    private val client = OkHttpClient()
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        findViewById<Button>(R.id.backButton).setOnClickListener {
            finish() // 현재 Activity를 종료하고 이전 화면으로 돌아감
        }

        val detailTitle = findViewById<TextView>(R.id.detailTitle)
        val detailLayout = findViewById<LinearLayout>(R.id.detailLayout)

        // `pagekey` 전달받기
        val pageKey = intent.getStringExtra("pagekey")

        if (pageKey != null) {
            fetchDetail(pageKey, detailTitle, detailLayout)
        } else {
            Toast.makeText(this, "Invalid Page Key", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchDetail(pageKey: String, detailTitle: TextView, detailLayout: LinearLayout) {
        val request = Request.Builder()
            .url("$SERVER_IP/page/$pageKey") // 상세 정보 API URL
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(applicationContext, "Failed to fetch details", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (response.isSuccessful) {
                        val responseData = response.body!!.string()
                        Log.i("DetailActivity", "Response Data: $responseData")

                        val detailData = gson.fromJson(responseData, DetailResponse::class.java)

                        CoroutineScope(Dispatchers.Main).launch {
                            detailTitle.text = detailData.title ?: "No Title"

                            // 각 테이블 정보를 동적으로 추가
                            for (table in detailData.tables) {
                                val sectionTitle = TextView(applicationContext).apply {
                                    text = table.title
                                    textSize = 18f
                                    setPadding(0, 16, 0, 8)
                                }
                                detailLayout.addView(sectionTitle)

                                for (row in table.table) {
                                    val rowContent = TextView(applicationContext).apply {
                                        text = row.joinToString("\n") { "${it.title}: ${it.content}" }
                                        textSize = 16f
                                    }
                                    detailLayout.addView(rowContent)
                                }
                            }
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(applicationContext, "Error: ${response.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }
}
