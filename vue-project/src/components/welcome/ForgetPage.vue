<script setup>

import {Lock, Message} from "@element-plus/icons-vue";
import {reactive, ref} from 'vue'
import {post} from "@/utils";
import {ElMessage} from "element-plus";
import router from "@/router";

//初始设定获取验证码关闭
const isEmail = ref(false)

const active = ref(0)

const coldTime = ref(0)

const formRef = ref()

const form = reactive({
  email:'',
  code:'',
  password:'',
  repeat_password:''
})

const validatePasswordIsSame = (rule,value,callback) => {
  if (value === ''){
    callback(new Error('请再次输入密码'))
  }else if(value !== form.password){
    callback(new Error('两次输入的密码不一致'))
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



const rules = {
  email:[
    { required: true,message: '请输入邮件地址', trigger: ['blur','change']},
    { type:'email', message:'请输入合法的电子邮件地址', trigger: ['blur','change']}
  ],
  code:[
    { required: true, message:'请输入验证码', trigger: ['blur','change']}
  ],
  password:[
    { required: true,message: '请输入密码', trigger: ['blur','change']},
    { validator: validatePassword,trigger:['blur','change']},
    { min:6,max:16,message:'密码长度为6-16个字符',trigger:['blur','change'] }
  ],
  repeat_password:[
    { validator: validatePasswordIsSame,trigger:['blur','change'] }
  ],
}


const onValidate = (prop,isValid)=>{
  if (prop === 'email'){
    isEmail.value = isValid
  }
}


const sendEmail = () =>{
  coldTime.value = 60
  post('/api/auth/valid-reset-email',{
    email: form.email
  },(message)=>{
    ElMessage.success(message)

    setInterval(()=> coldTime.value--,1000)
  },(message)=> {
    ElMessage.warning(message)
    coldTime.value = 0
  })
}


const startReset =()=> {
  formRef.value.validate((isValid)=>{
    if (isValid){
      post('/api/auth/start-reset',{
        email: form.email,
        code: form.code
      },()=>{
        active.value++
      })
    }else {
      ElMessage.warning('请填写电子邮件和验证码')
    }
  })
}

const doReset =()=> {
  formRef.value.validate((isValid)=>{
    if (isValid){
      post('/api/auth/do-reset',{
        password: form.password
      },(message)=>{
        ElMessage.success(message)
        router.push('/')
      })
    }else {
      ElMessage.warning('请完整填写新密码')
    }
  })

}

</script>

<template>
  <div>
    <div style="margin: 30px 20px">
      <el-steps :active=active finish-status="success" align-center>
        <el-step title="验证电子邮件"></el-step>
        <el-step title="重新设定密码"></el-step>
      </el-steps>
    </div>
    <transition name="el-fade-in-linear" mode="out-in">
      <div style="text-align: center; margin: 0 20px;height: 100%" v-if="active === 0">
        <div style="margin-top: 100px">
          <div style="font-size: 25px;font-weight: bold">重置密码</div>
          <div style="font-size: 14px;color: grey">请输入需要重置密码的电子邮件地址</div>
        </div>
        <div style="margin-top: 50px">
          <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef">
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
                  <el-input type="text" placeholder="请输入验证码" v-model="form.code" :maxlength="6">
                  </el-input>
                </el-col>
                <el-col :span="5">
                  <el-button type="primary" :disabled="!isEmail || coldTime>0" @click="sendEmail">{{ coldTime>0 ? '请稍后'+coldTime+'秒' : '获取验证码' }}</el-button>
                </el-col>
              </el-row>
            </el-form-item>
          </el-form>
        </div>
        <div style="margin-top: 70px">
          <el-button @click="startReset()" style="width: 270px" type="danger">开始重置</el-button>
        </div>
      </div>
    </transition>

    <transition name="el-fade-in-linear" mode="out-in">
      <div style="text-align: center; margin: 0 20px;height: 100%" v-if="active === 1">
        <div style="margin-top: 80px">
          <div style="font-size: 25px;font-weight: bold">重置密码</div>
          <div style="font-size: 14px;color: grey">请填写您的新密码,务必牢记,防止丢失</div>
        </div>

        <div style="margin-top: 50px">
          <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef">
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
          </el-form>
        </div>
        <div style="margin-top: 70px">
          <el-button @click="doReset()" style="width: 270px" type="danger">立即重置</el-button>
        </div>
      </div>
    </transition>
  </div>
</template>

<style scoped>

</style>