# Project-1-Group-6

As we have created our dockerfile, you can download and run using the following script:

1. Run `docker pull shikharg1997/group6-project1-phase1-bonus:v0` to download image 
2. Run `docker run -it --rm shikharg1997/group6-project1-phase1-bonus:v0` to start the container. 
3. Run `sbt assembly`
4. Run `spark-submit target/scala-2.12/CSE511-assembly-0.1.0.jar result/output rangequery src/resources/arealm10000.csv -93.63173,33.0183,-93.359203,33.219456
   rangejoinquery src/resources/arealm10000.csv src/resources/zcta10000.csv
   distancequery src/resources/arealm10000.csv -88.331492,32.324142 1
   distancejoinquery src/resources/arealm10000.csv src/resources/arealm10000.csv 0.1`

Docker Image: `docker pull shikharg1997/group6-project1-phase1-bonus:v0`
