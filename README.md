sa.sy.lxd
----
A Java Library interacting with the LXD REST API

## Installation
It requires install lxd and setup it before uses the Library
```
snap install lxd
sudo adduser $user lxd
lxd init
```
then add `sa.sy.lxd` to maven dependencies 
```
<dependency>
    <groupId>sa.sy</groupId>
    <artifactId>lxd</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## Progress
- [ ] Instances
    - [x] list
    - [x] get
    - [x] create
    - [ ] edit
    - [ ] delete

- [ ] Images
    - [x] list
    - [x] get
    - [ ] create
    - [ ] edit
    - [ ] delete
    
- [ ] Projects
    - [x] list
    - [x] get
    - [ ] create
    - [ ] edit
    - [ ] delete
    
- [ ] Operations
    - [x] list
    - [x] get
    - [ ] create
    - [ ] edit
    - [ ] delete
    
- [ ] Networks
    - [ ] list
    - [ ] get
    - [ ] create
    - [ ] edit
    - [ ] delete
    
- [ ] Profiles
    - [ ] list
    - [ ] get
    - [ ] create
    - [ ] edit
    - [ ] delete
 - [ ] events
 - [ ] Clusters
     - [ ] list
     - [ ] get
     - [ ] create
     - [ ] edit
     - [ ] delete
     
- [ ] Storage Pools
    - [ ] list
    - [ ] get
    - [ ] create
    - [ ] edit
    - [ ] delete
    
## License
The MIT License (MIT)

Copyright (c) 2020 Salem Yaslem 
[Read More...](LICENSE.md)