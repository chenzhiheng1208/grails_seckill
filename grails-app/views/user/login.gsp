<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>用户登录</title>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>
<body>
<h2>用户登录</h2>
<g:if test="${flash.message}">
    <div class="message">${flash.message}</div>
</g:if>
<form id="loginForm">
    <label>用户名：</label>
    <input type="text" name="username" required /><br/>

    <label>密码：</label>
    <input type="password" name="password" required /><br/>

    <input type="submit" value="登录"/>
</form>

<script type="text/javascript">
    $(document).ready(function(){
        $('#loginForm').submit(function(e) {
            e.preventDefault();
            var data = {
                username: $('input[name="username"]').val(),
                password: $('input[name="password"]').val()
            };
            $.ajax({
                url: '${createLink(controller: "auth", action: "login")}',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(data),
                success: function(response) {
                    // 登录成功，依据返回的角色信息进行跳转
                    console.log('登录成功', response);
                    localStorage.setItem("jwt", data.token);
                    if (response.role === 'ADMIN') {
                        window.location.href = '/product/inventoryManagePage';
                    } else {
                        window.location.href = '/product/buyProductPage';
                    }
                },
                error: function(xhr) {
                    // 处理错误情况
                    console.error('登录失败', xhr);
                }
            });
        });
    });
</script>

</body>
</html>
