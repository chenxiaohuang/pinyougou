app.controller('baseController',function ($scope) {
    //分页控件配置currentPage:当前页  totalItems ：总记录数 itemsPerPage perPageOptions ：分页选项 onChange:当页码变更后自动触发的方法
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function(){
            $scope.reloadList();//重新加载
        }
    };

    //刷新列表
    $scope.reloadList=function(){
        $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
    };

    $scope.selectIds=[];//用户勾选的ID集合
    //用户勾选复选框
    $scope.updateSelection=function ($event,id) {
        if($event.target.checked){
            $scope.selectIds.push(id);//push向集合添加元素
        }else {
            var index=$scope.selectIds.indexOf(id);//查找值在selectIds数组中的位置
            //参数1：移除的位置 参数2：移除的个数，点击一次勾选只能选中一个所有参数2是“1”
            $scope.selectIds.splice(index,1);
        }
    };


    //提取json字符串数据中某个属性，返回拼接字符串 逗号分隔
        $scope.jsonToString=function(jsonString,key){
            var json=JSON.parse(jsonString);//将json字符串转换为json对象
            var value="";
            for(var i=0;i<json.length;i++){
                if(i>0){
                    value+=","
                }
                value+=json[i][key];
            }
            return value;
        }

});