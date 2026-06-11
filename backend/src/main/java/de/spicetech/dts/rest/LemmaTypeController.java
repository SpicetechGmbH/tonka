package de.spicetech.dts.rest;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.spicetech.dts.service.LemmaTypeService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("rest/lemma-types")
public class LemmaTypeController {

  @Autowired
  private LemmaTypeService service;

  @GetMapping
  public ResponseEntity<ObjectNode> getLemmaTypes() {
    UUID runId = UUID.randomUUID();
    String methodName = "getLemmaTypes";
    log.info("Entering {} -> runId: {}", methodName, runId);

    ObjectNode responseObject = JsonNodeFactory.instance.objectNode();
    ArrayNode lemmaTypes = service.getLemmaTypes();
    responseObject.set("lemmaTypes", lemmaTypes);

    return ResponseEntity.ok(responseObject);
  }
}
