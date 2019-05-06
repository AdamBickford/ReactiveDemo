
class: center, top

# Reactive Programming 

<h3 style="margin:4px; padding:10px;">Spring Boot</h3> 
.subtext[h<span>ttps</span>://spring.io/projects/spring-boot]
<p style="height:4px">
<h3 style="margin:4px; padding:14px;">Project Reactor</h3>
.subtext[h<span>ttps</span>://projectreactor.io/]
---




# Why Reactive?

--

+ Async

--
  + Efficiently Scale I/O
--
name: 'outline'
  + Rich operators
--

  + Micro-services
---
template:'outline'
  + Micro-services (Obviously)
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

#Why Functional?
+ More Structure

--

  + Why are you looping?
--
name:'pro-functional'
        + Filter
---

##Filtering Procedurally

```java
private Set<User> filterProcedural() {
    Set<User> set = new HashSet<>();
    for (User user : getUsers()) {
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
        + Map
---

#Mapping Procedurally

```java
private Set<String> firstNamesImperative() {
    Set<String> firstNames = new HashSet<>();
    List<User> users = getUsers();
    for (int i = 0; i < users.size(); i++) {
        firstNames.add(users.get(i).getFirstName());
    }
    return firstNames;
}
```

---

#Mapping Functionally

```java
private Set<String> firstNamesFunctional() {
    return getUsers().stream()
        .map(User::getFirstName)
        .collect(Collectors.toSet());
}
```
 
---
template:'pro-functional'
        + Reduce

---
