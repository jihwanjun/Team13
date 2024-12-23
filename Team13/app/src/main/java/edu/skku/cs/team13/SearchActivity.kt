package edu.skku.cs.team13

import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import edu.skku.cs.team13.CheckableSpinnerAdapter.SpinnerItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import kotlin.random.Random

class SearchActivity : AppCompatActivity() {
    companion object {
        const val SERVER_IP: String = "http://43.201.24.186:80"
    }

    private val types: List<SpinnerItem> = listOf(
        SpinnerItem("type", "job", "일자리"),
        SpinnerItem("type", "residence", "주거"),
        SpinnerItem("type", "education", "교육"),
        SpinnerItem("type", "welfareCulture", "복지.문화"),
        SpinnerItem("type", "right", "참여.권리")
    )
    private val selectedTypes: MutableSet<SpinnerItem> = HashSet()
    private val employmentTargets: List<SpinnerItem> = listOf(
        SpinnerItem("employmentTarget", "employed", "재직자"),
        SpinnerItem("employmentTarget", "selfEmployed", "자영업자"),
        SpinnerItem("employmentTarget", "unemployed", "미취업자"),
        SpinnerItem("employmentTarget", "freelancer", "프리랜서"),
        SpinnerItem("employmentTarget", "dailyWorker", "일용근로자"),
        SpinnerItem("employmentTarget", "startup", "(예비)창업자"),
        SpinnerItem("employmentTarget", "shortTerm", "단기근로자"),
        SpinnerItem("employmentTarget", "farmer", "영농종사자"),
        SpinnerItem("employmentTarget", "noMatter", "제한없음")
    )
    private val selectedEmploymentTargets: MutableSet<SpinnerItem> = HashSet()
    private val recruitmentStatuses: List<SpinnerItem> = listOf(
        SpinnerItem("recruitmentStatus", "regular", "상시"),
        SpinnerItem("recruitmentStatus", "underWay", "모집중"),
        SpinnerItem("recruitmentStatus", "toBe", "모집예정"),
        SpinnerItem("recruitmentStatus", "end", "마감")
    )
    private val selectedRecruitmentStatuses: MutableSet<SpinnerItem> = HashSet()
    private val ageGroups: List<SpinnerItem> = listOf(
        SpinnerItem("ageGroup", "19~24", "19~24"),
        SpinnerItem("ageGroup", "25~29", "25~29"),
        SpinnerItem("ageGroup", "30~34", "30~34"),
        SpinnerItem("ageGroup", "35~39", "35~39")
    )
    private val selectedAgeGroups: MutableSet<SpinnerItem> = HashSet()
    private val regions: List<SpinnerItem> = listOf(
        SpinnerItem("region", "busan", "부산"),
        SpinnerItem("region", "daegu", "대구"),
        SpinnerItem("region", "incheon", "인천"),
        SpinnerItem("region", "gwangju", "광주"),
        SpinnerItem("region", "daejeon", "대전"),
        SpinnerItem("region", "ulsan", "울산"),
        SpinnerItem("region", "gyeonggi", "경기"),
        SpinnerItem("region", "gangwon", "강원"),
        SpinnerItem("region", "northChungcheong", "충북"),
        SpinnerItem("region", "southChungcheong", "충남"),
        SpinnerItem("region", "northJeolla", "전북"),
        SpinnerItem("region", "southJeolla", "전남"),
        SpinnerItem("region", "northGyeongsang", "경북"),
        SpinnerItem("region", "southGyeongsang", "경남"),
        SpinnerItem("region", "jeju", "제주"),
        SpinnerItem("region", "sejong", "세종")
    )
    private val selectedRegions: MutableSet<SpinnerItem> = HashSet()
    private val seoulRegions: List<SpinnerItem> = listOf(
        SpinnerItem("seoulRegion", "jongro", " 종로구"),
        SpinnerItem("seoulRegion", "jung", " 중구"),
        SpinnerItem("seoulRegion", "yongsan", " 용산구"),
        SpinnerItem("seoulRegion", "seongdong", " 성동구"),
        SpinnerItem("seoulRegion", "gwangjin", " 광진구"),
        SpinnerItem("seoulRegion", "dongdaemun", " 동대문구"),
        SpinnerItem("seoulRegion", "jungnang", " 중랑구"),
        SpinnerItem("seoulRegion", "seongbuk", " 성북구"),
        SpinnerItem("seoulRegion", "gangbuk", " 강북구"),
        SpinnerItem("seoulRegion", "dobong", " 도봉구"),
        SpinnerItem("seoulRegion", "nowon", " 노원구"),
        SpinnerItem("seoulRegion", "eunpyeong", " 은평구"),
        SpinnerItem("seoulRegion", "seodaemun", " 서대문구"),
        SpinnerItem("seoulRegion", "mapo", " 마포구"),
        SpinnerItem("seoulRegion", "yangcheon", " 양천구"),
        SpinnerItem("seoulRegion", "gangseo", " 강서구"),
        SpinnerItem("seoulRegion", "guro", " 구로구"),
        SpinnerItem("seoulRegion", "geumcheon", " 금천구"),
        SpinnerItem("seoulRegion", "yeongdeungpo", " 영등포구"),
        SpinnerItem("seoulRegion", "dongjak", " 동작구"),
        SpinnerItem("seoulRegion", "gwanak", " 관악구"),
        SpinnerItem("seoulRegion", "seocho", " 서초구"),
        SpinnerItem("seoulRegion", "gangnam", " 강남구"),
        SpinnerItem("seoulRegion", "songpa", " 송파구"),
        SpinnerItem("seoulRegion", "gangdong", " 강동구")
    )
    private val selectedSeoulRegions: MutableSet<SpinnerItem> = HashSet()

    private val resultList: MutableList<Map<String, String>> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val recommendButton = findViewById<Button>(R.id.recommendButton)
        recommendButton.setOnClickListener {
            if (resultList.isEmpty()) {
                Toast.makeText(applicationContext, "추천할 결과가 없습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // 랜덤으로 하나의 아이템 선택
            val randomIndex = Random.nextInt(resultList.size)
            val randomItem = resultList[randomIndex]

            // pagekey 가져오기
            val pageKey = randomItem["pagekey"]
            if (pageKey != null) {
                // Intent를 통해 DetailActivity로 이동
                val intent = Intent(applicationContext, DetailActivity::class.java)
                intent.putExtra("pagekey", pageKey) // pagekey 전달
                startActivity(intent)
            } else {
                Toast.makeText(applicationContext, "선택된 항목에 pagekey가 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        val typeSpinner = findViewById<Spinner>(R.id.typeSpinner)
        val typeAdapter = CheckableSpinnerAdapter(this, "유형", types, selectedTypes)
        typeSpinner.adapter = typeAdapter

        val employmentTargetSpinner = findViewById<Spinner>(R.id.employmentTargetSpinner)
        val employmentTargetAdapter =
            CheckableSpinnerAdapter(this, "취업상태", employmentTargets, selectedEmploymentTargets)
        employmentTargetSpinner.adapter = employmentTargetAdapter

        val recruitmentStatusSpinner = findViewById<Spinner>(R.id.recruitmentStatusSpinner)
        val recruitmentStatusAdapter =
            CheckableSpinnerAdapter(this, "모집현황", recruitmentStatuses, selectedRecruitmentStatuses)
        recruitmentStatusSpinner.adapter = recruitmentStatusAdapter

        val ageGroupSpinner = findViewById<Spinner>(R.id.ageGroupSpinner)
        val ageGroupAdapter = CheckableSpinnerAdapter(this, "연령대", ageGroups, selectedAgeGroups)
        ageGroupSpinner.adapter = ageGroupAdapter

        val agencySpinner = findViewById<Spinner>(R.id.agencySpinner)

        val regionSpinner = findViewById<Spinner>(R.id.regionSpinner)
        val regionAdapter = CheckableSpinnerAdapter(this, "도/특별시", regions, selectedRegions)
        regionSpinner.adapter = regionAdapter
        regionSpinner.visibility = View.GONE

        val seoulRegionSpinner = findViewById<Spinner>(R.id.seoulRegionSpinner)
        val seoulRegionAdapter =
            CheckableSpinnerAdapter(this, "서울자치구", seoulRegions, selectedSeoulRegions)
        seoulRegionSpinner.adapter = seoulRegionAdapter
        seoulRegionSpinner.visibility = View.GONE

        agencySpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selected = parent?.getItemAtPosition(position) as String
                if ("서울자치구".equals(selected)) {
                    clearRegionSpinner(regionSpinner)
                    seoulRegionSpinner.visibility = View.VISIBLE
                } else if ("도/특별시".equals(selected)) {
                    regionSpinner.visibility = View.VISIBLE
                    clearSeoulRegionSpinner(seoulRegionSpinner)
                } else {
                    clearRegionSpinner(regionSpinner)
                    clearSeoulRegionSpinner(seoulRegionSpinner)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                clearRegionSpinner(regionSpinner)
                clearSeoulRegionSpinner(seoulRegionSpinner)
            }
        }

        val pageInput = findViewById<EditText>(R.id.editTextNumber)

        val client = OkHttpClient()
        val gson = Gson()
        val typeToken = object : TypeToken<List<Map<String, String>>>() {}
        val searchButton = findViewById<Button>(R.id.searchButton)
        val searchInput = findViewById<EditText>(R.id.editTextText)
        val resultLayout = findViewById<LinearLayout>(R.id.resultLayout)
        searchButton.setOnClickListener {
            val query: MutableMap<String, MutableList<String>> = HashMap()
            query.put("sw", mutableListOf(searchInput.text.toString()))
            query.put("pageIndex", mutableListOf(pageInput.text.toString()))
            for (type in selectedTypes) {
                val list = query.getOrPut(type.key!!) { ArrayList() }
                list.add(type.value!!)
            }
            for (employmentTarget in selectedEmploymentTargets) {
                val list = query.getOrPut(employmentTarget.key!!) { ArrayList() }
                list.add(employmentTarget.value!!)
            }
            for (recruitmentStatus in selectedRecruitmentStatuses) {
                val list = query.getOrPut(recruitmentStatus.key!!) { ArrayList() }
                list.add(recruitmentStatus.value!!)
            }
            for (ageGroups in selectedAgeGroups) {
                val list = query.getOrPut(ageGroups.key!!) { ArrayList() }
                list.add(ageGroups.value!!)
            }
            val agency = agencySpinner.selectedItem as String
            if ("서울시".equals(agency)) {
                query.put("agency", mutableListOf("seoulSi"))
            } else if ("서울자치구".equals(agency)) {
                query.put("agency", mutableListOf("seoulGu"))
            } else if ("중앙정부".equals(agency)) {
                query.put("central", mutableListOf("central"))
            } else if ("도/특별시".equals(agency)) {
                query.remove("central")
                query.remove("agency")
            }
            for (region in selectedRegions) {
                val list = query.getOrPut(region.key!!) { ArrayList() }
                list.add(region.value!!)
            }
            for (seoulRegion in selectedSeoulRegions) {
                val list = query.getOrPut(seoulRegion.key!!) { ArrayList() }
                list.add(seoulRegion.value!!)
            }

            val request = Request.Builder().url("${SERVER_IP}/search").post(
                gson.toJson(query).toRequestBody("application/json; charset=utf-8".toMediaType())
            ).build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (response.isSuccessful && response.code != 204) {
                            val received = response.body!!.string()
                            Log.i("myjson", received)
                            val result = gson.fromJson(received, typeToken)
                            resultList.clear() // 이전 결과 초기화
                            resultList.addAll(result)
                            CoroutineScope(Dispatchers.Main).launch {
                                resultLayout.removeAllViews()
                                for (item in result) {
                                    val viewItem =
                                        View.inflate(applicationContext, R.layout.result_item, null)
                                    Log.i("myjson", item.toString())
                                    viewItem.findViewById<TextView>(R.id.resultItemTitleText).text =
                                        item["title"]
                                    viewItem.findViewById<TextView>(R.id.resultItemContentText).text =
                                        item["desc"]
                                    viewItem.setOnClickListener {
                                        val intent = Intent(applicationContext, DetailActivity::class.java)
                                        intent.putExtra("pagekey", item["pagekey"]) // pagekey 전달
                                        startActivity(intent)
                                    }

                                    resultLayout.addView(viewItem)
                                }
                            }
                        } else {
                            CoroutineScope(Dispatchers.Main).launch {
                                Toast.makeText(
                                    applicationContext,
                                    "Network Error",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            })


        }
        pageInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                searchButton.callOnClick()
            }
        }
        findViewById<ImageButton>(R.id.subPageButton).setOnClickListener {
            var value = pageInput.text.toString().toIntOrNull()
            if (value == null || value <= 1) {
                value = 1
            } else value -= 1
            pageInput.setText(value.toString())
            searchButton.callOnClick()
        }
        findViewById<ImageButton>(R.id.addPageButton).setOnClickListener {
            var value1 = pageInput.text.toString().toIntOrNull()
            if (value1 == null || value1 < 1) {
                value1 = 1
            } else value1 += 1
            pageInput.setText(value1.toString())
            searchButton.callOnClick()
        }

    }

    fun clearRegionSpinner(spinner: Spinner) {
        spinner.visibility = View.GONE
        selectedRegions.clear()
        for (child in spinner.children) {
            child.findViewById<CheckBox>(R.id.checkbox).isChecked = false
        }
    }

    fun clearSeoulRegionSpinner(spinner: Spinner) {
        spinner.visibility = View.GONE
        selectedSeoulRegions.clear()
        for (child in spinner.children) {
            child.findViewById<CheckBox>(R.id.checkbox).isChecked = false
        }
    }

}