import { getTodo, getstat } from '@/app/api/index';
export function useCommon() {
    // 统计
    const getStatItemList = () => {
        let stat_cache =  uni.getStorageSync('statItemList')
        if(stat_cache){
            return Promise.resolve(stat_cache);
        } else {
            return getstat().then((res: any) => {
                let cacheList: any = {};
                res.data.forEach((item: any, index: number) => {
                    cacheList[item.key] = item;  
                })
                uni.setStorageSync('statItemList', cacheList) 
                return cacheList;
            })
        }      
    }
    // 代办
    const getTodoItemList = () => {
        let todo_cache =  uni.getStorageSync('todoItemList')
        if(todo_cache){
            return Promise.resolve(todo_cache);
        } else {
            return getTodo().then((res: any) => {
                let cacheList: any = {};
                res.data.forEach((item: any, index: number) => {
                    cacheList[item.key] = item;  
                })
                uni.setStorageSync('todoItemList', cacheList) 
                return cacheList;
            })
        }
    }
     return {
        getStatItemList,
        getTodoItemList
    }

}