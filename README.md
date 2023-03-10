# SECBlockchain

---

# 1. How to run
The system takes as input the basic configuration file which provides
the desired number of members and clients with the respective ids, hostnames
and port numbers.  
The specific description of this file's commands is given in **1.1**  
  
The config file(s) must be in the root directory of **blockchain-initiator** module.  
After having the configuration file, one can start the system by going to the
**blockchain-initiator** directory and providing the following arguments:  
```shell
mvn exec:java -Dexec.args="config_file -gen -debug"
```
where: 
- **config_file** is the name of the file (just the name, not the full path)
assuming the file is in the root directory of **blockchain-initiator**.
- **-gen** is an optional argument to whether or not re-generate all the keys
to the members (note that if the config file changes the number of members, then 
it must be run with -gen on).
- **-debug** is necessary only if the config file includes the additional commands
stated in **2**. Prints additional information about each process. Must be allways
the last flag.

## 1.1 - Config file grammar 
### 1.1.1 -  Create Processes
``` HTML
P <id> <type> <hostname>:<port>
       -> <id> : integer number for the process id
       -> <type> : Process type: 'M' - blockchain member,
                                 'C' - client
```
---
### 1.1.2 - Config file example
```
P 1 M 127.0.0.1:10001
P 2 M 127.0.0.1:10002
P 3 M 127.0.0.1:10003
P 4 M 127.0.0.1:10004
P 5 C 127.0.0.1:10005
```

# 2. Tests
Behavior tests can be made through the configuration file by adding commands to perform a 
specific action on a specific timestamp as described below.

### 2.1 - Slot duration
Slots are used to manipulate the time when a command is executed more easily
```HTML
T <duration>
     -> <duration> : time of each slot in milliseconds
```    
### 2.2 - Member operations (for arbitrary behavior simulation)
```HTML
A <id> <operations>
       -> <operations> : <operation> <operations>*
       -> <operation> : (<id>, <operator>) | (<id>, A, <id>)
                           -> <id> : member id (cannot be the leader)
                           -> <operator> : 'O' - Omit messages,
                                           'C' - Arbitrarly corrupt messages,
                                           'A' - authenticate as process with <id>
```
### 2.3 - Client requests
```HTML
R <id> <requests>
       -> <requests> : <request> <requests>*
       -> <request> : (<id>, "<string>", <delay>)
               -> <id> : client id
               -> <string> : Any combination of characters
               -> <delay> : time of delay for the request since entering the slot in millis
```
### 2.4 - Config file example (for debug)
```
P 1 M 127.0.0.1:10001
P 2 M 127.0.0.1:10002
P 3 M 127.0.0.1:10003
P 4 M 127.0.0.1:10004
P 5 C 127.0.0.1:10005
T 500
A 2 (1, O) (2, C) (4, A, 3)
R 2 (5, "balelas-string", 10) (6, "popota", 300)
```
