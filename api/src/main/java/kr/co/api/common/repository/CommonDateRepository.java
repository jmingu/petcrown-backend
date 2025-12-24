package kr.co.api.common.repository;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
@RequiredArgsConstructor
public class CommonDateRepository {

    private final DSLContext dsl;
    /**
     * 현재 주의 weekStartDate 조회 (date_trunc 사용)
     */
    public LocalDate selectCurrentWeekStartDate() {
        Field<LocalDate> weekStart =
                DSL.field("date_trunc('week', current_date)::date", LocalDate.class);

        return dsl.select(weekStart).fetchOne(weekStart);
    }

    /**
     * 지난주의 weekStartDate 조회 (date_trunc 사용)
     */
    public LocalDate selectLastWeekStartDate() {
        Field<LocalDate> lastWeekStart =
                DSL.field(
                        "date_trunc('week', {0} - interval '7 days')::date",
                        LocalDate.class,
                        DSL.currentDate()
                );

        return dsl.select(lastWeekStart).fetchOne(lastWeekStart);
    }
}
