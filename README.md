# Low-Level Design (LLD) Notes

## Creational Patterns

### 1. Singleton Pattern
**Intent:** Ensure a class has only one instance and provide a global point of access.

```mermaid
classDiagram
    class DatabaseConnection {
        -static volatile DatabaseConnection instance
        -DatabaseConnection()
        +static getInstance() DatabaseConnection
    }
```
### 2. Factory Pattern
**Intent:** Provide an interface for creating objects in a superclass, but allow subclasses to alter the type of objects that will be created.

```mermaid
classDiagram
    class Notification {
        <<interface>>
        +notifyUser()
    }
    class EmailNotification {
        +notifyUser()
    }
    class SmsNotification {
        +notifyUser()
    }
    class NotificationFactory {
        +createNotification(String type) Notification$
    }
    Notification <|.. EmailNotification : Implements
    Notification <|.. SmsNotification : Implements
    NotificationFactory ..> Notification : creates
```
### 3. Absolute Factory Pattern

```mermaid
classDiagram
    class FurnitureFactory {
        <<interface>>
        +createChair() Chair
        +createSofa() Sofa
    }
    class ModernFactory {
        +createChair()
        +createSofa()
    }
    class VictorianFactory {
        +createChair()
        +createSofa()
    }
    
    FurnitureFactory <|.. ModernFactory
    FurnitureFactory <|.. VictorianFactory
    ModernFactory ..> ModernChair : Creates
    VictorianFactory ..> VictorianChair : Creates
```

### 4. Builder Pattern

```mermaid
classDiagram
    class User {
        -String firstName
        -String lastName
        -int age
        -String phone
        -User(UserBuilder builder)
    }
    class UserBuilder {
        -String firstName
        -String lastName
        -int age
        -String phone
        +UserBuilder(firstName, lastName)
        +setAge(age) UserBuilder
        +setPhone(phone) UserBuilder
        +build() User
    }
    %% Use *-- for nested/inner class relationship
    User *-- UserBuilder : Defines
    UserBuilder ..> User : Creates
```

### 5. Prototype Pattern
**Intent:** It allows you to create a new object by cloning an existing prototype and modifying only what's necessary.

```mermaid
classDiagram
    class Prototype {
        <<interface>>
        +clone() Prototype
    }
    class GameCharacter {
        -String name
        -String weapon
        -int health
        +clone() Prototype
    }
    Prototype <|.. GameCharacter
```

## Structural Patterns

### 1. Adapter Pattern
**Intent:** Convert the interface of a class into another interface clients expect.

```mermaid
classDiagram
    class WeightSensor {
        <<interface>>
        +getWeightInKilos() double
    }
    class LegacyPoundSensor {
        +getWeightInPounds() double
    }
    class WeightAdapter {
        -LegacyPoundSensor legacySensor
        +getWeightInKilos() double
    }

    WeightSensor <|.. WeightAdapter : Implements
    WeightAdapter --> LegacyPoundSensor : Wraps/Adapts
```

### 2. Bridge Pattern
**Intent:** Decouple an abstraction from its implementation so that the two  can vary independently.

```mermaid
classDiagram
    class Device {
        <<interface>>
        +isEnabled() boolean
        +enable()
        +disable()
        +setVolume(percent)
    }
    class TV {
        -boolean on
        -int volume
    }
    class Radio {
        -boolean on
        -int volume
    }

    class RemoteControl {
        #Device device
        +togglePower()
    }
    class AdvancedRemote {
        +mute()
    }

    Device <|.. TV : Implements
    Device <|.. Radio : Implements
    
    RemoteControl <|-- AdvancedRemote : Extends
    
    %% The Bridge (Composition)
    RemoteControl o-- Device : Bridge (Has-A)
```