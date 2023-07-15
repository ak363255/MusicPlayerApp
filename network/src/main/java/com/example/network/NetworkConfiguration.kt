package com.example.network

class NetworkConfiguration private constructor(
    val baseHeader:Map<String,String>,
    val requestTimeOut:Long,
    val connectionTimeOut:Long,
    val readTimeOut :Long,

) {
    class Builder(){
        private var baseHeader:Map<String,String> = mutableMapOf()
        private var connectionTimeOut:Long = 5000L
        private var requestTimeOut:Long = 5000L
        private var readTimeOut:Long = 5000L
        fun baseHeader(header:Map<String,String>){
            this.baseHeader = header
        }
        fun connectionTimeOut(connectionTimeOutInMIlli: Long){
            this.connectionTimeOut = connectionTimeOutInMIlli
        }
        fun requestTimeOut(requestTimeOutInMIlli: Long){
            this.requestTimeOut = requestTimeOutInMIlli
        }
        fun readTimeOut(readTimeOutInMIlli:Long){
            this.readTimeOut = readTimeOutInMIlli
        }
        fun build():NetworkConfiguration{
            return NetworkConfiguration(
                baseHeader = baseHeader,
                requestTimeOut = requestTimeOut,
                readTimeOut = readTimeOut,
                connectionTimeOut = connectionTimeOut

            )
        }
    }

}