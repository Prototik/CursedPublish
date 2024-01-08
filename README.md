# Cursed Publish plugin

[![Gradle versions](https://img.shields.io/badge/Gradle-6.8_--_8.5-02303A?logo=gradle&logoColor=02303A&labelColor=white)](https://gradle.org)
![Java versions](https://img.shields.io/badge/Java-8_--_21-f7901e?logo=data%3Aimage%2Fsvg%2Bxml%3Bbase64%2CPHN2ZyB3aWR0aD0iMTYiIGhlaWdodD0iMTYiIHZlcnNpb249IjEuMSIgdmlld0JveD0iMCAwIDE2IDE2IiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciPjxwYXRoIGQ9Im03LjY2NzMgOC41OTE0cy0wLjg5MTk0LTEuNzE4Ny0wLjg1OS0yLjk3NDZjMC4wMjQwMzMtMC44OTc0NSAyLjA0NzYtMS43ODQ2IDIuODQyLTMuMDcyNyAwLjc5MzA3LTEuMjg4OS0wLjA5ODg3Mi0yLjU0NC0wLjA5ODg3Mi0yLjU0NHMwLjE5ODQ0IDAuOTI1NTktMC4zMzAyOSAxLjg4MjhjLTAuNTI4NyAwLjk1ODU3LTIuNDgwMiAxLjUyMDktMy4yMzg5IDMuMTczLTAuNzU4NzMgMS42NTIxIDEuNjg1IDMuNTM1NiAxLjY4NSAzLjUzNTZ6IiBmaWxsPSIjZjc5MDFlIiBzdHJva2Utd2lkdGg9Ii4wMzE4MzIiLz48cGF0aCBkPSJtMTEuMDA2IDMuMzcwMXMtMy4wMzk4IDEuMTU3LTMuMDM5OCAyLjQ3ODFjMCAxLjMyMjUgMC44MjUzNSAxLjc1MTYgMC45NTg1NyAyLjE4MDggMC4xMzE4NSAwLjQzMDUzLTAuMjMxMzkgMS4xNTctMC4yMzEzOSAxLjE1N3MxLjE4OTMtMC44MjYwNSAwLjk5MDE1LTEuNzg0NmMtMC4xOTg0NC0wLjk1ODU3LTEuMTIzNC0xLjI1NjYtMC41OTM5Ni0yLjIxMzggMC4zNTM2My0wLjY0MjAzIDEuOTE2NC0xLjgxNzYgMS45MTY0LTEuODE3NnoiIGZpbGw9IiNmNzkwMWUiIHN0cm9rZS13aWR0aD0iLjAzMTgzMiIvPjxnIGZpbGw9IiMwMDc2OTkiIHN0cm9rZS13aWR0aD0iLjAzMTgzMiI%2BPHBhdGggZD0ibTcuMzM3NyAxMC4zMzhjMi44MDkxLTAuMTAwMjQgMy44MzM2LTAuOTg2NzEgMy44MzM2LTAuOTg2NzEtMS44MTY5IDAuNDk1MDktNi42NDIgMC40NjI4MS02LjY3NTYgMC4wOTk1NzItMC4wMzIyNzgtMC4zNjMyNCAxLjQ4NjYtMC42NjEyNSAxLjQ4NjYtMC42NjEyNXMtMi4zNzg1IDAtMi41NzcgMC41OTQ2M2MtMC4xOTg0OCAwLjU5NDY2IDEuMTI1NCAxLjA1MiAzLjkzMjQgMC45NTM3NnoiLz48cGF0aCBkPSJtMTEuNDAyIDExLjczczIuNzQ5My0wLjU4NjM4IDIuNDc3NC0yLjA3OTljLTAuMzI5NTktMS44MTgyLTIuMjQ2Ny0wLjc5Mzc3LTIuMjQ2Ny0wLjc5Mzc3czEuMzU2MSAwIDEuNDg3MyAwLjgyNTM1YzAuMTMyNTUgMC44MjYwNS0xLjcxOCAyLjA0ODMtMS43MTggMi4wNDgzeiIvPjxwYXRoIGQ9Im0xMC4wNDcgMTAuOTM4cy0wLjY5MjgzIDAuMTgxOTUtMS43MTk0IDAuMjk3MzFjLTEuMzc2MSAwLjE1NDQ4LTMuMDM5OCAwLjAzMjI4LTMuMTcyMy0wLjE5OTE0LTAuMTMwNDUtMC4yMzEzOSAwLjIzMTM5LTAuMzYzMjQgMC4yMzEzOS0wLjM2MzI0LTEuNjUyOCAwLjM5Njg5LTAuNzQ4NDQgMS4wODk3IDEuMTg4NiAxLjIyMjkgMS42NjAzIDAuMTEzMjkgNC4xMzE2LTAuNDk1NzYgNC4xMzE2LTAuNDk1NzZ6Ii8%2BPHBhdGggZD0ibTUuNzg1MiAxMi4zODFzLTAuNzQ5ODEgMC4wMjEzLTAuNzkzNzcgMC40MTgxOGMtMC4wNDMyNiAwLjM5NDE1IDAuNDYxNDQgMC43NDg0NCAyLjMxMjYgMC44NTgzMyAxLjg1MDUgMC4xMDk4NSAzLjE1MS0wLjUwNjA3IDMuMTUxLTAuNTA2MDdsLTAuODM3MDMtMC41MDgxMXMtMC41Mjk0IDAuMTExMjItMS4zNDQ1IDAuMjIxMTFjLTAuODE1NzQgMC4xMTA1NS0yLjQ4ODQtMC4wODg1OS0yLjU1NDMtMC4yNDE3LTAuMDY3MjYyLTAuMTU0NTUgMC4wNjU5NTctMC4yNDE3NCAwLjA2NTk1Ny0wLjI0MTc0eiIvPjxwYXRoIGQ9Im0xMi45ODcgMTQuMTY2YzAuMjg2MzMtMC4zMDktMC4wODg1OS0wLjU1MTM3LTAuMDg4NTktMC41NTEzN3MwLjEzMTg1IDAuMTU0NDgtMC4wNDI1NiAwLjMzMDI5Yy0wLjE3NjQ4IDAuMTc1NzgtMS43ODUzIDAuNjE1OTItNC4zNjMgMC43NDg0NC0yLjU3NyAwLjEzMjUyLTUuMzc1MS0wLjI0MjQtNS40NjM3LTAuNTcyNjYtMC4wODU4Mi0wLjMzMDI5IDEuNDMzLTAuNTkzOTYgMS40MzMtMC41OTM5Ni0wLjE3NTc4IDAuMDIxOTYtMi4yOTA3IDAuMDY1OTMtMi4zNTggMC42MzkyNi0wLjA2NTkyNSAwLjU3MTk2IDAuOTI0OTIgMS4wMzQ4IDQuODkxNyAxLjAzNDggMy45NjQ4LTYuNjhlLTQgNS43MDYxLTAuNzI3ODUgNS45OTEtMS4wMzQ4eiIvPjxwYXRoIGQ9Im0xMS40NDYgMTUuNDY1Yy0xLjc0MDcgMC4zNTE1Ni03LjAyNjUgMC4xMjk3OC03LjAyNjUgMC4xMjk3OHMzLjQzNTMgMC44MTU3NCA3LjM1NjggMC4xMzMyMmMxLjg3NDUtMC4zMjY4NiAxLjk4MzctMS4yMzM5IDEuOTgzNy0xLjIzMzlzLTAuNTczMzMgMC42MTcyOS0yLjMxNCAwLjk3MDkyeiIvPjwvZz48L3N2Zz4K&labelColor=white)

Plugin to automatically publish artifacts (mods) on the CurseForge portal directly from Gradle build lifecycle.

## Setup

[![Latest version](https://img.shields.io/gradle-plugin-portal/v/rocks.aur.cursed.publish?logo=gradle&label=Latest%20version&color=%2302303A)](https://plugins.gradle.org/plugin/rocks.aur.cursed.publish)

<details open>
<summary>Kotlin buildscript</summary>

```kotlin
plugins {
    id("rocks.aur.cursed.publish") version "<version>"
}
```
</details>

<details>
<summary>Groovy buildscript</summary>

```groovy
plugins {
    id 'rocks.aur.cursed.publish' version '<version>'
}
```
</details>

## Configuration

```kotlin
cursedPublish {
    projectId("<curseforge-project-id>") // numeric project id from mod page
    apiToken("<curseforge-token>") // curseforge token from settings page

    // You can use AbstractArchiveTask here to set file name and file source at the same type
    file(tasks.jar) {
        // Display name for mod file
        displayName("MyCoolMod (Fabric)")
        
        // Changelog
        changelog(CursedChangelogType.Text, "Some changelog")

        // Minecraft version
        minecraft("1.20.4")
        
        // ModLoader, usually only one
        forge()
        neoForge()
        fabric()
        quilt()
        
        // Java version
        java(17)
        
        // Environment
        client()
        server()

        // Release type
        alpha()
        beta()
        release()

        relations {
            // Define dependencies to other curseforge projects
            // You cannot declare one slug more than once
            requiredDependency("<project-slug>")
            optionalDependency("<project-slug>")
            embeddedLibrary("<project-slug>")
            incompatible("<project-slug>")
            tool("<project-slug>")
        }

        additionalFile(tasks.sourcesJar) {
            // the same as file, but you cannot define game versions
            // (minecraft, modloader, java, environment) and nested files here
        }
    }
}
```

Plugin will create task for each top-level file (for example, when you register file with name `jar`, plugin will create task `curseforgeUploadJarFile` to upload file itself and all of its additional files).

All such tasks aggregated into `curseforgeUploadAllFiles` meta task to publish all declared files.