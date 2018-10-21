app.controller("brandController",function ($scope,$controller,brandService) {
    $controller("baseController", {$scope:$scope});
    $scope.findAll=function () {
        brandService.findAll().success(function (response) {
            $scope.list=response;
        })
            .error(function () {
                alert("服务器忙")
            })
    };

    $scope.findPage=function (page,rows) {
        brandService.findPage(page,rows).success(function (response) {
            $scope.list = response.rows;
            $scope.paginationConf.totalItems = response.total;
        });
    };

    $scope.save=function () {
        var obj ;
        if($scope.entity.id != null){
            obj = brandService.update($scope.entity);
        }else {
            obj = brandService.add($scope.entity);
        }
        obj.success(function (response) {
            if(response.success){
                $scope.reloadList();
            }else{
                alert(response.message);
            }
        })
    };

    $scope.findOne=function (id) {
        brandService.findOne(id).success(function (response) {
            $scope.entity=response;
        })
    };

    $scope.delete=function () {
        if($scope.selectedIds.length < 1){
            alert("请先选择要删除的品牌");
            return;
        }
        if(confirm("你确定要删除么?")){
            brandService.delete($scope.selectedIds).success(function (response) {
                if(response.success){
                    $scope.reloadList();
                    $scope.selectedIds=[];
                }else{
                    alert(response.message);
                }
            })
        }
    };

    $scope.searchEntity={};
    $scope.search=function (page,rows) {
        brandService.search(page,rows,$scope.searchEntity).success(function (response) {
            $scope.list=response.rows;
            $scope.paginationConf.totalItems = response.total;
        })
    }
});