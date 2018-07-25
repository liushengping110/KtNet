package wizorle.ktnet

import android.os.Environment
import android.os.Handler
import android.os.Looper
import okhttp3.*
import okio.Buffer
import okio.BufferedSink
import okio.Okio
import org.jetbrains.anko.doAsync
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import wizorle.ktnet.UrlInterceptor
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


/**
 * 网络请求工具类
 * Created by EdgeDi
 * 2017/9/19 17:28
 */
object HttpUtil {


    /**
     * 上传文件TYPE，二进制流
     * 大部分上传文件为二进制流，但具体还需询问后台
     */
    val BINARY_SYSTEM = MediaType.parse("application/octet-stream")!!
    /***
     *  域名需要http://||https://开头，否则会报错
     */
    private @Volatile
//    var base_url = "http://39.107.75.214:80//order_bg/"  //post-json数据带参数名
//    var base_url = "http://v.juhe.cn/joke/"//get
//    var base_url = "http://api.letvcloud.com/"//post
//        var base_url = "http://39.107.75.214:8080/wizrole_bg/"//downLoad
        var base_url = "http://192.168.1.132:8280/wizrole_bg/"//upload
//        var base_url = "http://weihuo.s1.natapp.cc/Customer/cuploadpic/"//upload 文件带参数

    /**
     * jsonTYPE
     */
    private val json = MediaType.parse("application/json; charset=utf-8")

    /**
     * 存放get请求和post请求的参数
     */
    private val get_map = hashMapOf<String, String>()
    /**
     * 存放头部
     */
    private val head_map = hashMapOf<String, String>()
    /**
     * 存放上传文件
     */
    private val upload_mep = hashMapOf<String, RequestBody>()

    private lateinit var call: Call<ResponseBody>
    private val servce by lazy { retrofit.create(HttpService::class.java) }

    private val client by lazy { OkHttpClient() }

    private var retrofit: Retrofit
    //加拦截器，对url处理
    init {
        val httpclient = OkHttpClient.Builder()
        httpclient.addInterceptor(UrlInterceptor())
        retrofit = Retrofit.Builder()
                .baseUrl(base_url)
                .client(httpclient.build())
                .build()
    }

    fun putHead(key: String, value: Any): HttpUtil {
        head_map.put(key, value.toString())
        return this
    }

    /**
     * 将参数添加进键值队，在Ask之前调用
     * @key
     * @value
     */
    fun putKeyCode(key: String, value: Any): HttpUtil {
        get_map.put(key, value.toString())
        return this
    }

    /**
     * 更改域名
     * @url 域名
     */
    fun setBase_Url(url: String): HttpUtil {
        retrofit = Retrofit.Builder()
                .baseUrl(url)
                .build()
        return this
    }

    /**
     * 将要上传的文件和key添加进键值队并进行进度监听
     * @key key的形式：Filedata\";filename=\"icon.png  Filedata为上传key,icon.png为服务端接收到文件的名字
     * @file 要上传的文件
     * @type 服务端要求的文件类型，默认可使用已定义好的TYPE
     * @onLengthListener 单个文件下载进度回调
     */
    fun putKeyUpload(key: String, file: File, type: MediaType, onLengthListener: OnLengthListener?): HttpUtil {
        val value = object : RequestBody() {
            override fun contentType(): MediaType? = type
            override fun contentLength(): Long = file.length()
            override fun writeTo(sink: BufferedSink?) {
                if (onLengthListener !== null) {
                    onLengthListener.Count(contentLength())
                    val source = Okio.source(file)
                    val buffer = Buffer()
                    var length = 0L
                    var count = source.read(buffer, 2048)
                    while (count != -1L) {
                        sink?.write(buffer, count)
                        length += count
                        count = source.read(buffer, 2048)
                        onLengthListener.Length(length)
                    }
                }
            }
        }
        upload_mep.put(key, value)
        return this
    }

    /**
     * 添加头部请求
     */
    private fun head(url: String) = servce.Head(url, get_map, head_map)

    /**
     * get请求
     */
    private fun get(url: String) = servce.GET(url, get_map)

    /**
     * post请求
     */
    private fun post(url: String) = servce.POST(url, get_map)

    /**
     * json请求
     */
    private fun json(url: String, json: JSONObject) =
            servce.JSON(url, RequestBody.create(this.json, json.toString()))//不带参数名
//            servce.JSON(url, FormBody.Builder().add("param", json.toString()).build())//带参数名

    /**
     * 上传文件
     */
    private fun upload(url: String) = servce.UPLOAD(url, upload_mep)

    /**
     * 下载文件
     */
    private fun load(url: String) = servce.LOAD(url, get_map)

    /**
     * 外部调用get
     * @url 接口
     * @onDataListener 请求数据回调
     */
    @Synchronized
    fun AskGet(url: String, onDataListener: OnDataListener, cla: Class<*>) {
        call = get(url)
        Asynchronous(call, onDataListener, cla)
    }

    /**
     * 外部调用post
     * 传参同上
     */
    @Synchronized
    fun AskPost(url: String, onDataListener: OnDataListener, cla: Class<*>) {
        call = post(url)
        Asynchronous(call, onDataListener, cla)
    }

    @Synchronized
    fun AskHead(url: String, onDataListener: OnDataListener, cla: Class<*>) {
        call = head(url)
        Asynchronous(call, onDataListener, cla)
    }

    /**
     * 外部调用json
     * @url 接口
     * @json JSONObject对象
     * @onDataListener 请求数据回调
     */
    @Synchronized
    fun AskJson(url: String, json: JSONObject, onDataListener: OnDataListener, cla: Class<*>) {
        call = json(url, json)
        Asynchronous(call, onDataListener, cla)
    }


    /**
     * 外部调用上传
     * 传参同get
     */
    @Synchronized
    fun AskUpload(url: String, onDataListener: OnDataListener, cla: Class<*>) {
        call = upload(url)
        Asynchronous(call, onDataListener, cla)
    }

    /**
     * 外部调用下载
     * @url 接口
     * @onLoadingListener 下载结果回调
     * 错误回调和下载完成回调均在主线程，进度则在子线程，方便操作
     */
    @Synchronized
    fun AskLoad(url: String, name: String, onLoadingListener: OnLoadingListener, filetype: String = ".apk") {
        val request = Request.Builder()
                .url(url)
                .build()
        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call?, e: IOException?) {
                handler.post { onLoadingListener.onError(e.toString()) }
            }

            override fun onResponse(call: okhttp3.Call?, response: okhttp3.Response?) {
                if (response != null) {
                    val buf = ByteArray(2048)
                    val path =
                            /**isExistDir(saveDir)*/
                            Environment.getExternalStorageDirectory()
                    val input = response.body()?.byteStream()
                    val length = response.body()?.contentLength()
                    //选定文件夹
                    val file_path=File("/sdcard/wizrole/")
                    if(!file_path.exists()){
                        file_path.mkdir();
                    }
                    val filePath="/sdcard/wizrole/"+name+filetype
                    val file = File(filePath)
//                    val file = File(path, name + filetype)
                    val output = FileOutputStream(file)
                    var sum = 0L
                    var len = input?.read(buf)?.toLong()
                    try {
                        while (len != -1L) {
                            output.write(buf, 0, len!!.toInt())
                            sum += len
                            val progress = sum * 1.0f / length!! * 100
                            onLoadingListener.onProgress(progress)
                            len = input?.read(buf)?.toLong()
                        }
                        output.flush()
                        handler.post {
                            onLoadingListener.onSuccess(file)
                        }
                    } catch (e: Exception) {
                        handler.post { onLoadingListener.onError(e.toString()) }
                    } finally {
                        input?.close()
                        output.close()
                    }
                } else {
                    handler.post { onLoadingListener.onError("数据为空") }
                }
            }
        })
    }

    /**
     * 在页面destroy时调用，结束还在请求的call
     */
    fun Cancel() {
        call.cancel()
    }

    /**
     * 发起请求
     */
    private fun Asynchronous(call: Call<ResponseBody>, onDataListener: OnDataListener, clazz: Class<*>) {
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                doAsync {
                    val string = response?.body()?.string()
                    if (string != null) {
                        val bean = JsonUtil.fromJson(string, clazz)
                        if (bean != null) {
                            handler.post { onDataListener.Success(bean) }
                        } else {
                            handler.post({ onDataListener.Error("数据解析错误") })
                        }
                    } else {
                        handler.post({ onDataListener.Error("请求数据为空") })
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                handler.post { onDataListener.Error(t.toString()) }
            }

        })
        if (get_map.size > 0)
            get_map.clear()
        if (upload_mep.size > 0)
            upload_mep.clear()
        if (head_map.size > 0)
            head_map.clear()
    }

    private val handler = object : Handler(Looper.getMainLooper()) {}

    /**
     * 请求数据回调
     */
    interface OnDataListener {

        fun Error(e: String?)

        fun Success(content: Any)

    }

    /**
     * 上传回调
     */
    interface OnLengthListener {

        fun Length(length: Long)

        fun Count(count: Long)

    }

    /**
     * 下载回调
     */
    interface OnLoadingListener {

        fun onSuccess(file: File)

        fun onError(e: String)

        fun onProgress(progress: Float)

    }

}
