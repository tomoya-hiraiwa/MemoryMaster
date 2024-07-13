package edu.ws2024.taskmaster.model

import com.google.gson.Gson

data class Account(var name: String = "", var password: String = "")


data class Site(
    var name: String = "",
    var type: String = "",
    var description: String = "",
    var url: String = "",
    var isFav: Boolean = false
)

data class PicData(var name: String = "",var filePath: String = "")

val gson = Gson()


var accountName = ""

var siteDetailData = Site()

var pictureDetailData = PicData()

var pictureData = mutableListOf<PicData>()


var originSiteData = mutableListOf<Site>()

enum class DeleteType{
    Site,Picture
}

