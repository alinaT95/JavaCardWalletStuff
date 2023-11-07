package com.tonjubiterreactnativetest.api.listener;

public interface ApiAsyncListener<T> {

	public void onUiChange();

	public void onResult(int errorCode, T result);
}/**/
