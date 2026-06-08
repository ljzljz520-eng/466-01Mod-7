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
            ElMessage.error(error.response.data.message || '请求失败')
        } else {
            ElMessage.error('网络错误或服务器不可达')
        }
        return Promise.reject(error)
    }
)

export default api
