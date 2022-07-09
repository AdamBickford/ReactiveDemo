aws cloudformation create-stack --stack-name mystack \
  --template-body file://foo.template \
  --parameters ParameterKey=S3BucketResource,ParameterValue=arn:aws:s3:::rx-demo/* \
  --capabilities CAPABILITY_NAMED_IAM

###

aws cloudformation delete-stack --stack-name mystack

aws s3 cp s3://BUCKET-NAME/FILENAME

aws s3 cp s3://rx-demo/foo.template .
