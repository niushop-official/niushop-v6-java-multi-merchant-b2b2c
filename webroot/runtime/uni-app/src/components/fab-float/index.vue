<template>
  <movable-area class="fab-container" :style="{'z-index': props.zIndex,}" v-if="Object.keys(FabMenuData).length && FabMenuData.is_show">
    <movable-view
      class="fab-button"
      :x="x"
      :y="y"
      :disabled="!props.draggable"
      direction="all"
      :out-of-bounds="false"
      :damping="20"
      :friction="2"
      :scale="false"
      :animation="true"
      @change="handleChange"
      @scale="handleScale"
      @touchend="handleTouchEnd">
      <view
        class="fab-main-button"
        :class="{ 'fab-main-button-active': isMenuOpen }"
        @tap="handleMainButtonClick">
        <slot name="main-button">
            <text class="nc-iconfont nc-icon-guanbiV6xx-1 text-[34rpx] font-500" v-if="isMenuOpen"></text>
            <text class="fab-icon" v-else></text>
        </slot>
      </view>
      <view class="fab-menu" :class="{ 'fab-menu-active': isMenuOpen }">
        <Circle
          v-if="props.layout === 'circle'"
          :menuItems="FabMenuData.navs"
          :isOpen="isMenuOpen"
          :position="[x, y]"
          @select="handleMenuItemClick"
          @close="handleMainButtonClick">
          <template #menu-item="{ data, index }">
            <slot name="menu-item" :data="data" :index="index"> </slot>
          </template>
        </Circle>
        <Column
          v-if="props.layout === 'column'"
          :position="[x, y]"
          :menuItems="FabMenuData.navs"
          :isOpen="isMenuOpen"
          :direction="props.direction"
          @select="handleMenuItemClick">
          <template #menu-item="{ data, index }">
            <slot name="menu-item" :data="data" :index="index"></slot>
          </template>
        </Column>
      </view>
    </movable-view>
  </movable-area>
</template>

<script setup>
import { ref, defineProps, watchEffect,  onMounted } from "vue";
import Circle from "./components/circle.vue";
import Column from "./components/column.vue";
import { getFabNav } from '@/app/api/system'

// 组件属性定义
const props = defineProps({
    // 初始位置
    position: {
        type: Array,
        default: () => [999, 999],
    },
    // 按扭是否可以拖动
    draggable: {
        type: Boolean,
        default: true,
    },
    // 是否自动吸附
    autosorption: {
        type: Boolean,
        default: true,
    },
    // 磁吸安全距离
    safeDistance: {
        type: Number,
        default: 12,
    },
    // 底部安全距离
    bottomSafeDistance: {
        type: Number,
        default: 120,
    },
    // 顶部安全距离
    topSafeDistance: {
        type: Number,
        default: 120,
    },
    // 布局模式：circle（环形）或 column（列形）
    layout: {
        type: String,
        default: "column",
        validator: (value) => ["circle", "column"].includes(value),
    },
    // 列行布局弹出方向：left（左侧）或 right（右侧）
    direction: {
        type: String,
        default: "top",
        validator: (value) => ["top", "bottom", "left", "right"].includes(value),
    },
    // 层级
    zIndex: {
        type: Number,
        default: 9999,
    },
});

const emit = defineEmits(["close", "open", "select"]);

// 按钮位置状态
const x = ref(props.position[0]);
const oldX = ref(props.position[0]);
const y = ref(props.position[1]);
const oldY = ref(props.position[1]);

// 菜单状态
const isMenuOpen = ref(false);
const isFirst = ref(true);

const FabMenuData = ref({});

onMounted(() => {
    getFabMenuList();
});

const getFabMenuList = () => {
    getFabNav().then(res => {
        FabMenuData.value = res.data;
    })
}


// 处理拖动事件
const handleChange = (e) => {
    const { x: newX, y: newY } = e.detail;
    oldX.value = newX;
    oldY.value = newY;
};

// 处理触摸结束事件，实现自动吸附
const handleTouchEnd = () => {
    const { windowWidth, windowHeight } = uni.getWindowInfo();
    const threshold = windowWidth * 0.5; // 屏幕中点作为判断阈值
    const rightEdge = windowWidth - props.safeDistance * 4;
    // 根据当前位置决定吸附到哪一边

    x.value = oldX.value;
    y.value = oldY.value;
    setTimeout(() => {
        if (props.autosorption) {
        x.value = oldX.value > threshold ? rightEdge : props.safeDistance;
        }

        if (oldY.value < props.topSafeDistance) {
        y.value = props.topSafeDistance;
        } else if (oldY.value > windowHeight - props.bottomSafeDistance) {
        y.value = windowHeight - props.bottomSafeDistance;
        }
    }, 100);
};

// 这里监听组件传入的 position  如果y值大于屏幕高度减去底部安全距离，则将y值设置为屏幕高度减去底部安全距离
watchEffect(() => {
    if (isFirst.value) {
        isFirst.value = false;
        handleTouchEnd();
    }
});

// 处理缩放事件（目前禁用）
const handleScale = () => {};

// 处理主按钮点击
const handleMainButtonClick = () => {
    isMenuOpen.value = !isMenuOpen.value;
    if (!isMenuOpen.value) {
        emit("close");
    } else {
        emit("open");
    }
};

// 处理菜单项点击
const handleMenuItemClick = (item) => {
    console.log("Menu item clicked:", item.action);
    emit("select", item); // 触发selec
    // 这里可以根据action执行相应的操作
    isMenuOpen.value = false;
    emit("close");
};
</script>

<style lang="scss" scoped>
.fab-container {
    position: fixed;
    width: 100%;
    height: 100%;
    pointer-events: none;
}

.fab-button {
    position: fixed;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 70rpx;
    height:70rpx;
    pointer-events: auto;
}

.fab-main-button {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 70rpx;
    height: 70rpx;
    background: rgba(51, 51, 51, .1);
    border-radius: 50%;
    box-shadow: 0px 0px 6px 0px rgba(0, 0, 0, .1);
    transition: all 0.3s ease;
}

.fab-main-button-active {
    background: #fff;
}

.fab-icon {
    width: 48rpx;
    height: 48rpx;
    line-height: 70rpx;
    border-radius: 50%;
    box-shadow: 0 0 16rpx 0 rgba(0, 0, 0, .1);
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #fff;
}

.fab-menu {
    position: absolute;
    pointer-events: none;
    opacity: 0;
    transition: all 0.3s ease;
}

.fab-menu-item {
    position: absolute;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    transform: scale(0);
    transform-origin: center center;
}

.fab-menu-active {
    pointer-events: auto;
    opacity: 1;
}
</style>
