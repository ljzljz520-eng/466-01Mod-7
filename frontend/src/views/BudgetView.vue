<template>
  <AppLayout>
    <div class="max-w-4xl mx-auto space-y-6">
      <h2 class="text-2xl font-bold text-slate-900">项目额度</h2>

      <div v-if="account" class="bg-white rounded-xl shadow-sm border border-slate-200 p-6">
        <div class="flex items-center justify-between mb-6">
          <div>
            <h3 class="text-lg font-bold text-slate-800">{{ account.groupName }}</h3>
            <p class="text-sm text-slate-400">负责人：{{ account.piName }}</p>
          </div>
          <div class="text-right">
            <p class="text-sm text-slate-400">账户余额</p>
            <p class="text-3xl font-bold" :class="account.balance > 5000 ? 'text-green-600' : account.balance > 0 ? 'text-orange-500' : 'text-red-600'">
              ¥{{ account.balance?.toLocaleString() }}
            </p>
          </div>
        </div>

        <div class="h-2 bg-slate-100 rounded-full overflow-hidden mb-6">
          <div class="h-full rounded-full transition-all duration-500"
               :class="account.balance > 5000 ? 'bg-green-500' : account.balance > 0 ? 'bg-orange-400' : 'bg-red-500'"
               :style="{ width: Math.min(100, (account.balance / 80000) * 100) + '%' }">
          </div>
        </div>

        <div class="flex gap-3 mb-2">
          <el-select v-model="selectedGroupId" placeholder="切换课题组" style="width:220px" @change="onGroupChange">
            <el-option
              v-for="acc in accounts"
              :key="acc.id"
              :label="acc.groupName"
              :value="acc.id"
            />
          </el-select>
        </div>
      </div>

      <div class="bg-white rounded-xl shadow-sm border border-slate-200">
        <div class="p-5 border-b border-slate-100">
          <h3 class="font-bold text-slate-800">扣费流水</h3>
        </div>
        <div v-if="billingRecords.length === 0 && !loadingRecords" class="p-8 text-center text-slate-400">
          暂无扣费记录
        </div>
        <el-table v-else :data="billingRecords" v-loading="loadingRecords" style="width:100%"
                  :header-cell-style="{ background:'#f8fafc', color:'#64748b' }">
          <el-table-column prop="createdAt" label="时间" width="180">
            <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
          </el-table-column>
          <el-table-column prop="type" label="类型" width="100">
            <template #default="{ row }">
              <el-tag size="small" :type="getBillingTypeTag(row.type)">{{ getBillingTypeLabel(row.type) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="amount" label="金额" width="120">
            <template #default="{ row }">
              <span :class="row.amount >= 0 ? 'text-green-600' : 'text-red-600'" class="font-semibold">
                {{ row.amount >= 0 ? '+' : '' }}¥{{ row.amount }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="description" label="说明" />
        </el-table>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import AppLayout from '@/components/AppLayout.vue'
import api from '@/api'

const account = ref(null)
const accounts = ref([])
const billingRecords = ref([])
const selectedGroupId = ref(null)
const loadingRecords = ref(false)

function getBillingTypeLabel(type) {
  const map = { PREPAY: '预扣', SUPPLEMENT: '补扣', REFUND: '退还' }
  return map[type] || type
}

function getBillingTypeTag(type) {
  const map = { PREPAY: 'danger', SUPPLEMENT: 'warning', REFUND: 'success' }
  return map[type] || 'info'
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return d.toLocaleString('zh-CN')
}

async function onGroupChange(accountId) {
  loadingRecords.value = true
  try {
    account.value = await api.get(`/accounts/${accountId}`)
    billingRecords.value = await api.get(`/accounts/${accountId}/billing-records`)
  } finally {
    loadingRecords.value = false
  }
}

onMounted(async () => {
  accounts.value = await api.get('/accounts')
  if (accounts.value.length > 0) {
    selectedGroupId.value = accounts.value[0].id
    await onGroupChange(selectedGroupId.value)
  }
})
</script>
