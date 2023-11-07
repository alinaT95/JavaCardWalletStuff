package com.ftsafe.CoinPassBIO;

import javacard.framework.ISOException;
public class Error {
	
	// SW error
	// 产生助记码的 SW
	static final  short NOT_FOUND_MASTER_KEY_TYPE_TAG = 0X6401;
	static final short NOT_FOUND_ECC_KEY_TYPE = 0X6402;
	static final short NOT_FOUND_ENTROPY_SIZE_TAG = 0x6403;
	static final short WRONG_ENTROPY_SIZE = 0X6404;
	static final short NOT_FOUND_MASTER_KEY_SIZE_TAG = 0X6405;
	static	 final short WRONG_MASTER_KEY_SIZE_TAG = 0X6406;
	
	// 验证助记码的 SW	 
   static final short NOT_FOUND_PASS_PHRASE_TAG = 0X6411;
  
   static final short NOT_FOUND_MNEMONICS_TAG = 0X6412;
  
   static final short NOT_FOUND_MNMONICE_IN_DICT = 0X6413;
  
   static final short MNOMENIC_LEN_OUT_OF_LIMIT = 0X6414;
	
   static final short ENTROPY_BITS_SIZE_MUST_BE_THE_TIMES_OF_EIGHT = 0X6415;
   
   static final short MNMONIC_WORD_COUNT_MUST_BE_TIMES_OF_THREE  = 0x6416;
   
   static final short NEED_TO_GENERATE_MNOMENIC_FIRST = 0X6417;
	
	
   static final short NOT_FOUND_BIP32PATH_TAG = 0X6421;
   
   static final short NO_CORRECT_BIP32PATH_FORMAT = 0X6422;
   
   static final short NOT_FOUND_TRANSCATION_RAW_TEXT_TAG = 0X6431;
	
   static final short NOT_FOUND_AMOUNTS_DATA__TAG  = 0X6432;
   
   static final short NOT_FOUND_CHANGE_ADDR_IDX_TAG  = 0X6433;
   
	public static void wrong6E00_CLA_not_supported()
	{
		ISOException.throwIt((short)0x6E00);
	}
	public static void wrongLength_6C00(short wLen)
	{
		ISOException.throwIt((short)(0x6C00+wLen));
	}
	public static void wrongOffset_6B00()
	{
		ISOException.throwIt((short)0x6B00);
	}
	public static void wrong6D00_INS_not_supported()
	{
		ISOException.throwIt((short)0x6D00);
	}
	
	public static void wrong6A86_incorrect_P1P2()
	{
		ISOException.throwIt((short)0x6A86);
	}
	
	public static void wrong6700_P3Error()
	{
		ISOException.throwIt((short)0x6700);
	}
	
	public static void wrong6CXX_LEError(byte length)
	{
		ISOException.throwIt((short)(0x6C00 + length));
	}
	
	public static void wrong6283_file_invalid()
	{
		ISOException.throwIt((short)(0x6283));
	}
	
	public static void wrong6300_authentication_failed()
	{
		ISOException.throwIt((short)(0x6300));
	}
	
	public static void wrong6580_no_message_sent()
	{
		ISOException.throwIt((short)(0x6580));
	}
	
	public static void wrong6581_write_nvm_failed()
	{
		ISOException.throwIt((short)(0x6581));
	}
	
	public static void wrong6600_communication_failed()
	{
		ISOException.throwIt((short)(0x6600));
	}
	
	public static void wrong6980_reference_key_not_found()
	{
		ISOException.throwIt((short)(0x6980));
	}
	
	public static void wrong6981_file_type_incorrect()
	{
		ISOException.throwIt((short)(0x6981));
	}
	
	public static void wrong6982_security_status_not_satified()
	{
		ISOException.throwIt((short)(0x6982));
	}
	
	public static void wrong6983_authentication_block()
	{
		ISOException.throwIt((short)(0x6983));
	}
	
	public static void wrong6984_data_invalid()
	{
		ISOException.throwIt((short)(0x6984));
	}
	
	public static void wrong6985_conditions_not_satisfied()
	{
		ISOException.throwIt((short)(0x6985));
	}
	
	//no current EF
	public static void wrong6986_command_not_allowed()
	{
		ISOException.throwIt((short)(0x6986));
	}
	
	public static void wrong6988_mac_invalid()
	{
		ISOException.throwIt((short)(0x6988));
	}
	
	public static void wrong6A80_data_parameter_incorrect()
	{
		ISOException.throwIt((short)(0x6A80));
	}
	
	public static void wrong6A81_func_not_supported()
	{
		ISOException.throwIt((short)(0x6A81));
	}
	
	public static void wrong6A82_file_not_found()
	{
		ISOException.throwIt((short)(0x6A82));
	}
	
	public static void wrong6A83_record_not_found()
	{
		ISOException.throwIt((short)(0x6A83));
	}
	
	public static void wrong6A84_file_full()
	{
		ISOException.throwIt((short)(0x6A84));
	}

	public static void wrong6A88_reference_data_not_found()
	{
		ISOException.throwIt((short)(0x6A88));
	}
	
	public static void wrong6A89_fid_exist_already()
	{
		ISOException.throwIt((short)(0x6A89));
	}
	
	public static void wrong6A8A_DFName_exist_already()
	{
		ISOException.throwIt((short)(0x6A8A));
	}
	
	public static void wrong6A90_SM2_decrypt_failed()
	{
		ISOException.throwIt((short)(0x6A90));
	}
	
	public static void wrong6B00_binary_addr_out()
	{
		ISOException.throwIt((short)(0x6B00));
	}
	
	public static void wrong63CX_key_invalid(byte remainTimes)
	{
		ISOException.throwIt((short)(0x63C0 + remainTimes));
	}
	
	public static void wrong6F08_sign_failed()
	{
		ISOException.throwIt((short)(0x6F08));
	}
	public static void wrong6F09_REJECT()
	{
		ISOException.throwIt((short)(0x6F09));
	}
	public static void wrong6F81_inter_system_failed()
	{
		ISOException.throwIt((short)(0x6F81));
	}
	
	public static void wrong6F83_algorithm_not_supported()
	{
		ISOException.throwIt((short)(0x6F83));
	}
	
	public static void wrong6F84_padding_not_supported()
	{
		ISOException.throwIt((short)(0x6F84));
	}
	
	public static void wrong9401_finger_not_uplifted()
	{
		ISOException.throwIt((short)(0x9401));
	}
	
	public static void wrong9402_store_fingerprint_failed()
	{
		ISOException.throwIt((short)(0x9402));
	}
	
	public static void wrong9403_delete_fingerprint_failed()
	{
		ISOException.throwIt((short)(0x9403));
	}
	
	public static void wrong6a90_validate_user_mnemonic_error()
	{
		ISOException.throwIt((short)(0x6a90));
	}
	public static void wrong6A91_need_personalized_finish()
	{
		ISOException.throwIt((short)(0x6a91));
	}
	public static void wrong6a92_NEED_MASTER_KEY()
	{
		ISOException.throwIt((short)(0x6a92));
	}
	public static void wrong6A93_need_select_language()
	{
		ISOException.throwIt((short)(0x6A93));
	}
	public static void wrong6A94_der_decode()
	{
		ISOException.throwIt((short)(0x6A94));
	}
	
	public static   short _6A95_STEPOUT = (short)(0x6A95);
	public static void  stepOut6A95()
	{
		ISOException.throwIt(_6A95_STEPOUT);
	}

	public static void  wrong6A96_API_USAGE_WRONG()
	{
		ISOException.throwIt((short)(0x6A96));
	}
	public static void  wrong6A97_OUT_OF_STORAGE()
	{
		ISOException.throwIt((short)(0x6A97));
	}
	public static void  wrong6A98_NOT_FOUND_OUTPUT()
	{
		ISOException.throwIt((short)(0x6A98));
	}
	public static void  wrong6A99_NOT_KNOWND_ADDRE_TYPE()
	{
		ISOException.throwIt((short)(0x6A99));
	}
	public static void  wrong6A9A_CHANGE_ADDR_NOT_MATCH()
	{
		ISOException.throwIt((short)(0x6A9A));
	}
	public static void  wrong6A9B_CHANGE_ADDR_NUM_NOT_MATCH()
	{
		ISOException.throwIt((short)(0x6A9B));
	}
	public static void  wrong6A9C_confirm_page_index(){
		ISOException.throwIt((short)(0x6A9C));
	}
	public static void  wrong6A9D_ADD_OR_SUB_OVERFLOW(){
		ISOException.throwIt((short)(0x6A9D));
	}
	public static void  wrong6A9E_REGISTER_FAILED(){
		ISOException.throwIt((short)(0x6A9E));
	}
	public static void  wrong6A9F_GET_COIN_DATA_FAILED(){
		ISOException.throwIt((short)(0x6A9F));
	}
	public static void  wrong6AA0_OUTPUTS_NUM(){
		ISOException.throwIt((short)(0x6AA0));
	}
	public static void  wrong6AA1_AddCoinInfo_FAILED(){
		ISOException.throwIt((short)(0x6AA1));
	}
	public static void  wrong6AA2_TRANSATION_TYPE(){
		ISOException.throwIt((short)(0x6AA2));
	}
	public static void  wrong6AA3_TRANSATION_STATE(){
		ISOException.throwIt((short)(0x6AA3));
	}
	public static short TIMER_OUT_ERROR_CODE = (short)(0x6AA4);
	
	public static void  wrong6AA4_Timer_out(){
		ISOException.throwIt(TIMER_OUT_ERROR_CODE);
	}
	public static void  wrong6AA5_NOT_SUPPORT_COIND_TYPE(){
		ISOException.throwIt((short)(0x6AA5));
	}	
	public static void  wrong6AA6_NOT_SUPPORT_HASH_TYPE(){
		ISOException.throwIt((short)(0x6AA6));
	}
	
	public static void  wrong6AA7_NOT_SUPPORT_FORID(){
		ISOException.throwIt((short)(0x6AA7));
	}	
	public static void  wrong6AA8_USDT_NUMBER_NOT_MATCH_DUST(){
		ISOException.throwIt((short)(0x6AA8));
	}
}
