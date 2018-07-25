package wizorle.ktnet;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by 何人执笔？ on 2018/5/16.
 * liushengping
 * url拦截器
 */

public class UrlInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request build = chain.request().newBuilder().url(chain.request().url().url().toString() ).build();
        return chain.proceed(build);
    }
}