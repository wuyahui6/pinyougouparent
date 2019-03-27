app.controller('cartController',function ($scope, cartService,addressService,orderService) {

    /**
     * 获取购物车列表
     */
    $scope.findCartListFromRedis = function () {
        cartService.findCartListFromRedis().success(
            function (response) {
                $scope.cartList = response;
                sum();  //每次查购物车的时候，求和
            }
        )
    }

    /**
     * 商品添加购物车列表
     * @param itemId
     * @param num
     */
    $scope.addItemToCartList = function (itemId,num) {
        cartService.addItemToCartList(itemId,num).success(
            function (response) {
                if(response.success){
                    //刷新购物车页面
                    $scope.findCartListFromRedis();
                }else{
                    alert(response.message);
                }
            }
        )
    }

    /**
     * 求合计数和总金额
     */
    sum = function () {
        $scope.totalMoney = 0;  //总金额
        $scope.totalNum = 0;    //总数量

        //循环购物车
        for (var i = 0; i < $scope.cartList.length; i++) {
             var cart = $scope.cartList[i] //从购物车列表获取购物车
            for (var j = 0; j < cart.orderItemList.length; j++) {
                //循环获取购物车中的商品对象
                $scope.totalMoney += cart.orderItemList[j].totalFee;  //将每个商品的总金额累加
                $scope.totalNum += cart.orderItemList[j].num;         //累加总数量
            }
        }
    }

    /**
     * 查询当前登录人收货地址列表
     */
    $scope.findAddressList = function () {
        addressService.findListByUserId().success(
            function (response) {
                $scope.addressList = response; //将返回的收货人地址列表保存到$scope.addressList

                //循环所有的地址列表查找默认的地址
                for (var i = 0; i < response.length; i++) {
                    if(response[i].isDefault == '1'){ //找到默认地址
                        $scope.address = response[i];
                    }
                }
            }
        )
    }

    //保存用户选择地址对象
    $scope.selectAddress = function (address) {
        $scope.address = address;
    }

    //判断是否是用户选择的地址
    $scope.isSelectAddress = function (address) {
        if($scope.address == address){
            return true;
        }
        return false;
    }

    $scope.order = {paymentType:'1'};  //初始化一个订单对象，为了传递参数，传递支付方式 1是微信，2是货到付款
    //选择支付方式
    $scope.selectPaymentType = function (value) {
        alert(value);
        $scope.order.paymentType = value;
    }

    //创建订单
    $scope.createOrder = function () {
        $scope.order.receiverMobile = $scope.address.mobile; //收货人电话
        $scope.order.receiverAreaName = $scope.address.address; //收货地址
        $scope.order.receiver = $scope.address.contact;      //收货人

        orderService.add($scope.order).success(
            function (response) {
                if(response.success){
                    location.href = "pay.html";  //如果订单生成成功跳转支付页面
                }else{
                    alert(response.message);
                }
            }
        )
    }
})