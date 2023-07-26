layui.use(['form','jquery','jquery_cookie'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);

    /**
     * ⽤户密码修改 表单提交
     */
    form.on('submit(saveBtn)',function (data){
        console.log(data.field);

        // 发送ajax请求，修改⽤户密码
        $.ajax({
            type:'post',
            url:ctx+'/user/updatePwd',
            data:{
                oldPwd:data.field.old_password,
                newPwd:data.field.new_password,
                confirmPassword:data.field.again_password
            },
            success:function (resutl){
                // 判断是否成功
                if(resutl.code == 200){
                    // 修改成功后，⽤户⾃动退出系统
                    layer.msg("⽤户密码修改成功，系统将在3秒钟后退出...",function (){
                        // 退出系统后，删除对应的cookie
                        $.removeCookie("userIdStr",{domain:"localhost",path:"/crm"});
                        $.removeCookie("userName",{domain:"localhost",path:"/crm"});
                        $.removeCookie("trueName",{domain:"localhost",path:"/crm"});

                        // 跳转到登录⻚⾯ (⽗窗⼝跳转)
                        window.parent.location.href = ctx + "/index";
                    });
                }else{
                    layer.msg(resutl.msg)
                }
            }
        })

    })

});