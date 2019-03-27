app.controller('contentController',function ($scope,contentService) {

    //根据广告分类的id获取广告列表数据
    $scope.findByParentId = function (id) {
            contentService.findByParentId(id).success(
                function (response) {
                    $scope.list = response;
                    //alert(JSON.stringify($scope.list));
                }
            )
    }
})