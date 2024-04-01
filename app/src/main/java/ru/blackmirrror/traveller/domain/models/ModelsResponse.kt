package ru.blackmirrror.traveller.domain.models

import com.google.gson.annotations.SerializedName

data class UserResponse (
    @SerializedName("id"          ) var id          : Long,
    @SerializedName("username"    ) var username    : String
)

data class UserRequest (
    @SerializedName("username"    ) var username    : String,
    @SerializedName("password"    ) var password    : String
)

data class Mark (
    @SerializedName("id"          ) var id          : Long?  = null,
    @SerializedName("latitude"    ) var latitude    : Double,
    @SerializedName("longitude"   ) var longitude   : Double,
    @SerializedName("description" ) var description : String,
    @SerializedName("imageUrl"    ) var imageUrl    : String?  = null,
    @SerializedName("likes"       ) var likes       : Int = 0,
    @SerializedName("user"        ) var user        : UserResponse?  = null,
    @SerializedName("dateChanges" ) var dateChanges : Int?    = null,
    @SerializedName("dateCreate"  ) var dateCreate  : Int?    = null
)

data class Favorite (
    @SerializedName("id"          ) var id          : Long?  = null,
    @SerializedName("mark"        ) var mark        : Mark,
    @SerializedName("user"        ) var user        : UserResponse,
)

data class Subscribe (
    @SerializedName("id"          ) var id          : Long?  = null,
    @SerializedName("user"        ) var user        : UserResponse,
    @SerializedName("subscribe"   ) var subscribe   : UserResponse,
)