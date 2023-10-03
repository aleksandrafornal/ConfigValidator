# ConfigValidator

### 
ConfigValidator class allows developers to validate the configuration.

It detects missing fields and objects, so developer know exactly what is absent.
Main advantage of ConfigValidator is opposite behaviour to fail fast, it gathers all the missing fields and objects instead of failing at first the missing one.

This code can be a valuable tool for applications that rely on configuration with `.conf` extension.

### Example
Config file
```
parent1{ 
    child1 = "child"
}    

child3 = "child"

parent2{
    parent3{
        child1 = "child"
    }
}
```

Validation code
```
validateConfig(environment.config){
    obj("parent1"){
        field("child1")
    }

    field("child3")

    obj("parent2"){
        obj("parent3"){
            field("child1")
        }
    }
}
```

### Example when objects in config are missing
Actual config file
```
parent { 
    child = "child"
}   
```

Expected config file
```
obj(parent1){
    field(child)
}

field("child3")

obj("parent3"){
    obj("parent4"){
        obj("parent5"){
            field("child")
        }
    }
}
```
List of errors
```
errors = ["parent1", "parent1.child","child3", "parent3.parent4", "parent3.parent4.parent5", 
"parent3.parent4.parent5.child"]
```