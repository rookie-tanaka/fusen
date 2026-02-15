package jp.tanaka.fusen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.temporal.ChronoUnit

// UIに渡すための状態クラス
data class FusenUiState(
    val number: String = "",
    val daysPassedText: String = ""
)

class FusenViewModel(private val repository: FusenRepository): ViewModel() {
    // RepositoryのFlowをStateFlowに変換する
    // これでUIは常に「最新の状態」だけを知っていればよくなるでやんす。
    // らしい。
    @RequiresApi(Build.VERSION_CODES.O)
    val uiState: StateFlow<FusenUiState> = repository.memoFlow
        .map{ data ->
            val daysText = if (data.lastUpdated == 0L) {
                "さあ、付箋を挟みましょう！"
            } else {
                val diff = calculateDaysPassed(data.lastUpdated)
                "${diff}日経過"
            }
            FusenUiState(data.number, daysText)
        }
        .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000), // UIが消えて5秒後に監視を停止
        initialValue = FusenUiState()
    )

    fun onNumberChanged(newNumber: String){
        // 数字かどうかのチェック
        if (newNumber.all { it.isDigit()}){
            viewModelScope.launch {
                repository.saveMemo(newNumber)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateDaysPassed(savedTime: Long): Long {
        val savedInstant = Instant.ofEpochMilli(savedTime)
        val nowInstant = Instant.now()

        return ChronoUnit.DAYS.between(savedInstant, nowInstant)

    }
}