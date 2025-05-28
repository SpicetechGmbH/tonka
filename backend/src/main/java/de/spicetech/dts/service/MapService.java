package de.spicetech.dts.service;

import static de.spicetech.dts.jooq.tables.Map.MAP;
import static de.spicetech.dts.jooq.tables.MapType.MAP_TYPE;

import java.math.BigDecimal;

import org.jooq.DSLContext;
import org.jooq.Record18;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.node.ArrayNode;

import de.spicetech.jooq.JooqJacksonConverter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MapService {

  @Autowired
  DSLContext dsl;

  public ArrayNode getMaps(String mapType) {

    Result<Record18<String, String, String, String, Integer, String, String, Integer, Integer, Integer, String, String, String, BigDecimal, Float, Float, Float, Float>> maps = dsl
        .select(MAP.ID, MAP_TYPE.MAP_TYPE_, MAP.URL, MAP.LAYER, MAP.SUBLAYER, MAP.SERVICE, MAP.TIMELINE_TITLE,
            MAP.TIMELINE_DATE_DAY,
            MAP.TIMELINE_DATE_MONTH, MAP.TIMELINE_DATE_YEAR, MAP.TIMELINE_DATE_LABEL, MAP.MAP_DESCRIPTION,
            MAP.ARCHIVE_SIGNATURE, MAP.ANGLE, MAP.X_MIN, MAP.Y_MIN, MAP.X_MAX, MAP.Y_MAX)
        .from(MAP)
        .innerJoin(MAP_TYPE).on(MAP.MAP_TYPE_ID.eq(MAP_TYPE.ID))
        .where(MAP_TYPE.MAP_TYPE_.eq(mapType))
        .orderBy(MAP_TYPE.MAP_TYPE_, MAP.TIMELINE_DATE_YEAR)
        .fetch();

    return JooqJacksonConverter.result2ArrayNode(maps);
  }

}
