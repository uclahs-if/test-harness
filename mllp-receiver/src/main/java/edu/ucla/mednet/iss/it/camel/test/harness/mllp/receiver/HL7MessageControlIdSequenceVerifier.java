package edu.ucla.mednet.iss.it.camel.test.harness.mllp.receiver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HL7MessageControlIdSequenceVerifier implements Processor {
  static final Pattern HL7_MESSAGE_CONTROL_ID_PATTERN = Pattern.compile("MSH\\|(?:[^|]*\\|){8}(\\d+)\\|.*");
  String exchangeProperty = this.getClass().getSimpleName();
  Logger log = LoggerFactory.getLogger(this.getClass());
  int lastMessageControlId = -1;

  @Override
  public void process(Exchange exchange) {
    Message message = exchange.getMessage();

    String body = message.getBody(String.class);
    if (null != body) {
      Matcher matcher = HL7_MESSAGE_CONTROL_ID_PATTERN.matcher(body);
      if (matcher.find()) {
        String messageControlIdString = matcher.group(1);
        exchange.setProperty(exchangeProperty + ".LastMessageControlId", String.format("%05d", lastMessageControlId));
        exchange.setProperty(exchangeProperty + ".MessageControlId", messageControlIdString);
        try {
          int messageControlId = Integer.parseInt(messageControlIdString);
          if (-1 != lastMessageControlId) {
            if (lastMessageControlId < messageControlId) {
              if (lastMessageControlId + 1 != messageControlId) {
                exchange.setProperty(exchangeProperty + ".Result", "NOT_SEQUENTIAL");
                log.warn("Message Control Id's are not sequential: {} {}", lastMessageControlId, messageControlId);
              } else {
                exchange.setProperty(exchangeProperty + ".Result", "SEQUENTIAL");
              }
            } else {
              exchange.setProperty(exchangeProperty + ".Result", "NOT_INCREASING");
              log.error("Message Control Id's are not increasing: {} {}", lastMessageControlId, messageControlId);
            }
          } else {
            exchange.setProperty(exchangeProperty + ".Result", "SEQUENTIAL");
          }
          lastMessageControlId = messageControlId;
        } catch (NumberFormatException nfe) {
          exchange.setProperty(exchangeProperty + ".Result", "PARSE_FAILURE");
          log.error("Failed to parse Message Control Id String: {}", messageControlIdString);
        }
      } else {
        exchange.setProperty(exchangeProperty + ".Result", "PATTERN_MATCH_FAILED");
        log.warn("Pattern failed to match message control Id");
      }

    } else {
      exchange.setProperty(exchangeProperty + ".Result", "NO_BODY");
      log.warn("Message Body not found");
    }
  }

  public String getExchangeProperty() {
    return exchangeProperty;
  }

  public void setExchangeProperty(String exchangeProperty) {
    this.exchangeProperty = exchangeProperty;
  }
}
