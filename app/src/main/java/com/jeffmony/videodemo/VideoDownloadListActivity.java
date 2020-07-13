package com.jeffmony.videodemo;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jeffmony.downloader.VideoDownloadManager;
import com.jeffmony.downloader.listener.DownloadListener;
import com.jeffmony.downloader.listener.IDownloadInfosCallback;
import com.jeffmony.downloader.model.VideoTaskItem;
import com.jeffmony.downloader.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class VideoDownloadListActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DownloadFeatureActivity";
    private static final int PERMISSION_REQUEST_CODE = 1001;
    private TextView mPauseAllBtn;
    private TextView mStartAllBtn;
    private TextView tvEmpty;
    private Button mBtAdd;
    private ListView mDownloadListView;

    private VideoListAdapter mAdapter;
    private List<VideoTaskItem> items = new ArrayList<>();
    GetDialog getDialog=new GetDialog();
    private Dialog mInputDia;
    private TextView mSetting;
    private TextView mInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_list);
        mPauseAllBtn = (TextView) findViewById(R.id.pause);
        mStartAllBtn = (TextView) findViewById(R.id.start);
        mSetting = (TextView) findViewById(R.id.setting);
        mInfo = (TextView) findViewById(R.id.info);
        mBtAdd = (Button) findViewById(R.id.add);
        tvEmpty = (TextView) findViewById(R.id.tv_empty);
        mDownloadListView = (ListView) findViewById(R.id.download_listview);
        mStartAllBtn.setOnClickListener(this);
        mPauseAllBtn.setOnClickListener(this);
        mSetting.setOnClickListener(this);
        mInfo.setOnClickListener(this);
        requestPermissions();
        VideoDownloadManager.getInstance().setGlobalDownloadListener(mListener);
        initDatas();
    }
    private void requestPermissions() {
        List<String> permissionList = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this, permissionList.toArray(new String[permissionList.size()]),
                    PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int index = 0; index < grantResults.length; index++) {
                if (grantResults[index] == PackageManager.PERMISSION_DENIED) {
                    Toast
                            .makeText(this, "[" + permissions[index] + "]权限被拒绝",
                                    Toast.LENGTH_LONG)
                            .show();
                }
            }
        }
    }
    private void initDatas() {
//        VideoTaskItem item1 = new VideoTaskItem("http://v.51mjtv.com:2100/20200621/ATkIMgaM/index.m3u8?sign=029cba59e1ee1f0d6b32eecd5fe78a8df2c6517d67fdb451b3a674bd3e2d330364ea2d710b048e0b5c8815df27dd91d270fb773e9171f2fc8ec083d4f23a097a");
//        VideoTaskItem item2 = new VideoTaskItem("https://videos.kkyun-iqiyi.com/20171117/XZiuPaA3/index.m3u8");
//        VideoTaskItem item3 = new VideoTaskItem("https://tv2.youkutv.cc/2020/04/14/MbqulRmS8sjQGJG9/playlist.m3u8");
//        VideoTaskItem item4 = new VideoTaskItem("https://tv2.youkutv.cc/2020/04/14/Pejd7TL3wdLZVbxO/playlist.m3u8");
//        VideoTaskItem item5 = new VideoTaskItem("https://tv2.youkutv.cc/2020/04/13/AWlDA5ORHHzLX81U/playlist.m3u8");
//        VideoTaskItem item6 = new VideoTaskItem("https://hls.aoxtv.com/v3.szjal.cn/20200114/dtOHlPFE/index.m3u8");
//
//        items[0] = item1;
//        items[1] = item2;
//        items[2] = item3;
//        items[3] = item4;
//        items[4] = item5;
//        items[5] = item6;

//        items.add(item1);
        mAdapter = new VideoListAdapter(this, R.layout.download_item, items);
        mDownloadListView.setAdapter(mAdapter);
        VideoDownloadManager.getInstance().fetchDownloadItems(mInfosCallback);

        mDownloadListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                VideoTaskItem item = items.get(position);
                if (item.isInitialTask()) {
                    VideoDownloadManager.getInstance().startDownload(item);
                } else if (item.isRunningTask()) {
                    VideoDownloadManager.getInstance().pauseDownloadTask(item.getUrl());
                } else if (item.isInterruptTask()) {
                    VideoDownloadManager.getInstance().resumeDownload(item.getUrl());
                }
            }
        });
        mBtAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputDia = getDialog.getedtTwoDialog2(VideoDownloadListActivity.this, new GetDialog.EdtStrResult() {
                    @Override
                    public void onStr(String str1) {
//                        if(isHttpUrl(str1)){
                            try {
                                VideoTaskItem item1=new VideoTaskItem(str1);
                                VideoDownloadManager.getInstance().startDownload(item1);
                                items.add(item1);
                                tvEmpty.setVisibility(View.GONE);
                                mAdapter.notifyDataSetChanged();
                            }catch (Exception e){
                                Toast.makeText(VideoDownloadListActivity.this,"无法下载",Toast.LENGTH_SHORT);
                            }
                            mInputDia.dismiss();
//                        }else {
//                            Toast.makeText(VideoDownloadListActivity.this,"请输入正确网址",Toast.LENGTH_SHORT);
//                        }

                    }
                });
                mInputDia.show();
            }
        });
        tvEmpty.setVisibility(items.size()<1?View.VISIBLE:View.GONE);
    }

    /**
     * 判断字符串是否为URL
     * @param urls 需要判断的String类型url
     * @return true:是URL；false:不是URL
     */
    public static boolean isHttpUrl(String urls) {
        boolean isurl = false;
        String regex = "(((https|http)?://)?([a-z0-9]+[.])|(www.))"
                + "\\w+[.|\\/]([a-z0-9]{0,})?[[.]([a-z0-9]{0,})]+((/[\\S&&[^,;\u4E00-\u9FA5]]+)+)?([.][a-z0-9]{0,}+|/?)";//设置正则表达式

        Pattern pat = Pattern.compile(regex.trim());//对比
        Matcher mat = pat.matcher(urls.trim());
        isurl = mat.matches();//判断是否匹配
        if (isurl) {
            isurl = true;
        }
        return isurl;
    }
    private long mLastProgressTimeStamp;
    private long mLastSpeedTimeStamp;

    private DownloadListener mListener = new DownloadListener() {

        @Override
        public void onDownloadDefault(VideoTaskItem item) {
            LogUtils.w(TAG,"onDownloadDefault: " + item);
            notifyChanged(item);
        }

        @Override
        public void onDownloadPending(VideoTaskItem item) {
            LogUtils.w(TAG,"onDownloadPending: " + item);
            notifyChanged(item);
        }

        @Override
        public void onDownloadPrepare(VideoTaskItem item) {
            LogUtils.w(TAG,"onDownloadPrepare: " + item);
            notifyChanged(item);
        }

        @Override
        public void onDownloadStart(VideoTaskItem item) {
            LogUtils.w(TAG,"onDownloadStart: " + item);
            notifyChanged(item);
        }

        @Override
        public void onDownloadProgress(VideoTaskItem item) {
            long currentTimeStamp = System.currentTimeMillis();
            if (currentTimeStamp - mLastProgressTimeStamp > 1000) {
                LogUtils.w(TAG, "onDownloadProgress: " + item.getPercentString() + ", curTs=" + item.getCurTs() + ", totalTs=" + item.getTotalTs());
                notifyChanged(item);
                mLastProgressTimeStamp = currentTimeStamp;
            }
        }

        @Override
        public void onDownloadSpeed(VideoTaskItem item) {
            long currentTimeStamp = System.currentTimeMillis();
            if (currentTimeStamp - mLastSpeedTimeStamp > 1000) {
                notifyChanged(item);
                mLastSpeedTimeStamp = currentTimeStamp;
            }
        }

        @Override
        public void onDownloadPause(VideoTaskItem item) {
            LogUtils.w(TAG,"onDownloadPause: " + item.getUrl());
            notifyChanged(item);
        }

        @Override
        public void onDownloadError(VideoTaskItem item) {
            LogUtils.w(TAG,"onDownloadError: " + item.getUrl());
            notifyChanged(item);
        }

        @Override
        public void onDownloadSuccess(VideoTaskItem item) {
            LogUtils.w(TAG,"onDownloadSuccess: " + item);
            notifyChanged(item);
        }
    };

    private void notifyChanged(final VideoTaskItem item) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyChanged(items, item);
            }
        });
    }

    private IDownloadInfosCallback mInfosCallback =
            new IDownloadInfosCallback() {
                @Override
                public void onDownloadInfos(List<VideoTaskItem> items) {
                    for (VideoTaskItem item : items) {
                        notifyChanged(item);
                    }
                }
            };


    @Override
    public void onClick(View v) {
        if (v == mStartAllBtn) {
            for(VideoTaskItem item:items){
                VideoDownloadManager.getInstance().startDownload(item);
            }
            mAdapter.notifyDataSetChanged();
        } else if (v == mPauseAllBtn) {
            VideoDownloadManager.getInstance().pauseAllDownloadTasks();
        }else if(v==mSetting){
            Intent intent = new Intent(this, DownloadSettingsActivity.class);
            startActivity(intent);
        }else if (v==mInfo){
            getDialog.getTipDialog(this,"功能:\n1.下载整视频，如mp4/mkv/mov/3gp等视频\n" +
                    "2.下载HLS，即M3U8视频\n" +
                    "3.M3U8 视频下载完成，会生成一个本地的local.m3u8文件\n" +
                    "4.视频下载完成，可以点击播放视频文件\n" +
                    "5.视频下载数据库记录视频下载信息\n" +
                    "6.增加视频下载队列").show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VideoDownloadManager.getInstance().removeDownloadInfosCallback(mInfosCallback);
    }

}
