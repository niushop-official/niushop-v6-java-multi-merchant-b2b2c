<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">
            <div class="flex justify-between items-center mb-[20px]">
                <span class="text-page-title">{{pageName}}</span>
            </div>
            <div class="bg-[#f9f9f9] p-[16px] min-h-[80vh]" v-loading="loading">
                <el-card class="box-card !border-none mb-[16px]" shadow="never">
                    <el-steps :active="activeStep" align-center>
                        <el-step title="资质提交" class="cursor-pointer" @click="handleStep(0)" >
                            <template #icon>
                                <span class="nc-iconfont nc-icon-shuaxinV6mm2 !text-[32px]" v-if="activeStep == 0"></span>
                                <span class="nc-iconfont nc-icon-duiV6xx !text-[32px]" v-else></span>
                            </template>
                        </el-step>
                        <el-step title="业务入驻" class="cursor-pointer" @click="handleStep(1)">
                            <template #icon>
                                <span class="nc-iconfont nc-icon-shuaxinV6mm2 !text-[32px]" v-if="activeStep == 1"></span>
                                <span class="nc-iconfont nc-icon-duiV6xx !text-[32px]" v-else></span>
                            </template>
                        </el-step>
                        <el-step title="入驻审核" class="cursor-pointer" @click="handleStep(2)">
                            <template #icon>
                                <span class="nc-iconfont nc-icon-shuaxinV6mm2 !text-[32px]" v-if="activeStep == 2"></span>
                                <span class="nc-iconfont nc-icon-duiV6xx !text-[32px]" v-else></span>
                            </template>
                        </el-step>
                        <el-step title="入驻成功" class="cursor-pointer" @click="handleStep(4)">
                            <template #icon>
                                <span class="nc-iconfont nc-icon-duiV6xx !text-[32px]"></span>
                            </template>
                        </el-step>
                    </el-steps>
                </el-card>
                <div>
                    <div class="bg-[#fff] p-[20px] flex flex-col items-center justify-center mb-[16px] leading-[1]" v-if="huifuData.is_settle == 1 && currentStep == 2">
                        <span class="nc-iconfont nc-icon-shijianV6mm !text-[66px] mb-[30px]"></span>
                        <div class="text-[26px] font-[500] mb-[16px]">申请已提交，等待平台审核</div>
                        <div class="text-[12px] text-[#999] mb-[10px]">汇付公司的服务时间为周一至周五，周末期间的请求可能稍有延迟，请您谅解。工作人员将在下个工作日尽快为您处理，感谢您的耐心等待。</div>
                        <div class="text-[16px]">{{huifuData.settle_desc }}</div>
                    </div>
                    <div class="bg-[#fff] p-[20px] flex flex-col items-center justify-center mb-[16px] leading-[1]"  v-if="huifuData.is_settle == -1 && currentStep == 2">
                        <span class="nc-iconfont nc-icon-cuohaoV6mm text-[#ef000c] !text-[66px] mb-[30px]"></span>
                        <div class="text-[26px] font-[500] mb-[16px]">支付信息未通过</div>
                        <div class="text-[16px]">{{huifuData.settle_desc }}</div>
                    </div>
                    <div class="bg-[#fff] p-[20px] flex flex-col items-center justify-center mb-[16px] leading-[1]" v-if="huifuData.is_settle == 2 && currentStep == 4">
                        <span class="nc-iconfont nc-icon-duihaoV6mm text-primary !text-[66px] mb-[30px]"></span>
                        <div class="text-[26px] font-[500] mb-[16px]">支付信息已通过</div>
                        <div class="text-[16px]">您提交的支付认证信息平台已经通过，欢迎您的加入</div>
                        <el-button type="primary"  class="mt-[16px]" @click="router.push('/mall_huifu/info')">查看</el-button>
                    </div>
                </div>
                <div v-if="!loading">
                    <el-form :model="formData" label-width="220px" ref="formRef" :rules="formRules" class="page-form bg-[#fff] p-[20px]" v-show="currentStep == 0">
                        <h3 class="panel-title !text-sm">用户类型</h3>
                        <el-form-item :label="t('用户类型')" prop="type">
                            <el-radio-group v-model="formData.type" class="ml-4" :disabled ="disabled">
                                <el-radio :label="1">{{ t('企业') }}</el-radio>
                                <el-radio :label="2">{{ t('个人') }}</el-radio>
                            </el-radio-group>
                        </el-form-item>
                        <h3 class="panel-title !text-sm">基本信息</h3>
                        <template v-if="formData.type === 1">
                            <el-form-item :label="t('公司类型')" prop="ent_type">
                                <el-select v-model="formData.ent_type" clearable :placeholder="t('请选择公司类型')" class="input-width" :disabled="disabled">
                                    <el-option :label="item" :value="index"  v-for="(item, index) in initData.EntType" :key="index" />
                                </el-select>
                            </el-form-item>
                            <el-form-item :label="t('企业用户名称')" prop="reg_name">
                                <el-input v-model.trim="formData.reg_name" clearable :placeholder="t('请输入企业用户名称')" maxlength="20" class="input-width" show-word-limit :disabled="disabled" />
                            </el-form-item>
                            <el-form-item :label="t('经营简称')" prop="short_name">
                                <el-input v-model.trim="formData.short_name" clearable :placeholder="t('请输入经营简称')" maxlength="20" class="input-width" show-word-limit :disabled="disabled" />
                            </el-form-item>
                            <el-form-item :label="t('营业执照')" prop="license_img">
                                <div>
                                    <upload-image v-model="formData.license_img" :disabled="disabled" />
                                    <p class="text-[12px] text-[#a9a9a9]">1.上传图片可以识别证照相关字段内容</p>
                                    <p class="text-[12px] text-[#a9a9a9] leading-[20px]">2.上传图片必须小于1M且清晰可见</p>
                                </div>
                            </el-form-item>
                            <el-form-item :label="t('营业执照编号')" prop="license_code">
                                <el-input v-model.trim="formData.license_code" clearable :placeholder="t('请输入营业执照编号')" maxlength="20" class="input-width" show-word-limit :disabled="disabled" />
                            </el-form-item>
                            <el-form-item :label="t('证照有效期起始日期')" prop="license_begin_date">
                                <el-date-picker v-model="formData.license_begin_date" type="date" value-format="YYYY-MM-DD" placeholder="请选择证照有效期起始日期" :disabled="disabled"/>
                            </el-form-item>
                            <el-form-item :label="t('证照有效期结束日期')" prop="license_end_date">
                                <div class="flex items-center">
                                    <el-radio-group v-model="formData.license_validity_type" class="mx-4" :disabled="disabled">
                                        <el-radio :label="0">{{ t('非长期有效') }}</el-radio>
                                        <el-radio :label="1">{{ t('长期有效') }}</el-radio>
                                    </el-radio-group>
                                    <el-date-picker v-model="formData.license_end_date" type="date" value-format="YYYY-MM-DD" placeholder="请选择证照有效期结束日期"  v-if="formData.license_validity_type === 0" :disabled="disabled" />
                                </div>
                            </el-form-item>
                            <el-form-item :label="t('注册地址')" prop="address_area">
                                <el-select v-model="formData.reg_prov_id" value-key="id" clearable class="w-[200px]"  ref="provinceRef" :disabled="disabled">
                                    <el-option :label="t('provincePlaceholder')" :value="0"/>
                                    <el-option v-for="(item, index) in areaList.province " :key="index" :label="item.name"  :value="item.id"/>
                                </el-select>
                                <el-select v-model="formData.reg_area_id" value-key="id" clearable class="w-[200px] ml-3" ref="cityRef" :disabled="disabled">
                                    <el-option :label="t('cityPlaceholder')" :value="0"/>
                                    <el-option v-for="(item, index) in areaList.city " :key="index" :label="item.name"  :value="item.id"/>
                                </el-select>
                                <el-select v-model="formData.reg_district_id" value-key="id" clearable class="w-[200px] ml-3"  ref="districtRef" :disabled="disabled">
                                    <el-option :label="t('districtPlaceholder')" :value="0"/>
                                    <el-option v-for="(item, index) in areaList.district " :key="index" :label="item.name"  :value="item.id"/>
                                </el-select>
                            </el-form-item>
                            <el-form-item :label="t('注册详细地址')" prop="reg_detail">
                                <div>
                                    <el-input v-model.trim="formData.reg_detail" clearable :placeholder="t('请输入注册详细地址')" maxlength="100" class="input-width" :disabled="disabled" />
                                    <p class="text-[12px] text-[#a9a9a9]">商户注册地址或营业执照住所</p>
                                </div>
                            </el-form-item>
                            <el-form-item :label="t('法人证件类型')" prop="legal_cert_type">
                                <el-select v-model="formData.legal_cert_type" clearable :placeholder="t('请选择法人证件类型')" class="input-width" :disabled="disabled" >
                                    <el-option :label="item" :value="index" v-for="(item,index) in  initData.LegalType" :key="index" />
                                </el-select>
                            </el-form-item>
                            <el-form-item :label="t('证件人像面')" prop="legal_cert_front_pic">
                                <upload-image v-model="formData.legal_cert_front_pic" :disabled="disabled" />
                            </el-form-item>
                            <el-form-item :label="t('证件国徽面')" prop="legal_cert_back_pic">
                                <upload-image v-model="formData.legal_cert_back_pic" :disabled="disabled" />
                            </el-form-item>
                            <el-form-item :label="t('法人姓名')" prop="legal_name">
                                <el-input v-model.trim="formData.legal_name" clearable :placeholder="t('请输入法人姓名')" maxlength="20" class="input-width" :disabled="disabled" />
                            </el-form-item>
                            <el-form-item :label="t('法人证件号码')" prop="legal_cert_no">
                                <el-input v-model.trim="formData.legal_cert_no" clearable :placeholder="t('请输入法人证件号码')" maxlength="20" class="input-width" :disabled="disabled"  />
                            </el-form-item>
                            <el-form-item :label="t('法人证件有效期开始日期')" prop="legal_cert_begin_date">
                                <el-date-picker v-model="formData.legal_cert_begin_date" type="date" value-format="YYYY-MM-DD" placeholder="请选择法人证件有效期开始日期" :disabled="disabled" />
                            </el-form-item>
                            <el-form-item :label="t('法人证件有效期截止日期')" prop="legal_cert_end_date">
                                <div class="flex items-center">
                                    <el-radio-group v-model="formData.legal_cert_validity_type" class="mx-4" :disabled="disabled">
                                        <el-radio :label="0">{{ t('非长期有效') }}</el-radio>
                                        <el-radio :label="1">{{ t('长期有效') }}</el-radio>
                                    </el-radio-group>
                                    <el-date-picker v-model="formData.legal_cert_end_date" type="date" value-format="YYYY-MM-DD" placeholder="请选择法人证件有效期截止日期"  v-if="formData.legal_cert_validity_type === 0" :disabled="disabled" />
                                </div>
                            </el-form-item>
                            <el-form-item :label="t('联系人姓名')" prop="contact_name">
                                <el-input v-model.trim="formData.contact_name" clearable :placeholder="t('请输入联系人姓名')" maxlength="20" class="input-width"  :disabled="disabled" />
                            </el-form-item>
                            <el-form-item :label="t('联系人手机号')" prop="contact_mobile">
                                <el-input v-model.trim="formData.contact_mobile" clearable :placeholder="t('请输入联系人手机号')" maxlength="20" class="input-width" :disabled="disabled" />
                            </el-form-item>
                            <el-form-item :label="t('联系人电子邮箱')" prop="contact_email">
                                <el-input v-model.trim="formData.contact_email" clearable :placeholder="t('请输入联系人电子邮箱')" maxlength="20" class="input-width" :disabled="disabled" />
                            </el-form-item>
                        </template>
                        <template v-if="formData.type === 2">
                            <el-form-item :label="t('姓名')" prop="per_name">
                                <el-input v-model.trim="formData.per_name" clearable :placeholder="t('请输入姓名')" maxlength="20" class="input-width" :disabled="disabled" />
                            </el-form-item>
                            <el-form-item :label="t('证件类型')" prop="per_cert_type">
                                <el-select v-model="formData.per_cert_type" clearable :placeholder="t('请选择证件类型')" class="input-width" :disabled="disabled" >
                                    <el-option :label="item" :value="index" v-for="(item,index) in  initData.LegalType" :key="index" />
                                </el-select>
                            </el-form-item>
                            <el-form-item :label="t('证件人像面')" prop="per_cert_front_pic">
                                <div>
                                    <upload-image v-model="formData.per_cert_front_pic" :disabled="disabled" />
                                    <p class="text-[12px] text-[#a9a9a9]">1.上传图片可以识别姓名、证件号码</p>
                                    <p class="text-[12px] text-[#a9a9a9] leading-[20px]">2.上传图片必须小于1M且清晰可见</p>
                                </div>
                            </el-form-item>
                            <el-form-item :label="t('证件国徽面')" prop="per_cert_back_pic">
                                <div>
                                    <upload-image v-model="formData.per_cert_back_pic" :disabled="disabled" />
                                    <p class="text-[12px] text-[#a9a9a9]">1.上传图片可以识别证件有效期</p>
                                    <p class="text-[12px] text-[#a9a9a9] leading-[20px]">2.上传图片必须小于1M且清晰可见</p>
                                </div>
                            </el-form-item>
                            <el-form-item :label="t('证件号码')" prop="per_cert_no">
                                <el-input v-model.trim="formData.per_cert_no" clearable :placeholder="t('请输入证件号码')" maxlength="20" class="input-width" :disabled="disabled" />
                            </el-form-item>
                            <el-form-item :label="t('证件有效期开始日期')" prop="per_cert_begin_date">
                                <el-date-picker v-model="formData.per_cert_begin_date" type="date" value-format="YYYY-MM-DD" placeholder="请选择证件有效期开始日期" :disabled="disabled" />
                            </el-form-item>
                            <el-form-item :label="t('证件有效期截止日期')" prop="per_cert_end_date">
                                <div class="flex items-center">
                                    <el-radio-group v-model="formData.per_cert_validity_type" class="mx-4" :disabled="disabled">
                                        <el-radio :label="0">{{ t('非长期有效') }}</el-radio>
                                        <el-radio :label="1">{{ t('长期有效') }}</el-radio>
                                    </el-radio-group>
                                    <el-date-picker v-model="formData.per_cert_end_date" type="date" value-format="YYYY-MM-DD" placeholder="请选择证件有效期截止日期"  v-if="formData.per_cert_validity_type === 0" :disabled="disabled" />
                                </div>
                            </el-form-item>
                            <el-form-item :label="t('联系人手机号')" prop="per_mobile_no">
                                <el-input v-model.trim="formData.per_mobile_no" clearable :placeholder="t('请输入联系人手机号')" maxlength="20" class="input-width" :disabled="disabled" />
                            </el-form-item>
                            <el-form-item :label="t('联系人电子邮箱')" prop="per_email">
                                <el-input v-model.trim="formData.per_email" clearable :placeholder="t('请输入联系人电子邮箱')" maxlength="20" class="input-width" :disabled="disabled" />
                            </el-form-item>
                        </template>
                        <el-form-item :label="t('商户通知标识')" prop="sms_send_flag">
                            <div>
                                <el-radio-group v-model="formData.sms_send_flag" class="ml-4" :disabled="disabled">
                                    <el-radio label="N">{{ t('否') }}</el-radio>
                                    <el-radio label="Y">{{ t('是') }}</el-radio>
                                </el-radio-group>
                                <p class="text-[12px] text-[#a9a9a9]">入驻成功后短信通知商户联系人</p>
                            </div>
                        </el-form-item>
                    </el-form>
                    <!-- 开通产品 -->
                    <el-form :model="formData"  label-width="220px" ref="productformRef" :rules="productformRules" class="page-form bg-[#fff] p-[20px]" v-show="currentStep == 1">
                        <h3 class="panel-title !text-sm">结算卡信息</h3>
                        <el-form-item :label="t('卡类型')" prop="card_type">
                            <div v-if="formData.type == 1">
                                <el-select v-model="formData.card_type" clearable :placeholder="t('请选择卡类型')" class="input-width" :disabled="updataDisabled">
                                    <el-option :label="item" :value="index"  v-for="(item, index) in initData.CardType" :key="index" />
                                </el-select>
                                <p class="text-[12px] text-primary">提示：个人用户仅支持对私法人类型</p>
                            </div>
                            <div v-else>
                                <el-select v-model="formData.card_type" clearable :placeholder="t('请选择卡类型')" class="input-width" :disabled="true">
                                    <el-option :label="item" :value="index"  v-for="(item, index) in initData.CardType" :key="index" />
                                </el-select>
                                <p class="text-[12px] text-primary">提示：个人用户仅支持对私法人类型</p>
                            </div>
                        </el-form-item>
                        <el-form-item :label="t('卡户名')" prop="card_name">
                            <div>
                                <el-input v-model.trim="formData.card_name" clearable :placeholder="t('请输入卡户名')" maxlength="20" class="input-width" :disabled="updataDisabled" />
                                <p class="text-[12px] text-[#a9a9a9]">持卡人姓名</p>
                            </div>
                        </el-form-item>
                        <el-form-item :label="t('卡号')" prop="card_no">
                            <div>
                                <el-input v-model.trim="formData.card_no" clearable :placeholder="t('请输入卡号')" maxlength="30" class="input-width" :disabled="updataDisabled" />
                                <p class="text-[12px] text-[#a9a9a9]">提示：银行卡号，为避免资金结算失败，请正确填写</p>
                            </div>
                        </el-form-item>
                        <template v-if="formData.card_type == 1 || formData.card_type == 2">
                            <el-form-item :label="t('持卡人证件类型')" prop="cert_type">
                                <div>
                                    <el-select v-model="formData.cert_type" clearable :placeholder="t('请选择持卡人证件类型')" class="input-width" :disabled="updataDisabled">
                                        <el-option :label="item" :value="index" v-for="(item,index) in  initData.LegalType" :key="index" />
                                    </el-select>
                                    <p class="text-[12px] text-[#a9a9a9]">提示：对私结算必填</p>
                                </div>
                            </el-form-item>
                            <el-form-item :label="t('持卡人证件号码')" prop="cert_no">
                                <div>
                                    <el-input v-model.trim="formData.cert_no" clearable :placeholder="t('请输入持卡人证件号码')" maxlength="20" class="input-width" :disabled="updataDisabled" />
                                    <p class="text-[12px] text-[#a9a9a9]">提示：对私结算必填, 持卡人年龄不小于18岁且不能大于80岁</p>
                                </div>
                            </el-form-item>
                            <el-form-item :label="t('持卡人证件有效期开始日期')" prop="cert_begin_date">
                                <el-date-picker v-model="formData.cert_begin_date" type="date" value-format="YYYY-MM-DD" placeholder="请选择持卡人证件有效期开始日期" :disabled="updataDisabled" />
                            </el-form-item>
                            <el-form-item :label="t('持卡人证件有效期截止日期')" prop="cert_end_date">
                                <div class="flex items-center">
                                    <el-radio-group v-model="formData.cert_validity_type" class="mx-4" :disabled="updataDisabled">
                                        <el-radio :label="0">{{ t('非长期有效') }}</el-radio>
                                        <el-radio :label="1">{{ t('长期有效') }}</el-radio>
                                    </el-radio-group>
                                    <el-date-picker v-model="formData.cert_end_date" type="date" value-format="YYYY-MM-DD" placeholder="请选择持卡人证件有效期截止日期"  v-if="formData.cert_validity_type === 0" :disabled="updataDisabled" />
                                </div>
                            </el-form-item>
                        </template>
                        <el-form-item :label="t('银行所在地址')" prop="bank_area">
                            <el-select v-model="formData.card_prov_id" value-key="id" clearable class="w-[200px]"  ref="provinceBankRef" :disabled="updataDisabled" >
                                <el-option :label="t('provincePlaceholder')" :value="0"/>
                                <el-option v-for="(item, index) in bankAreaList" :key="index" :label="item.title"  :value="item.value"/>
                            </el-select>
                            <el-select v-model="formData.card_area_id" value-key="id" clearable class="w-[200px] ml-3" ref="cityBankRef" :disabled="updataDisabled" >
                                <el-option :label="t('cityPlaceholder')" :value="0"/>
                                <el-option v-for="(item, index) in bankCityList" :key="index" :label="item.title"  :value="item.value"/>
                            </el-select>
                        </el-form-item>
                       <template v-if="formData.card_type == 0">
                            <el-form-item :label="t('开户行')" prop="bank_code">
                                <div>
                                    <el-select v-model="formData.bank_code" clearable :placeholder="t('请选择开户行')" class="input-width" filterable remote value-key="id"  ref="bankCodeRef" @change="getBankBranch" :disabled="updataDisabled" :remote-method="remoteMethod">
                                        <el-option :label="item.name" :value="item.id" v-for="(item, index) in bankList"  :key="index" />
                                    </el-select>
                                    <p class="text-[12px] text-[#a9a9a9]">提示：对私结算可以不填</p>
                                </div>
                            </el-form-item>
                            <el-form-item :label="t('支行名称')" prop="branch_code" v-if="formData.bank_code">
                                <el-select v-model="formData.branch_code" clearable :placeholder="t('请选择支行名称')" value-key="id"  filterable remote class="input-width"  ref="branchCodeRef" :disabled="updataDisabled" :remote-method="remoteMethodBranch">
                                    <el-option :label="item.sub_bank_name" :value="item.sub_bank_code" v-for="(item, index) in bankBranchList"  :key="index" />
                                </el-select>
                            </el-form-item>
                       </template>
                        <el-form-item :label="t('银行卡绑定手机号')" prop="mp">
                            <el-input v-model.trim="formData.mp" clearable :placeholder="t('请输入银行卡绑定手机号')" maxlength="20" class="input-width" :disabled="updataDisabled" />
                        </el-form-item>
                        <h3 class="panel-title !text-sm">结算配置</h3>
                        <el-form-item :label="t('结算周期')" prop="settle_cycle">
                            <div>
                                <el-select v-model="formData.settle_cycle" clearable :placeholder="t('请选择结算周期')" class="input-width" :disabled="updataDisabled">
                                    <el-option :label="item" :value="index" v-for="(item, index) in initData.SettleCycleType" :key="index" />
                                </el-select>
                                <p class="text-[12px] text-[#a9a9a9]">提示：D1: 下个自然日到账；T1: 下个工作日到账</p>
                            </div>
                        </el-form-item>
                        <!-- <el-form-item :label="t('结算方式')" prop="settle_pattern">
                            <div>
                                <el-select v-model="formData.settle_pattern" clearable :placeholder="t('请选择结算方式')" class="input-width" :disabled="updataDisabled">
                                    <el-option :label="item" :value="index" v-for="(item,index) in initData.SettlePatternType" :key="index" />
                                </el-select>
                                <p class="text-[12px] text-[#a9a9a9]">批次结算指按照指定结算批次进行资金结算</p>
                                <p class="text-[12px] text-[#a9a9a9]">定时结算是指按照商户指定到账时间进行资金结算</p>
                            </div>
                        </el-form-item> -->
                        <el-form-item :label="t('手续费内外扣标记')" prop="out_settle_flag">
                            <div>
                                <el-select v-model="formData.out_settle_flag"  clearable :placeholder="t('请选择手续费内外扣标记')" class="input-width" :disabled="updataDisabled">
                                    <el-option :label="item" :value="Number(index)"  v-for="(item,index) in initData.OutSettleFlag" :key="index"/>
                                </el-select>
                            </div>
                        </el-form-item>
                        <el-form-item :label="t('手续费外扣商户号')" prop="out_settle_huifuid" v-if="formData.out_settle_flag == 1">
                            <div>
                                <el-input v-model.trim="formData.out_settle_huifuid" clearable :placeholder="t('请输入手续费外扣商户号')" maxlength="20" class="input-width" :disabled="disabled"  />
                                <p class="text-[12px] text-[#a9a9a9]">提示：承担手续费的汇付商户号；当手续费内外扣标记为外扣时必填</p>
                            </div>
                        </el-form-item>
                        <el-form-item :label="t('手续费外扣账户类型')" prop="out_settle_acct_type" v-if="formData.out_settle_flag == 1">
                            <el-select v-model="formData.out_settle_acct_type" clearable :placeholder="t('请选择手续费外扣账户类型')" class="input-width" :disabled="disabled">
                                <el-option :label="item" :value="index"  v-for="(item,index) in initData.OutSettleAcctType" :key="index" />
                            </el-select>
                        </el-form-item>

                    </el-form>
                </div>
            </div>
        </el-card>
        <div class="fixed-footer-wrap" v-if="JSON.stringify(huifuData) == '[]' || huifuData.is_settle != 2">
            <div class="fixed-footer">
                <el-button type="primary" :loading="repeatLoading" @click="save(formRef)" v-if="!huifuData.extend_no">{{ t('认证') }}</el-button>
                <el-button type="primary" :loading="repeatLoading" @click="productFn(productformRef)" v-if="huifuData.is_settle == 0 || huifuData.is_settle == -1">{{ t('开通产品') }}</el-button>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from 'vue'
import { t } from '@/lang'
import { useRoute, useRouter } from 'vue-router'
import { FormInstance, FormRules } from 'element-plus'
import { getAreaListByPid } from '@/app/api/sys'
import { getHuifuGetparam, addHuifu, getHuifuBank, getHuifuProvincecity, getHuifuSubbank, getHuifuInfo, addBusinessentry } from '@/addon/mall_huifu/api/site/config'

const route = useRoute()
const router = useRouter()
const pageName = route.meta.title
const loading = ref<boolean>(true)
const repeatLoading = ref<boolean>(false)

const makeRange = (start: number, end: number) => {
    const result: number[] = []
    for (let i = start; i <= end; i++) {
        result.push(i)
    }
    return result
}
const disabledMinutes = (hour: number) => {
    if (hour === 0) {
        return makeRange(0, 30)
    }
}

const formData = reactive<Record<string, any>>({
    type: 1, // 1 企业 2 个人
    ent_type: '',
    ent_type_name: '',
    reg_name: '',
    short_name: '',
    license_img: '',
    license_code: '',
    license_begin_date: '',
    license_validity_type: 0,
    license_end_date: '',
    reg_prov_id: 0,
    reg_prov_name: '',
    reg_area_id: 0,
    reg_area_name: '',
    reg_district_id: 0,
    reg_district_name: '',
    reg_detail: '',
    legal_cert_type: '',
    legal_cert_type_name: '',
    legal_cert_front_pic: '',
    legal_cert_back_pic: '',
    legal_name: '',
    legal_cert_no: '',
    legal_cert_begin_date: '',
    legal_cert_validity_type: 0,
    legal_cert_end_date: '',
    contact_name: '',
    contact_mobile: '',
    contact_email: '',
    per_name: '',
    per_cert_type: '',
    per_cert_front_pic: '',
    per_cert_back_pic: '',
    per_cert_no: '',
    per_cert_begin_date: '',
    per_cert_validity_type: 0,
    per_cert_end_date: '',
    per_mobile_no: '',
    per_email: '',
    sms_send_flag: 'N',
    card_type: '',
    card_type_name: '',
    card_name: '',
    card_no: '',
    card_prov_id: 0,
    card_prov_name: '',
    card_area_id: 0,
    card_area_name: '',
    bank_code: '',
    bank_code_name: '',
    branch_code: '',
    branch_name: '',
    cert_type: '',
    cert_no: '',
    cert_begin_date: '',
    cert_validity_type: 0,
    cert_end_date: '',
    mp: '',
    settle_cycle: '',
    settle_cycle_name: '',
    min_amt: '',
    remained_amt: '',
    settle_abstract: '',
    settle_pattern: 'P0',
    settle_batch_no: 100,
    settle_is_priority_receipt: 'Y',
    out_settle_flag: '',
    out_settle_flag_name: '',
    out_settle_huifuid: '',
    out_settle_acct_type: ''
})
const huifuData = ref<any>({})
const disabled = ref<boolean>(false) // 是否禁用
const updataDisabled = ref<boolean>(false) // 是否禁用
const activeStep = ref<number>(0) // 步骤条
const currentStep = ref<number>(0) // 当前步骤

const formRef = ref<FormInstance>()
const productformRef = ref<FormInstance>()

// 正则表达式
const regExp: any = {
    idCard: /^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}([0-9]|X|x)$/
}

// 表单验证规则
const formRules = reactive<FormRules>({
    reg_name: [
        { required: true, message: t('请输入企业用户名称'), trigger: 'blur' }
    ],
    short_name: [
        { required: true, message: t('请输入经营简称'), trigger: 'blur' }
    ],
    license_img: [
        { required: true, message: t('请上传营业执照'), trigger: 'blur' } 
    ],
    license_code: [
        { required: true, message: t('请输入营业执照编号'), trigger: 'blur' }
    ],
    license_begin_date: [
        { required: true, message: t('请选择证照有效期起始日期'), trigger: 'blur' }
    ],
    license_end_date: [
        {
            required: true,
            validator: (rule: any, value: any, callback: any) => {
                if (formData.license_validity_type == 0 && !value) {
                    callback(new Error(t('请选择证照有效期结束日期')))
                } else if (formData.license_validity_type == 0 && (formData.license_begin_date && value < formData.license_begin_date)) {
                    callback(new Error(t('证照有效期结束日期不能小于起始日期')))
                } else {
                    callback()
                }
            }
        }
    ],
    address_area: [
        {
            required: true,
            validator: (rule: any, value: any, callback: any) => {
                if (!formData.reg_prov_id) {
                    callback(new Error(t('请选择省')))
                }
                if (!formData.reg_area_id) {
                    callback(new Error(t('请选择市')))
                }
                if (areaList.district.length && !formData.reg_district_id) {
                    callback(new Error(t('请选择区/县')))
                }
                callback()
            },
            trigger: ['blur', 'change']
        }
    ],
    reg_detail: [
        { required: true, message: t('请输入注册详细地址'), trigger: 'blur' }
    ],
    legal_cert_type: [
        { required: true, message: t('请选择法人证件类型'), trigger: ['blur', 'change'] }
    ],
    legal_cert_front_pic: [
        { required: true, message: t('请上传法人证件正面照'), trigger: 'blur' }
    ],
    legal_cert_back_pic: [
        { required: true, message: t('请上传法人证件反面照'), trigger: 'blur' }
    ],
    legal_name: [
        { required: true, message: t('请输入法人姓名'), trigger: 'blur' }
    ],
    legal_cert_no: [
        { required: true, message: t('请输入法人证件号码'), trigger: 'blur' },
        {
            trigger: 'blur',
            validator: (rule: any, value: any, callback: any) => {
                if (!regExp.idCard.test(value)) {
                    callback(new Error(t('请输入正确的身份证号码')))
                } else {
                    callback()
                }
            }
        }
    ],
    legal_cert_begin_date: [
        { required: true, message: t('请选择法人证件有效期开始日期'), trigger: 'blur' }
    ],
    legal_cert_end_date: [
        {
            required: true,
            validator: (rule: any, value: any, callback: any) => {
                if (formData.legal_cert_validity_type == 0 && !value) {
                    callback(new Error(t('请选择法人证件有效期截止日期')))
                } else if (formData.legal_cert_validity_type == 0 && (formData.legal_cert_begin_date && value < formData.legal_cert_begin_date)) {
                    callback(new Error(t('法人证件有效期结束日期不能小于起始日期')))
                } else {
                    callback()
                }
            }
        }
    ],
    contact_name: [
        { required: true, message: t('请输入联系人姓名'), trigger: 'blur' }
    ],
    contact_mobile: [
        { required: true, message: t('请输入联系人手机号'), trigger: 'blur' }
    ],
    per_name: [
        { required: true, message: t('请输入姓名'), trigger: 'blur' }
    ],
    per_cert_type: [
        { required: true, message: t('请选择证件类型'), trigger: ['blur', 'change'] }
    ],
    per_cert_front_pic: [
        { required: true, message: t('请上传证件人像面'), trigger: 'blur' }
    ],
    per_cert_back_pic: [
        { required: true, message: t('请上传证件国徽面'), trigger: 'blur' }
    ],
    per_cert_no: [
        { required: true, message: t('请输入证件号码'), trigger: 'blur' },
        {
            trigger: 'blur',
            validator: (rule: any, value: any, callback: any) => {
                if (!regExp.idCard.test(value)) {
                    callback(new Error(t('请输入正确的身份证号码')))
                } else {
                    callback()
                }
            }
        }
    ],
    per_cert_begin_date: [
        { required: true, message: t('请选择证件有效期开始日期'), trigger: 'blur' }
    ],
    per_cert_end_date: [
        { required: true, message: t('请选择证件有效期截止日期'), trigger: 'blur' }
    ],
    per_mobile_no: [
        { required: true, message: t('请输入联系人手机号'), trigger: 'blur' }
    ],
    per_email: [
        { required: true, message: t('请输入联系人电子邮箱'), trigger: 'blur' }
    ]

})
const productformRules = reactive<FormRules>({
    card_type: [
        { required: true, message: t('请选择卡类型'), trigger: ['blur', 'change'] }
    ],
    card_name: [
        { required: true, message: t('请输入卡户名'), trigger: 'blur' }
    ],
    card_no: [
        { required: true, message: t('请输入卡号'), trigger: 'blur' }
    ],
    cert_type: [
        { required: true, message: t('请选择持卡人证件类型'), trigger: ['blur', 'change'] }
    ],
    cert_no: [
        { required: true, message: t('请输入持卡人证件号码'), trigger: 'blur' }
    ],
    cert_begin_date: [
        { required: true, message: t('请选择持卡人证件有效期开始日期'), trigger: 'blur' }
    ],
    cert_end_date: [
        {
            required: true,
            validator: (rule: any, value: any, callback: any) => {
                if (formData.cert_validity_type == 0 && !value) {
                    callback(new Error(t('请选择持卡人证件有效期截止日期')))
                } else if (formData.cert_validity_type == 0 && (formData.cert_begin_date && value < formData.cert_begin_date)) {
                    callback(new Error(t('持卡人证件有效期截止日期不能小于起始日期')))
                } else {
                    callback()
                }
            }
        }
    ],
    bank_area: [
        {
            required: true,
            validator: (rule: any, value: any, callback: any) => {
                if (!formData.card_prov_id) {
                    callback(new Error(t('请选择银行所在省')))
                }
                if (!formData.card_area_id) {
                    callback(new Error(t('请选择银行所在市')))
                }
                callback()
            },
            trigger: ['blur', 'change']
        }
    ],
    bank_code: [
        { required: true, message: t('请选择开户行'), trigger: ['blur', 'change'] }
    ],
    branch_code: [
        { required: true, message: t('请选择支行名称'), trigger: 'blur' }
    ],
    settle_cycle: [
        { required: true, message: t('请选择结算周期'), trigger: ['blur', 'change'] }
    ],
    out_settle_flag: [
        { required: true, message: t('请选择手续费内外扣标记'), trigger: ['blur', 'change'] }
    ]
})
/**
 * 保存
 */
const save = async (formEl: FormInstance | undefined) => {
    if (repeatLoading.value || !formEl) return
    await formEl.validate(async (valid) => {
        if (valid) {
            if (formData.type == 1) {
                formData.ent_type_name = formData.ent_type ? initData.EntType[formData.ent_type] : ''
                formData.legal_cert_type_name = formData.legal_cert_type ? initData.LegalType[formData.legal_cert_type] : ''
                formData.reg_prov_name = formData.reg_prov_id ? provinceRef.value.selectedLabel : ''
                formData.reg_area_name = formData.reg_area_id ? cityRef.value.selectedLabel : ''
                formData.reg_district_name = formData.reg_district_id ? districtRef.value.selectedLabel : ''
            }
            if (formData.type == 2) {
                formData.per_cert_type_name = formData.per_cert_type ? initData.LegalType[formData.per_cert_type] : ''
            }
            repeatLoading.value = true

            addHuifu(formData).then(() => {
                getHuifuInfoFn()
                repeatLoading.value = false
            }).catch(() => {
                repeatLoading.value = false
            })
        }
    })
}
// 开通产品
const productFn = async (formEl: FormInstance | undefined) => {
    if (repeatLoading.value || !formEl) return

    await formEl.validate(async (valid) => {
        if (valid) {
            formData.card_type_name = formData.card_type ? initData.CardType[formData.card_type] : ''
            formData.card_prov_name = formData.card_prov_id ? provinceBankRef.value.selectedLabel : ''
            formData.card_area_name = formData.card_area_id ? cityBankRef.value.selectedLabel : ''
            formData.bank_code_name = formData.bank_code ? bankCodeRef.value.selectedLabel : ''
            formData.branch_name = formData.branch_code ? branchCodeRef.value.selectedLabel : ''
            formData.settle_cycle_name = formData.settle_cycle ? initData.SettleCycleType[formData.settle_cycle] : ''
            formData.out_settle_flag_name = formData.out_settle_flag ? initData.OutSettleFlag[formData.out_settle_flag] : ''
            repeatLoading.value = true

            addBusinessentry(formData).then(() => {
                getHuifuInfoFn()
                repeatLoading.value = false
            }).catch(() => {
                repeatLoading.value = false
            })
        }
    })
}

const bankAreaList = ref<any>([]) // 银行卡所在地址
const bankCityList = ref<any>([]) // 银行卡所在市
const bankList = ref<any>([]) // 开户行

const initData = reactive<any>({
    CardType: {}, // 卡类型
    EntType: {}, // 企业类型
    LegalType: {}, // 法人证件类型
    OutSettleAcctType: {}, // 手续费外扣标记类型
    OutSettleFlag: {}, // 手续费内外扣标记
    PriorityReceipType: {}, // 是否优先到账
    SettleBatchNoType: {}, // 结算批次号
    SettleCycleType: {}, // 结算周期
    SettlePatternType: {} // 结算方式
})
const setFormData = () => {
    getHuifuGetparam().then(res => {
        Object.assign(initData, res.data)
    })
    getHuifuBank().then(res => {
        bankList.value = res.data
    })

    getHuifuProvincecity().then(res => {
        bankAreaList.value = res.data
    })
}
setFormData()

const remoteMethod = (query: string) => {
    if (query) {
        getHuifuBank({ bank_name: query }).then(res => {
            bankList.value = res.data
        })
    }
}
const remoteMethodBranch = (query: string) => {
    if (query) {
        getHuifuSubbank({ sub_bank_name: query }).then(res => {
            bankBranchList.value = res.data
        })
    }
}

const bankBranchList = ref<any>([]) // 支行
const getBankBranch = (val: any) => {
    getHuifuSubbank({ bank_code: val }).then(res => {
        bankBranchList.value = res.data
    })
}

const areaList = reactive<any>({
    province: [],
    city: [],
    district: []
})
const provinceRef = ref()
const cityRef = ref()
const districtRef = ref()
const provinceBankRef = ref()
const cityBankRef = ref()
const bankCodeRef = ref()
const branchCodeRef = ref()

/**
 * 获取省
 */
getAreaListByPid(0).then(res => {
    areaList.province = res.data
})

watch(() => formData.reg_prov_id, (nval) => {
    if (nval) {
        getAreaListByPid(formData.reg_prov_id).then(res => {
            areaList.city = res.data

            const cityId = formData.reg_area_id
            if (cityId) {
                let isExist = false
                for (let i = 0; i < res.data.length; i++) {
                    if (cityId == res.data[i].id) {
                        isExist = true
                        break
                    }
                }
                if (isExist) {
                    formData.reg_area_id = cityId
                    return
                }
            }
            formData.reg_area_id = 0
        })
    } else {
        formData.reg_area_id = 0
    }
})

/**
* 获取区
 */
watch(() => formData.reg_area_id, (nval) => {
    if (nval) {
        getAreaListByPid(formData.reg_area_id).then(res => {
            areaList.district = res.data

            const districtId = formData.reg_district_id
            if (districtId) {
                let isExist = false
                for (let i = 0; i < res.data.length; i++) {
                    if (districtId == res.data[i].id) {
                        isExist = true
                        break
                    }
                }
                if (isExist) {
                    formData.reg_district_id = districtId
                    return
                }
            }
            formData.reg_district_id = 0
        })
    } else {
        formData.reg_district_id = 0
    }
})

// 银行所在市
watch([() => formData.card_prov_id, () => bankAreaList.value], ([newCardProvId, newBankAreaList]) => {
    if (newCardProvId && newBankAreaList && newBankAreaList.length) {
        bankAreaList.value.forEach((item: any) => {
            if (item.value == newCardProvId) {
                bankCityList.value = item.cities
            }
        })
        const cityId = formData.card_area_id
        if (cityId) {
            let isExist = false
            for (let i = 0; i < bankCityList.value.length; i++) {
                if (cityId == bankCityList.value[i].value) {
                    isExist = true
                    break
                }
            }
            if (isExist) {
                formData.card_area_id = cityId
                return
            }
            formData.card_area_id = 0
        }
    }
})

const handleStep = (val:any) => {
    if (val > activeStep.value) {
        return false
    }
    currentStep.value = val
}

const getHuifuInfoFn = () => {
    getHuifuInfo().then(res => {
        huifuData.value = res.data
        if (JSON.stringify(res.data) == '[]') {
            activeStep.value = 0
            currentStep.value = 0
        } else {
            if (huifuData.value.is_settle == 2) {
                activeStep.value = 4
                currentStep.value = 4
                disabled.value = true
                updataDisabled.value = true
            } else if (huifuData.value.is_settle == 1) {
                disabled.value = true
                updataDisabled.value = true
                activeStep.value = 2
                currentStep.value = 2
            } else if (huifuData.value.is_settle == -1) {
                disabled.value = true
                activeStep.value = 2
                currentStep.value = 2
            } else if (huifuData.value.is_settle == 0) {
                disabled.value = true
                activeStep.value = 1
                currentStep.value = 1
            }
            Object.keys(formData).forEach((key: string) => {
                if (res.data[key] != undefined) formData[key] = res.data[key]
            })

            if (formData.type == 2) {
                formData.card_type = 1
            }
            if (formData.bank_code) {
                getHuifuBank({ bank_code: formData.bank_code }).then(res => {
                    bankList.value = res.data
                })
                getHuifuSubbank({ bank_code: formData.bank_code, sub_bank_code: formData.branch_code }).then(res => {
                    bankBranchList.value = res.data
                })
            }
        }
        loading.value = false
    })
}
getHuifuInfoFn()
</script>

<style lang="scss" scoped>
:deep(.el-step__title.is-process) {
    color: var(--el-color-primary) !important;
}
:deep(.el-step__head.is-process .el-step__icon.is-icon){
    color: var(--el-color-primary)!important;
}
</style>
