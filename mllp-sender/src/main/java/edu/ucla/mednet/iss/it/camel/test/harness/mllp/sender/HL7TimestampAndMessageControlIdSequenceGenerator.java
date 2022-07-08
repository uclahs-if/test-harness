package edu.ucla.mednet.iss.it.camel.test.harness.mllp.sender;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Replace the HL7 Timestamp (MSH-7) with a current timestamp and
 * the Message Control ID (MSH-10) in the payload with a sequential value.
 */
public class HL7TimestampAndMessageControlIdSequenceGenerator implements Processor {
  static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
  static final Pattern HL7_TIMESTAMP_AND_MESSAGE_CONTROL_ID_PATTERN = Pattern.compile("(?s)(MSH\\|(?:[^|]*\\|){5})(\\d+)((?:[^|]*\\|){3})(\\d+)(\\|.*)");
  Logger log = LoggerFactory.getLogger(this.getClass());
  int nextMessageControlId = 10000;


  /**
   * Processes the message exchange.
   * @param exchange  the message exchange
   * @throws Exception  – if an internal processing error has occurred.
   */
  @Override
  public void process(Exchange exchange) throws Exception {
    Message message = exchange.getMessage();

    String body = message.getBody(String.class);
    if (null != body) {
      Matcher matcher = HL7_TIMESTAMP_AND_MESSAGE_CONTROL_ID_PATTERN.matcher(body);
      if (matcher.matches()) {
        String newTimestamp = dateFormat.format(new Date());
        String newMessageControlId = String.format("%05d", nextMessageControlId++);
        String newBody = matcher.group(1)
            + newTimestamp
            + matcher.group(3)
            + newMessageControlId
            + matcher.group(5);

        message.setBody(newBody);
        message.setHeader(this.getClass().getSimpleName(), newMessageControlId);
      } else {
        exchange.setProperty(this.getClass().getSimpleName() + ".Result", "PATTERN_MATCH_FAILED");
        log.warn("Pattern failed to match HL7 Timestamp Message Control ID");
      }
    } else {
      log.warn("Message Body not found");
    }
  }

  public int getNextMessageControlId() {
    return nextMessageControlId;
  }

  public void setNextMessageControlId(int nextMessageControlId) {
    this.nextMessageControlId = nextMessageControlId;
  }
}
