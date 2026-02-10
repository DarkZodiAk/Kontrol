package com.darkzodiak.kontrol.statistics.presentation

sealed interface StatisticsAction {
    object ShowPreviousWeek: StatisticsAction
    object ShowNextWeek: StatisticsAction
    object BackToCurrentWeek: StatisticsAction
    data class ChangeDay(val reportIndex: Int): StatisticsAction
    object ReturnedToScreen: StatisticsAction
}