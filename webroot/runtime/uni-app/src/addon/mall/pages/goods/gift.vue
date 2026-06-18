<template>
  <view class="min-h-[100vh] bg-[var(--page-bg-color)]" :style="themeColor()">
    <view class="bg-linear h-[320rpx]">
      <!-- #ifdef MP-WEIXIN || APP-PLUS -->
      <view class="absolute top-0 left-0 right-0 z-999">
        <top-tabbar :data="topTabbarData" class="top-header" :isFill="false" />
      </view>
      <!-- #endif -->
    </view>
    <view class="relative">
      <view class="sidebar-margin card-template top-card" v-if="!loading">
        <view class="mb-[34rpx] flex items-center">
          <up-image width="46rpx" height="46rpx" :radius="'var(--goods-rounded-big)'" :src="img(activeInfo.site?.logo)" model="aspectFill">
            <template #error>
              <image class="rounded-[var(--goods-rounded-big)] overflow-hidden w-[46rpx] h-[46rpx]" :src="img('addon/mall/store_default.png')" mode="aspectFill" />
            </template>
          </up-image>
          <text class="text-[28rpx] font-400 ml-[12rpx] flex-1 truncate">{{ activeInfo.site?.site_name }}</text>
        </view>
        <view class="mb-[26rpx]">
          <view class="flex items-start justify-between">
            <view>
              <view class="text-[30rpx] font-700">
                <template v-if="currentType == 'full_gift'">
                  店铺内下单送赠品
                </template>
                <template v-else-if="currentType == 'discount'">
                  店铺内下单折扣立减
                </template>
                <template v-else>
                  店铺内下单满{{ activeInfo.type=='amount'?'元':'件' }}{{ activeInfo.promotion_type == 'reduction' ? '直减' : '优惠' }}
                </template>
              </view>
            </view>
            <view class="flex items-center">
              <view class="text-[#FF0000] mr-[10rpx] text-[24rpx]">距结束</view>
              <up-count-down class="text-[#FF0000] text-[24rpx] " :time="discountTime" format="HH:mm:ss" autoStart millisecond @change="onChange">
                <view class="flex items-center gap-[6rpx] text-[#fff] leading-[1]">
                  <view v-if="timeData.days > 0" class="text-[24rpx] flex items-center justify-center min-w-[30rpx] h-[32rpx] rounded-[6rpx] bg-[#FF0000] text-[#fff] px-[2rpx]">
                    <text class="min-w-[30rpx] text-center" v-if="timeData.days && timeData.days > 0">{{ timeData.days }}</text>
                  </view>
                  <text v-if="timeData.days > 0" class="text-[#FF0000] text-[24rpx]">天</text>
                  <view
                      class="text-[24rpx] flex items-center justify-center min-w-[30rpx] h-[32rpx] rounded-[6rpx] bg-[#FF0000] text-[#fff] px-[2rpx]">
                    <text class="min-w-[30rpx] text-center" v-if="timeData.hours && timeData.hours > 0">{{ timeData.hours >= 10 ? timeData.hours : '0' + timeData.hours }}</text>
                    <text class="min-w-[30rpx] text-center" v-else>00</text>
                  </view>
                  <text class="text-[#FF0000]">:</text>
                  <view class="text-[24rpx] flex items-center justify-center min-w-[30rpx] h-[32rpx] rounded-[6rpx] bg-[#FF0000] text-[#fff] px-[2rpx]">
                    <text class="min-w-[30rpx] text-center" v-if="timeData.minutes && timeData.minutes > 0">{{ timeData.minutes >= 10 ? timeData.minutes : '0' + timeData.minutes }}</text>
                    <text class="min-w-[30rpx] text-center" v-else>00</text>
                  </view>
                  <text class="text-[#FF0000]">:</text>
                  <view class="text-[24rpx] flex items-center justify-center min-w-[30rpx] h-[32rpx] rounded-[6rpx] bg-[#FF0000] text-[#fff] px-[2rpx]">
                    <text class="min-w-[30rpx] text-center" v-if="timeData.seconds && timeData.seconds > 0">{{ timeData.seconds < 10 ? '0' + timeData.seconds : timeData.seconds }}</text>
                    <text class="min-w-[30rpx] text-center" v-else>00</text>
                  </view>
                </view>
              </up-count-down>
            </view>
          </view>
        </view>
        <view class="text-[#999] text-[24rpx] mt-[10rpx]">
            有效期限：{{ activeInfo.start_time }}-{{ activeInfo.end_time }}
          </view>
        <view v-for="(item, index) in activeInfo.rule_json" :key="index">
          <template v-if="currentType == 'full_gift'" >
            <view class="card-template mt-[20rpx]" :style="headBgStyle">
              <view class="text-[28rpx]">
                <text v-if="activeInfo.rule_type!='ladder'">每</text>满<text class="price-font text-[#FF1E00]">{{ item.amount }}元</text>送以下赠品
              </view>
              <view v-for="(citem, cindex) in item.gift_sku_data"
                    class="rounded-[24rpx] bg-[#fff] flex flex-col mt-[20rpx]">
                <view class="p-[20rpx] flex">
                  <view class="w-[120rpx] h-[120rpx] overflow-hidden" @click="toLink(citem)">
                    <up-image width="120rpx" height="120rpx" :radius="'var(--goods-rounded-big)'" :src="img(citem.sku_image)" model="aspectFill" >
                      <template #error>
                        <image class="rounded-[var(--goods-rounded-big)] overflow-hidden w-[120rpx] h-[120rpx]" :src="img('static/resource/images/diy/shop_default.jpg')" mode="aspectFill" />
                      </template>
                    </up-image>
                  </view>
                  <view class="flex-1 ml-[10rpx]">
                    <view class="flex justify-between">
                      <view class="text-[28rpx] leading-[40rpx] text-[#303133] using-hidden mb-[10rpx]">
                        {{ citem.goods_name }}
                      </view>
                      <view>×{{ citem.num }}</view>
                    </view>
                    <view class="text-[24rpx] text-[#999]">
                      {{ citem.sku_name }}
                    </view>
                  </view>
                </view>
              </view>
            </view>
          </template>
          <template v-else >
            <view class="card-template mt-[20rpx] border-[#eee] border-[1rpx] border-solid !rounded-[20rpx]">
              <view class="text-[28rpx] leading-[38rpx]">
                <text v-if="activeInfo.rule_type!='ladder'">每</text>满<text class="price-font text-[#FF1E00] ">{{ item.amount }}{{ activeInfo.type=='amount'?'元':'件' }}</text>{{ activeInfo.promotion_type == 'reduction' ? '直减' : '打' }}<text class="price-font">{{ item.reduce }}</text>{{ activeInfo.promotion_type == 'reduction' ? '元' : '折' }}
              </view>
             
            </view>
          </template>
        </view>
      </view>

      <view class="goods-recommend" v-if="!loading">
        <view class="mt-[60rpx] flex flex-col items-center sidebar-margin">
          <view class="flex items-center mb-[30rpx]">
            <image class="w-[100rpx] h-[16rpx]" :src="img('addon/mall/gift_left.png')" mode="aspectFill" />
            <text class="text-[30rpx] mx-[18rpx] font-500 ">适用商品</text>
            <image class="w-[100rpx] h-[16rpx]" :src="img('addon/mall/gift_right.png')" mode="aspectFill" />
          </view>
          <view class="biserial-goods-list">
            <view>
              <template v-for="(item, index) in goodsList" :key="index">
                <view v-if="(index % 2) == 0" class="flex flex-col bg-[#fff] box-border overflow-hidden rounded-[24rpx]"
                      :class="{ 'mt-[24rpx]': index > 1 }" @click="toLink(item)">
                  <up-image  :width="style2Width" :height="style2Width" :src="img(item.sku_image || '')" model="aspectFill">
                    <template #error>
                      <image :style="{ 'width': style2Width, 'height': style2Width}" :src="img('static/resource/images/diy/shop_default.jpg')"
                             mode="aspectFill" />
                    </template>
                  </up-image>
                  <view class="relative min-h-[44rpx] px-[16rpx] flex-1 pt-[16rpx] pb-[20rpx] flex flex-col justify-between">
                    <view class="text-[#303133] leading-[40rpx] text-[28rpx] multi-hidden">
                      {{ item.goods_name }}
                    </view>
                    <view class="text-[24rpx] text-[#999] leading-[30rpx] using-hidden my-[5rpx]" v-if="item.sku_name">
                      {{ item.sku_name }}
                    </view>
                    <view class="flex justify-between flex-wrap items-center mt-[20rpx]">
                      <view class="flex flex-col">
                        <view class="flex items-baseline leading-[1]">
                          <view class="text-[var(--price-text-color)] price-font block truncate max-w-[270rpx]">
                            <text class="text-[24rpx] font-400">￥</text>
                            <text class="text-[40rpx] font-500">{{ parseFloat(item.price).toFixed(2).split('.')[0] }}</text>
                            <text class="text-[24rpx] font-500">.{{ parseFloat(item.price).toFixed(2).split('.')[1] }}</text>
                          </view>
                        </view>
                        <text class="text-[22rpx] mt-[8rpx] text-[var(--text-color-light9)]">
                          已售{{ item.stock }}{{ item.unit || '件' }}
                        </text>
                      </view>
                    </view>
                  </view>
                </view>
              </template>
            </view>
            <view>
              <template v-for="(item, index) in goodsList"  :key="index">
                <view v-if="(index % 2) == 1" class="flex flex-col bg-[#fff] box-border overflow-hidden rounded-[24rpx]"
                      :class="{ 'mt-[24rpx]': index > 1 }" :style="itemCss" @click="toLink(item)">
                  <up-image :width="style2Width" :height="style2Width" :src="img(item.sku_image || '')" model="aspectFill">
                    <template #error>
                      <image :style="{ 'width': style2Width, 'height': style2Width}" :src="img('static/resource/images/diy/shop_default.jpg')"
                             mode="aspectFill" />
                    </template>
                  </up-image>
                  <view class="relative min-h-[44rpx] px-[16rpx] flex-1 pt-[16rpx] pb-[20rpx] flex flex-col justify-between">
                    <view class="text-[#303133] leading-[40rpx] text-[28rpx] multi-hidden">
                      {{ item.goods_name }}
                    </view>
                    <view class="text-[24rpx] text-[#999] leading-[30rpx] using-hidden my-[5rpx]" v-if="item.sku_name">
                      {{ item.sku_name }}
                    </view>
                    <view class="flex justify-between flex-wrap items-center mt-[20rpx]">
                      <view class="flex flex-col">
                        <view class="flex items-baseline leading-[1]">
                          <view class="text-[var(--price-text-color)] price-font block truncate max-w-[270rpx]">
                            <text class="text-[24rpx] font-400">￥</text>
                            <text class="text-[40rpx] font-500">{{ parseFloat(item.price).toFixed(2).split('.')[0] }}</text>
                            <text class="text-[24rpx] font-500">.{{ parseFloat(item.price).toFixed(2).split('.')[1] }}</text>
                          </view>
                        </view>
                        <text class="text-[22rpx] mt-[8rpx] text-[var(--text-color-light9)]">
                          已售{{ item.stock }}{{ item.unit || '件' }}
                        </text>
                      </view>
                    </view>
                  </view>
                </view>
              </template>
            </view>
          </view>

          <!-- 新增：加载状态提示 -->
          <view class="text-[24rpx] text-[#999] py-[30rpx]">
            <text v-if="pageParams.loadingMore">正在加载更多...</text>
            <text v-else-if="!pageParams.hasMore && goodsList.length > 0">没有更多商品了</text>
            <text v-else-if="goodsList.length === 0">暂无适用商品</text>
          </view>
        </view>
        <view class="h-[50rpx]"></view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { reactive, ref, computed, nextTick } from 'vue'
import { t } from '@/locale'
import { redirect, img, pxToRpx, timeTurnTimeStamp } from '@/utils/common';
import { onLoad, onPageScroll, onReachBottom } from '@dcloudio/uni-app';
import { topTabar } from '@/utils/topTabbar'
import { useGoods } from '@/addon/mall/hooks/useGoods'
import { getShopMarketingList, getShopMarketingInfo, getMarketingGoodsList } from '@/addon/mall/api/goods';

const loading = ref<boolean>(true);
// 获取系统状态栏的高度
let menuButtonInfo: any = {};
// 如果是小程序，获取右上角胶囊的尺寸信息，避免导航栏右侧内容与胶囊重叠(支付宝小程序非本API，尚未兼容)
// #ifdef MP-WEIXIN || MP-BAIDU || MP-TOUTIAO || MP-QQ
menuButtonInfo = uni.getMenuButtonBoundingClientRect();
// #endif
/********* 自定义头部 - start ***********/
const topTabarObj = topTabar()
let topTabbarData = topTabarObj.setTopTabbarParam({ title: '优惠活动' })
const topStyle = computed(() => {
  let style = pxToRpx(Number(menuButtonInfo.height) + menuButtonInfo.top + 8) + 30 + 'rpx;'
  return style
})
/********* 自定义头部 - end ***********/
const activeList = ref([])
const currentType = ref('')
const activeInfo = ref({})
const goodsList = ref([]) // 新增：存放新接口返回的商品列表
const discountTime = ref(0)
const timeData = ref({});

// ========== 新增：滑动加载核心参数 ==========
const pageParams = reactive({
  page: 1,         // 当前页码
  size: 10,        // 每页条数
  hasMore: true,   // 是否还有更多数据
  loadingMore: false // 是否正在加载更多
});

// ========== 新增：加载商品列表通用方法 ==========
const loadGoodsList = async (isLoadMore = false) => {
  // 边界判断：无更多数据 或 正在加载中，直接返回
  if (!pageParams.hasMore || pageParams.loadingMore) return;

  // 标记加载状态
  pageParams.loadingMore = true;

  try {
    const goods_data = uni.getStorageSync('manjianData')
    const marketingType = goods_data.type;
    const marketingId = activeInfo.value.id;

    // 调用商品接口（传递分页参数）
    const res = await getMarketingGoodsList(
        marketingType,
        Number(marketingId),
        {
          page: pageParams.page,
          size: pageParams.size
        }
    );

    // 处理返回数据
    const data = res.data?.data || [];
    if (data.length > 0) {
      if (isLoadMore) {
        // 加载更多：追加数据
        goodsList.value = [...goodsList.value, ...data];
      } else {
        // 首次加载：替换数据
        goodsList.value = data;
      }
      // 判断是否还有更多（返回条数 < 每页条数 → 无更多）
      pageParams.hasMore = data.length >= pageParams.size;
    } else {
      // 无数据时标记为无更多
      pageParams.hasMore = false;
      if (!isLoadMore) goodsList.value = [];
    }
  } catch (err) {
    console.error('获取营销商品列表失败：', err);
    uni.showToast({ title: '加载失败，请重试', icon: 'none' });
  } finally {
    // 结束加载状态
    pageParams.loadingMore = false;
  }
};

onLoad((options) => {
  const goods_data = uni.getStorageSync('manjianData')
  currentType.value = goods_data.type

  getShopMarketingInfo({
    market_ids:{
      [goods_data.type]: goods_data.id
    }
  }).then(async (res: any) => {
    // 保存活动详情
    activeInfo.value = res.data[goods_data.type]
    // 获取当前时间，计算倒计时
    const now = new Date();
    const currentTotalSeconds = Math.floor(now.getTime() / 1000);
    discountTime.value = (timeTurnTimeStamp(activeInfo.value.end_time) - currentTotalSeconds) * 1000;

    // 首次加载商品列表
    await loadGoodsList(false);

    nextTick(() => {
      loading.value = false
    })
  }).catch((err) => {
    // 活动详情接口报错的异常处理
    console.error('获取活动详情失败：', err);
    loading.value = false;
  })
})

// ========== 新增：监听上拉触底事件 ==========
onReachBottom(() => {
  if (pageParams.hasMore && !pageParams.loadingMore) {
    pageParams.page += 1; // 页码+1
    loadGoodsList(true);  // 标记为加载更多
  }
});

const headBgStyle = computed(() => ({
  'background-image': `url(${img('addon/mall/gift_bg.png')})`,
  'background-repeat': 'no-repeat',
  'background-size': '100% 100%'
}))

// 定义 onChange 方法
const onChange = (e) => {
  timeData.value = e;
};

const style2Width = computed(() => {
  let style = '';
  style += 'calc((100vw - 60rpx) / 2 )'
  return style;
})

// 新增：修复itemCss未定义问题（原代码使用但未定义）
const itemCss = computed(() => ({}));

const toLink = (data: any) => {
  redirect({ url: '/addon/mall/pages/goods/detail', param: { goods_id: data.goods_id } })
}
</script>

<style lang="scss" scoped>
.top-image {
  /*  #ifdef  H5  */
  margin-top: -0rpx;
  /*  #endif  */
  /*  #ifndef  H5  */
  margin-top: 0;
  /*  #endif  */
}
.content{
  background: rgba(255, 255, 255, 0.3);
}
.bg-linear {
  background: linear-gradient(180deg, #FF4617 28.04%, #ff851a00 100%);
}

.active {
  background: #fff;
  color: #FF1111;
}

.biserial-goods-list {
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-gap: 10px;
}
.top-card{
  /*  #ifdef  H5  */
  margin-top: -285rpx;
  /*  #endif  */
  /*  #ifndef  H5  */
  margin-top: -130rpx;
  /*  #endif  */
}
</style>