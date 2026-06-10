<template>
  <AppLayout>
    <div class="max-w-4xl mx-auto space-y-6">
      <div class="flex items-center justify-between">
        <h2 class="text-2xl font-bold text-slate-900">仪器预约</h2>
        <el-tag type="info" effect="plain">当前课题组：{{ currentGroupName }}</el-tag>
      </div>

      <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-6 space-y-5">
        <el-form :model="form" label-width="100px" label-position="top" size="large">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <el-form-item label="选择仪器" required>
              <el-select v-model="form.instrumentId" placeholder="请选择仪器" filterable @change="onInstrumentChange" style="width:100%">
                <el-option
                  v-for="inst in availableInstruments"
                  :key="inst.id"
                  :label="inst.name"
                  :value="inst.id"
                >
                  <div class="flex justify-between items-center w-full">
                    <span>{{ inst.name }}</span>
                    <el-tag size="small" :type="getTypeTag(inst.type)">{{ getTypeLabel(inst.type) }}</el-tag>
                  </div>
                </el-option>
              </el-select>
            </el-form-item>

            <el-form-item label="预约时长（小时）" required>
              <el-input-number v-model="form.bookedHours" :min="0.5" :max="24" :step="0.5" :precision="1" style="width:100%" @change="onFormChange" />
            </el-form-item>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <el-form-item label="使用耗材">
              <el-switch v-model="form.useConsumables" active-text="需要" inactive-text="不需要" @change="onFormChange" />
            </el-form-item>

            <el-form-item label="加急服务">
              <el-switch v-model="form.isUrgent" active-text="加急" inactive-text="普通" @change="onFormChange" />
            </el-form-item>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <el-form-item label="预约人姓名" required>
              <el-input v-model="form.userName" placeholder="请输入姓名" />
            </el-form-item>

            <el-form-item label="所属课题组">
              <el-select v-model="form.piAccountId" placeholder="选择课题组" style="width:100%" @change="onFormChange">
                <el-option
                  v-for="acc in accounts"
                  :key="acc.id"
                  :label="acc.groupName"
                  :value="acc.id"
                >
                  <div class="flex justify-between items-center w-full">
                    <span>{{ acc.groupName }}（{{ acc.piName }}）</span>
                    <span class="text-xs text-slate-400">余额 ¥{{ acc.balance }}</span>
                  </div>
                </el-option>
              </el-select>
            </el-form-item>
          </div>
        </el-form>
      </div>

      <div v-if="costEstimate" class="bg-white rounded-xl shadow-sm border border-slate-200 p-6">
        <h3 class="text-lg font-bold text-slate-800 mb-4">费用预估</h3>
        <div class="grid grid-cols-2 md:grid-cols-4 gap-4 mb-4">
          <div class="bg-blue-50 rounded-lg p-4 text-center">
            <p class="text-xs text-blue-500 mb-1">时长费用</p>
            <p class="text-lg font-bold text-blue-700">¥{{ costEstimate.timeCost }}</p>
          </div>
          <div class="bg-green-50 rounded-lg p-4 text-center">
            <p class="text-xs text-green-500 mb-1">耗材费用</p>
            <p class="text-lg font-bold text-green-700">¥{{ costEstimate.consumableCost }}</p>
          </div>
          <div class="bg-orange-50 rounded-lg p-4 text-center">
            <p class="text-xs text-orange-500 mb-1">加急附加</p>
            <p class="text-lg font-bold text-orange-700">¥{{ costEstimate.urgentSurcharge }}</p>
          </div>
          <div class="bg-indigo-50 rounded-lg p-4 text-center">
            <p class="text-xs text-indigo-500 mb-1">预估总计</p>
            <p class="text-lg font-bold text-indigo-700">¥{{ costEstimate.totalEstimatedCost }}</p>
          </div>
        </div>

        <div class="flex items-center justify-between p-4 rounded-lg"
             :class="costEstimate.balanceSufficient ? 'bg-green-50 border border-green-200' : 'bg-red-50 border border-red-200'">
          <div>
            <span class="text-sm text-slate-600">课题组当前余额：</span>
            <span class="font-bold" :class="costEstimate.balanceSufficient ? 'text-green-600' : 'text-red-600'">
              ¥{{ costEstimate.currentBalance }}
            </span>
          </div>
          <el-tag :type="costEstimate.balanceSufficient ? 'success' : 'danger'" effect="dark">
            {{ costEstimate.balanceSufficient ? '余额充足' : '余额不足' }}
          </el-tag>
        </div>
      </div>

      <div class="flex justify-end gap-4">
        <el-button @click="calcEstimate" :loading="estimating" :disabled="!canEstimate" type="primary" plain>
          计算费用
        </el-button>
        <el-button @click="submitAppointment" :loading="submitting" :disabled="!canSubmit" type="primary">
          提交预约
        </el-button>
      </div>

      <div v-if="selectedInstrument" class="bg-slate-50 rounded-xl p-5 border border-slate-200">
        <h4 class="font-bold text-slate-700 mb-2">{{ selectedInstrument.name }}</h4>
        <p class="text-sm text-slate-500 mb-3">{{ selectedInstrument.description }}</p>
        <div class="flex gap-4 text-xs text-slate-400">
          <span>标准费率：¥{{ selectedInstrument.ratePerHour }}/小时</span>
          <span>耗材费：¥{{ selectedInstrument.consumableFee }}</span>
          <span>加急费率：{{ (selectedInstrument.urgentSurchargeRate * 100).toFixed(0) }}%</span>
        </div>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import AppLayout from '@/components/AppLayout.vue'
import { useInstrumentStore } from '@/stores/app'
import { useAccountStore } from '@/stores/app'
import { useAppointmentStore } from '@/stores/app'
import api from '@/api'

const instrumentStore = useInstrumentStore()
const accountStore = useAccountStore()
const appointmentStore = useAppointmentStore()

const form = ref({
  instrumentId: null,
  piAccountId: null,
  userName: '',
  userRole: 'STUDENT',
  bookedHours: 1.0,
  useConsumables: false,
  isUrgent: false
})

const estimating = ref(false)
const submitting = ref(false)
const costEstimate = ref(null)
const currentGroupName = ref('')

const availableInstruments = computed(() => instrumentStore.instruments)
const accounts = computed(() => accountStore.accounts)
const selectedInstrument = computed(() => {
  if (!form.value.instrumentId) return null
  return availableInstruments.value.find(i => i.id === form.value.instrumentId)
})

const canEstimate = computed(() => form.value.instrumentId && form.value.bookedHours > 0 && form.value.piAccountId)
const canSubmit = computed(() => canEstimate.value && form.value.userName && costEstimate.value?.balanceSufficient)

function getTypeLabel(type) {
  const map = { NMR: '核磁', SEQUENCER: '测序', EM: '电镜' }
  return map[type] || type
}

function getTypeTag(type) {
  const map = { NMR: 'primary', SEQUENCER: 'success', EM: 'warning' }
  return map[type] || 'info'
}

function onInstrumentChange() {
  costEstimate.value = null
}

function onFormChange() {
  costEstimate.value = null
}

async function calcEstimate() {
  estimating.value = true
  try {
    costEstimate.value = await api.post('/appointments/estimate', form.value)
    if (!costEstimate.value.balanceSufficient) {
      ElMessage.warning('课题组余额不足，无法提交预约')
    }
  } finally {
    estimating.value = false
  }
}

async function submitAppointment() {
  submitting.value = true
  try {
    await api.post('/appointments', form.value)
    ElMessage.success('预约提交成功！')
    costEstimate.value = null
    form.value.instrumentId = null
    form.value.bookedHours = 1.0
    form.value.useConsumables = false
    form.value.isUrgent = false
    await accountStore.fetchAccounts()
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  await Promise.all([
    instrumentStore.fetchAvailableInstruments(),
    accountStore.fetchAccounts()
  ])
})
</script>
