import axios from 'axios'
import { ElMessage } from 'element-plus'

const api = axios.create({
    baseURL: '/api',
    timeout: 10000
})

api.interceptors.response.use(
    response => response.data,
    error => {
        if (error.response) {
            const msg = error.response.data?.message || error.response.data?.error || '请求失败'
            ElMessage.error(msg)
        } else {
            ElMessage.error('网络错误或服务器不可达')
        }
        return Promise.reject(error)
    }
)

export default api
