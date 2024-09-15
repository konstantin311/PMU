package com.example.pmu1

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class NewsItem(
    val id: Int,
    val content: String,
    var likeCount: Int = 0
)

class NewsViewModel : ViewModel() {
    private val allNews = listOf(
        NewsItem(1, "Срочные новости: Рынок достиг рекордного максимума"),
        NewsItem(2, "Прогноз погоды: Завтра ожидается сильный дождь"),
        NewsItem(3, "Спорт: Местная команда выигрывает чемпионат"),
        NewsItem(4, "Технологии: Выпущен новый смартфон"),
        NewsItem(5, "Развлечения: Премьера нового фильма состоится на следующей неделе"),
        NewsItem(6, "Здоровье: Новое исследование о пользе питания"),
        NewsItem(7, "Путешествия: Лучшие направления на 2024 год"),
        NewsItem(8, "Финансы: Советы по разумному инвестированию"),
        NewsItem(9, "Образование: Новые инструменты для обучения студентов"),
        NewsItem(10, "Наука: Открытие новой планеты")
    )

    private val likeCounts = mutableMapOf<Int, Int>().withDefault { 0 }

    val currentNews = mutableStateOf<List<NewsItem>>(getUniqueNews())

    init {
        startNewsRotation()
    }

    private fun startNewsRotation() {
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                delay(5000)
                updateNews()
            }
        }
    }

    private fun updateNews() {
        currentNews.value = getUniqueNews()
    }

    private fun getUniqueNews(): List<NewsItem> {
        // Выбираем 4 уникальные новости
        val shuffledNews = allNews.shuffled()
        return shuffledNews.take(4)
    }

    fun likeNews(newsItem: NewsItem) {
        val currentLikes = likeCounts.getValue(newsItem.id)
        likeCounts[newsItem.id] = currentLikes + 1
        currentNews.value = currentNews.value.map {
            if (it.id == newsItem.id) {
                it.copy(likeCount = currentLikes + 1) // Обновляем количество лайков в новостях
            } else {
                it
            }
        }
    }

    fun getLikeCount(newsItem: NewsItem): Int {
        return likeCounts.getValue(newsItem.id)
    }
}