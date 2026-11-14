# ── Shared config ────────────────────────────────────────────────────────────
BUCKET       := data-pipeline-atwal
REGION       := ap-southeast-2
CLASS        := Main

# ── Local config ─────────────────────────────────────────────────────────────
LOCAL_JAR    := target/scala-2.12/sparkdatasetlearning_2.12-0.1.0.jar
SPARK_SUBMIT := $(HOME)/spark-local/spark-3.5.6-bin-hadoop3/bin/spark-submit

# ── EMR config (dummy values — replace when cluster is available) ─────────────
EMR_CLUSTER_ID := j-XXXXXXXXXXXX
EMR_JAR_S3     := s3://$(BUCKET)/jars/sparkdatasetlearning-assembly.jar

.PHONY: check-credentials build-local build-emr upload-emr local emr clean

# ─────────────────────────────────────────────────────────────────────────────
# SHARED
# ─────────────────────────────────────────────────────────────────────────────

## Verify AWS temporary credentials are exported in the current shell
check-credentials:
	@[ -n "$$AWS_ACCESS_KEY_ID" ] && \
	 [ -n "$$AWS_SECRET_ACCESS_KEY" ] && \
	 [ -n "$$AWS_SESSION_TOKEN" ] || \
	 { echo ""; \
	   echo "ERROR: AWS credentials not set. Run:"; \
	   echo "  aws login"; \
	   echo "  eval \"\$$(aws configure export-credentials --profile default --format env)\""; \
	   echo ""; \
	   exit 1; }
	@echo "AWS credentials OK"

# ─────────────────────────────────────────────────────────────────────────────
# LOCAL branch: thin JAR, spark-submit on this machine
# ─────────────────────────────────────────────────────────────────────────────

## Resolve dependencies and build a thin JAR (Spark provided by local install)
build-local:
	sbt update
	sbt clean package

## Run the full pipeline locally
local: check-credentials build-local
	$(SPARK_SUBMIT) \
		--class $(CLASS) \
		--master 'local[*]' \
		--packages org.apache.hadoop:hadoop-aws:3.3.4 \
		$(LOCAL_JAR) \
		$(BUCKET) \
		$(REGION)

# ─────────────────────────────────────────────────────────────────────────────
# EMR branch: fat JAR, job submitted to cluster — all processing on EMR
# ─────────────────────────────────────────────────────────────────────────────

## Build a fat JAR (spark-sql marked provided, hadoop-aws bundled)
build-emr:
	sbt -Dbuild.target=emr clean assembly

## Upload the fat JAR to S3
upload-emr: build-emr
	aws s3 cp target/scala-2.12/sparkdatasetlearning-assembly.jar $(EMR_JAR_S3)

## Submit the job to the EMR cluster (cluster must already be running)
emr: check-credentials upload-emr
	aws emr add-steps \
		--cluster-id $(EMR_CLUSTER_ID) \
		--steps Type=Spark,Name="CustomerAccountProcessing",\
ActionOnFailure=CONTINUE,\
Args=[--class,$(CLASS),\
--deploy-mode,cluster,\
$(EMR_JAR_S3),\
$(BUCKET),\
$(REGION)]

# ─────────────────────────────────────────────────────────────────────────────
# UTILS
# ─────────────────────────────────────────────────────────────────────────────

## Wipe compiled output
clean:
	sbt clean
