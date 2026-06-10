import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '@/api'

export const useInstrumentStore = defineStore('instrument', () => {
    const instruments = ref([])
    const loading = ref(false)

    async function fetchInstruments() {
        loading.value = true
        try {
            instruments.value = await api.get('/instruments')
        } finally {
            loading.value = false
        }
    }

    async function fetchAvailableInstruments() {
        loading.value = true
        try {
            instruments.value = await api.get('/instruments/available')
        } finally {
            loading.value = false
        }
    }

    return { instruments, loading, fetchInstruments, fetchAvailableInstruments }
})

export const useAccountStore = defineStore('account', () => {
    const accounts = ref([])
    const currentAccount = ref(null)
    const billingRecords = ref([])
    const loading = ref(false)

    async function fetchAccounts() {
        loading.value = true
        try {
            accounts.value = await api.get('/accounts')
        } finally {
            loading.value = false
        }
    }

    async function fetchAccountById(id) {
        loading.value = true
        try {
            currentAccount.value = await api.get(`/accounts/${id}`)
        } finally {
            loading.value = false
        }
    }

    async function fetchAccountByGroupName(groupName) {
        loading.value = true
        try {
            currentAccount.value = await api.get(`/accounts/group/${groupName}`)
        } finally {
            loading.value = false
        }
    }

    async function fetchBillingRecords(accountId) {
        loading.value = true
        try {
            billingRecords.value = await api.get(`/accounts/${accountId}/billing-records`)
        } finally {
            loading.value = false
        }
    }

    return { accounts, currentAccount, billingRecords, loading, fetchAccounts, fetchAccountById, fetchAccountByGroupName, fetchBillingRecords }
})

export const useAppointmentStore = defineStore('appointment', () => {
    const appointments = ref([])
    const currentAppointment = ref(null)
    const costEstimate = ref(null)
    const loading = ref(false)

    async function estimateCost(request) {
        loading.value = true
        try {
            costEstimate.value = await api.post('/appointments/estimate', request)
        } finally {
            loading.value = false
        }
    }

    async function createAppointment(request) {
        loading.value = true
        try {
            currentAppointment.value = await api.post('/appointments', request)
            return currentAppointment.value
        } finally {
            loading.value = false
        }
    }

    async function settleAppointment(request) {
        loading.value = true
        try {
            return await api.post('/appointments/settle', request)
        } finally {
            loading.value = false
        }
    }

    async function completeAppointment(id) {
        loading.value = true
        try {
            return await api.put(`/appointments/${id}/complete`)
        } finally {
            loading.value = false
        }
    }

    async function cancelAppointment(id) {
        loading.value = true
        try {
            return await api.put(`/appointments/${id}/cancel`)
        } finally {
            loading.value = false
        }
    }

    async function fetchAppointmentsByUser(userName) {
        loading.value = true
        try {
            appointments.value = await api.get(`/appointments/user/${userName}`)
        } finally {
            loading.value = false
        }
    }

    async function fetchAppointmentsByGroup(piAccountId) {
        loading.value = true
        try {
            appointments.value = await api.get(`/appointments/group/${piAccountId}`)
        } finally {
            loading.value = false
        }
    }

    async function fetchAppointmentsByStatus(status) {
        loading.value = true
        try {
            appointments.value = await api.get(`/appointments/status/${status}`)
        } finally {
            loading.value = false
        }
    }

    async function fetchAllAppointments() {
        loading.value = true
        try {
            appointments.value = await api.get('/appointments')
        } finally {
            loading.value = false
        }
    }

    return {
        appointments, currentAppointment, costEstimate, loading,
        estimateCost, createAppointment, settleAppointment,
        completeAppointment, cancelAppointment,
        fetchAppointmentsByUser, fetchAppointmentsByGroup,
        fetchAppointmentsByStatus, fetchAllAppointments
    }
})
