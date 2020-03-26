package com.chillimport.server.parser;

import com.chillimport.server.Cell;
import com.chillimport.server.TableDataTypes;
import com.chillimport.server.config.Configuration;
import com.chillimport.server.config.StringColumn;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class TimeParser {

  /**
   * Returns the ZonedDateTime time for observations in a row of a table.
   * Creates this from the elements of the row relevant for the time as
   * specified in the configuration.
   *
   * @param cfg the configuration
   * @param row the row as list of Cells
   *
   * @return the ZonedDateTime
   */
  public static ZonedDateTime toZonedDateTime(Configuration cfg,
                                              ArrayList<Cell> row)
      throws NullPointerException, DateTimeException, IllegalArgumentException {
    if (cfg == null) {
      throw new NullPointerException("Configuration is null.");
    }
    if (row == null) {
      throw new NullPointerException("Row is null.");
    }

    ZonedDateTime zdt;
    StringColumn[] columns = cfg.getDateTime();

    if (columns.length == 1 &&
        row.get(columns[0].getColumn()).getCellType() == TableDataTypes.DATE) {
      return ZonedDateTime.ofInstant(
          row.get(columns[0].getColumn()).toDate().toInstant(),
          toZoneId(cfg.getTimezone()));
    }

    StringBuilder time = new StringBuilder();
    StringBuilder regex = new StringBuilder();

    for (StringColumn item : columns) {
      time.append(row.get(item.getColumn()).toString());
      regex.append(item.getString());
    }

    DateTimeFormatter formatter =
        new DateTimeFormatterBuilder()
            .appendPattern(regex.toString())
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
            .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
            .parseDefaulting(ChronoField.YEAR_OF_ERA, 1)
            .toFormatter();

    if (Pattern.matches(".*VV.*", regex)) {
      formatter.withZone(ZoneId.systemDefault());
      zdt = ZonedDateTime.parse(time.toString(), formatter);
    } else {
      String zone = cfg.getTimezone();
      zdt = ZonedDateTime.of(LocalDateTime.parse(time.toString(), formatter),
                             toZoneId(zone));
    }

    return zdt;
  }

  /**
   * Converts String to ZoneId.
   *
   * @param zone the zone as String
   *
   * @return the ZoneId
   *
   * @throws DateTimeException if String is not valid
   */
  private static ZoneId toZoneId(String zone)
      throws DateTimeException, NullPointerException {
    if (zone == null) {
      throw new NullPointerException("No timezone specified.");
    }

    if (!zone.startsWith("-")) {
      zone = "+" + zone;
    }
    return ZoneOffset.of(zone);
  }
}
