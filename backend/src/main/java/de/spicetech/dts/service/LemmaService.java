package de.spicetech.dts.service;

import static de.spicetech.dts.jooq.tables.Illustration.ILLUSTRATION;
import static de.spicetech.dts.jooq.tables.Keyword.KEYWORD;
import static de.spicetech.dts.jooq.tables.Lemma.LEMMA;
import static de.spicetech.dts.jooq.tables.LemmaSync.LEMMA_SYNC;
import static de.spicetech.dts.jooq.tables.LemmaType.LEMMA_TYPE;
import static de.spicetech.dts.jooq.tables.LemmaVersion.LEMMA_VERSION;
import static de.spicetech.dts.jooq.tables.Location.LOCATION;
import static de.spicetech.dts.jooq.tables.NmLemmaIllustration.NM_LEMMA_ILLUSTRATION;
import static de.spicetech.dts.jooq.tables.NmLemmaKeyword.NM_LEMMA_KEYWORD;
import static de.spicetech.dts.jooq.tables.NmLemmaLocation.NM_LEMMA_LOCATION;
import static de.spicetech.dts.jooq.tables.Person.PERSON;
import static de.spicetech.dts.jooq.tables.Place.PLACE;
import static de.spicetech.dts.jooq.tables.ViewLemma.VIEW_LEMMA;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record11;
import org.jooq.Record15;
import org.jooq.Record17;
import org.jooq.Record2;
import org.jooq.Record8;
import org.jooq.Result;
import org.jooq.exception.NoDataFoundException;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.spicetech.dts.jooq.tables.ViewLemma;
import de.spicetech.dts.util.Toolbox;
import de.spicetech.jooq.JooqJacksonConverter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LemmaService {

  @Autowired
  DSLContext dsl;

  @Autowired
  Toolbox toolbox;

  public ArrayNode getAllLemmata() {
    ViewLemma viewLemma1 = VIEW_LEMMA.as("view_lemma_1");
    ViewLemma viewLemma2 = VIEW_LEMMA.as("view_lemma_2");

    Result<Record8<String, String, String, Boolean, Integer, Integer, String, String>> allLemmata = dsl
        .select(viewLemma1.ID, viewLemma1.TITLE, viewLemma1.TIMELINE_TITLE, viewLemma1.FEATURED,
            LOCATION.UTM_COORD_E,
            LOCATION.UTM_COORD_N,
            LEMMA_TYPE.LEMMA_TYPE_, ILLUSTRATION.THUMBNAIL_FILE_NAME)
        .from(viewLemma1)
        .innerJoin(NM_LEMMA_LOCATION).on(viewLemma1.ID.eq(NM_LEMMA_LOCATION.LEMMA_ID))
        .innerJoin(LOCATION).on(NM_LEMMA_LOCATION.LOCATION_ID.eq(LOCATION.ID))
        .innerJoin(LEMMA_TYPE).on(viewLemma1.LEMMA_TYPE_ID.eq(LEMMA_TYPE.ID))
        .innerJoin(NM_LEMMA_ILLUSTRATION).on(viewLemma1.ID.eq(NM_LEMMA_ILLUSTRATION.LEMMA_ID))
        .and(NM_LEMMA_ILLUSTRATION.NR.eq(1))
        .innerJoin(ILLUSTRATION).on(NM_LEMMA_ILLUSTRATION.ILLUSTRATION_ID.eq(ILLUSTRATION.ID))
        .where(NM_LEMMA_LOCATION.MAIN_LOCATION.eq(1))
        .and(viewLemma1.VERSION.eq(
            dsl.select(DSL.max(viewLemma2.VERSION)).from(viewLemma2)
                .where(viewLemma2.ID.eq(viewLemma1.ID))))
        .fetch();

    return JooqJacksonConverter.result2ArrayNode(allLemmata);
  }

  public ArrayNode getAllLemmataForList() {
    ViewLemma viewLemma1 = VIEW_LEMMA.as("view_lemma_1");
    ViewLemma viewLemma2 = VIEW_LEMMA.as("view_lemma_2");

    Result<Record17<String, String, String, String, String, String, Integer, Integer, Integer, String, String, String, Integer, Integer, String, String, String>> lemmata = dsl
        .selectDistinct(DSL.coalesce(PERSON.FULL_NAME, viewLemma1.TITLE).as("ordering"), LEMMA_TYPE.LEMMA_TYPE_,
            viewLemma1.ID, viewLemma1.TITLE, viewLemma1.ABSTRACT, viewLemma1.TIMELINE_TITLE,
            viewLemma1.TIMELINE_DATE_DAY, viewLemma1.TIMELINE_DATE_MONTH, viewLemma1.TIMELINE_DATE_YEAR,
            viewLemma1.TIMELINE_DATE_LABEL, viewLemma1.TIMELINE_DATE_RELEVANCE, viewLemma1.AUTHOR_NAME,
            LOCATION.UTM_COORD_E, LOCATION.UTM_COORD_N,
            ILLUSTRATION.THUMBNAIL_FILE_NAME, NM_LEMMA_LOCATION.LOCATION_RELEVANCE,
            NM_LEMMA_LOCATION.LOCATION_DATE_LABEL)
        .from(viewLemma1)
        .innerJoin(LEMMA_TYPE).on(viewLemma1.LEMMA_TYPE_ID.eq(LEMMA_TYPE.ID))
        .leftJoin(PERSON).on(viewLemma1.ID.eq(PERSON.LEMMA_ID))
        .leftJoin(PLACE).on(viewLemma1.ID.eq(PLACE.LEMMA_ID))
        .leftJoin(NM_LEMMA_LOCATION).on(viewLemma1.ID.eq(NM_LEMMA_LOCATION.LEMMA_ID))
        .innerJoin(LOCATION).on(NM_LEMMA_LOCATION.LOCATION_ID.eq(LOCATION.ID))
        .leftJoin(NM_LEMMA_ILLUSTRATION).on(viewLemma1.ID.eq(NM_LEMMA_ILLUSTRATION.LEMMA_ID))
        .and(NM_LEMMA_ILLUSTRATION.NR.eq(1))
        .leftJoin(ILLUSTRATION).on(NM_LEMMA_ILLUSTRATION.ILLUSTRATION_ID.eq(ILLUSTRATION.ID))
        .where(NM_LEMMA_LOCATION.MAIN_LOCATION.eq(1))
        .and(viewLemma1.VERSION.eq(
            dsl.select(DSL.max(viewLemma2.VERSION)).from(viewLemma2)
                .where(viewLemma2.ID.eq(viewLemma1.ID))))
        .orderBy(DSL.coalesce(PERSON.FULL_NAME, viewLemma1.TITLE).asc())
        .fetch();

    return JooqJacksonConverter.result2ArrayNode(lemmata);
  }

  public ObjectNode getLemma(String lemmaIdOrLink) throws UnsupportedEncodingException {
    // Try to fetch an ID for the given lemmaIdOrLink. If it is not found, use the
    // given lemmaIdOrLink as ID.
    String lemmaId = dsl
        .select(LEMMA.ID)
        .from(LEMMA)
        .where(LEMMA.LINK.eq(lemmaIdOrLink))
        .fetchOne(LEMMA.ID);

    if (lemmaId == null) {
      lemmaId = lemmaIdOrLink;
    }

    Result<Record> lemmaVersions = dsl
        .select(VIEW_LEMMA.ID.as("lemma_id"), DSL.max(VIEW_LEMMA.VERSION), LEMMA_TYPE.LEMMA_TYPE_,
            VIEW_LEMMA.TITLE,
            VIEW_LEMMA.ABSTRACT, VIEW_LEMMA.DESCRIPTION, VIEW_LEMMA.LEMMA_REFERENCE,
            VIEW_LEMMA.LEMMA_LITERATURE,
            VIEW_LEMMA.TIMELINE_DATE_DAY, VIEW_LEMMA.TIMELINE_DATE_MONTH, VIEW_LEMMA.TIMELINE_DATE_YEAR,
            VIEW_LEMMA.TIMELINE_DATE_LABEL, VIEW_LEMMA.TIMELINE_TITLE, VIEW_LEMMA.AUTHOR_NAME,
            VIEW_LEMMA.GND_IDENTIFIER, VIEW_LEMMA.FEATURED, PERSON.FULL_NAME, PERSON.BIRTH_PLACE,
            PERSON.DEATH_PLACE,
            PERSON.BIRTH_DATE, PERSON.DEATH_DATE, PLACE.ADDRESS, VIEW_LEMMA.LAST_UPDATE, VIEW_LEMMA.VERSION)
        .from(VIEW_LEMMA)
        .leftJoin(LEMMA_TYPE).on(VIEW_LEMMA.LEMMA_TYPE_ID.eq(LEMMA_TYPE.ID))
        .leftJoin(PERSON).on(VIEW_LEMMA.ID.eq(PERSON.LEMMA_ID))
        .leftJoin(PLACE).on(VIEW_LEMMA.ID.eq(PLACE.LEMMA_ID))
        .where(VIEW_LEMMA.ID.eq(lemmaId))
        .groupBy(VIEW_LEMMA.ID, LEMMA_TYPE.LEMMA_TYPE_, VIEW_LEMMA.TITLE, VIEW_LEMMA.ABSTRACT,
            VIEW_LEMMA.DESCRIPTION,
            VIEW_LEMMA.LEMMA_REFERENCE, VIEW_LEMMA.LEMMA_LITERATURE, VIEW_LEMMA.TIMELINE_DATE_DAY,
            VIEW_LEMMA.TIMELINE_DATE_MONTH, VIEW_LEMMA.TIMELINE_DATE_YEAR, VIEW_LEMMA.TIMELINE_DATE_LABEL,
            VIEW_LEMMA.TIMELINE_TITLE, VIEW_LEMMA.AUTHOR_NAME, VIEW_LEMMA.GND_IDENTIFIER,
            VIEW_LEMMA.FEATURED,
            PERSON.FULL_NAME, PERSON.BIRTH_PLACE, PERSON.DEATH_PLACE, PERSON.BIRTH_DATE, PERSON.DEATH_DATE,
            PLACE.ADDRESS, VIEW_LEMMA.LAST_UPDATE, VIEW_LEMMA.VERSION)
        .orderBy(VIEW_LEMMA.VERSION)
        .fetch();

    Result<Record11<String, String, String, String, String, Integer, Integer, Integer, Integer, String, Integer>> locations = dsl
        .select(NM_LEMMA_LOCATION.LEMMA_ID, LOCATION.ID, LOCATION.INTERNAL_NAME,
            NM_LEMMA_LOCATION.LOCATION_RELEVANCE,
            NM_LEMMA_LOCATION.LOCATION_DATE_LABEL,
            NM_LEMMA_LOCATION.NR_OF_LOCATION, NM_LEMMA_LOCATION.MAIN_LOCATION, LOCATION.UTM_COORD_E,
            LOCATION.UTM_COORD_N,
            ILLUSTRATION.THUMBNAIL_FILE_NAME, NM_LEMMA_ILLUSTRATION.NR)
        .from(NM_LEMMA_LOCATION)
        .innerJoin(LOCATION).on(NM_LEMMA_LOCATION.LOCATION_ID.eq(LOCATION.ID))
        .leftJoin(ILLUSTRATION).on(LOCATION.ILLUSTRATION_ID.eq(ILLUSTRATION.ID))
        .leftJoin(NM_LEMMA_ILLUSTRATION).on(LOCATION.ILLUSTRATION_ID.eq(NM_LEMMA_ILLUSTRATION.ILLUSTRATION_ID))
        .and(NM_LEMMA_ILLUSTRATION.LEMMA_ID.eq(NM_LEMMA_ILLUSTRATION.LEMMA_ID))
        .where(NM_LEMMA_LOCATION.LEMMA_ID.eq(lemmaId))
        .fetch();

    Result<Record15<String, Integer, String, String, String, String, String, String, BigDecimal, BigDecimal, String, String, String, String, String>> illustrations = dsl
        .select(ILLUSTRATION.ID, NM_LEMMA_ILLUSTRATION.NR, ILLUSTRATION.TITLE, ILLUSTRATION.CREATOR,
            ILLUSTRATION.ARCHIVE_SIGNATURE, ILLUSTRATION.PICTURE_DATE, ILLUSTRATION.PICTURE_DESCRIPTION,
            ILLUSTRATION.TECHNIQUE_MATERIAL, ILLUSTRATION.FORMAT_X_CM, ILLUSTRATION.FORMAT_Y_CM,
            ILLUSTRATION.LICENCE,
            ILLUSTRATION.SOURCE_BASE_INFORMATION, ILLUSTRATION.THUMBNAIL_FILE_NAME,
            ILLUSTRATION.ILLUSTRATION_FILE_NAME,
            ILLUSTRATION.TRANSCRIPTION_TEXT)
        .from(ILLUSTRATION)
        .leftJoin(NM_LEMMA_ILLUSTRATION).on(ILLUSTRATION.ID.eq(NM_LEMMA_ILLUSTRATION.ILLUSTRATION_ID))
        .where(NM_LEMMA_ILLUSTRATION.LEMMA_ID.eq(lemmaId))
        .orderBy(NM_LEMMA_ILLUSTRATION.NR)
        .fetch();

    Result<Record2<String, String>> keywords = dsl
        .select(KEYWORD.ID, KEYWORD.KEYWORD_)
        .from(NM_LEMMA_KEYWORD)
        .innerJoin(KEYWORD).on(NM_LEMMA_KEYWORD.KEYWORD_ID.eq(KEYWORD.ID))
        .where(NM_LEMMA_KEYWORD.LEMMA_ID.eq(lemmaId))
        .fetch();

    // String SQL_QUERY_PLACE_LEMMA_AVAILABLE = "select distinct b.id as id, b.title
    // as title "
    // + "from view_lemma a, view_lemma b, lemma_type lta, lemma_type ltb, "
    // + "location la, location lb, nm_lemma_location nmlla, nm_lemma_location nmllb
    // "
    // + "inner join nm_lemma_location on a.id = nmlla.lemma_id "
    // + "inner join nm_lemma_location on b.id = nmllb.lemma_id "
    // + "inner join location on nmlla.location_id = la.id inner "
    // + "join location on nmllb.location_id = lb.id " + "inner join lemma_type on
    // a.lemma_type_id = lta.id "
    // + "inner join lemma_type on b.lemma_type_id = ltb.id "
    // + "where ltb.lemma_type = 'PLACE' and la.id = lb.id and a.id != b.id and
    // a.id=? limit 1";

    // Result<?> dbQueryResultPlaceLemmaAvailable =
    // dsl.fetch(SQL_QUERY_PLACE_LEMMA_AVAILABLE, lemmaId);

    ObjectNode lemmaInformation = JsonNodeFactory.instance.objectNode();

    lemmaInformation.set("version", JooqJacksonConverter.result2ArrayNode(lemmaVersions));
    lemmaInformation.set("locations", JooqJacksonConverter.result2ArrayNode(locations));
    lemmaInformation.set("illustrations", JooqJacksonConverter.result2ArrayNode(illustrations));
    lemmaInformation.set("keywords", JooqJacksonConverter.result2ArrayNode(keywords));
    // lemmaInformation.set("placeLemma",
    // JooqJacksonConverter.result2ArrayNode(dbQueryResultPlaceLemmaAvailable));
    lemmaInformation.put("websafeTitle",
        toolbox.replaceByWebsafeChars(
            (String) lemmaVersions.getValues(VIEW_LEMMA.TITLE).get(lemmaVersions.size() - 1)));

    return lemmaInformation;
  }

  public ObjectNode syncLemmata(String lemmaId, int lemmaVersion, MultipartFile file) {
    ObjectNode result = JsonNodeFactory.instance.objectNode();

    if (file == null || file.isEmpty()) {
      log.error("File is empty");
      result.put("error", "fileIsEmpty");
      return result;
    }

    String lemmaVersionId = dsl.select(LEMMA_VERSION.ID)
        .from(LEMMA_VERSION)
        .where(LEMMA_VERSION.LEMMA_ID.eq(lemmaId))
        .and(LEMMA_VERSION.VERSION.eq(lemmaVersion))
        .fetchOne(LEMMA_VERSION.ID);

    if (lemmaVersionId == null) {
      log.error("Lemma version {} not found for lemmaId {}", lemmaVersion, lemmaId);
      result.put("error", "lemmaVersionNotFound");
      return result;
    }

    byte[] fileBytes;
    try {
      fileBytes = file.getBytes();
    } catch (IOException e) {
      log.error("Error reading file bytes", e);
      result.put("error", "fileReadError");
      return result;
    }

    dsl.insertInto(LEMMA_SYNC, LEMMA_SYNC.LEMMA_VERSION_ID, LEMMA_SYNC.AUDIO_DATA, LEMMA_SYNC.FILE_FORMAT)
        .values(lemmaVersionId, fileBytes, file.getContentType().split("/")[1])
        .execute();

    return result.put("success", "Sync file successfully uploaded")
        .put("lemmaId", lemmaId)
        .put("lemmaVersion", lemmaVersion)
        .put("lemmaVersionId", lemmaVersionId)
        .put("fileName", file.getOriginalFilename())
        .put("fileSize", file.getSize())
        .put("fileContentType", file.getContentType());
  }

  public Record getSyncLemmata(String lemmaId, int lemmaVersion) throws NoDataFoundException {
    String lemmaVersionId = dsl.select(LEMMA_VERSION.ID)
        .from(LEMMA_VERSION)
        .where(LEMMA_VERSION.LEMMA_ID.eq(lemmaId))
        .and(LEMMA_VERSION.VERSION.eq(lemmaVersion))
        .fetchOne(LEMMA_VERSION.ID);

    if (lemmaVersionId == null) {
      log.error("Lemma version {} not found for lemmaId {}", lemmaVersion, lemmaId);
      throw new NoDataFoundException(lemmaVersionId);
    }

    Record syncRecord = dsl.select(LEMMA_SYNC.AUDIO_DATA, LEMMA_SYNC.FILE_FORMAT)
        .from(LEMMA_SYNC)
        .where(LEMMA_SYNC.LEMMA_VERSION_ID.eq(lemmaVersionId))
        .fetchOne();

    if (syncRecord == null) {
      log.warn("No audio data found for lemmaVersionId {}", lemmaVersionId);
      return null;
    }

    return syncRecord;
  }

  public ObjectNode loadSync(String lemmaId, int lemmaVersion) {

    return null;
  }

}
