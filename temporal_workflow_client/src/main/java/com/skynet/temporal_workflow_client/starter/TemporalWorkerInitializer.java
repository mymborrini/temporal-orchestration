package com.skynet.temporal_workflow_client.starter;

import io.temporal.worker.WorkerFactory;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TemporalWorkerInitializer {

  private static final Logger log = LoggerFactory.getLogger(TemporalWorkerInitializer.class);
  private final WorkerFactory workerFactory;

  @Value("${temporal_workflow_client.activities.exceptions.arrangeTransport}")
  private boolean exceptionsArrangeTransport;


  /**
   * Starts the Temporal worker after the Spring context is initialized
   */
  @PostConstruct
  public void init() {
    log.info("Initializing temporal worker factory. Exceptions in arrange transport?: {}", exceptionsArrangeTransport);
    workerFactory.start();
  }

}
