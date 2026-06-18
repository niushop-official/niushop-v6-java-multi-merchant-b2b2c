<template>
    <el-dialog v-model="showDialog" title="查看地图" width="800px" :close-on-click-modal="true" :destroy-on-close="true" >
        <el-form :model="formData" label-width="0px" ref="formRef" class="page-form mb-[30px]">
            <el-form-item>
                <div>{{ formData.taker_address }}</div>
            </el-form-item>
            <el-form-item >
                <!-- 地图display：none会渲染报错 -->
                <div class="h-auto overflow-hidden">
                    <div id="container" class="w-full h-[400px] relative" v-loading="mapLoading"></div>
                </div>
                <div v-if="mapMessage" class="text-[#f52222] text-[12px] line-height-[15px]">{{ mapMessage }}</div>

            </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button type="primary" @click="showDialog = false">{{ t('confirm') }}</el-button>
            </span>
        </template>
    </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { t } from '@/lang'
import { createMarker, latLngToAddress } from '@/utils/qqmap'
import { getMap } from '@/app/api/sys'
import { cloneDeep } from 'lodash-es'

const showDialog = ref(false)
/**
 * 表单数据
 */
let formData: Record<string, any> = reactive({
    taker_latitude: 0,
    taker_longitude: 0,
    taker_address: '',
})

/**
* 初始化地图
 */
let map: any = null
let marker: any = null
const mapLoading = ref(true)
const mapMessage = ref('')
const initMap = () => {
    const TMap = (window as any).TMap
    const LatLng = TMap.LatLng
    const center = new LatLng(formData.taker_latitude, formData.taker_longitude)
    map = new TMap.Map('container', {
        center,
        zoom: 14
    })

    map.on('tilesloaded', () => {
        mapLoading.value = false
    })

    marker = createMarker(map)

    // map.on('click', (evt: any) => {
    //     map.setCenter(evt.latLng)
    //     marker.updateGeometries({
    //         id: 'center',
    //         position: evt.latLng
    //     })
    //     latLngChange(evt.latLng.lat, evt.latLng.lng)
    // })
    latLngChange(center.lat, center.lng)
}

const latLngChange = (lat: number, lng: number) => {
    latLngToAddress({ mapKey, lat, lng }).then(({ message, result }) => {
        if (message == 'query ok' || message == 'Success') {
            formData.taker_latitude = result.location.lat
            formData.taker_longitude = result.location.lng
            formData.taker_address = result.formatted_addresses && result.formatted_addresses.recommend ? result.formatted_addresses.recommend : ''
        } else {
            mapMessage.value = '地图加载失败：' + message
            // console.log(message, result)
        }
    }).catch(err => {
        // console.log(err)
    })
}

let mapKey: string = ''

onMounted(() => {
    const mapScript = document.createElement('script')
    getMap().then(res => {
        mapKey = res.data.key
        mapScript.type = 'text/javascript'
        mapScript.src = 'https://map.qq.com/api/gljs?libraries=tools,service&v=1.exp&key=' + res.data.key
        document.body.appendChild(mapScript)
    })
})

// 初始化
const setFormData = async (data: any) => {
    formData = cloneDeep(data)
    showDialog.value = true
    nextTick(() => {
        initMap()
    })
}

defineExpose({
    showDialog,
    setFormData
})
</script>

<style scoped>

</style>