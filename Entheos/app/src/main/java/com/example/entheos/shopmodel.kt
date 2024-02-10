package com.example.entheos


class shopmodel {

    var image:Int? = null
    var title:String? = null
    var text:String? = null
    var let:Double? = null
    var lon:Double? = null

    constructor(image: Int,title:String,text:String,let:Double,lon:Double){
        this.image = image
        this.title = title
        this.text = text
        this.let = let
        this.lon = lon
    }
}