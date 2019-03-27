app.controller('seckillController',function($scope,$location,seckillService,$interval){

	//秒杀商品列表
	$scope.findSeckillList = function(){
		seckillService.findSeckillList().success(
				function(response){
					$scope.seckillList = response;
				}
		)
	}
	
	$scope.findOne = function(){
		var id = $location.search()['id'];  //angularjs静态页面传参
		if(id!=null){
			seckillService.findOne(id).success(
					function(response){
						$scope.entity = response;
						
						 /*//计算出剩余时间
			            var endTime = new Date($scope.entity.endTime).getTime();
			            var nowTime = new Date().getTime();

			            //剩余时间
			            $scope.secondes =Math.floor( (endTime-nowTime)/1000 );

			            var time =$interval(function () {
			                if($scope.secondes>0){
			                    //时间递减
			                    $scope.secondes--;
			                    //时间格式化
			                    $scope.timeString=convertTimeString($scope.secondes);
			                }else{
			                    //结束时间递减
			                    $interval.cancel(time);
			                }
			            },1000);*/
					}
			)
		}
		
	}
	
	//距离结束时间   
	convertTimeString=function (allseconds) {
	    //计算天数(所有秒数除每天的秒数) 通过Math.floor向下取整
	    var days = Math.floor(allseconds/(60*60*24));

	    //小时      (所有秒数- 天数的所有秒数,剩下的秒数除以 一小时有多少秒) 通过Math.floor向下取整
	    var hours =Math.floor( (allseconds-(days*60*60*24))/(60*60) );

	    //分钟     (所有秒数 - 天数的秒数 - 小时的秒数 ，最后的秒数除以 一分钟有多少秒)
	    var minutes = Math.floor( (allseconds-(days*60*60*24)-(hours*60*60))/60 );

	    //秒
	    var seconds = allseconds-(days*60*60*24)-(hours*60*60)-(minutes*60);

	    //拼接时间
	    var timString="";
	    if(days>0){
	        timString=days+"天:";
	    }
	    if(hours<10){
	    	hours = "0" + hours;
	    }
	    if(minutes<10){
	    	minutes = "0" + minutes;
	    }
	    if(seconds<10){
	    	seconds = "0" + seconds;
	    }
	    
	    return timString+=hours+":"+minutes+":"+seconds;
	}
	
	//秒杀下单
	$scope.saveSeckillOrder = function(){
		//保存订单后->跳转到支付
		seckillService.saveSeckillOrder($scope.entity.id).success(
				function(response){
					if(response.success){
						alert("下单成功,进行支付操作")
					}else{
						alert(response.message);
					}
				}
		)
	}
})