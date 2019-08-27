app.controller('searchController',function($scope,searchService){

    //定义搜索对象的结构，category：商品分类
    $scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':''};

    //搜索
    $scope.search=function(){
        searchService.search( $scope.searchMap ).success(
            function(response){
                $scope.resultMap=response;//搜索返回的结果
            }
        );
    }

    //添加搜索项  改变searchMap值
    $scope.addSearchItem=function (key, value) {
        if (key=="category" ||  key=="brand"|| key=='price'){//如果点击的是分类或者是品牌/价格区间
            $scope.searchMap[key]=value;
        }else {//否则是规格
            $scope.searchMap.spec[key]=value;//移除此属性
        }
        $scope.search();//执行搜索
    }

    //移除复合搜索条件
    $scope.removeSearchItem=function(key){
        if(key=="category" ||  key=="brand"|| key=='price'){//如果是分类或品牌或价格区间
            $scope.searchMap[key]="";
        }else{//否则是规格
            delete $scope.searchMap.spec[key];//移除此属性
        }
        $scope.search();//执行搜索
    }


});