package edu.ucla.mednet.iss.it.camel.test.harness.mllp.sender;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HL7AcknowlegementVerifier implements Processor {
  Logger log = LoggerFactory.getLogger(this.getClass());

  @Override
  public void process(Exchange exchange) {
    Message message;
    if (exchange.hasOut()) {
      message = exchange.getOut();
    } else {
      message = exchange.getIn();
    }

    String acknowledgement = message.getHeader("CamelMllpAcknowledgement", String.class);
    if (null != acknowledgement) {
      if (acknowledgement.contains("\rMSA|AA|")) {
        log.trace("Received AA");
      } else if (acknowledgement.contains("\rMSA|AR|")) {
        log.warn("Received AR:\n\t{}", acknowledgement);
      } else if (acknowledgement.contains("\rMSA|AE|")) {
        log.error("Received AE:\n\r{}", acknowledgement);
      } else {
        log.error("Couldn't find acknowledgement in message: \n\t{}", acknowledgement.replace('\r', '\n'));
      }

    } else {
      log.warn("Message Body not found");
    }
  }
}
