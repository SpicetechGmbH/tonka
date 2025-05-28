package de.spicetech.dts.service;

import static de.spicetech.dts.jooq.tables.SearchColumn.SEARCH_COLUMN;
import static de.spicetech.dts.jooq.tables.SearchGroup.SEARCH_GROUP;
import static de.spicetech.dts.jooq.tables.Spelling.SPELLING;

import java.util.regex.Pattern;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record2;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.spicetech.jooq.JooqJacksonConverter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class QueryService {

  @Autowired
  DSLContext dsl;

  private static final String GROUP_2_QUERY_TEMPLATE = "select %%select_columns%%, (%%relevance%%) as relevance "
      + "from %%view%% where %%where_clause%% order by relevance desc";

  private static final String[] GROUP2_RESULT_COLUMNS = { "id", "version", "title", "abstract", "all_keywords",
      "all_location_relevances", "author_name", "gnd_identifier", "lemma_literature", "lemma_reference", "lemma_type",
      "lemma_type_gui_name", "timeline_title", "timeline_date_day", "timeline_date_month", "timeline_date_year",
      "timeline_date_label", "timeline_date_relevance", "utm_coord_e", "utm_coord_n", "thumbnail_file_name",
      "location_relevance", "location_date_label" };

  private static final String[] GROUP_ILLUSTRATION_2_RESULT_COLUMNS = { "id", "lemma_id", "lemma_type", "nr", "title",
      "timeline_title", "lemma_title", "thumbnail_file_name", "creator", "picture_description", "licence",
      "technique_material", "archive_signature", "transcription_text" };

  private static final String[] GROUP_MAP_2_RESULT_COLUMNS = { "id", "map_description", "timeline_title",
      "timeline_date_label" };

  private static final String[] GROUP_NET_2_RESULT_COLUMNS = { "id", "lemma_id", "internal_name", "location_relevance",
      "title", "timeline_title", "lemma_type" };

  private String group2QueryCreator(String queryString, String[] resultColumns, String searchGroup, String queryView) {
    // Create the select-part.
    String[] queryStringParts = queryString.trim().replaceAll(" +", " ").split(" ");

    // Create the select columns
    String selectColumns = String.join(",", resultColumns);

    // Create the relevance-select-part.
    Result<Record2<String, Integer>> searchColumnAndRelevance = dsl
        .select(SEARCH_COLUMN.COLUMN, SEARCH_COLUMN.QUANTIFIER)
        .from(SEARCH_GROUP)
        .leftJoin(SEARCH_COLUMN).on(SEARCH_GROUP.ID.eq(SEARCH_COLUMN.SEARCH_GROUP_ID))
        .where(SEARCH_GROUP.NAME.eq(searchGroup))
        .fetch();

    String relevance = "";
    for (String queryStringPart : queryStringParts) {
      for (Record searchRecord : searchColumnAndRelevance) {
        String searchColumn = searchRecord.get(SEARCH_COLUMN.COLUMN);
        Integer quantifier = searchRecord.get(SEARCH_COLUMN.QUANTIFIER);
        if (!relevance.isEmpty()) {
          relevance += " + ";
        }
        relevance += "coalesce(("
            + "select " + quantifier
            + " where lower(" + searchColumn + ") like '%" + queryStringPart + "%'"
            + "), 0)";
      }
    }

    // Create the where-clause
    String whereClause = "";
    for (String queryStringPart : queryStringParts) {
      if (!whereClause.isEmpty()) {
        whereClause += " and ";
      }
      whereClause += "all_in_one like '%" + queryStringPart + "%'";
    }

    // Create the query
    String query = GROUP_2_QUERY_TEMPLATE
        .replace("%%select_columns%%", selectColumns)
        .replace("%%relevance%%", relevance.isEmpty() ? "null" : relevance)
        .replace("%%view%%", queryView)
        .replace("%%where_clause%%", whereClause);

    return query;
  }

  public ObjectNode group2Query(String queryString) {
    String queryStringLowerCased = queryString.toLowerCase();
    String query = group2QueryCreator(queryStringLowerCased, GROUP2_RESULT_COLUMNS, "lemma", "view_group2_search");
    Result<Record> queryResult = dsl.fetch(query);

    ObjectNode response = JsonNodeFactory.instance.objectNode();
    response.set("queryData", JooqJacksonConverter.result2ArrayNode(queryResult));

    // Check for alternative spelling (for each word in queryString).
    Result<Record2<String, String>> spellings = dsl.select(SPELLING.PRIMARY_SPELLING, SPELLING.ALTERNATIVE_SPELLING)
        .from(SPELLING).fetch();

    String alternativeQuery = queryString;
    String lastAlternativeQuery = alternativeQuery;
    int maxNoOfIterations = queryString.split(" ").length;

    do {
      maxNoOfIterations--;
      lastAlternativeQuery = alternativeQuery;
      Record2<String, String> currentReplacement = null;

      // Iterate through all entries of table 'spelling'.
      for (Record2<String, String> spelling : spellings) {
        // Try to find an entry in 'spelling' where one of the given query tokens is in
        // 'alternative_spelling'.
        if (alternativeQuery.toLowerCase().contains(spelling.get(SPELLING.ALTERNATIVE_SPELLING).toLowerCase())) {
          // Use found replacement if there is none found until now or the maximum length
          // of replaced tokens can be increased.
          if (currentReplacement == null || spelling.get(SPELLING.ALTERNATIVE_SPELLING).length() > currentReplacement
              .get(SPELLING.ALTERNATIVE_SPELLING).length()) {
            currentReplacement = spelling;
          }
        }
      }
      if (currentReplacement != null) {
        alternativeQuery = alternativeQuery.replaceAll(
            "(?i)" + Pattern.quote(currentReplacement.get(SPELLING.ALTERNATIVE_SPELLING)),
            currentReplacement.get(SPELLING.PRIMARY_SPELLING));
      }
    } while (!alternativeQuery.equalsIgnoreCase(lastAlternativeQuery) && maxNoOfIterations > 0);

    if (!alternativeQuery.equals(queryString)) {
      response.put("alternativeQuery", alternativeQuery);
    }

    return response;
  }

  public ArrayNode groupIllustration2Query(String queryString) {
    String query = group2QueryCreator(queryString, GROUP_ILLUSTRATION_2_RESULT_COLUMNS, "illustration",
        "view_group_illustration_2search");
    Result<Record> queryResult = dsl.fetch(query);
    return JooqJacksonConverter.result2ArrayNode(queryResult);
  }

  public ArrayNode groupMap2Query(String queryString) {
    String query = group2QueryCreator(queryString, GROUP_MAP_2_RESULT_COLUMNS, "map", "view_group_map_2search");
    Result<Record> queryResult = dsl.fetch(query);
    return JooqJacksonConverter.result2ArrayNode(queryResult);
  }

  public ArrayNode groupNet2Query(String queryString) {
    String query = group2QueryCreator(queryString, GROUP_NET_2_RESULT_COLUMNS, "biographic_net",
        "view_group_bionet_2search");
    Result<Record> queryResult = dsl.fetch(query);
    return JooqJacksonConverter.result2ArrayNode(queryResult);
  }

}
