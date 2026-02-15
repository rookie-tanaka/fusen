package jp.tanaka.fusen

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// DataStoreのインスタンスを定義
// ファイル名は"settings"などが一般的らしい。
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "fusen_settings")

// データ層
class FusenRepository(private val dataStore: DataStore<Preferences>) {
    // 保存するキーを定義
    private val MEMO_KEY = stringPreferencesKey("memo_number")

    // Flowで読み込み
    // DataStoreはFlowを返すため、リアルタイムに更新を監視できる
    val memoFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[MEMO_KEY] ?: ""
    }

    // Suspend functionで書き込み
    // 書き込みは非同期なので、suspendをつける。
    suspend fun saveMemo(number: String) {
        dataStore.edit { preferences ->
            preferences[MEMO_KEY] = number
        }
    }
}