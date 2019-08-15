app.controller('brandController',function ($scope,$controller,brandService) {

    $controller('baseController',{$scope:$scope});

    //查询品牌列表
    $scope.findAll=function () {
        //回调函数
        brandSeevice.findAll().success(
            function (response) {
                $scope.list=response;//给表变量赋值
            }
        );
    };

    // //分页控件配置currentPage:当前页  totalItems ：总记录数 itemsPerPage perPageOptions ：分页选项 onChange:当页码变更后自动触发的方法
    // $scope.paginationConf = {
    //     currentPage: 1,
    //     totalItems: 10,
    //     itemsPerPage: 10,
    //     perPageOptions: [10, 20, 30, 40, 50],
    //     onChange: function(){
    //         $scope.reloadList();//重新加载
    //     }
    // };
    //
    // //刷新列表
    // $scope.reloadList=function(){
    //     $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
    // };

    //分页
    $scope.findPage=function (page,size) {
        brandService.findPage(page,size).success(
            function (response) {
                $scope.list=response.rows;//显示当前页数据
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        );
    };

    //根据id查询实体类
    $scope.findOne=function (id) {
        brandService.findOne(id).success(
            function (response) {
                $scope.entity=response;
            }
        )
    };

    //新增
    $scope.save=function () {
        var  object=null;
        if($scope.entity.id!=null) {
            object = brandService.update($scope.entity);
        }else {
            object = brandService.add($scope.entity);
        }
        alert("测试用的增加中"+object);
        object.success(
            function (response) {
                if (response.success) {//如果成功
                    $scope.reloadList();//刷新列表
                }else {
                    alert(response.success);//提示错误
                }
            }
        );
    };


    // $scope.selectIds=[];//用户勾选的ID集合
    // //用户勾选复选框
    // $scope.updateSelection=function ($event,id) {
    //     if($event.target.checked){
    //         $scope.selectIds.push(id);//push向集合添加元素
    //     }else {
    //         var index=$scope.selectIds.indexOf(id);//查找值在selectIds数组中的位置
    //         //参数1：移除的位置 参数2：移除的个数，点击一次勾选只能选中一个所有参数2是“1”
    //         $scope.selectIds.splice(index,1);
    //     }
    // };

    //批量删除
    $scope.dele=function () {

        brandService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {//如果成功
                    $scope.reloadList();//刷新列表
                }else {
                    alert(response.message);//提示错误
                }
            }
        );
    };

    $scope.searchEntity={};//定义搜索对象

    //条件查询
    $scope.search=function (page,size) {
        brandService.search(page,size,$scope.searchEntity).success(
            function (response) {
                $scope.paginationConf.totalItems=response.total;//更新总记录数
                $scope.list=response.rows;//显示当前业数据

            }
        );
    }

});