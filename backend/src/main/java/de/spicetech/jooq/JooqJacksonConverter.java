package de.spicetech.jooq;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Iterator;
import java.util.UUID;

import org.jooq.DSLContext;
import org.jooq.EnumType;
import org.jooq.Field;
import org.jooq.InsertSetMoreStep;
import org.jooq.InsertSetStep;
import org.jooq.JSON;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.UpdateSetFirstStep;
import org.jooq.UpdateSetMoreStep;
import org.jooq.impl.TableImpl;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.jooq.types.ULong;
import org.jooq.types.UShort;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JooqJacksonConverter {

	/**
	 * Converts a Jooq result object to Json array representation
	 * 
	 * @param result - the Jooq result object of a select statement
	 * 
	 * @return ArrayNode - Json representation of the Jooq result
	 */
	@SuppressWarnings("rawtypes")
	public static ArrayNode result2ArrayNode(Result result) {
		ArrayNode resultArrayNode = JsonNodeFactory.instance.arrayNode();

		for (Object recordObject : result) {
			Record record = (Record) recordObject;
			resultArrayNode.add(record2ObjectNode(record));
		}

		return resultArrayNode;
	}

	/**
	 * Converts a Jooq record object to Json object representation
	 * 
	 * @param record - the Jooq record object of a select statement (e.g. as a
	 *               result of fetchOne() method)
	 * 
	 * @return ObjectNode - Json representation of the Jooq result
	 */

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static ObjectNode record2ObjectNode(Record record) {

		ObjectNode recordObjectNode = JsonNodeFactory.instance.objectNode();

		if (record != null) {
			Field[] fields = record.fields();
			for (Field field : fields) {
				String fieldName = field.getName();
				if (record.get(field) != null) {
					Object value = record.get(field);
					if (value instanceof Integer) {
						recordObjectNode.put(fieldName, (Integer) value);
					} else if (value instanceof UInteger) {
						recordObjectNode.put(fieldName, ((UInteger) value).intValue());
					} else if (value instanceof String) {
						recordObjectNode.put(fieldName, (String) value);
					} else if (value instanceof Boolean) {
						recordObjectNode.put(fieldName, (Boolean) value);
					} else if (value instanceof Long) {
						recordObjectNode.put(fieldName, (Long) value);
					} else if (value instanceof ULong) {
						recordObjectNode.put(fieldName, ((ULong) value).longValue());
					} else if (value instanceof Double) {
						recordObjectNode.put(fieldName, (Double) value);
					} else if (value instanceof Short) {
						recordObjectNode.put(fieldName, (Short) value);
					} else if (value instanceof UShort) {
						recordObjectNode.put(fieldName, ((UShort) value).intValue());
					} else if (value instanceof Byte) {
						recordObjectNode.put(fieldName, (Byte) value);
					} else if (value instanceof UByte) {
						recordObjectNode.put(fieldName, ((UByte) value).intValue());
					} else if (value instanceof Float) {
						recordObjectNode.put(fieldName, (Float) value);
					} else if (value instanceof BigDecimal) {
						recordObjectNode.put(fieldName, (BigDecimal) value);
					} else if (value instanceof Character) {
						recordObjectNode.put(fieldName, (Character) value);
					} else if (value instanceof JSON) {
						JsonNode jsonNode = jooqJSONToJacksonJsonNode((JSON) value);
						recordObjectNode.set(fieldName, jsonNode);
					} else if (value instanceof Timestamp) {
						recordObjectNode.put(fieldName, ((Timestamp) value).getTime());
					} else if (value instanceof Time) {
						recordObjectNode.put(fieldName, ((Time) value).getTime());
					} else if (value instanceof Date) {
						recordObjectNode.put(fieldName, ((Date) value).getTime());
					} else if (value instanceof LocalDate) {
						recordObjectNode.put(fieldName, ((LocalDate) value).atStartOfDay(ZoneId.of("Europe/Berlin")).toInstant().toEpochMilli());
					} else if (value instanceof UUID) {
						recordObjectNode.put(fieldName, ((UUID) value).toString());
					} else if (value instanceof byte[]) {
						recordObjectNode.put(fieldName, Base64.getEncoder().encodeToString((byte[]) value));
					} else if (value instanceof EnumType) {
						recordObjectNode.put(fieldName, ((EnumType) value).getLiteral());
					}
				} else {
					recordObjectNode.putNull(fieldName);
				}
			}
		}
		return recordObjectNode;
	}

	private static JsonNode jooqJSONToJacksonJsonNode(JSON value) {
		JsonNode jsonNode = null;
		String jsonString = value.toString();
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			jsonNode = objectMapper.readTree(jsonString);

		} catch (JsonMappingException e) {
			throw new JooqJsonTypeException(
					"JsonMappingException occured when converting org.jooq.JSON to com.fasterxml.jackson.databind.JsonNode: %jsonString%"
							.replaceAll("%jsonString%", jsonString),
					e);

		} catch (JsonProcessingException e) {
			throw new JooqJsonTypeException(
					"JsonProcessingException occured when converting org.jooq.JSON to com.fasterxml.jackson.databind.JsonNode: %jsonString%"
							.replaceAll("%jsonString%", jsonString),
					e);
		}
		return jsonNode;
	}

	private static JSON jacksonJsonNodeToJooqJSON(JsonNode jsonNode) {
		return JSON.valueOf(jsonNode.toString());
	}

	/**
	 * Prepare a Jooq insertInto statement based on a Json object input
	 * 
	 * If the Json object contains name/value pairs without a matching database
	 * column, these name/value pairs are ignored
	 * 
	 * @param dsl        - the DSLContext of Jooq
	 * @param table      - the Jooq table
	 * @param objectNode - the Json object containing the data for insertion
	 * 
	 * @return org.jooq.InsertSetMoreStep - an object ready of execution on DB
	 */
	@SuppressWarnings({ "rawtypes", "unchecked", "resource" })
	public static InsertSetMoreStep json2JooqInsertInto(DSLContext dsl, TableImpl table, ObjectNode objectNode) {

		InsertSetStep insertSetStep = dsl.insertInto(table);
		InsertSetMoreStep insertSetMoreStep = null;

		for (Iterator<String> iter = objectNode.fieldNames(); iter.hasNext();) {
			String fieldName = iter.next();
			Field field = table.field(fieldName);
			if (field != null) {
				// only proceed, if field belongs to table --> ignore unknown columns

				String type;

				if (field.getDataType().isEnum()) {
					type = "enum";
				} else if (field.getDataType().isBinary()) {
					type = field.getDataType().getTypeName();
				} else {
					type = field.getType().getName();
				}

				if (insertSetMoreStep != null) {
					if (type.equals("java.lang.String")) {
						insertSetMoreStep = insertSetMoreStep.set(table.field(fieldName),
								objectNode.get(fieldName).asText());
					} else if (type.equals("java.sql.Timestamp")) {
						insertSetMoreStep = insertSetMoreStep.set(table.field(fieldName),
								new Timestamp(objectNode.get(fieldName).asLong()));
					} else if (type.equals("java.sql.Date")) {
						insertSetMoreStep = insertSetMoreStep.set(table.field(fieldName),
								new Date(objectNode.get(fieldName).asLong()));
					} else if (type.equals("java.lang.Byte") || type.equals("java.lang.Short")
							|| type.equals("java.lang.Integer") || type.equals("org.jooq.types.UByte")
							|| type.equals("org.jooq.types.UShort") || type.equals("org.jooq.types.UInteger")) {
						insertSetMoreStep = insertSetMoreStep.set(table.field(fieldName),
								objectNode.get(fieldName).asInt());
					} else if (type.equals("java.lang.Long") || type.equals("org.jooq.types.ULong")) {
						insertSetMoreStep = insertSetMoreStep.set(table.field(fieldName),
								objectNode.get(fieldName).asLong());
					} else if (type.equals("java.lang.Double")) {
						insertSetMoreStep = insertSetMoreStep.set(table.field(fieldName),
								objectNode.get(fieldName).asDouble());
					} else if (type.equals("java.math.BigDecimal")) {
						insertSetMoreStep = insertSetMoreStep.set(table.field(fieldName),
								new BigDecimal(objectNode.get(fieldName).asText()));
					} else if (type.equals("java.sql.Time")) {
						insertSetMoreStep = insertSetMoreStep.set(table.field(fieldName),
								new Time(objectNode.get(fieldName).asLong()));
					} else if (type.equals("org.jooq.JSON")) {
						insertSetMoreStep = insertSetMoreStep.set(table.field(fieldName),
								jacksonJsonNodeToJooqJSON(objectNode.get(fieldName)));
					} else if (type.equals("binary") | type.equals("varbinary")) {
						try {
							insertSetMoreStep = insertSetMoreStep.set(table.field(fieldName),
									objectNode.get(fieldName).binaryValue());
						} catch (IOException e) {
							throw new JooqJsonTypeException("IOException occured reading binaryValue from type: %type%"
									.replaceAll("%type%", type), e);

						}
					} else if (type.equals("enum")) {
						insertSetMoreStep = insertSetMoreStep.set(table.field(fieldName),
								field.getDataType().convert(objectNode.get(fieldName).asText()));
					} else {
						throw new JooqJsonTypeException("Unsupported type: %type%".replaceAll("%type%", type));
					}
				} else {
					if (type.equals("java.lang.String")) {
						insertSetMoreStep = insertSetStep.set(table.field(fieldName),
								objectNode.get(fieldName).asText());
					} else if (type.equals("java.sql.Timestamp")) {
						insertSetMoreStep = insertSetStep.set(table.field(fieldName),
								new Timestamp(objectNode.get(fieldName).asLong()));
					} else if (type.equals("java.sql.Date")) {
						insertSetMoreStep = insertSetStep.set(table.field(fieldName),
								new Date(objectNode.get(fieldName).asLong()));
					} else if (type.equals("java.lang.Byte") || type.equals("java.lang.Short")
							|| type.equals("java.lang.Integer") || type.equals("org.jooq.types.UByte")
							|| type.equals("org.jooq.types.UShort") || type.equals("org.jooq.types.UInteger")) {
						insertSetMoreStep = insertSetStep.set(table.field(fieldName),
								objectNode.get(fieldName).asInt());
					} else if (type.equals("java.lang.Long") || type.equals("org.jooq.types.ULong")) {
						insertSetMoreStep = insertSetStep.set(table.field(fieldName),
								objectNode.get(fieldName).asLong());
					} else if (type.equals("java.lang.Double")) {
						insertSetMoreStep = insertSetStep.set(table.field(fieldName),
								objectNode.get(fieldName).asDouble());
					} else if (type.equals("java.math.BigDecimal")) {
						insertSetMoreStep = insertSetStep.set(table.field(fieldName),
								new BigDecimal(objectNode.get(fieldName).asText()));
					} else if (type.equals("java.sql.Time")) {
						insertSetMoreStep = insertSetStep.set(table.field(fieldName),
								new Time(objectNode.get(fieldName).asLong()));
					} else if (type.equals("org.jooq.JSON")) {
						insertSetMoreStep = insertSetStep.set(table.field(fieldName),
								jacksonJsonNodeToJooqJSON(objectNode.get(fieldName)));
					} else if (type.equals("binary") | type.equals("varbinary")) {
						try {
							insertSetMoreStep = insertSetStep.set(table.field(fieldName),
									objectNode.get(fieldName).binaryValue());
						} catch (IOException e) {
							throw new JooqJsonTypeException("IOException occured reading binaryValue from type: %type%"
									.replaceAll("%type%", type), e);

						}
					} else if (type.equals("enum")) {
						insertSetMoreStep = insertSetStep.set(table.field(fieldName),
								field.getDataType().convert(objectNode.get(fieldName).asText()));
					} else {

						throw new JooqJsonTypeException("Unsupported type: %type%".replaceAll("%type%", type));
					}

				}
			}
		}

		return insertSetMoreStep;
	}

	/**
	 * Prepare a Jooq update statement based on a Json object input
	 * 
	 * If the Json object contains name/value pairs without a matching database
	 * column, these name/value pairs are ignored
	 * 
	 * @param dsl        - the DSLContext of Jooq
	 * @param table      - the Jooq table
	 * @param objectNode - the Json object containing the data for update
	 * 
	 * @return org.jooq.UpdateSetMoreStep - an object ready of execution on DB
	 */
	@SuppressWarnings({ "rawtypes", "unchecked", "resource" })
	public static UpdateSetMoreStep json2JooqUpdate(DSLContext dsl, TableImpl table, ObjectNode objectNode) {

		UpdateSetFirstStep updateSetStep = dsl.update(table);
		UpdateSetMoreStep updateSetMoreStep = null;

		for (Iterator<String> iter = objectNode.fieldNames(); iter.hasNext();) {
			String fieldName = iter.next();
			Field field = table.field(fieldName);

			if (field != null) {
				// only proceed, if field belongs to table --> ignore unknown cols

				String type;

				if (field.getDataType().isEnum()) {
					type = "enum";
				} else if (field.getDataType().isBinary()) {
					type = field.getDataType().getTypeName();
				} else {
					type = field.getType().getName();
				}

				if (updateSetMoreStep != null) {
					if (type.equals("java.lang.String")) {
						updateSetMoreStep = updateSetMoreStep.set(table.field(fieldName),
								objectNode.get(fieldName).asText());
					} else if (type.equals("java.sql.Timestamp")) {
						updateSetMoreStep = updateSetMoreStep.set(table.field(fieldName),
								new Timestamp(objectNode.get(fieldName).asLong()));
					} else if (type.equals("java.sql.Date")) {
						updateSetMoreStep = updateSetMoreStep.set(table.field(fieldName),
								new Date(objectNode.get(fieldName).asLong()));
					} else if (type.equals("java.lang.Byte") || type.equals("java.lang.Short")
							|| type.equals("java.lang.Integer") || type.equals("org.jooq.types.UByte")
							|| type.equals("org.jooq.types.UShort") || type.equals("org.jooq.types.UInteger")) {
						updateSetMoreStep = updateSetMoreStep.set(table.field(fieldName),
								objectNode.get(fieldName).asInt());
					} else if (type.equals("java.lang.Long") || type.equals("org.jooq.types.ULong")) {
						updateSetMoreStep = updateSetMoreStep.set(table.field(fieldName),
								objectNode.get(fieldName).asLong());
					} else if (type.equals("java.lang.Double")) {
						updateSetMoreStep = updateSetMoreStep.set(table.field(fieldName),
								objectNode.get(fieldName).asDouble());
					} else if (type.equals("java.math.BigDecimal")) {
						updateSetMoreStep = updateSetMoreStep.set(table.field(fieldName),
								new BigDecimal(objectNode.get(fieldName).asText()));
					} else if (type.equals("java.sql.Time")) {
						updateSetMoreStep = updateSetMoreStep.set(table.field(fieldName),
								new Time(objectNode.get(fieldName).asLong()));
					} else if (type.equals("org.jooq.JSON")) {
						updateSetMoreStep = updateSetMoreStep.set(table.field(fieldName),
								jacksonJsonNodeToJooqJSON(objectNode.get(fieldName)));
					} else if (type.equals("binary") | type.equals("varbinary")) {
						try {
							updateSetMoreStep = updateSetMoreStep.set(table.field(fieldName),
									objectNode.get(fieldName).binaryValue());
						} catch (IOException e) {
							throw new JooqJsonTypeException("IOException occured reading binaryValue from type: %type%"
									.replaceAll("%type%", type), e);

						}
					} else if (type.equals("enum")) {
						updateSetMoreStep = updateSetMoreStep.set(table.field(fieldName),
								field.getDataType().convert(objectNode.get(fieldName).asText()));
					} else {
						throw new JooqJsonTypeException("Unsupported type: %type%".replaceAll("%type%", type));
					}
				} else {
					if (type.equals("java.lang.String")) {
						updateSetMoreStep = updateSetStep.set(table.field(fieldName),
								objectNode.get(fieldName).asText());
					} else if (type.equals("java.sql.Timestamp")) {
						updateSetMoreStep = updateSetStep.set(table.field(fieldName),
								new Timestamp(objectNode.get(fieldName).asLong()));
					} else if (type.equals("java.sql.Date")) {
						updateSetMoreStep = updateSetStep.set(table.field(fieldName),
								new Date(objectNode.get(fieldName).asLong()));
					} else if (type.equals("java.lang.Byte") || type.equals("java.lang.Short")
							|| type.equals("java.lang.Integer") || type.equals("org.jooq.types.UByte")
							|| type.equals("org.jooq.types.UShort") || type.equals("org.jooq.types.UInteger")) {
						updateSetMoreStep = updateSetStep.set(table.field(fieldName),
								objectNode.get(fieldName).asInt());
					} else if (type.equals("java.lang.Long") || type.equals("org.jooq.types.ULong")) {
						updateSetMoreStep = updateSetStep.set(table.field(fieldName),
								objectNode.get(fieldName).asLong());
					} else if (type.equals("java.lang.Double")) {
						updateSetMoreStep = updateSetStep.set(table.field(fieldName),
								objectNode.get(fieldName).asDouble());
					} else if (type.equals("java.math.BigDecimal")) {
						updateSetMoreStep = updateSetStep.set(table.field(fieldName),
								new BigDecimal(objectNode.get(fieldName).asText()));
					} else if (type.equals("java.sql.Time")) {
						updateSetMoreStep = updateSetStep.set(table.field(fieldName),
								new Time(objectNode.get(fieldName).asLong()));
					} else if (type.equals("org.jooq.JSON")) {
						updateSetMoreStep = updateSetStep.set(table.field(fieldName),
								jacksonJsonNodeToJooqJSON(objectNode.get(fieldName)));
					} else if (type.equals("binary") | type.equals("varbinary")) {
						try {
							updateSetMoreStep = updateSetStep.set(table.field(fieldName),
									objectNode.get(fieldName).binaryValue());
						} catch (IOException e) {
							throw new JooqJsonTypeException("IOException occured reading binaryValue from type: %type%"
									.replaceAll("%type%", type), e);

						}
					} else if (type.equals("enum")) {
						updateSetMoreStep = updateSetStep.set(table.field(fieldName),
								field.getDataType().convert(objectNode.get(fieldName).asText()));
					} else {

						throw new JooqJsonTypeException("Unsupported type: %type%".replaceAll("%type%", type));
					}

				}
			}
		}

		return updateSetMoreStep;
	}

	public static ObjectNode jooqJsonToObjectNode(JSON jooqJson) {
		ObjectNode objectNode = null;
		try {
			objectNode = (ObjectNode) new ObjectMapper().readTree(jooqJson.toString());
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		return objectNode;
	}

	public static ArrayNode jooqJsonToArrayNode(JSON jooqJson) {
		ArrayNode arrayNode = null;
		try {
			arrayNode = (ArrayNode) new ObjectMapper().readTree(jooqJson.toString());
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		return arrayNode;
	}

}
