<template>
  <AppLayout>
    <div class="max-w-5xl mx-auto space-y-10">

      <div class="text-center py-12 space-y-4">
        <h2 class="text-4xl sm:text-5xl font-extrabold text-slate-900 tracking-tight">
          大型仪器 <span class="text-blue-600">预约管理平台</span>
        </h2>
        <p class="text-xl text-slate-500 max-w-2xl mx-auto">
          核磁 · 测序 · 电镜 — 在线预约、预算管控、自动结算
        </p>
      </div>

      <div class="grid gap-6 grid-cols-1 md:grid-cols-3">
        <router-link to="/booking" class="block">
          <el-card shadow="hover" class="status-card border-none ring-1 ring-slate-200 h-full cursor-pointer">
            <template #header>
              <div class="flex items-center gap-3 mb-1">
                <div class="p-2 bg-blue-50 rounded-lg text-blue-600">
                  <el-icon :size="20"><Calendar /></el-icon>
                </div>
                <h3 class="font-bold text-lg text-slate-800">仪器预约</h3>
              </div>
            </template>
            <p class="text-slate-600 text-sm">选择核磁、测序仪或电镜，填写时长与耗材，预估费用即时展示。</p>
          </el-card>
        </router-link>

        <router-link to="/appointments" class="block">
          <el-card shadow="hover" class="status-card border-none ring-1 ring-slate-200 h-full cursor-pointer">
            <template #header>
              <div class="flex items-center gap-3 mb-1">
                <div class="p-2 bg-green-50 rounded-lg text-green-600">
                  <el-icon :size="20"><List /></el-icon>
                </div>
                <h3 class="font-bold text-lg text-slate-800">我的预约</h3>
              </div>
            </template>
            <p class="text-slate-600 text-sm">查看预约状态、结算结果与差额调整说明，支持取消未开始的预约。</p>
          </el-card>
        </router-link>

        <router-link to="/budget" class="block">
          <el-card shadow="hover" class="status-card border-none ring-1 ring-slate-200 h-full cursor-pointer">
            <template #header>
              <div class="flex items-center gap-3 mb-1">
                <div class="p-2 bg-orange-50 rounded-lg text-orange-600">
                  <el-icon :size="20"><Wallet /></el-icon>
                </div>
                <h3 class="font-bold text-lg text-slate-800">项目额度</h3>
              </div>
            </template>
            <p class="text-slate-600 text-sm">仅展示本课题组账户余额与扣费流水，其他组信息不可见。</p>
          </el-card>
        </router-link>
      </div>

      <div class="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
        <div class="p-6 border-b border-slate-100 flex justify-between items-center">
          <h3 class="text-lg font-bold text-slate-800">系统状态</h3>
          <el-button type="primary" size="small" @click="checkHealth" :loading="loadingHealth">
            检查连接
          </el-button>
        </div>
        <div class="p-6 grid gap-4 grid-cols-1 md:grid-cols-3">
          <div class="flex items-center gap-3">
            <div class="h-3 w-3 rounded-full" :class="isBackendUp ? 'bg-green-500' : 'bg-red-400'"></div>
            <span class="text-sm text-slate-600">后端 API</span>
            <el-tag size="small" :type="isBackendUp ? 'success' : 'danger'">{{ isBackendUp ? '在线' : '离线' }}</el-tag>
          </div>
          <div class="flex items-center gap-3">
            <div class="h-3 w-3 rounded-full bg-green-500"></div>
            <span class="text-sm text-slate-600">前端服务</span>
            <el-tag size="small" type="success">在线</el-tag>
          </div>
          <div class="flex items-center gap-3">
            <div class="h-3 w-3 rounded-full bg-green-500"></div>
            <span class="text-sm text-slate-600">MySQL 8.0</span>
            <el-tag size="small" type="success">容器化</el-tag>
          </div>
        </div>
      </div>

    </div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import AppLayout from '@/components/AppLayout.vue'
import api from '@/api'

const loadingHealth = ref(false)
const isBackendUp = ref(false)

const checkHealth = async () => {
  loadingHealth.value = true
  try {
    const res = await api.get('/health')
    isBackendUp.value = res.status === 'UP'
  } catch {
    isBackendUp.value = false
  } finally {
    loadingHealth.value = false
  }
}

onMounted(() => {
  checkHealth()
})
</script>

<style scoped>
.status-card {
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}
.status-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 15px -3px rgb(0 0 0 / 0.1), 0 4px 6px -4px rgb(0 0 0 / 0.1);
}
</style>
