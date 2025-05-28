package de.spicetech.dts.rest;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.spicetech.dts.service.QueryService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("rest/query")
public class QueryController {

  @Autowired
  QueryService service;

  @GetMapping("article/{queryString}")
  public ResponseEntity<ObjectNode> group2Query(@PathVariable String queryString) {
    UUID runId = UUID.randomUUID();
    String methodName = "group2Query";
    log.info("Entering {} -> runId: {}", methodName, runId);

    ObjectNode response = service.group2Query(queryString);

    log.info("Leaving {} -> runId: {}", methodName, runId);
    return ResponseEntity.ok(response);
  }

  @GetMapping("illustration/{queryString}")
  public ResponseEntity<ArrayNode> groupIllustration2Query(@PathVariable String queryString) {
    UUID runId = UUID.randomUUID();
    String methodName = "groupIllustration2Query";
    log.info("Entering {} -> runId: {}", methodName, runId);

    ArrayNode response = service.groupIllustration2Query(queryString);

    log.info("Leaving {} -> runId: {}", methodName, runId);
    return ResponseEntity.ok(response);
  }

  @GetMapping("map/{queryString}")
  public ResponseEntity<ArrayNode> groupMap2Query(@PathVariable String queryString) {
    UUID runId = UUID.randomUUID();
    String methodName = "groupMap2Query";
    log.info("Entering {} -> runId: {}", methodName, runId);

    ArrayNode response = service.groupMap2Query(queryString);

    log.info("Leaving {} -> runId: {}", methodName, runId);
    return ResponseEntity.ok(response);
  }

  @GetMapping("net/{queryString}")
  public ResponseEntity<ArrayNode> groupNet2Query(@PathVariable String queryString) {
    UUID runId = UUID.randomUUID();
    String methodName = "groupNet2Query";
    log.info("Entering {} -> runId: {}", methodName, runId);

    ArrayNode response = service.groupNet2Query(queryString);

    log.info("Leaving {} -> runId: {}", methodName, runId);
    return ResponseEntity.ok(response);
  }

}
