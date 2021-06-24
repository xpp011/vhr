import { Message } from 'element-ui';
import el from "element-ui/src/locale/lang/el";
import router from "@/router";
const axios = require('axios').default;
/*axios请求封装   对异常返回数据做统一处理*/
axios.interceptors.response.use(success=>{
    if (success.status && success.status==200 && success.data.status == 500){
        Message.error({message:success.data.msg})
        return;
    }
    if (success.data.msg){
        Message.success({message:success.data.msg})
    }
    return success.data;
},error => {
    if (error.response.status==504 || error.response.status==404){
        Message.error({message:"服务器出去玩了，稍等一下"})
    }else if (error.response.status==403){
        Message.error({message:"该用户权限不足"})
    }else if (error.response.status==401){
        Message.error({message:"未登录，请前往登录"})
        router.replace("/");
    }else {
        Message.error({message:"未知错误!!!"})
    }
    return;
})

let base="";

export const postJsonRequest=(url,params)=>{
    return axios({
        method:"post",
        url:`${base}${url}`,
        data:params,
        headers:{
            'Content-Type':'application/json'
        }
    })
}

export const getJsonRequest=(url,params)=>{
    return axios({
        method:"get",
        url:`${base}${url}`,
        data:params,
        headers:{
            'Content-Type':'application/json'
        }
    })
}

export const putJsonRequest=(url,params)=>{
    return axios({
        method:"put",
        url:`${base}${url}`,
        data:params,
        headers:{
            'Content-Type':'application/json'
        }
    })
}

export const deleteJsonRequest=(url,params)=>{
    return axios({
        method:"delete",
        url:`${base}${url}`,
        data:params,
        headers:{
            'Content-Type':'application/json'
        }
    })
}
