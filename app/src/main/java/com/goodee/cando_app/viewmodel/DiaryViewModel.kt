package com.goodee.cando_app.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.goodee.cando_app.dto.DiaryDto
import com.goodee.cando_app.model.DiaryRepository
import com.goodee.cando_app.model.UserRepository

class DiaryViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG: String = "로그"
    private var appRepository: DiaryRepository
    private val _diaryListLiveData: MutableLiveData<List<DiaryDto>>
    val diaryListLiveData: LiveData<List<DiaryDto>>
        get() = _diaryListLiveData
    private val _diaryLiveData: MutableLiveData<DiaryDto>
    val diaryLiveData: LiveData<DiaryDto>
        get() = _diaryLiveData

    init {
        Log.d(TAG,"DiaryViewModel - init called")
        appRepository = DiaryRepository(application)
        _diaryListLiveData = appRepository.diaryListLiveData as MutableLiveData<List<DiaryDto>>
        _diaryLiveData = appRepository.diaryLiveData as MutableLiveData<DiaryDto>
    }

    // 모든 게시글 불러오기
    fun getDiaryList() {
        appRepository.getDiaryList()
    }
    // 게시글 1개 가져오기
    fun getDiary(dno: String = "1") {
        appRepository.getDiary(dno)
    }
    // 글 작성하기
    fun writeDiary(diaryDto: DiaryDto) {
        appRepository.writeDiary(diaryDto)
    }

    fun editDiary(diaryDto: DiaryDto) {
        appRepository.editDiary(diaryDto)
    }

    fun deleteDiary(dno: String) {
        appRepository.deleteDiary(dno)
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG,"DiaryViewModel - onCleared() called")
    }
}
