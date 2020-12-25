package zs3;

public class Msg_json {
    public String type = null;//消息类型
    public int from = 0;//发送方的uid
    public String fromName = null;//发送方的昵称
    public int to = 0;//接收方的uid
    public String msg = null;//私聊或评论的信息
    public long nowDate = 0;//发送私聊、点赞或评论的时间
    public int postID = 0;//表白贴或讨论贴的id
}
