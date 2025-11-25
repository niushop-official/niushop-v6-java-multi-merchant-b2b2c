/**
 * DIY组件相关修复工具
 */

/**
 * 修复Swiper组件的margin属性类型问题
 * @param value - 可能是number或string的值
 * @returns string类型的值
 */
export function fixSwiperMargin(value: any): string {
    if (typeof value === 'number') {
        return value.toString()
    }
    if (typeof value === 'string') {
        return value
    }
    return '0'
}

/**
 * 修复组件数据中可能的类型问题
 * @param componentData - 组件数据
 * @returns 修复后的组件数据
 */
export function fixComponentData(componentData: any): any {
    if (!componentData || typeof componentData !== 'object') {
        return componentData
    }

    // 深度克隆避免修改原数据
    const fixedData = JSON.parse(JSON.stringify(componentData))

    // 递归修复所有可能的margin属性
    function fixMarginProps(obj: any): void {
        if (!obj || typeof obj !== 'object') return

        // 修复Swiper相关的margin属性
        if (obj.hasOwnProperty('previousMargin') && typeof obj.previousMargin === 'number') {
            obj.previousMargin = obj.previousMargin.toString()
        }
        if (obj.hasOwnProperty('nextMargin') && typeof obj.nextMargin === 'number') {
            obj.nextMargin = obj.nextMargin.toString()
        }

        // 递归处理子对象
        for (const key in obj) {
            if (obj.hasOwnProperty(key) && typeof obj[key] === 'object') {
                if (Array.isArray(obj[key])) {
                    obj[key].forEach((item: any) => fixMarginProps(item))
                } else {
                    fixMarginProps(obj[key])
                }
            }
        }
    }

    fixMarginProps(fixedData)
    return fixedData
}

/**
 * 安全的组件渲染包装器
 * @param renderFn - 渲染函数
 * @returns 包装后的渲染函数
 */
export function safeComponentRender(renderFn: Function) {
    return function(...args: any[]) {
        try {
            return renderFn.apply(this, args)
        } catch (error) {
            console.warn('Component render error caught:', error)
            return null
        }
    }
}
