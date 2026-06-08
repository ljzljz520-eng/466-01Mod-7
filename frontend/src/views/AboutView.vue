<template>
  <AppLayout>
    <div class="max-w-3xl mx-auto bg-white p-8 rounded-xl shadow-sm border border-slate-200">
      <h1 class="text-3xl font-bold text-slate-900 mb-6">
        关于我们 <span class="text-blue-600">Sky-Lark</span>
      </h1>
      <div class="prose prose-slate max-w-none">
        <p class="text-lg leading-relaxed text-slate-600">
          <span class="text-blue-600 font-semibold">Sky-Lark</span> 是一个专为速度和可靠性设计的现代化、企业级全栈开发模板。
          它结合了 <strong>Spring Boot</strong> 的稳健性与 <strong>Vue 3</strong> 的响应式能力。
        </p>
        
        <h3 class="mt-8 text-xl font-bold text-slate-800">核心特性</h3>
        <ul class="list-disc pl-5 space-y-2 mt-4 text-slate-600">
          <li><strong>后端:</strong> Java 17, Spring Boot 3.2, Spring Data JPA</li>
          <li><strong>前端:</strong> Vue 3, Vite, Pinia, Vue Router</li>
          <li><strong>UI/UX:</strong> Element Plus + Tailwind CSS</li>
          <li><strong>数据库:</strong> MySQL 8.0 (含自动填充)</li>
          <li><strong>DevOps:</strong> 完整 Docker Compose 编排</li>
        </ul>

        <div class="mt-8 p-4 bg-blue-50 rounded-lg border border-blue-100 text-blue-800">
          <strong>提示:</strong> 请查看 <code>README.md</code> 获取部署说明。
        </div>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import AppLayout from '@/components/AppLayout.vue'
</script>
