<script setup>

import {Lock, User,Message} from "@element-plus/icons-vue";
import router from "@/router";
import {reactive, ref} from "vue";
import {ElMessage} from "element-plus";
import {post} from "@/utils";

const form = reactive({
  username:'',
  password:'',
  repeat_password:'',
  email:'',
  code:''
})
//初始设定获取验证码关闭
const isEmail = ref(false)

const validateUsername = (rule,value,callback) => {
  if (value === ''){
    callback(new Error('请输入用户名'))
  }else if(!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)){
    callback(new Error('用户名不能包含特殊字符,只能包含中文或英文'))
  }else {
    callback()
  }
}

const validatePassword = (rule,value,callback) => {
  if (value === ''){
    callback(new Error('请输入密码'))
  }else if(!/^[A-Za-z0-9_@]+$/.test(value)){
    callback(new Error('密码只能由数字字母下划线及@符号组成'))
  }else {
    callback()
  }
}
const validatePasswordIsSame = (rule,value,callback) => {
  if (value === ''){
    callback(new Error('请再次输入密码'))
  }else if(value !== form.password){
    callback(new Error('两次输入的密码不一致'))
  }else {
    callback()
  }
}

const rules = {
  username:[
    { validator: validateUsername,trigger:['blur','change']},
    { min:3,max:10,message:'用户名长度为3-10个字符',trigger:['blur','change']}
  ],
  password:[
    { required: true,message: '请输入密码', trigger: ['blur','change']},
    { validator: validatePassword,trigger:['blur','change']},
    { min:6,max:16,message:'用户名长度为6-16个字符',trigger:['blur','change'] }
  ],
  repeat_password:[
    { validator: validatePasswordIsSame,trigger:['blur','change'] }
  ],
  email:[
    { required: true,message: '请输入邮件地址', trigger: ['blur','change']},
    { type:'email', message:'请输入合法的电子邮件地址', trigger: ['blur','change']}
  ],
  code:[
    { required: true, message:'请输入验证码', trigger: ['blur','change']}
  ]
}

//如果邮箱格式正确,则获取验证码按钮开启
const onValidate = (prop,isValid)=>{
  if (prop === 'email'){
    isEmail.value = isValid
  }
}
const formRef = ref()

const register = ()=> {
  formRef.value.validate((isValid)=>{
    if (isValid){

    }else {
      ElMessage.warning('请完整填写注册表单内容')
    }
  })
}

const sendEmail = () =>{
  post('/api/auth/valid-email',{
    email: form.email
  },(message)=>{
    ElMessage.success(message)
  })
}


</script>

<template>
  <div style="text-align: center; margin: 0 20px">
    <div style="text-align: center; margin-top: 150px;">
      <div style="font-size: 25px; font-weight: bold">注册</div>
      <div style="font-size: 14px; color: grey">欢迎注册学习平台,请在下方完成用户注册</div>
    </div>
    <div style="margin-top: 50px">
      <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef">
        <el-form-item prop="username">
          <el-input type="text" placeholder="用户名" v-model="form.username">
            <template #prefix>
              <el-icon slot="prefix">
                <User/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input type="password" placeholder="密码" v-model="form.password">
            <template #prefix>
              <el-icon slot="prefix">
                <Lock/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="repeat_password">
          <el-input type="password" placeholder="确认密码" v-model="form.repeat_password">
            <template #prefix>
              <el-icon slot="prefix">
                <Lock/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="email">
          <el-input type="email" placeholder="电子邮箱" v-model="form.email">
            <template #prefix>
              <el-icon><Message /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="code">
          <el-row :gutter="10" style="width: 100%">
            <el-col :span="17">
              <el-input type="text" placeholder="请输入验证码" v-model="form.code">
              </el-input>
            </el-col>
            <el-col :span="5">
              <el-button type="primary" :disabled="!isEmail" @click="sendEmail">获取验证码</el-button>
            </el-col>
          </el-row>
        </el-form-item>
      </el-form>
    </div>

    <div style="margin-top: 80px;">
      <el-button style="width: 270px;" type="success" @click="register">注册</el-button>
    </div>
    <div style="margin-top: 20px">
      <span style="font-size: 14px; line-height: 15px; color: grey">已有账号?</span>
      <el-link type="primary" style="translate: 0 -2px" @click="router.push('/')">立即登录</el-link>
    </div>
  </div>
</template>
<style scoped>

</style>