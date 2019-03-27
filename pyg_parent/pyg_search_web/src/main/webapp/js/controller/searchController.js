app.controller('searchController',function ($scope, searchService) {

    //搜索条件
    $scope.searchMap = {keywords:'三星',spec:{},sort:'ASC'}; //注意一定要对spec规格进行初始化 ,sort是升降序

    //关键字搜索
    $scope.search = function () {
        $scope.searchMap.pageNo = $scope.paginationConf.currentPage; //设置pageNo
        $scope.searchMap.pageSize = $scope.paginationConf.itemsPerPage; //设置pageSize
        searchService.search($scope.searchMap).success(
            function (response) {
                 $scope.list = response.content;
                 //alert(JSON.stringify($scope.list));
                $scope.paginationConf.totalItems = response.total; //将总记录数设置到分页控件中
            }
        )
    }

    //增加过滤条件
    $scope.addFilterCondition = function (key, value) {
        if(key == 'category' || key == 'brand' || key == 'price'){ //如果是品牌或者分类或者价格
            $scope.searchMap[key] = value;
        }else{
            $scope.searchMap.spec[key] = value;
        }

        //进行查询
        $scope.search();
    }

    //升降序
    $scope.sortCondition = function (value) {
        $scope.searchMap.sort =value;
        $scope.search();
    }

    //移除查询条件
    $scope.removeSearchItem = function (key) {
        if(key == 'category' || key == 'brand' || key == 'price'){//判断如果是分类，品牌，价格
            delete $scope.searchMap[key]; //delete是前端删除map中的key和value的方法
        }else{
            delete $scope.searchMap.spec[key];
        }

        $scope.search(); //去掉条件后进行查询
    }


    //分页控件配置
    $scope.paginationConf = {
        currentPage: 1,  //当前页
        totalItems: 10,  //总记录数
        itemsPerPage: 10,  //每页记录数
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function(){
            $scope.search();//刷新
        }
    };
})

//自定义过滤器 第一个参数是trustHtml名字$sce是内部指令
app.filter('trustHtml',['$sce',function($sce){
    return function(data){
        return $sce.trustAsHtml(data);
    }
}]);