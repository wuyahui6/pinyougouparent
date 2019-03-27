 //控制层 
app.controller('userController' ,function($scope,$controller   ,userService){	
	
	$controller('baseController',{$scope:$scope});//继承
	

	//保存 
	$scope.save=function(){

		if($scope.entity.password != $scope.password){
			alert('两次密码输入不一致');
			return
		}

		var serviceObject=userService.add( $scope.entity,$scope.smsCode  );//增加

		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询
					location.href = "login.html";
				}else{
                    alert(response.message);
				}

			}		
		);				
	}

	//发送短信验证码
	$scope.sendSms = function () {
		userService.sendSms($scope.entity.phone).success(
			function (response) {
				alert(response.message);
            }
		)
    }

    //显示登录用户名
    $scope.showName = function () {
		userService.showName().success(
			function (response) {
				$scope.loginName = response;
            }
		)
    }
});	
