FROM ubuntu:20.04

#Installing dependencies
ENV DEBIAN_FRONTEND=nonintercative
ENV TZ=America/Phoenix
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
RUN apt-get update && apt-get install -y wget gpg curl mlocate tzdata default-jdk

#Installing Spark 3.XX
RUN cd tmp; wget https://dlcdn.apache.org/spark/spark-3.3.2/spark-3.3.2-bin-hadoop3.tgz; tar xvf spark-3.3.2-bin-hadoop3.tgz

#Setting up the root directory
RUN mv /tmp/spark-3.3.2-bin-hadoop3 /opt/spark

#Setting the environment variables
ENV SPARK_HOME=/opt/spark
ENV PATH=$PATH:$SPARK_HOME/bin:$SPARK_HOME/sbin

#Installing scala
RUN wget "https://downloads.lightbend.com/scala/2.12.10/scala-2.12.10.deb"
RUN dpkg -i scala-2.12.10.deb

#Installing sbt
RUN echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | tee /etc/apt/sources.list.d/sbt.list
RUN echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | tee /etc/apt/sources.list.d/sbt_old.list
RUN curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" |  gpg --no-default-keyring --keyring gnupg-ring:/etc/apt/trusted.gpg.d/scalasbt-release.gpg --import
RUN chmod 644 /etc/apt/trusted.gpg.d/scalasbt-release.gpg
RUN apt-get update -y && apt-get install sbt -y

#Setting up work_dir
WORKDIR /cse511
COPY build.sbt .
COPY src src
COPY project project

# Set the default command to run when starting the container
CMD ["/bin/bash"]