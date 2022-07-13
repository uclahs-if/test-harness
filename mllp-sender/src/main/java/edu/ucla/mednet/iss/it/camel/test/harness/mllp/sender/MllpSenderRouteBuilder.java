package edu.ucla.mednet.iss.it.camel.test.harness.mllp.sender;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.dataset.SimpleDataSet;
import org.apache.camel.component.mllp.MllpAcknowledgementException;
import org.apache.camel.component.mllp.MllpAcknowledgementReceiveException;
import org.apache.camel.model.OnExceptionDefinition;
import org.apache.camel.model.ProcessorDefinition;


public class MllpSenderRouteBuilder extends RouteBuilder {
  String routeId;
  LoggingLevel loggingLevel;

 // String sourceFile = "../../../shared-resources/data/ADT_A08_1K.hl7";
  Processor acknowledgementVerifier = new HL7AcknowlegementVerifier();
  String dataSetName = "source-dataset";
  Integer initialDelay = 1000;
  Integer produceDelay = 0;
  String mllpHost = "localhost";
  Integer mllpPort = 7777;
  int repeatCount = 100;
  int throughputLoggerGroupSize = 1;

  public MllpSenderRouteBuilder() {
  }

  public MllpSenderRouteBuilder(String routeId) {
    this.routeId = routeId;
  }

  @Override
  public void configure() throws Exception {
    verifyConfiguration();

    errorHandler(
        defaultErrorHandler()
            .allowRedeliveryWhileStopping(false)
    );

    OnExceptionDefinition retryForeverRedeliveryHandler =
        onException(ConnectException.class, SocketException.class, SocketTimeoutException.class, MllpAcknowledgementException.class).id(routeId + ": Retry Forever Handler")
            .handled(false)
            .maximumRedeliveries(-1)
            .redeliveryDelay(1000)
            .maximumRedeliveryDelay(15000)
            .backOffMultiplier(2)
            .logRetryAttempted(true)
            .logContinued(true)
            .logExhausted(true)
            .retryAttemptedLogLevel(LoggingLevel.WARN)
            .retriesExhaustedLogLevel(LoggingLevel.WARN);

    HL7TimestampAndMessageControlIdSequenceGenerator hl7TimestampAndMessageControlIdSequenceGenerator = new HL7TimestampAndMessageControlIdSequenceGenerator();
    hl7TimestampAndMessageControlIdSequenceGenerator.setNextMessageControlId(10000);

    SimpleDataSet dataset = new SimpleDataSet();
    dataset.setDefaultBody(Hl7Message.ADT);
    dataset.setSize(repeatCount);
    dataset.setReportCount(throughputLoggerGroupSize);
    dataset.setOutputTransformer(hl7TimestampAndMessageControlIdSequenceGenerator);

    // Add the file dataset to the camel registry.
    this.getContext().getRegistry().bind(dataSetName, dataset);

    ProcessorDefinition<?> routeDefinition = fromF("dataset:%s?initialDelay=%d&produceDelay=%d", dataSetName, initialDelay, produceDelay)
        .routeId(routeId)
        .log(loggingLevel, "SendingMessage: ${header[HL7MessageControlIdSequenceGenerator]}")
        .toF("mllp:%s:%d", mllpHost, mllpPort)
        .log(loggingLevel, "Receiving Acknowledgement: $simple{header[CamelMllpMessageControlId]}");

    if (hasAcknowledgementVerifier()) {
      routeDefinition.process(acknowledgementVerifier);
    }
  }

  void verifyConfiguration() {
    if (!hasMllpHost()) {
      throw new IllegalStateException("MLLP Host must be specified");
    }
    if (!hasMllpPort()) {
      throw new IllegalStateException("MLLP Port must be specified");
    }

    if (!hasDataSetName()) {
      throw new IllegalStateException("DataSet name must be specified");
    }

    if (!hasRouteId()) {
      routeId = String.format("mllp-sender-%s:%d", mllpHost, mllpPort);
    }

    if (!hasLoggingLevel()) {
      loggingLevel = LoggingLevel.DEBUG;
    }

    if (!hasInitialDelay()) {
      this.initialDelay = 1000;
    }

    if (!hasProduceDelay()) {
      this.produceDelay = 50;
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

  public boolean hasLoggingLevel() {
    return loggingLevel != null;
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

  public boolean hasAcknowledgementVerifier() {
    return acknowledgementVerifier != null;
  }

  public Processor getAcknowledgementVerifier() {
    return acknowledgementVerifier;
  }

  public void setAcknowledgementVerifier(Processor acknowledgementVerifier) {
    this.acknowledgementVerifier = acknowledgementVerifier;
  }

  public boolean hasDataSetName() {
    return dataSetName != null && !dataSetName.isEmpty();
  }

  public String getDataSetName() {
    return dataSetName;
  }

  public void setDataSetName(String dataSetName) {
    this.dataSetName = dataSetName;
  }

  public boolean hasInitialDelay() {
    return initialDelay != null;
  }

  public Integer getInitialDelay() {
    return initialDelay;
  }

  public void setInitialDelay(Integer initialDelay) {
    this.initialDelay = initialDelay;
  }

  public boolean hasProduceDelay() {
    return produceDelay != null;
  }

  public Integer getProduceDelay() {
    return produceDelay;
  }

  public void setProduceDelay(Integer produceDelay) {
    this.produceDelay = produceDelay;
  }

  public boolean hasMllpHost() {
    return mllpHost != null && !mllpHost.isEmpty();
  }

  public String getMllpHost() {
    return mllpHost;
  }

  public void setMllpHost(String mllpHost) {
    this.mllpHost = mllpHost;
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

}
