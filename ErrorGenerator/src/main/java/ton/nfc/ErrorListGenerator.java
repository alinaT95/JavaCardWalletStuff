package ton.nfc;

import static ton.nfc.ErrorCodes.*;
import static ton.nfc.TonWalletAppletApduCommands.GET_APP_INFO_APDU;

import com.google.gson.*;
import org.json.JSONObject;

public class ErrorListGenerator {
  private static final ByteArrayUtil BYTE_ARRAY_HELPER = ByteArrayUtil.getInstance();

  public static void main(String[] args) throws Exception{
    Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

    JsonParser jp = new JsonParser();
    System.out.println("Start");
    JsonHelper jsonHelper = JsonHelper.getInstance();
    /*for(Short sw : codeToMsg.keySet()) {
      JsonElement je = jp.parse(jsonHelper.createErrorJsonForCardException(BYTE_ARRAY_HELPER.hex(sw), GET_APP_INFO_APDU));
      System.out.println(gson.toJson(je));
    }*/
    for(String errMsg : ResponsesConstants.errorMsgToErrorCodeMap.keySet()) {
      JsonElement je = jp.parse(jsonHelper.createErrorJson(errMsg));
      System.out.println(gson.toJson(je));
    }

  }
}
