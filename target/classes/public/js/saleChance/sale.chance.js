layui.use(['table','layer'],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;

    /**
     * 营销机会列表展示
     */
    var tableIns = table.render({
        elem: '#saleChanceList', // 表格绑定的ID
        url : ctx + '/sale_chance/list', // 访问数据的地址
        cellMinWidth : 95,
        page : true, // 开启分⻚
        height : "full-125",
        limits : [10,15,20,25],
        limit : 10,
        toolbar: "#toolbarDemo",
        id : "saleChanceTable",
        cols : [[
            {type: "checkbox", fixed:"center"},
            {field: "id", title:'编号',fixed:"true"},
            {field: 'chanceSource', title: '机会来源',align:"center"},
            {field: 'customerName', title: '客户名称', align:'center'},
            {field: 'cgjl', title: '成功⼏率', align:'center'},
            {field: 'overview', title: '概要', align:'center'},
            {field: 'linkMan', title: '联系⼈', align:'center'},
            {field: 'linkPhone', title: '联系电话', align:'center'},
            {field: 'description', title: '描述', align:'center'},
            {field: 'createMan', title: '创建⼈', align:'center'},
            {field: 'createDate', title: '创建时间', align:'center'},
            {field: 'updateDate', title: '修改时间', align:'center'},
            {field: 'uname', title: '分派⼈', align:'center'},
            {field: 'assignTime', title: '分配时间', align:'center'},
            {field: 'state', title: '分配状态', align:'center',templet:function(d){
                    return formatterState(d.state);
                }},
            {field: 'devResult', title: '开发状态', align:'center',templet:function (d) {
                    return formatterDevResult(d.devResult);
                }},
            {title: '操作', templet:'#saleChanceListBar',fixed:"right",align:"center",
                minWidth:150}
        ]]
    });

    /**
     * 格式化分配状态
     * 0 - 未分配
     * 1 - 已分配
     * 其他 - 未知
     * @param state
     * @returns {string}
     */
    function formatterState(state){
        if(state==0) {
            return "<div style='color: yellow'>未分配</div>";
        } else if(state==1) {
            return "<div style='color: green'>已分配</div>";
        } else {
            return "<div style='color: red'>未知</div>";
        }
    }

    /**
     * 格式化开发状态
     * 0 - 未开发
     * 1 - 开发中
     * 2 - 开发成功
     * 3 - 开发失败
     * @param value
     * @returns {string}
     */
    function formatterDevResult(value){
        if(value == 0) {
            return "<div style='color: yellow'>未开发</div>";
        } else if(value==1) {
            return "<div style='color: #00FF00;'>开发中</div>";
        } else if(value==2) {
            return "<div style='color: #00B83F'>开发成功</div>";
        } else if(value==3) {
            return "<div style='color: red'>开发失败</div>";
        } else {
            return "<div style='color: #af0000'>未知</div>"
        }
    }

    /**
     * 绑定搜索按钮的点击事件
     */
    $(".search_btn").click(function () {
        tableIns.reload({
            where: { //设定异步数据接⼝的额外参数，任意设
                customerName: $("[name='customerName']").val(), // 客户名
                createMan: $("[name='createMan']").val(), // 创建⼈
                state: $("#state").val() // 状态
            }
            ,page: {
                curr: 1 // 重新从第 1 ⻚开始
            }
        }); // 只重载数据
    });
    /**
     * 监听头部工具栏事件
     *  格式：
     *      table.on('toolbar(数据表格的lay-filter属性值)', function (data) {
     *
            })
     */
    table.on('toolbar(saleChances)', function (data) {
        // data.event：对应的元素上设置的lay-event属性值
        // console.log(data);
        // 判断对应的事件类型
        if (data.event == "add") {
            // 添加操作
            openSaleChanceDialog();

        } else if (data.event == "del") {
            // 删除操作
            deleteSaleChance(data);
        }
    })

    /**
     * 打开添加/修改营销机会数据的窗口
     *      如果营销机会ID为空，则为添加操作
     *      如果营销机会ID不为空，则为修改操作
     */
    function openSaleChanceDialog(saleChanceId) {
        // 弹出层的标题
        var title = "<h3>营销机会管理 - 添加营销机会</h3>";
        var url = ctx + "/sale_chance/toSaleChancePage";

        // 判断营销机会ID是否为空
        if (saleChanceId != null && saleChanceId != '') {
            // 更新操作
            title  = "<h3>营销机会管理 - 更新营销机会</h3>";
            // 请求地址传递营销机会的ID
            url += '?saleChanceId=' + saleChanceId;
        }

        // iframe层
        layui.layer.open({
            // 类型
            type: 2,
            // 标题
            title: title,
            // 宽高
            area: ['500px', '620px'],
            // url地址
            content: url,
            // 可以最大化与最小化
            maxmin:true
        });
    }

    /**
     * 行工具栏监听事件
     table.on('tool(数据表格的lay-filter属性值)', function (data) {

         });
     */
    table.on('tool(saleChances)', function (data) {
        console.log(data);
        // 判断类型
        if (data.event == "edit") { // 编辑操作

            // 得到营销机会的ID
            var saleChanceId = data.data.id;
            // 打开修改营销机会数据的窗口
            openSaleChanceDialog(saleChanceId)

        } else if (data.event == "del") { // 删除操作
            // 弹出确认框，询问用户是否确认删除
            layer.confirm('确定要删除该记录吗？',{icon:3, title:"营销机会管理"}, function (index) {
                // 关闭确认框
                layer.close(index);

                // 发送ajax请求，删除记录
                $.ajax({
                    type:"post",
                    url:ctx + "/sale_chance/delete",
                    data:{
                        ids:data.data.id
                    },
                    success:function (result) {
                        // 判断删除结果
                        if (result.code == 200) {
                            // 提示成功
                            layer.msg("删除成功！",{icon:6});
                            // 刷新表格
                            tableIns.reload();
                        } else {
                            // 提示失败
                            layer.msg(result.msg, {icon:5});
                        }
                    }
                });
            });
        }
    });

    /**
     * 删除营销机会（删除多条记录）
     * @param data
     */
    function deleteSaleChance(data) {
        // 获取数据表格选中的行数据   table.checkStatus('数据表格的ID属性值');
        var checkStatus = table.checkStatus("saleChanceTable");
        console.log(checkStatus);

        // 获取所有被选中的记录对应的数据
        var saleChanceData = checkStatus.data;

        // 判断用户是否选择的记录 (选中行的数量大于0)
        if (saleChanceData.length < 1) {
            layer.msg("请选择要删除的记录！",{icon:5});
            return;
        }

        // 询问用户是否确认删除
        layer.confirm('您确定要删除选中的记录吗？',{icon:3, title:'营销机会管理'}, function (index) {
            // 关闭确认框
            layer.close(index);
            // 传递的参数是数组   ids=1&ids=2&ids=3
            var ids = "ids=";
            // 循环选中的行记录的数据
            for(var i = 0; i < saleChanceData.length; i++) {
                if(i < saleChanceData.length -1) {
                    ids = ids + saleChanceData[i].id + "&ids="
                } else {
                    ids = ids + saleChanceData[i].id;
                }
            }
            // console.log(ids);

            // 发送ajax请求，执行删除营销机会
            $.ajax({
                type:"post",
                url:ctx + "/sale_chance/delete",
                data:ids, // 传递的参数是数组 ids=1&ids=2&ids=3
                success:function (result) {
                    // 判断删除结果
                    if (result.code == 200) {
                        // 提示成功
                        layer.msg("删除成功！",{icon:6});
                        // 刷新表格
                        tableIns.reload();
                    } else {
                        // 提示失败
                        layer.msg(result.msg, {icon:5});
                    }
                }
            });
        });

    }


});
