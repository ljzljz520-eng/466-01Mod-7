<template>
  <AppLayout>
    <div class="max-w-5xl mx-auto space-y-6">
      <div class="flex items-center justify-between">
        <h2 class="text-2xl font-bold text-slate-900">结算管理</h2>
        <el-select v-model="statusFilter" placeholder="筛选状态" style="width:160px" @change="filterByStatus">
          <el-option label="全部" value="" />
          <el-option label="已确认（待完成）" value="CONFIRMED" />
          <el-option label="已完成（待结算）" value="COMPLETED" />
          <el-option label="已结算" value="SETTLED" />
        </el-select>
      </div>

      <div v-if="appointments.length === 0 && !loading" class="bg-white rounded-xl shadow-sm border border-slate-200 p-12 text-center">
        <el-icon :size="48" class="text-slate-300 mb-4"><DocumentChecked /></el-icon>
        <p class="text-slate-400">暂无待处理的预约</p>
      </div>

      <div v-else class="space-y-4">
        <div v-for="apt in appointments" :key="apt.id" class="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
          <div class="p-5">
            <div class="flex items-start justify-between mb-3">
              <div>
                <div class="flex items-center gap-3 mb-1">
                  <h3 class="font-bold text-slate-800">{{ apt.instrumentName }}</h3>
                  <el-tag size="small" :type="getTypeTag(apt.instrumentType)">{{ getTypeLabel(apt.instrumentType) }}</el-tag>
                  <el-tag size="small" :type="getStatusType(apt.status)">{{ getStatusLabel(apt.status) }}</el-tag>
                </div>
                <p class="text-sm text-slate-400">{{ apt.groupName }} · {{ apt.userName }} · {{ formatDate(apt.createdAt) }}</p>
              </div>
            </div>

            <div class="grid grid-cols-2 md:grid-cols-4 gap-3">
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

            <div v-if="apt.adjustmentAmount != null && apt.adjustmentAmount !== 0" class="mt-3 p-3 rounded-lg"
                 :class="apt.adjustmentAmount > 0 ? 'bg-orange-50 border border-orange-200' : 'bg-green-50 border border-green-200'">
              <div class="flex items-center gap-2">
                <span class="text-sm font-medium" :class="apt.adjustmentAmount > 0 ? 'text-orange-700' : 'text-green-700'">
                  {{ apt.adjustmentAmount > 0 ? '补扣' : '退还' }} ¥{{ Math.abs(apt.adjustmentAmount) }}
                </span>
              </div>
              <p v-if="apt.adjustmentReason" class="text-xs text-slate-500 mt-1">调整说明：{{ apt.adjustmentReason }}</p>
            </div>

            <div class="flex gap-2 mt-4" v-if="apt.status === 'CONFIRMED'">
              <el-button size="small" type="success" @click="handleComplete(apt.id)">标记完成</el-button>
              <el-button size="small" type="danger" plain @click="handleCancel(apt.id)">取消预约</el-button>
            </div>
          </div>

          <div v-if="apt.status === 'COMPLETED'" class="border-t border-slate-100 bg-slate-50 p-5">
            <h4 class="font-bold text-slate-700 mb-3">执行结算</h4>
            <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4">
              <div>
                <label class="text-sm text-slate-500 mb-1 block">实际使用时长（小时）</label>
                <el-input-number v-model="settleForms[apt.id].actualHours" :min="0.5" :max="48" :step="0.5" :precision="1" style="width:100%" />
              </div>
              <div class="md:col-span-2">
                <label class="text-sm text-slate-500 mb-1 block">调整说明（必填，多扣或少扣的原因）</label>
                <el-input v-model="settleForms[apt.id].adjustmentReason" placeholder="如：样品处理延误导致超时30分钟" />
              </div>
            </div>
            <el-button type="primary" @click="handleSettle(apt)" :loading="settlingId === apt.id">
              确认结算
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import AppLayout from '@/components/AppLayout.vue'
import api from '@/api'

const appointments = ref([])
const loading = ref(false)
const statusFilter = ref('')
const settlingId = ref(null)
const settleForms = reactive({})

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

function initSettleForm(apt) {
  if (!settleForms[apt.id]) {
    settleForms[apt.id] = {
      actualHours: apt.bookedHours,
      adjustmentReason: ''
    }
  }
}

async function loadAppointments() {
  loading.value = true
  try {
    if (statusFilter.value) {
      appointments.value = await api.get(`/appointments/status/${statusFilter.value}`)
    } else {
      appointments.value = await api.get('/appointments')
    }
    appointments.value.forEach(apt => {
      if (apt.status === 'COMPLETED') {
        initSettleForm(apt)
      }
    })
  } finally {
    loading.value = false
  }
}

async function filterByStatus() {
  await loadAppointments()
}

async function handleComplete(id) {
  try {
    await ElMessageBox.confirm('确认将此预约标记为已完成？', '标记完成', { type: 'info' })
    await api.put(`/appointments/${id}/complete`)
    ElMessage.success('已标记完成')
    await loadAppointments()
  } catch {
    // cancelled
  }
}

async function handleCancel(id) {
  try {
    await ElMessageBox.confirm('确定要取消此预约吗？预扣费用将退还。', '取消预约', { type: 'warning' })
    await api.put(`/appointments/${id}/cancel`)
    ElMessage.success('预约已取消')
    await loadAppointments()
  } catch {
    // cancelled
  }
}

async function handleSettle(apt) {
  const form = settleForms[apt.id]
  if (!form.actualHours || form.actualHours <= 0) {
    ElMessage.warning('请输入实际使用时长')
    return
  }
  if (!form.adjustmentReason || !form.adjustmentReason.trim()) {
    ElMessage.warning('请填写调整说明，说明费用差异原因')
    return
  }
  settlingId.value = apt.id
  try {
    await api.post('/appointments/settle', {
      appointmentId: apt.id,
      actualHours: form.actualHours,
      adjustmentReason: form.adjustmentReason
    })
    ElMessage.success('结算完成')
    await loadAppointments()
  } finally {
    settlingId.value = null
  }
}

onMounted(() => {
  loadAppointments()
})
</script>
