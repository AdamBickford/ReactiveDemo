
class: center, top

# Reactive Programming 

<h3 style="margin:4px; padding:10px;">Spring Boot</h3> 
.subtext[h<span>ttps</span>://spring.io/projects/spring-boot]
<p style="height:4px">
<h3 style="margin:4px; padding:14px;">Project Reactor</h3>
.subtext[h<span>ttps</span>://projectreactor.io/]
---

name:'pvf'

##Procedural vs Functional  

--
name:'procedural'
Procedural

--
+ Imperative
--

    + *How* to do the work
--
        + Loops
--
        + Mutable State
--
        + Shared State
---
template:'procedural'

+ What's the problem?
--

    + Difficult to reason
--
        + State can change on every iteration
--
        + State can come from anywhere
--
    + Difficult to test
--
        + Environment setup can be complex
--
    + Difficult to compose
--
    + Concurrency dependent!
--
        + CPUs are not getting faster, more cores are common

---


name:'pro-functional'
template:'pro-functional'
  
  ##Why are you looping?      
--

  + Map
  
--

  + Filter
  
--

  + Query (any, all, etc)
  
--
   
  + Aggregate (count, sum, reduce, etc)

--

  + #There's a function for that!

---

template:'pvf'
name:'functional'
Functional 

--
+ Declarative

--
    + *What* to do
--
        + Functions
--
        + Immutable state
--
        + No side effects
---
template:'functional'
+ Benefits
--

    + Pure Functions
--
        + Easy to test
--
        + Easy to reason
--
            + Output only depends on input
--
        + Composable
--
        + Deferred Execution
--
    + Immutable state
--
    + Minimal side effects
--
        + Concurrency Agnostic
---
template:'functional'

+ What's the problem *now*?

--
  + Concurrency! (Still? How?!)
--

      + Blocking is wasteful
--
      + Blocking adds latency
--
      + Threads use memory
---

## What is Reactive Programming?

--

+ Async Functional Programming

--

+ Code Reacts to Changes in Data

--

+ Time is a first class citizen

---

# Why Reactive?


  
  
--
name: 'outline'

  + Rich operators

--

  + Micro-services

---

template:'outline'
  + Micro-services (Obviously)
  
---

#Project Reactor

+ Implementation of ReactiveX
.subtext[h<span>ttps</span>://reactivex.io/]

--

  + Spring 5
  
--
  
  + Java 9
  
--

  + .NET
  
--

  + Javascript
  
--

  + Python

---

#Publisher

--

+ Observer like

--

  + Mono 0-1

--

  + Flux 0-N (including infinite)

--

+ Define pipeline of operators 

--

+ Nothing happens until subscription

--

  + Cold

--

      + Each subscriber goes to source
    
--

  + Hot
 
--
  
      + Shared (pub-sub) 

---

#Operators

--

+ Transform data
  
--

  + Filter
  
--

  + Map
  
--

  + Zip

--

+ Control data flow

--

  + Switching
  
--

  + Delaying
  
--

  + Buffering
