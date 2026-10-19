## run these commands to check the current AWS configurations

```bash
aws --version
aws configure list-profiles
aws configure list
```
## Path to the aws config - which now has been cleared - it had old IAM keys

```bash
cd /Users/gurpreetatwal/.aws
```

## Installed latest version using

```bash
curl -fsSL https://awscli.amazonaws.com/v2/install.sh | sudo bash -s -- --system
```

## Login in aws cli

```bash
aws login
```

## Run this in your terminal after successfully completing `aws login` so that your temporary AWS credentials become environment variables that Spark's S3A credential provider can read

```bash
eval "$(aws configure export-credentials --profile default --format env)"
```

## Verify the environment variables

```bash
[ -n "$AWS_ACCESS_KEY_ID" ] && \
[ -n "$AWS_SECRET_ACCESS_KEY" ] && \
[ -n "$AWS_SESSION_TOKEN" ] && \
echo "AWS temporary credentials are set"
```

## Get the caller identity

```bash
aws sts get-caller-identity
```

## Get the details

```bash
aws configure list
aws configure list-profiles
```

## Created s3 bucket and then uploaded files like this

```bash
aws s3 rm s3://data-pipeline-atwal/input/account_data.csv
aws s3 cp /Users/gurpreetatwal/spark-scala-learn/src/main/resources/account_data.csv s3://data-pipeline-atwal/input/account_data.csv
aws s3 ls s3://data-pipeline-atwal/input/   
```

## Logout from AWS CLI

```bash
$ aws logout
```

## Open the project in terminal and download dependencies 

```bash
sbt update
sbt clean package
```

## Execute the command to read and process s3 files

```bash
spark-submit \
  --class S3ReadTest \
  --master 'local[*]' \
  --packages org.apache.hadoop:hadoop-aws:3.3.4 \
  target/scala-2.12/sparkdatasetlearning_2.12-0.1.0.jar \
  data-pipeline-atwal \
  ap-southeast-2
  ```