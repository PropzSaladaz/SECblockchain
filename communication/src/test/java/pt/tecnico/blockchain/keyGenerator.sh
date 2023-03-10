# Generate private & public keys for mobile device

echo "creating mobile private key"
openssl genrsa -out process1-private.key

echo "creating process1 public key"
openssl rsa -in process1-private.key -pubout > process1-public.key


echo "creating process2 private key"
openssl genrsa -out process2-private.key

echo "creating process2 public key"
openssl rsa -in process2-private.key -pubout > process2-public.key


cp process1-public.key ../pt.tecnico.blockchain
cp process1-private.key ../pt.tecnico.blockchain


cp process2-public.key ../pt.tecnico.blockchain
cp process2-private.key ../pt.tecnico.blockchain