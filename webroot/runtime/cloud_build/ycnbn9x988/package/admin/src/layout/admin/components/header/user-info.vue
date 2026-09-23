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
          <el-dropdown-item v-if="isCircle" @click="changeCircle">
            <div class="flex items-center leading-[1] py-[5px]">
              <span class="iconfont iconqiehuan ml-[4px] !text-[14px] mr-[10px]"></span>
              <span class="text-[14px]">切换商圈</span>
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
          <el-dropdown-item @click="changePasswordDialog = true">
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

    <!-- 修改密码弹窗 -->
    <el-dialog v-model="changePasswordDialog" width="450px" title="修改密码">
      <div>
        <el-form :model="saveInfo" label-width="90px" ref="formRef" :rules="formRules" class="page-form">
          <el-form-item :label="t('originalPassword')" prop="original_password">
            <el-input v-model="saveInfo.original_password" type="password" :placeholder="t('originalPasswordPlaceholder')" clearable class="input-width" />
          </el-form-item>
          <el-form-item :label="t('newPassword')" prop="password">
            <el-input v-model="saveInfo.password" type="password" :placeholder="t('passwordPlaceholder')" clearable class="input-width" />
            <div class="form-tip">{{ t('passwordTip') }}</div>
          </el-form-item>
          <el-form-item :label="t('passwordCopy')" prop="password_copy">
            <el-input v-model="saveInfo.password_copy" type="password" :placeholder="t('passwordPlaceholder')" clearable class="input-width" />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
                <span class="dialog-footer">
                    <el-button @click="changePasswordDialog = false">{{ t('cancel') }}</el-button>
                    <el-button type="primary" @click="submitForm(formRef)">{{ t('save') }}</el-button>
                </span>
      </template>
    </el-dialog>

    <!-- 切换商圈弹窗 -->
    <el-dialog v-model="changeCircleDialog" width="900px" title="切换商圈" align-center append-to-body>
      <el-form :inline="true" :model="circleSearch" ref="circleSearchFormRef">
        <el-form-item>
          <div class="secrch-input w-[400px] h-[38px] flex items-center border-solid border-[1px] border-[#ddd] rounded-[8px] box-border">
            <el-input
              v-model.trim="circleSearch.name"
              placeholder="请输入商圈名称"
              class="w-[320px] !text-[12px]"
              maxlength="60"
              @keyup.enter="getCircleListFn()"
            />
            <span class="text-[14px] font-[500] text-[#323233] flex-shrink-0 mr-[16px] cursor-pointer" @click="getCircleListFn()">查询</span>
          </div>
        </el-form-item>
      </el-form>
      <div class="min-h-[350px]">
        <div class="flex flex-wrap gap-[16px]" v-loading="circleLoading">
          <div
            v-for="(item, index) in circleList"
            :key="index"
            @click="selectCircle(item)"
            :class="['w-[260px] p-[12px] box-border border-[1px] border-solid border-[#ddd] rounded-[6px] cursor-pointer flex relative overflow-hidden items-center', {'!border-primary bg-[#f3f7ff]': currentCircleId == item.id}]"
          >
            <view
              class="absolute top-[-1px] right-[-1px] w-[18px] h-[18px] bg-primary rounded-bl-[10px] rounded-tr-[6px] flex items-center justify-center"
              v-if="currentCircleId == item.id"
            >
              <span class="nc-iconfont nc-icon-duihaoV6xx-1 text-[#fff] !text-[12px]"></span>
            </view>
            <div class="flex-1">
              <div class="text-[14px] text-[#222] font-[600] truncate mb-[4px]">{{ item.name }}</div>
              <div class="text-[12px] text-[#999]">商圈ID：{{ item.id }}</div>
            </div>
          </div>
          <div v-if="!circleList.length && !circleLoading" class="m-auto pt-[60px]">
            <img src="@/app/assets/images/empty.png" />
            <p class="text-center text-gray-400">暂无商圈</p>
          </div>
        </div>
      </div>
      <div class="mt-[16px] flex justify-end">
        <el-pagination
          v-model:current-page="circlePage"
          v-model:page-size="circleLimit"
          layout="total, prev, pager, next, jumper"
          :total="circleTotal"
          @current-change="getCircleListFn"
        />
      </div>
    </el-dialog>

    <user-info-edit ref="userInfoEditRef"  />
  </div>
</template>

<script lang="ts" setup>
import { UserFilled } from '@element-plus/icons-vue'
import { reactive, ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import type { FormInstance, FormRules, ElNotification } from 'element-plus'
import userInfoEdit from '@/app/components/user-info-edit/index.vue'
import useUserStore from '@/stores/modules/user'
import { setUserInfo } from '@/app/api/personal'
import { t } from '@/lang'
import { img, getAppType } from '@/utils/common'
import { getCircleSwitchList } from '@/app/api/site'
import storage from '@/utils/storage'

const userStore = useUserStore()
const router = useRouter()
const isCircle = getAppType() === 'circle'

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

// 切换商圈 --- start
const changeCircleDialog = ref(false)
const circleLoading = ref(false)
const circleList = ref<any[]>([])
const circlePage = ref(1)
const circleLimit = ref(9)
const circleTotal = ref(0)
const circleSearch = reactive({ name: '' })
const currentCircleId = ref(storage.get('circleId') || 0)

const getCircleListFn = (page: number = 1) => {
    circlePage.value = page
    circleLoading.value = true
    getCircleSwitchList({
        page: circlePage.value,
        limit: circleLimit.value,
        name: circleSearch.name
    }).then((res: any) => {
        circleList.value = res.data.data || res.data
        circleTotal.value = res.data.total || 0
        circleLoading.value = false
    }).catch(() => {
        circleLoading.value = false
    })
}

const changeCircle = () => {
    currentCircleId.value = storage.get('circleId') || 0
    getCircleListFn()
    changeCircleDialog.value = true
}

const selectCircle = (circle: any) => {
    storage.set({ key: 'circleId', data: circle.id })
    storage.set({ key: 'circleInfo', data: circle })
    location.href = `${location.origin}/circle/`
}
// 切换商圈 --- end
</script>

<style lang="scss" scoped>
.el-popper .el-dropdown-menu {
  width: 150px;
}
:deep(.secrch-input .el-input__wrapper) {
    box-shadow: none !important;
}
</style>
