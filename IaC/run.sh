aws cloudformation create-stack --stack-name mystack \
  --template-body file://foo.template \
  --parameters ParameterKey=S3BucketResource,ParameterValue=arn:aws:s3:::rx-demo/* \
  --capabilities CAPABILITY_NAMED_IAM

###
              sudo docker run --rm -i grafana/k6 run -e THE_IP=${THE_IP} - <load.js | tail -n 18 > /home/ec2-user/results.txt

aws cloudformation update-stack --stack-name mystack \
  --template-body file://foo.template \
  --parameters ParameterKey=S3BucketResource,ParameterValue=arn:aws:s3:::rx-demo/* \
  --capabilities CAPABILITY_NAMED_IAM

aws cloudformation delete-stack --stack-name mystack

aws s3 cp s3://BUCKET-NAME/FILENAME

aws s3 cp s3://rx-demo/foo.template .

aws s3 cp s3://../spring-web-app/target/spring-web-app-0.0.1-SNAPSHOT.jar



sudo yum update -y
sudo amazon-linux-extras install docker -y
sudo service docker start
sudo usermod -a -G docker ec2-user
aws s3 cp s3://rx-demo/load.js /home/ec2-user
sudo docker run --rm -i grafana/k6 run -e THE_IP=host.docker.internal - <load.js | echo -e "adam was here:\n\n\n\n\n\n\n\n\n $(tail -n 18)" >results.txt
tail -n 18 tmp.txt >results.txt

sudo docker run --rm -i influxdb=http://localhost:8086/myk6db grafana/k6 run - <~/code/ReactiveDemo/performance/load.js

#git clone 'https://github.com/grafana/k6'
#cd k6
#docker-compose up -d \
#    influxdb \
#    grafana
##docker-compose run -v   'c:\Users\esp_2\code\ReactiveDemo\performance':/scripts \
#docker-compose run -v   /mnt/c/Users/esp_2/code/ReactiveDemo/performance:/scripts \
#    k6 run /scripts/load.js
#
#docker run --rm -v /c/Users/esp_2/code/ReactiveDemo/performance:/scripts:/src -i grafana/k6 run /src/load.js

docker run --rm -i grafana/k6 run -e THE_IP=${THE_IP} - <load.js | tail -n 18 > /home/ec2-user/results.txt

#    // const BASE_URL = 'http://host.docker.internal:8081/request?latencies='; // make sure this is not production


curl http://$(cat theip.txt):8080/request?latencies=1000,2000


docker run --rm -i grafana/k6 run -e THE_IP=$(cat theip.txt) - <load.js | tail -n 18 > /home/ec2-user/results.txt


docker run --rm -i grafana/k6 run -e THE_IP=host.docker.internal - <load.js | tail -n 18 > results.txt
