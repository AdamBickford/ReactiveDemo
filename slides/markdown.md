
class: center, top

# Reactive Programming 

<h3 style="margin:4px; padding:10px;">Spring Boot</h3> 
.subtext[h<span>ttps</span>://spring.io/projects/spring-boot]
<p style="height:4px">
<h3 style="margin:4px; padding:14px;">Project Reactor</h3>
.subtext[h<span>ttps</span>://projectreactor.io/]
---


##Functional vs Procedural

--

Procedural

--
+ Imperative
--

  + *How* to do the work

--
        + Structured Programming (for, while, etc)
--
        + State Management
--
        + Context Aware
--

Functional

--
+ Declarative

--
    + *What* to do
--
        + Functions
--
        + Minimal side effects
--
        + Context Agnostic
---



##Filtering Procedurally

```java
private Set<User> filterProdecural() {
    HashSet<User> set = new HashSet<>();
    List<User> users = getUsers();
    for (int i = 0; i < users.size(); i++) {
        User user = users.get(i);
        if (user.getFirstName().startsWith("a")) {
            set.add(user);
        }
    }
    return set;
}
```
---

##Filtering Functionally

```java
 private Set<User> filterFunctional() {
    return getUsers().stream()
        .filter(user -> user.getFirstName().startsWith("a"))
        .collect(Collectors.toSet());
    }
```

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


# What is Reactive Programming?

--


+ Functional programming over time


--

+ Async

--

+ Code Reacts to changes in Data

--

+ Observer pattern

---

# Why Reactive?


--

+ Concurrency Agnostic

--
  
+ Efficiently Utilize Memory
  
--
  
  + Reduce Threads
  
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

