package hhh.bawei.com.offlinedownload;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements XListView.IXListViewListener{

    private XListView xlv;

    List<OffLineBean.AppBean> list =new ArrayList<>();
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        xlv = (XListView) findViewById(R.id.xlistView);

        xlv.setPullRefreshEnable(true);
        xlv.setPullLoadEnable(true);
        xlv.setXListViewListener(this);


        getXUtils();


        xlv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            private RadioButton rb_phoneliuliang;
            private RadioButton wifi;

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                //设置图片
                builder.setIcon(R.mipmap.ic_launcher);
                //设置标题
                builder.setTitle("网络选择");
                View view1 = View.inflate(MainActivity.this, R.layout.dialog, null);
                builder.setView(view1);

                builder.show();

                wifi = (RadioButton) view1.findViewById(R.id.rb_wifi);
                rb_phoneliuliang = (RadioButton) view1.findViewById(R.id.rb_phoneliuliang);
                wifi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);

                        View view1 = View.inflate(MainActivity.this, R.layout.wifi_dialog, null);

                        builder1.setView(view1);
                        builder1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        builder1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                downloadApk(i);
                            }
                        });
                        builder1.show();
                    }
                });
                rb_phoneliuliang.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Toast.makeText(MainActivity.this, "跳转到设置WiFi页面", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
                        startActivity(intent);
                    }
                });
                return false;
            }
        });

    }

    private ProgressDialog dialog3;


    //下载apk
    protected void downloadApk(final int i) {
//弹出对话框
        dialog3 = new ProgressDialog(MainActivity.this);
        dialog3.setTitle("正在下载......");
        //设置水平
        dialog3.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        dialog3.show();

        //1.获取sd卡路径
        String path = Environment.getExternalStorageDirectory().getPath() + "/off.apk";
        //2.发送请求，获取apk，并放到指定路径
        RequestParams rp = new RequestParams(list.get(i+1).getUrl());

        rp.setSaveFilePath(path);
        rp.setAutoRename(true);

        x.http().get(rp, new Callback.ProgressCallback<File>() {


            //下载成功
            @Override
            public void onSuccess(File result) {

                Toast.makeText(MainActivity.this, "下载完成,开始安装!", Toast.LENGTH_SHORT).show();

                installApk(result);


            }

            //下载出现问题
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.v("tag", "失败");
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                Log.v("tag", "结束");

            }

            @Override
            public void onWaiting() {

            }

            //刚刚开始下载
            @Override
            public void onStarted() {
                Log.v("tag", "开始");
            }

            //下载过程中方法
            @Override
            public void onLoading(long total, long current, boolean isDownloading) {


                // 创建一个数值格式化对象
                NumberFormat numberFormat = NumberFormat.getInstance();
                // 设置精确到小数点后2位

                numberFormat.setMaximumFractionDigits(2);
                String result = numberFormat.format((float)current/(float)total*100);
                Double a = Double.valueOf(result);
                int b =  (int) Math.round(a.doubleValue());
                dialog3.setProgress(b);
                Log.d("msg",Integer.valueOf(result)+"=========");


         }
        });


    }


    //新版本APK下载完毕后，能够启动应用安装器安装apk的intent相关选项是？

    private void installApk(File file) {
        //系统应用界面,源码,安装apk入口
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        //文件作为数据源
        //设置安装的类型
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(intent);

    }

    private void getXUtils(){
        RequestParams params = new RequestParams("http://mapp.qzone.qq.com/cgi-bin/mapp/mapp_subcatelist_qq?yyb_cateid=-10&categoryName=%E8%85%BE%E8%AE%AF%E8%BD%AF%E4%BB%B6&pageNo=1&pageSize=20&type=app&platform=touch&network_type=unknown&resolution=412x732");
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {

                String[] aa = result.split(";");
                OffLineBean offLineBean = JSON.parseObject(aa[0], OffLineBean.class);
                list.addAll(offLineBean.getApp());

                adapter = new MyAdapter(MainActivity.this,list);
                xlv.setAdapter(adapter);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    @Override
    public void onRefresh() {

        list.clear();

        adapter.notifyDataSetChanged();

        getXUtils();

        xlv.stopRefresh();

        xlv.setRefreshTime("刚刚");

    }

    @Override
    public void onLoadMore() {

    }
    class MyAdapter extends BaseAdapter{

        Context context;
        List<OffLineBean.AppBean> list;
        public MyAdapter(Context context, List<OffLineBean.AppBean> list) {
            this.context=context;
            this.list=list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            VH vh;
            if (view==null){
                vh=new VH();
                view=View.inflate(context,R.layout.xlv_item,null);
                vh.tv = (TextView) view.findViewById(R.id.tv);
                view.setTag(vh);
            }else {
                vh= (VH) view.getTag();
            }
            vh.tv.setText(list.get(i).getName());
            return view;
        }

    }
    class VH{
        TextView tv;
    }
}
