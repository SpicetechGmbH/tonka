package de.spicetech.dts.rest;

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

import de.spicetech.dts.service.MapService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("rest/map")
public class MapController {

  @Autowired
  MapService service;

  @GetMapping("{mapType}")
  public ResponseEntity<ObjectNode> getMaps(@PathVariable("mapType") String mapType) {
    UUID runId = UUID.randomUUID();
    String methodName = "getMaps";
    log.info("Entering {} -> runId: {}", methodName, runId);

    ArrayNode maps = service.getMaps(mapType);
    ObjectNode response = JsonNodeFactory.instance.objectNode();
    response.set("maps", maps);

    log.info("Leaving {} -> runId: {}", methodName, runId);
    return ResponseEntity.ok(response);
  }

}
