# SKL Interpreter for Java
**Status for developing branch (`0.3`):** [![Build Status](https://travis-ci.org/TheSilkMiner/SKL-Java-Interpreter.svg?branch=0.3)](https://travis-ci.org/TheSilkMiner/SKL-Java-Interpreter)

## Description
This SKL Interpreter is a project that aims to allow users of the Java language to use the features provided by the SKL family of languages.
This project is implemented in Kotlin, so interoperability with Java is guaranteed.

The goal of this project is <strong>NOT</strong> to be as light-weight as possible, but to be able to provide the best experience for the user.
That is also the reason why it depends on so many other libraries: the easier it is to code with this library, the better for our users.

## What does SKL mean?
SKL stands for SilK Language (previously Silk Kustom Language) and is a family of languages that contains data storage, scripting and/or programming languages.

## Usage
To use this library, you can either use a JAR file that is directly linked to the repository or download it from our Maven repository.
The code examples assume you are using Gradle as your build system.

### Through the repository
<details><summary><strong>We do not currently have a private Maven repository set up, so you cannot rely on this method</strong></summary>
First add the repository to the `repositories` Gradle block, then specify it as a compile-time dependency.

```gradle
repositories {
    maven {
        name: "Silk's Private Repo"
        url: 'https://maven.thesilkminer.net/private'
    }
}

dependencies {
    // SKL Interpreter for Java
    compile group: 'net.thesilkminer.skl.interpreter', name: 'SKL-Java-Interpreter', version: '0.3'
}
```
</details>

### As a JAR dependency
You can also add a JAR dependency directly through Gradle instead of relying on repositories.

After you obtained a copy of the file (e.g., by compiling the project manually or downloading a release), save it in the `libs` directory inside your Gradle project directory.
Then add the following bits to the `build.gradle` file:

```gradle
repositories {
    flatDir {
        dirs 'lib'
    }
}

dependencies {
    // SKL Interpreter for Java
    compile name: 'SKL-Java-Interpreter-0.3'
}
```
