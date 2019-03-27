app.service('cartService',function ($http) {

    this.findCartListFromRedis = function () {
        return $http.get('cart/findCartListFromRedis.do')
    }

//http://localhost:9107/cart/addItemToCartList.do?itemId=1369368&num=10
    this.addItemToCartList = function (itemId,num) {
        return $http.get('cart/addItemToCartList.do?itemId='+itemId+"&num="+num);
    }
})