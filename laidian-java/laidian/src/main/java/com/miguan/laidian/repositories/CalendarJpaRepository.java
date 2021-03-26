package com.miguan.laidian.repositories;

import com.miguan.laidian.entity.Calendar;
import com.miguan.laidian.redis.util.CacheConstant;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CalendarJpaRepository extends JpaRepository<Calendar, Long> {

    @Cacheable(value = CacheConstant.CALENDAR, unless = "#result == null")
    @Query(value = "select * from calendar where TO_DAYS(`day`) = TO_DAYS(NOW())  ", nativeQuery = true)
    Calendar queryCalendarToday();

}
