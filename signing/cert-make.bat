REM generate public/private key, usually RSA, but could be DSA.
REM Modern certs are mostly RSA, but DSA certs work in Java 1.2.
REM set up distinguished name as your Certificate Authority requests.
keytool -genkey -keyalg RSA -alias pluginsigner -dname "CN=rhomobile.com, OU=Rhomobile, O=Rhomobile Inc, L=Victoria, S=BC, C=US"

REM export cert request
keytool -certreq -alias pluginsigner -file certrequest.csr