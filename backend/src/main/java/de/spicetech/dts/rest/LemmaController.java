package de.spicetech.dts.rest;

import static de.spicetech.dts.jooq.tables.LemmaSync.LEMMA_SYNC;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import org.jooq.exception.NoDataFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.spicetech.dts.service.LemmaService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("rest/lemma")
public class LemmaController {

  @Autowired
  LemmaService service;

  @GetMapping
  public ResponseEntity<ObjectNode> getAllLemmata() {
    UUID runId = UUID.randomUUID();
    String methodName = "getAllLemmata";
    log.info("Entering {} -> runId: {}", methodName, runId);

    ObjectNode allLemmata = JsonNodeFactory.instance.objectNode();
    allLemmata.set("allLemmata", service.getAllLemmata());

    log.info("Leaving {} -> runId: {}", methodName, runId);
    return ResponseEntity.ok(allLemmata);
  }

  @GetMapping("list")
  public ResponseEntity<ArrayNode> getAllLemmataForList() {
    UUID runId = UUID.randomUUID();
    String methodName = "getAllLemmataForList";
    log.info("Entering {} -> runId: {}", methodName, runId);

    ArrayNode allLemmataForList = service.getAllLemmataForList();

    log.info("Leaving {} -> runId: {}", methodName, runId);
    return ResponseEntity.ok(allLemmataForList);
  }

  @GetMapping("{id}")
  public ResponseEntity<ObjectNode> getLemma(@PathVariable("id") String id) {
    UUID runId = UUID.randomUUID();
    String methodName = "getLemma";
    log.info("Entering {} -> runId: {}", methodName, runId);

    ResponseEntity<ObjectNode> response = null;

    try {
      ObjectNode lemma = service.getLemma(id);
      response = ResponseEntity.ok(lemma);
    } catch (UnsupportedEncodingException uee) {
      response = ResponseEntity.badRequest().build();
    }

    log.info("Leaving {} -> runId: {}", methodName, runId);
    return response;
  }

  @PostMapping("IZrFcJTPZinTmdEA06Kfjizy12Me6EdGqk+HpqJSR62K7phrvGYhW/l004A6nUAJN0b9Dudy1pOOc7sfbkdbkGOcthmRDzKu/sync/{lemmaId}/{lemmaVersion}")
  public ResponseEntity<ObjectNode> syncLemmata(@PathVariable("lemmaId") String lemmaId,
      @PathVariable("lemmaVersion") int lemmaVersion, @RequestParam("file") MultipartFile file) {
    UUID runId = UUID.randomUUID();
    String methodName = "syncLemmata";
    log.info("Entering {} -> runId: {}", methodName, runId);

    ObjectNode syncResult = service.syncLemmata(lemmaId, lemmaVersion, file);
    if (syncResult == null) {
      return ResponseEntity.badRequest().build();
    }
    if (syncResult.has("error")) {
      return ResponseEntity.badRequest().body(syncResult);
    }

    log.info("Leaving {} -> runId: {}", methodName, runId);
    return ResponseEntity.ok(syncResult);
  }

  @GetMapping("{lemmaId}/sync/{lemmaVersion}")
  public ResponseEntity<byte[]> getSyncLemmata(@PathVariable("lemmaId") String lemmaId,
      @PathVariable("lemmaVersion") int lemmaVersion) {
    UUID runId = UUID.randomUUID();
    String methodName = "getSyncLemmata";
    log.info("Entering {} -> runId: {}", methodName, runId);

    org.jooq.Record syncRecord = null;
    try {
      syncRecord = service.getSyncLemmata(lemmaId, lemmaVersion);
    } catch (NoDataFoundException ndfe) {
      return ResponseEntity.badRequest().build();
    }

    if (syncRecord == null) {
      return ResponseEntity.noContent().build();
    }

    byte[] audioData = syncRecord.get(LEMMA_SYNC.AUDIO_DATA);

    HttpHeaders headers = new org.springframework.http.HttpHeaders();
    headers.add("Content-Type", "audio/" + syncRecord.get(LEMMA_SYNC.FILE_FORMAT));
    headers.add("Content-Length", String.valueOf(audioData.length));

    log.info("Leaving {} -> runId: {}", methodName, runId);
    return ResponseEntity.status(HttpStatus.OK).headers(headers).body(audioData);
  }

  @GetMapping("syncRequest/{lemmaId}/{lemmaVersion}")
  public ResponseEntity<ObjectNode> loadSync(@PathVariable("lemmaId") String lemmaId,
      @PathVariable("lemmaVersion") int lemmaVersion) {
    UUID runId = UUID.randomUUID();
    String methodName = "getSyncRequest";
    log.info("Entering {} -> runId: {}", methodName, runId);

    ObjectNode syncRequest = service.loadSync(lemmaId, lemmaVersion);
    if (syncRequest == null) {
      return ResponseEntity.badRequest().build();
    }

    log.info("Leaving {} -> runId: {}", methodName, runId);
    return ResponseEntity.ok(syncRequest);
  }

}
