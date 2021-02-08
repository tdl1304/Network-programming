# How to run
#### How to generate keys  
* Make a folder
* cd into it
* Enter into console: `keytool -genkey -alias  signFiles -keystore yourfilename -keyalg RSA`
* Enter answers to your unique key and remember the password

#### Run SSL server and client by entering the commands:  
* Create jar files of the java files
* Enter: `java -jar -Djavax.net.ssl.keyStore=path-to-keystore -Djavax.net.ssl.keyStorePassword=yourpassword "...SSLServer.jar"`  
* Enter: `java -jar -Djavax.net.ssl.trustStore=path-to-keystore -Djavax.net.ssl.trustStorePassword=yourpassword "...SSLClient.jar"`
