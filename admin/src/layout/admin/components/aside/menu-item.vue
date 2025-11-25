<template>
    <template v-if="meta.show">
        <el-sub-menu v-if="routes.children" :index="String(routes.name)">
            <template #title>
                <div class="w-[25px] h-full flex items-center justify-center">
                    <template v-if="meta.icon">
                        <el-image class="w-[16px] h-[16px] overflow-hidden" :src="meta.icon" fit="fill" v-if="isUrl(meta.icon)"/>
                        <icon :name="meta.icon" v-else />
                    </template>
                    <icon v-else :name="'iconfont iconshezhi1'" />
                </div>
                <span :class="['ml-[10px]']">{{ meta.title }}</span>
            </template>
            <menu-item v-for="(route, index) in routes.children" :routes="route" :key="index" />
        </el-sub-menu>
        <template v-else>
            <el-menu-item :index="String(routes.name)" @click="router.push({ name: routes.name })">
                <template #title>
                    <div class="w-[25px] h-full flex items-center justify-center">
                        <template v-if="meta.icon">
                            <el-image class="w-[16px] h-[16px] overflow-hidden" :src="meta.icon" fit="fill" v-if="isUrl(meta.icon)"/>
                            <icon :name="meta.icon" v-else />
                        </template>
                        <icon v-else :name="'iconfont iconshezhi1'" />
                    </div>
                    <span :class="[{'text-[15px]': routes.meta.class == 1}, {'text-[14px]': routes.meta.class != 1}, {'ml-[10px]': routes.meta.class == 2, 'ml-[15px]': routes.meta.class == 3}]">{{ meta.title }}</span>
                </template>
            </el-menu-item>
        </template>
        <div v-if="routes.is_border" class="!border-0 !border-t-[1px] border-solid mx-[25px] bg-[#f7f7f7] my-[5px]"></div>
    </template>

</template>

<script lang="ts" setup>
import { useRouter } from 'vue-router'
import { computed } from 'vue'
import { img, isUrl } from '@/utils/common'
import menuItem from './menu-item.vue'
import useUserStore from '@/stores/modules/user'

const router = useRouter()
const props = defineProps({
    routes: {
        type: Object,
        required: true
    }
})
const userStore = useUserStore()
const siteInfo = userStore.siteInfo
const meta = computed(() => props.routes.meta)
const addons = computed(() => {
    const addons:Record<string, any> = {}
    siteInfo?.apps.forEach((item: any) => { addons[item.key] = item })
    siteInfo?.site_addons.forEach((item: any) => { addons[item.key] = item })
    return addons
})

</script>

<style lang="scss">
.el-sub-menu{
    .el-icon{
        width: auto;
    }

    li{
        font-size: 15px;
    }
}
.admin-side{
   .el-icon{
        margin: 0 !important;
    }
}
</style>
