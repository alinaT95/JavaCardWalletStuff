package com.jubiter.sdk.demo.jubnative;

/**
 * 版权所有：(C)飞天诚信科技股份有限公司
 * 文件名称：InnerDiscCallback
 * 文件标识：
 * 内容摘要：内部断开回调接口
 * 其它说明：其它内容的说明
 * 当前版本：1.0.0
 * 作    者：zhl
 * 完成日期：2017/5/18
 **/
public interface InnerDiscCallback {

    /**
     * the callback of disconnect
     *
     * @param name device name
     */
    public void onDisconnect(String name);
}
