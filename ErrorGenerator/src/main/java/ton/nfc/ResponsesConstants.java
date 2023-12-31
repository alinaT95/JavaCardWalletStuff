package ton.nfc;

import java.util.*;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ton.nfc.CoinManagerApduCommands.*;
import static ton.nfc.TonWalletAppletApduCommands.*;
import static ton.nfc.TonWalletConstants.*;


public class ResponsesConstants {
  /**
   * Error types
   */

  public static final String CARD_ERROR_TYPE_ID = "0";
  public static final String ANDROID_INTERNAL_ERROR_TYPE_ID = "9";
  public static final String NFC_INTERRUPTION_TYPE_ID = "2";
  public static final String ANDROID_NFC_ERROR_TYPE_ID = "22";
  public static final String INPUT_DATA_FORMAT_ERROR_TYPE_ID = "3";
  public static final String CARD_RESPONSE_DATA_ERROR_TYPE_ID = "4";
  public static final String IMPROPER_APPLET_STATE_ERROR_TYPE_ID = "5";
  public static final String ANDROID_KEYSTORE_HMAC_KEY_ERROR_TYPE_ID = "8";
  public static final String WRONG_CARD_ERROR_TYPE_ID = "7";

  public static final List<String> ERROR_TYPE_IDS = Arrays.asList(
          CARD_ERROR_TYPE_ID,
          ANDROID_INTERNAL_ERROR_TYPE_ID,
          NFC_INTERRUPTION_TYPE_ID,
          ANDROID_NFC_ERROR_TYPE_ID,
          INPUT_DATA_FORMAT_ERROR_TYPE_ID,
          CARD_RESPONSE_DATA_ERROR_TYPE_ID,
          IMPROPER_APPLET_STATE_ERROR_TYPE_ID,
          ANDROID_KEYSTORE_HMAC_KEY_ERROR_TYPE_ID,
          WRONG_CARD_ERROR_TYPE_ID);

  public static final String CARD_ERROR_TYPE_MSG = "Applet fail: card operation error";
  public static final String ANDROID_INTERNAL_ERROR_TYPE_MSG = "Android code fail: internal error";
  public static final String NFC_INTERRUPTION_TYPE_MSG = "Native code fail: NFC connection interruption";
  public static final String ANDROID_NFC_ERROR_TYPE_MSG = "Android code fail: NFC error";
  public static final String INPUT_DATA_FORMAT_ERROR_TYPE_MSG = "Native code fail: incorrect format of input data";
  public static final String CARD_RESPONSE_DATA_ERROR_TYPE_MSG = "Native code fail: incorrect response from card";
  public static final String IMPROPER_APPLET_STATE_ERROR_TYPE_MSG = "Native code fail: improper applet state";
  public static final String ANDROID_KEYSTORE_HMAC_KEY_ERROR_TYPE_MSG = "Native code (Android) fail: hmac key issue";
  public static final String WRONG_CARD_ERROR_TYPE_MSG = "Native code fail: wrong card";

  public static final List<String> ERROR_TYPE_MSGS = Arrays.asList(
          CARD_ERROR_TYPE_MSG,
          ANDROID_INTERNAL_ERROR_TYPE_MSG,
          NFC_INTERRUPTION_TYPE_MSG,
          ANDROID_NFC_ERROR_TYPE_MSG,
          INPUT_DATA_FORMAT_ERROR_TYPE_MSG,
          CARD_RESPONSE_DATA_ERROR_TYPE_MSG,
          IMPROPER_APPLET_STATE_ERROR_TYPE_MSG,
          ANDROID_KEYSTORE_HMAC_KEY_ERROR_TYPE_MSG,
          WRONG_CARD_ERROR_TYPE_MSG);

  private static Map<String, String> errorTypeIdToErrorTypeMsgMap = new HashMap<>();

  /**
   * ANDROID_INTERNAL_ERROR_TYPE_ID = 9
   */
  public static final String ERROR_MSG_APDU_EMPTY = "Apdu command is null";
  public static final String ERROR_MSG_APDU_DATA_FIELD_LEN_INCORRECT = "Data field in APDU must have length > 0 and <= 255 bytes.";
  public static final String ERROR_MSG_SW_TOO_SHORT = "APDU response bytes are incorrect. It must contain at least 2 bytes of status word (SW) from the card.";
  public static final String ERROR_MSG_APDU_RESPONSE_TOO_LONG = "APDU response is incorrect. Response from the card can not contain > 255 bytes.";
  public static final String ERROR_MSG_PIN_BYTES_SIZE_INCORRECT = "Pin byte array must have length " + PIN_SIZE + ".";
  public static final String ERROR_MSG_LABEL_BYTES_SIZE_INCORRECT = "Device label byte array must have length " + LABEL_LENGTH + ".";
  public static final String ERROR_MSG_ACTIVATION_PASSWORD_BYTES_SIZE_INCORRECT = "Activation password byte array must have length " + PASSWORD_SIZE + ".";
  public static final String ERROR_MSG_INITIAL_VECTOR_BYTES_SIZE_INCORRECT = "Initial vector byte array must have length " + IV_SIZE + ".";
  public static final String ERROR_MSG_DATA_BYTES_SIZE_INCORRECT = "Data for signing byte array must have length > 0 and <= " + DATA_FOR_SIGNING_MAX_SIZE + ".";
  public static final String ERROR_MSG_DATA_WITH_HD_PATH_BYTES_SIZE_INCORRECT = "Data for signing byte array must have length > 0 and <= " + DATA_FOR_SIGNING_MAX_SIZE_FOR_CASE_WITH_PATH + ".";
  public static final String ERROR_MSG_APDU_P1_INCORRECT = "APDU parameter P2 must take value from {0, 1, 2}.";
  public static final String ERROR_MSG_KEY_CHUNK_BYTES_SIZE_INCORRECT = "Key (from keyChain) chunk byte array must have length > 0 and <= " + DATA_PORTION_MAX_SIZE + ".";
  public static final String ERROR_MSG_KEY_MAC_BYTES_SIZE_INCORRECT = "Key (from keyChain) mac byte array must have length " + HMAC_SHA_SIG_SIZE + ".";
  public static final String ERROR_MSG_SAULT_BYTES_SIZE_INCORRECT = "Sault byte array must have length " + SAULT_LENGTH + ".";
  public static final String ERROR_MSG_HD_INDEX_BYTES_SIZE_INCORRECT = "hdIndex byte array must have length > 0 and <= " + MAX_HD_INDEX_SIZE + ".";
  public static final String ERROR_MSG_KEY_INDEX_BYTES_SIZE_INCORRECT =  "Key (from keyChain) index byte array must have length = " + KEYCHAIN_KEY_INDEX_LEN + ".";
  public static final String ERROR_MSG_LENGTH_IS_NOT_POSITIVE = "Length is not a positive value";
  public static final String ERROR_MSG_INTENT_EMPTY =  "Intent is null.";
  public static final String ERROR_MSG_SOURCE_ARRAY_IS_NULL = "Source array is null";
  //public static final String ERROR_MSG_SOURCE_ARRAY_IS_EMPTY = "Source array is empty";
  public static final String ERROR_MSG_DEST_ARRAY_IS_NULL = "Destination array is null";
  public static final String ERROR_MSG_ARRAY_ELEMENTS_ARE_NOT_DIGITS = "All array elements must be decimal digits >= 0 and < 9";
  public static final String ERROR_MSG_ARRAY_TO_MAKE_DIGITAL_STR_MUST_NOT_BE_EMPTY = "Can not convert empty array (or null) into digital string.";
  public static final String ERROR_MSG_NFC_CALLBACK_IS_NULL = "Nfc callback is null.";
  public static final String ERROR_MSG_EXCEPTION_OBJECT_IS_NULL = "Exception object is null.";
  public static final String ERROR_MSG_COUNT_IS_LESS_THEN_ZERO = "Count of is less than 0.";
  public static final String ERROR_MSG_COUNT_IS_TOO_BIG = "Count is greater than length of source array (";
  public static final String ERROR_MSG_FROM_IS_LESS_THEN_ZERO = "From is less than 0";
  public static final String ERROR_MSG_OFFSET_IS_OUT_OF_BOUND = "Offset is out of bound (";
  public static final String ERROR_MSG_END_INDEX_OUT_OF_BOUND = "End index is out of bound (";
  public static final String ERROR_MSG_ARRAYS_ARE_NULL = "All arrays arg are null.";
  public static final String ERROR_MSG_STRING_IS_NULL = "String is null.";
  public static final String ERROR_MSG_STRING_IS_NOT_CORRECT_HEX = "Source string is not correct hex: ";
  public static final String ERROR_MSG_SOURCE_OFFSET_IS_NOT_CORRECT = "Incorrect offset in source array.";
  public static final String ERROR_MSG_DEST_OFFSET_IS_NOT_CORRECT = "Incorrect offset in dest array.";
  public static final String ERROR_MSG_SOURCE_ARRAY_LENGTH_LESS_THAN_TWO = "Source array must have length >= 2.";
  public static final String ERROR_MSG_FIRST_ARRAY_IS_NULL = "First array is null";
  public static final String ERROR_MSG_SECOND_ARRAY_IS_NULL = "Second array is null";
  public static final String ERROR_MSG_NO_CONTEXT = "Context is null";
  public static final String ERROR_MSG_ERR_MSG_IS_NULL = "Error message is null.";
  public static final String ERROR_MSG_ERR_KEY_BYTES_FOR_HMAC_SHA256_IS_NULL = "Key for HMAC-SHA256 algorithm is null.";
  public static final String ERROR_MSG_ERR_KEY_BYTES_FOR_HMAC_SHA256_IS_TOO_SHORT = "Key bytes for HMAC-SHA256 must have length >= " + SHA_HASH_SIZE + ".";
  public static final String ERROR_MSG_ERR_DATA_BYTES_FOR_HMAC_SHA256_IS_NULL = "Data to sign by HMAC-SHA256 algorithm is null.";
  public static final String ERROR_MSG_ERR_CURRENT_SERIAL_NUMBER_IS_NULL = "Current serial number is null.";
  public static final String ERROR_MSG_ERR_IS_NOT_SEC_KEY_ENTRY = "Not an instance of a SecretKeyEntry";
  public static final String ERROR_MSG_MALFORMED_JSON_MSG = "Malformed data for json (message is null).";
  public static final String ERROR_MSG_MALFORMED_SW_FOR_JSON = "Malformed SW for json.";
  public static final String ERROR_MSG_CAPDU_IS_NULL = "CAPDU is null.";
  public static final String ERROR_MSG_STRING_IS_NOT_ASCII = "String is not in ascii.";
  public static final String ERROR_MSG_APDU_DATA_FIELD_IS_NULL = "APDU data field is null.";
  public static final String ERROR_MSG_RECOVER_DATA_PORTION_SIZE_INCORRECT = "Recovery data portion must have length > 0 and <= " + DATA_RECOVERY_PORTION_MAX_SIZE + ".";
  public static final String ERROR_MSG_RECOVERY_DATA_MAC_BYTES_SIZE_INCORRECT = "Mac (byte array) of recovery data portion must have length " + HMAC_SHA_SIG_SIZE + ".";
  public static final String ERROR_MSG_START_POSITION_BYTES_SIZE_INCORRECT =  "Start position byte array must have length = 2.";
  public static final String ERROR_MSG_APDU_RESPONSE_IS_NULL =  "APDU response is null.";

  public static final List<String> ANDROID_INTERNAL_ERRORS = Arrays.asList(
          ERROR_MSG_APDU_EMPTY,
          ERROR_MSG_APDU_DATA_FIELD_LEN_INCORRECT,
          ERROR_MSG_SW_TOO_SHORT,
          ERROR_MSG_APDU_RESPONSE_TOO_LONG,
          ERROR_MSG_PIN_BYTES_SIZE_INCORRECT,
          ERROR_MSG_LABEL_BYTES_SIZE_INCORRECT,
          ERROR_MSG_ACTIVATION_PASSWORD_BYTES_SIZE_INCORRECT,
          ERROR_MSG_INITIAL_VECTOR_BYTES_SIZE_INCORRECT,
          ERROR_MSG_DATA_BYTES_SIZE_INCORRECT,
          ERROR_MSG_DATA_WITH_HD_PATH_BYTES_SIZE_INCORRECT,
          ERROR_MSG_APDU_P1_INCORRECT,
          ERROR_MSG_KEY_CHUNK_BYTES_SIZE_INCORRECT,
          ERROR_MSG_KEY_MAC_BYTES_SIZE_INCORRECT,
          ERROR_MSG_SAULT_BYTES_SIZE_INCORRECT,
          ERROR_MSG_HD_INDEX_BYTES_SIZE_INCORRECT,
          ERROR_MSG_KEY_INDEX_BYTES_SIZE_INCORRECT,
          ERROR_MSG_LENGTH_IS_NOT_POSITIVE,
          ERROR_MSG_INTENT_EMPTY,
          ERROR_MSG_SOURCE_ARRAY_IS_NULL,
          //ERROR_MSG_SOURCE_ARRAY_IS_EMPTY,
          ERROR_MSG_DEST_ARRAY_IS_NULL,
          ERROR_MSG_ARRAY_ELEMENTS_ARE_NOT_DIGITS,
          ERROR_MSG_ARRAY_TO_MAKE_DIGITAL_STR_MUST_NOT_BE_EMPTY,
          ERROR_MSG_NFC_CALLBACK_IS_NULL,
          ERROR_MSG_EXCEPTION_OBJECT_IS_NULL,
          ERROR_MSG_COUNT_IS_LESS_THEN_ZERO,
          ERROR_MSG_COUNT_IS_TOO_BIG,
          ERROR_MSG_FROM_IS_LESS_THEN_ZERO,
          ERROR_MSG_OFFSET_IS_OUT_OF_BOUND,
          ERROR_MSG_END_INDEX_OUT_OF_BOUND,
          ERROR_MSG_ARRAYS_ARE_NULL,
          ERROR_MSG_STRING_IS_NULL,
          ERROR_MSG_STRING_IS_NOT_CORRECT_HEX,
          ERROR_MSG_SOURCE_OFFSET_IS_NOT_CORRECT,
          ERROR_MSG_DEST_OFFSET_IS_NOT_CORRECT,
          ERROR_MSG_SOURCE_ARRAY_LENGTH_LESS_THAN_TWO,
          ERROR_MSG_FIRST_ARRAY_IS_NULL,
          ERROR_MSG_SECOND_ARRAY_IS_NULL,
          ERROR_MSG_NO_CONTEXT,
          ERROR_MSG_ERR_MSG_IS_NULL,
          ERROR_MSG_ERR_KEY_BYTES_FOR_HMAC_SHA256_IS_NULL,
          ERROR_MSG_ERR_KEY_BYTES_FOR_HMAC_SHA256_IS_TOO_SHORT,
          ERROR_MSG_ERR_DATA_BYTES_FOR_HMAC_SHA256_IS_NULL,
          ERROR_MSG_ERR_CURRENT_SERIAL_NUMBER_IS_NULL,
          ERROR_MSG_ERR_IS_NOT_SEC_KEY_ENTRY,
          ERROR_MSG_MALFORMED_JSON_MSG,
          ERROR_MSG_MALFORMED_SW_FOR_JSON,
          ERROR_MSG_CAPDU_IS_NULL,
          ERROR_MSG_STRING_IS_NOT_ASCII,
          ERROR_MSG_APDU_DATA_FIELD_IS_NULL,
          ERROR_MSG_RECOVER_DATA_PORTION_SIZE_INCORRECT,
          ERROR_MSG_RECOVERY_DATA_MAC_BYTES_SIZE_INCORRECT,
          ERROR_MSG_START_POSITION_BYTES_SIZE_INCORRECT,
          ERROR_MSG_APDU_RESPONSE_IS_NULL
  );


  /**
   * NFC_ERROR_TYPE_ID = 2
   */
  public static final String ERROR_NFC_CONNECTION_INTERRUPTED = "Nfc connection was interrupted by user.";

  public static final List<String>  NFC_INTERUPTION_ERRORS = Arrays.asList(ERROR_NFC_CONNECTION_INTERRUPTED);


  /**
   * ANDROID_NFC_ERROR_TYPE_ID = 22
   */
  public static final String ERROR_MSG_NFC_CONNECT = "Nfc connection establishing error.";
  public static final String ERROR_MSG_NFC_DISABLED = "Nfc is disabled.";
  public static final String ERROR_MSG_NO_NFC_HARDWARE = "Nfc hardware is not found for this smartphone.";
  public static final String ERROR_MSG_NO_TAG = "Nfc tag is not found.";
  public static final String ERROR_MSG_NFC_DISCONNECT = "Error happened during NFC tag disconnection.";
  public static final String ERROR_TRANSCEIVE = "Data transfer via NFC failed. Probably NFC connection was lost.";
  public static final String ERROR_BAD_RESPONSE = "Response from the card is too short. It must contain at least 2 bytes.";


  public static final List<String>  ANDROID_NFC_ERRORS = Arrays.asList(
          ERROR_MSG_NFC_CONNECT,
          ERROR_MSG_NFC_DISABLED,
          ERROR_MSG_NO_NFC_HARDWARE,
          ERROR_MSG_NO_TAG,
          ERROR_MSG_NFC_DISCONNECT,
          ERROR_TRANSCEIVE,
          ERROR_BAD_RESPONSE
  );

  /**
   * INPUT_DATA_FORMAT_ERROR_TYPE_ID = 3
   */
  public static final String ERROR_MSG_PASSWORD_LEN_INCORRECT = "Activation password is a hex string of length " + (2 * PASSWORD_SIZE) + ".";
  public static final String ERROR_MSG_COMMON_SECRET_LEN_INCORRECT = "Common secret is a hex string of length "  + (2 * COMMON_SECRET_SIZE) + ".";
  public static final String ERROR_MSG_INITIAL_VECTOR_LEN_INCORRECT = "Initial vector is a hex string of length "  + (2 * IV_SIZE) + ".";
  public static final String ERROR_MSG_PASSWORD_NOT_HEX = "Activation password is not a valid hex string.";
  public static final String ERROR_MSG_COMMON_SECRET_NOT_HEX = "Common secret is not a valid hex string.";
  public static final String ERROR_MSG_INITIAL_VECTOR_NOT_HEX = "Initial vector is not a valid hex string.";
  public static final String ERROR_MSG_PIN_LEN_INCORRECT = "Pin must be a numeric string of length " + PIN_SIZE + ".";
  public static final String ERROR_MSG_PIN_FORMAT_INCORRECT = "Pin is not a valid numeric string.";
  public static final String ERROR_MSG_DATA_FOR_SIGNING_NOT_HEX = "Data for signing is not a valid hex .";
  public static final String ERROR_MSG_DATA_FOR_SIGNING_LEN_INCORRECT = "Data for signing must be a nonempty hex string of even length > 0 and <= " + (2 * DATA_FOR_SIGNING_MAX_SIZE) + ".";
  public static final String ERROR_MSG_DATA_FOR_SIGNING_WITH_PATH_LEN_INCORRECT  =  "Data for signing must be a nonempty hex string of even length > 0 and <= " + (2 * DATA_FOR_SIGNING_MAX_SIZE_FOR_CASE_WITH_PATH) + ".";
  public static final String ERROR_MSG_RECOVERY_DATA_NOT_HEX = "Recovery data is not a valid hex string.";
  public static final String ERROR_MSG_RECOVERY_DATA_LEN_INCORRECT = "Recovery data is a hex string of length > 0 and <= " + (2 * RECOVERY_DATA_MAX_SIZE) + ".";
  public static final String ERROR_MSG_HD_INDEX_LEN_INCORRECT = "Hd index must be a numeric string of length > 0 and <= " + MAX_HD_INDEX_SIZE + ".";
  public static final String ERROR_MSG_HD_INDEX_FORMAT_INCORRECT = "Hd index is not a valid numeric string.";
  public static final String ERROR_MSG_DEVICE_LABEL_LEN_INCORRECT = "Device label must be a hex string of length " + (2 * LABEL_LENGTH) + ".";
  public static final String ERROR_MSG_DEVICE_LABEL_NOT_HEX = "Device label is not a valid hex string.";
  public static final String ERROR_MSG_KEY_HMAC_LEN_INCORRECT = "Key hmac is a hex string of length " + (2 * HMAC_SHA_SIG_SIZE) + ".";
  public static final String ERROR_MSG_KEY_HMAC_NOT_HEX = "Key hmac is not a valid hex string.";
  public static final String ERROR_MSG_KEY_NOT_HEX = "Key is not a valid hex string.";
  public static final String ERROR_MSG_KEY_LEN_INCORRECT = "Key is a hex string of length > 0 and <= " + (2 * MAX_KEY_SIZE_IN_KEYCHAIN) + ".";
  public static final String ERROR_MSG_KEY_SIZE_INCORRECT = "Key size must be > 0 and <= " + MAX_KEY_SIZE_IN_KEYCHAIN + ".";
  public static final String ERROR_MSG_NEW_KEY_LEN_INCORRECT = "Length of new key must be equal to length of old key = ";
  public static final String ERROR_MSG_KEY_INDEX_VALUE_INCORRECT =  "Key index is a numeric string representing integer >= 0 and <= " + (MAX_NUMBER_OF_KEYS_IN_KEYCHAIN - 1) + ".";
  public static final String ERROR_MSG_KEY_INDEX_STRING_NOT_NUMERIC =  "Key index is not a valid numeric string.";
  public static final String ERROR_MSG_SERIAL_NUMBER_LEN_INCORRECT = "Serial number is a numeric string of length "  + SERIAL_NUMBER_SIZE + ".";
  public static final String ERROR_MSG_SERIAL_NUMBER_NOT_NUMERIC = "Serial number is not a valid numeric string.";

  public static final List<String>  INPUT_DATA_FORMAT_ERRORS = Arrays.asList(
          ERROR_MSG_PASSWORD_LEN_INCORRECT,
          ERROR_MSG_COMMON_SECRET_LEN_INCORRECT,
          ERROR_MSG_INITIAL_VECTOR_LEN_INCORRECT,
          ERROR_MSG_PASSWORD_NOT_HEX,
          ERROR_MSG_COMMON_SECRET_NOT_HEX,
          ERROR_MSG_INITIAL_VECTOR_NOT_HEX,
          ERROR_MSG_PIN_LEN_INCORRECT,
          ERROR_MSG_PIN_FORMAT_INCORRECT,
          ERROR_MSG_DATA_FOR_SIGNING_NOT_HEX,
          ERROR_MSG_DATA_FOR_SIGNING_LEN_INCORRECT,
          ERROR_MSG_DATA_FOR_SIGNING_WITH_PATH_LEN_INCORRECT,
          ERROR_MSG_RECOVERY_DATA_NOT_HEX,
          ERROR_MSG_RECOVERY_DATA_LEN_INCORRECT,
          ERROR_MSG_HD_INDEX_LEN_INCORRECT,
          ERROR_MSG_HD_INDEX_FORMAT_INCORRECT,
          ERROR_MSG_DEVICE_LABEL_LEN_INCORRECT,
          ERROR_MSG_DEVICE_LABEL_NOT_HEX,
          ERROR_MSG_KEY_HMAC_LEN_INCORRECT,
          ERROR_MSG_KEY_HMAC_NOT_HEX,
          ERROR_MSG_KEY_NOT_HEX,
          ERROR_MSG_KEY_LEN_INCORRECT,
          ERROR_MSG_KEY_SIZE_INCORRECT,
          ERROR_MSG_NEW_KEY_LEN_INCORRECT,
          ERROR_MSG_KEY_INDEX_VALUE_INCORRECT,
          ERROR_MSG_KEY_INDEX_STRING_NOT_NUMERIC,
          ERROR_MSG_SERIAL_NUMBER_LEN_INCORRECT,
          ERROR_MSG_SERIAL_NUMBER_NOT_NUMERIC
  );

  /**
   * CARD_RESPONSE_DATA_ERROR_TYPE_ID = 4
   */
  public static final String ERROR_MSG_SAULT_RESPONSE_LEN_INCORRECT =  "Sault response from card must have length " + SAULT_LENGTH + ". Current length is ";
  public static final String ERROR_MSG_STATE_RESPONSE_LEN_INCORRECT =  "Applet state response from card must have length 1.";
  public static final String ERROR_MSG_STATE_RESPONSE_INCORRECT =  "Unknown applet state = ";
  public static final String ERROR_MSG_RECOVERY_DATA_HASH_RESPONSE_LEN_INCORRECT = "Recovery data hash must have length " + SHA_HASH_SIZE + ".";
  public static final String ERROR_MSG_RECOVERY_DATA_LENGTH_RESPONSE_LEN_INCORRECT = "Recovery data length byte array must have length 2.";
  public static final String ERROR_MSG_RECOVERY_DATA_LENGTH_RESPONSE_INCORRECT = "Recovery data length must be > 0 and <= " + RECOVERY_DATA_MAX_SIZE+ ".";
  public static final String ERROR_IS_RECOVERY_DATA_SET_RESPONSE_LEN_INCORRECT = "Response from IS_RECOVERY_DATA_SET card operation must have length 1.";
  public static final String ERROR_RECOVERY_DATA_PORTION_INCORRECT_LEN = "Recovery data portion must have length = ";
  public static final String ERROR_MSG_HASH_OF_ENCRYPTED_PASSWORD_RESPONSE_LEN_INCORRECT = "Hash of encrypted password must have length " + SHA_HASH_SIZE + ".";
  public static final String ERROR_MSG_HASH_OF_ENCRYPTED_COMMON_SECRET_RESPONSE_LEN_INCORRECT = "Hash of encrypted common secret must have length " + SHA_HASH_SIZE + ".";
  public static final String ERROR_MSG_HASH_OF_ENCRYPTED_COMMON_SECRET_RESPONSE_INCORRECT = "Card two-factor authentication failed: Hash of encrypted common secret is invalid.";
  public static final String ERROR_MSG_HASH_OF_ENCRYPTED_PASSWORD_RESPONSE_INCORRECT = "Card two-factor authentication failed: Hash of encrypted password is invalid.";
  public static final String ERROR_MSG_SIG_RESPONSE_LEN_INCORRECT = "Signature must have length " + SIG_LEN + ".";
  public static final String ERROR_MSG_PUBLIC_KEY_RESPONSE_LEN_INCORRECT = "Public key must have length " + PUBLIC_KEY_LEN + ".";
  public static final String ERROR_MSG_GET_NUMBER_OF_KEYS_RESPONSE_LEN_INCORRECT =  "Response from GET_NUMBER_OF_KEYS card operation must have length " + GET_NUMBER_OF_KEYS_LE + ".";
  public static final String ERROR_MSG_NUMBER_OF_KEYS_RESPONSE_INCORRECT = "Number of keys in keychain must be >= 0 and <= " + MAX_NUMBER_OF_KEYS_IN_KEYCHAIN + ".";
  public static final String ERROR_MSG_GET_OCCUPIED_SIZE_RESPONSE_LEN_INCORRECT = "Response from GET_OCCUPIED_SIZE card operation must have length " + GET_NUMBER_OF_KEYS_LE + ".";
  public static final String ERROR_MSG_GET_FREE_SIZE_RESPONSE_LEN_INCORRECT = "Response from GET_FREE_SIZE_RESPONSE card operation must have length " + GET_NUMBER_OF_KEYS_LE + ".";
  public static final String ERROR_MSG_OCCUPIED_SIZE_RESPONSE_INCORRECT = "Occupied size in keychain can not be negative.";
  public static final String ERROR_MSG_FREE_SIZE_RESPONSE_INCORRECT  = "Free size in keychain can not be negative.";
  public static final String ERROR_MSG_GET_KEY_INDEX_IN_STORAGE_AND_LEN_RESPONSE_LEN_INCORRECT = "Response from GET_KEY_INDEX_IN_STORAGE_AND_LEN card operation must have length " + GET_KEY_INDEX_IN_STORAGE_AND_LEN_LE + ".";
  public static final String ERROR_MSG_KEY_INDEX_INCORRECT = "Key index must be >= 0 and <= " + (MAX_NUMBER_OF_KEYS_IN_KEYCHAIN - 1) + ".";
  public static final String ERROR_MSG_KEY_LENGTH_INCORRECT = "Key length (in keychain) must be > 0 and <= " + MAX_KEY_SIZE_IN_KEYCHAIN + ".";
  public static final String ERROR_MSG_DELETE_KEY_CHUNK_RESPONSE_LEN_INCORRECT = "Response from DELETE_KEY_CHUNK card operation must have length " + DELETE_KEY_CHUNK_LE + ".";
  public static final String ERROR_MSG_DELETE_KEY_CHUNK_RESPONSE_INCORRECT = "Response from DELETE_KEY_CHUNK card operation must have value 0 or 1.";
  public static final String ERROR_MSG_DELETE_KEY_RECORD_RESPONSE_LEN_INCORRECT = "Response from DELETE_KEY_RECORD card operation must have length " + DELETE_KEY_RECORD_LE + ".";
  public static final String ERROR_MSG_DELETE_KEY_RECORD_RESPONSE_INCORRECT = "Response from DELETE_KEY_RECORD card operation must have value 0 or 1.";
  public static final String ERROR_MSG_GET_DELETE_KEY_CHUNK_NUM_OF_PACKETS_RESPONSE_LEN_INCORRECT = "Response from GET_DELETE_KEY_CHUNK_NUM_OF_PACKETS card operation must have length " + GET_DELETE_KEY_CHUNK_NUM_OF_PACKETS_LE + ".";
  public static final String ERROR_MSG_GET_DELETE_KEY_CHUNK_NUM_OF_PACKETS_RESPONSE_INCORRECT = "Response from GET_DELETE_KEY_CHUNK_NUM_OF_PACKETS card operation can not be negative.";
  public static final String ERROR_MSG_GET_DELETE_KEY_RECORD_NUM_OF_PACKETS_RESPONSE_LEN_INCORRECT = "Response from GET_DELETE_KEY_RECORD card operation must have length " + GET_DELETE_KEY_RECORD_NUM_OF_PACKETS_LE + ".";
  public static final String ERROR_MSG_GET_DELETE_KEY_RECORD_NUM_OF_PACKETS_RESPONSE_INCORRECT = "Response from GET_DELETE_KEY_RECORD_NUM_OF_PACKETS card operation can not be negative.";
  public static final String ERROR_MSG_NUM_OF_KEYS_INCORRECT_AFTER_ADD = "After ADD_KEY card operation number of keys must be increased by 1.";
  public static final String ERROR_MSG_NUM_OF_KEYS_INCORRECT_AFTER_CHANGE = "After CHANGE_KEY card operation number of keys must not be changed.";
  public static final String ERROR_MSG_SEND_CHUNK_RESPONSE_LEN_INCORRECT = "Response from SEND_CHUNK card operation must have length " + SEND_CHUNK_LE + ".";
  public static final String ERROR_MSG_GET_HMAC_RESPONSE_LEN_INCORRECT = "Hash of key (from keychain) must have length " + SHA_HASH_SIZE + ".";
  public static final String ERROR_MSG_INITIATE_DELETE_KEY_RESPONSE_LEN_INCORRECT  = "Response from INITIATE_DELETE_KEY card operation must have length " + INITIATE_DELETE_KEY_LE + ".";
  public static final String ERROR_KEY_DATA_PORTION_INCORRECT_LEN = "Key data portion must have length = ";
  public static final String ERROR_MSG_GET_SERIAL_NUMBER_RESPONSE_LEN_INCORRECT = "Serial number must have length " + SERIAL_NUMBER_SIZE + ".";
  public static final String ERROR_MSG_GET_PIN_TLT_OR_RTL_RESPONSE_LEN_INCORRECT = "Response from GET_PIN_TLT (GET_PIN_RTL) must have length > 0.";
  public static final String ERROR_MSG_GET_PIN_TLT_OR_RTL_RESPONSE_VAL_INCORRECT = "Response from GET_PIN_TLT (GET_PIN_RTL) must have value >= 0 and <= " + MAX_PIN_TRIES + ".";
  public static final String ERROR_MSG_GET_ROOT_KEY_STATUS_RESPONSE_LEN_INCORRECT = "Response from GET_ROOT_KEY_STATUS must have length > 0.";
  public static final String ERROR_MSG_GET_DEVICE_LABEL_RESPONSE_LEN_INCORRECT = "Response from GET_DEVICE_LABEL_APDU must have length = " + LABEL_LENGTH + ".";
  public static final String ERROR_MSG_GET_CSN_RESPONSE_LEN_INCORRECT = "Response from GET_CSN_VERSION must have length > 0.";
  public static final String ERROR_MSG_GET_SE_VERSION_RESPONSE_LEN_INCORRECT = "Response from GET_SE_VERSION_APDU must have length > 0.";
  public static final String ERROR_MSG_GET_AVAILABLE_MEMORY_RESPONSE_LEN_INCORRECT = "Response from GET_AVAILABLE_MEMORY must have length > 0.";
  public static final String ERROR_MSG_GET_APPLET_LIST_RESPONSE_LEN_INCORRECT = "Response from GET_APPLET_LIST must have length > 0.";

  public static final List<String>  CARD_RESPONSE_DATA_ERRORS = Arrays.asList(
          ERROR_MSG_SAULT_RESPONSE_LEN_INCORRECT,
          ERROR_MSG_STATE_RESPONSE_LEN_INCORRECT,
          ERROR_MSG_STATE_RESPONSE_INCORRECT,
          ERROR_MSG_RECOVERY_DATA_HASH_RESPONSE_LEN_INCORRECT,
          ERROR_MSG_RECOVERY_DATA_LENGTH_RESPONSE_LEN_INCORRECT,
          ERROR_MSG_RECOVERY_DATA_LENGTH_RESPONSE_INCORRECT,
          ERROR_IS_RECOVERY_DATA_SET_RESPONSE_LEN_INCORRECT,
          ERROR_RECOVERY_DATA_PORTION_INCORRECT_LEN,
          ERROR_MSG_HASH_OF_ENCRYPTED_PASSWORD_RESPONSE_LEN_INCORRECT,
          ERROR_MSG_HASH_OF_ENCRYPTED_COMMON_SECRET_RESPONSE_LEN_INCORRECT,
          ERROR_MSG_HASH_OF_ENCRYPTED_COMMON_SECRET_RESPONSE_INCORRECT,
          ERROR_MSG_HASH_OF_ENCRYPTED_PASSWORD_RESPONSE_INCORRECT,
          ERROR_MSG_SIG_RESPONSE_LEN_INCORRECT,
          ERROR_MSG_PUBLIC_KEY_RESPONSE_LEN_INCORRECT,
          ERROR_MSG_GET_NUMBER_OF_KEYS_RESPONSE_LEN_INCORRECT,
          ERROR_MSG_NUMBER_OF_KEYS_RESPONSE_INCORRECT,
          ERROR_MSG_GET_OCCUPIED_SIZE_RESPONSE_LEN_INCORRECT,
          ERROR_MSG_GET_FREE_SIZE_RESPONSE_LEN_INCORRECT,
          ERROR_MSG_OCCUPIED_SIZE_RESPONSE_INCORRECT,
          ERROR_MSG_FREE_SIZE_RESPONSE_INCORRECT,
          ERROR_MSG_GET_KEY_INDEX_IN_STORAGE_AND_LEN_RESPONSE_LEN_INCORRECT,
          ERROR_MSG_KEY_INDEX_INCORRECT,
          ERROR_MSG_KEY_LENGTH_INCORRECT,
          ERROR_MSG_DELETE_KEY_CHUNK_RESPONSE_LEN_INCORRECT,
          ERROR_MSG_DELETE_KEY_CHUNK_RESPONSE_INCORRECT,
          ERROR_MSG_DELETE_KEY_RECORD_RESPONSE_LEN_INCORRECT,
          ERROR_MSG_DELETE_KEY_RECORD_RESPONSE_INCORRECT,
          ERROR_MSG_GET_DELETE_KEY_CHUNK_NUM_OF_PACKETS_RESPONSE_LEN_INCORRECT,
          ERROR_MSG_GET_DELETE_KEY_CHUNK_NUM_OF_PACKETS_RESPONSE_INCORRECT,
          ERROR_MSG_GET_DELETE_KEY_RECORD_NUM_OF_PACKETS_RESPONSE_LEN_INCORRECT,
          ERROR_MSG_GET_DELETE_KEY_RECORD_NUM_OF_PACKETS_RESPONSE_INCORRECT,
          ERROR_MSG_NUM_OF_KEYS_INCORRECT_AFTER_ADD,
          ERROR_MSG_NUM_OF_KEYS_INCORRECT_AFTER_CHANGE,
          ERROR_MSG_SEND_CHUNK_RESPONSE_LEN_INCORRECT,
          ERROR_MSG_GET_HMAC_RESPONSE_LEN_INCORRECT,
          ERROR_MSG_INITIATE_DELETE_KEY_RESPONSE_LEN_INCORRECT,
          ERROR_KEY_DATA_PORTION_INCORRECT_LEN,
          ERROR_MSG_GET_SERIAL_NUMBER_RESPONSE_LEN_INCORRECT,
          ERROR_MSG_GET_PIN_TLT_OR_RTL_RESPONSE_LEN_INCORRECT,
          ERROR_MSG_GET_PIN_TLT_OR_RTL_RESPONSE_VAL_INCORRECT,
          ERROR_MSG_GET_ROOT_KEY_STATUS_RESPONSE_LEN_INCORRECT,
          ERROR_MSG_GET_DEVICE_LABEL_RESPONSE_LEN_INCORRECT,
          ERROR_MSG_GET_CSN_RESPONSE_LEN_INCORRECT,
          ERROR_MSG_GET_SE_VERSION_RESPONSE_LEN_INCORRECT,
          ERROR_MSG_GET_AVAILABLE_MEMORY_RESPONSE_LEN_INCORRECT,
          ERROR_MSG_GET_APPLET_LIST_RESPONSE_LEN_INCORRECT
  );


  /**
   * IMPROPER_APPLET_STATE_ERROR_TYPE_ID = 5
   */
  public static final String ERROR_MSG_APDU_NOT_SUPPORTED = "APDU command is not supported";
  public static final String ERROR_MSG_APPLET_DOES_NOT_WAIT_AUTHENTICATION = "Applet must be in mode that waits authentication. Now it is: ";
  public static final String ERROR_MSG_APPLET_IS_NOT_PERSONALIZED = "Applet must be in personalized mode. Now it is: ";
  public static final String ERROR_MSG_APPLET_DOES_NOT_WAIT_TO_DELETE_KEY = "Applet must be in mode for deleting key. Now it is: ";


  public static final List<String>  IMPROPER_APPLET_STATE_ERRORS = Arrays.asList(ERROR_MSG_APDU_NOT_SUPPORTED, ERROR_MSG_APPLET_DOES_NOT_WAIT_AUTHENTICATION, ERROR_MSG_APPLET_IS_NOT_PERSONALIZED,
          ERROR_MSG_APPLET_DOES_NOT_WAIT_TO_DELETE_KEY);


  /**
   * ANDROID_KEYSTORE_HMAC_KEY_ERROR_TYPE_ID = 8
   */
  public static final String ERROR_MSG_KEY_FOR_HMAC_DOES_NOT_EXIST_IN_ANDROID_KEYSTORE = "Key for hmac signing for specified serial number does not exist.";
  public static final String ERROR_MSG_CURRENT_SERIAL_NUMBER_IS_NOT_SET_IN_ANDROID_KEYSTORE = "Current serial number is not set. Can not select key for hmac.";

  public static final List<String> ANDROID_KEYSTORE_HMAC_KEY_ERRORS = Arrays.asList(ERROR_MSG_KEY_FOR_HMAC_DOES_NOT_EXIST_IN_ANDROID_KEYSTORE, ERROR_MSG_CURRENT_SERIAL_NUMBER_IS_NOT_SET_IN_ANDROID_KEYSTORE);

  /**
   * WRONG_CARD_ERROR_TYPE_ID = 7
   */

  public static final String ERROR_MSG_CARD_HAVE_INCORRECT_SN  = "You try to use security card with incorrect serial number. ";

  public static final List<String> WRONG_CARD_ERRORS = Arrays.asList(ERROR_MSG_CARD_HAVE_INCORRECT_SN);

  public static final List<List<String>> ALL_NATIVE_ERROR_MESSAGES = Arrays.asList(ANDROID_INTERNAL_ERRORS, NFC_INTERUPTION_ERRORS, ANDROID_NFC_ERRORS, INPUT_DATA_FORMAT_ERRORS, CARD_RESPONSE_DATA_ERRORS, IMPROPER_APPLET_STATE_ERRORS, ANDROID_KEYSTORE_HMAC_KEY_ERRORS, WRONG_CARD_ERRORS);

  private static Map<List<String>, String> errorMsgsToErrorTypeIdMap = new HashMap<>();
  public static Map<String, String> errorMsgToErrorCodeMap = new LinkedHashMap<>();

  //error code is a numeric string of length 5 (or 6),
  //it is built as concatenation: errorTypeId + zeros (2 or 3) + successive number of error message in the list
  private static void fillErrCodesMap(List<String> errMsgsArray) {
    String errTypeId = errorMsgsToErrorTypeIdMap.get(errMsgsArray);
    for(int i = 0; i < errMsgsArray.size(); i++) {
      String errMsg = errMsgsArray.get(i);
      String index = Integer.valueOf(i).toString();
      int numOfZeros = 4 - index.length();
      String zeros = Stream.generate(() -> String.valueOf('0')).limit(numOfZeros).collect(Collectors.joining());
      String errCode = errTypeId + zeros + index;
      errorMsgToErrorCodeMap.put(errMsg, errCode);
    }
  }

  public static void initiateMaps() {
    for (int i = 0; i < ERROR_TYPE_IDS.size(); i++) {
      errorTypeIdToErrorTypeMsgMap.put(ERROR_TYPE_IDS.get(i), ERROR_TYPE_MSGS.get(i));
    }
    for (int i = 0; i < ALL_NATIVE_ERROR_MESSAGES.size(); i++) {
      errorMsgsToErrorTypeIdMap.put(ALL_NATIVE_ERROR_MESSAGES.get(i), ERROR_TYPE_IDS.get(i + 1));
      fillErrCodesMap(ALL_NATIVE_ERROR_MESSAGES.get(i));
    }
  }

  static {
    initiateMaps();
  }

  public static String getErrorTypeMsg(String typeId) {
    return errorTypeIdToErrorTypeMsgMap.get(typeId);
  }

  public static String getErrorType(String errMsg) {
    String errorCode = getErrorCode(errMsg);/* errorMsgToErrorCodeMap.get(errMsg);
    if (errorCode == null) {
      for(String key : errorMsgToErrorCodeMap.keySet()) {
        if (errMsg.startsWith(key)) {
          errorCode = errorMsgToErrorCodeMap.get(key);
          break;
        }
      }
    }*/
    if (errorCode != null) {
      return errorCode.substring(0, 1);
    }
    return null;
  }

  public static String getErrorCode(String errMsg) {
    String errorCode = errorMsgToErrorCodeMap.get(errMsg);
    if (errorCode == null) {
      for(String key : errorMsgToErrorCodeMap.keySet()) {
        if (errMsg.startsWith(key)) {
          errorCode = errorMsgToErrorCodeMap.get(key);
          break;
        }
      }
    }
    return errorCode;
  }



}