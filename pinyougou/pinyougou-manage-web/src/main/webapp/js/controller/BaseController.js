app.controller("baseController",function ($scope) {
    $scope.paginationConf = {
        currentPage:1,
        totalItems:10,
        itemsPerPage:10,
        perPageOptions:[10,20,30,40,50],
        onChange:function () {
            $scope.reloadList();
        }
    };

    $scope.reloadList = function(){
        //$scope.findPage($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
        $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
    };

    $scope.selectedIds = [];

    $scope.updateSelection = function ($event,id) {
        if($event.target.checked){
            $scope.selectedIds.push(id);
        }else{
            var index = $scope.selectedIds.indexOf(id);
            selectedIds.splice(id,1);
        }
    };

    $scope.jsonToString = function (jsonStr, key) {
        var str = "";
        var jsonArray = JSON.parse(jsonStr);
        for(var i = 0; i < jsonArray.length; i++) {
            if(i > 0) {
                str += ",";
            }
            str += jsonArray[i][key];
        }
        return str;
    };
});