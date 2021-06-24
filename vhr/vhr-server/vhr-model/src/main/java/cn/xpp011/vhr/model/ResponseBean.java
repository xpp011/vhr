package cn.xpp011.vhr.model;

/**
 * @作者 江南一点雨
 * @公众号 江南一点雨
 * @微信号 a_java_boy
 * @GitHub https://github.com/lenve
 * @博客 http://wangsong.blog.csdn.net
 * @网站 http://www.javaboy.org
 * @时间 2019-09-20 8:39
 */
public class ResponseBean {
    private Integer status;
    private String msg;
    private Object obj;

    public static ResponseBean ok(String msg){
        return new ResponseBean(200,msg,null);
    }

    public static ResponseBean ok(String msg,Object obj){
        return new ResponseBean(200,msg,obj);
    }

    public static ResponseBean error(String msg){
        return new ResponseBean(500,msg,null);
    }

    public static ResponseBean error(String msg,Object obj){
        return new ResponseBean(500,msg,obj);
    }


    //使其类无法创造
    private ResponseBean() {
    }

    private ResponseBean(Integer status, String msg, Object obj) {
        this.status = status;
        this.msg = msg;
        this.obj = obj;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }
}
