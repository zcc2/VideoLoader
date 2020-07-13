package com.jeffmony.videodemo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jeffmony.downloader.model.VideoTaskItem;
import com.jeffmony.downloader.model.VideoTaskState;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class VideoListAdapter extends ArrayAdapter<VideoTaskItem> {

    private Context mContext;

    public VideoListAdapter(Context context, int resource, List<VideoTaskItem> items) {
        super(context, resource, items);
        mContext = context;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.download_item, null);
        VideoTaskItem item = getItem(position);
        TextView urlTextView = (TextView) view.findViewById(R.id.url_text);
        urlTextView.setText(item.getUrl());
        TextView stateTextView = (TextView) view.findViewById(R.id.status_txt);
        TextView playBtn = (TextView) view.findViewById(R.id.download_play_btn);
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                remove(item);
                return false;
            }
        });
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PlayerActivity.class);
                intent.putExtra("videoUrl", item.getFilePath());
                mContext.startActivity(intent);
//                try {
//                    if (TextUtils.isEmpty(baseBean.getFileName())) {
//                        toastShort("未命名文件，无法查看");
//                        return;
//                    }
//                    Intent intent = new Intent();
//                    intent.setAction(Intent.ACTION_VIEW);
//                    String type = getMimeType(baseBean.getFileName());
//                    //设置intent的data和Type属性。
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                        Uri contentUri = FileProvider.getUriForFile(mContext, Constant.FILE_PROVIDER, file);
//                        intent.setDataAndType(contentUri, type);
//                    } else {
//                        intent.setDataAndType(Uri.fromFile(file), type);
//                    }
////                            intent.setDataAndType(Uri.fromFile(file), type);
//                    startActivity(intent);
//                } catch (Exception e) {
//                    toastShort("没有任何与该类型文件关联的程序");
//                }
            }
        });
        setStateText(stateTextView, playBtn, item);
        TextView infoTextView = (TextView) view.findViewById(R.id.download_txt);
        setDownloadInfoText(infoTextView, item);
        return view;
    }

    private void setStateText(TextView stateView, TextView playBtn, VideoTaskItem item) {
        switch (item.getTaskState()) {
            case VideoTaskState.PENDING:
                playBtn.setVisibility(View.INVISIBLE);
                stateView.setText("等待中");
                break;
            case VideoTaskState.PREPARE:
                playBtn.setVisibility(View.INVISIBLE);
                stateView.setText("准备好");
                break;
            case VideoTaskState.START:
                playBtn.setVisibility(View.INVISIBLE);
                stateView.setText("开始下载");
                break;
            case VideoTaskState.DOWNLOADING:
                stateView.setText("下载中...");
                break;
            case VideoTaskState.PAUSE:
                playBtn.setVisibility(View.INVISIBLE);
                stateView.setText("下载暂停, 已下载=" + item.getDownloadSizeString());
                break;
            case VideoTaskState.SUCCESS:
                playBtn.setVisibility(View.VISIBLE);
                stateView.setText("下载完成, 总大小=" + item.getDownloadSizeString());
                break;
            case VideoTaskState.ERROR:
                playBtn.setVisibility(View.INVISIBLE);
                stateView.setText("下载错误");
                break;
            default:
                playBtn.setVisibility(View.INVISIBLE);
                stateView.setText("未下载");
                break;

        }
    }

    private void setDownloadInfoText(TextView infoView, VideoTaskItem item) {
        switch (item.getTaskState()) {
            case VideoTaskState.DOWNLOADING:
                infoView.setText("进度:" + item.getPercentString() + ", 速度:" + item.getSpeedString() +", 已下载:" + item.getDownloadSizeString());
                break;
            case VideoTaskState.SUCCESS:
                infoView.setText("进度:" + item.getPercentString());
                break;
            case VideoTaskState.PAUSE:
                infoView.setText("进度:" + item.getPercentString());
                break;
            default:
                break;
        }
    }

    public void notifyChanged(List<VideoTaskItem> items, VideoTaskItem item) {
        for (int index = 0; index < getCount(); index++) {
            if (getItem(index).equals(item)) {
//                items[index] = item;
                items.set(index,item);
                notifyDataSetChanged();
            }
        }
    }

}
