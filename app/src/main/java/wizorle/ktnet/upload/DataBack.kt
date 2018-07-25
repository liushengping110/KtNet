package wizorle.ktnet.upload

/**
 * Created by 何人执笔？ on 2018/7/20.
 * liushengping
 */
data class DataBack(var header:Header,var body:Body){
    data class Header(var rspCode:Int,var rspMsg: String)
    data class Body(var url:String,var thumb:String)
}