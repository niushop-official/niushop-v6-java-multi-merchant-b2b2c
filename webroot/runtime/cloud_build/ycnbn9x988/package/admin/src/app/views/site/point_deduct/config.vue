<template>
  <!-- 店铺默认积分抵扣比例配置 -->
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
        <h3 class="text-page-title">{{ t('shopPointDeductRatio') }}</h3>

        <!-- 店铺默认抵扣比例（增减按钮样式） -->
        <el-form-item
            :label="t('shopDefaultDeductRatio')"
            class="required-form-item"
            prop="deduct_rate"
        >
          <div class="deduct-amount-group">
            <!-- 比例输入框 -->
            <el-input-number
                v-model.trim="formData.deduct_rate"
                maxlength="8"
                class="deduct-amount-input"
                step="0.01"
                :min="0"
                :max="100"
                placeholder="请输入抵扣比例"
            >
            </el-input-number>
            <span class="ml-1">%</span>
            <!-- 说明文字 -->
           
          </div>
           <span class="deduct-desc ml-2">（例：输入10表示商品金额的10%可使用积分抵扣）</span>
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
import { getShopPointConfig, setShopPointConfig } from '@/app/api/basic_site'

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
    deduct_rate: [
        {
            required: true,
            trigger: 'blur',
            validator: (rule: any, value: any, callback: any) => {
                if (value === null || value === '') {
                    callback('请输入店铺默认抵扣比例')
                } else if (isNaN(value) || !regExp.digit.test(value)) {
                    callback('比例格式错误，最多保留两位小数')
                } else if (Number(value) < 0 || Number(value) > 100) {
                    callback('抵扣比例必须在0-100之间')
                } else {
                    callback()
                }
            }
        }
    ]
})

// 表单数据
const formData = reactive<Record<string, any>>({
    deduct_rate: '100' // 店铺默认抵扣比例（默认值）
})

/**
 * 增减比例处理
 */
const handleRatioChange = (step: number) => {
    const current = Number(formData.deduct_rate) || 0
    const newVal = (current + step).toFixed(2)
    // 限制0-100范围
    const finalVal = Math.max(0, Math.min(100, Number(newVal))).toFixed(2)
    formData.deduct_rate = finalVal
}

/**
 * 过滤数字（保留两位小数）
 */
// const filterDigit = (e: Event) => {
//     const target = e.target as HTMLInputElement
//     target.value = target.value.replace(/[^0-9.]/g, '')
//     if (target.value.indexOf('.') !== -1) {
//         const arr = target.value.split('.')
//         target.value = arr[0] + '.' + (arr[1] || '').substring(0, 2)
//     }
//     // 同步到表单数据
//     formData.deduct_rate = target.value
// }

/**
 * 获取店铺默认配置
 */
const setFormData = async () => {
    try {
        const res = await getShopPointConfig()
        const data = res.data || {}
        formData.deduct_rate = (data.deduct_rate ?? '10.0').toString()
    } catch (error) {
        console.error('获取店铺积分配置失败：', error)
    } finally {
        loading.value = false
    }
}
setFormData()

/**
 * 保存店铺默认配置
 */
const onSave = async (formEl: FormInstance | undefined) => {
    if (loading.value || !formEl) return

    await formEl.validate(async (valid) => {
        if (valid) {
            loading.value = true
            try {
                const saveData = cloneDeep(formData)
                saveData.deduct_rate = Number(saveData.deduct_rate) || 0

                await setShopPointConfig(saveData)
            } catch (error) {
                console.error('保存店铺积分配置失败：', error)
            } finally {
                loading.value = false
            }
        }
    })
}
</script>

<style lang="scss" scoped>
// 完全复用参考页面的样式结构
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
    width: 180px;
    border-radius: 0;
    border-left: none;
    border-right: none;
    text-align: center;

    :deep(.el-input__suffix) {
      right: 0;
      color: #606266;
    }
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

// 全局容器样式
.main-container {
  padding: 20px;
  min-height: 100vh;
  box-sizing: border-box;
  background-color: #f5f7fa;
}

.box-card {
  background: #fff;
  border-radius: 4px;
  padding: 20px;
  margin-bottom: 20px;
}
</style>
