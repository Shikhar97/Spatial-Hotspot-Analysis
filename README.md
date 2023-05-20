# Project-1-Group-6-Phase-2

As we have created our dockerfile, you can download and run using the following script:

1. Run `docker pull shikharg1997/group6-project1-phase2-bonus:v0` to download image 
2. Run `docker run -it --rm shikharg1997/group6-project1-phase2-bonus:v0` to start the container. 
3. Run `sbt assembly`
4. Run `spark-submit target/scala-2.12/CSE511-Hotspot-Analysis-assembly-0.1.0.jar result/output hotzoneanalysis src/resources/point-hotzone.csv src/resources/zone-hotzone.csv hotcellanalysis src/resources/yellow_tripdata_2009-01_point.csv`

Docker Image: `docker pull shikharg1997/group6-project1-phase2-bonus:v0`
