<template>
    <div class="main-container">
        <el-card class="card !border-none" shadow="never">
            <el-page-header :content="id ? t('编辑活动') : t('添加活动')" :icon="ArrowLeft" @back="back()" />
        </el-card>

        <!-- 表单 -->
        <el-card class="box-card mt-[15px] !border-none" shadow="never" v-loading="loading">
            <el-tabs v-model="activeTabName" @tab-click="tabHandleClick">
                <el-tab-pane :label="t('活动信息')" name="basic">
                    <el-form :model="formData" label-width="120px" ref="basicFormRef" :rules="formRules" scroll-to-error class="page-form">
                        <el-form-item :label="t('活动名称')" prop="title">
                            <el-input v-model.trim="formData.title" clearable :placeholder="t('请输入活动名称')" class="input-width" :maxlength="20" show-word-limit />
                        </el-form-item>
                        <div class="ml-[120px] mb-[10px] mt-[10px] text-[12px] text-[#999] leading-[20px]">活动名称将显示在列表中展示，方便商家管理使用</div>
                        <el-form-item :label="t('活动图片')" prop="banner">
                            <upload-image v-model="formData.banner" :limit="1" />
                        </el-form-item>
                        <!-- 活动时间 -->
                        <el-form-item :label="t('活动时间')" prop="time">
                            <div class="!w-[300px]">
                                <el-date-picker v-model="formData.time" type="datetimerange" format="YYYY-MM-DD HH:mm"
                                    range-separator="-" start-placeholder="开始时间" end-placeholder="结束时间"
                                    @change="pintuanTimeChange" />
                            </div>
                        </el-form-item>
                        <div class="ml-[120px] mb-[10px] mt-[10px] text-[12px] text-[#999] leading-[20px]">商品只会在活动时间段内的指定场次进行展示</div>
                        <div v-if="id && formData.start_time && new Date(formData.start_time).getTime() < new Date().getTime()" class="ml-[120px] mb-[10px] text-[12px] text-[#ff4949] leading-[20px]">注意：活动已开始，开始时间不可修改，仅可延长结束时间</div>
                        <!-- 拼团类型 -->
                        <el-form-item :label="t('拼团类型')" prop="type">
                            <el-radio-group v-model="formData.type" :disabled="isProhibitEdit">
                                <el-radio v-for="(value, key) in pintuanTypeOptions" :label="key" :key="key">{{ value }}</el-radio>
                            </el-radio-group>
                        </el-form-item>
                        <el-form-item :label="t('允许直接参团')" prop="type">
                            <div>
                                <el-radio-group v-model="formData.can_join_directly">
                                    <el-radio :label="1">{{ t('是') }}</el-radio>
                                    <el-radio :label="0">{{ t('否') }}</el-radio>
                                </el-radio-group>
                                <div class="mt-[10px] text-[12px] text-[#999] leading-[20px]">关闭 "允许直接参团" 后，当前活动的团组将仅支持邀请制参团，其他人需通过邀请方可加入。</div>
                            </div>

                        </el-form-item>
                        <!-- 拼团人数 -->
                        <el-form-item :label="t('拼团人数')" prop="member_num_config.normal" v-if="formData.type == 'normal'|| formData.type == 'referral'">
                            <div class="!w-[300px]">
                                <el-input v-model.number="formData.member_num_config.normal" class="!w-[200px]" @keyup="filterNumber($event)" :maxlength="5" show-word-limit />
                            </div>
                        </el-form-item>
                        <!-- 阶梯拼团设置 -->
                        <el-form-item :label="t('阶梯设置')" v-if="formData.type === 'ladder'">
                            <div class="ladder-container">
                                <div v-for="(item, index) in formData.member_num_config.ladder" :key="index" class="ladder-item">
                                    <div class="flex flex-col md:flex-row gap-4 mb-[18px]">
                                        <div class="">
                                            <el-form-item :label="'第' + item.ladder + '阶梯人数'"
                                                :prop="`member_num_config.ladder[${index}].member_num`" :rules="[
                                                    {
                                                        required: true,
                                                        message: t('阶梯人数不能为空'),
                                                        trigger: 'blur'
                                                    },
                                                    {
                                                        validator: (rule, value, callback) => {
                                                            // 基础验证：必须是大于等于2的数字
                                                            if (value === '' || value === null || value === undefined) {
                                                                callback(t('阶梯人数不能为空'));
                                                            } else if (isNaN(value) || Number(value) < 2) {
                                                                callback(t('阶梯人数必须大于等于2'));  
                                                            } else {
                                                                // 递增验证：从第二个阶梯开始，必须大于前一个阶梯
                                                                if (index > 0) {
                                                                    const prevValue = formData.member_num_config.ladder[index - 1].member_num;
                                                                    if (Number(value) <= Number(prevValue)) {
                                                                        callback(t(`第${index + 1}阶梯人数必须大于第${index}阶梯`));
                                                                    }
                                                                }
                                                                callback();
                                                            }
                                                        },
                                                        trigger: 'blur' 
                                                    }
                                                ]">
                                                <el-input v-model.number="item.member_num" @keyup="filterNumber($event)" :maxlength="5" :placeholder="t('请输入阶梯人数')" show-word-limit class="!w-[200px]" />
                                            </el-form-item>
                                        </div>
                                        <div class="">
                                            <el-button v-if="index > 0" type="primary" link @click="removeLadder(index)">
                                                删除
                                            </el-button>
                                        </div>
                                    </div>
                                </div>
                                <el-button v-if="formData.type === 'ladder' && formData.member_num_config.ladder.length < 3" type="primary" plain @click="addLadder">
                                    添加阶梯
                                </el-button>
                            </div>
                        </el-form-item>

                        <div class="ml-[120px] mb-[10px] mt-[10px] text-[12px] text-[#999] leading-[20px]">最少两人成团，最多支持三级阶梯</div>
                        <el-form-item :label="t('团长优惠')" prop="leader_discount">
                            <div>
                                <el-radio-group v-model="formData.leader_discount">
                                    <el-radio label="on">{{ t('是') }}</el-radio>
                                    <el-radio label="off">{{ t('否') }}</el-radio>
                                </el-radio-group>
                                <div class="mt-[10px] text-[12px] text-[#999] leading-[20px]">开启后，发起拼团的团长可专享活动预设的「团长价」，优惠力度高于普通团员价格，鼓励用户主动发起拼团。</div>
                            </div>

                        </el-form-item>
                        <el-form-item label="拼团有效期" prop="valid_duration">
                            <div class="flex items-center space-x-2">
                                <el-select class="!w-[120px]" v-model="duration.days" placeholder="0" @change="calculateTotalSeconds">
                                    <el-option v-for="day in dayOptions" :key="day" :label="day" :value="day" />
                                </el-select>
                                <span>日</span>
                                <el-select class="!w-[120px]" v-model="duration.hours" placeholder="0" @change="calculateTotalSeconds">
                                    <el-option v-for="hour in hourOptions" :key="hour" :label="hour" :value="hour" />
                                </el-select>
                                <span>时</span>
                                <el-select class="!w-[120px]" v-model="duration.minutes" placeholder="0" @change="calculateTotalSeconds">
                                    <el-option v-for="minute in minuteOptions" :key="minute" :label="minute" :value="minute" />
                                </el-select>
                                <span>分</span>
                            </div>
                        </el-form-item>
                        <!-- 活动对象 -->
                        <el-form-item :label="t('活动对象')" prop="member_rule">
                            <el-radio-group v-model="formData.member_rule">
                                <el-radio v-for="(value, key) in memberRuleOptions" :label="key" :key="key">{{ value }}</el-radio>
                            </el-radio-group>
                        </el-form-item>
                        <el-form-item :label="t('选择会员')" prop="member_data" v-if="formData.member_rule == 'member'">
                            <select-member ref="selectMemberRef" v-model="formData.member_ids" @memberSelect="memberSelect" :min="1" :max="20" />
                        </el-form-item>
                        <el-form-item v-if="formData.member_data.length && formData.member_rule == 'member'" prop="member_data">
                            <el-table :data="formData.member_data" size="large" max-height="400">
                                <el-table-column prop="nickname" :show-overflow-tooltip="true" :label="t('memberInfo')" min-width="150">
                                    <template #default="{ row }">
                                        <div class="flex items-center">
                                            <div class="mr-[10px] rounded-full w-[50px] h-[50px] flex items-center justify-center">
                                                <img class="max-w-[50px] max-h-[50px]" v-if="row.headimg" :src="img(row.headimg)" alt="">
                                                <img class="max-w-[50px] max-h-[50px]" v-else src="@/app/assets/images/member_head.png" alt="">
                                            </div>
                                            <div class="flex flex-col">
                                                <span>{{ row.nickname || '' }}</span>
                                            </div>
                                        </div>
                                    </template>
                                </el-table-column>
                                <el-table-column prop="username" :label="t('用户名')" width="180" />
                                <el-table-column prop="member_no" :label="t('会员编号')" min-width="80" />
                                <el-table-column :label="t('operation')" align="right" min-width="160">
                                    <template #default="{ row, $index }">
                                        <el-button type="primary" link @click="deleteMemberEvent(row, $index)">{{ t("delete") }}</el-button>
                                    </template>
                                </el-table-column>
                            </el-table>
                        </el-form-item>
                        <!-- 会员标签 -->
                        <el-form-item :label="t('会员标签')" prop="member_label_id" v-if="formData.member_rule == 'label'">
                            <el-select  v-model="formData.member_label_id" clearable multiple :placeholder="t('请选择会员标签')" class="input-width">
                                <el-option :label="item['label_name']" :value="item['label_id']" v-for="(item, index) in labelSelectData" :key="index" />
                            </el-select>
                        </el-form-item>
                        <!-- 会员等级 -->
                        <el-form-item :label="t('会员等级')" prop="member_level_id" v-if="formData.member_rule == 'level'">
                            <el-select v-model="formData.member_level_id" clearable multiple :placeholder="t('请选择会员等级')" class="input-width">
                                <el-option :label="item['level_name']" :value="item['level_id']" v-for="(item, index) in levelSelectData" :key="index" />
                            </el-select>
                        </el-form-item>
                    </el-form>
                </el-tab-pane>
                <el-tab-pane :label="t('商品信息')" name="goodsInfo">
                    <el-form :model="formData" label-width="120px" ref="goodsInfoFormRef" :rules="formRules" scroll-to-error class="page-form">
                        <el-form-item :label="t('拼团商品')" prop="goods_data">
                            <div class=flex-1>
                                <div class="flex items-center mb-[10px]" v-if="Object.keys(formData.goods_data).length === 0 || (Object.keys(formData.goods_data).length != 0  && !formData.id)">
                                    <el-button type="primary" @click="importEvent" v-if="Object.keys(formData.goods_data).length === 0">{{ t('选择商品')}}</el-button>
                                    <el-button type="primary" @click="clearGoods()" class="ml-3" v-if="Object.keys(formData.goods_data).length != 0  && !formData.id">{{ t("重新选择") }}</el-button>
                                </div>
                                <div v-if="Object.keys(formData.goods_data).length != 0 && goodsTable.data">
                                    <div class="w-full sku_list">
                                        <div class="flex items-center flex-wrap mb-[15px]  pl-[14px]">
                                            <el-checkbox v-model="toggleCheckbox" size="large" class="!mr-[15px] mb-[10px]" @change="toggleChange" :indeterminate="isIndeterminate">
                                                <span>{{ t("已选") }} {{ multipleSelection.length }} {{ t("项") }}</span>
                                            </el-checkbox>
                                            <div class="text-[14px] text-[#666] mb-[10px]">{{ t("批量操作") }}</div>
                                            <el-input v-if="formData.type != 'ladder'" v-model.trim="batchPintuanPrice" clearable class="!w-[110px] ml-[10px] mb-[10px]" :placeholder="t('拼团价')" maxlength="8" />
                                            <el-input v-if="formData.leader_discount == 'on'" v-model.trim="batchLeaderPrice" clearable class="!w-[110px] ml-[10px] mb-[10px]" :placeholder="t('团长价')" maxlength="8" />
                                            <el-input v-if="formData.type === 'ladder' && formData.member_num_config.ladder.length >= 1" v-model.trim="batchLadderPrice1" clearable class="!w-[110px] ml-[10px] mb-[10px]" :placeholder="t('阶梯1')" maxlength="8" />
                                            <el-input v-if="formData.type === 'ladder' && formData.member_num_config.ladder.length >= 2" v-model.trim="batchLadderPrice2" clearable class="!w-[110px] ml-[10px] mb-[10px]" :placeholder="t('阶梯2')" maxlength="8" />
                                            <el-input v-if="formData.type === 'ladder' && formData.member_num_config.ladder.length >= 3" v-model.trim="batchLadderPrice3" clearable class="!w-[110px] ml-[10px] mb-[10px]" :placeholder="t('阶梯3')" maxlength="8" />
                                            <el-input v-model.trim="batchOrderLimitBuy" clearable class="!w-[110px] ml-[10px] mb-[10px]" :placeholder="t('每单限购')" maxlength="8" />
                                            <el-input v-model.trim="batchMemberLimitBuy" clearable class="!w-[110px] ml-[10px] mb-[10px]" :placeholder="t('每人限购')" maxlength="8" />

                                            <el-button class="ml-[10px] mb-[10px]" type="primary" @click="saveBatch">{{ t("confirm") }}</el-button>
                                        </div>

                                        <el-table :data="goodsTable.data" size="large"  ref="goodsInfoTableRef" @selection-change="tableHandleSelectionChange">
                                            <el-table-column type="selection" width="55" />
                                            <el-table-column prop="nickname" :label="t('商品信息')" min-width="200" fixed="left">
                                                <template #default="{ row }">
                                                    <div class="flex items-center">
                                                        <div class="mr-[10px] rounded-full w-[50px] h-[50px] flex items-center justify-center">
                                                            <el-image v-if="formData.goods_data.cover || row.sku_image" :src="img(row.sku_image ? row.sku_image : formData.goods_data.cover)" fit="cover" class="w-[50px] h-[50px]" >
                                                                <template #error>
                                                                    <img class="w-[50px] h-[50px]" src="@/addon/pintuan/assets/goods_default.png" alt="">
                                                                </template>
                                                            </el-image>
                                                            <img class="w-[50px] h-[50px]" v-else src="@/addon/pintuan/assets/goods_default.png" alt="">
                                                        </div>
                                                        <div class="flex flex-col items-start cursor-pointer">
                                                            <span class="using-hidden"  :title="row.sku_name ? formData.goods_data.title + ' ' + row.sku_name : formData.goods_data.title">{{ row.sku_name ? formData.goods_data.title + " " + row.sku_name: formData.goods_data.title}}</span>
                                                            <span v-if="row.sku_name">{{ row.sku_name }}</span>
                                                            <span  v-if="row.supply_id">供货商：{{ row.supply_name }}</span>
                                                        </div>
                                                    </div>
                                                </template>
                                            </el-table-column>
                                            <el-table-column prop="original" :label="t('供货价')" min-width="120" v-if="formData.goods_data.supply_id">
                                                <template #default="{ row }">
                                                    <el-form-item class="sku-form-item-wrap">
                                                        <div>{{ row.price_config.cost_price }}</div>
                                                    </el-form-item>
                                                </template>
                                            </el-table-column>
                                            <el-table-column prop="original" :label="t('原价')" min-width="120">
                                                <template #default="{ row }">
                                                    <el-form-item class="sku-form-item-wrap">
                                                        <div>{{ row.price_config.original }}</div>
                                                    </el-form-item>
                                                </template>
                                            </el-table-column>
                                            <el-table-column prop="pintuan" :label="t('拼团价')" min-width="120" v-if="formData.type != 'ladder'">
                                                <template #default="{ row }">
                                                    <el-form-item class="sku-form-item-wrap"
                                                        :prop="`goods_data.sku_list[${row.index}].price_config.pintuan`" :rules="[
                                                            {
                                                                required: true,
                                                                validator: (rule, value, callback) => {
                                                                    if(row.is_join){
                                                                        if (value === '' || value === null || value === undefined) {
                                                                            callback(t('请输入拼团价'))
                                                                        } else if (isNaN(value) && !regExp.digit.test(value)) {
                                                                            callback((t('格式不正确')));
                                                                        } else if (value <= 0) {
                                                                            callback(t('拼团价不能小于等于0'))
                                                                        } else if (Number(value) > Number(row.price_config.original)) {
                                                                            callback(t('拼团价不能大于原价'))
                                                                        } else if((Number(value) < Number(row.price_config.cost_price)) && row.supply_id){
                                                                            callback(t('拼团价不能小于供货价'))
                                                                        } else {
                                                                            callback()
                                                                        }
                                                                    } else {
                                                                        callback()
                                                                    }
                                                                },
                                                                trigger: ['blur', 'change']
                                                            }
                                                        ]">
                                                        <el-input :disabled=" !row.is_join" type="number" v-model.trim="row.price_config.pintuan" maxlength="8" />
                                                    </el-form-item>
                                                </template>
                                            </el-table-column>
                                            <el-table-column prop="leader" :label="t('团长价')" min-width="120" v-if="formData.leader_discount == 'on'">
                                                <template #default="{ row }">
                                                    <el-form-item class="sku-form-item-wrap"
                                                        :prop="`goods_data.sku_list[${row.index}].price_config.leader`" :rules="[
                                                            {
                                                                required: true,
                                                                validator: (rule, value, callback) => {
                                                                    if(row.is_join){
                                                                        if (value === '' || value === null || value === undefined) {
                                                                            return callback(t('请输入团长价'))
                                                                        }
                                                                        if (isNaN(value) || !regExp.digit.test(value)) {
                                                                            return callback(t('格式不正确'))
                                                                        }
                                                                        if (Number(value) <= 0) {
                                                                            return callback(t('团长价不能小于等于0'))
                                                                        }
                                                                        if((Number(value) < Number(row.price_config.cost_price)) && row.supply_id){
                                                                            callback(t('团长价不能小于供货价'))
                                                                        }

                                                                        // 普通拼团模式校验
                                                                        if (formData.type !== 'ladder' && row.price_config.pintuan) {
                                                                            const pintuanPrice = Number(row.price_config.pintuan);
                                                                            const leaderPrice = Number(value);
                                                                            if (leaderPrice > pintuanPrice) {
                                                                                return callback(t('团长价不能大于拼团价'))
                                                                            }
                                                                        }

                                                                        // 阶梯拼团模式校验
                                                                        if (formData.type === 'ladder' && row.price_config.ladder) {
                                                                            const ladderPrices = row.price_config.ladder
                                                                                .filter((item: any) => item && item.price && Number(item.price) > 0)
                                                                                .map((item: any) => Number(item.price));

                                                                            if (ladderPrices.length > 0) {
                                                                                const lastLadderPrice = ladderPrices[ladderPrices.length - 1];
                                                                                if (Number(value) > lastLadderPrice) {
                                                                                    return callback(t(`团长价不能高于阶梯${ladderPrices.length}`))
                                                                                }
                                                                            } else {
                                                                                // return callback(t('请先填写阶梯价'))
                                                                            }
                                                                        }

                                                                        callback()
                                                                    } else {
                                                                        callback()
                                                                    }
                                                                },
                                                                trigger: ['blur', 'change']
                                                            }
                                                        ]">
                                                        <el-input :disabled=" !row.is_join" type="number" v-model.trim="row.price_config.leader" maxlength="8" />
                                                    </el-form-item>
                                                </template>
                                            </el-table-column>
                                            <!-- 阶梯1 -->
                                            <el-table-column prop="ladder" :label="t('阶梯1')" min-width="120" v-if="formData.type === 'ladder' && formData.member_num_config.ladder.length >= 1">
                                                <template #default="{ row }">
                                                    <el-form-item v-if="row.price_config.ladder[0]" class="sku-form-item-wrap"
                                                        :prop="`goods_data.sku_list[${row.index}].price_config.ladder[0].price`"
                                                        :rules="[
                                                            {
                                                                required: true,
                                                                validator: (rule, value, callback) => {
                                                                    if(row.is_join){
                                                                        if (value === '' || value === null) {
                                                                            callback(t('请输入阶梯1'))
                                                                        } else if (isNaN(value) && !regExp.digit.test(value)) {
                                                                            callback(t('阶梯1格式不正确'))
                                                                        }else if (Number(value) > Number(row.price_config.original)) {
                                                                            callback(t('阶梯1不能大于原价'))
                                                                        } else if (value <= 0) {
                                                                            callback(t('阶梯1不能小于等于0'))
                                                                        } else if((Number(value) < Number(row.price_config.cost_price)) && row.supply_id){
                                                                            callback(t('阶梯1不能小于供货价'))
                                                                        } else {
                                                                            // 检查是否有后续阶梯，触发它们的验证
                                                                            if (row.price_config.ladder.length > 1) {
                                                                                validateLadderPrice(row.index, 1);
                                                                            }
                                                                            callback()
                                                                        }
                                                                    }else{
                                                                            callback()
                                                                    }
                                                                },
                                                                trigger: ['blur', 'change']
                                                            }
                                                        ]">
                                                        <el-input :disabled="!row.is_join" type="number" v-model.trim="row.price_config.ladder[0].price" maxlength="8" />
                                                    </el-form-item>
                                                </template>
                                            </el-table-column>

                                            <!-- 阶梯2 -->
                                            <el-table-column prop="ladder" :label="t('阶梯2')" min-width="120" v-if="formData.type === 'ladder' && formData.member_num_config.ladder.length >= 2">
                                                <template #default="{ row }">
                                                    <el-form-item v-if="row.price_config.ladder[1]" class="sku-form-item-wrap"
                                                        :prop="`goods_data.sku_list[${row.index}].price_config.ladder[1].price`"
                                                        :rules="[
                                                            {
                                                                required: true,
                                                                validator: (rule, value, callback) => {
                                                                    if(row.is_join){
                                                                        if (value === '' || value === null) {
                                                                            callback(t('请输入阶梯2'));
                                                                        } else if (isNaN(value) && !regExp.digit.test(value)) {
                                                                            callback(t('阶梯2格式不正确'));
                                                                        }else if (Number(value) > Number(row.price_config.original)) {
                                                                            callback(t('阶梯2不能大于原价'))
                                                                        } else if (value <= 0) {
                                                                            callback(t('阶梯2不能小于等于0'));
                                                                        } else if((Number(value) < Number(row.price_config.cost_price)) && row.supply_id){
                                                                            callback(t('阶梯2不能小于供货价'))
                                                                        } else if (Number(value) >= Number(row.price_config.ladder[0].price)) {
                                                                            // 核心验证：阶梯2必须小于阶梯1
                                                                            callback(t('阶梯2必须小于阶梯1'));
                                                                        } else {
                                                                            // callback(t('请先填写阶梯价'))
                                                                            callback()
                                                                        }
                                                                    }else{
                                                                        callback()
                                                                    }

                                                                },
                                                                trigger: ['blur', 'change']
                                                            }
                                                        ]">
                                                        <el-input :disabled="!row.is_join" type="number" v-model.trim="row.price_config.ladder[1].price" maxlength="8" />
                                                    </el-form-item>
                                                </template>
                                            </el-table-column>

                                            <!-- 阶梯3 -->
                                            <el-table-column prop="ladder" :label="t('阶梯3')" min-width="120" v-if="formData.type === 'ladder' && formData.member_num_config.ladder.length >= 3">
                                                <template #default="{ row }">
                                                    <el-form-item v-if="row.price_config.ladder[2]" class="sku-form-item-wrap"
                                                        :prop="`goods_data.sku_list[${row.index}].price_config.ladder[2].price`"
                                                        :rules="[
                                                            {
                                                                required: true,
                                                                validator: (rule, value, callback) => {
                                                                    if(row.is_join){
                                                                        if (value === '' || value === null) {
                                                                            callback(t('请输入阶梯3'));
                                                                        } else if (isNaN(value) && !regExp.digit.test(value)) {
                                                                            callback(t('阶梯3格式不正确'));
                                                                        } else if (Number(value) > Number(row.price_config.original)) {
                                                                            callback(t('阶梯3不能大于原价'))
                                                                        }else if (value <= 0) {
                                                                            callback(t('阶梯3不能小于等于0'));
                                                                        } else if((Number(value) < Number(row.price_config.cost_price)) && row.supply_id){
                                                                            callback(t('阶梯3不能小于供货价'))
                                                                        } else if (Number(value) >= Number(row.price_config.ladder[1].price)) {
                                                                            // 核心验证：阶梯3必须小于阶梯2
                                                                            callback(t('阶梯3必须小于阶梯2'));
                                                                        } else {
                                                                            callback();
                                                                        }
                                                                    }else{
                                                                        callback();
                                                                    }

                                                                },
                                                                trigger: ['blur', 'change']
                                                            }
                                                        ]">
                                                        <el-input :disabled="!row.is_join" type="number" v-model.trim="row.price_config.ladder[2].price" maxlength="8" />
                                                    </el-form-item>
                                                </template>
                                            </el-table-column>
                                            <el-table-column prop="stock" :label="t('库存')" min-width="120">
                                                <template #default="{ row }">
                                                    <el-form-item class="sku-form-item-wrap">
                                                        <div>{{ row.stock }}</div>
                                                    </el-form-item>
                                                </template>
                                            </el-table-column>
                                            <el-table-column prop="order_limit_buy"  min-width="120">
                                                <template #header>
                                                    <div style="display: inline-flex; align-items: center">
                                                        <span class="mr-[5px]">{{ t('每单限购') }}</span>
                                                        <el-tooltip class="box-item" effect="light" :content="t('拼团场景下，单次下单时可购买的商品数量将受到限制；若选择单独购买（非拼团），则不受此数量限制')" placement="top">
                                                            <el-icon color="#666">
                                                                <QuestionFilled />
                                                            </el-icon>
                                                        </el-tooltip>
                                                    </div>
                                                </template>
                                                <template #default="{ row }">
                                                    <el-form-item class="sku-form-item-wrap"
                                                        :prop="`goods_data.sku_list[${row.index}].order_limit_buy`" :rules="[
                                                            {
                                                                trigger: 'blur',
                                                                validator: (rule, value, callback) => {
                                                                    if(row.is_join){
                                                                        if (value === '' || value === null || value === undefined) {
                                                                            callback();
                                                                        } else if (isNaN(value) && !regExp.number.test(value)) {
                                                                            callback(t('每单限购格式不正确'));
                                                                        } else if (value < 0) {
                                                                            callback(t('每单限购不能小于0'));
                                                                        }else if( row.member_limit_buy && Number(row.member_limit_buy) < Number(value)){
                                                                            callback(t('每单限购不能大于每人限购'));
                                                                        } else {
                                                                            callback();
                                                                        }
                                                                    }else{
                                                                        callback();
                                                                    }

                                                                }
                                                            }
                                                        ]">
                                                        <el-input :disabled="!row.is_join" type="number" v-model.trim="row.order_limit_buy" maxlength="8" />
                                                    </el-form-item>
                                                </template>
                                            </el-table-column>
                                            <el-table-column prop="member_limit_buy" min-width="120">
                                                <template #header>
                                                    <div style="display: inline-flex; align-items: center">
                                                        <span class="mr-[5px]">{{ t('每人限购') }}</span>
                                                        <el-tooltip class="box-item" effect="light" :content="t('用户针对本活动的总购买数量将受到限制（累计计算）；若选择单独购买（非拼团），则不受此总数量限制')" placement="top">
                                                            <el-icon color="#666">
                                                                <QuestionFilled />
                                                            </el-icon>
                                                        </el-tooltip>
                                                    </div>
                                                </template>
                                                <template #default="{ row }">
                                                    <el-form-item class="sku-form-item-wrap"
                                                        :prop="`goods_data.sku_list[${row.index}].member_limit_buy`" :rules="[
                                                            {
                                                                trigger: 'blur',
                                                                validator: (rule, value, callback) => {
                                                                    if(row.is_join){
                                                                        if (value === '' || value === null || value === undefined) {
                                                                            callback();
                                                                        } else if (isNaN(value) && !regExp.number.test(value)) {
                                                                            callback(t('每人限购格式不正确'));
                                                                        } else if (value < 0) {
                                                                            callback(t('每人限购不能小于0'));
                                                                        }else if( row.order_limit_buy && Number(value) <  Number(row.order_limit_buy)){
                                                                            callback(t('每人限购不能小于每单限购'));
                                                                        } else {
                                                                            callback();
                                                                        }
                                                                    }else{
                                                                        callback();
                                                                    }

                                                                }
                                                            }
                                                        ]">
                                                        <el-input :disabled="!row.is_join" type="number" v-model.trim="row.member_limit_buy" maxlength="8" />
                                                    </el-form-item>
                                                </template>
                                            </el-table-column>
                                            <el-table-column :label="t('是否默认')" fixed="right" min-width="100">
                                                <template #default="{ row, $index }">
                                                    <div class="flex flex-center">
                                                        <el-switch v-model="row.is_default" :active-value="1" :disabled="row.spec_type=='single'" :inactive-value="0" @change="specValueIsDefaultChangeListener($event, row.index)" />
                                                    </div>
                                                </template>
                                            </el-table-column>
                                            <el-table-column :label="t('是否参与')" fixed="right" min-width="100">
                                                <template #default="{ row, $index }">
                                                    <div class="flex flex-center">
                                                        <el-switch v-model="row.is_join" :active-value="1" :inactive-value="0" :disabled="row.is_default==1 || row.spec_type=='single'" />
                                                    </div>
                                                </template>
                                            </el-table-column>
                                        </el-table>
                                    </div>
                                </div>
                            </div>
                        </el-form-item>
                        <!-- 虚拟成团 -->
                        <el-form-item :label="t('虚拟成团')" prop="virtual_mode">
                            <div>
                                <el-radio-group v-model="formData.virtual_mode">
                                    <el-radio label="on">{{ t('是') }}</el-radio>
                                    <el-radio label="off">{{ t('否') }}</el-radio>
                                </el-radio-group>
                                <div class="mt-[10px] text-[12px] text-[#999] leading-[20px]">开启后，若团组或活动到期时未达到成团人数，系统将自动为未满员的团组完成成团，保障用户权益不受影响。</div>
                            </div>

                        </el-form-item>
                        <el-form-item :label="t('poster')">
                            <el-select v-model="formData.poster_id" :placeholder="t('posterPlaceholder')" clearable>
                                <el-option v-for="item in posterOptions" :key="item.id" :label="item.name" :value="item.id" />
                            </el-select>
                            <div class="ml-[10px]">
                                <span class="cursor-pointer text-primary mr-[10px]" @click="refreshGoodsPoster(true)">{{ t('refresh') }}</span>
                            </div>
                        </el-form-item>
                        <div class="ml-[120px] mb-[10px] text-[12px] text-[#999] leading-[20px]">{{ t('posterTips') }}</div>
                        <el-form-item :label="t('diyForm')">
                            <el-select v-model="formData.form_id" :placeholder="t('diyFormPlaceholder')" clearable>
                                <el-option v-for="item in diyFormOptions" :key="item.form_id" :label="item.page_title" :value="item.form_id" />
                            </el-select>
                            <div class="ml-[10px]">
                                <span class="cursor-pointer text-primary mr-[10px]" @click="refreshDiyForm(true)">{{ t('refresh') }}</span>
                                <span class="cursor-pointer text-primary" @click="toDiyFormEvent">{{ t('addDiyForm') }}</span>
                            </div>
                        </el-form-item>
                        <el-form-item label="商品详情模板">
                            <el-select v-model="formData.goods_data.diy_detail_id" placeholder="请选择商品详情模板" clearable>
                                <el-option v-for="item in detailTemplateOptions" :key="item.id" :label="item.page_title" :value="item.id" />
                            </el-select>
                        </el-form-item>
                    </el-form>
                </el-tab-pane>
                <el-tab-pane :label="t('分享设置')" name="setShareConfig">
                    <el-form :model="formData" label-width="120px" ref="setShareConfigFormRef" :rules="formRules" scroll-to-error class="page-form">
                        <h3 class="panel-title !text-sm pl-[15px]">{{ t('拼团分享设置(有团)') }}</h3>
                        <el-form-item :label="t('分享标题')" prop="share_config">
                            <div>
                                <el-input v-model.trim="formData.share_config.group.title" clearable class="!w-[500px]" :placeholder="t('请输入分享标题')" maxlength="100" show-word-limit />
                                <el-button class="ml-[20px]" type="primary" @click="defaultActiveTitleEvent('groupTitle')" plain>{{ t('使用默认标题') }}</el-button>
                            </div>
                        </el-form-item>
                        <div class="my-[10px] ml-[120px] text-[14px]">
                            <span>{{ t('点击添加数据') }}</span>
                            <span class="ml-[10px] text-primary cursor-pointer" v-for="(option, index) in addgroupableDataOptions" :key="index" @click="addgroupDataToTitle(option)">
                                {{ option }}
                            </span>
                        </div>
                        <el-form-item :label="t('分享内容')" prop="desc">
                            <div>
                                <el-input v-model="formData.share_config.group.desc" :placeholder="t('请输入分享内容')" type="textarea" maxlength="500" show-word-limit rows="5" class="!w-[500px]" clearable />
                                <el-button class="ml-[20px]" type="primary" @click="defaultActiveTitleEvent('groupDesc')" plain>{{ t('使用默认内容')}}</el-button>
                            </div>
                        </el-form-item>
                        <div class="my-[10px] ml-[120px] text-[14px]">
                            <span>{{ t('点击添加数据') }}</span>
                            <span class="ml-[10px] text-primary cursor-pointer" v-for="(option, index) in addgroupableDataOptions" :key="index" @click="addgroupDataToDesc(option)"> {{ option }} </span>
                        </div>
                        <h3 class="panel-title !text-sm pl-[15px]">{{ t('拼团分享设置(普通)') }}</h3>
                        <el-form-item :label="t('分享标题')" prop="share_config">
                            <div>
                                <el-input v-model.trim="formData.share_config.normal.title" clearable class="!w-[500px]" :placeholder="t('请输入分享标题')" maxlength="100" show-word-limit />
                                <el-button class="ml-[20px]" type="primary" @click="defaultActiveTitleEvent('normalTitle')" plain>{{t('使用默认标题')}}</el-button>
                            </div>

                        </el-form-item>
                        <div class="my-[10px] ml-[120px] text-[14px]">
                            <span>{{ t('点击添加数据') }}</span>
                            <span class="ml-[10px] text-primary cursor-pointer" v-for="(option, index) in addnormalableDataOptions" :key="index"  @click="addnormalDataToTitle(option)">
                                {{ option }}
                            </span>
                        </div>
                        <el-form-item :label="t('分享内容')" prop="desc">
                            <div>
                                <el-input v-model="formData.share_config.normal.desc" :placeholder="t('请输入分享内容')" type="textarea" maxlength="500" show-word-limit rows="5" class="!w-[500px]" clearable />
                                <el-button class="ml-[20px]" type="primary" @click="defaultActiveTitleEvent('normalDesc')" plain>{{ t('使用默认内容') }}</el-button>
                            </div>
                        </el-form-item>
                        <div class="my-[10px] ml-[120px] text-[14px]">
                            <span>{{ t('点击添加数据') }}</span>
                            <span class="ml-[10px] text-primary cursor-pointer" v-for="(option, index) in addnormalableDataOptions" :key="index"  @click="addnormalDataToDesc(option)">
                                {{ option }}
                            </span>
                        </div>
                    </el-form>
                </el-tab-pane>
                <el-tab-pane label="活动配置" name="acitveConfig">
                    <el-form ref="acitveConfigFormRef" :model="formData" label-width="120px" :rules="formRules"  scroll-to-error class="page-form">
                        <el-form-item :label="t('背景音乐')" prop="bg_music_type">
                            <!-- 背景音乐级联选择 -->
                            <el-cascader v-model="musicSelected" :options="musicOptions" placeholder="选择背景音乐" @change="handleMusicChange" clearable class="!w-[360px]"></el-cascader>
                        </el-form-item>
                        <el-form-item :label="t('背景音乐地址')" prop="bg_music_url">
                            <div class="flex">
                                <el-input v-model.trim="formData.bg_music_url" clearable :placeholder="t('在此输入背景音乐地址')" class="!w-[360px] mr-[10px]" />
                                <upload-audio v-model="formData.bg_music_url" :limit="1" />
                            </div>
                        </el-form-item>
                        <!-- :disabled="isProhibitEdit" -->
                        <el-form-item :label="t('背景特效')" prop="bg_effect_type">
                            <el-cascader v-model="effectSelected" :options="effectOptions" placeholder="选择背景特效" @change="handleEffectChange" clearable class="!w-[360px]"></el-cascader>
                        </el-form-item>
                    </el-form>
                </el-tab-pane>
                <el-tab-pane label="购买须知" name="rule">
                    <editor v-model="formData.desc" :height="600" class="editor-width" @handleBlur="handleBlur" />
                </el-tab-pane>
            </el-tabs>
        </el-card>
        <!-- 提交按钮 -->
        <div class="fixed-footer-wrap">
            <div class="fixed-footer">
                <el-button type="primary" @click="onSave()">{{ t("save") }}</el-button>
                <el-button @click="back()">{{ t("back") }}</el-button>
            </div>
        </div>
        <import-goods-popup ref="importGoodsPopupRef" @confirm="importConfirm" />
    </div>
</template>
<script lang="ts" setup>
import { ref, reactive, computed, nextTick, watch } from 'vue'
import { t } from '@/lang'
import { useRoute, useRouter } from 'vue-router'
import { editPintuan, addPintuan, getPintuanDetail, getPintuanShare, getGoodsInfoTemplate, getPintuanInit } from '@/addon/pintuan/api/site/active'
import { getPintuanShareInit } from '@/addon/pintuan/api/site/config'
import { FormInstance, ElMessage } from 'element-plus'
import selectMember from '@/addon/pintuan/views/site/active/components/select-member.vue'
import importGoodsPopup from '@/addon/pintuan/views/site/active/components/import-goods-popup.vue'
import { ArrowLeft } from '@element-plus/icons-vue'
import { getMemberLabelAll, getMemberLevelAll } from '@/app/api/member'
import { filterNumber, img, getCurrentDataTime } from '@/utils/common'
import { getPosterList } from '@/app/api/poster'
import { getDiyFormList } from '@/app/api/diy_form_site'
import { cloneDeep } from 'lodash'

const router = useRouter()
const route = useRoute()
const activeTabName = ref('basic')
const setShareConfigFormRef = ref()
const goodsInfoFormRef = ref()
const basicFormRef = ref()
const acitveConfigFormRef = ref()
const id = route.query.id
const loading = ref(false)
const initialFormData = reactive({
    title: '', // 名称
    desc: '',
    time: [],
    start_time: '',
    end_time: '',
    banner: '',
    source: 'pintuan',
    can_join_directly: 1,
    type: 'normal', // 拼团类型 normal 普通 ladder 阶梯 referral 老带新
    member_num_config: {
        normal: 2, // 普通
        ladder: [
            {
                ladder: 1, // 阶梯
                member_num: '' // 成团人数
            }
        ], // 阶梯
        referral: 2 // 老带新
    }, // 拼团人数配置
    virtual_mode: 'off', // 虚拟成团
    leader_discount: 'off', // 团长优惠
    valid_duration: '', // 有效期
    goods: '',
    goods_ids: [],
    goods_data: {

    },
    member_rule: 'all', // all 全部 member_level 会员等级 member_label 会员标签 member 会员
    member_level_id: [],
    member_label_id: [],
    member_ids: [], // 会员id
    member_rule_config: [],
    member_data: [],
    share_config: {
        group: {
            title: '',
            desc: ''
        },
        normal: {
            title: '',
            desc: ''
        }
    },
    poster_id: '',
    form_id: '',
    bg_music_type: '',
    bg_music_child_type: '',
    bg_effect_type: '',
    bg_effect_child_type: '',
    bg_music_url: '',
    expense_rate: '',
})

const tabHandleClick = (tab: any, event: any) => {
    activeTabName.value = tab.props.name
}
const importGoodsPopupRef: Record<string, any> | null = ref(null)
const importEvent = () => {
    importGoodsPopupRef.value.show()
}
// 导入商品
const importConfirm = (data: any) => {
    formData.goods_data = data.data
    formData.source = formData.goods_data.source
    formData.goods_data.sku_list.forEach((item: any) => {
        // 1. 定义默认的 ladder 结构（3个等级）
        const defaultLadder = [
            { ladder: 1, price: '' },
            { ladder: 2, price: '' },
            { ladder: 3, price: '' }
        ]

        // 2. 处理现有 ladder 数据（如果有）
        let existingLadder = []
        if (item.price_config.ladder && Array.isArray(item.price_config.ladder)) {
            // 过滤有效数据（只保留有 ladder 和 price 的项）
            existingLadder = item.price_config.ladder.filter(ladderItem =>
                ladderItem && typeof ladderItem.ladder === 'number'
            )
        }

        // 3. 合并：用现有数据覆盖默认结构（有几个覆盖几个，剩下的用默认）
        const mergedLadder = defaultLadder.map((defaultItem, index) => {
            // 如果现有数据有对应索引的项，就合并（优先用现有 price）
            if (existingLadder[index]) {
                return { ...defaultItem, ...existingLadder[index] }
            }
            // 没有则用默认项
            return defaultItem
        })

        // 4. 最终赋值
        item.price_config = {
            cost_price: item.price_config.cost_price || '',
            original: item.price_config.original || '',
            pintuan: item.price_config.pintuan || '',
            leader: item.price_config.leader || '',
            ladder: mergedLadder // 使用合并后的数组
        }
        item.member_limit_buy = item.member_limit_buy || '' // 会员限购
        item.order_limit_buy = item.order_limit_buy || '' // 订单限购
        item.supply_name = formData.goods_data.supply_name || '' // 供货商
    })
    setGoodsList()
    goodsInfoFormRef.value?.validateField('goods_data')
}

const verify = (callback: any) => {
    let formRef = [
        {
            key: 'basic',
            verify: false,
            ref: basicFormRef.value
        },
        {
            key: 'setShareConfig',
            verify: false,
            ref: setShareConfigFormRef.value
        },
        {
            key: 'goodsInfo',
            verify: false,
            ref: goodsInfoFormRef.value
        },
        {
            key: 'acitveConfig',
            verify: false,
            ref: acitveConfigFormRef.value
        }
    ]

    // 等待每个表单都验证完成
    formRef.forEach((el: any, index) => {
        el.ref.validate((valid: any) => {
            el.verify = valid
        })
    })
    setTimeout(() => {
        let verify = true
        // 检测验证，并且定位tab页面
        for (let i = 0; i < formRef.length; i++) {
            if (formRef[i].verify == false) {
                activeTabName.value = formRef[i].key
                verify = false
                break
            }
        }
        if (verify && callback) callback()
    }, 10)
}

// 分享设置
const addgroupableDataOptions = ref([])
const addnormalableDataOptions = ref([])
const getPintuanShareKeywordFn = () => {
    getPintuanShare().then((res: any) => {
        if (res.data) {
            addgroupableDataOptions.value = res.data.group
            addnormalableDataOptions.value = res.data.normal
        }
    })
}
const defaultData = ref({})
const getPintuanShareInitFn = () => {
    getPintuanShareInit().then(res => {
        defaultData.value = res.data
    })
}
getPintuanShareInitFn()

getPintuanShareKeywordFn()
/**
 * 使用默认说明
 */
const defaultActiveTitleEvent = (type:any) => {
    if (type == 'groupTitle') {
        formData.share_config.group.title = defaultData.value.group_title
    } else if (type == 'normalTitle') {
        formData.share_config.normal.title = defaultData.value.normal_title
    } else if (type == 'groupDesc') {
        formData.share_config.group.desc = defaultData.value.group_desc
    } else if (type == 'normalDesc') {
        formData.share_config.normal.desc = defaultData.value.normal_desc
    }
}
const addgroupDataToTitle = (option: string) => {
    formData.share_config.group.title = formData.share_config.group.title += `{{${option}}}`
}
const addgroupDataToDesc = (option: string) => {
    formData.share_config.group.desc = formData.share_config.group.desc += `{{${option}}}`
}
const addnormalDataToTitle = (option: string) => {
    formData.share_config.normal.title = formData.share_config.normal.title += `{{${option}}}`
}
const addnormalDataToDesc = (option: string) => {
    formData.share_config.normal.desc = formData.share_config.normal.desc += `{{${option}}}`
}


// 海报列表下拉框
const posterOptions = reactive([])

// 商品海报
const refreshGoodsPoster = (bool = false) => {
    getPosterList({
        type: 'pintuan_goods'
    }).then((res) => {
        const data = res.data
        if (data) {
            posterOptions.splice(0, posterOptions.length, ...data)
            if (bool) {
                ElMessage({
                    message: t('refreshSuccess'),
                    type: 'success'
                })
            }
        }
    })
}

refreshGoodsPoster()

// 万能表单列表下拉框
const diyFormOptions = reactive([])

// 跳转到万能表单列表，添加表单
const toDiyFormEvent = () => {
    const url = router.resolve({
        path: '/diy_form/list'
    })
    window.open(url.href)
}

// 刷新万能表单
const refreshDiyForm = (bool = false) => {
    getDiyFormList({
        type: 'DIY_FROM_PINTUAN_GOODS',
        status: 1
    }).then((res) => {
        const data = res.data
        if (data) {
            diyFormOptions.splice(0, diyFormOptions.length, ...data)
            if (bool) {
                ElMessage({
                    message: t('refreshSuccess'),
                    type: 'success'
                })
            }
        }
    })
}

refreshDiyForm()

/** ***************** 商品详情模板-start *************************/
// 商品详情模板列表下拉框
const detailTemplateOptions = reactive([])

// 刷新微页面
const refreshDetailTemplate = (bool = false) => {
    getGoodsInfoTemplate({
        type: ['DIY_PINTUAN_GOODS_DETAIL', 'DIY_PINTUAN_ACTIVITY_GOODS_DETAIL']
    }).then((res) => {
        const data = res.data
        if (data) {
            detailTemplateOptions.splice(0, detailTemplateOptions.length, ...data)
            if (bool) {
                ElMessage({
                    message: t('refreshSuccess'),
                    type: 'success'
                })
            }
        }
    })
}

refreshDetailTemplate()
/** *****************商品详情模板-end *************************/

/** ************ 会员-start *********************/
const selectMemberRef = ref<any>(null)

const memberList = ref<any>([])
// 选择会员
const memberSelect = (value: any) => {
    formData.member_data = Object.values(value)
    formRef.value?.validateField('member_ids')
}

// 删除会员
const deleteMemberEvent = (row: any, index: number) => {
    formData.member_data.splice(index, 1)
    // 找到并删除对应的 member_id
    const idIndex = formData.member_ids.indexOf(row.member_id)
    if (idIndex !== -1) {
        formData.member_ids.splice(idIndex, 1)
    }
}

/************** 会员-end *********************/

const duration = reactive({
    days: 0, // 天
    hours: 0, // 时
    minutes: 0 // 分
})
// 选项数据
const dayOptions = computed(() => Array.from({ length: 31 }, (_, i) => i)) // 0-30天
const hourOptions = computed(() => Array.from({ length: 24 }, (_, i) => i)) // 0-23时
const minuteOptions = computed(() => Array.from({ length: 60 }, (_, i) => i)) // 0-59分

// 计算总秒数
const calculateTotalSeconds = () => {
    const totalSeconds =
        (duration.days * 86400) +
        (duration.hours * 3600) +
        (duration.minutes * 60)

    formData.valid_duration = Math.max(60, totalSeconds) // 至少1分钟
    nextTick(() => {
        basicFormRef.value?.validateField('valid_duration')
    })
}

// 将总秒数转换为天/时/分并直接更新duration对象
const convertSecondsToUnits = (seconds) => {
    const days = Math.floor(seconds / 86400)
    const remainingSeconds = seconds % 86400
    const hours = Math.floor(remainingSeconds / 3600)
    const minutes = Math.floor((remainingSeconds % 3600) / 60)

    // 直接更新duration对象
    duration.days = days
    duration.hours = hours
    duration.minutes = minutes
}

const goodsTable = reactive<any>({
    data: []
})

const formData: Record<string, any> = reactive({ ...initialFormData })
const formRef = ref<FormInstance>()
const isProhibitEdit = ref(false) // 禁止编辑
const addLadder = () => {
    formData.member_num_config.ladder.push({
        ladder: formData.member_num_config.ladder.length + 1, // 阶梯
        member_num: '' // 成团人数
    })
    if (Object.keys(formData.goods_data).length != 0 && formData.goods_data?.sku_list.length > 0) {
        formData.goods_data.sku_list.forEach((item: any) => {
            item.price_config.ladder.push({
                ladder: formData.member_num_config.ladder.length, // 阶梯
                price: 0 // 成团价格
            })
        })
    }
}
const removeLadder = (index: number) => {
    formData.member_num_config.ladder.splice(index, 1)
    if (Object.keys(formData.goods_data).length != 0 && formData.goods_data?.sku_list.length > 0) {
        formData.goods_data.sku_list.forEach((item: any) => {
            item.price_config.ladder.splice(index, 1)
        })
    }
}

/**************** 批量操作 - start ************************/
// 完整数据转分页数据
const splitArray = (array: [], size: number) => {
    const result = []
    for (let i = 0; i < array.length; i += size) {
        result.push(array.slice(i, i + size))
    }
    return result
}
// 设置展示商品
const setGoodsList = (page = 1) => {
    if (Object.keys(formData.goods_data).length != 0 && formData.goods_data?.sku_list.length) {
        formData.goods_data.sku_list.forEach((item: any, index: number) => {
            item.index = index
            item.spec_type = formData.goods_data.spec_type
        })
        goodsTable.data = formData.goods_data.sku_list
    } else {
        goodsTable.data = []
    }
}
// 设置默认规格
const specValueIsDefaultChangeListener = (value: any, key: any) => {
    for (const k in formData.goods_data.sku_list) {
        if (k == key) {
            formData.goods_data.sku_list[k].is_default = value
            formData.goods_data.sku_list[k].is_join = 1
        } else {
            formData.goods_data.sku_list[k].is_default = 0
        }
    }
}
// 批量复选框
const toggleCheckbox = ref()
const batchPintuanPrice = ref('') // 批量拼团价
const batchLeaderPrice = ref('') // 批量团长价
const batchLadderPrice1 = ref('') // 批量阶梯价格
const batchLadderPrice2 = ref('') // 批量阶梯价格
const batchLadderPrice3 = ref('') // 批量阶梯价格
const batchMemberLimitBuy = ref('') // 批量限购
const batchOrderLimitBuy = ref('') // 批量订单限购
// 批量设置原价

const goodsInfoTableRef = ref()

// 复选框中间状态
const isIndeterminate = ref(false)

// 监听批量复选框事件
const toggleChange = (value: any) => {
    isIndeterminate.value = false
    goodsInfoTableRef.value?.toggleAllSelection()
}
// 选中数据
const multipleSelection: any = ref([])
// 监听表格单行选中
const tableHandleSelectionChange = (val: []) => {
    multipleSelection.value = val

    toggleCheckbox.value = false
    if (multipleSelection.value.length > 0 && multipleSelection.value.length < goodsTable.data.length) {
        isIndeterminate.value = true
    } else {
        isIndeterminate.value = false
    }

    if (multipleSelection.value.length == goodsTable.data.length) {
        toggleCheckbox.value = true
    }
}

const digit = /^\d{0,10}(.?\d{0,2})$/
const digitTwo = /^\d{0,10}$/

const saveBatch = () => {
    if (!multipleSelection.value.length) {
        ElMessage({
            type: 'warning',
            message: `${t('请选择要操作的商品')}`
        })
        return
    }


    if (batchMemberLimitBuy.value) {
        if (!digitTwo.test(batchMemberLimitBuy.value)) {
            ElMessage({
                type: 'warning',
                message: `${t('每人限购数量格式不正确')}`
            })
            return
        } else if (Number(batchMemberLimitBuy.value) < 0) {
            ElMessage({
                type: 'warning',
                message: `${t('每人限购数量不能小于零')}`
            })
            return
        }
    }
    if (batchPintuanPrice.value) {
        if (!digit.test(batchPintuanPrice.value)) {
            ElMessage({
                type: 'warning',
                message: `${t('拼团价格格式不正确')}`
            })
            return
        } else if (Number(batchPintuanPrice.value) <= 0) {
            ElMessage({
                type: 'warning',
                message: `${t('拼团价格不能小于等于零')}`
            })
            return
        }
    }
    if (batchLeaderPrice.value) {
        if (!digit.test(batchLeaderPrice.value)) {
            ElMessage({
                type: 'warning',
                message: `${t('团长价格格式不正确')}`
            })
            return
        } else if (Number(batchLeaderPrice.value) <= 0) {
            ElMessage({
                type: 'warning',
                message: `${t('团长价格不能小于等于零')}`
            })
            return
        }
    }
    if (batchLadderPrice1.value) {
        if (!digit.test(batchLadderPrice1.value)) {
            ElMessage({
                type: 'warning',
                message: `${t('阶梯1价格格式不正确')}`
            })
            return
        } else if (Number(batchLadderPrice1.value) <= 0) {
            ElMessage({
                type: 'warning',
                message: `${t('阶梯1价格不能小于等于零')}`
            })
            return
        }
    }
    if (batchLadderPrice2.value) {
        if (!digit.test(batchLadderPrice2.value)) {
            ElMessage({
                type: 'warning',
                message: `${t('阶梯2价格格式不正确')}`
            })
            return
        } else if (Number(batchLadderPrice2.value) <= 0) {
            ElMessage({
                type: 'warning',
                message: `${t('阶梯2价格不能小于等于零')}`
            })
            return
        }
    }
    if (batchLadderPrice3.value) {
        if (!digit.test(batchLadderPrice3.value)) {
            ElMessage({
                type: 'warning',
                message: `${t('阶梯3价格格式不正确')}`
            })
            return
        } else if (Number(batchLadderPrice3.value) <= 0) {
            ElMessage({
                type: 'warning',
                message: `${t('阶梯3价格不能小于等于零')}`
            })
            return
        }
    }
    if (batchOrderLimitBuy.value) {
        if (!digitTwo.test(batchOrderLimitBuy.value)) {
            ElMessage({
                type: 'warning',
                message: `${t('每单限购格式不正确')}`
            })
        } else if (Number(batchOrderLimitBuy.value) < 0) {
            ElMessage({
                type: 'warning',
                message: `${t('每单限购不能小于零')}`
            })
        }
    }

    formData.goods_data.sku_list.forEach((el: any, index: any, arr: any) => {
        multipleSelection.value.forEach((v: any) => {
            if (v.index === el.index) {
                el.price_config.pintuan = batchPintuanPrice.value || el.price_config.pintuan
                el.price_config.leader = batchLeaderPrice.value || el.price_config.leader
                if (el.price_config.ladder.length >= 1) el.price_config.ladder[0].price = batchLadderPrice1.value || el.price_config.ladder[0].price
                if (el.price_config.ladder.length >= 2) el.price_config.ladder[1].price = batchLadderPrice2.value || el.price_config.ladder[1].price
                if (el.price_config.ladder.length >= 3) el.price_config.ladder[2].price = batchLadderPrice3.value || el.price_config.ladder[2].price
                el.member_limit_buy = batchMemberLimitBuy.value || el.member_limit_buy
                el.order_limit_buy = batchOrderLimitBuy.value || el.order_limit_buy
            }
        })
    })

    batchPintuanPrice.value = '' // 批量拼团价
    batchLeaderPrice.value = '' // 批量团长价
    batchLadderPrice1.value = '' // 批量阶梯价格
    batchLadderPrice2.value = '' // 批量阶梯价格
    batchLadderPrice3.value = '' // 批量阶梯价格
    batchMemberLimitBuy.value = '' // 批量限购
    batchOrderLimitBuy.value = '' // 批量订单限购
}

/** ************** 批量操作 - end ************************/

// 清除商品
const clearGoods = () => {
    // formData.goods_data = {}
    importGoodsPopupRef.value.show()
    // formData.source = 'pintuan'
}

const levelSelectData = ref([])
const labelSelectData = ref([])
// 原数据
const bgMusicList = ref([])
// 格式化后
const musicSelected = ref([])
const effectSelected = ref([])
const musicOptions = ref([])
const effectOptions = ref([])

const pintuanTypeOptions = ref([])
const memberRuleOptions = ref([])
const getInfo = () => {
    // 获取全部标签
    getMemberLabelAll().then(({ data }) => {
        labelSelectData.value = data
    })

    getMemberLevelAll().then(({ data }) => {
        levelSelectData.value = data
    })
    getPintuanInit().then((res) => {
        bgMusicList.value = res.data.bg_music_list

        musicOptions.value = formatMusicData(res.data.bg_music_list)
        effectOptions.value = formatEffectData(res.data.bg_effect_list)

        pintuanTypeOptions.value = res.data.type_list
        memberRuleOptions.value = res.data.member_rule
    })
}

getInfo()
const formatEffectData = (data: any) => {
    const effectList = data
    return Object.keys(effectList).map(typeKey => {
        const typeItem = effectList[typeKey]

        // 处理二级选项
        const children = typeItem.child_list
            ? Object.keys(typeItem.child_list).map(childKey => ({
                value: childKey,
                label: typeItem.child_list[childKey]
            }))
            : [] // 无特效分类没有子项

        return {
            value: typeKey,
            label: typeItem.title,
            children: children
        }
    })
}
const formatMusicData = (data: any) => {
    const musicList = data
    return Object.keys(musicList).map(typeKey => {
        const typeItem = musicList[typeKey]
        // 处理二级选项（child_list）
        const children = typeItem.child_list
            ? Object.keys(typeItem.child_list).map(childKey => ({
                value: childKey, // 子类型标识（如quiet_guitar_soundtrack）
                label: typeItem.child_list[childKey].title // 子类型显示名（如安静吉他配乐）
            }))
            : []
        return {
            value: typeKey, // 一级类型标识（如popular）
            label: typeItem.title, // 一级类型显示名（如流行）
            children: children // 二级选项
        }
    })
}
const handleEffectChange = (value: any) => {
    if (value == undefined) {
        formData.bg_effect_type = ''
        formData.bg_effect_child_type = ''
        effectSelected.value = []
        return
    }
    if (value.length === 0) {
        // 清空选择
        formData.bg_effect_type = ''
        formData.bg_effect_child_type = ''
        return
    }

    formData.bg_effect_type = value[0]

    if (value[0] === 'normal') {
        // 选择"无特效"，清空子项
        formData.bg_effect_child_type = ''
        effectSelected.value = ['normal'] // 只包含一级分类
    } else {
        // 其他分类需要选择子项
        formData.bg_effect_child_type = value.length > 1 ? value[1] : ''
    }

    // 验证
    basicFormRef.value?.validateField('bg_effect_type')
    basicFormRef.value?.validateField('bg_effect_child_type')
}
const handleMusicChange = (value: any) => {
    if (value == undefined) {
        // 清空选择
        formData.bg_music_type = ''
        formData.bg_music_child_type = ''
        musicSelected.value = []
        return
    }
    if (value.length == 0) return
    formData.bg_music_type = value[0]
    formData.bg_music_child_type = value[1]
    if (value[1]) {
        const musicType = value[0]
        const musicChildType = value[1]
        // 从原始数据中获取 URL
        const musicItem = bgMusicList.value[musicType]?.child_list?.[musicChildType]
        if (musicItem) {
            formData.bg_music_url = musicItem.url
        }
    }
    basicFormRef.value?.validateField('bg_music_type')
    basicFormRef.value?.validateField('bg_music_child_type')
}
//时间
const pintuanTimeChange = (val: any) => {
    // 如果活动已开始，不允许修改开始时间，只能修改结束时间
    if (id && formData.start_time && new Date(formData.start_time).getTime() < new Date().getTime()) {
        // 保持开始时间不变，只更新结束时间
        formData.time = [
            formData.start_time,
            getCurrentDataTime(JSON.parse(JSON.stringify(val[1])))
        ]
    } else {
        // 活动未开始，可以同时修改开始和结束时间
        formData.time[0] = getCurrentDataTime(JSON.parse(JSON.stringify(val[0])))
        formData.time[1] = getCurrentDataTime(JSON.parse(JSON.stringify(val[1])))
    }

    formData.start_time = formData.time[0]
    formData.end_time = formData.time[1]
    nextTick(() => {
        if(formData.valid_duration > 0){
            basicFormRef.value?.validateField('valid_duration')
        }     
    })
}

const getDetailInfo = () => {
    getPintuanDetail(id).then(({ data }) => {
        Object.assign(formData, data)
        formData.goods_data.diy_detail_id = data.goods_data.diy_detail_id
        if (formData.member_rule == 'level') {
            formData.member_level_id = formData.member_rule_config
        } else if (formData.member_rule == 'label') {
            formData.member_label_id = formData.member_rule_config
        } else if (formData.member_rule == 'member') {
            formData.member_ids = formData.member_rule_config
        }
        convertSecondsToUnits(formData.valid_duration)
        if (data.status == 'process') {
            isProhibitEdit.value = true
        }
        formData.time = [formData.start_time, formData.end_time]
        formData.source = data.goods_data.source || 'pintuan'
        // formData.goods_data = formData.goods
        musicSelected.value = [data.bg_music_type, data.bg_music_child_type]
        // 编辑时初始化背景特效
        if (data.bg_effect_type) {
            formData.bg_effect_type = data.bg_effect_type

            if (data.bg_effect_type === 'normal') {
                // 如果是"无特效"，只设置一级分类
                effectSelected.value = [data.bg_effect_type]
                formData.bg_effect_child_type = '' // 无子项
            } else if (data.bg_effect_child_type) {
                // 有子分类（非无特效）
                effectSelected.value = [data.bg_effect_type, data.bg_effect_child_type]
                formData.bg_effect_child_type = data.bg_effect_child_type
            } else {
                // 非无特效但没有子项（可能是数据问题，默认不选择）
                effectSelected.value = [data.bg_effect_type]
                formData.bg_effect_child_type = ''
            }
        } else {
            // 未选择特效
            effectSelected.value = []
            formData.bg_effect_type = ''
            formData.bg_effect_child_type = ''
        }
        setGoodsList()
        loading.value = false

    })
}

if (id) {
    loading.value = true
    getDetailInfo()
}

const receiveTime = (rule: any, value: any, callback: any) => {
    if (!formData.time || (formData.time && !formData.time[0] && !formData.time[1])) {
        callback(new Error(t('请选择活动时间')))
    } else if (!formData.time[0]) {
        callback(new Error(t('请选择活动开始时间')))
    } else if (!formData.time[1]) {
        callback(new Error(t('请选择活动结束时间')))
    } else if (formData.time[1] <= formData.time[0]) {
        callback(new Error(t('活动结束时间必须大于开始时间')))
    } else if (new Date(formData.time[1]).getTime() <= new Date().getTime()) {
        callback(new Error(t('活动结束时间不能小于当前时间')))
    }
    callback()
}
// 1. 先定义计算活动时间区间总秒数的方法
const getTimeRangeSeconds = () => {
    if (!formData.start_time || !formData.end_time) return 0
    const start = new Date(formData.start_time).getTime()
    const end = new Date(formData.end_time).getTime()
    return Math.floor((end - start) / 1000) // 转换为秒
}
// 手动触发指定阶梯的验证
const validateLadderPrice = (skuIndex, ladderIndex) => {
    if (!formRef.value) return
    formRef.value.validateField(`goods_data.sku_list[${skuIndex}].price_config.ladder[${ladderIndex}].price`)
}
// 监听banner值的变化，自动触发验证
watch(
    () => formData.banner,
    (newVal) => {
    // 当banner有值（上传成功）或从有值变为空（删除图片）时，触发验证
        basicFormRef.value?.validateField('banner')
    }
)
const formRules = computed(() => {
    return {
        title: [
            { required: true, message: t('请输入活动名称'), trigger: 'blur' }
        ],
        type: [
            { required: true, message: t('请选择活动类型'), trigger: 'change' }
        ],
        time: [
            { required: true, validator: receiveTime, trigger: 'change' }
        ],
        banner: [
            { required: true, message: t('请上传活动图片'), trigger: 'change' }
        ],
        valid_duration: [
            {
                required: true,
                validator: (rule: any, value: string, callback: any) => {
                    if (!value || value <= 0) {
                        return callback(new Error(t('有效期不能为空且必须大于0')))
                    }

                    // 新增：校验是否超过活动时间区间
                    const timeRangeSeconds = getTimeRangeSeconds()
                    if (timeRangeSeconds > 0 && Number(value) > timeRangeSeconds) {
                    // 格式化活动时间区间为“天时分”用于提示
                    // const formatRange = () => {
                    //     const days = Math.floor(timeRangeSeconds / 86400);
                    //     const hours = Math.floor((timeRangeSeconds % 86400) / 3600);
                    //     const minutes = Math.floor((timeRangeSeconds % 3600) / 60);
                    //     return `${days > 0 ? days + '天' : ''}${hours > 0 ? hours + '时' : ''}${minutes}分`;
                    // };
                        return callback(new Error(t('拼团有效期不能超过活动时间区间')))
                    }

                    // 校验通过
                    callback()
                },
                trigger: ['blur', 'change']
            }
        ],
        'member_num_config.normal': [
            { required: true, message: '请输入拼团人数', trigger: 'blur' },
            {
                validator: (rule: any, value: string, callback: any) => {
                    if (value < 2) {
                        callback(new Error(t('拼团人数不能小于2')))
                    } else callback()
                },
            }
        ],
        'member_num_config.referral': [
            { required: true, message: '请输入新用户成团人数', trigger: 'blur' },
            {
                validator: (rule: any, value: string, callback: any) => {
                    if (value < 2) {
                        callback(new Error(t('拼团人数不能小于2')))
                    } else callback()
                },
            }
        ],
        goods_data: [
            {
                required: true,
                validator: (rule: any, value: string, callback: any) => {
                    if (Object.keys(value).length === 0) {
                        callback(new Error(t('请选择商品')))
                    } else {
                        callback()
                    }
                },
                trigger: ['blur', 'change']
            }
        ],
        member_level_id: [
            { required: true, message: t('请选择会员等级'), trigger: 'blur' }
        ],
        member_label_id: [
            { required: true, message: t('请选择会员标签'), trigger: 'blur' }
        ],
        member_data: [
            { required: true, message: t('请选择会员'), trigger: 'blur' }
        ],
        expense_rate: [
            {
                validator: (rule: any, value: string, callback: any) => {
                    if (value == '') {
                        callback()
                    } else if (isNaN(value) && !regExp.digit.test(value)) {
                        callback(new Error(t('手续费率格式输入错误')))
                    } else if (Number(value) > 100 || Number(value) < 0) {
                        callback(new Error(t('手续费率应在0-100之间')))
                    } else {
                        callback()
                    }
                },
                trigger: 'blur'
            }
        ],
    }
})

// 正则表达式
const regExp = {
    number: /^\d{0,10}(.?\d{0,1})$/,
    digit: /^\d{0,10}(.?\d{0,2})$/
}

const onSave = () => {
    if (loading.value) return
    if (formData.goods_data.sku_list && formData.goods_data.sku_list.length > 0) {
        let isHasDefaultSpec = false // 是否存在默认规格
        formData.goods_data.sku_list.forEach((item: any) => {
            if (item.is_default) {
                isHasDefaultSpec = true // 存在默认规格
            }
        })
        if (!isHasDefaultSpec) {
            ElMessage.warning('商品缺少默认规格')
            return
        }
    }

    verify(() => {
        loading.value = true
        if (formData.member_rule == 'member') {
            formData.member_rule_config = formData.member_ids
        } else if (formData.member_rule == 'level') {
            formData.member_rule_config = formData.member_level_id
        } else if (formData.member_rule == 'label') {
            formData.member_rule_config = formData.member_label_id
        } else {
            formData.member_rule_config = []
        }
        let obj = cloneDeep(formData)

        // -------------- 新增：过滤空价格的阶梯配置 --------------
        // 1. 处理顶层price_config中的ladder
        if (obj.goods_data.price_config?.ladder && Array.isArray(obj.goods_data.price_config.ladder)) {
            obj.goods_data.price_config.ladder = obj.goods_data.price_config.ladder.filter((item: any) => {
                // 确保price是字符串（如果是数字则转为字符串，否则设为空）
                const priceStr = typeof item.price === 'number'  ? String(item.price)  : (item.price || '') 
                // 去除空格后判断是否非空
                return priceStr.trim() !== ''
            })
        }
        // 2. 处理sku_list中每个项的price_config.ladder
        if (obj.goods_data.sku_list && Array.isArray(obj.goods_data.sku_list)) {
            obj.goods_data.sku_list = obj.goods_data.sku_list.map((skuItem: any) => {
                if (skuItem.price_config?.ladder && Array.isArray(skuItem.price_config.ladder)) {
                    skuItem.price_config.ladder = skuItem.price_config.ladder.filter((item: any) => {
                        const priceStr = typeof item.price === 'number'  ? String(item.price) : (item.price || '')
                        return priceStr.trim() !== ''
                    })
                }
                return skuItem
            })
        }
        // -------------------------------------------------------
        if (id) {
            obj.id = id
            editPintuan(obj).then((res) => {
                loading.value = false
                back()
            }).catch(() => {
                loading.value = false
            })
        } else {
            addPintuan(obj).then((res) => {
                loading.value = false
                back()
            }).catch(() => {
                loading.value = false
            })
        }
    })
}

const back = () => {
    router.push('/pintuan/active/list')
}
</script>

<style lang="scss" scoped>
.sku-form-item-wrap :deep(.el-form-item__content) {
    margin-left: 0 !important;
}

:deep(.el-table td.el-table__cell) {
    padding: 25px 0 !important;
}

.sku_list :deep(.cell) {
    // min-height: 35px !important;
    overflow: initial !important;
}

.sku_list :deep(.el-form-item__error) {
    padding-top: 6px;
    left: 2px;
}
</style>
