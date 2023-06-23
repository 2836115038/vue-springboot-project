<template>
  <div style="text-align: center; margin: 0 20px">
    <div style="text-align: center; margin-top: 150px;">
      <div style="font-size: 25px; font-weight: bold">登录</div>
      <div style="font-size: 14px; color: grey">在进入系统前请先登录</div>
    </div>
    <div style="margin-top: 50px;">
      <el-input v-model="form.username" type="text" placeholder="用户名/邮箱">
        <template #prefix>
          <el-icon slot="prefix">
            <User/>
          </el-icon>
        </template>
      </el-input>
      <el-input v-model="form.password" type="password" placeholder="密码" style="margin-top: 10px;">
        <template #prefix>
          <el-icon slot="prefix">
            <Lock/>
          </el-icon>
        </template>
      </el-input>
    </div>
    <el-row style="margin-top: 5px;">
      <el-col :span="12" style="text-align: left">
        <el-checkbox v-model="form.remember" label="记住我" size="large"></el-checkbox>
      </el-col>
      <el-col :span="12" style="text-align: right">
        <el-link @click="router.push('/forget')">忘记密码?</el-link>
      </el-col>
    </el-row>
    <div style="margin-top: 40px">
      <el-button @click="login()" type="success" style="width: 270px; font-weight: bold" plain>登陆</el-button>
    </div>
    <el-divider>
      <span style="color: grey">没有账号</span>
    </el-divider>
    <div>
      <el-button @click="router.push('/register')" style="width: 270px;" type="warning" plain>注册</el-button>
    </div>
  </div>
</template>
<script setup>
import {Lock, User} from "@element-plus/icons-vue";
import {reactive} from "vue";
import {ElMessage} from "element-plus";
import {get, post} from "@/utils";
import router from "@/router";
import {useStore} from "@/stores";

const form = reactive({
  username: '',
  password: '',
  remember: false
})

const store = useStore()
const login = () => {
  if (!form.username || !form.password) {
    ElMessage.warning('用户名和密码不能为空!')
  } else {
    post('/api/auth/login', {
      username: form.username,
      password: form.password,
      remember: form.remember
    }, (message) => {
      ElMessage.success(message)
      get('/api/user/me',(message)=>{
        store.auth.user = message
        router.push('/index')
      },()=>{
        store.auth.user = null
      })
    })
  }
}
</script>
<style scoped>

</style>