# Spark Scala Data Pipeline

## Environment

- macOS (Apple Silicon)
- Java / OpenJDK: 17.0.17
- Apache Spark: 3.5.6 — installed at `$HOME/spark-local/spark-3.5.6-bin-hadoop3`
- Scala: 2.12.18
- Build tool: sbt
- IDE: Kiro (VS Code)

## Project structure

```text
spark-scala-learn/
├── Makefile
├── build.sbt
├── requirements.md
└── src/main/scala/
    ├── Main.scala
    ├── DataProcessor.scala
    └── S3Connection.scala
```

## 1. AWS CLI setup

Install the latest AWS CLI:

```bash
curl -fsSL https://awscli.amazonaws.com/v2/install.sh | sudo bash -s -- --system
```

Verify installation and current config:

```bash
aws --version
aws configure list-profiles
aws configure list
```

AWS config is stored at `~/.aws`.

## 2. AWS login (manual — opens browser)

```bash
aws login
```

After successful login, export temporary credentials as environment variables so Spark's S3A provider can read them:

```bash
eval "$(aws configure export-credentials --profile default --format env)"
```

Verify credentials are set:

```bash
[ -n "$AWS_ACCESS_KEY_ID" ] && \
[ -n "$AWS_SECRET_ACCESS_KEY" ] && \
[ -n "$AWS_SESSION_TOKEN" ] && \
echo "AWS temporary credentials are set"
```

Verify identity:

```bash
aws sts get-caller-identity
```

## 3. S3 bucket

Bucket: `data-pipeline-atwal`, region: `ap-southeast-2`

Upload input files:

```bash
aws s3 cp src/main/resources/account_data.csv s3://data-pipeline-atwal/input/account_data.csv
aws s3 cp src/main/resources/address_data.csv s3://data-pipeline-atwal/input/address_data.csv
aws s3 cp src/main/resources/customer_data.csv s3://data-pipeline-atwal/input/customer_data.csv
```

List files:

```bash
aws s3 ls s3://data-pipeline-atwal/input/
```

Remove a file:

```bash
aws s3 rm s3://data-pipeline-atwal/input/account_data.csv
```

## 4. Build and run

Dependencies are managed via `build.sbt`. All commands are run via `make`.

**Step 1 — Login to AWS (opens browser):**

```bash
aws login
```

**Step 2 — Export temporary credentials:**

```bash
eval "$(aws configure export-credentials --profile default --format env)"
```

**Step 3 — Run the pipeline:**

All `make` commands must be run from the project root:

```bash
cd spark-scala-learn
```

```bash
# Run locally — thin JAR, Spark runs on this machine
make local

# Run on EMR — fat JAR uploaded to S3, processing on the cluster
make emr
```

Individual targets:

```bash
make check-credentials   # verify AWS env vars are set before running
make build-local         # sbt update + sbt clean package (thin JAR for local)
make build-emr           # sbt clean assembly (fat JAR for EMR)
make upload-emr          # build fat JAR and upload to S3
make clean               # wipe compiled output
```

The generated JAR is at:

```text
target/scala-2.12/sparkdatasetlearning_2.12-0.1.0.jar
```

## 5. spark-submit (manual equivalent of make local)

```bash
"$HOME/spark-local/spark-3.5.6-bin-hadoop3/bin/spark-submit" \
  --class Main \
  --master 'local[*]' \
  --packages org.apache.hadoop:hadoop-aws:3.3.4 \
  target/scala-2.12/sparkdatasetlearning_2.12-0.1.0.jar \
  data-pipeline-atwal \
  ap-southeast-2
```

## 6. AWS logout

```bash
aws logout
```

## 7. Background

- **sbt** resolves dependencies, compiles Scala source into JVM bytecode, and packages it into a JAR
- **spark-submit** launches the application on Spark
- `local[*]` runs Spark locally using all available CPU cores
- `--packages org.apache.hadoop:hadoop-aws:3.3.4` supplies the S3A connector at runtime without bundling it in the JAR
- Temporary AWS credentials (`AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`, `AWS_SESSION_TOKEN`) are picked up automatically by Spark's `EnvironmentVariableCredentialsProvider`
- Keep Scala 2.12.18 and Spark 3.5.6 aligned — mismatched versions will compile but fail at runtime
