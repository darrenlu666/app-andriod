package com.codyy.erpsportal.statistics.models.entities;

import android.support.annotation.IntDef;

import static com.codyy.erpsportal.statistics.models.entities.StatFilterBy.BY_MONTH;
import static com.codyy.erpsportal.statistics.models.entities.StatFilterBy.BY_SPECIFIC_DATE;
import static com.codyy.erpsportal.statistics.models.entities.StatFilterBy.BY_TERM;
import static com.codyy.erpsportal.statistics.models.entities.StatFilterBy.BY_WEEK;

/**
 * 统计筛选类型限制
 * Created by gujiajia on 2017/4/15.
 */
@IntDef({BY_WEEK, BY_MONTH, BY_SPECIFIC_DATE, BY_TERM})
public @interface StatFilterBy {
    int BY_WEEK = 1;
    int BY_MONTH = 2;
    int BY_SPECIFIC_DATE = 3;
    int BY_TERM = 4;
}
