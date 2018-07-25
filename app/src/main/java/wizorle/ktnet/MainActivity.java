package wizorle.ktnet;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import wizorle.ktnet.get.Getback;
import wizorle.ktnet.json.LoginBack;
import wizorle.ktnet.post.Video;
import wizorle.ktnet.upload.DataBack;
import wizorle.ktnet.upload.UpLoad;

public class MainActivity extends AppCompatActivity {

    public ProgressBar progressBar;
    public TextView progress_text;

    public ProgressBar progressBar_up;
    public TextView progress_text_up;

    public ProgressBar  progress_up_d;
    public TextView progress_text_up_d;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //下载
        progressBar=(ProgressBar)findViewById(R.id.progress);
        progress_text=(TextView)findViewById(R.id.progress_text);
        //单文件
        progressBar_up=(ProgressBar)findViewById(R.id.progress_up);
        progress_text_up=(TextView)findViewById(R.id.progress_text_up);
        //文件带参数
        progress_up_d=(ProgressBar)findViewById(R.id.progress_up_d);
        progress_text_up_d=(TextView)findViewById(R.id.progress_text_up_d);


        findViewById(R.id.text_get).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get();
            }
        });

        findViewById(R.id.text_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post();
            }
        });
        findViewById(R.id.text_post_json).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postJson();
            }
        });
        findViewById(R.id.text_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downLoad();
            }
        });
        findViewById(R.id.text_upfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upFile();
            }
        });
        findViewById(R.id.text_upfile_fd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                upFileData();
                test();
            }
        });

    }

    /**
     * get
     */
    public void get(){
        //http://v.juhe.cn/joke/randJoke.php?key=keykey&type=pic
        HttpUtil.INSTANCE
                .putKeyCode("key","2689c4e9d6c76006c8a3280c20915027")
                .putKeyCode("type","pic")
                .AskGet("randJoke.php", new HttpUtil.OnDataListener() {
                    @Override
                    public void Error(String e) {
                        Toast.makeText(MainActivity.this, e.toString(),Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void Success(Object content) {
                        if(content instanceof Getback){
                            Toast.makeText(MainActivity.this,((Getback)content).getResult().get(0).getContent(),Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(MainActivity.this,"解析失败",Toast.LENGTH_LONG).show();
                        }
                    }
                }, Getback.class);
    }

    /**
     * post
     */
    public String msg;
    public Date temp;
    public void post(){
        try {
            final Date date=new Date();
            SimpleDateFormat format =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
            String time=format.format(date);
            temp = format.parse(time);
            String str=
                    "api"+"video.list"
                            +"format"+"json"
                            +"timestamp"+ temp.getTime()
                            +"user_unique"+"rfexqjdibo"
                            +"ver"+"2.0"
                            +"31913847fe078bd3bfd8306a1fbda769";
            msg=getMD5(str);
        }catch (ParseException e) {
            e.getErrorOffset();
        }
        HttpUtil.INSTANCE
                .putKeyCode("user_unique","rfexqjdibo")
                .putKeyCode("timestamp",temp.getTime()+"")
                .putKeyCode("api","video.list")
                .putKeyCode("format","json")
                .putKeyCode("ver","2.0")
                .putKeyCode("sign",msg)
                .AskPost("open.php", new HttpUtil.OnDataListener() {
                    @Override
                    public void Error(String e) {
                        Log.e(getClass().toString(),e);
                    }

                    @Override
                    public void Success(Object content) {
                      
                        if (content instanceof Video){
                            if(((Video) content).code==0){
                                Toast.makeText(MainActivity.this,((Video)content).getData().get(0).getImg(),Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(MainActivity.this,"请求成功，数据获取失败",Toast.LENGTH_LONG).show();
                            }
                        }else {
                            Toast.makeText(MainActivity.this,"失败",Toast.LENGTH_LONG).show();
                        }
                    }
                }, Video.class);
    }
    /**
     * post上传json数据
     */
    public void postJson(){
        try {
            JSONObject object=new JSONObject();
            object.put("TradeCode", "Y003");
            object.put("LoginName", "18201367542");
            object.put("LoginPass", "lsp");
            HttpUtil.INSTANCE.AskJson("orderInterface.do",object, new HttpUtil.OnDataListener() {
                @Override
                public void Error(String e) {
                    Toast.makeText(MainActivity.this, e.toString(),Toast.LENGTH_LONG).show();
                }
                @Override
                public void Success(Object content) {
                    if (content instanceof LoginBack){
                        if(((LoginBack) content).getResultCode().equals("0")){
                            Toast.makeText(MainActivity.this,((LoginBack) content).getResultContent(),Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(MainActivity.this, ((LoginBack) content).getResultContent(),Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(MainActivity.this, "数据解析失败",Toast.LENGTH_LONG).show();
                    }
                }
            }, LoginBack.class);
        }catch (JSONException e){
            Toast.makeText(MainActivity.this, "数据错误",Toast.LENGTH_LONG).show();
        }
    }


    /**
     * 文件下载apk
     * http://39.107.75.214:8080/wizrole_bg/static/hoService.apk
     * http://192.168.0.104:8280/wizrole_bg/static/hoService.apk
     */
    public void downLoad(){
        HttpUtil.INSTANCE.AskLoad("http://39.107.75.214:8080/wizrole_bg/static/hoService.apk", "my", new HttpUtil.OnLoadingListener() {
            @Override
            public void onSuccess(File file) {
                Toast.makeText(MainActivity.this,"下载成功："+file.getAbsolutePath(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(String e) {
                Toast.makeText(MainActivity.this,"下载失败",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onProgress(float progress) {
                progress_t=progress;
                handler.sendEmptyMessage(0);
            }
        }, ".apk");
    }

    /**
     * 单文件上传
     */
    public long count_now;
    public long lenght_now;
    public void upFile(){
        File file = new File("/sdcard/wizrole/xingxing.jpg");
            HttpUtil.INSTANCE
                .putKeyUpload("file", file, MultipartBody.FORM, new HttpUtil.OnLengthListener() {
                    @Override
                    public void Length(long length) {
                        lenght_now=length;
                        handler.sendEmptyMessage(1);
                    }

                    @Override
                    public void Count(long count) {
                        count_now=count;
                    }
                })
                .AskUpload("fileUpload.do", new HttpUtil.OnDataListener() {
                    @Override
                    public void Error(String e) {
                        handler.sendEmptyMessage(4);
                    }

                    @Override
                    public void Success(Object content) {
                        if(content instanceof UpLoad){
                            Toast.makeText(MainActivity.this,"成功",Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(MainActivity.this,"失败",Toast.LENGTH_LONG).show();
                        }
                    }
                }, UpLoad.class);
    }

    /**
     * 文件带参数上传
     */
    public long lenght_d;
    public long count_d;
    public void upFileData(){
        File file=new File("/sdcard/wizrole/xingxing.jpg");
        HttpUtil.INSTANCE.putKeyUpload("file", file,MultipartBody.FORM, new HttpUtil.OnLengthListener() {
            @Override
            public void Length(long length) {
                lenght_d=length;
                handler.sendEmptyMessage(2);
            }

            @Override
            public void Count(long count) {
                count_d=count;
                handler.sendEmptyMessage(2);
            }
        }).putKeyCode("param","123456")
                .AskUpload("fileUploadHaveParam.do", new HttpUtil.OnDataListener() {
                    @Override
                    public void Error(String e) {
                        Toast.makeText(MainActivity.this,"文件带参数上传失败",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void Success(Object content) {
                        DataBack dataBa=(DataBack)content;
                        Toast.makeText(MainActivity.this,dataBa.getBody().getUrl(),Toast.LENGTH_LONG).show();
                    }
                }, DataBack.class);
    }

    public float progress_t;
    public Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0://下载
                    progressBar.setProgress((int)progress_t);
                    progress_text.setText((int)progress_t+"%");
                    break;
                case 1://单文件上传
                    progressBar_up.setProgress((int)(lenght_now/count_now)*100);
                    progress_text_up.setText((int)(lenght_now/count_now)*100+"%");
                    break;
                case 2://文件带参数
                    progress_up_d.setProgress((int)(lenght_d/count_d)*100);
                    progress_text_up_d.setText((int)(lenght_d/count_d)*100+"%");
                    break;
            }
        }
    };


   public void test() {
       File file=new File("/sdcard/wizrole/xingxing.jpg");
       RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
//       RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpeg"), file);
       MultipartBody builder =new  MultipartBody.Builder()
               .setType(MultipartBody.FORM)
               .addFormDataPart("head_image", "head_image", fileBody)
               .addFormDataPart("auth", "123456")
               .addFormDataPart("w", "50")
               .addFormDataPart("h", "50")
               .build();
       final Request request = new Request.Builder()
               .url("http://weihuo.s1.natapp.cc/Customer/cuploadpic/uploadpic")
               .post(builder)
               .build();
       OkHttpClient.Builder httpBuilder =new  OkHttpClient.Builder();
       OkHttpClient client=httpBuilder.build();
       client.newCall(request).enqueue(new Callback() {
           @Override
           public void onFailure(Call call, IOException e) {
               Toast.makeText(MainActivity.this,"失败",Toast.LENGTH_LONG).show();
           }

           @Override
           public void onResponse(Call call, Response response) throws IOException {
               String msg=response.body().string();
               Gson gson=new Gson();
               DataBack dataBack=gson.fromJson(msg,DataBack.class);
           }
       });
   }


    /**
     * String的MD5ֵ
     */
    private static String getMD5(String info) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(info.getBytes("UTF-8"));
            byte[] md5Array = md5.digest();
            return bytesToHex1(md5Array);
        } catch (NoSuchAlgorithmException e) {
            return "";
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }
    @NonNull
    private static String bytesToHex1(byte[] md5Array) {
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < md5Array.length; i++) {
            int temp = 0xff & md5Array[i];
            String hexString = Integer.toHexString(temp);
            if (hexString.length() == 1) {
                strBuilder.append("0").append(hexString);
            } else {
                strBuilder.append(hexString);
            }
        }
        return strBuilder.toString();
    }
}
