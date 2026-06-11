package de.spicetech.dts.service;

import static de.spicetech.dts.jooq.tables.LemmaType.LEMMA_TYPE;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.node.ArrayNode;

import de.spicetech.dts.jooq.tables.records.LemmaTypeRecord;
import de.spicetech.jooq.JooqJacksonConverter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LemmaTypeService {

  @Autowired
  DSLContext dsl;

  public ArrayNode getLemmaTypes() {
    Result<LemmaTypeRecord> lemmaTypeResult = dsl.selectFrom(LEMMA_TYPE).fetch();
    return JooqJacksonConverter.result2ArrayNode(lemmaTypeResult);
  }

}
