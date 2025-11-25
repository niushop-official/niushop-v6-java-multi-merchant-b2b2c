<template>
    <div class="min-w-[1200px] bg-[#f0f0f0] mt-[40px]">
		<div class="w-[1200px]  bg-[#f0f0f0] mx-auto border-[#e2e2e2] border-solid border-b-[1px] h-[162px] flex items-center justify-around">
			<template v-for="(item,index) in goodsService" :key="index">
				<div class="flex flex-col items-center justify-center ">
					<div class="w-[50px] h-[50px] mb-[14px]">
						<el-image v-if="item.image" style="width: 50px; height: 50px" :src="img(item.image?item.image:'')" fit="cover" />
					</div>
					<span class="text-[14px] text-[#666]">{{ item.service_name }}</span>
				</div>
			</template>
		</div>
		<div class="w-[1200px] mx-auto pt-[30px] pb-[40px]">
            <p class="text-center text-[#666] text-[14px]">
				<span>友情链接：</span>
				<template v-for="(item,index) in friendlyLink" :key="index">
					<NuxtLink :to="item.link_url" target="_blank">
						<span>{{item.link_title}}</span>
						<span class="mx-[10px] text-[#D9D9D9]"  v-if="index + 1 != friendlyLink.length">|</span>
					</NuxtLink>
				</template>
			</p>
            <p class="text-center mt-[10px] text-[#999] text-[14px]" v-if="copyright">
                <NuxtLink :to="copyright.gov_url" v-if="copyright.gov_record">
                    <span class="mr-2">公安备案号:{{ copyright.gov_record }}</span>
                </NuxtLink>
				<NuxtLink to="https://beian.miit.gov.cn/" v-if="copyright.icp">
					<span class="mr-2">备案号:{{ copyright.icp }}</span>
				</NuxtLink>
                <NuxtLink :to="copyright.copyright_link">
                    <span class="mr-2" v-if="copyright.company_name">{{ copyright.company_name }}</span>
                    <span class="mr-2" v-if="copyright.copyright_desc">©{{ copyright.copyright_desc }}</span>
                </NuxtLink>
			</p>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { getCopyRight } from '@/app/api/system';
import {  ref } from 'vue'
import { getFriendlyLink, getGoodsService } from '@/app/api/index'

const copyright = ref({
	gov_record: '',
	icp: '',
	copyright_link: '',
	company_name: '',
	copyright_desc: '',
	gov_url: ''
})

const friendlyLink = ref([]) // 格式：{ link_title: '', link_url: '' }

const goodsService = ref([]) // 格式：{ service_name: '', image: '' }

const getFriendlyLinkFn = () =>{
	getFriendlyLink().then((res:any) =>{
		friendlyLink.value = res.data
	})
}
getFriendlyLinkFn()
const getGoodsServiceFn = () =>{
	getGoodsService().then((res:any) =>{
		goodsService.value = res.data
	})
}
getGoodsServiceFn()
const getCopy = () => {
    getCopyRight().then((res:any) => {
        copyright.value = res.data
    })
}
getCopy()
</script>

<style lang="scss" scoped>

</style>
