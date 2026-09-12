<template>
    <el-dialog v-model="showDialog" :title="elDialogTitle" width="500px" :destroy-on-close="true">
        <el-form :model="formData" label-width="120px" ref="formRef" :rules="formRules" class="page-form" v-loading="loading">
            <div v-if="formData.site_id == 0">
                <el-form-item :label="t('siteName')" prop="site_name">
                    <el-input v-model.trim="formData.site_name" clearable :placeholder="t('siteNamePlaceholder')" maxlength="20" class="input-width" show-word-limit />
                </el-form-item>
				<el-form-item :label="t('categoryName')" prop="category_id">
                    <el-select v-model="formData.category_id" clearable :placeholder="t('categoryNamePlaceholder')" class="input-width" @change="handleCategoryChange">
                        <el-option :label="item['name']" :value="item['category_id']" v-for="(item, index) in categoryList" :key="index" />
                    </el-select>
                </el-form-item>
                <el-form-item :label="t('childCategoryName')" prop="child_category_id" v-if="formData.child_category_id">
				    <el-select v-model="formData.child_category_id" clearable :placeholder="t('childCategoryNamePlaceholder')" class="input-width">
				        <el-option :label="item['name']" :value="item['category_id']" v-for="(item, index) in childCategoryList" :key="index" />
				    </el-select>
				</el-form-item>
                <el-form-item :label="t('circle')" prop="circle_id" v-if="user_store.checkAddon('circle')">
                    <el-cascader v-model="formData.circle_id" :options="circleOptions" :props="{ checkStrictly: true, value: 'id', label: 'name', children: 'children', maxDepth: 3 }" clearable class="input-width" :placeholder="t('selectCirclePlaceholder')" />
                </el-form-item>
                <el-form-item :label="t('contactMobile')" prop="phone">
                    <div>
                        <el-input v-model.trim="formData.phone" clearable :placeholder="t('contactMobilePlaceholder')" class="input-width" show-word-limit />
                        <p class="text-[12px] text-[#a9a9a9]">{{ t('contactMobileDesc') }}</p>
                    </div>
                </el-form-item>
                <el-form-item :label="t('businessLicense')" prop="business_license">
                    <upload-image v-model="formData.business_license" />
                </el-form-item>
                <el-form-item :label="t('manager')" prop="uid">
                    <el-select v-model="formData.uid" :placeholder="t('managerPlaceholder')" class="input-width" filterable>
                        <el-option :label="t('newAddManager')" :value="0">
                            <template #default>
                                <div class="flex items-center">
                                    <el-icon class="mr-[10px]">
                                        <Plus />
                                    </el-icon>
                                    {{ t('newAddManager') }}
                                </div>
                            </template>
                        </el-option>
                        <el-option v-for="item in siteUser" :key="item.uid" :label="item.username" :value="item.uid">
                            <div class="flex items-center">
                                <el-avatar :src="img(item.head_img)" size="small" class="mr-[10px]" v-if="item.head_img" />
                                <img src="@/app/assets/images/member_head.png" alt="" class="mr-[10px] w-[24px]" v-else>
                                {{ item.username }}
                            </div>
                        </el-option>
                    </el-select>
                </el-form-item>

                <div v-show="formData.uid === 0">
                    <el-form-item :label="t('username')" prop="username">
                        <el-input v-model.trim="formData.username" clearable :placeholder="t('usernamePlaceholder')" class="input-width" maxlength="20" :readonly="user_name_input" @click="user_name_input = false" @blur="user_name_input = true"/>
                    </el-form-item>

                    <el-form-item :label="t('password')" prop="password">
                        <el-input v-model.trim="formData.password" clearable :placeholder="t('passwordPlaceholder')" class="input-width" :show-password="true" type="password" :readonly="password_input" @click="password_input = false" @blur="password_input = true" />
                    </el-form-item>

                    <el-form-item :label="t('confirmPassword')" prop="confirm_password">
                        <el-input v-model.trim="formData.confirm_password" :placeholder="t('confirmPasswordPlaceholder')" type="password" :show-password="true" clearable class="input-width" :readonly="confirm_password_input" @click="confirm_password_input = false" @blur="confirm_password_input = true" />
                    </el-form-item>
                </div>
            </div>
            <div v-else>
                <el-form-item :label="t('siteName')">
                    {{ formData.site_name }}
                </el-form-item>
            </div>
            <el-form-item :label="t('isSelf')" prop="is_self">
                <el-radio-group v-model="formData.is_self" class="ml-4">
                    <el-radio :label="1">{{ t('selfShop') }}</el-radio>
                    <el-radio :label="0">{{ t('notSelfShop') }}</el-radio>
                </el-radio-group>
            </el-form-item>
<!--            <el-form-item :label="t('expireTime')" prop="expire_time" class="input-width">-->
<!--                <el-date-picker class="flex-1 !flex" v-model="formData.expire_time" clearable type="datetime" :placeholder="t('expireTimePlaceholder')" />-->
<!--            </el-form-item>-->
        </el-form>

        <template #footer>
            <span class="dialog-footer">
                <el-button @click="showDialog = false">{{ t('cancel') }}</el-button>
                <el-button type="primary" :loading="loading" @click="confirm(formRef)">{{ t('confirm') }}</el-button>
            </span>
        </template>
    </el-dialog>
</template>

<script lang="ts" setup>
import { ref, reactive, computed } from 'vue'
import { t } from '@/lang'
import type { FormInstance } from 'element-plus'
import { addShop, editShop, getShopInfo } from '@/app/lang/zh-cn/api/admin/site_shop'
import { getSiteCategoryAll } from '@/app/api/site_category'
import { getCircleList } from '@/app/api/site'
import { getAllUserList } from '@/app/api/user'
import { img } from '@/utils/common'
import userStore from '@/stores/modules/user'

const user_store = userStore()
const showDialog = ref(false)
const loading = ref(true)
const categoryList = ref([])
const childCategoryList = ref([])
const circleOptions = ref<any[]>([])
const siteUser = ref([])
const siteType = ref([])
const domain = location.hostname
getAllUserList({}).then(({ data }) => {
    siteUser.value = data
}).catch()

const end = new Date()

end.setTime(end.getTime() + 3600 * 1000 * 2 * 360)
/**
 * 表单数据
 */
const initialFormData = {
    site_id: '',
    site_name: '',
    real_name: '',
    uid: '',
    username: '',
    password: '',
    confirm_password: '',
    phone: '',
    is_self: 0,
    category_id: '',
    child_category_id: '',
    circle_id: '',
    business_license: '',
}
const formData: Record<string, any> = reactive({ ...initialFormData })

const formRef = ref<FormInstance>()

const user_name_input = ref(true)
const password_input = ref(true)
const confirm_password_input = ref(true)

const setCategoryList = async () => {
    categoryList.value = await (await getSiteCategoryAll({
        addon: 'mall'
    })).data
}
setCategoryList()

const setFormData = async (row: any = null) => {
    loading.value = true
    Object.assign(formData, initialFormData)
    if (row) {
        const data = await (await getShopInfo(row.site_id)).data
        siteType.value = data
        Object.keys(formData).forEach((key: string) => {
            if (data[key] != undefined) formData[key] = data[key]
        })
    }
    loading.value = false
}

// 表单验证规则
const formRules = computed(() => {
    return {
        site_name: [
            { required: true, message: t('siteNamePlaceholder'), trigger: 'blur' }
        ],
        category_id: [
            { required: true, message: t('categoryNamePlaceholder'), trigger: 'blur' }
        ],
        phone: [
            { required: true, message: t('contactMobilePlaceholder'), trigger: 'blur' },
            {
                validator: (rule: any, value: string, callback: any) => {
                    const phonePattern = /^(?:(?:\+|00)86)?1[3-9]\d{9}$|^(?:(?:\d{3}-)?\d{8}|^(?:\d{4}-)?\d{7,8})(?:-\d+)?$|^[48]00-?\d{3}-?\d{4}$|^(?:\+?\d{1,3}[-\s.]?)?(?:\(?\d{2,4}\)?[-\s.]?)?\d{2,4}[-\s.]?\d{3,4}[-\s.]?\d{3,4}$/
                    if (!value) {
                        return callback(new Error(t('contactMobilePlaceholder')))
                    } else if (!phonePattern.test(value)) {
                        return callback(new Error(t('contactMobileTips')))
                    } else {
                        return callback()
                    }
                },
                trigger: 'blur'
            }
        ],
        uid: [
            {
                required: true,
                validator: (rule: any, value: string, callback: any) => {
                    if (formData.uid === '') callback(new Error(t('managerPlaceholder')))
                    else callback()
                },
                trigger: 'blur'
            }
        ],
        username: [
            { required: formData.uid == 0, message: t('usernamePlaceholder'), trigger: 'blur' }
        ],
        password: [
            { required: formData.uid == 0, message: t('passwordPlaceholder'), trigger: 'blur' }
        ],
        real_name: [
            { required: true, message: t('userRealNamePlaceholder'), trigger: 'blur' }
        ],
        confirm_password: [
            { required: formData.uid == 0, message: t('confirmPasswordPlaceholder'), trigger: 'blur' },
            {
                validator: (rule: any, value: string, callback: any) => {
                    if (formData.uid == 0 && value != formData.password) callback(new Error(t('confirmPasswordError')))
                    else callback()
                },
                trigger: 'blur'
            }
        ]
        // expire_time: [
        //     { required: true, message: t('expireTimePlaceholder'), trigger: 'blur' }
        // ]
    }
})

const emit = defineEmits(['complete'])

// 处理分类变更事件
const handleCategoryChange = (category_id: any) => {
    // 获取当前选中的父级分类对象
    const selectedCategory = categoryList.value.find(item => item.category_id === category_id)
    
    // 重置子分类列表
    childCategoryList.value = []
    formData.child_category_id = ''
    
    // 检查是否有子分类
    if (selectedCategory && selectedCategory.child_list && Array.isArray(selectedCategory.child_list) && selectedCategory.child_list.length > 0) {
        // 赋值子分类列表
        childCategoryList.value = selectedCategory.child_list
        // 自动选中第一个子分类
        formData.child_category_id = selectedCategory.child_list[0].category_id
    }
}

const loadCircleList = () => {
    getCircleList({}).then((res: any) => {
        const data = res.data
        if (data && data.length) {
            circleOptions.value = data
        }
    })
}
loadCircleList()

// 弹窗头部
const elDialogTitle = computed(() => {
    // const title = ref<string>('')
    if (loading.value) return ''
    return formData.site_id ? t('editSite') : t('addSite')
})

/**
 * 确认
 * @param formEl
 */
const confirm = async (formEl: FormInstance | undefined) => {
    if (loading.value || !formEl) return

    await formEl.validate(async (valid) => {
        if (valid) {
            loading.value = true
            
            const data = { ...formData }
            // 处理circle_id，只传最后一级id
            if (Array.isArray(data.circle_id) && data.circle_id.length > 0) {
                data.circle_id = data.circle_id[data.circle_id.length - 1]
            }
            const save = data.site_id ? editShop : addShop
            save(data).then(res => {
                loading.value = false
                showDialog.value = false
                emit('complete')
            }).catch(() => {
                loading.value = false
                // showDialog.value = false
            })
        }
    })
}

// 禁止点击input输入框时浏览器显示默认的账号信息
window.onload = function () {
    var inputs = document.getElementsByTagName('input')
    for (var i = 0; i < inputs.length; i++) {
        inputs[i].setAttribute('autocomplete', 'off')
    }
}

defineExpose({
    showDialog,
    setFormData,
    loading
})
</script>

<style lang="scss" scoped></style>
