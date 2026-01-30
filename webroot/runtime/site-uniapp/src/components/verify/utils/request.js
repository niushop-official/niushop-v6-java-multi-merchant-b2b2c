
let baseUrl = import.meta.env.VITE_APP_BASE_URL || `${ location.origin }/api/`
baseUrl && baseUrl.substr(-1) != '/' && (baseUrl += '/')

export const myRequest = (option={})=>{
	return new Promise((reslove,reject)=>{
		let url = option.url || '';
		if (url.startsWith('/')) {
			url = url.substring(1);
		}
		
		uni.request({
			url: baseUrl + url, 
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
