<template>
    <template v-if="meta.show">
        <el-sub-menu v-if="routes.children" :index="String(routes.name)">
            <template #title>
                <div class="w-[25px] flex items-center justify-center flex-shrink-0">
                    <template v-if="meta.icon">
                        <el-image class="w-[16px] h-[16px] overflow-hidden" :src="meta.icon" fit="fill" v-if="isUrl(meta.icon)"/>
                        <icon :name="meta.icon" v-else />
                    </template>
                    <icon v-else :name="'iconfont iconshezhi1'" />
                </div>
                <span class="text-[14px]">{{ meta.title }}</span>
            </template>
            <menu-item v-for="(route, index) in routes.children" :routes="route" :key="index" :level="props.level + 1" />
        </el-sub-menu>
        <el-menu-item :index="String(routes.name)" @click="router.push({ name: routes.name })" v-else>
            <div class="w-[25px] flex items-center justify-center flex-shrink-0" v-if="props.level == 1">
                <template v-if="meta.icon">
                    <el-image class="w-[16px] h-[16px] overflow-hidden" :src="meta.icon" fit="fill" v-if="isUrl(meta.icon)"/>
                    <icon :name="meta.icon" v-else />
                </template>
                <icon v-else :name="'iconfont iconshezhi1'" />
            </div>
            <template #title>
                <span class="text-[14px]">{{ meta.title }}</span>
            </template>
        </el-menu-item>
    </template>

</template>

<script lang="ts" setup>
import { useRoute, useRouter } from 'vue-router'
import { ref, computed } from 'vue'
import { img, isUrl } from '@/utils/common'
import menuItem from './menu-item.vue'

const router = useRouter()
const route = useRoute()

const props = defineProps({
    routes: {
        type: Object,
        required: true
    },
    collapse: {
        type: Boolean,
        required: false
    },
    level: {
        type: Number,
        default: 1
    }
})

const meta = computed(() => props.routes.meta)
const currentIndex: any = ref<any>('')

if (meta.value.level == 3 && props.routes.children && props.routes.children.length) {
    // 四级菜单，默认选中第一个
    currentIndex.value = props.routes.children[0].name

    // 父级相同的菜单，设置选中效果
    if (route.meta.parent_key == props.routes.children[0].meta.parent_key) {
        currentIndex.value = route.name
    } else if (route.meta.level > 4) {
        // 属于父级的子级菜单，将三级菜单设为选中
        for (let i = 0; i < props.routes.children.length; i++) {
            if (route.meta.parent_key == props.routes.children[i].name) {
                currentIndex.value = route.name
                break
            }
        }
    }
}

</script>

<style lang="scss">
</style>
