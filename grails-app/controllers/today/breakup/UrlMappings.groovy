package today.breakup

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }
        //"/"(controller: "user", action: "loginPage") // 设置默认访问 login 页面
        // 商品购买页面
        "/product/buy"(controller: "product", action: "buyProductPage")
        "/"(controller: "auth", action: "login")
        // 库存管理页面
        "/product/inventory"(controller: "product", action: "inventoryManagePage")

//        "/"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
