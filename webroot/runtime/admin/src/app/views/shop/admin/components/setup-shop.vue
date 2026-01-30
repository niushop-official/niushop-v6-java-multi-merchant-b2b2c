<template>
    <el-dialog v-model="showDialog" :title="t('setupShop')" width="500px" :destroy-on-close="true">
        <el-form :model="formData" label-width="120px" ref="formRef" :rules="formRules" class="page-form" v-loading="loading">
            <div v-if="formData.site_id == 0">
                <el-form-item :label="t('siteName')" prop="site_name">
                    <el-input v-model="formData.site_name" clearable :placeholder="t('siteNamePlaceholder')" class="input-width" maxlength="20" />
                </el-form-item>

                <el-form-item :label="t('groupId')" prop="group_id">
                    <el-select v-model="formData.group_id" clearable :placeholder="t('groupIdPlaceholder')" class="input-width">
                        <el-option :label="item['group_name']" :value="item['group_id']" v-for="(item, index) in groupList" :key="index" />
                    </el-select>
                </el-form-item>
                <el-form-item :label="t('contactMobile')" prop="phone">
                    <el-input v-model.trim="formData.phone" clearable :placeholder="t('contactMobilePlaceholder')" class="input-width" maxlength="30" />
                </el-form-item>
                <el-form-item :label="t('categoryName')" prop="category_id">
                    <el-select v-model="formData.category_id" clearable :placeholder="t('categoryIdPlaceholder')" class="input-width">
                        <el-option :label="item['category_name']" :value="item['category_id']" v-for="(item, index) in categoryList" :key="index" />
                    </el-select>
                </el-form-item>
                <el-form-item :label="t('businessLicense')" prop="business_license">
                    <upload-image v-model="formData.business_license" :limit="10" />
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
                        <el-input v-model="formData.username" clearable :placeholder="t('usernamePlaceholder')" class="input-width" maxlength="20" :readonly="user_name_input" @click="user_name_input = false" @blur="user_name_input = true" />
                    </el-form-item>

                    <el-form-item :label="t('password')" prop="password">
                        <el-input v-model="formData.password" clearable :placeholder="t('passwordPlaceholder')" class="input-width" :show-password="true" type="password" :readonly="password_input" @click="password_input = false" @blur="password_input = true" />
                    </el-form-item>

                    <el-form-item :label="t('confirmPassword')" prop="confirm_password">
                        <el-input v-model="formData.confirm_password" :placeholder="t('confirmPasswordPlaceholder')" type="password" :show-password="true" clearable class="input-width" :readonly="confirm_password_input" @click="confirm_password_input = false" @blur="confirm_password_input = true" />
                    </el-form-item>
                </div>
            </div>
            <div v-else>
                <el-form-item :label="t('siteName')">
                    {{ formData.site_name }}
                </el-form-item>

                <el-form-item :label="t('groupId')" prop="group_id">
                    <el-select v-model="formData.group_id" clearable :placeholder="t('groupIdPlaceholder')" class="input-width">
                        <el-option :label="item['group_name']" :value="item['group_id']" v-for="(item, index) in groupList" :key="index" />
                    </el-select>
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
import { addShop, getShopGroupAll, getShopCategoryAll } from '@/app/api/shop_admin'
import { getAllUserList } from '@/app/api/user'
import { img } from '@/utils/common'

const showDialog = ref(false)
const loading = ref(false)
const groupList = ref([])
const categoryList = ref([])
const siteUser = ref([])
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
    // expire_time: end,
    group_id: '',
    phone: '',
    is_self: 0,
    category_id: '',
    business_license: ''
}

const setGroupList = async () => {
    groupList.value = await (await getShopGroupAll()).data
}
setGroupList()
const setCategoryList = async () => {
    categoryList.value = await (await getShopCategoryAll()).data
}
setCategoryList()

const formData: Record<string, any> = reactive({ ...initialFormData })

const formRef = ref<FormInstance>()

const user_name_input = ref(true)
const password_input = ref(true)
const confirm_password_input = ref(true)

// 表单验证规则
const formRules = computed(() => {
    return {
        site_name: [
            { required: true, message: t('siteNamePlaceholder'), trigger: 'blur' }
        ],
        group_id: [
            { required: true, message: t('groupIdPlaceholder'), trigger: 'blur' }
        ],
        category_id: [
            { required: true, message: t('categoryIdPlaceholder'), trigger: 'blur' }
        ],
        phone: [
            { required: true, message: t('contactMobilePlaceholder'), trigger: 'blur' },
            {
                validator: (rule: any, value: string, callback: any) => {
                    const phonePattern = /^1[3456789]\d{9}$/
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

/**
 * 确认
 * @param formEl
 */
const confirm = async (formEl: FormInstance | undefined) => {
    if (loading.value || !formEl) return

    await formEl.validate(async (valid) => {
        if (valid) {
            loading.value = true

            const data = formData
            addShop(data).then(res => {
                loading.value = false
                showDialog.value = false
                emit('complete')
            }).catch(() => {
                loading.value = false
            })
        }
    })
}

const setFormData = async (row: any = null) => {
    Object.assign(formData, row)
}

defineExpose({
    showDialog,
    setFormData,
    loading
})
</script>

<style lang="scss" scoped></style>
