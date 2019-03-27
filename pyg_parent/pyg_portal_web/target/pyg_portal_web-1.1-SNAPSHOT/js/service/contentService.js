app.service('contentService',function ($http) {

    this.findByParentId = function (id) {
        return $http.get('content/findByCategoryId.do?id='+id);
    }
})