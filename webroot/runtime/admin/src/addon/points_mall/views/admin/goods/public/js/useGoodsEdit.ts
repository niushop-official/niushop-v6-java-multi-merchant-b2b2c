import { reactive, ref, computed, nextTick } from 'vue'
import { t } from '@/lang'
import { TabsPaneContext, ElMessage } from 'element-plus'
import Sortable from 'sortablejs'
import { range, cloneDeep, isArray } from 'lodash-es'
import { debounce } from '@/utils/common'
import { useRoute, useRouter } from 'vue-router'
import Test from '@/utils/test'
import { getMemberLabelAll, getMemberLevelAll } from '@/app/api/member'
import { getCategoryTree as getMallCategoryTree} from '@/app/api/basic_admin'
import { getCategory } from '@/addon/points_mall/api/admin/category' 
import { getMemberRule, addGoods, editGoods } from '@/addon/points_mall/api/admin/goods'
import { getDiyFormList } from '@/app/api/diy_form_admin'

// 商品添加/编辑
export function useGoodsEdit(params: any = {}) {

    const route = useRoute()
    const router = useRouter()

    const repeat = ref(false)

    // 表单数据
    const formData: any = reactive({
        goods_id: '',

        // 基础信息
        goods_name: '',
        sub_title: '',
        goods_image: '',
        goods_video: '',
        goods_category: '',
        category: '',
        status: 1,
        sort: '',
        join_member_type: 'all',
        member_type_config: '',
        source_id: '',
        source: '',
        source_name: '',
        // 价格库存
        spec_type: 'single',
        point: '',
        price: '',
        market_price: '',
        stock: '',
        unit: '',
        member_limit_buy: '',
        virtual_sale_num: '',
        is_allow_refund: 1,
        delivery_type: ['express'],
        
        // 商品详情
        goods_desc: '',
        skuCheckAll: false,// 是否全选
        skuIsIndeterminate: false,// 是否部分选中
        skuCheckedCities: [],// 选中的规格
    })

    Object.assign(formData, params.formData);


    formData.goods_id = ref(route.query.goods_id)

    // 追加表单数据
    const appendFormData = params.appendFormData;
    Object.assign(formData, appendFormData);

    // 追加刷新商品sku数据
    const appendRefreshGoodsSkuData = params.appendRefreshGoodsSkuData || {};

    // 追加单规格数据
    const appendSingleGoodsData = params.appendSingleGoodsData;

    const getFormRules = params.getFormRules;

    const formRefArr: any = reactive({});

    const getFormRef = params.getFormRef;

    const verifyArr: any = reactive([])

    nextTick(() => {
        let formRef = getFormRef();
        for (let key in formRef) {
            formRefArr[key] = formRef[key];
        }

        if (params.getVerify) {
            verifyArr.splice(0, verifyArr.length, ...params.getVerify())
        }

    });


    const activeName: any = ref('basic')
    const tabHandleClick = (tab: TabsPaneContext, event: Event) => {
        // console.log(tab, event)
    }

    const memberRuleOptions = reactive({})
    const labelSelectData = reactive<any>([]) // 标签
    const levelSelectData = reactive<any>([]) // 等级
    const getInit = () => {
        getMemberRule().then((res: any) => {
            Object.assign(memberRuleOptions, res.data)
        })
        // 获取全部标签
        getMemberLabelAll().then((res: any) => {
            labelSelectData.splice(0, labelSelectData.length, ...res.data)
        })

        getMemberLevelAll().then((res: any) => {
            levelSelectData.splice(0, levelSelectData.length, ...res.data) 
        })
    }
    getInit()
    // 平台商品分类
    const goodsMallCategoryOptions = reactive([])
    // 刷新平台商品分类
    const refreshMallGoodsCategory = () => {
        getMallCategoryTree().then((res) => {
            const data = res.data
            if (data) {
                const goodsCategoryTree: any = []
                data.forEach((item: any) => {
                    const children: any = []
                    if (item.child_list) {
                        item.child_list.forEach((childItem: any) => {
                            const grandson: any = []
                            if (childItem.child_list) {
                                childItem.child_list.forEach((grandsonItem: any) => {
                                    grandson.push({
                                        label: grandsonItem.category_name,
                                        value: grandsonItem.category_id
                                    })
                                })
                            }
                            children.push({
                                label: childItem.category_name,
                                value: childItem.category_id,
                                children: grandson
                            })
                        })
                    }
                    goodsCategoryTree.push({
                        value: item.category_id,
                        label: item.category_name,
                        children
                    })
                })
                goodsMallCategoryOptions.splice(0, goodsMallCategoryOptions.length, ...goodsCategoryTree)
            }
        })
    };

    refreshMallGoodsCategory()

    const goodsGroupOptions = reactive<any>([]) // 商品分组
    const refreshGoodsGroup = () => {
        getCategory().then((res) => {
            goodsGroupOptions.splice(0, goodsGroupOptions.length, ...res.data)
        })
    };
    refreshGoodsGroup()

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
            type: 'DIY_FORM_GOODS_DETAIL',
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

    const goodsSpecFormat: any = reactive([]) // 规格项/规格值
    const goodsSkuData: any = reactive({}) // 商品SKU规格数据
    const specData: any = reactive([]) // 商品规格值

    const activeGoodsCount: any = ref(0)

    const isDisabledPrice = () => {
        if (activeGoodsCount.value > 0) {
            return true;
        }
        return false;
    }

    const handleGoodsInit = (data: any) => {
        if (formData.goods_id && data.goods_info) {

            // 商品参与营销活动的数量
            activeGoodsCount.value = data.goods_info.active_goods_count;

            // 基础信息
            formData.goods_name = data.goods_info.goods_name
            formData.sub_title = data.goods_info.sub_title
            formData.goods_image = data.goods_info.goods_image
            formData.goods_video = data.goods_info.goods_video
            formData.goods_category = data.goods_info.goods_category  ? Number(data.goods_info.goods_category) : ''
            formData.category = data.goods_info.category ? Number(data.goods_info.category) : ''
            formData.status = Number(data.goods_info.status) 
            formData.sort = data.goods_info.sort|| ''
            formData.join_member_type = data.goods_info.join_member_type || 'all'
            formData.member_type_config = data.goods_info.member_type_config 
            formData.source_id = data.goods_info.source_id || ''
            formData.source = data.goods_info.source
            formData.source_name = data.goods_info.source_name
            formData.member_limit_buy = data.goods_info.member_limit_buy || ''
            formData.virtual_sale_num = data.goods_info.virtual_sale_num || ''
            formData.is_allow_refund = data.goods_info.is_allow_refund

            // 价格库存
            formData.spec_type = data.goods_info.spec_type
            formData.stock = data.goods_info.stock

            if (formData.spec_type == 'single') {
                // 单规格
                const skuInfo = data.goods_info.sku_list[0]
                formData.point = parseInt(skuInfo.point) || 0
                formData.price = skuInfo.price
                formData.market_price = skuInfo.market_price
                

                if (appendSingleGoodsData) Object.assign(formData, appendSingleGoodsData(skuInfo));
            } else if (formData.spec_type == 'multi') {
                // 多规格
                const specList = data.goods_info.spec_list
                if(specList && specList.length > 0){
                    specList.forEach((item: any) => {
                        const values: any = []
                        if(!isArray(item.spec_values)){
                            item.spec_values = item.spec_values.split(',')
                        }
                        item.spec_values.forEach((v: any) => {
                            values.push({
                                id: generateRandom(),
                                spec_value_name: v
                            })
                        })
                        goodsSpecFormat.push({
                            idRanDom: generateRandom(),
                            spec_id: item.spec_id,
                            goods_id: item.goods_id,
                            spec_name: item.spec_name,
                            values
                        })
                    })
                }  

                refreshGoodsSkuData();

                const skuList = data.goods_info.sku_list

                for (let key in goodsSkuData) {
                    for (let i = 0; i < skuList.length; i++) {
                        let item = skuList[i];
                        if (goodsSkuData[key].spec_name == item.sku_spec_format?.replace(/,/g, ' ') || goodsSkuData[key].spec_name == item.spec_name) {
                            goodsSkuData[key].sku_id = item.sku_id;
                            goodsSkuData[key].source = item.source;
                            goodsSkuData[key].source_id = item.source_id;
                            goodsSkuData[key].source_sku_id = item.source_sku_id;
                            goodsSkuData[key].sku_image = item.sku_image;
                            goodsSkuData[key].point = parseInt(item.point) || '';
                            goodsSkuData[key].price = item.price || '';
                            goodsSkuData[key].market_price = item.market_price || '';
                            goodsSkuData[key].sku_name = item.sku_name;
                            for (let field in appendRefreshGoodsSkuData) {
                                goodsSkuData[key][field] = item[field];
                            }
                            goodsSkuData[key].stock = item.stock || '';
                            goodsSkuData[key].sku_id = item.sku_id;
                            goodsSkuData[key].sku_no = item.sku_no;
                            goodsSkuData[key].is_default = item.is_default;
                            goodsSkuData[key].is_join = item.is_join
                            break;
                        }
                    }
                }

                nextTick(() => {
                    refreshSkuTable()
                    bindSpecValue()
                })
            }

            formData.unit = data.goods_info.unit

            // 商品详情
            formData.goods_desc = data.goods_info.goods_desc
        }
    }

    // 绑定拖拽规格值事件
    const bindSpecValue = () => {
        // 商品正在参与营销活动，禁止操作
        if (isDisabledPrice()) {
            return
        }
        if (!getFormRef().specValueRef) return

        for (let i = 0; i < getFormRef().specValueRef.length; i++) {
            const item = getFormRef().specValueRef[i]
            const sortable = Sortable.create(item, {
                group: 'draggable-element-' + i,
                animation: 200,
                // 结束拖拽
                onEnd: event => {
                    const temp = goodsSpecFormat[i].values[event.oldIndex!]
                    goodsSpecFormat[i].values.splice(event.oldIndex!, 1)
                    goodsSpecFormat[i].values.splice(event.newIndex!, 0, temp)

                    nextTick(() => {
                        sortable.sort(
                            range(goodsSpecFormat[i].values.length).map(value => {
                                return value.toString()
                            })
                        )

                        // 渲染商品规格数据、表格
                        refreshGoodsSkuData()
                        refreshSkuTable()
                    })
                }
            })
        }
    }

    // 生成随机数
    const generateRandom = (len: number = 5) => {
        return Number(Math.random().toString().substr(3, len) + Date.now()).toString(36)
    }

    // 添加规格项
    const addSpec = () => {
        // 商品正在参与营销活动，禁止操作
        if (isDisabledPrice()) {
            ElMessage({
                type: 'warning',
                message: `${ t('participateInActiveDisableTips') }`
            })
            return
        }
        if (goodsSpecFormat.length > 4) {
            ElMessage({
                type: 'warning',
                message: `${ t('maxAddSpecTips') }`
            })
            return
        }
        goodsSpecFormat.push({
            idRanDom: generateRandom(),
            spec_name: '',
            values: [
                {
                    idRanDom: generateRandom(),
                    spec_value_name: ''
                }
            ]
        })
    }

    // 删除规格项
    const deleteSpec = (index: number) => {
        // 商品正在参与营销活动，禁止操作
        if (isDisabledPrice()) {
            ElMessage({
                type: 'warning',
                message: `${ t('participateInActiveDisableTips') }`
            })
            return
        }
        goodsSpecFormat.splice(index, 1)
        // 渲染商品规格数据、表格、统计库存变化
        refreshGoodsSkuData()
        refreshSkuTable()
        specStockSum()
    }

    // 添加规格值
    const addSpecValue = (index: number) => {
        // 商品正在参与营销活动，禁止操作
        if (isDisabledPrice()) {
            ElMessage({
                type: 'warning',
                message: `${ t('participateInActiveDisableTips') }`
            })
            return
        }
        goodsSpecFormat[index].values.push({
            id: generateRandom(),
            spec_value_name: ''
        })
        bindSpecValue()
    }

    // 监听规格值变化
    const specValueNameInputListener = debounce((value) => {
        // 渲染商品规格数据、表格
        refreshGoodsSkuData()
        refreshSkuTable()

    })

    // 删除规格值
    const deleteSpecValue = (index: number, specIndex: number) => {
        // 商品正在参与营销活动，禁止操作
        if (isDisabledPrice()) {
            ElMessage({
                type: 'warning',
                message: `${ t('participateInActiveDisableTips') }`
            })
            return
        }

        goodsSpecFormat[index].values.splice(specIndex, 1)
        // 渲染商品规格数据、表格、统计库存变化
        refreshGoodsSkuData()
        refreshSkuTable()
        specStockSum()
    }

    // 设置默认规格
    const specValueIsDefaultChangeListener = (value: any, key: any) => {
        for (const k in goodsSkuData) {
            if (k == key) {
                goodsSkuData[k].is_default = value
                goodsSkuData[k].is_join = 1
            } else {
                goodsSkuData[k].is_default = 0
            }
        }
    }
    
    // 监听规格库存变化，统计总库存
    const specStockSum = debounce(() => {
        let stock = 0
        for (const k in goodsSkuData) {
            if (goodsSkuData[k].stock) stock += parseInt(goodsSkuData[k].stock)
        }
        formData.stock = stock
    })


    // 刷新商品规格数据
    const refreshGoodsSkuData = () => {
        const arr = goodsSpecFormat
        const tempGoodsSkuData = cloneDeep(goodsSkuData)// 记录原始数据，后续用作对比
        let skuData: any = {}
        let tempIndex = 0;
        for (const spec of arr) {
            let item_prop_arr: any = {}
            if (Object.keys(skuData).length > 0) {
                for (const ele_2 in skuData) {
                    for (let ele_3 of spec.values) {
                        let sku_spec = cloneDeep(skuData[ele_2].sku_spec)// 防止对象引用
                        sku_spec.push(ele_3)
                        item_prop_arr['sku_' + tempIndex] = {
                            spec_name: `${ skuData[ele_2].spec_name } ${ ele_3.spec_value_name }`,
                            sku_name: `${ skuData[ele_2].sku_name } ${ ele_3.spec_value_name }`,
                            sku_spec,
                            sku_image: '',
                            point: '',
                            price: '',
                            market_price: '',
                            stock: '',
                            is_default: 0,
                            is_join: 1,
                        }

                        for (let key in appendRefreshGoodsSkuData) {
                            item_prop_arr['sku_' + tempIndex][key] = appendRefreshGoodsSkuData[key].value;
                        }
                        tempIndex++;
                    }
                }
            } else {
                for (let ele_1 of spec.values) {
                    let spec_name = ele_1.spec_value_name
                    item_prop_arr['sku_' + tempIndex] = {
                        spec_name: spec_name,
                        sku_name: spec_name,
                        sku_spec: [ele_1],
                        sku_image: '',
                        point: '',
                        price: '',
                        market_price: '',
                        stock: '',
                        is_default: 0,
                        is_join: 1,
                    }
                    for (let key in appendRefreshGoodsSkuData) {
                        item_prop_arr['sku_' + tempIndex][key] = appendRefreshGoodsSkuData[key].value;
                    }
                    tempIndex++;
                }
            }

            skuData = Object.keys(item_prop_arr).length > 0 ? item_prop_arr : skuData
        }

        // 比对已存在的规格项/值，并且赋值
        for (const tempKey in tempGoodsSkuData) {
            for (const key in skuData) {
                const count = matchSkuSpecCount(tempGoodsSkuData[tempKey].sku_spec, skuData[key].sku_spec)
                if (count === skuData[key].sku_spec.length) {
                    // 匹配成功后，要同步最新的规格项名称、规格值集合
                    const specName = skuData[key].spec_name
                    const skuSpec = skuData[key].sku_spec
                    Object.assign(skuData[key], tempGoodsSkuData[tempKey])
                    skuData[key].spec_name = specName
                    skuData[key].sku_spec = skuSpec
                    break
                }
            }
        }

        for (const item in goodsSkuData) {
            delete goodsSkuData[item]
        }

        let firstSpec = ''

        for (const key in skuData) {
            if (firstSpec == '') {
                firstSpec = key
                skuData[key].is_default = 1
            } else {
                skuData[key].is_default = 0
            }
            goodsSkuData[key] = skuData[key]
        }
        formData.skuCheckAll = false;// 是否全选
        formData.skuIsIndeterminate = false// 是否部分选中
        formData.skuCheckedCities = []// 选中的规格
    }

    // 匹配规格值
    const matchSkuSpecCount = (oVal: any, nVal: any) => {
        let count = 0// 匹配次数，与规格值相等时为匹配成功
        for (let i = 0; i < oVal.length; i++) {
            for (let j = 0; j < nVal.length; j++) {
                if (oVal[i].id === nVal[j].id) {
                    count++
                    break
                }
            }
        }
        return count
    }

    // 刷新商品规格表格
    const refreshSkuTable = () => {
        let length = 0
        // 统计有效规格数量
        for (let i = 0; i < goodsSpecFormat.length; i++) {
            if (goodsSpecFormat[i].spec_name != '' && goodsSpecFormat[i].values.length > 0) {
                length++
            }
        }

        let row = 1 // 跨行
        const tempSpecData = []

        // 规格值单元格合并
        for (let i = length - 1; i >= 0; i--) {
            for (let k = 0; k < Object.keys(goodsSkuData).length;) {
                if (goodsSpecFormat[i].values.length > 0) {
                    for (let ele of goodsSpecFormat[i].values) {
                        tempSpecData.push({
                            index: k,
                            colSpan: i,
                            rowSpan: row,
                            spec_value_name: ele.spec_value_name
                        })
                        k = k + row
                    }
                } else {
                    k++
                }
            }
            row = row * goodsSpecFormat[i].values.length
        }

        tempSpecData.reverse()
        specData.splice(0, specData.length, ...tempSpecData)
    }

    const batchOperation: any = reactive({
        spec: '', // 所选规格id，空为全部
        point: '', // 积分
        price: '', // 销售价
        market_price: '', // 划线价
        stock: '',

    })

    const appendBatchOperation: any = {};
    for (let key in appendRefreshGoodsSkuData) {
        appendBatchOperation[key] = appendRefreshGoodsSkuData[key].value;
    }
    

    Object.assign(batchOperation, appendBatchOperation);

    const skuHandleCheckAllChange = (value: any) => {
        formData.skuIsIndeterminate = false
        if (value) {
            formData.skuCheckedCities = Object.keys(goodsSkuData)
        } else {
            formData.skuCheckedCities = []
        }
    }
    const handleCheckedCitiesChange = (value: any) => {
        const checkedCount = value.length
        formData.skuCheckAll = checkedCount === Object.keys(goodsSkuData).length
        formData.skuIsIndeterminate = checkedCount > 0 && checkedCount < Object.keys(goodsSkuData).length
    }
    // 批量设置确认
    const saveBatch = () => {
        if (formData.skuCheckedCities.length == 0) {
            ElMessage({
                type: 'warning',
                message: `${ t('请选择商品规格') }`
            })
            return
        }
        // 验证输入内容
        if (batchOperation.point && (isNaN(batchOperation.point) || !regExp.number.test(batchOperation.point))) {
            ElMessage({
                type: 'warning',
                message: `${t('积分格式错误')}`
            })
            return
        }
        if (batchOperation.market_price && (isNaN(batchOperation.market_price) || !regExp.digit.test(batchOperation.market_price))) {
            ElMessage({
                type: 'warning',
                message: `${t('marketPriceTips')}`
            })
            return
        }

        
        if (batchOperation.stock && (isNaN(batchOperation.stock) || !regExp.number.test(batchOperation.stock))) {
            ElMessage({
                type: 'warning',
                message: `${ t('stockTips') }`
            })
            return
        }



        for (let field in appendRefreshGoodsSkuData) {
            let reg = regExp[appendRefreshGoodsSkuData[field].regExp]
            let message = appendRefreshGoodsSkuData[field].message

            if (batchOperation[field] && (isNaN(batchOperation[field]) || !reg.test(batchOperation[field]))) {
                ElMessage({
                    type: 'warning',
                    message: message
                })
                return
            }

        }

        // 设置全部规格
        formData.skuCheckedCities.forEach((key: any) => {
            if (batchOperation.point) goodsSkuData[key].point = batchOperation.point
            if (batchOperation.price) goodsSkuData[key].price = batchOperation.price
            if (batchOperation.market_price) goodsSkuData[key].market_price = batchOperation.market_price
            if (batchOperation.stock) goodsSkuData[key].stock = batchOperation.stock
            for (let field in appendRefreshGoodsSkuData) {
                if (batchOperation[field]) goodsSkuData[key][field] = batchOperation[field]
            }

        })

        // 保存完清空
        batchOperation.point = ''
        batchOperation.price = ''
        batchOperation.market_price = ''
        batchOperation.stock = ''

        for (let field in appendRefreshGoodsSkuData) {
            batchOperation[field] = '';
        }

    }

    // 正则表达式
    const regExp: any = {
        required: /[\S]+/,
        number: /^\d{0,10}$/,
        digit: /^\d{0,10}(.?\d{0,2})$/,
        special: /^\d{0,10}(.?\d{0,3})$/
    }

    // 表单验证规则
    const formRules = computed(() => {
        let rules = {
            goods_name: [
                {
                    required: true,
                    trigger: 'blur',
                    validator: (rule: any, value: any, callback: any) => {
                        if (value === '') {
                            callback(new Error(t('goodsNamePlaceholder')))
                        }
                        if (value.length > 60) {
                            callback(new Error(t('goodsNameMaxLengthTips')))
                        } else {
                            callback()
                        }
                    }
                }
            ],
            sub_title: [
                {
                    trigger: 'blur',
                    validator: (rule: any, value: any, callback: any) => {
                        if (value.length > 80) {
                            callback(new Error(t('subTitleMaxLengthTips')))
                        } else {
                            callback()
                        }
                    }
                }
            ],
            goods_image: [
                { required: true, message: t('goodsImagePlaceholder'), trigger: 'blur' }
            ],
            goods_category: [
                { required: true, message: t('goodsCategoryPlaceholder'), trigger: 'blur' },
            ],
            category: [
                { required: true, message: t('请选择商品分组'), trigger: ['blur', 'change'] },
            ],
            sort: [
                {
                    trigger: 'blur',
                    validator: (rule: any, value: any, callback: any) => {
                        if (isNaN(value) || !regExp.number.test(value)) {
                            callback(new Error(t('sortTips')))
                        } else {
                            callback()
                        }
                    }
                }
            ],
            // 单规格
            point: [
                {
                    trigger: 'blur',
                    validator: (rule: any, value: any, callback: any) => {
                        if (formData.spec_type == 'single') {
                            if (isNaN(value) || !regExp.number.test(value)) {
                                callback(new Error(t('pointTips')))
                            } else if (value < 0) {
                                callback(new Error(t('pointNotZeroTips')))
                            } else {
                                callback()
                            }
                        } else {
                            callback()
                        }
                    }
                }
            ],
            market_price: [
                {
                    trigger: 'blur',
                    validator: (rule: any, value: any, callback: any) => {
                        if (formData.spec_type == 'single') {
                            if (isNaN(value) || !regExp.digit.test(value)) {
                                callback(new Error(t('marketPriceTips')))
                            } else if (value < 0) {
                                callback(new Error(t('marketPriceNotZeroTips')))
                            } else {
                                callback()
                            }
                        } else {
                            callback()
                        }
                    }
                }
            ],
            member_limit_buy: [
                {
                    trigger: 'blur',
                    validator: (rule: any, value: any, callback: any) => {
                        if (formData.spec_type == 'single') {
                            if (value === '') {
                                callback()
                            } else if (isNaN(value) || !regExp.number.test(value)) {
                                callback(new Error(t('每人限购格式不正确')))
                            } else if (Number(value) < 0) {
                                callback(new Error(t('每人限购不能小于0')))
                            } else {
                                callback()
                            }
                        } else {
                            callback()
                        }
                    }
                }
            ],
            stock: [
                {
                    required: true,
                    trigger: 'blur',
                    validator: (rule: any, value: any, callback: any) => {
                        if (formData.spec_type == 'single') {
                            if (value === '') {
                                callback(new Error(t('stockPlaceholder')))
                            } else if (isNaN(value) || !regExp.number.test(value)) {
                                callback(new Error(t('stockTips')))
                            } else if (Number(value) < 0) {
                                callback(new Error(t('stockNotZeroTips')))
                            } else {
                                callback()
                            }
                        } else {
                            callback()
                        }
                    }
                }
            ],
            virtual_sales: [
                {
                    trigger: 'blur',
                    validator: (rule: any, value: any, callback: any) => {
                        if (formData.spec_type == 'single') {
                            if (isNaN(value) || !regExp.number.test(value)) {
                                callback(new Error(t('virtualSaleNumTips')))
                            } else if (Number(value) < 0) {
                                callback(new Error(t('virtualSaleNumNotZeroTips')))
                            } else {
                                callback()
                            }
                        } else {
                            callback()
                        }
                    }
                }
            ],
            // 多规格
            spec_type: [
                {
                    trigger: 'blur',
                    validator: (rule: any, value: any, callback: any) => {
                        if (formData.spec_type == 'multi') {
                            if (Object.keys(goodsSkuData).length == 0) {
                                callback(new Error(t('pleaseEditSpecPlaceholder')))
                            }
                        }
                        callback()
                    }
                }
            ],
            goods_desc: [
                {
                    required: true,
                    trigger: ['blur', 'change'],
                    validator: (rule: any, value: any, callback: any) => {
                        if (value === '') {
                            callback(new Error(t('goodsDescPlaceholder')))
                        } else if (value.length < 5 || value.length > 50000) {
                            callback(new Error(t('goodsDescMaxTips')))
                            return false
                        } else {
                            callback()
                        }
                    }
                }
            ]
        };

        if (getFormRules) {
            Object.assign(rules, getFormRules(formData, regExp));
        }

        return rules;
    })

    // 多规格，原价 验证规则
    const skuPriceRules = (is_join:any) => {
        return [{
            trigger: 'blur',
            validator: (rule: any, value: any, callback: any) => {
                if (formData.spec_type == 'multi') {
                    if (isNaN(value) || !regExp.number.test(value)) {
                        callback(t('pointTips'))
                    } else if (Number(value) < 0) {
                        callback(t('pointNotZeroTips'))
                    } else {
                        callback();
                    }
                } else {
                    callback();
                }

            }
        }]
    };
    // 多规格，划线价 验证规则
    const skuMarketPriceRules = (is_join:any) => {
        return [{
            trigger: 'blur',
            validator: (rule: any, value: any, callback: any) => {
                if (formData.spec_type == 'multi' && is_join == 1) {
                    if (isNaN(value) || !regExp.digit.test(value)) {
                        callback(t('marketPriceTips'))
                    } else if (value < 0) {
                        callback(t('marketPriceNotZeroTips'))
                    } else {
                        callback();
                    }
                } else {
                    callback();
                }
            }
        }]
    }

    // 多规格，库存 验证规则
    const skuStockRules = (is_join:any) => {
        return [{
            trigger: 'blur',
            validator: (rule: any, value: any, callback: any) => {
                if (formData.spec_type == 'multi' && is_join == 1) {
                    if (value.length == 0) {
                        callback(t('stockPlaceholder'))
                    } else if (isNaN(value) || !regExp.number.test(value)) {
                        callback(t('stockTips'))
                    } else if (Number(value)<0) {
                        callback(t('stockNotZeroTips'))
                    } else {
                        callback();
                    }

                } else {
                    callback();
                }

            }
        }]
    }

    const verify = (callback: any) => {
        let formRef = [
            {
                key: 'basic',
                verify: false,
                ref: formRefArr.basicFormRef
            },
            {
                key: 'price_stock',
                verify: false,
                ref: formRefArr.priceStockFormRef
            },
            {
                key: 'price_stock',
                verify: false,
                ref: formRefArr.skuFormRef
            }
        ];
        formRef = formRef.concat(verifyArr);

        // 调整验证顺序 -- 详情验证
        let obj = {
            key: 'detail',
            verify: false,
            ref: formRefArr.detailFormRef
        };
        formRef.push(obj)

        formRef.forEach((el: any, index) => {
            el.ref.validate((valid: any) => {
                el.verify = valid
            })
        })

        if (formData.spec_type == 'multi') {
            let specVerify = true
            let repeatSpecNameArr: any = [];
            let repeatSpecValueNameArr: any = [];
            for (let i = 0; i < goodsSpecFormat.length; i++) {
                const spec = goodsSpecFormat[i]
                if (Test.require(spec.spec_name)) {
                    specVerify = false
                    ElMessage({ type: 'warning', message: `${ t('specNameRequire') }` })
                    break
                }
                if (repeatSpecNameArr.indexOf(spec.spec_name) > -1) {
                    specVerify = false
                    ElMessage({ type: 'warning', message: `${ t('specNameRepeat') }` })
                    break
                } else {
                    repeatSpecNameArr.push(spec.spec_name);
                }

                if (spec.values.length) {
                    for (let v = 0; v < spec.values.length; v++) {
                        const value = spec.values[v]
                        if (Test.require(value.spec_value_name)) {
                            specVerify = false
                            ElMessage({ type: 'warning', message: `${ t('specValueRequire') }` })
                            break
                        }

                        if (repeatSpecValueNameArr.indexOf(value.spec_value_name) > -1) {
                            specVerify = false
                            ElMessage({ type: 'warning', message: `${ t('specValueNameRepeat') }` })
                            break
                        } else {
                            repeatSpecValueNameArr.push(value.spec_value_name);
                        }
                    }
                } else {
                    specVerify = false
                    ElMessage({ type: 'warning', message: `${ t('specValueRequire') }` })
                }

                if (!specVerify) break
            }

            if (!specVerify) {
                activeName.value = 'price_stock'
                return
            }

            let isHasDefaultSpec = false; // 是否存在默认规格
            for (const k in goodsSkuData) {
                if (goodsSkuData[k].is_default) {
                    isHasDefaultSpec = true;
                }
            }

            if (!isHasDefaultSpec) {
                activeName.value = 'price_stock'
                ElMessage({ type: 'warning', message: `${ t('lackDefaultSpec') }` })
                return
            }
        }

        setTimeout(() => {
            let verify = true
            // 检测验证，并且定位tab页面
            for (let i = 0; i < formRef.length; i++) {
                if (formRef[i].verify == false) {
                    activeName.value = formRef[i].key
                    verify = false
                    break
                }
            }
            if (verify && callback) callback()
        }, 10)
    }
    // 保存数据
    const save = (callback: any = null) => {
        verify(() => {

            if (repeat.value) return
            repeat.value = true

            const api = route.query.source ? addGoods : (formData.goods_id ? editGoods : addGoods)

            const data = cloneDeep(formData)

            if (data.spec_type == 'multi') {
                data.stock = 0
                for (const k in goodsSkuData) {
                    if (goodsSkuData[k].stock) data.stock += parseInt(goodsSkuData[k].stock)
                }
            }

            data.goods_sku_data = goodsSkuData
            data.goods_spec_format = goodsSpecFormat

            if (callback) {
                Object.assign(data, callback(data));
            }
            
            
           api(data).then((res: any) => {
                repeat.value = false
                router.push('/points_mall/goods/list')
            }).catch(() => {
                repeat.value = false
            })

        })
    }

    const back = () => {
        router.push('/points_mall/goods/list')
    }

    const filterSpecial = (event: any) => {
        event.target.value = event.target.value.replace(/[^\u4e00-\u9fa5a-zA-Z0-9\s]/g, '')
        event.target.value = event.target.value.replace(/[`~!@#$%^&*()_\-+=<>?:"{}|,.\/;'\\[\]·~！@#￥%……&*（）——\-+={}|《》？：“”【】、；‘’，。、]/g, '')
    }

    const handleBlur = (e: any) => {
        formRefArr.detailFormRef?.validateField('desc')
    }

    return {
        formData,
        activeName,
        tabHandleClick,

        memberRuleOptions,
        labelSelectData,
        levelSelectData,

        goodsMallCategoryOptions,
        refreshMallGoodsCategory,

        goodsGroupOptions,
        refreshGoodsGroup,

        diyFormOptions,
        toDiyFormEvent,
        refreshDiyForm,

        goodsSpecFormat,
        goodsSkuData,
        specData,

        generateRandom,

        isDisabledPrice,
        addSpec,
        deleteSpec,
        addSpecValue,
        specValueNameInputListener,
        deleteSpecValue,
        specValueIsDefaultChangeListener,
        specStockSum,
        refreshGoodsSkuData,

        batchOperation,
        skuHandleCheckAllChange,
        handleCheckedCitiesChange,
        saveBatch,

        regExp,

        formRules,
        skuPriceRules,
        skuMarketPriceRules,
        skuStockRules,
        handleGoodsInit,
        save,
        back,
        filterSpecial,
        handleBlur
    }
}
