package de.spicetech.dts.service;

import static de.spicetech.dts.jooq.tables.Illustration.ILLUSTRATION;
import static de.spicetech.dts.jooq.tables.Lemma.LEMMA;
import static de.spicetech.dts.jooq.tables.LemmaVersion.LEMMA_VERSION;
import static de.spicetech.dts.jooq.tables.NmLemmaIllustration.NM_LEMMA_ILLUSTRATION;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.node.ObjectNode;

import de.spicetech.jooq.JooqJacksonConverter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class IllustrationService {

    @Autowired
    DSLContext dsl;

    public ObjectNode getIllustration(String id) {
        Record illustrationRecord = dsl.select()
                .from(ILLUSTRATION)
                .where(ILLUSTRATION.ID.eq(id))
                .fetchOne();

        return JooqJacksonConverter.record2ObjectNode(illustrationRecord);
    }

    public ObjectNode getIllustration(String lemmaId, int nr) {
        Record illustrationRecord = dsl
                .select(ILLUSTRATION.ID,
                        DSL.coalesce(NM_LEMMA_ILLUSTRATION.TITLE, ILLUSTRATION.TITLE).as("title"),
                        ILLUSTRATION.CREATOR,
                        ILLUSTRATION.ARCHIVE_SIGNATURE,
                        ILLUSTRATION.PICTURE_DATE,
                        DSL.coalesce(NM_LEMMA_ILLUSTRATION.PICTURE_DESCRIPTION, ILLUSTRATION.PICTURE_DESCRIPTION)
                                .as("picture_description"),
                        ILLUSTRATION.TECHNIQUE_MATERIAL,
                        ILLUSTRATION.FORMAT_X_CM,
                        ILLUSTRATION.FORMAT_Y_CM,
                        ILLUSTRATION.LICENCE,
                        ILLUSTRATION.SOURCE_BASE_INFORMATION,
                        ILLUSTRATION.THUMBNAIL_FILE_NAME,
                        ILLUSTRATION.ILLUSTRATION_FILE_NAME,
                        ILLUSTRATION.TRANSCRIPTION_TEXT,
                        ILLUSTRATION.REPRO,
                        LEMMA_VERSION.TITLE.as("lemma_title"))
                .from(NM_LEMMA_ILLUSTRATION)
                .join(ILLUSTRATION).on(NM_LEMMA_ILLUSTRATION.ILLUSTRATION_ID.eq(ILLUSTRATION.ID))
                .and(NM_LEMMA_ILLUSTRATION.NR.eq(nr))
                .join(LEMMA).on(NM_LEMMA_ILLUSTRATION.LEMMA_ID.eq(LEMMA.ID))
                .and(NM_LEMMA_ILLUSTRATION.LEMMA_ID.eq(lemmaId))
                .leftJoin(LEMMA_VERSION).on(LEMMA.ID.eq(LEMMA_VERSION.LEMMA_ID))
                .and(LEMMA_VERSION.VERSION.eq(
                        dsl.select(DSL.max(LEMMA_VERSION.VERSION))
                                .from(LEMMA_VERSION)
                                .where(LEMMA_VERSION.LEMMA_ID.eq(LEMMA.ID))))
                .fetchOne();

        return JooqJacksonConverter.record2ObjectNode(illustrationRecord);
    }

}
