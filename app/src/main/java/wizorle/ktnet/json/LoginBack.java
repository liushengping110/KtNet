package wizorle.ktnet.json;

import java.io.Serializable;

/**
 * Created by liushengping on 2017/11/16/016.
 * 何人执笔？
 * 登录成功返回
 */

public class LoginBack implements Serializable{

    public String ResultCode;
    public String ResultContent;
    public String HostNo;    // 该用户的主键ID
    public String HostName;//用户真实姓名
    public String HostAvatar;//用户头像地址
    public String InfoSign;//      - 信息标志(1-个人信息未完成 店铺完成  , 2-个人完成,店铺未完成  , 3- 个人未完成,店铺未完成  , 0-两个都完成)


    public String getInfoSign() {
        return InfoSign;
    }

    public String getHostAvatar() {
        return HostAvatar;
    }


    public String getHostName() {
        return HostName;
    }

    public String getResultContent() {
        return ResultContent;
    }

    public String getResultCode() {
        return ResultCode;
    }

    public String getHostNo() {
        return HostNo;
    }
}
