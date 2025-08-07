package com.skynet.temporal_workflow_client.starter;

import io.temporal.worker.WorkerFactory;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TemporalWorkerInitializer {

  private final WorkerFactory workerFactory;


  /**
   * Starts the Temporal worker after the Spring context is initialized
   */
  @PostConstruct
  public void init() {
    workerFactory.start();
  }

}
