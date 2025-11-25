import useMemberStores from '@/stores/member'
import { ElMessage } from 'element-plus'
/**
 * 获取token
 * @returns
 */
export function getToken(): null | string {
    return useMemberStores().token
}
/**
 * 防抖函数
 * @param fn
 * @param delay
 * @returns
 */
export function debounce(fn: (args?: any) => any, delay: number = 300) {
    let timer: null | number = null
    return function (...args: any) {
        if (timer != null) {
            clearTimeout(timer)
            timer = null
        }
        timer = setTimeout(() => {
            fn.call(this, ...args)
        }, delay);
    }
}

/**
* 判断是否是url
* @param str 
* @returns 
*/
export function isUrl(str: string): boolean {
    return str.indexOf('http://') != -1 || str.indexOf('https://') != -1
}

/**
* 图片输出
* @param path 
* @returns 
*/
export function img(path: string): string {
    const runtimeConfig = useRuntimeConfig()
    return isUrl(path) ? path : `${runtimeConfig.public.VITE_IMG_DOMAIN || location.origin}/${path}`
}

/**
 * 金额格式化
 */
export function moneyFormat(money: string): string {
    return isNaN(parseFloat(money)) ? money : parseFloat(money).toFixed(2)
}

/**
 * @description 深度克隆
 * @param {object} obj 需要深度克隆的对象
 * @returns {*} 克隆后的对象或者原值（不是对象）
 */
export function deepClone(obj: object) {
    // 对常见的“非”值，直接返回原来值
    if ([null, undefined, NaN, false].includes(obj)) return obj
    if (typeof obj !== 'object' && typeof obj !== 'function') {
        // 原始类型直接返回
        return obj
    }
    const o = isArray(obj) ? [] : {}
    for (const i in obj) {
        if (obj.hasOwnProperty(i)) {
            o[i] = typeof obj[i] === 'object' ? deepClone(obj[i]) : obj[i]
        }
    }
    return o
}
const isArray = (value: any) => {
    if (typeof Array.isArray === 'function') {
        return Array.isArray(value)
    }
    return Object.prototype.toString.call(value) === '[object Array]'
}

/**
 * 复制
 * @param {Object} message
 * @param {Object} callback
 */
export function copy(value, callback) {
    var oInput = document.createElement('input'); //创建一个隐藏input（重要！）
    oInput.value = value; //赋值
    oInput.setAttribute("readonly", "readonly");
    document.body.appendChild(oInput);
    oInput.select(); // 选择对象
    document.execCommand("Copy"); // 执行浏览器复制命令
    oInput.className = 'oInput';
    oInput.style.display = 'none';
    document.body.removeChild(oInput); // 删除创建的对象
    ElMessage({
        message: '复制成功',
        type: 'success',
      })
    typeof callback == 'function' && callback();
}

/**
 * 过滤特殊字符
  * @param event
 */
export function filterSpecial(event:any){
    event.target.value = event.target.value.replace(/[^\u4e00-\u9fa5a-zA-Z0-9]/g, '')
    event.target.value = event.target.value.replace(/[`~!@#$%^&*()_\-+=<>?:"{}|,.\/;'\\[\]·~！@#￥%……&*（）——\-+={}|《》？：“”【】、；‘’，。、]/g,'')
}


// 获取有效期
export function getValidTime(minutes: any = 1) {
    var date = new Date();
    date.setSeconds(60 * minutes);
    let validTime: any = parseInt(date.getTime() / 1000); // 定位信息 5分钟内有效，过期后将重新获取定位信息
    return validTime;
}

/**
 * 时间戳转日期格式
 * @param timeStamp
 * @param type
 */
export function timeStampTurnTime(timeStamp: any, type = "") {
if (timeStamp != undefined && timeStamp != "" && timeStamp > 0) {
    var date = new Date();
    date.setTime(timeStamp * 1000);
    var y = date.getFullYear();
    var m: any = date.getMonth() + 1;
    m = m < 10 ? ('0' + m) : m;
    var d: any = date.getDate();
    d = d < 10 ? ('0' + d) : d;
    var h: any = date.getHours();
    h = h < 10 ? ('0' + h) : h;
    var minute: any = date.getMinutes();
    var second: any = date.getSeconds();
    minute = minute < 10 ? ('0' + minute) : minute;
    second = second < 10 ? ('0' + second) : second;
    if (type) {
        if (type == 'yearMonthDay') {
            return y + '年' + m + '月' + d + '日';
        }
        return y + '-' + m + '-' + d;
    } else {
        return y + '-' + m + '-' + d + ' ' + h + ':' + minute + ':' + second;
    }

} else {
    return "";
}
}

/**
 * 日期格式转时间戳
 * @param {Object} date
 */
export function timeTurnTimeStamp(dateStr: string) {
let timestamp;
let date;

// 尝试解析 'YYYY年M月D日'
try {
    let dateStr1 = dateStr.replace('年', '-').replace('月', '-').replace('日', '');
    date = new Date(dateStr1);
    timestamp = date.getTime();
} catch (e) {
    // 尝试解析 'YYYY-MM-DD'
    try {
        date = new Date(dateStr);
        timestamp = date.getTime();
    } catch (e) {
        // 尝试解析 'YYYY/MM/DD'
        try {
            date = new Date(dateStr.replace(/\//g, "-"));
            timestamp = date.getTime();
        } catch (e) {
            // 尝试解析 'YYYY年M月D日 HH时mm分'
            try {
                let dateStr1 = dateStr.replace('年', '-').replace('月', '-').replace('日', ' ').replace('时', ':').replace('分', '');
                date = new Date(dateStr1);
                timestamp = date.getTime();
            } catch (e) {
                // 尝试解析 'YYYY-MM-DD HH:mm'
                try {
                    date = new Date(dateStr);
                    timestamp = date.getTime();
                } catch (e) {
                    // 尝试解析 'YYYY/MM/DD HH:mm'
                    try {
                        date = new Date(dateStr.replace(/\//g, "-"));
                        timestamp = date.getTime();
                    } catch (e) {
                        // 如果所有格式都失败，返回null
                        console.error("无法解析日期字符串:", dateStr);
                        return null;
                    }
                }
            }
        }
    }
}
return (timestamp);
}