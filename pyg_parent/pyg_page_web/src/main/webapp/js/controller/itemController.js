app.controller("itemController",function($scope,$http){

	$scope.num = 1; //默认购买数量是1
	//数量加减
	$scope.addNum=function(value){
		$scope.num+=value;
		if($scope.num<1){
			$scope.num=1;
		}		
	}
	
    $scope.spec = {}; //map类型

    //选择规格选项的时候调用
	$scope.selectSpec = function(key,value){
		$scope.spec[key] = value; //给map赋值
		searchSku(); //在点击规格选项的时候，匹配查询
	}

	//判断是否有seleced样式的方法
	$scope.isSelectSpec = function(key,value){
		if($scope.spec[key] == value){
			return true;
		}
		return false;
	}

	$scope.sku = {}; //打算将itemList的对象保存其中

	//将默认item的对象赋值给$scope.sku
	$scope.loadSku = function(){
		$scope.sku = skuList[0];
		$scope.spec = JSON.parse(JSON.stringify($scope.sku.spec));  //深克隆
	}


	//循环所有的skuList中的对象，查找对象中的spec和页面勾选的spec对象是否一致
	searchSku=function(){
		for(var i=0;i< skuList.length;i++  ){
			if( matchObject (skuList[i].spec, $scope.spec ) ){
				//如果一致直接将当前对象设置到$scope.sku
				$scope.sku=skuList[i];
				return;
			}		
		}
	}
 
	//匹配两个对象是否相等
	matchObject=function(map1,map2){		
		for(var k in map1){
			if(map1[k]!=map2[k]	){
				return false;
			}				
		}
		for(var k in map2){
			if(map1[k]!=map2[k]	){
				return false;
			}				
		}
		return true;		
	}

	//商品添加购物车
	$scope.addItemToCartList = function () {
		$http.get('http://localhost:9107/cart/addItemToCartList.do?itemId='+$scope.sku.id+'&num='+$scope.num,{'withCredentials':true} ).success(
				function (response) {
					if(response.success){
						location.href = "http://localhost:9107";
					}else{
						alert(response.message);
					}
                }
		)
    }


});