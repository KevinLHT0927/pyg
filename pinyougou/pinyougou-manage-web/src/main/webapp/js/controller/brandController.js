app.controller("brandController",function ($scope, $controller,brandService) {

    //继承baseController
    $controller("baseController", {$scope:$scope});

    $scope.findPage= function (page,rows) {
        brandService.findPage().success(function (response) {
            $scope.list = response.rows;
            $scope.paginationConf.totalItems=response.total;
        })
    };

    $scope.findAll = function() {
        brandService.findAll().success(function (response) {
            $scope.list = response;
        });
    };


    $scope.search = function (page,rows) {
        brandService.search($scope.searchEntity,page,rows).success(function (response) {
            $scope.list = response.rows;
            $scope.paginationConf.totalItems=response.total;
        })
    }
    /*
        保存的方法。
     */
    $scope.save = function () {
        var obj;
        if ($scope.entity.id != null) {
            obj = brandService.update($scope.entity);
        } else {
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

    $scope.findOne = function (id) {
        brandService.findOne(id).success(function (response) {
            $scope.entity = response;
        });
    };

    $scope.searchEntity = {};


    $scope.delete = function () {
        if($scope.selectedIds.length<1){
            alert("请至少选择一个项目");
            return;
        }

        if (confirm("您确定要删除这些项目吗")) {
            brandService.delete($scope.selectedIds).success(function (response) {
                if(response.success){
                    $scope.reloadList();
                    $scope.selectedIds = [];
                }else{
                    alert(response.message);
                }
            });
        }
    };


});