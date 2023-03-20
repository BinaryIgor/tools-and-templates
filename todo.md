* Simple http-based pub-sub-proxy
  * subscriber: ```{  aliveAt, topics, url }```
  * subscriber, required endpoint
    * POST
    * /events, ```{ topic: /topic, event: {} }```
  * publisher
    * POST to pub-sub-proxy
    * /events, ```{ topic: /topic, event: {} }``` (same as subscriber gets)
    * get all active subscribers, if topics they have -> send message
    * exponential backoff retry of type -> 1s, 2s, 4s, 8s, 16s -> die
  * subscriber sends 
* Compare that to ready-to-use RabbitMQ prometheus monitoring/dashboards