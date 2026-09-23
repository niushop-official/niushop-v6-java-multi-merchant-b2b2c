<template>
    <div>
        <el-dropdown @command="clickEvent" :tabindex="1">
            <div class="userinfo flex h-full items-center">
                <el-avatar :size="25" :icon="UserFilled" :src="info && info.head_img ? img(info.head_img) : ''"/>
                <div class="user-name pl-[8px] text-[#fff]">{{ userStore.userInfo.username }}</div>
                <icon name="element ArrowDown" color="#fff" class="ml-[5px]" />
            </div>
            <template #dropdown>
                <el-dropdown-menu>
                    <el-dropdown-item @click="changeSite">
                        <div class="flex items-center leading-[1] py-[5px]">
                        <span class="iconfont iconqiehuan ml-[4px] !text-[14px] mr-[10px]"></span>
                        <span class="text-[14px]">切换店铺</span>
                        </div>
                    </el-dropdown-item>
                    <el-dropdown-item @click="getUserInfoFn">
                        <!-- <router-link to="/user/center"> -->
                        <div class="flex items-center leading-[1] py-[5px]">
                        <span class="iconfont iconshezhi1 ml-[4px] !text-[14px] mr-[10px]"></span>
                        <span class="text-[14px]">账号设置</span>
                        </div>
                        <!-- </router-link> -->
                    </el-dropdown-item>
                    <el-dropdown-item @click="changePasswordDialog=true">
                        <div class="flex items-center leading-[1] py-[5px]">
                        <span class="iconfont iconxiugai ml-[4px] !text-[14px] mr-[10px]"></span>
                        <span class="text-[14px]">修改密码</span>
                        </div>
                    </el-dropdown-item>
                    <el-dropdown-item command="logout">
                        <div class="flex items-center leading-[1] py-[2px]">
                        <span class="iconfont icontuichudenglu !text-[21px] mr-[8px]"></span>
                        <span class="text-[14px]">退出登录</span>
                        </div>
                    </el-dropdown-item>
                </el-dropdown-menu>
            </template>
        </el-dropdown>

        <el-dialog v-model="changePasswordDialog" width="450px" title="修改密码">
            <div>
                <el-form :model="saveInfo" label-width="90px" ref="formRef" :rules="formRules" class="page-form">
                    <el-form-item :label="t('originalPassword')" prop="original_password">
                        <el-input v-model="saveInfo.original_password" type="password" :placeholder="t('originalPasswordPlaceholder')" clearable class="input-width" />
                    </el-form-item>
                    <el-form-item :label="t('newPassword')" prop="password">
                        <el-input v-model="saveInfo.password" type="password" :placeholder="t('passwordPlaceholder')" clearable class="input-width" />
                        <div class="form-tip">{{t('passwordTip')}}</div>
                    </el-form-item>
                    <el-form-item :label="t('passwordCopy')" prop="password_copy">
                        <el-input v-model="saveInfo.password_copy" type="password" :placeholder="t('passwordPlaceholder')" clearable class="input-width" />
                    </el-form-item>
                </el-form>
            </div>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="changePasswordDialog = false">{{t('cancel')}}</el-button>
                    <el-button type="primary" @click="submitForm(formRef)">{{t('save')}}</el-button>
                </span>
            </template>
        </el-dialog>
        <el-dialog v-model="changeSiteDialog" width="1060px" title="切换店铺" align-center append-to-body>
            <el-form :inline="true" :model="site.searchParam" ref="searchFormRef">
                <el-form-item>
                    <div class="secrch-input w-[500px] h-[38px] flex items-center  border-solid border-[1px] border-[#ddd] rounded-[8px] box-border">
                        <el-select v-model="site.searchParam.search_type" class="!w-[112px] text-[12px] flex-shrink-0">
                            <el-option :label="t('店铺名称')" value="site_name"></el-option>
                            <el-option :label="t('店铺编号')" value="site_id"></el-option>
                        </el-select>
                        <span class="w-[1px] h-[24px] bg-[#eee]"></span>
                        <el-input v-model.trim="site.searchParam.search_value" :placeholder="t('请输入店铺名称')" class="w-[340px] !text-[12px]"  maxlength="60" />
                        <span class="text-[14px] font-[500] text-[#323233] flex-shrink-0 mr-[16px] cursor-pointer" @click="getHomeSiteFn()">查询</span>
                    </div>
                </el-form-item>
            </el-form>
            <div class="flex items-center mb-[20px]">
                <template v-for="(item, key) in addonList" :key="item.key">
                    <span class="text-[12px] text-[#888] cursor-pointer" :class="{'!text-[#222] font-[700]':site.searchParam.app == item.key }" @click="handleApp(item)">{{ item.title }}({{ item.site_num }})</span>
                    <span class="w-[1px] h-[13px] bg-[#ddd] mx-[14px]" v-if="key !== Object.keys(addonList).length - 1"></span>
                </template>
            </div>
            <div class="min-h-[385px]">
                <div v-loading="site.loading">
                    <div class="grid grid-cols-3 gap-[16px]">
                        <div v-for="(item, index) in site.tableData" :key="index" @click="selectSite(item)" :class="['w-[330px] p-[18px] box-border border-[1px] border-solid border-[#ddd] rounded-[4px] cursor-pointer flex relative overflow-hidden',{'!border-primary bg-[#f3f7ff]': siteInfo.site_id == item.site_id}]">
                            <view class="absolute top-[-1px] right-[-1px] w-[18px] h-[18px] bg-primary rounded-bl-[10px] rounded-tr-[4px] flex items-center justify-center" v-if="siteInfo.site_id == item.site_id">
                                <span class="nc-iconfont nc-icon-duihaoV6xx-1 text-[#fff] !text-[12px]"></span>
                            </view>
                            <div class="relative w-[70px] h-[70px]">
                                <img v-if="item.front_end_logo" class="w-[70px] h-[70px] rounded-[4px] overflow-hidden" :src="img(item.front_end_logo)" />
                                <img v-else class="w-[70px] h-[70px] rounded-[4px] overflow-hidden" src="@/app/assets/images/site_logo.png" />
                                <view v-if="item.business_state == 1" class="operation pl-[4px] pr-[10px] pb-[6px] absolute top-0 left-0 text-[12px] text-[#fff] box-border leading-[18px]">{{ item.business_state_name }}</view>
                                <view v-else class="rest pl-[4px] pr-[10px] pb-[6px] absolute top-0 left-0 text-[12px] text-[#fff] box-border leading-[18px]">{{ item.business_state_name }}</view>
                            </div>
                            <div class="flex-1 ml-[12px]">
                                <div class="text-[14px] text-[#222] max-w-[145px] font-[600] truncate mb-[4px]">{{item.site_name}}</div>
                                <div class="text-[12px] text-[#999] mb-[4px]">店铺编号：{{item.site_id}}</div>
                                <el-tooltip class="box-item" effect="light" placement="bottom">
                                    <template #content>
                                        <div class="flex items-center">
                                            <template v-for="(subItem,subIndex) in item.app_name" :key="subIndex">
                                                <span>{{ subItem }}</span>
                                                <span v-if="subIndex !== item.app_name.length - 1">、</span>
                                            </template>
                                        </div>
                                    </template>
                                    <div class="max-w-[236px]  using-hidden" v-if="item.app_name.length">
                                        <span class="inline-block text-[12px] mr-[6px] h-[18px] px-[4px] rounded-[2px] border-solid border-[1px] whitespace-nowrap" :style="{ color: randomColorList[subIndex % randomColorList.length], borderColor: randomColorList[subIndex % randomColorList.length] }" v-for="(subItem,subIndex) in item.app_name" :key="subIndex">{{ subItem }}</span>
                                    </div>
                                </el-tooltip>
                            </div>
                        </div>
                    </div>
                    
                    <div v-if="!site.tableData.length && !site.loading" class="flex flex-col items-center m-auto">
                        <img src="@/app/assets/images/empty.png"/>
                        <p class="text-center text-gray-400">暂无店铺</p>
                    </div>
                </div>
            </div>
            <div class="mt-[16px] flex justify-end">
                <el-pagination v-model:current-page="site.page" v-model:page-size="site.limit"
                            layout="total, prev, pager, next, jumper" :total="site.total"
                            @current-change="getHomeSiteFn" :hide-on-single-page="true"/>
            </div>
        </el-dialog>
        <user-info-edit ref="userInfoEditRef" />
    </div>
</template>

<script lang="ts" setup>
import { UserFilled } from '@element-plus/icons-vue'
import { reactive, ref, computed } from 'vue'
import { img } from '@/utils/common'
import { RouteLocationRaw, useRouter } from 'vue-router'
import { FormInstance, FormRules, ElNotification } from 'element-plus'
import userInfoEdit from '@/app/components/user-info-edit/index.vue'
import useUserStore from '@/stores/modules/user'
import { setUserInfo } from '@/app/api/personal'
import { getHomeSite, getAddon } from '@/app/api/home'
import { t } from '@/lang'
import storage from '@/utils/storage'
const userStore = useUserStore()

const siteInfo = userStore.siteInfo
const router = useRouter()

const clickEvent = (command: string) => {
    switch (command) {
        case 'logout':
            userStore.logout()
            break
    }
}

const userInfoEditRef = ref(null)
const getUserInfoFn = () => {
    userInfoEditRef.value?.open()
}

const info = computed(() => {
    return userInfoEditRef.value?.saveInfo
})

// 修改密码 --- start
const changePasswordDialog = ref(false)
const formRef = ref<FormInstance>()
// 提交信息
const saveInfo = reactive({
    original_password: '',
    password: '',
    password_copy: ''
})
// 表单验证规则
const formRules = reactive<FormRules>({
    original_password: [
        { required: true, message: t('originalPasswordPlaceholder'), trigger: 'blur' }
    ],
    password: [
        { required: true, message: t('passwordPlaceholder'), trigger: 'blur' }
    ],
    password_copy: [
        { required: true, message: t('passwordPlaceholder'), trigger: 'blur' }
    ]
})
const submitForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.validate((valid) => {
        if (valid) {
            let msg = ''
            if (saveInfo.password && !saveInfo.original_password) msg = t('originalPasswordHint')
            if (saveInfo.password && saveInfo.original_password && !saveInfo.password_copy) msg = t('newPasswordHint')
            if (saveInfo.password && saveInfo.original_password && saveInfo.password_copy && saveInfo.password != saveInfo.password_copy) msg = t('doubleCipherHint')
            if (msg) {
                ElNotification({
                    type: 'error',
                    message: msg
                })
                return
            }

            setUserInfo(saveInfo).then((res: any) => {
                changePasswordDialog.value = false
            })
        } else {
            return false
        }
    })
}
// 修改密码 --- end
const addonList = ref({})
const getAddonFn = () => {
    getAddon().then((res: any) => {
        addonList.value = res.data
    })
}

const randomColorList = ref([
    '#1890FF',
    '#FF6D11',
    '#29C441',
    '#FF0202',
    '#890CE8',
    '#F404C0',
    '#00C8DE'
])
// 切换店铺
const changeSiteDialog = ref(false)
const site = reactive({
    page: 1,
    limit: 9,
    searchParam: {
        search_value: '',
        search_type: 'site_name',
        app: ''
    },
    loading: false,
    tableData: [],
    total: 0
})
const getHomeSiteFn = (page: number = 1) => {
    site.page = page
    site.loading = true
    getHomeSite({
        page: site.page,
        limit: site.limit,
        ...site.searchParam
    }).then(res => {
        site.tableData = res.data.data
        site.total = res.data.total
        site.loading = false
    }).catch(() => {
        site.loading = false
    })
}
const handleApp = (data: any) => {
    site.searchParam.app = data.key
    getHomeSiteFn()
}
const changeSite = () => {
    getHomeSiteFn()
    getAddonFn()
    changeSiteDialog.value = true
}
const selectSite = (site: any) => {
    storage.set({ key: 'siteId', data: site.site_id })
    storage.set({ key: 'siteInfo', data: site })
    storage.set({ key: 'comparisonSiteIdStorage', data: site.site_id })
    useUserStore().$patch((site) => {
        site.siteInfo = site
    })
    location.href = `${location.origin}/site/`
}
</script>

<style lang="scss" scoped>
.el-popper .el-dropdown-menu{
  width: 150px;
}
.home-item{
  box-shadow: 0 2px 4px 0 rgba(161,167,183,0.18);
  .items-tab span{
    transform: scale(0.9);
  }
}
.home-item:hover {
  border-color: var(--el-color-primary);
  .title {
    color: var(--el-color-primary);
  }
  .home-item-head{
    background-color: #A1A7B7;
    span{
      color: #fff !important;
    }
  }
}
:deep(.secrch-input .el-input__wrapper){
    box-shadow: none !important;
}
:deep(.secrch-input .el-select__wrapper){
    box-shadow: none !important;
}
:deep(.secrch-input .el-select__selected-item){
    font-size: 12px;
    color:#323233;
    font-weight: 700;
}
.operation {
    background-image: url('@/app/assets/images/operation.png');
    background-repeat: no-repeat;
    background-size: 100% 100%;
}
.rest {
    background-image: url('@/app/assets/images/rest.png');
    background-repeat: no-repeat;
    background-size: 100% 100%;
}
</style>
