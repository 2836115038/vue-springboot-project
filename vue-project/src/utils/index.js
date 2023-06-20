import axios from "axios";
import {ElMessage} from "element-plus";

const defaultError = ()=>ElMessage.error('发生未知错误,请联系管理员!')
const defaultFailure = (message) =>ElMessage.warning(message)

function post(url,data,success,failure = defaultFailure,error = defaultError){
    axios.post(url,data,{
        headers:{
            'Content-Type':'application/x-www-form-urlencoded'
        },
        withCredentials:true
    }).then(({data})=>{
        if (data.code === 200){
            success(data.message,data.code)
        }else {
            failure(data.message,data.code)
        }
    }).catch(error)
}

function get(url,success,failure = defaultFailure,error = defaultError){
    axios.post(url,{
        withCredentials:true
    }).then(({data})=>{
        if (data.code === 200){
            success(data.message,data.code)
        }else {
            failure(data.message,data.code)
        }
    }).catch(error)
}

export { get ,post }