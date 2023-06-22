<template>
  <div style="width: 100vw; height: 100vh; overflow: hidden; display: flex">
      <div style="flex: 1;">
        <el-image style="width: 100%; height: 100%" fit="cover"
                  src="https://cdn.pixabay.com/photo/2017/12/29/18/47/mountains-3048299_1280.jpg"></el-image>
      </div>
      <div class="welcome-title">
        <div style="font-size: 30px; font-weight: bold">欢迎来到我的学习平台</div>
        <div style="margin-top: 10px">在这里你可以学习如何使用vue3.0和springboot3.0搭建前后端分离项目网站</div>
        <div style="margin-top: 5px">作者微信: _5201314erlang</div>
      </div>
      <div style="width: 400px; background-color: white; z-index: 1">
        <router-view v-slot="{ Component }">
          <transition name="el-fade-in-linear" mode="out-in">
            <component :is="Component" style="height: 100%"/>
          </transition>
        </router-view>
      </div>
    </div>
</template>
<script setup>

</script>
<style scoped>
.welcome-title {
  position: absolute;
  bottom: 30px;
  left: 30px;
  color: white;
  text-shadow: 0 0 10px black;
}
</style>