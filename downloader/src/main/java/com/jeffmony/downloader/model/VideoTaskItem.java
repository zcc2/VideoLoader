package com.jeffmony.downloader.model;

import com.jeffmony.downloader.m3u8.M3U8;
import com.jeffmony.downloader.utils.Utility;

public class VideoTaskItem implements Cloneable {

    private String mUrl;                 //下载视频的url
    private long mDownloadCreateTime;    //下载创建的时间
    private int mTaskState;              //当前任务的状态
    private String mMimeType;            // 视频url的mime type
    private String mFinalUrl;            //30x跳转之后的url
    private int mErrorCode;              //当前任务下载错误码
    private int mVideoType;              //当前文件类型
    private M3U8 mM3U8;                  //M3U8结构,如果非M3U8,则为null
    private int mTotalTs;                //当前M3U8的总分片
    private int mCurTs;                  //当前M3U8已缓存的分片
    private float mSpeed;                //当前下载速度, getSpeedString 函数可以将速度格式化
    private float mPercent;              //当前下载百分比, 0 ~ 100,是浮点数
    private long mDownloadSize;          //已下载大小, getDownloadSizeString 函数可以将大小格式化
    private long mTotalSize;             //文件总大小, M3U8文件无法准确获知
    private String mFileHash;            //文件名的md5
    private String mSaveDir;             //保存视频文件的文件目录名
    private boolean mIsCompleted;        //是否下载完成
    private boolean mIsInDatabase;       //是否存到数据库中
    private long mLastUpdateTime;        //上一次更新数据库的时间
    private String mFileName;            //文件名
    private String mFilePath;            //文件完整路径(包括文件名)
    private boolean mPaused;

    public VideoTaskItem(String url) { mUrl = url; }

    public String getUrl() {
        return mUrl;
    }

    public void setDownloadCreateTime(long time) { mDownloadCreateTime = time; }

    public long getDownloadCreateTime() { return mDownloadCreateTime; }

    public void setTaskState(int state) { mTaskState = state; }

    public int getTaskState() { return mTaskState; }

    public void setMimeType(String mimeType) { mMimeType = mimeType; }

    public String getMimeType() {
        return mMimeType;
    }

    public void setFinalUrl(String finalUrl) { mFinalUrl = finalUrl; }

    public String getFinalUrl() { return mFinalUrl; }

    public void setErrorCode(int errorCode) { mErrorCode = errorCode; }

    public int getErrorCode() {
        return mErrorCode;
    }

    public void setVideoType(int type) { mVideoType = type; }

    public int getVideoType() {
        return mVideoType;
    }

    public void setM3U8(M3U8 m3u8) { mM3U8 = m3u8; }

    public M3U8 getM3U8() {
        return mM3U8;
    }

    public void setTotalTs(int count) { mTotalTs = count; }

    public int getTotalTs() { return mTotalTs; }

    public void setCurTs(int count) { mCurTs = count; }

    public int getCurTs() { return mCurTs; }

    public void setSpeed(float speed) {
        mSpeed = speed;
    }

    public float getSpeed() {
        return mSpeed;
    }

    public String getSpeedString() {
        return Utility.getSize((long)mSpeed) + "/s";
    }

    public void setPercent(float percent) {
        mPercent = percent;
    }

    public float getPercent() {
        return mPercent;
    }

    public String getPercentString() {
        return Utility.getPercent(mPercent);
    }

    public void setDownloadSize(long size) {
        mDownloadSize = size;
    }

    public long getDownloadSize() {
        return mDownloadSize;
    }

    public String getDownloadSizeString() {
        return Utility.getSize(mDownloadSize);
    }

    public void setTotalSize(long size) {
        mTotalSize = size;
    }

    public long getTotalSize() { return mTotalSize; }

    public void setFileHash(String md5) { mFileHash = md5; }

    public String getFileHash() { return mFileHash; }

    public void setSaveDir(String path) { mSaveDir = path; }

    public String getSaveDir() { return mSaveDir; }

    public void setIsCompleted(boolean completed) { mIsCompleted = completed; }

    public boolean isCompleted() { return mIsCompleted; }

    public void setIsInDatabase(boolean in) { mIsInDatabase = in; }

    public boolean isInDatabase() { return mIsInDatabase; }

    public void setLastUpdateTime(long time) { mLastUpdateTime = time; }

    public long getLastUpdateTime() { return mLastUpdateTime; }

    public void setFileName(String name) { mFileName = name; }

    public String getFileName() { return mFileName; }

    public void setFilePath(String path) { mFilePath = path; }

    public String getFilePath() { return mFilePath;}

    public void setPaused(boolean paused) { mPaused = paused; }

    public boolean isPaused() { return mPaused; }

    public boolean isRunningTask() {
        return mTaskState == VideoTaskState.DOWNLOADING;
    }

    public boolean isPendingTask() {
        return mTaskState == VideoTaskState.PENDING;
    }

    public boolean isErrorState() { return mTaskState == VideoTaskState.ERROR; }

    public boolean isSuccessState() {
        return mTaskState == VideoTaskState.SUCCESS;
    }

    public boolean isInterruptTask() {
        return mTaskState == VideoTaskState.PAUSE || mTaskState == VideoTaskState.ERROR;
    }

    public boolean isInitialTask() {
        return mTaskState == VideoTaskState.DEFAULT;
    }

    public boolean isNonHlsType() {
        return mVideoType == Video.Type.MKV_TYPE ||
                mVideoType == Video.Type.QUICKTIME_TYPE ||
                mVideoType == Video.Type.WEBM_TYPE ||
                mVideoType == Video.Type.GP3_TYPE ||
                mVideoType == Video.Type.MP4_TYPE;
    }

    public boolean isHlsType() {
        return mVideoType == Video.Type.HLS_TYPE;
    }

    public void reset() {
        mTaskState = VideoTaskState.DEFAULT;
        mDownloadCreateTime = 0L;
        mMimeType = null;
        mErrorCode = 0;
        mVideoType = Video.Type.DEFAULT;
        mTaskState = VideoTaskState.DEFAULT;
        mM3U8 = null;
        mSpeed = 0.0f;
        mPercent = 0.0f;
        mDownloadSize = 0;
        mTotalSize = 0;
        mFileName = null;
        mFilePath = null;
    }

    @Override
    public Object clone() {
        VideoTaskItem taskItem = new VideoTaskItem(mUrl);
        taskItem.setDownloadCreateTime(mDownloadCreateTime);
        taskItem.setTaskState(mTaskState);
        taskItem.setMimeType(mMimeType);
        taskItem.setErrorCode(mErrorCode);
        taskItem.setVideoType(mVideoType);
        taskItem.setPercent(mPercent);
        taskItem.setDownloadSize(mDownloadSize);
        taskItem.setTotalSize(mTotalSize);
        taskItem.setFileHash(mFileHash);
        taskItem.setFilePath(mFilePath);
        taskItem.setFileName(mFileName);
        return taskItem;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof VideoTaskItem) {
            String objUrl = ((VideoTaskItem)obj).getUrl();
            if (mUrl.equals(objUrl)) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        return "VideoTaskItem[Url="+mUrl+
                ",Type="+mVideoType+
                ",Percent="+mPercent+
                ",DownloadSize="+mDownloadSize+
                ",State="+mTaskState+
                ",FilePath="+mFileName+
                ",LocalFile="+mFilePath+"]";
    }
}
