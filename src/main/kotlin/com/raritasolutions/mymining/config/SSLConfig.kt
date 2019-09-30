package com.raritasolutions.mymining.config

import org.apache.catalina.Context
import org.apache.catalina.connector.Connector
import org.apache.tomcat.util.descriptor.web.SecurityCollection
import org.apache.tomcat.util.descriptor.web.SecurityConstraint
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
@ConditionalOnProperty("security.require-ssl", havingValue = "true", matchIfMissing = false)
class SSLConfig {

    @Value("\${http.port}")
    var httpPort: Int = -1

    @Value("\${server.port}")
    var serverPort: Int = -1

    @Bean
    fun servletContainer() =
        object : TomcatServletWebServerFactory() {
            override fun postProcessContext(context: Context) {
                val securityConstraint = SecurityConstraint()
                securityConstraint.userConstraint = "CONFIDENTIAL"
                val collection = SecurityCollection()
                collection.addPattern("/*")
                securityConstraint.addCollection(collection)
                context.addConstraint(securityConstraint)
            }
        }.apply {
            addAdditionalTomcatConnectors(redirectConnector())
        }


    private fun redirectConnector(): Connector {
        val connector = Connector("org.apache.coyote.http11.Http11NioProtocol")
        return connector.apply {
            scheme = "http"
            port = httpPort
            secure = false
            redirectPort = serverPort
        }
    }

}