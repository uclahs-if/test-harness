package edu.ucla.mednet.iss.it.camel.test.harness.mllp.sender;

import org.apache.camel.main.Main;

public class MainApp {

  /**
   * A main() so we can easily run these routing rules in our IDE
   */
  public static void main(String... args) throws Exception {
    Main main = new Main(MainApp.class);
    main.run(args);
  }

}
