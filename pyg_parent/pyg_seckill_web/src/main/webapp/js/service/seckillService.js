app.service('seckillService',function($http){
	this.findSeckillList = function(){
		return $http.get('seckill/findSeckillGoods.do');
	}
	
	this.findOne = function(id){
		return $http.get('seckill/findOne.do?id='+id);
	}
	
	this.saveSeckillOrder = function(id){
		return $http.get('seckill/saveSeckillOrder.do?goodsId='+id);
	}
})