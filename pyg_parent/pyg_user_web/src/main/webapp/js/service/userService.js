//服务层
app.service('userService',function($http){

	//增加 
	this.add=function(entity,code){
		return  $http.post('user/add.do?code='+code,entity );
	}

	//发送短信验证码
	this.sendSms = function (phone) {
		return $http.get('user/sendSms.do?phone='+phone);
    }

    this.showName = function () {
		return $http.get('user/showName.do');
    }

});
