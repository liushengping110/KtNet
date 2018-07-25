package wizorle.ktnet;

/**
 * Created by 何人执笔？ on 2018/5/16.
 * liushengping
 */

public class Mon {

    public String content;//金额
    public boolean status;//选中状态

    public boolean isStatus() {
        return status;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
