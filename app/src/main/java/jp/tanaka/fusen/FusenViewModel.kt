package jp.tanaka.fusen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FusenViewModel(private val repository: FusenRepository): ViewModel() {
    // RepositoryのFlowをStateFlowに変換する
    // これでUIは常に「最新の状態」だけを知っていればよくなるでやんす。
    // らしい。
    val memoState: StateFlow<String> = repository.memoFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000), // UIが消えて5秒後に監視を停止
        initialValue = "" // 読み込み完了までの初期値
    )

    fun onNumberChanged(newNumber: String){
        // 数字かどうかのチェック
        if (newNumber.all { it.isDigit()}){
            viewModelScope.launch {
                repository.saveMemo(newNumber)
            }
        }
    }
}