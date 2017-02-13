# StdURL [![Build Status](https://img.shields.io/travis/std4453/StdURL/master.svg?style=flat-square)](https://travis-ci.org/std4453/StdURL)

> Standard-Conforming Java URL processing library.

**StdURL** is a *Java* library to deal with *URL*s according to the [**URL Standard**](https://url.spec.whatwg.org/). It is intended that the library **100%** conforms to the standard, therefore **StdURL** is under rapid development in order to meet this goal.

## Installation

Currently unavailable.

## Usage example

``` java
import org.stdurl.URL;
import org.stdurl.parser.BasicURLParser;

URL url = BasicURLParser.parse("http://www.example.com/path/file?query#fragment");
if (url == null || url.isFailure()) {
    System.out.println("URL parser returns FAILURE");
}

System.out.println(url.getProtocol()); // http:
System.out.println(url.getHost()); // www.example.com
System.out.println(url.getPathname()); // /path/file
System.out.println(url.getSearch()); // ?query
System.out.println(url.getHash()); // #fragment
System.out.println(url.getHref()); // http://www.example.com/path/file?query#fragment
```

## Development setup

Use gradle to build this project:

```sh
gradle compile
```

To run all tests:

``` sh
gradle check
```

## Release History

* 1.0 (Feb. 13, 2016): First stable release.

## Others

Harry Zhang – [@std4453](https://twitter.com/std4453) – std4453@outlook.com

[https://github.com/std4453/StdURL](https://github.com/std4453/StdURL)
