# Spark Scala Setup

## 1. Environment

- macOS (Apple Silicon)
- Java / OpenJDK: 17.0.17
- Apache Spark: 3.5.6
- Scala: 2.12.18 (compatible with Spark)
- Standalone Scala runner: 1.9.1 (default Scala 3.7.4)
- Build tool: sbt
- IDE: VS Code

## 2. Project Setup

Create a new project and the following directory structure:

```text
spark-scala-learn/
├── build.sbt
└── src/main/scala/
    └── PracticeCode.scala
```

Install sbt if required:

```bash
brew install sbt
```

## 3. Create `build.sbt`

```scala
ThisBuild / scalaVersion := "2.12.18"
ThisBuild / version := "0.1.0"

lazy val root = (project in file("."))
  .settings(
    name := "SparkDatasetLearning",
    libraryDependencies +=
      "org.apache.spark" %% "spark-sql" % "3.5.6"
  )
```

## 4. Compile and Package

Run from the project root:

```bash
sbt package
```

Generated JAR:

```text
target/scala-2.12/sparkdatasetlearning_2.12-0.1.0.jar
```

## 5. Execute with Spark

Using the explicit Spark installation:

```bash
"$HOME/spark-local/spark-3.5.6-bin-hadoop3/bin/spark-submit" \
  --class PracticeCode \
  --master 'local[*]' \
  target/scala-2.12/sparkdatasetlearning_2.12-0.1.0.jar
```

Or, if `spark-submit` is configured in `PATH` using following commands:

```bash
export SPARK_HOME="$HOME/spark-local/spark-3.5.6-bin-hadoop3"
export PATH="$SPARK_HOME/bin:$PATH"
```

```bash
spark-submit --class PracticeCode --master 'local[*]' target/scala-2.12/sparkdatasetlearning_2.12-0.1.0.jar
```

## 6. Background

- **sbt** resolves dependencies, compiles Scala source code into JVM bytecode, and packages it into a JAR.
- **spark-submit** launches the application on Spark.
- `local[*]` runs Spark locally using all available logical CPU cores.
- Re-run `sbt package` after modifying the Scala source code.

**Compatibility:** Keep Scala 2.12.18 and Spark 3.5.6 aligned. The standalone Scala 3.7.4 runner is not used to compile this project.
