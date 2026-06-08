<template>
  <AppLayout>
    <div class="max-w-5xl mx-auto space-y-10">
      
      <!-- Hero Section -->
      <div class="text-center py-12 space-y-4">
        <h2 class="text-4xl sm:text-5xl font-extrabold text-slate-900 tracking-tight">
          欢迎使用 <span class="text-blue-600">Sky-Lark</span>
        </h2>
        <p class="text-xl text-slate-500 max-w-2xl mx-auto">
          基于 Spring Boot 3 & Vue 3 的稳健全栈基础设施。
          为您的下一个伟大创意已准备就绪。
        </p>
      </div>

      <!-- Status Cards -->
      <div class="grid gap-6 grid-cols-1 md:grid-cols-3">
        <!-- Frontend Card -->
        <el-card shadow="hover" class="status-card border-none ring-1 ring-slate-200">
          <template #header>
            <div class="flex items-center gap-3 mb-1">
              <div class="p-2 bg-blue-50 rounded-lg text-blue-600">
                <el-icon :size="20"><Monitor /></el-icon>
              </div>
              <h3 class="font-bold text-lg text-slate-800">前端 (Frontend)</h3>
            </div>
          </template>
          <div class="space-y-3">
            <p class="text-slate-600 text-sm">Vue 3 + Vite + Tailwind</p>
            <div class="flex items-center gap-2">
              <div class="h-2.5 w-2.5 rounded-full bg-green-500 animate-pulse"></div>
              <span class="text-xs font-medium text-slate-500">运行端口 3000</span>
            </div>
          </div>
        </el-card>

        <!-- Backend Card -->
        <el-card shadow="hover" class="status-card border-none ring-1 ring-slate-200">
          <template #header>
            <div class="flex items-center gap-3 mb-1">
              <div class="p-2 bg-green-50 rounded-lg text-green-600">
                <el-icon :size="20"><Connection /></el-icon>
              </div>
              <h3 class="font-bold text-lg text-slate-800">后端 API (Backend)</h3>
            </div>
          </template>
          <div class="space-y-3">
            <div v-if="loadingHealth" class="py-1">
              <el-skeleton animated :rows="1" />
            </div>
            <div v-else>
              <p class="text-slate-600 text-sm truncate" :title="backendStatus">
                {{ backendStatus || '连接中...' }}
              </p>
              <div class="flex items-center gap-2 mt-2">
                <el-button 
                  v-if="!isBackendUp" 
                  type="danger" 
                  size="small" 
                  plain 
                  @click="checkHealth"
                >
                  重试
                </el-button>
                <div v-else class="flex items-center gap-2">
                   <div class="h-2.5 w-2.5 rounded-full bg-green-500"></div>
                   <span class="text-xs font-medium text-slate-500">已连接</span>
                </div>
              </div>
            </div>
          </div>
        </el-card>

        <!-- Database Card -->
        <el-card shadow="hover" class="status-card border-none ring-1 ring-slate-200">
          <template #header>
            <div class="flex items-center gap-3 mb-1">
              <div class="p-2 bg-orange-50 rounded-lg text-orange-600">
                <el-icon :size="20"><DataLine /></el-icon>
              </div>
              <h3 class="font-bold text-lg text-slate-800">数据库 (Database)</h3>
            </div>
          </template>
          <div class="space-y-3">
            <p class="text-slate-600 text-sm">MySQL 8.0</p>
             <div class="flex items-center gap-2">
              <el-tag size="small" type="warning" effect="plain">端口 3306</el-tag>
              <span class="text-xs font-medium text-slate-500">已容器化</span>
            </div>
          </div>
        </el-card>
      </div>

      <!-- Data Table Section -->
      <div class="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
        <div class="p-6 border-b border-slate-100 flex justify-between items-center">
          <h3 class="text-lg font-bold text-slate-800">实时数据</h3>
          <el-button type="primary" size="small" @click="fetchData" :loading="loadingData">
            刷新数据
          </el-button>
        </div>
        <div class="p-0">
          <el-table :data="recordData" v-loading="loadingData" style="width: 100%" :header-cell-style="{ background: '#f8fafc', color: '#64748b' }">
            <el-table-column prop="id" label="ID" width="80" align="center" />
            <el-table-column prop="name" label="名称" width="200">
              <template #default="{ row }">
                <span class="font-medium text-slate-700">{{ row.name }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="description" label="描述" />
          </el-table>
        </div>
      </div>

    </div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import AppLayout from '@/components/AppLayout.vue'
import api from '@/api'

const loadingHealth = ref(false)
const loadingData = ref(false)
const backendStatus = ref('')
const isBackendUp = ref(false)
const recordData = ref([])
const sleep = (ms) => new Promise((resolve) => setTimeout(resolve, ms))

const checkHealth = async () => {
  loadingHealth.value = true
  try {
    const res = await api.get('/health')
    backendStatus.value = res.message
    isBackendUp.value = res.status === 'UP'
  } catch (err) {
    backendStatus.value = '后端已断开'
    isBackendUp.value = false
  } finally {
    loadingHealth.value = false
  }
}

const fetchData = async () => {
  loadingData.value = true
  try {
    for (let attempt = 0; attempt < 2; attempt += 1) {
      if (!isBackendUp.value) {
        await checkHealth()
      }
      if (!isBackendUp.value) {
        if (attempt === 0) {
          await sleep(800)
          continue
        }
        ElMessage.warning('后端未就绪，请稍后重试')
        return
      }
      try {
        const res = await api.get('/records')
        recordData.value = res
        return
      } catch (err) {
        if (attempt === 0) {
          await sleep(800)
          continue
        }
        ElMessage.error('数据加载失败，请稍后重试')
        return
      }
    }
  } finally {
    loadingData.value = false
  }
}

onMounted(async () => {
  await checkHealth()
  if (isBackendUp.value) {
    await fetchData()
  }
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
