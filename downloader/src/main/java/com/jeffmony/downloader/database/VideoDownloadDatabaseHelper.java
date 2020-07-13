package com.jeffmony.downloader.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jeffmony.downloader.model.VideoTaskItem;
import com.jeffmony.downloader.model.VideoTaskState;
import com.jeffmony.downloader.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class VideoDownloadDatabaseHelper {

    private static final String TAG = "VideoDownloadDatabaseHelper";
    private VideoDownloadSQLiteHelper mSQLiteHelper;

    public VideoDownloadDatabaseHelper(Context context) {
        if (context == null) { return; }
        mSQLiteHelper = new VideoDownloadSQLiteHelper(context);
    }

    public void markDownloadInfoAddEvent(VideoTaskItem item) {
        SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
        if (db == null) {
            return;
        }
        synchronized (this) {
            if (item.isInDatabase() || isTaskInfoExistInTable(db, item)) {
            } else {
                insertVideoDownloadInfo(db, item);
            }
        }
    }

    public void markDownloadProgressInfoUpdateEvent(VideoTaskItem item) {
        SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
        if (db == null) {
            return;
        }
        if (item.isInDatabase() || isTaskInfoExistInTable(db, item)) {
            updateDownloadProgressInfo(db, item);
        } else {
            insertVideoDownloadInfo(db, item);
        }
    }

    public void markDownloadInfoPauseEvent(VideoTaskItem item) {
        SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
        if (db == null) {
            return;
        }
        if (item.isInDatabase() || isTaskInfoExistInTable(db, item)) {
            updateDownloadProgressInfo(db, item);
        } else {
            insertVideoDownloadInfo(db, item);
        }
    }

    public void markDownloadInfoErrorEvent(VideoTaskItem item) {
        SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
        if (db == null) {
            return;
        }
        if (item.isInDatabase() || isTaskInfoExistInTable(db, item)) {
            updateDownloadProgressInfo(db, item);
        } else {
            insertVideoDownloadInfo(db, item);
        }
    }

    public void updateDownloadProgressInfo(SQLiteDatabase db, VideoTaskItem item) {
        if (db == null) {
            return;
        }
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(VideoDownloadSQLiteHelper.Columns.MIME_TYPE, item.getMimeType());
            values.put(VideoDownloadSQLiteHelper.Columns.TASK_STATE, item.getTaskState());
            values.put(VideoDownloadSQLiteHelper.Columns.VIDEO_TYPE, item.getVideoType());
            values.put(VideoDownloadSQLiteHelper.Columns.PERCENT, item.getPercent());
            values.put(VideoDownloadSQLiteHelper.Columns.CACHED_LENGTH, item.getDownloadSize());
            values.put(VideoDownloadSQLiteHelper.Columns.TOTAL_LENGTH, item.getTotalSize());
            values.put(VideoDownloadSQLiteHelper.Columns.CACHED_TS, item.getCurTs());
            values.put(VideoDownloadSQLiteHelper.Columns.TOTAL_TS, item.getTotalTs());
            values.put(VideoDownloadSQLiteHelper.Columns.FILE_NAME, item.getFileName());
            values.put(VideoDownloadSQLiteHelper.Columns.FILE_PATH, item.getFilePath());
            String whereClause = VideoDownloadSQLiteHelper.Columns.VIDEO_URL + " = ?";
            String[] whereArgs = {item.getUrl()};
            db.update(VideoDownloadSQLiteHelper.TABLE_VIDEO_DOWNLOAD_INFO, values,
                    whereClause, whereArgs);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            LogUtils.w(TAG,
                    "updateVideoDownloadInfo failed, exception = " + e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    private void insertVideoDownloadInfo(SQLiteDatabase db, VideoTaskItem item) {
        if (db == null) {
            return;
        }
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(VideoDownloadSQLiteHelper.Columns.VIDEO_URL, item.getUrl());
            values.put(VideoDownloadSQLiteHelper.Columns.MIME_TYPE, item.getMimeType());
            values.put(VideoDownloadSQLiteHelper.Columns.DOWNLOAD_TIME, item.getDownloadCreateTime());
            values.put(VideoDownloadSQLiteHelper.Columns.PERCENT, item.getPercent());
            values.put(VideoDownloadSQLiteHelper.Columns.TASK_STATE, item.getTaskState());
            values.put(VideoDownloadSQLiteHelper.Columns.VIDEO_TYPE, item.getVideoType());
            values.put(VideoDownloadSQLiteHelper.Columns.CACHED_LENGTH, item.getDownloadSize());
            values.put(VideoDownloadSQLiteHelper.Columns.TOTAL_LENGTH, item.getTotalSize());
            if (item.getM3U8() != null && item.getM3U8().getTsList() != null) {
                values.put(VideoDownloadSQLiteHelper.Columns.CACHED_TS, item.getM3U8().getCurTsIndex());
                values.put(VideoDownloadSQLiteHelper.Columns.TOTAL_TS, item.getM3U8().getTsList().size());
            }
            db.insert(VideoDownloadSQLiteHelper.TABLE_VIDEO_DOWNLOAD_INFO, null,
                    values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            LogUtils.w(TAG,
                    "insertVideoDownloadInfo failed, exception = " + e.getMessage());
        } finally {
            db.endTransaction();
            item.setIsInDatabase(true);
        }
    }

    private boolean isTaskInfoExistInTable(SQLiteDatabase db,
                                           VideoTaskItem item) {
        if (db == null)
            return false;
        Cursor cursor = null;
        try {
            String selection = VideoDownloadSQLiteHelper.Columns.VIDEO_URL + " = ?";
            String[] selectionArgs = {item.getUrl() + ""};
            String limit = "10";
            cursor =
                    db.query(VideoDownloadSQLiteHelper.TABLE_VIDEO_DOWNLOAD_INFO, null,
                            selection, selectionArgs, null, null, null, limit);
            if (cursor != null && cursor.moveToFirst() && cursor.getLong(0) > 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            LogUtils.w(TAG, "isTaskInfoExistInTable query failed, exception = " +
                    e.getMessage());
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public List<VideoTaskItem> getDownloadInfos() {
        SQLiteDatabase db = mSQLiteHelper.getReadableDatabase();
        if (db == null) {
            return null;
        }
        List<VideoTaskItem> items = new ArrayList<>();
        Cursor cursor = null;
        try {
            String limit = "100";
            cursor = db.query(VideoDownloadSQLiteHelper.TABLE_VIDEO_DOWNLOAD_INFO,
                    null, null, null, null, null, null, limit);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String url = cursor.getString(cursor.getColumnIndex(
                            VideoDownloadSQLiteHelper.Columns.VIDEO_URL));
                    VideoTaskItem item = new VideoTaskItem(url);
                    item.setMimeType(cursor.getString(cursor.getColumnIndex(
                            VideoDownloadSQLiteHelper.Columns.MIME_TYPE)));
                    item.setDownloadCreateTime(cursor.getLong(cursor.getColumnIndex(
                            VideoDownloadSQLiteHelper.Columns.DOWNLOAD_TIME)));
                    item.setPercent(cursor.getFloat(cursor.getColumnIndex(
                            VideoDownloadSQLiteHelper.Columns.PERCENT)));
                    item.setTaskState(cursor.getInt(cursor.getColumnIndex(
                            VideoDownloadSQLiteHelper.Columns.TASK_STATE)));
                    item.setVideoType(cursor.getInt(cursor.getColumnIndex(
                            VideoDownloadSQLiteHelper.Columns.VIDEO_TYPE)));
                    item.setDownloadSize(cursor.getLong(cursor.getColumnIndex(
                            VideoDownloadSQLiteHelper.Columns.CACHED_LENGTH)));
                    item.setTotalSize(cursor.getLong(cursor.getColumnIndex(
                            VideoDownloadSQLiteHelper.Columns.TOTAL_LENGTH)));
                    item.setCurTs(cursor.getInt(cursor.getColumnIndex(
                            VideoDownloadSQLiteHelper.Columns.CACHED_TS)));
                    item.setTotalTs(cursor.getInt(cursor.getColumnIndex(
                            VideoDownloadSQLiteHelper.Columns.TOTAL_TS)));
                    item.setFileName(cursor.getString(cursor.getColumnIndex(
                            VideoDownloadSQLiteHelper.Columns.FILE_NAME)));
                    item.setFilePath(cursor.getString(cursor.getColumnIndex(
                            VideoDownloadSQLiteHelper.Columns.FILE_PATH)));
                    if (item.isRunningTask() && Math.abs(item.getSpeed()) < 0.0001f) {
                        item.setTaskState(VideoTaskState.PAUSE);
                    }
                    items.add(item);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            LogUtils.w(TAG, "getDownloadInfos failed, exception = " + e.getMessage());
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return items;
    }

    public void deleteAllDownloadInfos() {
        SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
        if (db == null) {
            return;
        }
        db.beginTransaction();
        try {
            db.delete(VideoDownloadSQLiteHelper.TABLE_VIDEO_DOWNLOAD_INFO, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            LogUtils.w(TAG, "deleteAllDownloadInfos failed, exception = " + e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    public void deleteDownloadItemByUrl(VideoTaskItem item) {
        SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
        if (db == null) {
            return;
        }
        db.beginTransaction();
        try {
            String whereClause = VideoDownloadSQLiteHelper.Columns.VIDEO_URL + " = ? ";
            String whereArgs[] = {item.getUrl()};
            db.delete(VideoDownloadSQLiteHelper.TABLE_VIDEO_DOWNLOAD_INFO, whereClause, whereArgs);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            LogUtils.w(TAG, "deleteDownloadItemByUrl failed, exception = " + e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

}
