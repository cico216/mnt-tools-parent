let urlPath = 'http://127.0.0.1:8080';

const httpUtils = {
	/* 公共失败 */
	commonErr(error, fail){
		if(fail) {
			let errResult = {};
			errResult.code = -500;
			errResult.message = "服务器异常";
			fail(errResult);
		}
	},
	/* 公共成功 */
	commonSucc(result, success, fail){
		let ajaxResult = result.data;
		//返回成功时
		if(ajaxResult.code != 0) {
			if(fail) {
				fail(ajaxResult);
			}
			return;
		} 
		let data = ajaxResult.data;
		success(data);
	},
	/*
		get 请求
	*/
	doGet({url, data, header = null, success, fail = null}) {
		uni.request({
			url: urlPath + url,
			method:"GET",
			data:data,
			success:function(result) {
						httpUtils.commonSucc(result, success, fail);
					},
			fail:	function(error) {
						httpUtils.commonErr(error, fail);
					}
		})
	},
	/*
		post 请求
	*/
	doPost({url, data, header = null, success, fail = null}) {
		uni.request({
			url: urlPath + url,
			method:"POST",
			data:data,
			success:function(result) {
						httpUtils.commonSucc(result, success, fail);
					},
			fail:	function(error) {
						httpUtils.commonErr(error, fail);
					}
		})
	},
	
}
export default httpUtils;
