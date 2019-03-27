//服务层
app.service('orderService',function($http){

	//增加 
	this.add=function(entity){
		return  $http.post('../order/add.do',entity );
	}
});
