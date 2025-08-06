# In summary, these commands create a CA, generate a key and CSR for a server,
# and then use the CA to sign the server's certificate.
# This process is commonly used to create SSL/TLS certificates
# for use in development or testing environments.

# This command generates a 2048-bit RSA private key for the Certificate Authority (CA) and saves it in the file ca.key.
openssl genrsa -out ca.key 2048

# This creates a self-signed certificate for the CA, valid for 365 days, using the private key generated in the previous step. The certificate includes subject information (country, state, locality, organization, and common name) and is saved as ca.crt
openssl req -new -x509 -days 365 -key ca.key -subj "/C=CN/ST=GD/L=SZ/O=Acme, Inc./CN=Acme Root CA" -out ca.crt

# This generates a new 2048-bit RSA private key for the server (server.key) and creates a Certificate Signing Request (CSR) for the server with the specified information. The CSR is saved as server.csr
openssl req -newkey rsa:2048 -nodes -keyout server.key -subj "/C=CN/ST=GD/L=SZ/O=Acme, Inc./CN=localhost" -out server.csr

# This command signs the server's certificate request (CSR) using the CA created in steps 1 and 2. It creates a certificate valid for 365 days, adds a SubjectAltName extension for "localhost", and saves the signed certificate as server.crt.
openssl x509 -req -extfile <(printf "subjectAltName=DNS:localhost") -days 365 -in server.csr -CA ca.crt -CAkey ca.key -CAcreateserial -out server.crt
