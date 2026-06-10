<template>
  <AppLayout>
    <div class="max-w-5xl mx-auto space-y-6">
      <div class="flex items-center justify-between">
        <h2 class="text-2xl font-bold text-slate-900">我的预约</h2>
        <div class="flex gap-3">
          <el-input v-model="searchUser" placeholder="输入姓名查询" clearable style="width:200px" @keyup.enter="searchAppointments" />
          <el-button type="primary" @click="searchAppointments" :loading="loading">查询</el-button>
        </div>
      </div>

      <div v-if="appointments.length === 0 && !loading" class="bg-white rounded-xl shadow-sm border border-slate-200 p-12 text-center">
        <el-icon :size="48" class="text-slate-300 mb-4"><Calendar /></el-icon>
        <p class="text-slate-400">暂无预约记录</p>
        <router-link to="/booking">
          <el-button type="primary" class="mt-4">去预约</el-button>
        </router-link>
      </div>

      <div v-else class="space-y-4">
        <div v-for="apt in appointments" :key="apt.id" class="bg-white rounded-xl shadow-sm border border-slate-200 p-5 hover:shadow-md transition-shadow">
          <div class="flex items-start justify-between mb-3">
            <div>
              <div class="flex items-center gap-3 mb-1">
                <h3 class="font-bold text-slate-800">{{ apt.instrumentName }}</h3>
                <el-tag size="small" :type="getTypeTag(apt.instrumentType)">{{ getTypeLabel(apt.instrumentType) }}</el-tag>
                <el-tag size="small" :type="getStatusType(apt.status)">{{ getStatusLabel(apt.status) }}</el-tag>
              </div>
              <p class="text-sm text-slate-400">{{ apt.groupName }} · {{ apt.userName }}</p>
            </div>
            <div class="text-right">
              <p class="text-xs text-slate-400">{{ formatDate(apt.createdAt) }}</p>
            </div>
          </div>

          <div class="grid grid-cols-2 md:grid-cols-4 gap-3 mb-3">
            <div class="bg-slate-50 rounded-lg p-3">
              <p class="text-xs text-slate-400">预约时长</p>
              <p class="font-semibold text-slate-700">{{ apt.bookedHours }}h</p>
            </div>
            <div class="bg-slate-50 rounded-lg p-3">
              <p class="text-xs text-slate-400">预估费用</p>
              <p class="font-semibold text-slate-700">¥{{ apt.estimatedCost }}</p>
            </div>
            <div class="bg-slate-50 rounded-lg p-3">
              <p class="text-xs text-slate-400">实际时长</p>
              <p class="font-semibold text-slate-700">{{ apt.actualHours != null ? apt.actualHours + 'h' : '-' }}</p>
            </div>
            <div class="bg-slate-50 rounded-lg p-3">
              <p class="text-xs text-slate-400">实际费用</p>
              <p class="font-semibold text-slate-700">{{ apt.actualCost != null ? '¥' + apt.actualCost : '-' }}</p>
            </div>
          </div>

          <div class="flex items-center gap-3 text-xs text-slate-400">
            <span :class="apt.useConsumables ? 'text-green-500' : ''">耗材: {{ apt.useConsumables ? '是' : '否' }}</span>
            <span :class="apt.isUrgent ? 'text-orange-500' : ''">加急: {{ apt.isUrgent ? '是' : '否' }}</span>
          </div>

          <div v-if="apt.adjustmentAmount != null && apt.adjustmentAmount !== 0" class="mt-3 p-3 rounded-lg"
               :class="apt.adjustmentAmount > 0 ? 'bg-orange-50 border border-orange-200' : 'bg-green-50 border border-green-200'">
            <div class="flex items-center gap-2">
              <el-icon :size="14" :class="apt.adjustmentAmount > 0 ? 'text-orange-500' : 'text-green-500'">
                <WarningFilled v-if="apt.adjustmentAmount > 0" /><CircleCheckFilled v-else />
              </el-icon>
              <span class="text-sm font-medium" :class="apt.adjustmentAmount > 0 ? 'text-orange-700' : 'text-green-700'">
                {{ apt.adjustmentAmount > 0 ? '补扣' : '退还' }} ¥{{ Math.abs(apt.adjustmentAmount) }}
              </span>
            </div>
            <p v-if="apt.adjustmentReason" class="text-xs text-slate-500 mt-1 ml-5">调整说明：{{ apt.adjustmentReason }}</p>
          </div>

          <div class="flex gap-2 mt-3" v-if="apt.status === 'CONFIRMED'">
            <el-button size="small" type="danger" plain @click="handleCancel(apt.id)">取消预约</el-button>
          </div>
        </div>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import AppLayout from '@/components/AppLayout.vue'
import api from '@/api'

const appointments = ref([])
const loading = ref(false)
const searchUser = ref('')

function getTypeLabel(type) {
  const map = { NMR: '核磁', SEQUENCER: '测序', EM: '电镜' }
  return map[type] || type
}

function getTypeTag(type) {
  const map = { NMR: 'primary', SEQUENCER: 'success', EM: 'warning' }
  return map[type] || 'info'
}

function getStatusLabel(status) {
  const map = { PENDING: '待确认', CONFIRMED: '已确认', COMPLETED: '已完成', SETTLED: '已结算', CANCELLED: '已取消' }
  return map[status] || status
}

function getStatusType(status) {
  const map = { PENDING: 'warning', CONFIRMED: '', COMPLETED: 'success', SETTLED: 'info', CANCELLED: 'danger' }
  return map[status] || 'info'
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return d.toLocaleString('zh-CN')
}

async function searchAppointments() {
  if (!searchUser.value.trim()) {
    ElMessage.warning('请输入姓名')
    return
  }
  loading.value = true
  try {
    appointments.value = await api.get(`/appointments/user/${encodeURIComponent(searchUser.value.trim())}`)
  } finally {
    loading.value = false
  }
}

async function handleCancel(id) {
  try {
    await ElMessageBox.confirm('确定要取消此预约吗？预扣费用将退还至课题组账户。', '取消预约', { type: 'warning' })
    loading.value = true
    await api.put(`/appointments/${id}/cancel`)
    ElMessage.success('预约已取消，费用已退还')
    await searchAppointments()
  } catch {
    // cancelled
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  loading.value = true
  try {
    appointments.value = await api.get('/appointments')
  } finally {
    loading.value = false
  }
})
</script>
