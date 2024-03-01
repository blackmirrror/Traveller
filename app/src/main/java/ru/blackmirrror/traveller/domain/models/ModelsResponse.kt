package ru.blackmirrror.traveller.domain.models

import com.google.gson.annotations.SerializedName

data class MarkResponse (
    @SerializedName("id"          ) var id          : String?  = null,
    @SerializedName("latitude"    ) var latitude    : Double,
    @SerializedName("longitude"   ) var longitude   : Double,
    @SerializedName("description" ) var description : String,
    @SerializedName("image"       ) var image       : String?  = null,
    @SerializedName("likes"       ) var likes       : Int = 0,
    @SerializedName("is_liked"    ) var isLiked     : Boolean = false,
    @SerializedName("user"        ) var user        : UserResponse?  = null
)

data class UserResponse (
    @SerializedName("id"          ) var id          : String,
    @SerializedName("username"    ) var username    : String
)

data class UserRequest (
    @SerializedName("username"    ) var username    : String,
    @SerializedName("password"    ) var password    : String
)

data class LoginResponse (
    @SerializedName("access_token") var accessToken : String,
    @SerializedName("token_type"  ) var tokenType   : String
)