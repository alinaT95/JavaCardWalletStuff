package com.tonjubiterreactnativetest.api;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author fengshuo
 * @date 2018/4/26
 * @time 14:28
 */
public class ThreadUtils {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    private static final ExecutorService mThreadPool = Executors.newFixedThreadPool(CORE_POOL_SIZE);
    private static final Handler sMainHandler = new Handler(Looper.getMainLooper());


    public static void execute(Runnable runnable) {
        mThreadPool.execute(runnable);
    }


    public static void toMainThread(Runnable runnable) {
        sMainHandler.post(runnable);
    }

}
