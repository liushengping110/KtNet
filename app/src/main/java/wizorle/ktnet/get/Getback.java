package wizorle.ktnet.get;

import java.util.List;

/**
 * Created by 何人执笔？ on 2018/5/16.
 * liushengping
 */

public class Getback {
    public int error_code;
    public String reason;
    public List<Result> result;
    public String content;
    public String hashId;
    public long unixtime;
    public String url;

    public int getError_code() {
        return error_code;
    }

    public String getReason() {
        return reason;
    }

    public List<Result> getResult() {
        return result;
    }

    public String getContent() {
        return content;
    }

    public String getHashId() {
        return hashId;
    }

    public long getUnixtime() {
        return unixtime;
    }

    public String getUrl() {
        return url;
    }
}
