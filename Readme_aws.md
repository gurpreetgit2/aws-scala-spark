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
location: ap-southeast-2
```

## Logout from AWS CLI

```bash
$ aws logout
```

## Get the caller identity

```bash
aws sts get-caller-identity
```

## Get the details

```bash
aws configure list
```

## Created s3 bucket and then uploaded files like this

```bash
aws s3 rm s3://data-pipeline-atwal/input/account_data.csv
aws s3 cp /Users/gurpreetatwal/spark-scala-learn/src/main/resources/account_data.csv s3://data-pipeline-atwal/input/account_data.csv
aws s3 ls s3://data-pipeline-atwal/input/   
```