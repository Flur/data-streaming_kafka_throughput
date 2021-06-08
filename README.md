# Data Streaming Kafka Throughput

## Docker Setup
- run docker compose up -d

## Producers and Consumers Setup
- install jdk
- install sbt ([mac](https://www.scala-sbt.org/1.x/docs/Installing-sbt-on-Mac.html), [windows](https://www.scala-sbt.org/1.x/docs/Installing-sbt-on-Windows.html))
- run ```sbt``` in terminal 
- run ```project consumer``` or ```project producer```
- then ```run``` to run app

to scale producers / consumer open new terminal window and do all instructions above

## Plot Results
- install python 3
- init venv ```python3 venv venv```
- activate venv that you just created ```source venv/bin/activate``` (for mac)
- install requirements ```pip3 install -r requirements.txt```
- run ```python3 plot.py``` to see graphs (you should run producers and consumers before you can draw graphs)
