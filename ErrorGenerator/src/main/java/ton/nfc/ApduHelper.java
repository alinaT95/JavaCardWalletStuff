package ton.nfc;


import static ton.nfc.CoinManagerApduCommands.COIN_MANAGER_CLA;
import static ton.nfc.CoinManagerApduCommands.getCoinManagerApduCommandName;
import static ton.nfc.Constants.*;
import static ton.nfc.TonWalletAppletApduCommands.WALLET_APPLET_CLA;
import static ton.nfc.TonWalletAppletApduCommands.getTonWalletAppletApduCommandName;

public class ApduHelper {

  private static final ByteArrayUtil BYTE_ARRAY_HELPER = ByteArrayUtil.getInstance();

  private static ApduHelper instance;

  public static ApduHelper getInstance() {
    if (instance == null) {
      instance = new ApduHelper();
    }
    return instance;
  }

  private ApduHelper() {}

  public String getApduCommandName(CAPDU commandAPDU) {
    if (isSelectAPDU(commandAPDU)) {
      if (commandAPDU.getData().length == 0)
        return SELECT_COIN_MANAGER_APDU_NAME;
      else return SELECT_TON_WALLET_APPLET_APDU_NAME;
    }
    if (commandAPDU.getCla() == WALLET_APPLET_CLA && getTonWalletAppletApduCommandName(commandAPDU.getIns()) != null)
      return getTonWalletAppletApduCommandName(commandAPDU.getIns());
    if (commandAPDU.getCla() == (byte) COIN_MANAGER_CLA)
      return getCoinManagerApduCommandName(BYTE_ARRAY_HELPER.hex(commandAPDU.getData()));
    return null;
  }

  private boolean isSelectAPDU(CAPDU commandAPDU) {
    return commandAPDU.getCla() == SELECT_CLA && commandAPDU.getIns() == (byte) SELECT_INS &&  commandAPDU.getP1() == (byte) SELECT_P1 &&  commandAPDU.getP2() == (byte) SELECT_P2;
  }
}
