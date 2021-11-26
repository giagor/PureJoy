package com.topview.purejoy.home.data.bean

class UserJson {
    var profile : Profile? = null
    var code: Int? = null
    
    class Profile{
        var avatarUrl : String? = null
        var nickname : String? = null
        var userId : Long? = null
    }
}