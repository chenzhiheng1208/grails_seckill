<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>库存管理</title>
</head>
<body>
<h2>库存管理</h2>
<button onclick="showAddForm()">添加商品</button>

<div id="addProductForm" style="display:none;">
    <h3>添加商品</h3>
    <form id="addProduct">
        <label>名称：</label><input type="text" name="name" required/><br/>
        <label>描述：</label><input type="text" name="description" required/><br/>
        <label>价格：</label><input type="number" name="price" step="0.01" required/><br/>
        <label>库存：</label><input type="number" name="inventory" required/><br/>
        <button type="submit">提交</button>
    </form>
</div>

<!-- 编辑商品表单 -->
<div id="editProductForm" style="display:none;">
    <h3>编辑商品</h3>
    <form id="editProduct">
        <!-- 隐藏字段存储商品ID -->
        <input type="hidden" name="id" id="editProductId"/>
        <label>名称：</label><input type="text" name="name" id="editProductName" required/><br/>
        <label>描述：</label><input type="text" name="description" id="editProductDescription" required/><br/>
        <label>价格：</label><input type="number" name="price" id="editProductPrice" step="0.01" required/><br/>
        <label>库存：</label><input type="number" name="inventory" id="editProductInventory" required/><br/>
        <button type="submit">提交修改</button>
        <button type="button" onclick="hideEditForm()">取消</button>
    </form>
</div>

<table border="1">
    <tr>
        <th>ID</th>
        <th>商品名</th>
        <th>描述</th>
        <th>价格</th>
        <th>库存</th>
        <th>是否上架</th>
        <th>操作</th>
    </tr>
    <g:each in="${products}" var="product">
        <tr>
            <td>${product.id}</td>
            <td>${product.name}</td>
            <td>${product.description}</td>
            <td>${product.price}</td>
            <td>${product.inventory}</td>
            <td>${product.status}</td>
            <td>
                <button onclick="listProduct(${product.id})">上架</button>
                <button onclick="deleteProduct(${product.id})">删除</button>
                <button onclick='showEditForm(${product.id}, "${product.name}", "${product.description}", ${product.price}, ${product.inventory})'>编辑</button>
            </td>
        </tr>
    </g:each>
</table>

<script>
    function showAddForm() {
        document.getElementById("addProductForm").style.display = "block";
    }

    function listProduct(productId) {
        fetch("/product/listProduct", {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ id: productId })
        })
            .then(response => response.text())
            .then(message => {
                alert(message);
                location.reload(); // 刷新页面
            })
            .catch(error => {
                alert("上架失败");
            });
    }

    function deleteProduct(productId) {
        fetch("/product/deleteProduct", {
            method: "DELETE",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ id: productId })
        })
            .then(response => response.text())
            .then(message => {
                alert(message);
                location.reload(); // 刷新页面
            })
            .catch(error => {
                alert("删除失败");
            });
    }

    // 显示编辑表单，并自动填充当前商品数据
    function showEditForm(id, name, description, price, inventory) {
        document.getElementById("editProductId").value = id;
        document.getElementById("editProductName").value = name;
        document.getElementById("editProductDescription").value = description;
        document.getElementById("editProductPrice").value = price;
        document.getElementById("editProductInventory").value = inventory;
        document.getElementById("editProductForm").style.display = "block";
    }

    // 隐藏编辑表单
    function hideEditForm() {
        document.getElementById("editProductForm").style.display = "none";
    }

    // 编辑商品表单提交事件
    document.getElementById("editProduct").onsubmit = function(event) {
        event.preventDefault();
        let formData = new FormData(event.target);
        let jsonData = {};
        formData.forEach((value, key) => jsonData[key] = value);

        fetch("/product/editProduct", {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(jsonData)
        })
            .then(response => response.text())
            .then(message => {
                alert(message);
                location.reload(); // 修改成功后刷新页面
            })
            .catch(error => {
                alert("编辑失败");
            });
    };

    // 添加商品表单提交事件
    document.getElementById("addProduct").onsubmit = function(event) {
        event.preventDefault();
        let formData = new FormData(event.target);
        let jsonData = {};
        formData.forEach((value, key) => jsonData[key] = value);

        fetch("/product/add", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(jsonData)
        })
            .then(response => response.text())
            .then(message => {
                alert(message);
                location.reload(); // 修改成功后刷新页面
            })
            .catch(error => {
                alert("添加失败");
            });
    };
</script>
</body>
</html>
