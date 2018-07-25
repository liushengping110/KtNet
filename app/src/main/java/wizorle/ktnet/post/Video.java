package wizorle.ktnet.post;

import java.util.List;

/**
 * Created by 何人执笔？ on 2018/5/12.
 * liushengping
 */

public class Video {


    public int code;
    public String message;
    public int total;
    public List<VideoList> data;

    public int getCode() {
        return code;
    }

    public List<VideoList> getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}
