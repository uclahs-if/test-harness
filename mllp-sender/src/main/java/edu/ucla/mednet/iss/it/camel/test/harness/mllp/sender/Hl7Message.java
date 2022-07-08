package edu.ucla.mednet.iss.it.camel.test.harness.mllp.sender;

public class Hl7Message {
  public static final  String ADT = "MSH|^~\\&|ADT|EPIC|JCAPS|CC|20150602113948|1316|ADT^A08|60038|D|2.3^^|||||||\r"
      + "EVN|A05|20150602113948||ADT_EVENT|1316^Patient^Test^^^^^^UCLA^^^^^UCLA||\r"
      + "PID|1|1100832^^^MRN^MRN|1100832^^^MRN^MRN||TEST^FIG||19800101|M||R|435 MAIN STREET^^LONGMONT^CO^80503^USA^P^^BOULDER|BOULDER|(555)555-1234^P^PH^^^720^9501234~^NET^Internet^test@gmail.com||ENGLISH|S||90000028082|111-22-3333|||R||||||||N||\r"
      + "PD1|||UCLA HEALTH SYSTEM^^10|||||||||||||||\r"
      + "NK1|1|TEST^BFIG^^|FRI|435 Main Street^^LONGMONT^CO^80503^USA|(720)950-1234^^^^^720^9501234||Emergency Contact 1|||||||||||||||||||||||||||\r"
      + "PV1|1|EMERGENCY|OCCU HLTH^^^1000^^^^^^^DEPID|EM|||003246^BARRETT^CYNTHIA^T.^^^^^EPIC^^^^PROVID|||3ED||||HOM|||003246^BARRETT^CYNTHIA^T.^^^^^EPIC^^^^PROVID||90000028082|SELF|||||||||||||||||||||PRE_CONF|^^^1000^^^^^^^||||||||||||\r"
      + "PV2|1|2|3|4|5|6|7|20150602113300|9|10|11|12|13|4|5|6|7|8|9|20|y|N|23|24|25|6|7|8|9|0|1|2|3|4|5|6|7|8|9|0|1|2|3|4|5|6|7|8|\r"
      + "ZPV||||||||||||||||||||||\r"
      + "DG1|1||^aa|aa||ADMDXTEXT\r"
      + "GT1|1|1000000058|TEST,FIG||435 MAIN STREET^^LONGMONT^CO^80503^USA^^^BOULDER|(555)555-1234^^^^^720^9501234||19800427|M|CASE RATE|SLF|125-22-333|||||FDGDFDF^^^^^USA|(626)415-1111^^^^^626^4151111||Full|||||||||||||||||||||||||||||\r"
      + "UB2||||||||\r";

}
