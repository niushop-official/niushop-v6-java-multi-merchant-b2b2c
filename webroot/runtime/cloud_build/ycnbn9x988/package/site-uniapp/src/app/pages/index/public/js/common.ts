import { getTodo, getstat } from '@/app/api/index';
export function useCommon() {
    // 统计
    const getStatItemList = () => {
        return getstat().then((res: any) => {
            let cacheList: any = {};
            res.data.forEach((item: any, index: number) => {
                cacheList[item.key] = item;  
            })
            return cacheList;
        })   
    }
    // 代办
    const getTodoItemList = () => {
        return getTodo().then((res: any) => {
            let cacheList: any = {};
            res.data.forEach((item: any, index: number) => {
                cacheList[item.key] = item;  
            })
            return cacheList;
        })
    }
     return {
        getStatItemList,
        getTodoItemList
    }

}