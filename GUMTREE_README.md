# GumTree Integration in Your Spring Boot Project

This document explains how to use the GumTree library for Java source code comparison in your Hotel Booking Management System.

## What is GumTree?

GumTree is a powerful library for detecting and computing differences between two versions of source code files. It creates Abstract Syntax Trees (AST) from source files and computes fine-grained edit scripts showing exactly what changed between versions.

## Dependencies Added

The following GumTree dependencies have been added to your `pom.xml`:

```xml
<!-- GumTree library for tree differencing -->
<dependency>
    <groupId>com.github.gumtreediff</groupId>
    <artifactId>core</artifactId>
    <version>3.0.0</version>
</dependency>
<dependency>
    <groupId>com.github.gumtreediff</groupId>
    <artifactId>client</artifactId>
    <version>3.0.0</version>
</dependency>
<dependency>
    <groupId>com.github.gumtreediff</groupId>
    <artifactId>gen.jdt</artifactId>
    <version>3.0.0</version>
</dependency>
```

## Example Classes Created

### 1. `CompareJavaFiles.java`
- Basic example showing how to compare two Java files
- Located: `src/main/java/com/maneth/zikhron/CompareJavaFiles.java`

### 2. `GumTreeExample.java`
- Enhanced example with better formatting and error handling
- Located: `src/main/java/com/maneth/zikhron/GumTreeExample.java`

## Sample Files for Testing

### `Old.java` - Original version:
```java
public class Example {
    private String name;
    private int age;
    
    public Example(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    public String getName() {
        return name;
    }
    
    public int getAge() {
        return age;
    }
}
```

### `New.java` - Modified version:
```java
public class Example {
    private String name;
    private int age;
    private String email; // Added new field
    
    public Example(String name, int age, String email) { // Modified constructor
        this.name = name;
        this.age = age;
        this.email = email; // Added initialization
    }
    
    public String getName() {
        return name;
    }
    
    public int getAge() {
        return age;
    }
    
    // Added new getter method
    public String getEmail() {
        return email;
    }
    
    // Added new setter method
    public void setEmail(String email) {
        this.email = email;
    }
}
```

## How to Run

### Compile the project:
```bash
./mvnw clean compile
```

### Run the basic example:
```bash
java -cp "target/classes:$(./mvnw dependency:build-classpath -Dmdep.outputFile=/dev/stdout -q)" com.maneth.zikhron.CompareJavaFiles
```

### Run the enhanced example:
```bash
java -cp "target/classes:$(./mvnw dependency:build-classpath -Dmdep.outputFile=/dev/stdout -q)" com.maneth.zikhron.GumTreeExample
```

### Run with custom files:
```bash
java -cp "target/classes:$(./mvnw dependency:build-classpath -Dmdep.outputFile=/dev/stdout -q)" com.maneth.zikhron.GumTreeExample path/to/file1.java path/to/file2.java
```

## Key Features Demonstrated

1. **AST Generation**: Converting Java source code into Abstract Syntax Trees
2. **Tree Matching**: Finding correspondences between nodes in different versions
3. **Edit Script Generation**: Computing the sequence of operations needed to transform one version into another
4. **Action Types**: INSERT, DELETE, UPDATE, and MOVE operations
5. **Statistics**: Node counts, mappings, and edit action counts

## Use Cases in Your Project

You can use GumTree for:

1. **Code Review**: Automatically detect changes in Java files
2. **Version Control**: Analyze differences between commits
3. **Refactoring Analysis**: Track how code structure changes over time
4. **Automated Testing**: Verify that code changes don't break existing functionality
5. **Code Quality**: Measure the impact of changes on code complexity

## API Overview

### Basic Usage Pattern:
```java
// 1. Initialize generators
Run.initGenerators();

// 2. Generate ASTs from files
TreeContext src = TreeGenerators.getInstance().getTree("file1.java");
TreeContext dst = TreeGenerators.getInstance().getTree("file2.java");

// 3. Match trees and find mappings
Matcher matcher = Matchers.getInstance().getMatcher();
MappingStore mappings = matcher.match(src.getRoot(), dst.getRoot());

// 4. Generate edit script
EditScript editScript = new ChawatheScriptGenerator().computeActions(mappings);

// 5. Process results
for (Action action : editScript) {
    System.out.println(action);
}
```

## Integration with Spring Boot

To integrate GumTree functionality into your Spring Boot application:

1. Create a service class (e.g., `CodeComparisonService`)
2. Use `@Service` annotation
3. Inject the service into your controllers
4. Expose REST endpoints for file comparison

Example:
```java
@Service
public class CodeComparisonService {
    public List<String> compareJavaFiles(String file1, String file2) {
        // Use GumTree logic here
        // Return formatted diff results
    }
}
```

## Notes

- GumTree works best with syntactically correct Java files
- The library supports various programming languages (Java, JavaScript, Python, etc.)
- Performance scales well with file size
- Memory usage depends on AST complexity

## Troubleshooting

- Ensure Java files are syntactically correct
- Check file paths are correct
- Verify all dependencies are properly resolved
- Use absolute paths when working with files in different directories
