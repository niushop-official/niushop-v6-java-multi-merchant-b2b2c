import { ref, reactive, computed, onUnmounted } from 'vue';
import { img, getToken, deepClone } from '@/utils/common';
import { getDiyFormInfo } from '@/app/api/diy_form';
// import useDiyStore from '@/app/stores/diy';

export function useDiyForm(params: any = {}) {

    const loading = ref(true);
    // const diyStore = useDiyStore();

    const form_id = ref(params.form_id || 0)
    const form_sign = ref(params.form_sign || 0)
    const name = ref(params.name || '')
    const template = ref('')
    const currRoute = ref('') //当前路由
    const requestData: any = reactive({});

    // 自定义页面 数据
    const diyData = reactive({
        pageMode: 'diy',
        title: '',
        global: {},
        value: [],
        status: 0
    })

    const getLoading = () => {
        return loading.value;
    }

    const data: any = computed(() => {
        return diyData;
    })

    const pageStyle = () => {
        var style = '';
        if (data.value.global.pageStartBgColor) {
            if (data.value.global.pageStartBgColor && data.value.global.pageEndBgColor) style += `background:linear-gradient(${ data.value.global.pageGradientAngle },${ data.value.global.pageStartBgColor },${ data.value.global.pageEndBgColor });`;
            else style += 'background-color:' + data.value.global.pageStartBgColor + ';';
        }
        if (data.value.global.bgUrl) {
            style += `background-image:url('${ img(data.value.global.bgUrl) }');`;
        }

        if (data.value.global.bgHeightScale) {
            style += `background-size: 100% ${ data.value.global.bgHeightScale }%;`;
        }

        return style;
    };

    // 监听页面加载
    // const createdLifeCycle = (callback: any = null) => {
    //     created((option: any) => {

    //         if (option.form_record_id) {
    //             requestData.form_record_id = option.form_record_id;
    //         }

    //         form_id.value = option.form_id || '';
    //         // if (name.value == '') name.value = option.name || '';
    //         // template.value = option.template || '';
    //         getData(callback)
    //     });
    // }

    const getData = (callback: any = null) => {
        if (!form_id.value) return; // 空值情况下不调用接口

        getDiyFormInfo({
            form_id: form_id.value,
            // name: name.value, todo 暂时没用
            // template: template.value // todo 暂时没用
        }).then((res: any) => {
            Object.assign(requestData, res.data);
            diyData.status = res.data.status;
            if (requestData.value) {
                diyData.pageMode = requestData.mode;
                diyData.title = requestData.title;

                // diyStore.id = requestData.form_id;
                let sources = requestData.value;
                // 匹配缓存，赋值
                diyData.global = sources.global;
                diyData.value = sources.value;
                diyData.value.forEach((item: any, index) => {
                    if (item.isHidden) {
                        // 隐藏组件
                        diyData.value.splice(index, 1)
                        // 过滤表单-提交组件且按钮悬空的情况下不设置样式
                    } else {
                        item.pageStyle = '';
                        if (item.componentName != 'FormSubmit' || item.componentName == 'FormSubmit' && item.btnPosition != 'hover_screen_bottom') {
                            if (item.pageStartBgColor) {
                                if (item.pageStartBgColor && item.pageEndBgColor) item.pageStyle += `background:linear-gradient(${ item.pageGradientAngle },${ item.pageStartBgColor },${ item.pageEndBgColor });`;
                                else item.pageStyle += 'background-color:' + item.pageStartBgColor + ';';
                            }

                            if (item.margin) {
                                if (item.margin.top > 0) {
                                    item.pageStyle += 'padding-top:' + item.margin.top + 'px' + ';';
                                }
                                item.pageStyle += 'padding-bottom:' + item.margin.bottom + 'px' + ';';
                                item.pageStyle += 'padding-right:' + item.margin.both + 'px' + ';';
                                item.pageStyle += 'padding-left:' + item.margin.both + 'px' + ';';
                            }
                        }
                    }
                });
            }

            loading.value = false;

            if (callback) callback(requestData)
        });
    }


    // 监听页面卸载
    const onUnmountedLifeCycle = () => {
        onUnmounted(() => {
        })
    }

    // 监听滚动事件
    // const onPageScrollLifeCycle = () => {
    //     onPageScroll((e) => {
    //         if (e.scrollTop > 0) {
    //             diyStore.scrollTop = e.scrollTop;
    //         }
    //     })
    // }

    return {
        getLoading,
        requestData,
        data: data.value,
        pageStyle,
        // created: createdLifeCycle,
        onUnmounted: onUnmountedLifeCycle,
        // onPageScroll: onPageScrollLifeCycle,
        getData
    }
}
