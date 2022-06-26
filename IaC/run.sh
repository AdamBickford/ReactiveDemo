aws cloudformation create-stack --stack-name mystack --template-body file://IaC/foo.template

aws cloudformation delete-stack --stack-name mystack

aws s3 cp s3://BUCKET-NAME/FILENAME
