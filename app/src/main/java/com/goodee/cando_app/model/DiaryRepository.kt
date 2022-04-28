package com.goodee.cando_app.model

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.goodee.cando_app.R
import com.goodee.cando_app.database.FireStoreDatabase
import com.goodee.cando_app.database.RealTimeDatabase
import com.goodee.cando_app.dto.DiaryDto
import com.google.firebase.Timestamp
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

const val COLLECTION_NAME = "diary"
class DiaryRepository(val application: Application) {
    private val TAG: String = "로그"
    private val _diaryListLiveData: MutableLiveData<List<DiaryDto>> = MutableLiveData()
    val diaryListLiveData: LiveData<List<DiaryDto>>
        get() = _diaryListLiveData
    private val _diaryLiveData: MutableLiveData<DiaryDto> = MutableLiveData()
    val diaryLiveData: LiveData<DiaryDto>
        get() = _diaryLiveData

    // 게시글 조회(게시글 클릭 시 1개의 게시글을 읽음)
    fun getDiary(dno: String) {
        Log.d(TAG,"AppRepository - getDiary($dno) called")
        FireStoreDatabase.getDatabase().collection(COLLECTION_NAME).document(dno).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                task.result?.let {  result ->
                    val title = result["title"]
                    val content = result["content"]
                    val author = result["author"]
                    val date: Timestamp = result["date"] as Timestamp
                    _diaryLiveData.value = DiaryDto(dno = dno, title = title as String, content = content as String, author = author as String, date = date.toDate())
                }
            } else {
                Toast.makeText(application.applicationContext, application.applicationContext.getString(R.string.toast_diary_read_fail), Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 게시글 목록 가져오기(로그인시 바로 보이는 게시글들)
    fun getDiaryList() {
        Log.d(TAG,"AppRepository - getDiaryList() called")
        val database = FireStoreDatabase.getDatabase()
        database.collection(COLLECTION_NAME).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val result = task.result
                val diaryList = ArrayList<DiaryDto>()
                if (result != null) {
                    result.documents.forEach { diary ->
                        val diaryDto = diary.toObject(DiaryDto::class.java)
                        if (diaryDto != null) {
                            diaryDto.dno = diary.id
                            diaryList.add(diaryDto)
                        }
                    }
                    _diaryListLiveData.postValue(diaryList)
                }
            } else {
                Toast.makeText(application.applicationContext, application.applicationContext.getString(R.string.toast_diarys_read_fail), Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 게시글 작성
    fun writeDiary(diaryDto: DiaryDto) {
        Log.d(TAG,"AppRepository - writeDiary() called")
        val diaryRef = RealTimeDatabase.getDatabase().child("Diary").ref
        val key = diaryRef.push().key
        val lastDno = diaryRef.limitToLast(1).get().addOnCompleteListener {
            Log.d(TAG,"AppRepository - it : ${it.result}")
        }
        if (!lastDno.isSuccessful) {
            Log.d(TAG,"AppRepository - diaryRef.limitToLast(1).get().isSuccessful : false")
            return
        }
        Log.d(TAG,"AppRepository - lastDno : ${lastDno.result}")

        val diary = HashMap<String, DiaryDto>()
        key?.let {
            diary.put(key, diaryDto)
        }

        val firebaseDatabase = RealTimeDatabase.getDatabase()
        firebaseDatabase.child("Diary/${key}").setValue(diaryDto).addOnCompleteListener { task ->       // Diary/${key}에 글 저장하기
            Log.d(TAG,"AppRepository - task.isSuccessful : ${task.isSuccessful}")
            if (task.isSuccessful) Toast.makeText(application, "글 작성 성공", Toast.LENGTH_SHORT).show()
            else Toast.makeText(application, "글 작성 실패", Toast.LENGTH_SHORT).show()
        }
    }

    // 게시글 수정하기
    fun editDiary(diaryDto: DiaryDto) {
        Log.d(TAG,"AppRepository - editDiary() called")
        val cloudStore = FireStoreDatabase.getDatabase()

        cloudStore.collection(COLLECTION_NAME).document(diaryDto.dno).set(diaryDto).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG,"AppRepository - 글 수정 성공")
                _diaryLiveData.postValue(diaryDto)
            } else {
                Log.d(TAG,"AppRepository - 글 수정 실패")
            }
        }
    }

    fun deleteDiary(dno: String) {
        Log.d(TAG,"AppRepository - deleteDiary() called")
        val cloudStore = FireStoreDatabase.getDatabase()
        cloudStore.collection(COLLECTION_NAME).document(dno).delete().addOnCompleteListener { task ->
            Log.d(TAG,"AppRepository - task.isSuccessful : ${task.isSuccessful}")
            _diaryLiveData.postValue(null)
        }
    }
}