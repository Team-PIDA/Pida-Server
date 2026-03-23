package com.pida.category.item.support

import com.pida.category.CategoryLabel
import java.time.Clock
import java.time.LocalDate

object MapCategoryItemsTitleBuilder {
    fun build(
        categoryLabel: CategoryLabel,
        count: Int,
        clock: Clock = Clock.systemDefaultZone(),
    ): String =
        when (categoryLabel) {
            CategoryLabel.EVENT -> "${LocalDate.now(clock).year} 벚꽃 축제 ${count}곳"
            CategoryLabel.CAFE -> "주변에 벚꽃 뷰 카페 ${count}곳을 찾았어요"
            CategoryLabel.FLOWER_SPOT -> "주변에 걷기 좋은 산책로 ${count}곳이 있어요"
        }
}
