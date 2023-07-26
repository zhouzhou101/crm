layui.use(['form','jquery','jquery_cookie'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);
    /**
     * ⽤户登录 表单提交
     */
    form.on('submit(login)', function(data){

        console.log(data.field) //当前容器的全部表单字段，名值对形式：{name: value}
        /* 表单元素的非空验证() */

        // 发送 ajax 请求，请求⽤户登录
        $.ajax({
            type:"post",
            url:ctx + "/user/login",
            data:{
                userName:data.field.username,
                userPwd:data.field.password
            },
            success:function (result){//result是回调函数,用来接收后端传回来的值
                console.log(result);
                // 判断是否登录成功
                if(result.code == 200){
                    /**
                     * 设置用户是登录状态
                     *  1. 利用session会话
                     *      保存用户信息，如果会话存在，则用户是登录状态；否则是非登录状态
                     *      缺点：服务器关闭，会话则会失效
                     *  2. 利用cookie
                     *      保存用户信息，cookie未失效，则用户是登录状态
                     */
                    layer.msg("登陆成功",function (){
                        if ($("#rememberMe").prop("checked")){
                            // 将⽤户信息存到cookie中
                            $.cookie("userIdStr",result.result.userIdStr,{expires: 7});
                            $.cookie("userName",result.result.userName,{expires: 7});
                            $.cookie("trueName",result.result.trueName,{expires: 7});
                        }else{
                            // 将⽤户信息存到cookie中
                            $.cookie("userIdStr",result.result.userIdStr);
                            $.cookie("userName",result.result.userName);
                            $.cookie("trueName",result.result.trueName);
                            // 登录成功后，跳转到⾸⻚
                        }

                        window.location.href = ctx + "/main";
                    })
                }else{
                    // 提示信息
                    layer.msg(result.msg,{icon:10})
                }
            }
        })

        return false; //阻止表单跳转。如果需要表单跳转，去掉这段即可。
    });
    
});