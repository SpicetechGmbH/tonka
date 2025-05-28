package de.spicetech.dts.rest;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.node.ObjectNode;

import de.spicetech.dts.service.IllustrationService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("rest/illustration")
public class IllustrationController {

  @Autowired
  IllustrationService service;

  @GetMapping("{id}")
  public ResponseEntity<ObjectNode> getIllustration(@PathVariable("id") String id) {
    UUID runId = UUID.randomUUID();
    String methodName = "getIllustration";
    log.info("Entering {} -> runId: {}", methodName, runId);

    ResponseEntity<ObjectNode> response = null;

    try {
      ObjectNode illustration = service.getIllustration(id);
      response = ResponseEntity.ok(illustration);
    } catch (Exception e) {
      log.error("Error in {} -> runId: {}", methodName, runId, e);
      response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    log.info("Leaving {} -> runId: {}", methodName, runId);
    return response;
  }

  @GetMapping("{lemmaId}/{nr}")
  public ResponseEntity<ObjectNode> getIllustration(@PathVariable("lemmaId") String lemmaId,
      @PathVariable("nr") int nr) {
    UUID runId = UUID.randomUUID();
    String methodName = "getIllustration";
    log.info("Entering {} -> runId: {}", methodName, runId);

    ResponseEntity<ObjectNode> response = null;

    try {
      ObjectNode illustration = service.getIllustration(lemmaId, nr);
      response = ResponseEntity.ok(illustration);
    } catch (Exception e) {
      log.error("Error in {} -> runId: {}", methodName, runId, e);
      response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    log.info("Leaving {} -> runId: {}", methodName, runId);
    return response;
  }

}
