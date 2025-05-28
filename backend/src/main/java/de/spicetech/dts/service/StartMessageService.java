package de.spicetech.dts.service;

import static de.spicetech.dts.jooq.tables.StartMessage.START_MESSAGE;

import java.time.LocalDate;
import java.time.ZoneId;

import org.jooq.DSLContext;
import org.jooq.Record2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.node.ObjectNode;

import de.spicetech.jooq.JooqJacksonConverter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StartMessageService {

  @Autowired
  DSLContext dsl;

  public ObjectNode readCurrentStartMessage() {
    LocalDate berlinDate = LocalDate.now(ZoneId.of("Europe/Berlin"));

    Record2<String, String> startMessage = dsl.select(START_MESSAGE.TITLE, START_MESSAGE.DESCRIPTION)
        .from(START_MESSAGE)
        .where(START_MESSAGE.BEGIN.le(berlinDate)).and(START_MESSAGE.END.gt(berlinDate))
        .fetchAny();

    return JooqJacksonConverter.record2ObjectNode(startMessage);
  }

}
