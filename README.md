# SKL Interpreter for Java
**Status for developing branch (`0.3`):** [![Build Status](https://travis-ci.org/TheSilkMiner/SKL-Java-Interpreter.svg?branch=0.3)](https://travis-ci.org/TheSilkMiner/SKL-Java-Interpreter)

## Table of Contents
`// TODO`

## Description
This SKL Interpreter is a project that aims to allow users of the Java language to use the features provided by the SKL family of languages.
This project is implemented in Kotlin, so interoperability with Java is guaranteed.

## What does SKL mean?
SKL stands for SilK Language (previously Silk Kustom Language) and is a family of languages that contains data storage, scripting and/or programming languages.
Refer to the section about the SKL itself in the following sections of this README file.

## Usage
To use this library, you can either use a JAR file that is directly linked to the repository or download it from our Maven repository.
The code examples assume you are using Gradle as your build system.

You can add substitute `SKL-Java-Interpreter` to `SKL-Java-Interpreter-API` in every example to depend only on the API.
This provides some useful advantages, mainly the possibility to use a custom implementation or to swap out the implementation
at runtime, allowing the user to write implementation-agnostic code.

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

## The SKL family
The SKL family is a group of programming languages that covers most if not all of the various possibilities where a language may be needed.
Following there is a list of the entirety of the languages that are present in the SKL family.
Every language is or will be represented in this library, with its own API to safely interact with it from JVM languages.

### SKL (Silk Kustom Language)
SKL is a general purpose language, designed to be both expressive and easy to use.
It supports both dynamic and static typing and various paradigms, such as FP and OOP.

**Current version:** 0.0.0<br />
**Development status:** Stale - Abandoned<br />
**Library entry point:** None

### SKS (SilK Scripting)
SKS is a scripting type of markup language that is designed to provide a unified experience for users.
Its goal is to allow all languages - even the ones that need a previous step of compilation - to be used for scripting.
At the same time, it also provides some other "custom" languages that are mainly used for basic scripts.

**Current version:** 0.2.1<br />
**Development status:** Stale - In development<br />
**Library entry point:** `net.thesilkminer.skl.interpreter.api.sks.SksApi`

### SKD (SilK Database)
SKD is another kind of flat-file, unencrypted data storage, such as XML or JSON.
The main advantage is the possibility of representing the same data in various representations, such as XML or JSON, or
custom renditions of their syntax (the such-called cXML and cJSON).
Another advantage is the possibility of editing the database according to certain conditions, due to the presence
of a powerful preprocessor.

**Current version**: 0.3<br />
**Development status:** Active - In development<br />
**Library entry point:** `net.thesilkminer.skl.interpreter.skd.api.Skd`

### IsLang Compiled
IsLang Compiled, also known as IsLang, is a general purpose language that aims to support all the present programming paradigms
and both dynamic and static typing with a modern, quick and easy-to-grasp syntax. The main difference from its predecessor is
a syntax that is much less verbose and the presence of other features like native ASM or `unsafe` constructs. It compiles down
to a special form of bytecode that runs on IVM and that allows a huge amount of optimization both by compilers and by the JIT
compiler. Some compilers may also choose to output native machine code.

**Current version**: 0.0.0<br />
**Development status:** Inactive - Not started<br />
**Library entry point:** None

### IsLang Interpreted
IsLang Interpreted, also known as IIsLang, is a general purpose language that is similar to its big brother: IsLang Compiled.
The main difference is that IsLang Interpreted provides some more features such as immediate execution of code outside a
function - like in Python - at the cost of removing some of the more powerful constructs of IsLang Compiled, such as
`unsafe` or completely safe and static typing. 

**Current version**: 0.0.1<br />
**Development status:** Stale - In development - Closed Source<br />
**Library entry point:** None

### IsLang Esoteric
IsLang Esoteric is a new entry in the group of esoteric and code-golf languages. Also known as Island, its syntax reminds
of a ship that sails through the ocean and finds various islands. Due to the esoteric nature of the language, it is not
suggested for beginners, but it is natively supported by both IsLang Compiled and IsLang Interpreted (a limited subset in
the latter case), which allows expert programmers to embed part of the source code for faster code generation.

**Current version**: 0.0.0<br />
**Development status:** Inactive - Not started<br />
**Library entry point:** None

## The structure of the library
This library is divided into two main artifacts: the API and the implementation. The API artifact contains only the API
classes of all the various languages that are part of the SKL family. All the default implementations are instead contained
in the implementation artifact, along with all the APIs. Implementation-only artifacts or API/implementation artifacts for
a single module are currently not provided. They may be provided for releases only, but this is still a decision for the
internal team to discuss. You may compile the library yourself, though.

## Contributing
Contributions are always accepted and encouraged.

### Before submitting a pull request
- [ ] Make sure that you are targeting the currently developing branch (the one with the most recent Travis build, usually)
- [ ] Check if a pull request has already been opened: if it is, comment on it and review it instead of creating a new one.
- [ ] Check both open and closed issues to see if the feature was already accepted and in the works or refused.
- [ ] If the current feature or bug fix is currently in development, please contact the development team to ensure that a pull request is welcome.
- [ ] If you are proposing a new feature, please open an issue first, so that discussion can happen and you do not waste time on something that may be refused later.

### Preparing the environment a pull request
First of all clone this Git repository on your machine. To do so, use the `git clone` command:
```posh
D:\GitHub> git clone https://github.com/TheSilkMiner/SKL-Java-Interpreter.git 
``` 

After the repository has been cloned, set your directory into the one and checkout the latest development branch.
The following example assumes that the development branch is `bleeding`: change the name accordingly
```posh
D:\GitHub> Set-Location -Path "./SKL-Java-Interpreter"
D:\GitHub\SKL-Java-Interpreter [master ≡]> git checkout bleeding
```

The branch will automatically be "cloned" to your machine. You can now checkout again and create a new branch according to the naming conventions.
More specifically, the name must be made of two parts, divided by a forward slash.
The first part must describe what the branch goal is, according to the following rules:
- if the branch aims to add something completely new, then the appropriate tag is `feature`
- if the branch aims to add something missing to an already existing feature, then use `enhancement`
- if the branch aims to fix a bug that is present in the software, then use `bug`
- if the branch aims to fix a flaw in the documentation or anything else not code related, use `doc`
- for everything else, use the general `fix`
The second part should describe what the goal of the branch is, with one or a few (maximum three) words separated by hyphens.
The following example assumes the addition of a new feature that is the ability to read a file from a stream of sounds recorded by a microphone.
```posh
D:\GitHub\SKL-Java-Interpreter [bleeding ≡]> git branch feature/read-mic-sounds
D:\GitHub\SKL-Java-Interpreter [bleeding ≡]> git checkout feature/read-mic-sounds
```

The final bit is to push the newly created branch upstream, so that your local branch is in sync and can show the exact status
of your branch with the upstream clone.
```posh
D:\GitHub\SKL-Java-Interpreter [feature/read-mic-sounds]> git push origin feature/read-mic-sounds --set-upstream
```

Now you are ready to start developing.

### Submitting a pull request
Once you are ready with your changes, you are ready to submit a pull request.
Head over to your fork on GitHub, switch to the branch you just made and then click the button "Create pull request".

Before submitting a pull request, make sure you pass all the following points:
- [ ] Your title needs to be descriptive
- [ ] Your description must contain an explanation of the changes and an example of usage in case of new features.
- [ ] The above also applies to bugs, also if they are one-liners.
- [ ] Make sure all your commits are signed-off. PGP signature is not needed, but it is a nice touch.
- [ ] Make sure the Travis build passes or at least that it does not fail due to files you have created or modified.
- [ ] Check that the code is clear enough and that there are comments where you deem necessary.
- [ ] Make sure you have documented all API changes and additions with a Javadoc or KDoc comment.
- [ ] Make sure any API changes are non-breaking. If you require breaking changes, then explain the reason in the pull request description.
