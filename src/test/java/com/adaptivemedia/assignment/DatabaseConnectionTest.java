package com.adaptivemedia.assignment;

import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static com.adaptivemedia.assignment.jooq.Tables.FETCH_LOG;
import static org.assertj.core.api.Assertions.assertThat;

public class DatabaseConnectionTest extends BaseIntegrationTest {

    @Autowired
    private DSLContext dslContext;

    @Test
    void contextLoads() {
    }

    @Test
    public void testDatabaseConnection() {
        var result = dslContext.fetchOne("SELECT 1");
        assertThat(result.getValue(0)).isEqualTo(1);
    }

    @Test
    void testReadFetchLogRow() {
        var record = dslContext.selectFrom(FETCH_LOG)
                               .fetchOne();

        assertThat(record).isNotNull();

        assertThat(record.getFetchDate()).isEqualTo(LocalDate.of(2025, 8, 1));
        assertThat(record.getId()).isNotNull();
        assertThat(record.getId()).isGreaterThan(0L);
        System.out.println("Found row: id=" + record.getId() + ", fetch_date=" + record.getFetchDate());
    }

    @Test
    void testCountFetchLogRows() {
        Integer count = dslContext.selectCount()
                                  .from(FETCH_LOG)
                                  .fetchOne(0, int.class);

        assertThat(count).isNotNull();
        assertThat(count).isEqualTo(1);
        System.out.println("fetch_log table has " + count + " row(s)");
    }
}
