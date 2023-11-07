package com.jubiter.sdk.demo.impl;

/**
 * Copyright © FEITIAN Technologies Co., Ltd. All Rights Reserved.
 *
 * @Date 2018/5/18  13:21
 * @Author ZJF
 * @Version 1.0
 */
public interface JubCallback<V,M> {

    void onSuccess(V v, M m);

    void onFailed(int errorCode);

}
