# SECBlockchain

---

# Requirements

|  | Version |
| --------------|---------|
| Java | 17      |
| Maven | 3.8.6 |

# 1. How to run
The system takes as input the basic configuration file which provides
the desired number of members and clients with the respective IDs, hostnames
and port numbers.  
The specific description of this file's commands is given in **1.1**  
  
The config file(s) must be in the root directory of **blockchain-initiator** module.  
After having the configuration file, one can start the system by  running
```shell
mvn install
```
in the main directory. After that, go to the
**blockchain-initiator** directory and execute the following command:  
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
- **IMPORTANT** - currently the system runs only with either `-gen` or both `-gen -debug` flags on

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

### 2.1 - Start time
Set the time at which the slots start counting
```HTML
S <hours>:<minutes>:<seconds>
```

### 2.2 - Slot duration
Slots are used to manipulate the time when a command is executed more easily
```HTML
T <duration>
     -> <duration> : time of each slot in milliseconds
```    
### 2.3 - Member operations (for arbitrary behavior simulation)
```HTML
A <slot> <operations>
       -> <operations> : <operation> <operations>*
       -> <operation> : (<id>, <operator>) | (<id>, A, <id>)
                           -> <id> : member id (cannot be the leader)
                           -> <operator> : 'O' - Omit messages,
                                           'C' - Arbitrarily corrupt messages,
                                           'A' - Set the id of sent messages as process with <id>
```
### 2.4 - Client requests
```HTML
R <slot> <requests>
       -> <requests> : <request> <requests>*
       -> <request> : (<senderId>, <operation><argumentWrapper>?, <gasPrice>, <gasLimit> )
               -> <senderId> : client id
               -> <operation> : 'C' - Create an account,
                                'T' - Transfer coins to a destination,
                                'B' - Check balance 
               -> <argumentWrapper> : 
                     -> In case of <operation> 'T': (<destination>, <amount>)
                            <destination> : the destination client id
                            <amount> : any combination of positive integers
                     -> In case of <operation> 'B' : (<typeOfRead>)
                            <typeOfRead> : 'W' - Weak read
                                           'S' - Strong read
               -> <gasPrice> : requests with higher gasPrices will be appended first
               -> <gasLimit> : maximum amount of gas the user is willing to pay (not currently used)
                
```
### 2.4 - Config File Example (for debug)
```
S 14:05:12
P 1 M 127.0.0.1:10001
P 2 M 127.0.0.1:10002
P 3 M 127.0.0.1:10003
P 4 M 127.0.0.1:10004
P 5 C 127.0.0.1:10005
P 6 C 127.0.0.1:10006
T 500
A 2 (1, O) (4, A, 3)
R 2 (5, C, 1, 1)            # client 5 creates an account
R 3 (5, B(W), 1, 1)         # client 5 checks balance with weak read
R 4 (5, T(6, 500), 1, 1)    # client 5 transfers 500 coins to client 6 in slot 3 with gasPrice of 1
```
Do not run the test above with the comments (#) as the current file parser will throw an error.
# 3. Run the provided set of tests
To run the provided set of tests just run the command in 1. with all 3 arguments, and change the file name.
All tests are under the **blockchain-initiator** directory.
