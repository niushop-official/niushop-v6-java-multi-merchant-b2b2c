<template>
	<div class="main-container">
		<el-card class="box-card !border-none" shadow="never">
			<div class="flex justify-between items-center">
				<span class="text-page-title">{{ pageName }}</span>
			</div>
			<div v-loading="initLoading"> <!-- 区分初始化加载状态 -->
				<!-- 基础信息 -->
				<el-form :model="formData" label-width="150px" ref="formRef" :rules="formRules" class="page-form">
					<el-card class="box-card !border-none" shadow="never">
						<el-form-item label="接收人" prop="uid">
							<el-select v-model="formData.uid" filterable reserve-keyword placeholder="请输入关键词搜索"
								:loading="searchLoading" style="width: 240px">
								<el-option v-for="item in filteredUserList" :key="item.uid" :label="item.username" :value="item.uid">
								    <div class="flex items-center">
								        <el-avatar :src="img(item.head_img)" size="small" class="mr-[10px]" v-if="item.head_img" />
								        <img src="@/app/assets/images/member_head.png" alt="" class="mr-[10px] w-[24px]" v-else>
								        {{ item.username }}
								    </div>
								</el-option>
							</el-select>
						</el-form-item>
					</el-card>
				</el-form>
			</div>
		</el-card>
		<div class="fixed-footer-wrap">
			<div class="fixed-footer">
				<el-button type="primary" :loading="submitLoading" @click="save(formRef)">
					{{ t('save') }}
				</el-button>
			</div>
		</div>
	</div>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted, computed } from 'vue'
import { t } from '@/lang'
import { FormInstance, FormRules } from 'element-plus'
import { useRoute } from 'vue-router'
import { getAllSiteUserList } from '@/app/api/user'
import { img } from '@/utils/common'
import { useSystemStore } from '@/stores/modules/system'
import { useUserStore } from '@/stores/modules/user'
import { setNoticeRecipient, getNoticeRecipient } from '@/app/api/notice'
// 路由与页面信息
const route = useRoute()
const pageName = route.meta.title as string

// 状态管理（分离不同场景的loading，避免冲突）
const initLoading = ref(true) // 初始化用户列表的加载状态
const searchLoading = ref(false) // 搜索时的加载状态
const submitLoading = ref(false) // 提交按钮的加载状态

// 表单数据
const formData = reactive<{ uid : string | number }>({
    uid: '', // 单选，值为用户uid
})

// 表单引用与验证规则
const formRef = ref<FormInstance>()
const formRules = reactive<FormRules>({
    uid: [
        { required: true, message: t('pleaseSelectRecipient'), trigger: 'blur' } // 修正提示信息
    ]
})

// 用户列表数据（直接使用原始列表，无需冗余变量）
const allUserList = ref<{ uid : string | number; username : string }[]>([])

// 过滤后的用户列表（用于搜索展示）
const filteredUserList = computed(() => {
    if (!searchQuery.value) return allUserList.value // 无搜索时展示全部
    // 本地过滤：匹配用户名（不区分大小写）
    return allUserList.value.filter(item =>
        item.username.toLowerCase().includes(searchQuery.value.toLowerCase())
    )
})

// 搜索关键词
const searchQuery = ref('')

// 初始化：获取所有用户列表
const getInitUserList = async () => {
    try {
        initLoading.value = true
        const res = await getAllSiteUserList()
        allUserList.value = res.data || [] // 确保数据格式正确
    } catch (err) {
        console.error('获取用户列表失败：', err)
        allUserList.value = []
    } finally {
        initLoading.value = false
    }
}
const getConfig = () => {
    getNoticeRecipient().then((res) => {
        formData.uid = res.data.uid
    })
}
getConfig()
// 搜索方法（本地过滤，无需远程接口）
const handleSearch = (query : string) => {
    searchQuery.value = query
    // 模拟搜索延迟（可选，增强体验）
    if (query) {
        searchLoading.value = true
        setTimeout(() => {
            searchLoading.value = false
        }, 100)
    }
}

/**
	 * 保存
	 */
const save = async (formEl : FormInstance | undefined) => {
    if (submitLoading.value || !formEl) return
    await formEl.validate(async (valid) => {
        if (valid) {
            submitLoading.value = true
            setNoticeRecipient({ uid: formData.uid }).then(() => {
                submitLoading.value = false
            }).catch(() => {
                submitLoading.value = false
            })
        }
    })
}

// 初始化加载用户列表
onMounted(() => {
    getInitUserList()
})
</script>