package de.spicetech.dts.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.node.ObjectNode;

import de.spicetech.dts.service.StartMessageService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("rest/startMessage")
public class StartMessageController {

  @Autowired
  StartMessageService service;

  @GetMapping
  public ResponseEntity<ObjectNode> getCurrentStartMessage() {
    ObjectNode startMessage = service.readCurrentStartMessage();
    if (startMessage == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(startMessage);
  }

}
