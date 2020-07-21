# Mapper
![build](https://github.com/rsbox/mapper/workflows/build/badge.svg)

A tool for mapping Jar different Jar file obfuscations between revisions and generating mappings

## Download
Visit the GitHub release page to download the last pre-compiled `mapper.jar` binary.

## Building
Clone the repository.
```bash
git clone https://github.com/rsbox/mapper.git
```

Build the gradle project.
```bash
gradle build -x test
```

*If you would like to build a Binary Jar.*
```bash
gradle shadowJar
```

The runnable Jar file will be located in `./mapper/launcher/build/libs/mapper.jar`