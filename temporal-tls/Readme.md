# Temporal tls

## Generate tls

### Tls certificate signed by a dummy CA with SAN

    ./tls/create_tls_with_ca_san.sh

1. First, a "root" Certificate Authority (CA) is created:
   - A private key for the CA is generated 
   - A self-signed certificate for this CA is created

2. Then a certificate for the server is created:
   - A private key for the server is generated 
   - A Certificate Signing Request (CSR) for the server is created 
   - The server's certificate is signed by the root CA created in the first step

So, the final result is:

 - A root CA certificate that is self-signed 
 - A certificate for the server that is signed by the root CA, therefore not self-signed

This approach simulates a certificate hierarchy similar to that used in the real world, where web server certificates are signed by recognized CAs. The main difference is that in this case, the root CA you've created is not recognized by default by browsers or operating systems, so it should be manually added as a trusted CA in the contexts where it will be used (typically in development or testing environments).

During this operation a new file is generated.
The ca.srl file is a serial number file used by OpenSSL when signing certificates. Here's more detail about it:

1. Purpose: It keeps track of the serial numbers of certificates issued by the CA.
2. Content: It contains a hexadecimal number representing the last serial number used.
3. Creation: It's created when you use the -CAcreateserial option in the OpenSSL command that signs the certificate.
4. Usage: Each time the CA signs a new certificate, it increments the number in this file and uses it as the serial number for the new certificate.
5. Importance: Serial numbers must be unique for each certificate issued by a CA. This file helps ensure that uniqueness.
6. Format: It's a simple text file containing a hexadecimal number.

In subsequent certificate signings, OpenSSL will read this file, increment the number, and use it for the next certificate. If you're issuing multiple certificates with the same CA, this file helps maintain the uniqueness of serial numbers across those certificates.

### Tls certificate signed by a dummy CA without SAN

      ./tls/create_tls_with_ca.sh

At a logical level, the process is essentially the same: a CA is created, a key and CSR are generated for a server, and the CA is used to sign the server's certificate. The main difference lies in the amount of details included in the certificates and the lack of the SubjectAltName extension, which could be important for some modern uses of SSL/TLS certificates.


## Connection

### Without TLS

You can connect to temporal without tls

    grpcurl -plaintext localhost:45000 list


### With TLS
In this case you will pass through nginx. Remember to run nginx with this volume mount. (See ./docker-compose.yml)
    
    - ./nginx-l7.conf:/etc/nginx/conf.d/default.conf    

Run this command from the project folder

    grpcurl -cacert ./temporal-tls/tls/server.crt localhost:45003 list
