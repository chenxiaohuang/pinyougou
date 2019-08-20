//文件上传服务层
app.service("uploadService",function($http){
    this.uploadFile=function(){//上传文件
        var formData=new FormData();//html5后提供的类；以二进制上传文件数据用的
        formData.append("file",file.files[0]);//参数1：file 第一个文件上传框name
        return $http({
            method:'POST',
            url:"../upload.do",//控制层访问方法的路径
            data: formData,//对文件的二进制的封装
            headers: {'Content-Type':undefined},//原本默认的是传json数据，上传文件需要指定undefined
            transformRequest: angular.identity//对整个表单二进制序列化的作用
        });//二进制上传
    }
});