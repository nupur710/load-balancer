This is my implemenataion of a load balancer (from Coding Challenges: https://codingchallenges.fyi/challenges/challenge-load-balancer) written in Java. A load balancer
distributes incoming client requests across multiple server instances to ensure efficient resource utilization.

Components/Features:
1. Server: represents individual server instances
2. ServerManager: manages creation and retrival of server instances
3. LoadBalancer: main component that recieves incoming client request and forwards them to the appropriate server based on specified strategy
4. HealthCheck: periodically checks health of servers and maintains a list of healthy servers
5. LoadBalancerStrategy: implements algorithms for load balancing & session persistence:
	i. Round-Robin
	ii. Weighted Round-Robin
	iii. Least Connections

Setup and Running:
1. Clone repository
2. Build using Maven: mvn clean package
3. Run: run-load-balancer.bat
This starts server and load balancer instances

Once the load balancer is up and running, you can send http requests to it:
curl http://localhost:1221
The load balancer will distribute these requests among the available server instances

Round-Robin strategy:

![RR](https://github.com/user-attachments/assets/bfae43fe-5af8-47e3-b5bf-f176f80915fd)

Weighted Round-Robin strategy:

![image](https://github.com/user-attachments/assets/f9d48547-67d1-44fd-aad5-fc477b354248)

Configuration: The following can be configured in src/main/resources/config.properties:
1. server.startPort
2. noOfServers
3. loadbalancer.strategy
4. healthCheck.interval
5. loadBalancer.port
6. sessionTimeout


