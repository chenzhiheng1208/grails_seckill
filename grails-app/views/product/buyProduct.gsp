<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>商品购买</title>
</head>
<body>
<h2>商品列表</h2>
<table border="1">
    <tr>
        <th>ID</th>
        <th>商品名</th>
        <th>描述</th>
        <th>价格</th>
        <th>库存</th>
        <th>操作</th>
    </tr>
    <g:each in="${products}" var="product">
        <g:if test="${product.status == 'listed'}">
            <tr>
                <td>${product.id}</td>
                <td>${product.name}</td>
                <td>${product.description}</td>
                <td>${product.price}</td>
                <td>${product.inventory}</td>
                <td>
                    <button onclick="placeOrder(${product.id})">秒杀</button>
                </td>
            </tr>
        </g:if>
    </g:each>
</table>


<script>
    // 页面加载时调用初始化全部库存接口
    window.onload = function() {
        fetch("/seckillOrder/initializeAllStocks", {
            method: "POST",
            headers: { "Content-Type": "application/json" }
        }).then(response => response.json())
            .then(data => console.log("库存初始化信息：", data.message))
            .catch(error => console.error("初始化库存失败：", error));
    };

    // function placeOrder(productId) {
    //     // 此处 userId 仅做示例，实际应从登录信息中获取
    //     fetch("/seckillOrder/placeOrder", {
    //         method: "POST",
    //         headers: { "Content-Type": "application/json" },
    //         body: JSON.stringify({ productId: productId, userId: 1 })
    //     }).then(response => response.json())
    //         .then(data => alert(data.message))
    //         .catch(error => alert("秒杀失败"));
    // }
    function placeOrder(productId) {
        fetch("/seckillOrder/placeOrder", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ productId: productId, userId: 1 }) // userId 应从登录信息获取
        })
            .then(response => response.json())
            .then(data => {
                alert(data.message);
                location.reload(); // 订单成功后刷新页面
            })
            .catch(error => {
                alert("秒杀失败");
                location.reload();
            });
    }
</script>
</body>
</html>
