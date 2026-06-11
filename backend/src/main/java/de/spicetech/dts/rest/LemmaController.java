package de.spicetech.dts.rest;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
