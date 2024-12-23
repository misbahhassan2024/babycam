package com.mexemai.babycam.aicam.Helpers

class Config{
    companion object {
        val myArray = arrayOf(
            "-1", // PING
            "0", // FlashLight On
            "1", // Flashlight Off
            "2", // Brightness Low
            "3", // Brightness High
            "4", // Alert On
            "5", // Alert Off
            "6", // Baby Monitor
        )
    }
}

//CLASSES = [
//'baby',
//'baby-crawling',
//'baby-lifted',
//'baby-lying-on-back',
//'baby-lying-on-left-side',
//'baby-lying-on-right-side',
//'baby-lying-on-stomach',
//'baby-sitting',
//'baby-sitting-assisted',
//'null'
//]

/* To Call it in function like this
 val array = Config.myArray
    for (item in array) {
        println(item)
    }

* */