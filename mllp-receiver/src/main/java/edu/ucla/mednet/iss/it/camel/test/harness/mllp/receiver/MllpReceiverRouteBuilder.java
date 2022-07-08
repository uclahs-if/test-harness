package edu.ucla.mednet.iss.it.camel.test.harness.mllp.receiver;

import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

public class MllpReceiverRouteBuilder extends RouteBuilder {
  String routeId;
  LoggingLevel loggingLevel = LoggingLevel.DEBUG;

  Processor messageVerifier;

  Integer mllpPort = 1235;
  Integer throughputLoggerGroupSize = 1;

  public MllpReceiverRouteBuilder() {
  }

  public MllpReceiverRouteBuilder(String routeId) {
    this.routeId = routeId;
  }

  @Override
  public void configure() throws Exception {
    verifyConfiguration();

    fromF("mllp:0.0.0.0:%d", mllpPort)
        .routeId(routeId)
        .log(loggingLevel, routeId, "Receiving Message: $simple{header[CamelMllpMessageControlId]}")
        .toF("log:%s-throughput?level=INFO&groupSize=%d", routeId, throughputLoggerGroupSize)
        .process(messageVerifier)
        .filter(exchangeProperty("HL7MessageControlIdSequenceVerifier.Result").isNotEqualTo("SEQUENTIAL"))
          .toF("log:%s-verification?level=ERROR&showBody=false&showProperties=true&showHeaders=true", routeId)
        .end()
        .log(loggingLevel, routeId, "Sending Acknowledgement: $simple{header[CamelMllpMessageControlId]}")
    ;

  }

  void verifyConfiguration() {
    if (!hasMllpPort()) {
      throw new IllegalStateException("MLLP Port must be specified");
    }

    if (!hasMessageVerifier()) {
      messageVerifier = new HL7MessageControlIdSequenceVerifier();
    }

    if (!hasRouteId()) {
      routeId = String.format("mllp-receiver-0.0.0.0:%d", mllpPort);
    }

  }

  public boolean hasRouteId() {
    return routeId != null && !routeId.isEmpty();
  }

  public String getRouteId() {
    return routeId;
  }

  public void setRouteId(String routeId) {
    this.routeId = routeId;
  }

  public LoggingLevel getLoggingLevel() {
    return loggingLevel;
  }

  public void setLoggingLevel(LoggingLevel loggingLevel) {
    this.loggingLevel = loggingLevel;
  }

  public void setLoggingLevel(String loggingLevelString) {
    this.loggingLevel = LoggingLevel.valueOf(loggingLevelString);
  }

  public boolean hasMessageVerifier() {
    return messageVerifier != null;
  }

  public Processor getMessageVerifier() {
    return messageVerifier;
  }

  public void setMessageVerifier(Processor messageVerifier) {
    this.messageVerifier = messageVerifier;
  }

  public boolean hasMllpPort() {
    return mllpPort != null;
  }

  public Integer getMllpPort() {
    return mllpPort;
  }

  public void setMllpPort(Integer mllpPort) {
    this.mllpPort = mllpPort;
  }

  public boolean hasThroughputLoggerGroupSize() {
    return throughputLoggerGroupSize != null;
  }

  public Integer getThroughputLoggerGroupSize() {
    return throughputLoggerGroupSize;
  }

  public void setThroughputLoggerGroupSize(Integer throughputLoggerGroupSize) {
    this.throughputLoggerGroupSize = throughputLoggerGroupSize;
  }
}