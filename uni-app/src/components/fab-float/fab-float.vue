<template>
	<view v-if="fabMenuData && fabMenuData.is_show">
		<view style="touch-action: none;">
			<view class="home" style="position:fixed;" :style="{ top: top + 'px'}" @touchmove.stop.prevent="setTouchMove">
                <!-- 数列 -->
                <view class="fab1 absolute right-[20rpx] bottom-0 rounded-[44rpx] p-[10rpx]"  v-if="fabMenuData.style.type == 1">
                    <view :style="{ display: homeActive == false ? 'none' : '' }" class="relative z-[2]">
                        <view class="img-box w-[70rpx] h-[70rpx] rounded-full bg-[#fff] mb-[16rpx]" v-for="(item,index) in fabMenuData.navs"  :key="index" @click="handleSelect(item)">
                            <image class="w-full h-full rounded-full" :src="img(item.icon)"></image>
                        </view>
                    </view>
                    <view @click="open" class="relative z-[2]">
                       <view class="w-[70rpx] h-[70rpx] rounded-full bg-[#fff]">
                            <image class="w-full h-full rounded-full" :src="img(fabMenuData.style.icon)"></image>
                       </view>
                    </view>
                </view>
                <!-- 横排 -->
                <view class="fab2 flex items-center relative p-[10rpx]" v-if="fabMenuData.style.type == 2">
                    <view class="relative z-[2] flex" v-if="homeActive == true">
                        <view class="img-box relative z-[2] w-[70rpx] h-[70rpx] rounded-full bg-[#fff]" :class="{'ml-[16rpx]': index}" v-for="(item,index) in fabMenuData.navs" :key="index" @click="handleSelect(item)">
                            <image class="w-full h-full rounded-full" :src="img(item.icon)"></image>
                        </view>
                    </view>
                    <view v-if="homeActive == false" @click="open">
                       <view class="relative z-[2] w-[70rpx] h-[70rpx] rounded-full bg-[#fff]">
                            <image class="w-full h-full rounded-full" :src="img(fabMenuData.style.icon)"></image>
                       </view>
                    </view>
                    <view class ="relative z-[2] flex items-center justify-center w-[28rpx] h-[28rpx] ml-[16rpx]" v-if="homeActive == true" @click="open">
                        <text class="nc-iconfont nc-icon-youV6xx text-[28rpx] text-[#333]"></text>
                    </view>
                </view>
                <!-- 扇形 -->
                <view class="menus" :class="{'menus-open':homeActive == true }" v-if="fabMenuData.style.type == 3">
                    <view style="height: 149px; width: 1px;"></view>
                    <view class="menu menu-main rounded-full" @click="open">
                        <image class="w-full h-full rounded-full" :src="img(fabMenuData.style.icon)"></image>
                    </view>
                    <view :class="`menu menu-item menu-${index + 1}`"  v-for="(item,index) in fabMenuData.navs" :key="index" @click="handleSelect(item)">
                        <image class="w-[70rpx] h-[70rpx] rounded-full" :src="img(item.icon)"></image>
                    </view>
                </view>
                <!-- 弓状 -->
                <view class="menus fab4" :class="{'menus-open':homeActive == true }"  v-if="fabMenuData.style.type == 4">
                    <view style="height: 175px; width: 1px;"></view>
                    <view class="menu-bag" :class="{'close-bag': homeActive == false && flag, 'has-bag': homeActive == true}"></view>
                    <view class="menu menu-main bg-[#fff] z-10"  @click="open">
                        <text class="nc-iconfont nc-icon-guanbiV6xx-1 text-[#333] text-[34rpx]" v-if="homeActive == true"></text>
                    </view>
                    <view :class="`menu menu-item menu-${index + 1}`"  :style="{zIndex: homeActive == true ? 999 : -1 }" v-for="(item,index) in fabMenuData.navs" :key="index" @click="handleSelect(item)">
                        <image class="w-[70rpx] h-[70rpx] rounded-full" :src="img(item.icon)" mode="aspectFill" />
                    </view>
                </view>
			</view>
		</view>
	</view>
</template>

<script setup lang="ts">
import { ref, onBeforeUnmount, onMounted } from 'vue';
import { img, redirect } from '@/utils/common';
import { getFabNav } from '@/app/api/system'

const top = ref(445); 
const homeActive = ref(false)
const fabMenuData = ref<any>(null);
const flag = ref(false);

onMounted(() => {
    getFabMenuList();
});

const getFabMenuList = () => {
    getFabNav().then((res: any) => {
        fabMenuData.value = res.data;
    }).catch((err: any) => {
    });
}
const setTouchMove = (e: any) => {
    // 限制拖动范围：66px ~ 545px
    if (e.touches[0].clientY < 545 && e.touches[0].clientY > 66) {
        top.value = e.touches[0].clientY; 
    }
};

/**
 * 切换悬浮菜单显示/隐藏
 */
const open = () => {
    if (homeActive.value) {
       homeActive.value = false
       if(fabMenuData.value.style.type == 4){
            flag.value = true;
       }
    } else {
        homeActive.value = true
    }
};

const handleSelect = (item: any) => {
  redirect({url: item.url.value, mode: 'redirectTo'})
}

onBeforeUnmount(() => {
    homeActive.value = false
});

// 若需要暴露属性/方法（可选，按需使用）
defineExpose({
    top,
    open
});
</script>

<style lang="scss" scoped>
.home {
    position: fixed;
    color: #fff;
    text-align: center;
    z-index: 999;
    display: flex;
    right: 0;
    -webkit-transform: translateY(-50%);
    transform: translateY(-50%);
    .fab1{
        background: hsla(0, 0%, 100%, .5);
        -webkit-backdrop-filter: blur(5px);
        backdrop-filter: blur(5px);
        .img-box{
            box-shadow: 0px 0px 10px 0px rgba(0, 0, 0, 0.2);
        }
    }
    .fab2{
        border-radius: 22px 0 0 22px;
        background: hsla(0, 0%, 100%, .5);
        -webkit-backdrop-filter: blur(5px);
        backdrop-filter: blur(5px);
        .img-box{
            box-shadow: 0px 0px 8px 0px rgba(0, 0, 0, .1);
        }
    }
    .menus{
        width: 35px;
        height: 35px;
        line-height: 35px;
        position: relative;
        right: 10px;
        font-size: 12px;
        color: #fff;
        margin: auto;
        .menu{
            display: block;
            text-align: center;
            border-radius: 50%;
            position: absolute;
            top: 50%;
            left: 50%;
            -webkit-transform: translate(-50%, -50%);
            transform: translate(-50%, -50%);
            transition-timing-function: cubic-bezier(.25, 1.2, .45, 1);
            transition-duration: 1s;
        }
        .menu-main{
            z-index: 2;
            width: 35px;
            height: 35px;
            line-height: 35px;
            font-size: 36px;
            box-shadow: 0 0 8px 0 rgba(0, 0, 0, .1);
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .menu-item{
            width: 35px;
            height: 35px;
            line-height: 35px;
            color: inherit;
            background-color: var(--color);
            box-shadow: 0px 0px 8px 0px rgba(0, 0, 0, .1);
            -webkit-transform: translate(-50%, -50%);
            transform: translate(-50%, -50%);
        }
    }
    .menus-open{
        .menu-1{
            -webkit-transform: translate(-50%, calc(-50% - 50px));
            transform: translate(-50%, calc(-50% - 50px));
        }
        .menu-2{
            -webkit-transform: translate(calc(-50% - 45px), calc(-50% - 25px));
            transform: translate(calc(-50% - 45px), calc(-50% - 25px));
        }
        .menu-3{
            -webkit-transform: translate(calc(-50% - 45px), calc(-50% + 25px));
            transform: translate(calc(-50% - 45px), calc(-50% + 25px));
        }
        .menu-4{
            -webkit-transform: translate(-50%, calc(-50% + 50px));
            transform: translate(-50%, calc(-50% + 50px));
        }
    }
    .fab4{
        .menu-bag{
            position: absolute;
            top: 50%;
            left: 50%;
            -webkit-transform: translate(-50%, -50%);
            transform: translate(-50%, -50%);
            width: 35px;
            height: 35px;
            border-radius: 50%;
            background: rgba(51, 51, 51, .1);
            -webkit-backdrop-filter: blur(3px);
            backdrop-filter: blur(3px);
            box-shadow: 0px 0px 6px 0px rgba(0, 0, 0, .1); 
        }
        .close-bag{
            background: rgba(51, 51, 51, .1);
            -webkit-backdrop-filter: blur(3px);
            backdrop-filter: blur(3px);
        }
        .has-bag{
            background: hsla(0, 0%, 100%, .5);
        }
        .menu-main{
            width: 24px;
            height: 24px;
        }
        &.menus-open{
           .menu-main{
                width: 35px;
                height: 35px;
           }
        }
    }
}
.close-bag {
    -webkit-animation: close .5s ease forwards;
    animation: close .5s ease forwards;
}
.has-bag {
    border-radius: 50%;
    -webkit-animation: show .3s ease forwards;
    animation: show .3s ease forwards;
}
@keyframes show {  
    0% {
        width: 70rpx;
        height: 70rpx;
    }
    100% {
        width: 350rpx;
        height: 350rpx;
    }
}
@keyframes close {  
    0% {
        width: 350rpx;
        height: 350rpx;
        
    }
    100% {
        width: 70rpx;
        height: 70rpx;
    }
}
</style>
