<img src="https://raw.githubusercontent.com/jankotek/mapdb-site/gh-pages/images/art/rocket-small.png" width=90 height=90 align="left"/>

MapDB: database engine 

目前  ,mapdb在内存回收方面存在问题。空间碎片太多，需要更合理的回收策略。

=======================
[![Build Status](https://travis-ci.org/jankotek/mapdb.svg?branch=master)](https://travis-ci.org/jankotek/mapdb)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.mapdb/mapdb/badge.svg)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.mapdb%22%20AND%20a%3Amapdb)
[![Join the chat at https://gitter.im/jankotek/mapdb](https://badges.gitter.im/jankotek/mapdb.svg)](https://gitter.im/jankotek/mapdb?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)


MapDB combines embedded database engine and Java collections.
It is free under Apache 2 license. MapDB is fplexible and can be used in many roles:

* Drop-in replacement for Maps, Lists, Queues and other collections.
* Off-heap collections not affected by Garbage Collector
* Multilevel cache with expiration and disk overflow.
* RDBMs replacement with  transactions, MVCC, incremental backups etc…
* Local data processing and filtering. MapDB has utilities to process huge quantities of data in reasonable time.

Hello world
-------------------

Maven snippet, VERSION is [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.mapdb/mapdb/badge.svg)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.mapdb%22%20AND%20a%3Amapdb)

    <dependency>
        <groupId>org.mapdb</groupId>
        <artifactId>mapdb</artifactId>
        <version>VERSION</version>
    </dependency>


Hello world:

        //import org.mapdb.*
        DB db = DBMaker.memoryDB().make();
        ConcurrentMap map = db.hashMap("map").make();
        map.put("something", "here");

Continue at [Quick Start](https://jankotek.gitbooks.io/mapdb/content/quick-start/) or at  [Documentation](http://www.mapdb.org/doc/).

Support
------------

More [details](http://www.mapdb.org/support/).

Development
--------------------

MapDB is written in Kotlin. You will need Intellij Idea 15 Community Edition to edit it.

Use Maven to build MapDB: `mvn install`

MapDB comes with extensive unit tests, by default only tiny fraction is executed, so build finishes under 10 minutes.
Full test suite has over million test cases and runs several hours/days.
To run full test suite set `-Dmdbtest=1` property.
