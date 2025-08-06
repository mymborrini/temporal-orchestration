# Tls certificate generated

Follow the instructions

#### Create tls directory
    mkdir tls
    cd tls
#### Generate a private key for the CA
    openssl genrsa -out ca.key 2048
#### Generate a self-signed certificate for the CA
    openssl req -new -x509 -key ca.key -out ca.pem -days 365 -subj "/CN=Temporal CA"
#### Generate a private key for the server
    openssl genrsa -out key.pem 2048
#### Generate a request to sign the certificate
    openssl req -new -key key.pem -out server.csr -subj "/CN=temporal"
#### Sign the CSR with the CA to create the certificate of the server
    openssl x509 -req -in server.csr -CA ca.pem -CAkey ca.key -CAcreateserial -out cert.pem -days 365
#### Remove temporary files
    rm server.csr ca.key ca.srl