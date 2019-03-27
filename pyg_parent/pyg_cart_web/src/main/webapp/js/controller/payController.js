app.controller('payController',function ($scope, payService) {

    /**
     * 根据微信返回的支付地址，生成二维码
     */
    $scope.createNative = function () {
        payService.createNative().success(
            function (response) {
                $scope.out_trade_no = response.out_trade_no; //支付单号
                $scope.total_fee = response.total_fee; //总金额

                //生成二维码
                var qr = window.qr = new QRious({
                    element: document.getElementById('qrious'),
                    size: 250,
                    value: response.code_url, //改成支付地址
                    level: 'H'
                })

                queryPayStatus(); //查询订单状态
            }
        )
    }

    /**
     * 查询支付状态
     */
    queryPayStatus = function () {
        payService.queryPayStatus($scope.out_trade_no).success(
            function (response) {
                if(response.success){
                    location.href = "paysuccess.html"; //跳到支付成功
                }else{
                    if(response.message == "timeout"){
                        $scope.createNative();  //如果超时，重新生成二维码;
                    }else{
                        location.href = "payfail.html"//跳到支付失败页
                    }
                }
            }
        )
    }
})