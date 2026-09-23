
// #ifdef H5
let baseUrl = import.meta.env.VITE_APP_BASE_URL || `${ location.origin }/api/`
// #endif
// #ifndef H5
let baseUrl = import.meta.env.VITE_APP_BASE_URL
// #endif

export const myRequest = (option={})=>{
	return new Promise((reslove,reject)=>{
		uni.request({
			url: baseUrl + option.url, 
			data :option.data,
			method:option.method || "GET",
			success: (result) => {
				reslove(result)
			},
			fail:(error)=>{
				reject(error)
			}
		})
	})
}
