# 🦆 bliki-kernel

`bliki-kernel` is a trimmed-down fork of Bliki, a parsing library for Wikitext in Java.

All credit goes to Axel Kramer, Jan Berkel, and other contributors. We just removed stuff.

## Changelog

`bliki-kernel` was forked from commit [`8882759`](https://bitbucket.org/axelclk/info.bliki.wiki/commits/8882759847ae4913017379553bd4754734d73ac2?at=master) of https://bitbucket.org/axelclk/info.bliki.wiki.

Differences with `bliki`:
* `bliki-addons` and `bliki-creator` were removed, so this repository is a fork of `bliki-core` only
* All references to a specific SLF4J binding were removed (cf. https://bitbucket.org/axelclk/info.bliki.wiki/issues/47)
* All references to Scribunto and Lua were removed
* The POM file was trimmed down, and dependencies were updated to newer versions
* The JAR is obviously smaller, if you care about that

## Latest release

[![Release](https://jitpack.io/v/thunken/bliki-kernel.svg?style=flat-square)](https://github.com/thunken/bliki-kernel/releases)

To add a dependency on this project using Gradle, Maven, sbt, or Leiningen, we recommend using [JitPack](https://jitpack.io/#thunken/bliki-kernel/1.0.5). The Maven group ID is `com.github.thunken`, and the artifact ID is `bliki-kernel`.

For example, for Maven, first add the JitPack repository to your build file:
```xml
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```

And then add the dependency:
```xml
	<dependency>
	    <groupId>com.github.thunken</groupId>
	    <artifactId>bliki-kernel</artifactId>
	    <version>1.0.5</version>
	</dependency>
```
