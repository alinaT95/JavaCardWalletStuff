package com.jubiter.sdk.demo.jubnative;

/**
 * 版权所有：(C)飞天诚信科技股份有限公司
 * 文件名称：InnerScanCallback
 * 文件标识：
 * 内容摘要：内部扫描回调接口
 * 其它说明：其它内容的说明
 * 当前版本：1.0.0
 * 作 者：fs
 * 完成日期：2017/5/17
 **/
public interface InnerScanCallback {

    /**
     * callback of scan
     *
     * @param name device name
     * @param uuid device MAC
     * @param devType device type
     */
    public void onScanResult(String name, String uuid, int devType);

    /**
     * 扫描停止
     */
    public void onStop();

    /**
     *
     * @param errorCode
     */
    public void onError(int errorCode);


}
