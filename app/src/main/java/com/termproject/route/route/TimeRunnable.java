//package com.termproject.route.route;
//
//public class TimeRunnable implements Runnable {
//    int time=0;
//    private Object mPauseLock;
//    private boolean mPaused;
//    private boolean mFinished;
//
//    public TimeRunnable() {
//        mPauseLock = new Object();
//        mPaused = false;
//        mFinished = false;
//    }
//
//    public void run() {
//        while (!mFinished) {
//            int tempHour, tempMinute, tempSecond;
//            tempHour = newRunningActivity.time / 3600;
//            tempMinute = (newRunningActivity.time % 3600) / 60;
//            tempSecond = newRunningActivity.time % 60;
//            String hour = "", min = "", sec = "";
//            if (tempHour < 10) {
//                hour = "0" + tempHour;
//            } else hour = "" + tempHour;
//            if (tempMinute < 10) {
//                min = "0" + tempMinute;
//            } else min = "" + tempMinute;
//            if (tempSecond < 10) {
//                sec = "0" + tempSecond;
//            } else sec = "" + tempSecond;
//
//
//
//            synchronized (mPauseLock) {
//                while (mPaused) {
//                    try {
//                        mPauseLock.wait();
//                    } catch (InterruptedException e) {
//                    }
//                }
//            }
//        }
//    }
//
//    /**
//     * Call this on pause.
//     */
//    public void onPause() {
//        synchronized (mPauseLock) {
//            mPaused = true;
//        }
//    }
//
//    /**
//     * Call this on resume.
//     */
//    public void onResume() {
//        synchronized (mPauseLock) {
//            mPaused = false;
//            mPauseLock.notifyAll();
//        }
//    }
//
//}
//
//
