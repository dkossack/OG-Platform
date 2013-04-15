/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.credit.schedulegeneration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;

import org.threeten.bp.LocalDate;
import org.threeten.bp.ZonedDateTime;

import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.CompareUtils;

/**
 * 
 */
public final class ScheduleUtils {

  private ScheduleUtils() {
  }

  public static ZonedDateTime[] getTruncatedTimeLine(final ZonedDateTime[] allDates, final ZonedDateTime startDate, final ZonedDateTime endDate,
      final boolean sorted) {
    ArgumentChecker.notNull(allDates, "all dates");
    ArgumentChecker.notNull(startDate, "start date");
    ArgumentChecker.notNull(endDate, "end date");
    ArgumentChecker.isTrue(startDate.isBefore(endDate), "Start date {} must be before end date {}", startDate, endDate);
    final int n = allDates.length;
    if (n == 0) {
      return new ZonedDateTime[] {startDate, endDate};
    }
    final HashSet<ZonedDateTime> truncated = new LinkedHashSet<>(n + 2, 1);
    final LocalDate startDateAsLocal = startDate.toLocalDate();
    final LocalDate endDateAsLocal = endDate.toLocalDate();
    for (final ZonedDateTime date : allDates) {
      final LocalDate localDate = date.toLocalDate();
      if (!(localDate.isBefore(startDateAsLocal) || localDate.isAfter(endDateAsLocal))) {
        truncated.add(date);
      }
    }
    final int truncatedSize = truncated.size();
    if (truncatedSize == 0) {
      return new ZonedDateTime[] {startDate, endDate };
    }
    final ZonedDateTime[] truncatedArray = truncated.toArray(new ZonedDateTime[truncatedSize]);
    if (!sorted) {
      Arrays.sort(truncatedArray);
    }
    if (truncatedArray[0].equals(startDate)) {
      if (truncatedArray[truncatedSize - 1].equals(endDate)) {
        return truncatedArray;
      }
      final ZonedDateTime[] result = new ZonedDateTime[truncatedSize + 1];
      System.arraycopy(truncatedArray, 0, result, 0, truncatedSize);
      result[truncatedSize] = endDate;
      return result;
    }
    if (truncatedArray[truncatedSize - 1].equals(endDate)) {
      final ZonedDateTime[] result = new ZonedDateTime[truncatedSize + 1];
      System.arraycopy(truncatedArray, 0, result, 1, truncatedSize);
      result[0] = startDate;
      return result;
    }
    final ZonedDateTime[] result = new ZonedDateTime[truncatedSize + 2];
    System.arraycopy(truncatedArray, 0, result, 1, truncatedSize);
    result[0] = startDate;
    result[truncatedSize + 1] = endDate;
    return result;
  }

  public static Double[] getTruncatedTimeLine(final double[] allTimes, final double startTime, final double endTime, final boolean sorted,
      final double tolerance) {
    ArgumentChecker.notNull(allTimes, "all dates");
    ArgumentChecker.isTrue(startTime < endTime, "Start time {} must be before end time {}", startTime, endTime);
    final int n = allTimes.length;
    if (n == 0) {
      return new Double[] {startTime, endTime};
    }
    final HashSet<Double> truncated = new LinkedHashSet<>(n + 2, 1);
    for (final Double time : allTimes) {
      if (time > startTime && time < endTime) {
        truncated.add(time);
      }
    }
    final int truncatedSize = truncated.size();
    if (truncatedSize == 0) {
      return new Double[] {startTime, endTime};
    }
    final Double[] truncatedArray = truncated.toArray(new Double[truncatedSize]);
    if (!sorted) {
      Arrays.sort(truncatedArray);
    }
    if (CompareUtils.closeEquals(truncatedArray[0], startTime, tolerance)) {
      if (CompareUtils.closeEquals(truncatedArray[truncatedSize - 1], endTime, tolerance)) {
        return truncatedArray;
      }
      final Double[] result = new Double[truncatedSize + 1];
      System.arraycopy(truncatedArray, 0, result, 0, truncatedSize);
      result[truncatedSize] = endTime;
      return result;
    }
    if (CompareUtils.closeEquals(truncatedArray[truncatedSize - 1], endTime, tolerance)) {
      final Double[] result = new Double[truncatedSize + 1];
      System.arraycopy(truncatedArray, 0, result, 1, truncatedSize);
      result[0] = startTime;
      return result;
    }
    final Double[] result = new Double[truncatedSize + 2];
    System.arraycopy(truncatedArray, 0, result, 1, truncatedSize);
    result[0] = startTime;
    result[truncatedSize + 1] = endTime;
    return result;
  }
}