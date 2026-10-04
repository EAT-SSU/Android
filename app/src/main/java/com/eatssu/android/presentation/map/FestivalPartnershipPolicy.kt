package com.eatssu.android.presentation.map

import com.eatssu.android.domain.model.Partnership
import com.eatssu.common.enums.PeriodType
import java.time.LocalDate

private val FESTIVAL_START_DATE: LocalDate = LocalDate.of(2026, 9, 14)
private val FESTIVAL_END_DATE: LocalDate = LocalDate.of(2026, 9, 16)

internal fun isFestivalPartnershipPeriod(date: LocalDate): Boolean =
    !date.isBefore(FESTIVAL_START_DATE) && !date.isAfter(FESTIVAL_END_DATE)

internal fun activeFestivalPartnerships(
    partnerships: List<Partnership>,
    date: LocalDate,
): List<Partnership> {
    if (!isFestivalPartnershipPeriod(date)) return emptyList()

    return partnerships.mapNotNull { partnership ->
        val activeFestivalInfos = partnership.partnershipInfos.filter { info ->
            info.periodType == PeriodType.FESTIVAL && info.isVisibleOn(date)
        }

        partnership
            .takeIf { activeFestivalInfos.isNotEmpty() }
            ?.copy(partnershipInfos = activeFestivalInfos)
    }
}

internal fun mergePartnerships(
    userPartnerships: List<Partnership>,
    festivalPartnerships: List<Partnership>,
): List<Partnership> {
    val mergedByStoreName = linkedMapOf<String, Partnership>()

    (userPartnerships + festivalPartnerships).forEach { partnership ->
        val previous = mergedByStoreName[partnership.storeName]
        mergedByStoreName[partnership.storeName] = if (previous == null) {
            partnership
        } else {
            previous.copy(
                partnershipInfos = (previous.partnershipInfos + partnership.partnershipInfos)
                    .distinctBy(Partnership.PartnershipInfo::id),
                naverMapUrl = previous.naverMapUrl ?: partnership.naverMapUrl,
                kakaoMapUrl = previous.kakaoMapUrl ?: partnership.kakaoMapUrl,
            )
        }
    }

    return mergedByStoreName.values.toList()
}

internal val Partnership.hasFestivalPartnership: Boolean
    get() = partnershipInfos.any { it.periodType == PeriodType.FESTIVAL }

private fun Partnership.PartnershipInfo.isVisibleOn(date: LocalDate): Boolean {
    val start = startDate.toLocalDateOrNull() ?: return false
    val end = endDate.toLocalDateOrNull() ?: return false
    if (start.isAfter(FESTIVAL_END_DATE) || end.isBefore(FESTIVAL_START_DATE)) return false

    val visibleStart = minOf(start, FESTIVAL_START_DATE)
    return !date.isBefore(visibleStart) && !date.isAfter(end)
}

private fun String.toLocalDateOrNull(): LocalDate? = runCatching(LocalDate::parse).getOrNull()
