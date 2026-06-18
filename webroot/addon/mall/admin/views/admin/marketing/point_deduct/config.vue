<template>
  <!-- 积分抵扣配置 -->
  <div class="main-container">
    <el-form
        class="page-form"
        :model="formData"
        label-width="150px"
        ref="ruleFormRef"
        :rules="formRules"
        v-loading="loading"
    >
      <el-card class="box-card !border-none" shadow="never">
        <h3 class="text-page-title">{{ t('pointDeductRule') }}</h3>

        <!-- 功能开关：指定active/inactive为数字类型，匹配接口返回的int -->
        <el-form-item :label="t('isUse')">
          <el-switch
              v-model="formData.is_open"
              :active-value="1"
              :inactive-value="0"
          />
        </el-form-item>

        <!-- 积分抵用金额（增减按钮样式） -->
        <el-form-item
            :label=" t('deductMoney') "
            v-if="formData.is_open === 1"
            class="required-form-item"
        >
          <div class="deduct-amount-group">
            <!-- 减号按钮 -->
            <el-button
                type="default"
                @click="handleDeductAmountChange(-0.01)"
                :disabled="Number(formData.deduct_amount) <= 0"
                class="amount-btn"
            >-</el-button> <!-- 文字减号 -->
            <!-- 金额输入框 -->
            <el-input
                v-model.trim="formData.deduct_amount"
                @keyup="filterDigit($event)"
                maxlength="10"
                class="deduct-amount-input"
                step="0.01"
                placeholder="请输入金额"
            />
            <!-- 加号按钮 -->
            <el-button
                type="default"
                @click="handleDeductAmountChange(0.01)"
                class="amount-btn"
            >+</el-button> <!-- 文字加号 -->
            <!-- 说明文字 -->
            <span class="deduct-desc ml-2">（指1积分抵用多少金额，单位：元）</span>
          </div>
        </el-form-item>
      </el-card>
    </el-form>

    <!-- 底部保存按钮 -->
    <div class="fixed-footer-wrap">
      <div class="fixed-footer">
        <el-button type="primary" @click="onSave(ruleFormRef)">{{ t('save') }}</el-button>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { t } from '@/lang'
import { FormInstance, FormRules } from 'element-plus'
import { cloneDeep } from 'lodash-es'
import { getPointDeductConfig, setPointDeductConfig } from '@/addon/mall/api/admin/marketing'

// 加载状态
const loading = ref(true)
// 表单引用
const ruleFormRef = ref<FormInstance>()

// 正则表达式
const regExp: any = {
    digit: /^\d+(\.\d{1,2})?$/ // 最多两位小数
}

// 表单验证规则
const formRules = reactive<FormRules>({
    deduct_amount: [
        {
            required: true,
            trigger: 'blur',
            validator: (rule: any, value: any, callback: any) => {
                if (formData.is_open !== 1) return callback()
                if (value === null || value === '') {
                    callback('请输入积分抵用金额')
                } else if (isNaN(value) || !regExp.digit.test(value)) {
                    callback('金额格式错误，最多保留两位小数')
                } else if (Number(value) <= 0) {
                    callback('金额必须大于0')
                } else {
                    callback()
                }
            }
        }
    ]
})

// 表单数据
const formData = reactive<Record<string, any>>({
    is_open: 0, // 功能开关
    deduct_amount: '3.1' // 1积分抵用金额（默认值）
})

/**
 * 增减金额处理
 */
const handleDeductAmountChange = (step: number) => {
    const current = Number(formData.deduct_amount) || 0
    const newVal = (current + step).toFixed(2)
    formData.deduct_amount = newVal
}

/**
 * 过滤数字（保留两位小数）
 */
const filterDigit = (e: Event) => {
    const target = e.target as HTMLInputElement
    target.value = target.value.replace(/[^0-9.]/g, '')
    if (target.value.indexOf('.') !== -1) {
        const arr = target.value.split('.')
        target.value = arr[0] + '.' + (arr[1] || '').substring(0, 2)
    }
}

/**
 * 获取积分抵扣配置
 */
const setFormData = async () => {
    try {
        const res = await getPointDeductConfig()
        const data = res.data || {}
        formData.is_open = Number(data.is_open) || 0
        formData.deduct_amount = (data.deduct_amount ?? '3.1').toString()
    } catch (error) {
        console.error('获取积分抵扣配置失败：', error)
    } finally {
        loading.value = false
    }
}
setFormData()

/**
 * 保存积分抵扣配置
 */
const onSave = async (formEl: FormInstance | undefined) => {
    if (loading.value || !formEl) return

    await formEl.validate(async (valid) => {
        if (valid) {
            loading.value = true
            try {
                const saveData = cloneDeep(formData)
                saveData.is_open = Number(saveData.is_open)
                saveData.deduct_amount = Number(saveData.deduct_amount) || 0

                await setPointDeductConfig(saveData)
            } catch (error) {
                console.error('保存积分抵扣配置失败：', error)
            } finally {
                loading.value = false
            }
        }
    })
}
</script>

<style lang="scss" scoped>
// 金额增减组样式
.deduct-amount-group {
  display: flex;
  align-items: center;
  gap: 0;

  .el-button {
    border-radius: 0;
    border-right: none;
    &:last-of-type {
      border-right: 1px solid #dcdfe6;
      border-left: none;
    }
  }

  .deduct-amount-input {
    width: 80px;
    border-radius: 0;
    border-left: none;
    border-right: none;
    text-align: center;
  }

  .deduct-desc {
    color: #909399;
    font-size: 12px;
  }
}

.fixed-footer-wrap {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  padding: 15px 20px;
  border-top: 1px solid #ebeef5;
  z-index: 99;
}

.fixed-footer {
  max-width: 1200px;
  margin: 0 auto;
  text-align: right;
}

.text-page-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 20px;
  color: #303133;
}

// 必填标识样式
:deep(.required-form-item .el-form-item__label)::before {
  content: '*';
  color: #f56c6c;
  margin-right: 4px;
  display: inline-block !important;
}
</style>
